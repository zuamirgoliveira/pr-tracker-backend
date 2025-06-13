package com.prtracker.backend.repository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RepositoryController {

    private final RepositoryService service;

    @GetMapping("/user/repos")
    public ResponseEntity<List<RepositoryDto>> userRepos( @Valid
            @RequestHeader("Authorization") String auth,
            @Validated @ModelAttribute RepositoryFilter dto) {
        var repos = service.listUserRepos(auth, dto);
        return ResponseEntity.ok(repos);
    }

    @GetMapping("/orgs/{org}/repos")
    public ResponseEntity<List<RepositoryDto>> orgRepos(
            @RequestHeader("Authorization") String auth,
            @PathVariable String org,
            @Validated @ModelAttribute RepositoryFilter dto) {
        var repos = service.listOrgRepos(auth, org, dto);
        return ResponseEntity.ok(repos);
    }

    @GetMapping("/users/{username}/repos")
    public ResponseEntity<List<RepositoryDto>> publicUserRepos(
            @PathVariable String username,
            @Validated @ModelAttribute RepositoryFilter filter
    ) {
        var repos = service.listPublicRepos(username, filter);
        return ResponseEntity.ok(repos);
    }
}
