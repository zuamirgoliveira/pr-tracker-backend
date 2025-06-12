package com.prtracker.backend.branch;

import org.springframework.web.bind.annotation.RequestParam;

public record BranchFilter(
        @RequestParam(name = "protected", required = false) Boolean onlyProtected,
        @RequestParam(name = "perPage",    defaultValue = "30") Integer perPage,
        @RequestParam(name = "page",       defaultValue = "1")  Integer page
) {
}
