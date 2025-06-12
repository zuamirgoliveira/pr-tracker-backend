package com.prtracker.backend.github;

import com.prtracker.backend.commit.CommitDto;
import com.prtracker.backend.commit.CommitsFilter;
import com.prtracker.backend.pullrequest.PullRequestDto;
import com.prtracker.backend.repository.RepositoryDto;
import com.prtracker.backend.repository.RepositoryFilter;
import com.prtracker.backend.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GitHubApiClient {

    @Qualifier("githubWebClient")
    private final WebClient webClient;

    public List<PullRequestDto> getPullRequests(String owner, String repo, String state, String auth) {
        return webClient.get()
                .uri("/repos/{owner}/{repo}/pulls?state={state}", owner, repo, state)
                .header("Authorization", auth)
                .retrieve()
                .bodyToFlux(PullRequestDto.class)
                .collectList()
                .block();
    }

    public UserDto fetchUser(String auth) {
        return webClient.get()
                .uri("/user")
                .header("Authorization", auth)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }

    public Flux<RepositoryDto> fetchUserRepos(String auth, RepositoryFilter filter) {
        return webClient.get()
                .uri(u -> u.path("/user/repos")
                        .queryParam("type", filter.type())
                        .queryParam("sort", filter.sort())
                        .queryParam("direction", filter.direction())
                        .queryParam("per_page", filter.perPage())
                        .queryParam("page", filter.page())
                        .build())
                .header("Authorization", auth)
                .retrieve()
                .bodyToFlux(RepositoryDto.class);
    }

    public Flux<RepositoryDto> fetchOrgRepos(String auth, String org, RepositoryFilter filter) {
        return webClient.get()
                .uri(u -> u.path("/orgs/{org}/repos")
                        .queryParam("type", filter.type())
                        .queryParam("sort", filter.sort())
                        .queryParam("direction", filter.direction())
                        .queryParam("per_page", filter.perPage())
                        .queryParam("page", filter.page())
                        .build(org))
                .header("Authorization", auth)
                .retrieve()
                .bodyToFlux(RepositoryDto.class);
    }

    public Flux<CommitDto> fetchCommits(
            String auth,
            String owner,
            String repo,
            CommitsFilter filter
    ) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repos/{owner}/{repo}/commits")
                        .queryParam("sha",       filter.sha())
                        .queryParam("path",      filter.path())
                        .queryParam("author",    filter.author())
                        .queryParam("committer", filter.committer())
                        .queryParam("since",     filter.since())
                        .queryParam("until",     filter.until())
                        .queryParam("per_page",  filter.perPage())
                        .queryParam("page",      filter.page())
                        .build(owner, repo))
                .header("Authorization", auth)
                .retrieve()
                .bodyToFlux(CommitDto.class);
    }

    public Flux<RepositoryDto> fetchPublicRepos(
            String username,
            RepositoryFilter filter
    ) {
        return webClient.get()
                .uri(u -> u.path("/users/{username}/repos")
                        .queryParam("type",      filter.type())
                        .queryParam("sort",      filter.sort())
                        .queryParam("direction", filter.direction())
                        .queryParam("per_page",  filter.perPage())
                        .queryParam("page",      filter.page())
                        .build(username))
                .retrieve()
                .bodyToFlux(RepositoryDto.class);
    }
}
