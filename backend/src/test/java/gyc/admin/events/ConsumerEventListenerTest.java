package gyc.admin.events;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyc.admin.domain.entity.ConsumerActivityView;
import gyc.admin.repository.ConsumerActivityViewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConsumerEventListenerTest {

    @Mock
    private ConsumerActivityViewRepository repository;

    @Test
    void requestSubmittedFoldsIntoASubmittedRow() {
        ConsumerEventListener listener = new ConsumerEventListener(new ObjectMapper(), repository);
        EventEnvelope envelope = EventEnvelope.of("REQUEST_SUBMITTED", "consumer-backend",
                Map.of("requestId", "r1", "userId", "alice", "kind", "export"));

        listener.apply(envelope);

        ConsumerActivityView saved = captureSaved();
        assertThat(saved.getRequestId()).isEqualTo("r1");
        assertThat(saved.getUserId()).isEqualTo("alice");
        assertThat(saved.getKind()).isEqualTo("export");
        assertThat(saved.getStatus()).isEqualTo("SUBMITTED");
        assertThat(saved.getDetail()).isEmpty();
    }

    @Test
    void actionCompletedFoldsResultIntoDetail() {
        ConsumerEventListener listener = new ConsumerEventListener(new ObjectMapper(), repository);
        EventEnvelope envelope = EventEnvelope.of("ACTION_COMPLETED", "consumer-backend",
                Map.of("requestId", "r1", "userId", "alice", "kind", "export", "result", "processed:export"));

        listener.apply(envelope);

        ConsumerActivityView saved = captureSaved();
        assertThat(saved.getStatus()).isEqualTo("COMPLETED");
        assertThat(saved.getDetail()).isEqualTo("processed:export");
    }

    @Test
    void sessionFlaggedDefaultsMissingKindToBlankNotTheLiteralStringNull() {
        // Regression test: SESSION_FLAGGED carries no "kind" per the event
        // contract. Before the fix, String.valueOf(null) produced "null".
        ConsumerEventListener listener = new ConsumerEventListener(new ObjectMapper(), repository);
        EventEnvelope envelope = EventEnvelope.of("SESSION_FLAGGED", "consumer-backend",
                Map.of("requestId", "r1", "userId", "alice", "reason", "policy violation"));

        listener.apply(envelope);

        ConsumerActivityView saved = captureSaved();
        assertThat(saved.getStatus()).isEqualTo("FLAGGED");
        assertThat(saved.getDetail()).isEqualTo("policy violation");
        assertThat(saved.getKind()).isEmpty();
    }

    private ConsumerActivityView captureSaved() {
        ArgumentCaptor<ConsumerActivityView> captor = ArgumentCaptor.forClass(ConsumerActivityView.class);
        verify(repository).save(captor.capture());
        return captor.getValue();
    }
}
