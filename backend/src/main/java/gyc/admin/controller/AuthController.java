package gyc.admin.controller;

import gyc.admin.dto.LoginRequest;
import gyc.admin.dto.LoginResponse;
import gyc.admin.security.JwtService;
import gyc.admin.security.UserStore;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final UserStore userStore;
    private final JwtService jwtService;

    public AuthController(UserStore userStore, JwtService jwtService) {
        this.userStore = userStore;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Exchange username/password for a Bearer JWT. Seeded user: admin/admin123.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return userStore.find(request.username())
                .filter(user -> userStore.matches(request.password(), user.passwordHash()))
                .map(user -> ResponseEntity.ok(new LoginResponse(jwtService.issueToken(user.username()), user.username())))
                .orElseGet(() -> ResponseEntity.status(401).build());
    }
}
