package com.prtracker.backend.pullrequest;

import com.prtracker.backend.github.GitHubApiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PullRequestServiceTest {

    @Mock
    private GitHubApiClient apiClient;

    @InjectMocks
    private PullRequestService service;

    @Test
    @DisplayName("listPullRequests should map PRs to PullRequestWithAge and preserve nested User and state")
    void shouldMapPullRequestsWithNestedUserAndHoursOpen() {
        ZonedDateTime now = ZonedDateTime.now();
        // PR created now
        PullRequestDto.User user1 = new PullRequestDto.User("user1", "avatar1");
        PullRequestDto pr1 = new PullRequestDto(
                "title1", "https://url1", now, user1, "open"
        );
        // PR created 5 hours ago
        PullRequestDto.User user2 = new PullRequestDto.User("user2", "avatar2");
        PullRequestDto pr2 = new PullRequestDto(
                "title2", "https://url2", now.minusHours(5), user2, "closed"
        );
        when(apiClient.getPullRequests("owner", "repo", "all", "Bearer TOKEN"))
                .thenReturn(List.of(pr1, pr2));

        List<PullRequestService.PullRequestWithAge> result =
                service.listPullRequests("owner", "repo", "all", "Bearer TOKEN");

        assertThat(result).hasSize(2);
        var w1 = result.get(0);
        assertThat(w1.pullRequest()).isSameAs(pr1);
        assertThat(w1.pullRequest().user().login()).isEqualTo("user1");
        assertThat(w1.pullRequest().state()).isEqualTo("open");
        assertThat(w1.hoursOpen()).isZero();

        var w2 = result.get(1);
        assertThat(w2.pullRequest()).isSameAs(pr2);
        assertThat(w2.pullRequest().user().avatarUrl()).isEqualTo("avatar2");
        assertThat(w2.pullRequest().state()).isEqualTo("closed");
        assertThat(w2.hoursOpen()).isEqualTo(5);
    }

    @Test
    @DisplayName("listPullRequests returns empty list when no PRs")
    void shouldReturnEmptyListWhenNoPullRequests() {
        when(apiClient.getPullRequests(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        List<PullRequestService.PullRequestWithAge> result =
                service.listPullRequests("anyOwner", "anyRepo", "anyState", "anyAuth");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("listPullRequests propagates exception from apiClient")
    void shouldPropagateExceptionFromApiClient() {
        RuntimeException ex = new RuntimeException("API failure");
        when(apiClient.getPullRequests(any(), any(), any(), any()))
                .thenThrow(ex);

        assertThatThrownBy(() ->
                service.listPullRequests("o", "r", "s", "auth")
        ).isSameAs(ex);
    }
}