package com.prtracker.backend.commit;

import com.prtracker.backend.github.GitHubApiClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommitService {

    private final GitHubApiClient client;

    public CommitService(GitHubApiClient client) {
        this.client = client;
    }

    public List<CommitDto> listCommits(
            String authHeader,
            String owner,
            String repo,
            CommitsFilter filter
    ) {
        return client.fetchCommits(authHeader, owner, repo, filter)
                .collectList()
                .block();
    }
}