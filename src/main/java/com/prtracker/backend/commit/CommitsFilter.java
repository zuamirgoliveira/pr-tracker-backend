package com.prtracker.backend.commit;

import org.springframework.web.bind.annotation.RequestParam;

public record CommitsFilter(
        @RequestParam(name = "sha",       required = false) String sha,
        @RequestParam(name = "path",      required = false) String path,
        @RequestParam(name = "author",    required = false) String author,
        @RequestParam(name = "committer", required = false) String committer,
        @RequestParam(name = "since",     required = false) String since,
        @RequestParam(name = "until",     required = false) String until,
        @RequestParam(name = "perPage",   defaultValue = "30") Integer perPage,
        @RequestParam(name = "page",      defaultValue = "1")  Integer page
) {}
