package com.embroiderydash.auth.api;

import com.embroiderydash.auth.application.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class AuthController {
    private final UserAccountService service;
    public AuthController(UserAccountService service) { this.service=service; }
    @PostMapping("/api/v1/auth/login") public LoginResponse login(@Valid @RequestBody LoginRequest request) { return service.login(request.username(), request.password()); }
    @GetMapping("/api/v1/auth/me") public UserResponse me(Authentication auth) { return service.get(auth.getName()); }
    @GetMapping("/api/v1/usuarios") public List<UserResponse> users() { return service.list(); }
    @PostMapping("/api/v1/usuarios") @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) { return service.createOperator(request); }
}
