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
    public List<PullRequestWithAge> listPullRequests(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(defaultValue = "all") String state,
            @RequestHeader("Authorization") String auth
    ) {
        return service.listPullRequests(owner, repo, state, auth);
    }

}
