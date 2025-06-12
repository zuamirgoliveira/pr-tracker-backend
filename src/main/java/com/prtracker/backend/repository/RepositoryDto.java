package com.prtracker.backend.repository;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RepositoryDto(
        Long      id,
        String    name,
        @JsonProperty("full_name")
        String    fullName,
        @JsonProperty("html_url")
        String    htmlUrl,
        String    description,
        Boolean   fork,
        @JsonProperty("created_at")
        String    createdAt,
        @JsonProperty("updated_at")
        String    updatedAt
) {
}
