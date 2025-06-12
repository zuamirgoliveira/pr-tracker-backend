package com.prtracker.backend.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RepositoryController {

    private final RepositoryService service;

    @GetMapping("/user/repos")
    public ResponseEntity<List<RepositoryDto>> userRepos(
            @RequestHeader("Authorization") String auth,
            @ModelAttribute RepositoryFilter dto) {
        var repos = service.listUserRepos(auth, dto);
        return ResponseEntity.ok(repos);
    }

    @GetMapping("/orgs/{org}/repos")
    public ResponseEntity<List<RepositoryDto>> orgRepos(
            @RequestHeader("Authorization") String auth,
            @PathVariable String org,
            @ModelAttribute RepositoryFilter dto) {
        var repos = service.listOrgRepos(auth, org, dto);
        return ResponseEntity.ok(repos);
    }
}
