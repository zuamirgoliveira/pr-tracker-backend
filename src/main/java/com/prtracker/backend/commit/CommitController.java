package com.prtracker.backend.commit;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/repos/{owner}/{repo}")
@RequiredArgsConstructor
public class CommitController {

    private final CommitService service;

    @GetMapping("/commits")
    public ResponseEntity<List<CommitDto>> getCommits(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String owner,
            @PathVariable String repo,
            @ModelAttribute CommitsFilter filter
    ) {
        List<CommitDto> commits = service.listCommits(authHeader, owner, repo, filter);
        return ResponseEntity.ok(commits);
    }
}
