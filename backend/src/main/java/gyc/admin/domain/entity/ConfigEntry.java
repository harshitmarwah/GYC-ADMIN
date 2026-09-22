package gyc.admin.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A config key/value owned by Admin. Changes fan out to Consumer via {@code ConfigChanged}. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigEntry {

    @Id
    private String key;

    private String value;
}
