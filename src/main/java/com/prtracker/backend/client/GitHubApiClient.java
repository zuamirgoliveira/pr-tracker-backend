package com.prtracker.backend.client;

import com.prtracker.backend.dto.GitHubPullRequestDto;
import com.prtracker.backend.dto.GitHubUserDto;
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

    public List<GitHubPullRequestDto> getPullRequests(String owner, String repo, String state, String authorization) {
        return githubWebClient.get()
                .uri("/repos/{owner}/{repo}/pulls?state={state}", owner, repo, state)
                .header("Authorization", authorization)
                .retrieve()
                .bodyToFlux(GitHubPullRequestDto.class)
                .collectList()
                .block();
    }

    public GitHubUserDto fetchCurrentUser(String authHeader) {
        return githubWebClient.get()
                .uri("/user")
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToMono(GitHubUserDto.class)
                .block();
    }

}
