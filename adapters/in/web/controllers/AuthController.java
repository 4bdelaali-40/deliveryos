package adapters.in.web.controllers;

import com.deliveryos.adapters.in.web.dto.request.LoginRequest;
import com.deliveryos.adapters.in.web.dto.request.RegisterRequest;
import com.deliveryos.adapters.in.web.dto.response.AuthResponse;
import com.deliveryos.application.usecases.AuthUseCase;
import com.deliveryos.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Controller Auth — Login, Register, Refresh, Logout.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "JWT authentication endpoints")
public class AuthController {

    private final AuthUseCase authUseCase;

    // ── Login ────────────────────────────────────────────────

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        AuthResponse response = authUseCase.login(request, ipAddress, userAgent);

        return ResponseEntity.ok(ApiResponse.ok(response, "Login successful"));
    }

    // ── Register ─────────────────────────────────────────────

    @PostMapping("/register")
    @Operation(summary = "Register a new user — ADMIN only")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authUseCase.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "User registered successfully"));
    }

    // ── Refresh ──────────────────────────────────────────────

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @RequestBody Map<String, String> body,
            HttpServletRequest httpRequest) {

        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("refreshToken is required"));
        }

        String ipAddress = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        AuthResponse response = authUseCase.refresh(refreshToken, ipAddress, userAgent);

        return ResponseEntity.ok(ApiResponse.ok(response, "Token refreshed successfully"));
    }

    // ── Logout ───────────────────────────────────────────────

    @PostMapping("/logout")
    @Operation(summary = "Logout — revoke all refresh tokens")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal UserDetails userDetails) {

        // On récupère l'userId depuis le token — sera amélioré en Semaine 2
        // pour inclure le claim userId directement dans le JWT
        return ResponseEntity.ok(ApiResponse.ok(null, "Logged out successfully"));
    }

    // ── Helpers ──────────────────────────────────────────────

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}