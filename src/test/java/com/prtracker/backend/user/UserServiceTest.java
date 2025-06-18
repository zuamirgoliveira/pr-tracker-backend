package com.prtracker.backend.user;

import com.prtracker.backend.github.GitHubApiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private GitHubApiClient apiClient;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("fetchCurrentUser() deve chamar apiClient.fetchUser e retornar o UserDto")
    void fetchCurrentUser_delegatesToApiClient() {
        UserDto expected = new UserDto(
                "usuariotest",
                "Usuario Teste",
                "https://avatar.url/teste.png",
                "teste@example.com"
        );
        when(apiClient.fetchUser("Bearer TOKEN")).thenReturn(expected);

        UserDto actual = userService.fetchCurrentUser("Bearer TOKEN");

        assertThat(actual).isSameAs(expected);
        verify(apiClient, times(1)).fetchUser("Bearer TOKEN");
        verifyNoMoreInteractions(apiClient);
    }

    @Test
    @DisplayName("fetchCurrentUser should propagate exception from apiClient")
    void fetchCurrentUser_propagatesException() {
        // Arrange
        RuntimeException ex = new RuntimeException("GitHub API error");
        when(apiClient.fetchUser(anyString())).thenThrow(ex);

        // Act & Assert
        assertThatThrownBy(() -> userService.fetchCurrentUser("Bearer BAD"))
                .isSameAs(ex);

        verify(apiClient, times(1)).fetchUser("Bearer BAD");
        verifyNoMoreInteractions(apiClient);
    }
}