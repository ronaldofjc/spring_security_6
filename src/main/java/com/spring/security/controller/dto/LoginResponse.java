package com.spring.security.controller.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
}
