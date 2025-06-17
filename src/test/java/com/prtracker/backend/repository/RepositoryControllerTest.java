package com.prtracker.backend.repository;

import com.prtracker.backend.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {RepositoryController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class RepositoryControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private RepositoryService service;

    @Test
    @DisplayName("GET /api/v1/user/repos → 200 + JSON list of user repos")
    void userRepos_returnsList() throws Exception {
        // Arrange
        RepositoryDto repo1 = new RepositoryDto(1L, "r1", "o/r1", "https://url1", "desc1", false, "2025-01-01T00:00:00Z", "2025-01-02T00:00:00Z");
        RepositoryDto repo2 = new RepositoryDto(2L, "r2", "o/r2", "https://url2", "desc2", true,  "2025-02-01T00:00:00Z", "2025-02-02T00:00:00Z");
        when(service.listUserRepos(eq("Bearer TOKEN"), any(RepositoryFilter.class)))
                .thenReturn(List.of(repo1, repo2));

        // Act & Assert
        mvc.perform(get("/api/v1/user/repos")
                        .header("Authorization", "Bearer TOKEN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("r1"))
                .andExpect(jsonPath("$[1].full_name").value("o/r2"));
    }

    @Test
    @DisplayName("GET /api/v1/orgs/{org}/repos → 200 + JSON list of org repos")
    void orgRepos_returnsList() throws Exception {
        // Arrange
        RepositoryDto repo = new RepositoryDto(3L, "rx", "orgX/rx", "https://urlX", "descX", false, "2025-03-01T00:00:00Z", "2025-03-02T00:00:00Z");
        when(service.listOrgRepos(eq("Bearer TOKEN"), eq("orgX"), any(RepositoryFilter.class)))
                .thenReturn(List.of(repo));

        // Act & Assert
        mvc.perform(get("/api/v1/orgs/orgX/repos?type=forks&page=2")
                        .header("Authorization", "Bearer TOKEN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].fork").value(false));
    }

    @Test
    @DisplayName("GET /api/v1/users/{username}/repos → 200 + JSON list of public user repos")
    void publicUserRepos_returnsList() throws Exception {
        // Arrange
        RepositoryDto repo = new RepositoryDto(4L, "ru", "userA/ru", "https://urlU", "descU", true, "2025-04-01T00:00:00Z", "2025-04-02T00:00:00Z");
        when(service.listPublicRepos(eq("userA"), any(RepositoryFilter.class)))
                .thenReturn(List.of(repo));

        // Act & Assert
        mvc.perform(get("/api/v1/users/userA/repos?sort=updated&perPage=5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].full_name").value("userA/ru"))
                .andExpect(jsonPath("$[0].html_url").value("https://urlU"));
    }
}
