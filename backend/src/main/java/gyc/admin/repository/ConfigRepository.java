package gyc.admin.repository;

import gyc.admin.domain.entity.ConfigEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<ConfigEntry, String> {
}
