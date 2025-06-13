package com.prtracker.backend.user;

import com.prtracker.backend.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {UserController.class, GlobalExceptionHandler.class})
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser
    @DisplayName("GET /api/v1/user → 200 + JSON do usuário")
    void shouldReturnCurrentUser() throws Exception {
        UserDto dto = new UserDto(
                "usuarioteste",
                "Usuario Teste",
                "https://avatar.url/teste.png",
                "teste@example.com"
        );


        Mockito.when(userService.fetchCurrentUser("Bearer TOKEN"))
                .thenReturn(dto);

        mvc.perform(get("/api/v1/user")
                        .header("Authorization", "Bearer TOKEN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.login").value("usuarioteste"))
                .andExpect(jsonPath("$.name").value("Usuario Teste"))
                .andExpect(jsonPath("$.avatar_url").value("https://avatar.url/teste.png"))
                .andExpect(jsonPath("$.email").value("teste@example.com"));
    }

    @Test
    @DisplayName("GET /api/v1/user sem token → 401 Unauthorized")
    void shouldReturn401IfNoAuth() throws Exception {
        mvc.perform(get("/api/v1/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/v1/user →  GitHub API 404 → repassa ErrorDto")
    void shouldReturnErrorDtoWhenServiceThrows() throws Exception {
        Mockito.when(userService.fetchCurrentUser("Bearer BAD"))
                .thenThrow(new RuntimeException("GitHub not found"));

        mvc.perform(get("/api/v1/user")
                        .header("Authorization", "Bearer BAD"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }
}
