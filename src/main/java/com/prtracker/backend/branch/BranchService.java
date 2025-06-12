package com.prtracker.backend.branch;

import com.prtracker.backend.github.GitHubApiClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchService {
    private final GitHubApiClient client;

    public BranchService(GitHubApiClient client) {
        this.client = client;
    }

    public List<BranchDto> listBranches(
            String auth,
            String owner,
            String repo,
            BranchFilter filter
    ) {
        return client.fetchBranches(auth, owner, repo, filter)
                .collectList()
                .block();
    }
}