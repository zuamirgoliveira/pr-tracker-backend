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

    public List<GitHubPullRequestDto> getPullRequests(String owner, String repo, String state) {
        return githubWebClient.get()
                .uri("/repos/{owner}/{repo}/pulls?state={state}", owner, repo, state)
                .retrieve()
                .bodyToFlux(GitHubPullRequestDto.class)
                .collectList()
                .block();
    }

}
