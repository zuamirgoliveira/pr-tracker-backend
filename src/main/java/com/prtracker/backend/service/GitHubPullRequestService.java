package com.prtracker.backend.service;

import com.prtracker.backend.client.GitHubApiClient;
import com.prtracker.backend.dto.GitHubPullRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GitHubPullRequestService {

    private final GitHubApiClient apiClient;

    public List<PullRequestWithAge> listOpenPullRequests(String owner, String repo, String state) {
        List<GitHubPullRequestDto> pullRequests = apiClient.getPullRequests(owner, repo, state);

        return pullRequests.stream()
                .map(pr -> new PullRequestWithAge(pr, Duration.between(pr.createdAt(), ZonedDateTime.now()).toHours()))
                .toList();
    }

    public record PullRequestWithAge(GitHubPullRequestDto pullRequest, long hoursOpen) {}
}
