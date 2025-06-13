package com.prtracker.backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping()
    public ResponseEntity<UserDto> getUser(
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(service.fetchCurrentUser(auth));
    }

}
