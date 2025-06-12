package com.prtracker.backend.commit;

public record CommitAuthor(
        String name,
        String email,
        String date
) {}
