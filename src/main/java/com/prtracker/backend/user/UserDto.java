package com.prtracker.backend.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDto(
        String login,
        String name,
        @JsonProperty("avatar_url")
        String avatarUrl,
        String email
) {}