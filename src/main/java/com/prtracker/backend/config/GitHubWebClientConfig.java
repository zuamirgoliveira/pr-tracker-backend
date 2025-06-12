package com.prtracker.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GitHubWebClientConfig {

    @Bean
    public WebClient githubWebClient(OAuth2AuthorizedClientManager manager) {
        return WebClient.builder()
                .baseUrl("https://api.github.com")
                .build();
    }

}