package com.prtracker.backend.controller;

import com.prtracker.backend.dto.GitHubUserDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/me")
    public GitHubUserDto getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        System.out.println("User attributes: " + principal.getAttributes());
        return new GitHubUserDto(
                principal.getAttribute("login"),
                Optional.ofNullable(principal.getAttribute("name")).orElse("").toString(),
                principal.getAttribute("avatar_url"),
                Optional.ofNullable(principal.getAttribute("email")).orElse("not provided").toString()
        );
    }

}
