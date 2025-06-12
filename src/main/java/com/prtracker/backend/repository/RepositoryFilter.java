package com.prtracker.backend.repository;

import org.springframework.web.bind.annotation.RequestParam;

public record RepositoryFilter(
        @RequestParam(name = "type",       defaultValue = "all")     String type,
        @RequestParam(name = "sort",       defaultValue = "created") String sort,
        @RequestParam(name = "direction",  defaultValue = "desc")    String direction,
        @RequestParam(name = "perPage",    defaultValue = "30")      Integer perPage,
        @RequestParam(name = "page",       defaultValue = "1")       Integer page
) {}
