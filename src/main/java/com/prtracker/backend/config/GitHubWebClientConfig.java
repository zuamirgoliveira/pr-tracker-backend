package com.prtracker.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.web.reactive.function.client.*;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GitHubWebClientConfig {

    @Bean
    public WebClient githubWebClient(OAuth2AuthorizedClientManager manager) {
        var oauth2 = new ServletOAuth2AuthorizedClientExchangeFilterFunction(manager);
        oauth2.setDefaultOAuth2AuthorizedClient(true);

        return WebClient.builder()
                .baseUrl("https://api.github.com")
                .apply(oauth2.oauth2Configuration())
                .build();
    }
}