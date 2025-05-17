package com.prtracker.backend.controller;

import com.prtracker.backend.service.GitHubPullRequestService;
import com.prtracker.backend.service.GitHubPullRequestService.PullRequestWithAge;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/github/repos")
@RequiredArgsConstructor
public class GitHubPullRequestController {

    private final GitHubPullRequestService service;

    @GetMapping("/{owner}/{repo}/pulls")
    public List<PullRequestWithAge> listOpenPullRequests(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(defaultValue = "all") String state
    ) {
        return service.listOpenPullRequests(owner, repo, state);
    }
}
