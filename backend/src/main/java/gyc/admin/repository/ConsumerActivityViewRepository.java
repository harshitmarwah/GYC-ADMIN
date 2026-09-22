package gyc.admin.repository;

import java.util.List;

import gyc.admin.domain.entity.ConsumerActivityView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumerActivityViewRepository extends JpaRepository<ConsumerActivityView, Long> {
    List<ConsumerActivityView> findAllByOrderByRecordedAtDesc();
}
