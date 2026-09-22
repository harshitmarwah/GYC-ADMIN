package gyc.admin.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "test-only-secret-key-must-be-at-least-32-bytes!!";

    @Test
    void issuedTokenIsValidAndCarriesTheUsername() {
        JwtService jwtService = new JwtService(SECRET, 60);

        String token = jwtService.issueToken("admin");

        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("admin");
    }

    @Test
    void garbageTokenIsNotValid() {
        JwtService jwtService = new JwtService(SECRET, 60);

        assertThat(jwtService.isValid("not-a-real-token")).isFalse();
    }

    @Test
    void tokenSignedWithADifferentSecretIsNotValid() {
        JwtService issuer = new JwtService(SECRET, 60);
        JwtService verifier = new JwtService("a-completely-different-secret-key-of-32-bytes!", 60);

        String token = issuer.issueToken("admin");

        assertThat(verifier.isValid(token)).isFalse();
    }

    @Test
    void expiredTokenIsNotValid() {
        // 0-minute expiration: by the time isValid() runs, "now" has already passed the expiry instant.
        JwtService jwtService = new JwtService(SECRET, 0);

        String token = jwtService.issueToken("admin");

        assertThat(jwtService.isValid(token)).isFalse();
    }
}
