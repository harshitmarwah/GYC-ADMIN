package gyc.admin.service;

import java.util.List;
import java.util.Map;

import gyc.admin.domain.entity.ConfigEntry;
import gyc.admin.domain.entity.RecordEntity;
import gyc.admin.events.AdminEventPublisher;
import gyc.admin.events.AdminEventType;
import gyc.admin.repository.ConfigRepository;
import gyc.admin.repository.RecordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecordService {

    private final RecordRepository recordRepository;
    private final ConfigRepository configRepository;
    private final AdminEventPublisher publisher;

    @Autowired
    public RecordService(RecordRepository recordRepository, ConfigRepository configRepository,
                          AdminEventPublisher publisher) {
        this.recordRepository = recordRepository;
        this.configRepository = configRepository;
        this.publisher = publisher;
    }

    @Transactional
    public RecordEntity createRecord(String title, String body) {
        RecordEntity saved = recordRepository.save(new RecordEntity(title, body));
        publisher.publish(AdminEventType.RECORD_UPDATED, Map.of(
                "recordId", saved.getId(),
                "title", saved.getTitle(),
                "body", saved.getBody(),
                "revision", saved.getRevision()));
        return saved;
    }

    @Transactional
    public RecordEntity updateRecord(Long id, String title, String body) {
        RecordEntity record = recordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No record " + id));
        record.applyEdit(title, body);
        RecordEntity saved = recordRepository.save(record);
        publisher.publish(AdminEventType.RECORD_UPDATED, Map.of(
                "recordId", saved.getId(),
                "title", saved.getTitle(),
                "body", saved.getBody(),
                "revision", saved.getRevision()));
        return saved;
    }

    public List<RecordEntity> listRecords() {
        return recordRepository.findAll();
    }

    public void suspendUser(String userId, boolean suspended, String reason) {
        // Admin does not own user state — this is a pure command that fans
        // out as an event for Consumer to apply against its own data.
        publisher.publish(AdminEventType.USER_SUSPENDED, Map.of(
                "userId", userId,
                "suspended", suspended,
                "reason", reason == null ? "" : reason));
    }

    @Transactional
    public ConfigEntry updateConfig(String key, String value) {
        ConfigEntry saved = configRepository.save(new ConfigEntry(key, value));
        publisher.publish(AdminEventType.CONFIG_CHANGED, Map.of("key", saved.getKey(), "value", saved.getValue()));
        return saved;
    }

    public List<ConfigEntry> listConfig() {
        return configRepository.findAll();
    }
}
