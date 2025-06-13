package com.prtracker.backend.branch;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/repos/{owner}/{repo}")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService service;

    @GetMapping("/branches")
    public ResponseEntity<List<BranchDto>> getBranches(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String owner,
            @PathVariable String repo,
            @Validated @ModelAttribute BranchFilter filter
    ) {
        List<BranchDto> branches =
                service.listBranches(authHeader, owner, repo, filter);
        return ResponseEntity.ok(branches);
    }
}
