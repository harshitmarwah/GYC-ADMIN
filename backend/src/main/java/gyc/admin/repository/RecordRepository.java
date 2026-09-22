package gyc.admin.repository;

import gyc.admin.domain.entity.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<RecordEntity, Long> {
}
