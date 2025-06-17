package com.prtracker.backend.repository;

import com.prtracker.backend.github.GitHubApiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepositoryServiceTest {

    @Mock
    private GitHubApiClient apiClient;

    @InjectMocks
    private RepositoryService service;

    @Test
    @DisplayName("listUserRepos() should delegate to apiClient.fetchUserRepos and return list")
    void listUserRepos_delegatesToApiClient() {
        // Arrange
        RepositoryFilter filter = new RepositoryFilter("all", "created", "desc", 5, 1);
        RepositoryDto dto1 = new RepositoryDto(
                101L,
                "repo1",
                "owner1/repo1",
                "https://url1",
                "First repository",
                false,
                "2025-01-01T00:00:00Z",
                "2025-01-02T00:00:00Z"
        );
        RepositoryDto dto2 = new RepositoryDto(
                102L,
                "repo2",
                "owner2/repo2",
                "https://url2",
                "Second repository",
                true,
                "2025-02-01T00:00:00Z",
                "2025-02-02T00:00:00Z"
        );
        when(apiClient.fetchUserRepos("Bearer TOKEN", filter))
                .thenReturn(Flux.just(dto1, dto2));

        List<RepositoryDto> result = service.listUserRepos("Bearer TOKEN", filter);

        assertThat(result).containsExactly(dto1, dto2);
        verify(apiClient, times(1)).fetchUserRepos("Bearer TOKEN", filter);
        verifyNoMoreInteractions(apiClient);
    }

    @Test
    @DisplayName("listOrgRepos() should delegate to apiClient.fetchOrgRepos and return list")
    void listOrgRepos_delegatesToApiClient() {
        RepositoryFilter filter = new RepositoryFilter("forks", "updated", "asc", 3, 2);
        RepositoryDto dto = new RepositoryDto(
                201L,
                "repoX",
                "orgA/repoX",
                "https://urlX",
                "Organization repository",
                false,
                "2025-03-01T00:00:00Z",
                "2025-03-02T00:00:00Z"
        );
        when(apiClient.fetchOrgRepos("Bearer TOKEN", "orgA", filter))
                .thenReturn(Flux.just(dto));

        List<RepositoryDto> result = service.listOrgRepos("Bearer TOKEN", "orgA", filter);

        assertThat(result).containsExactly(dto);
        verify(apiClient, times(1)).fetchOrgRepos("Bearer TOKEN", "orgA", filter);
        verifyNoMoreInteractions(apiClient);
    }

    @Test
    @DisplayName("listPublicRepos() should delegate to apiClient.fetchPublicRepos and return list")
    void listPublicRepos_delegatesToApiClient() {
        RepositoryFilter filter = new RepositoryFilter("public", "pushed", "desc", 4, 3);
        RepositoryDto dto = new RepositoryDto(
                301L,
                "repoY",
                "userB/repoY",
                "https://urlY",
                "Public repository",
                true,
                "2025-04-01T00:00:00Z",
                "2025-04-02T00:00:00Z"
        );
        when(apiClient.fetchPublicRepos("userB", filter))
                .thenReturn(Flux.just(dto));

        List<RepositoryDto> result = service.listPublicRepos("userB", filter);

        assertThat(result).containsExactly(dto);
        verify(apiClient, times(1)).fetchPublicRepos("userB", filter);
        verifyNoMoreInteractions(apiClient);
    }
}