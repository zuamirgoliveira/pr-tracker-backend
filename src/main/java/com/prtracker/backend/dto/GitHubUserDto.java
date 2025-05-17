package com.prtracker.backend.dto;

public record GitHubUserDto(
        String login,
        String name,
        String avatarUrl,
        String email
) {}