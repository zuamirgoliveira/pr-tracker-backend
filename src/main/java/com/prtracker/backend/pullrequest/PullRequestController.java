package com.prtracker.backend.pullrequest;

import com.prtracker.backend.pullrequest.PullRequestService.PullRequestWithAge;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/github/repos")
@RequiredArgsConstructor
public class PullRequestController {

    private final PullRequestService service;

    @GetMapping("/{owner}/{repo}/pulls")
    public List<PullRequestWithAge> listOpenPullRequests(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(defaultValue = "all") String state,
            @RequestHeader("Authorization") String authorization
    ) {
        return service.listOpenPullRequests(owner, repo, state, authorization);
    }
}
