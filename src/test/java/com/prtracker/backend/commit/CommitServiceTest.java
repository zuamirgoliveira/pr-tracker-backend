package com.prtracker.backend.commit;

import com.prtracker.backend.github.GitHubApiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommitServiceTest {

    @Mock
    private GitHubApiClient client;

    @InjectMocks
    private CommitService service;

    @Test
    @DisplayName("listCommits should delegate to client.fetchCommits and return list of CommitDto")
    void listCommits_delegatesAndReturnsList() {
        // Arrange
        CommitsFilter filter = new CommitsFilter(
                "shaVal", "pathVal", "authorVal", "committerVal", "2025-06-01T00:00:00Z", "2025-06-02T00:00:00Z", 5, 1
        );
        CommitAuthor author = new CommitAuthor("Name", "email@example.com", "2025-06-01T12:00:00Z");
        CommitInfo info = new CommitInfo(author, "message1");
        GitHubUser user = new GitHubUser("login1", "avatar1");
        CommitDto dto1 = new CommitDto("sha1", "https://html1", info, user);
        CommitDto dto2 = new CommitDto("sha2", "https://html2", info, user);
        when(client.fetchCommits("Bearer TOKEN", "ownerX", "repoY", filter))
                .thenReturn(Flux.just(dto1, dto2));

        // Act
        List<CommitDto> result = service.listCommits("Bearer TOKEN", "ownerX", "repoY", filter);

        // Assert
        assertThat(result).containsExactly(dto1, dto2);
        verify(client, times(1)).fetchCommits("Bearer TOKEN", "ownerX", "repoY", filter);
        verifyNoMoreInteractions(client);
    }

    @Test
    @DisplayName("listCommits returns empty list when no commits")
    void listCommits_returnsEmptyList() {
        // Arrange
        when(client.fetchCommits(anyString(), anyString(), anyString(), any(CommitsFilter.class)))
                .thenReturn(Flux.empty());

        // Act
        List<CommitDto> result = service.listCommits("auth", "o", "r", new CommitsFilter(null, null, null, null, null, null, 0, 0));

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("listCommits should propagate exception from client.fetchCommits")
    void listCommits_propagatesException() {
        // Arrange
        RuntimeException ex = new RuntimeException("Clients error");
        when(client.fetchCommits(anyString(), anyString(), anyString(), any(CommitsFilter.class)))
                .thenReturn(Flux.error(ex));

        // Act & Assert
        assertThatThrownBy(() ->
                service.listCommits("auth", "o", "r", new CommitsFilter(null, null, null, null, null, null, 0, 0))
        ).isSameAs(ex);

        verify(client, times(1)).fetchCommits(anyString(), anyString(), anyString(), any(CommitsFilter.class));
    }
}