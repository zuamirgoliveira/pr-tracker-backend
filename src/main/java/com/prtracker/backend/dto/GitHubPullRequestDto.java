package com.prtracker.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public record GitHubPullRequestDto(
        String title,
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("created_at") ZonedDateTime createdAt,
        @JsonProperty("user") User user,
        @JsonProperty("state") String state
) {
    public record User(String login, @JsonProperty("avatar_url") String avatarUrl) {}
}
