package com.prtracker.backend.commit;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommitDto(
        String sha,
        @JsonProperty("html_url") String htmlUrl,
        CommitInfo commit,
        GitHubUser author
) {}

