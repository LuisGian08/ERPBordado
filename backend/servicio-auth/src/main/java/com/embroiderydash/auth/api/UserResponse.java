package com.embroiderydash.auth.api;

import com.embroiderydash.auth.domain.UserAccount;
import java.time.OffsetDateTime;

public record UserResponse(Long id, String username, String displayName, String role, boolean enabled, OffsetDateTime createdAt) {
    public static UserResponse from(UserAccount u) { return new UserResponse(u.getId(), u.getUsername(), u.getDisplayName(), u.getRole().name(), u.isEnabled(), u.getCreatedAt()); }
}
