package com.prtracker.backend.repository;

import com.prtracker.backend.github.GitHubApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepositoryService {

    private final GitHubApiClient apiClient;

    public List<RepositoryDto> listUserRepos(String auth, RepositoryFilter filter) {
        return apiClient.fetchUserRepos(auth, filter)
                .collectList().block();
    }

    public List<RepositoryDto> listOrgRepos(String auth, String org, RepositoryFilter filter) {
        return apiClient.fetchOrgRepos(auth, org, filter)
                .collectList().block();
    }

    public List<RepositoryDto> listPublicRepos(
            String username,
            RepositoryFilter filter
    ) {
        return apiClient.fetchPublicRepos(username, filter)
                .collectList()
                .block();
    }
}
