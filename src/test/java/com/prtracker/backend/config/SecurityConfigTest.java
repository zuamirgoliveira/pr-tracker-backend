package com.prtracker.backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void corsConfigurationSource_shouldHaveExpectedSettings() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "api/**");
        CorsConfiguration config = source.getCorsConfiguration(request);

        assertThat(config).isNotNull();
        assertThat(config.getAllowedOrigins())
                .containsExactly("http://localhost:8080", "http://localhost:3000");
        assertThat(config.getAllowedMethods())
                .containsExactly("GET");
        assertThat(config.getAllowedHeaders())
                .containsExactly("Authorization", "Content-Type", "Accept");
        assertThat(config.getAllowCredentials()).isTrue();
    }
}