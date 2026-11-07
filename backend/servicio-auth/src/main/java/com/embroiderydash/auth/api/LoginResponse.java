package com.embroiderydash.auth.api;

import com.embroiderydash.auth.domain.UserAccount;

public record LoginResponse(String token, String accessToken, String tokenType, long expiresIn, UserResponse user) {
    public static LoginResponse of(String token, long expiresIn, UserAccount user) { return new LoginResponse(token, token, "Bearer", expiresIn, UserResponse.from(user)); }
}
