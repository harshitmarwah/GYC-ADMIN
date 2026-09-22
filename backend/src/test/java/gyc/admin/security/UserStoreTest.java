package gyc.admin.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserStoreTest {

    private final UserStore userStore = new UserStore();

    @Test
    void findsTheSeededAdminUser() {
        assertThat(userStore.find("admin")).isPresent();
    }

    @Test
    void unknownUserIsAbsent() {
        assertThat(userStore.find("nobody")).isEmpty();
    }

    @Test
    void correctPasswordMatches() {
        UserAccount admin = userStore.find("admin").orElseThrow();

        assertThat(userStore.matches("admin123", admin.passwordHash())).isTrue();
    }

    @Test
    void wrongPasswordDoesNotMatch() {
        UserAccount admin = userStore.find("admin").orElseThrow();

        assertThat(userStore.matches("wrong-password", admin.passwordHash())).isFalse();
    }
}
