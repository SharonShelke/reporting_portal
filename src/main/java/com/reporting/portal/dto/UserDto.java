package com.reporting.portal.dto;

public record UserDto(
    Long id,
    String firstName,
    String lastName,
    String name,
    String email,
    String role,
    String region,
    String status,
    String joined,
    String inviteToken
) {}
