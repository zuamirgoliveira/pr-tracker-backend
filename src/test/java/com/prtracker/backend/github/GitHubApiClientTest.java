package com.prtracker.backend.github;

import com.prtracker.backend.branch.BranchFilter;
import com.prtracker.backend.commit.CommitsFilter;
import com.prtracker.backend.pullrequest.PullRequestDto;
import com.prtracker.backend.repository.RepositoryDto;
import com.prtracker.backend.repository.RepositoryFilter;
import com.prtracker.backend.user.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class GitHubApiClientTest {

    private AtomicReference<ClientRequest> captured;
    private GitHubApiClient client;

    @BeforeEach
    void setup() {
        captured = new AtomicReference<>();

        ExchangeFunction spyExchange = request -> {
            captured.set(request);
            String path = request.url().getPath();
            String body = "[]";
            if (path.equals("/user")) {
                body = "{\"login\":\"zuma\",\"name\":\"Zuma\",\"avatar_url\":null,\"email\":null}";
            }
            return Mono.just(
                    ClientResponse
                            .create(HttpStatus.OK)
                            .header("Content-Type", "application/json")
                            .body(body)
                            .build()
            );
        };

        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .exchangeFunction(spyExchange)
                .build();

        client = new GitHubApiClient(webClient);
    }

    @Test
    @DisplayName("getPullRequests() should call correct URL and return empty list")
    void testGetPullRequests() {
        List<PullRequestDto> pulls = client.getPullRequests("owner", "repo", "open", "Bearer TOKEN");
        URI url = captured.get().url();
        assertThat(url.toString())
                .isEqualTo("https://api.github.com/repos/owner/repo/pulls?state=open");
        assertThat(captured.get().headers().getFirst("Authorization"))
                .isEqualTo("Bearer TOKEN");
        assertThat(pulls).isEmpty();
    }

    @Test
    @DisplayName("fetchUser() should call /user and map response")
    void testFetchUser() {
        UserDto user = client.fetchUser("Bearer TOKEN");
        URI url = captured.get().url();
        assertThat(url.toString())
                .isEqualTo("https://api.github.com/user");
        assertThat(captured.get().headers().getFirst("Authorization"))
                .isEqualTo("Bearer TOKEN");
        assertThat(user.login()).isEqualTo("zuma");
        assertThat(user.name()).isEqualTo("Zuma");
    }

    @Test
    @DisplayName("fetchUserRepos() should build correct URI with filters")
    void testFetchUserRepos() {
        RepositoryFilter filter = new RepositoryFilter("all", "created", "desc", 10, 2);
        Flux<RepositoryDto> flux = client.fetchUserRepos("Bearer TOKEN", filter);
        flux.collectList().block();

        URI url = captured.get().url();
        assertThat(url.getPath()).isEqualTo("/user/repos");
        assertThat(url.getQuery())
                .contains("type=all")
                .contains("sort=created")
                .contains("direction=desc")
                .contains("per_page=10")
                .contains("page=2");
        assertThat(captured.get().headers().getFirst("Authorization")).isEqualTo("Bearer TOKEN");
    }

    @Test
    @DisplayName("fetchOrgRepos() should build correct URI with filters")
    void testFetchOrgRepos() {
        RepositoryFilter filter = new RepositoryFilter("forks", "updated", "asc", 7, 4);
        client.fetchOrgRepos("Bearer TOKEN", "orgX", filter).collectList().block();

        URI url = captured.get().url();
        assertThat(url.getPath()).isEqualTo("/orgs/orgX/repos");
        assertThat(url.getQuery())
                .contains("type=forks")
                .contains("sort=updated")
                .contains("direction=asc")
                .contains("per_page=7")
                .contains("page=4");
    }

    @Test
    @DisplayName("fetchPublicRepos() should build correct URI with filters")
    void testFetchPublicRepos() {
        RepositoryFilter filter = new RepositoryFilter("sources", "pushed", "desc", 8, 5);
        client.fetchPublicRepos("userY", filter).collectList().block();

        URI url = captured.get().url();
        assertThat(url.getPath()).isEqualTo("/users/userY/repos");
        assertThat(url.getQuery())
                .contains("type=sources")
                .contains("sort=pushed")
                .contains("direction=desc")
                .contains("per_page=8")
                .contains("page=5");
    }

    @Test
    @DisplayName("fetchCommits() should build correct URI with filters")
    void testFetchCommits() {
        CommitsFilter filter = new CommitsFilter(
                "sha123", "path/to", "auth1", "comm1", "2025-06-01T00:00:00Z",
                "2025-06-02T00:00:00Z", 9, 6
        );
        client.fetchCommits("Bearer TOKEN", "owner", "repoZ", filter).collectList().block();

        URI url = captured.get().url();
        assertThat(url.getPath()).isEqualTo("/repos/owner/repoZ/commits");
        assertThat(url.getQuery())
                .contains("sha=sha123")
                .contains("path=path/to")
                .contains("author=auth1")
                .contains("committer=comm1")
                .contains("since=2025-06-01T00:00:00Z")
                .contains("until=2025-06-02T00:00:00Z")
                .contains("per_page=9")
                .contains("page=6");
    }

    @Test
    @DisplayName("fetchBranches() should build correct URI with filters")
    void testFetchBranches() {
        BranchFilter filter = new BranchFilter(true, 5, 3);
        Mono<Void> flux = client.fetchBranches("Bearer T", "o", "r", filter).then();
        flux.block();

        URI url = captured.get().url();
        assertThat(url.getPath()).isEqualTo("/repos/o/r/branches");
        assertThat(url.getQuery())
                .contains("protected=true")
                .contains("per_page=5")
                .contains("page=3");
    }
}