package com.template.templateapi.web.controller;

import com.template.templateapi.persistence.entity.UserEntity;
import com.template.templateapi.service.AuthService;
import com.template.templateapi.service.dto.request.ChangePasswordRequest;
import com.template.templateapi.service.dto.request.LoginRequest;
import com.template.templateapi.service.dto.request.RegisterRequest;
import com.template.templateapi.service.dto.response.user.UserResponse;
import com.template.templateapi.web.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> signUp(@Valid @RequestBody RegisterRequest request) {
        UserEntity saved = this.authService.register(request);

        ResponseCookie cookie = buildCookie(this.authService.login(request.getUsername(), request.getPassword()), Duration.ofMinutes(60 * 24));

        return ApiResponse.success(UserResponse.fromEntity(saved), "User registered successfully", HttpStatus.CREATED, cookie);
    }

    @PostMapping("/login")
    public ResponseEntity<?> signIn(@Valid @RequestBody LoginRequest request){
        String jwt = this.authService.login(request.getUsername(), request.getPassword());

        ResponseCookie cookie = buildCookie(jwt, Duration.ofMinutes(60 * 24));

        return ApiResponse.success(null, "Login successfully.", HttpStatus.OK, cookie);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> signOut(){
        ResponseCookie cookie = buildCookie("", Duration.ZERO);

        return ApiResponse.success(null, "Logout successfully.", HttpStatus.OK, cookie);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal Long id){
        Map<String,Object> data = this.authService.getAuthenticatedUserData(id);

        return ApiResponse.success(data, "User authenticated.", HttpStatus.OK);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request, @PathVariable Long id){
        return ApiResponse.success(null, "Password updated successfully.", HttpStatus.OK);
    }

    private ResponseCookie buildCookie(String jwt, Duration maxAge) {
        return ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Lax")
                .build();
    }
}
