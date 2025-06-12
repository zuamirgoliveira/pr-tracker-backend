package com.prtracker.backend.commit;

public record CommitInfo(
        CommitAuthor author,
        String message
) {}
