package com.prtracker.backend.branch;

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
class BranchServiceTest {

    @Mock
    private GitHubApiClient client;

    @InjectMocks
    private BranchService service;

    @Test
    @DisplayName("listBranches() should delegate to client.fetchBranches and return list of BranchDto")
    void listBranches_delegatesToClient() {
        // Arrange
        BranchFilter filter = new BranchFilter(true, 3, 2);
        BranchDto dto1 = new BranchDto(
                "main",
                new BranchDto.CommitRef("sha1", "https://api.github.com/..."),
                true
        );
        BranchDto dto2 = new BranchDto(
                "dev",
                new BranchDto.CommitRef("sha2", "https://api.github.com/..."),
                false
        );
        when(client.fetchBranches("Bearer TOKEN", "ownerX", "repoY", filter))
                .thenReturn(Flux.just(dto1, dto2));

        // Act
        List<BranchDto> result = service.listBranches("Bearer TOKEN", "ownerX", "repoY", filter);

        // Assert
        assertThat(result).containsExactly(dto1, dto2);
        verify(client, times(1)).fetchBranches("Bearer TOKEN", "ownerX", "repoY", filter);
        verifyNoMoreInteractions(client);
    }
}