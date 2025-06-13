package com.prtracker.backend.user;

import com.prtracker.backend.github.GitHubApiClient;
import com.prtracker.backend.pullrequest.PullRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final GitHubApiClient apiClient;

    public UserDto fetchCurrentUser(String authHeader) {
        return apiClient.fetchUser(authHeader);
    }

}
