package com.prtracker.backend.branch;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BranchDto(
        String name,
        CommitRef commit,
        @JsonProperty("protected") boolean isProtected
) {
    public record CommitRef(
            String sha,
            @JsonProperty("url") String apiUrl
    ) {}
}
