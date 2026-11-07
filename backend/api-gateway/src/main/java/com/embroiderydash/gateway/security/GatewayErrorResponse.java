package com.embroiderydash.gateway.security;

import java.time.OffsetDateTime;

public record GatewayErrorResponse(OffsetDateTime timestamp,int status,String error,String code,String message,String path) {}
