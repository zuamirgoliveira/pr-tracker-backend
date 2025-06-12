package com.prtracker.backend.commit;

import com.fasterxml.jackson.annotation.JsonProperty;
public record GitHubUser(
        String login,
        @JsonProperty("avatar_url") String avatarUrl
) {}
