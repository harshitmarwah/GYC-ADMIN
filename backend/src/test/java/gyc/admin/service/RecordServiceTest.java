package gyc.admin.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import gyc.admin.domain.entity.ConfigEntry;
import gyc.admin.domain.entity.RecordEntity;
import gyc.admin.events.AdminEventPublisher;
import gyc.admin.events.AdminEventType;
import gyc.admin.repository.ConfigRepository;
import gyc.admin.repository.RecordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {

    @Mock
    private RecordRepository recordRepository;
    @Mock
    private ConfigRepository configRepository;
    @Mock
    private AdminEventPublisher publisher;

    private RecordService service;

    @BeforeEach
    void setUp() {
        service = new RecordService(recordRepository, configRepository, publisher);
    }

    @Test
    void createRecordPersistsAndPublishesRecordUpdated() {
        when(recordRepository.save(any(RecordEntity.class))).thenAnswer(RecordServiceTest::simulateJpaAssignedId);

        RecordEntity result = service.createRecord("Q3 plan", "draft");

        assertThat(result.getTitle()).isEqualTo("Q3 plan");
        assertThat(result.getRevision()).isEqualTo(1);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(publisher).publish(org.mockito.ArgumentMatchers.eq(AdminEventType.RECORD_UPDATED), payloadCaptor.capture());
        assertThat(payloadCaptor.getValue())
                .containsEntry("title", "Q3 plan")
                .containsEntry("body", "draft")
                .containsEntry("revision", 1);
    }

    @Test
    void updateRecordIncrementsRevisionAndPublishes() {
        RecordEntity existing = new RecordEntity("old title", "old body");
        existing.setId(1L);
        when(recordRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(recordRepository.save(any(RecordEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        RecordEntity result = service.updateRecord(1L, "new title", "new body");

        assertThat(result.getTitle()).isEqualTo("new title");
        assertThat(result.getRevision()).isEqualTo(2);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(publisher).publish(org.mockito.ArgumentMatchers.eq(AdminEventType.RECORD_UPDATED), payloadCaptor.capture());
        assertThat(payloadCaptor.getValue()).containsEntry("revision", 2);
    }

    @Test
    void updateRecordThrowsWhenMissing() {
        when(recordRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateRecord(99L, "x", "y"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void suspendUserOnlyPublishesAndTouchesNoRepository() {
        service.suspendUser("alice", true, "policy violation");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(publisher).publish(org.mockito.ArgumentMatchers.eq(AdminEventType.USER_SUSPENDED), payloadCaptor.capture());
        assertThat(payloadCaptor.getValue())
                .containsEntry("userId", "alice")
                .containsEntry("suspended", true)
                .containsEntry("reason", "policy violation");
        verifyNoRecordOrConfigWrites();
    }

    @Test
    void suspendUserDefaultsNullReasonToBlank() {
        service.suspendUser("bob", false, null);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(publisher).publish(org.mockito.ArgumentMatchers.eq(AdminEventType.USER_SUSPENDED), payloadCaptor.capture());
        assertThat(payloadCaptor.getValue()).containsEntry("reason", "");
    }

    @Test
    void updateConfigPersistsAndPublishes() {
        when(configRepository.save(any(ConfigEntry.class))).thenAnswer(inv -> inv.getArgument(0));

        ConfigEntry result = service.updateConfig("processing.delayMs", "5000");

        assertThat(result.getKey()).isEqualTo("processing.delayMs");
        assertThat(result.getValue()).isEqualTo("5000");
        verify(publisher).publish(AdminEventType.CONFIG_CHANGED, Map.of("key", "processing.delayMs", "value", "5000"));
    }

    @Test
    void listRecordsDelegatesToRepository() {
        when(recordRepository.findAll()).thenReturn(List.of(new RecordEntity("a", "b")));

        assertThat(service.listRecords()).hasSize(1);
    }

    private void verifyNoRecordOrConfigWrites() {
        org.mockito.Mockito.verifyNoInteractions(recordRepository, configRepository);
    }

    // Real JPA assigns the @GeneratedValue id on save/flush; the mocked
    // repository has to do that itself, or Map.of(...) downstream rejects
    // the still-null id with a NullPointerException.
    private static RecordEntity simulateJpaAssignedId(org.mockito.invocation.InvocationOnMock invocation) {
        RecordEntity entity = invocation.getArgument(0);
        entity.setId(1L);
        return entity;
    }
}
