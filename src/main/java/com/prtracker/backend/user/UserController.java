package com.prtracker.backend.user;

import com.prtracker.backend.github.GitHubApiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final GitHubApiClient gitHubApiClient;  // injete seu client aqui

    public UserController(GitHubApiClient gitHubApiClient) {
        this.gitHubApiClient = gitHubApiClient;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {

        UserDto user = gitHubApiClient.fetchCurrentUser(authHeader);
        return ResponseEntity.ok(user);
    }

}
