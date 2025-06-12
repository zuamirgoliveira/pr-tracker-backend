package com.prtracker.backend.pullrequest;

import com.prtracker.backend.github.GitHubApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PullRequestService {

    private final GitHubApiClient apiClient;

    public List<PullRequestWithAge> listPullRequests(String owner, String repo, String state, String auth) {
        List<PullRequestDto> pullRequests = apiClient.getPullRequests(owner, repo, state, auth);

        return pullRequests.stream()
                .map(pr -> new PullRequestWithAge(pr, Duration.between(pr.createdAt(), ZonedDateTime.now()).toHours()))
                .toList();
    }

    public record PullRequestWithAge(PullRequestDto pullRequest, long hoursOpen) {}
}
