package com.prtracker.backend.client;

import com.prtracker.backend.dto.GitHubPullRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GitHubApiClient {

    @Qualifier("githubWebClient")
    private final WebClient githubWebClient;

    public List<GitHubPullRequestDto> getOpenPullRequests(String owner, String repo) {
        return githubWebClient.get()
                .uri("/repos/{owner}/{repo}/pulls?state=open", owner, repo)
                .retrieve()
                .bodyToFlux(GitHubPullRequestDto.class)
                .collectList()
                .block();
    }
}
