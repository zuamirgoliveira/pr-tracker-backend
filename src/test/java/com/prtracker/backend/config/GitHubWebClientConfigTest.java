package com.prtracker.backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GitHubWebClientConfigTest {

    @Test
    void githubWebClient_shouldUseBaseUrl() {
        GitHubWebClientConfig config = new GitHubWebClientConfig();

        WebClient client = config.githubWebClient();

        AtomicReference<URI> capturedUrl = new AtomicReference<>();
        ExchangeFunction spyExchange = request -> {
            capturedUrl.set(request.url());
            return Mono.just(ClientResponse.create(HttpStatus.OK).build());
        };

        WebClient testClient = client.mutate()
                .exchangeFunction(spyExchange)
                .build();

        testClient.get()
                .uri("/v1")
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        assertThat(capturedUrl.get().toString())
                .isEqualTo("https://api.github.com/v1");
    }
}