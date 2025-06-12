package com.prtracker.backend.github;

import com.prtracker.backend.pullrequest.PullRequestDto;
import com.prtracker.backend.user.UserDto;
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

    public List<PullRequestDto> getPullRequests(String owner, String repo, String state, String authorization) {
        return githubWebClient.get()
                .uri("/repos/{owner}/{repo}/pulls?state={state}", owner, repo, state)
                .header("Authorization", authorization)
                .retrieve()
                .bodyToFlux(PullRequestDto.class)
                .collectList()
                .block();
    }

    public UserDto fetchCurrentUser(String authHeader) {
        return githubWebClient.get()
                .uri("/user")
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }

}
