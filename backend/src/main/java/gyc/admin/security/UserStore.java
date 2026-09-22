package gyc.admin.security;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * A single seeded user for this skeleton — see the root README for
 * credentials. No registration flow; swap for a real user store before
 * this goes anywhere near production.
 */
@Component
public class UserStore {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final Map<String, UserAccount> usersByUsername = Map.of(
            "admin", new UserAccount("admin", encoder.encode("admin123")));

    public Optional<UserAccount> find(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }

    public boolean matches(String rawPassword, String hash) {
        return encoder.matches(rawPassword, hash);
    }
}
