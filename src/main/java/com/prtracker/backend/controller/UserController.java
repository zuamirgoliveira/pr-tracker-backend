package com.prtracker.backend.controller;

import com.prtracker.backend.client.GitHubApiClient;
import com.prtracker.backend.dto.GitHubUserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final GitHubApiClient gitHubApiClient;  // injete seu client aqui

    public UserController(GitHubApiClient gitHubApiClient) {
        this.gitHubApiClient = gitHubApiClient;
    }

    @GetMapping("/me")
    public ResponseEntity<GitHubUserDto> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {

        GitHubUserDto user = gitHubApiClient.fetchCurrentUser(authHeader);
        return ResponseEntity.ok(user);
    }

}
