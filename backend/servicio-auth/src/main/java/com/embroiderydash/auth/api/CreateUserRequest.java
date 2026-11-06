package com.embroiderydash.auth.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.embroiderydash.auth.domain.Role;

public record CreateUserRequest(
        @NotBlank @Size(min = 3, max = 80) String username,
        @NotBlank @Size(min = 2, max = 150) String displayName,
        @NotBlank @Size(min = 12, max = 72) String password,
        Role role) {}
