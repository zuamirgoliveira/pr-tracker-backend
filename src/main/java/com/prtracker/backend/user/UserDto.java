package com.prtracker.backend.user;

public record UserDto(
        String login,
        String name,
        String avatarUrl,
        String email
) {}