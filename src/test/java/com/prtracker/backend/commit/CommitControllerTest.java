package com.prtracker.backend.commit;

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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CommitController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class CommitControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CommitService service;

    @Test
    @DisplayName("GET /api/v1/repos/{owner}/{repo}/commits → 200 + JSON list of commits with filter params")
    void getCommits_withFilter_returnsList() throws Exception {
        // Arrange
        CommitsFilter filter = new CommitsFilter("sha1", "path1", "auth1", "comm1", "2025-06-01T00:00:00Z", "2025-06-02T00:00:00Z", 10, 2);
        CommitDto dto1 = new CommitDto("shaA", "urlA", null, null);
        CommitDto dto2 = new CommitDto("shaB", "urlB", null, null);
        when(service.listCommits(eq("Bearer TOKEN"), eq("ownerX"), eq("repoY"), any(CommitsFilter.class)))
                .thenReturn(List.of(dto1, dto2));

        // Act & Assert
        mvc.perform(get("/api/v1/repos/ownerX/repoY/commits?sha=sha1&path=path1&author=auth1&committer=comm1&since=2025-06-01T00:00:00Z&until=2025-06-02T00:00:00Z&perPage=10&page=2")
                        .header("Authorization", "Bearer TOKEN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sha").value("shaA"))
                .andExpect(jsonPath("$[1].html_url").value("urlB"));

        verify(service).listCommits(eq("Bearer TOKEN"), eq("ownerX"), eq("repoY"), any(CommitsFilter.class));
    }

    @Test
    @DisplayName("GET /api/v1/repos/{owner}/{repo}/commits → 200 + JSON list with default filters")
    void getCommits_defaultFilters_returnsList() throws Exception {
        // Arrange
        CommitDto dto = new CommitDto("shaX", "urlX", null, null);
        when(service.listCommits(eq("Bearer TOKEN"), eq("own"), eq("rep"), any(CommitsFilter.class)))
                .thenReturn(List.of(dto));

        mvc.perform(get("/api/v1/repos/own/rep/commits")
                        .header("Authorization", "Bearer TOKEN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sha").value("shaX"));

        verify(service).listCommits(eq("Bearer TOKEN"), eq("own"), eq("rep"), any(CommitsFilter.class));
    }

    @Test
    @DisplayName("GET /api/v1/repos/{owner}/{repo}/commits missing Authorization header → 400 Bad Request")
    void getCommits_missingAuthHeader_returnsBadRequest() throws Exception {
        mvc.perform(get("/api/v1/repos/own/rep/commits"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/repos/{owner}/{repo}/commits service throws Exception → 500 + ErrorDto")
    void getCommits_serviceThrowsInternalError_returnsErrorDto() throws Exception {
        // Arrange
        doThrow(new RuntimeException("failure")).when(service).listCommits(any(), any(), any(), any());

        mvc.perform(get("/api/v1/repos/own/rep/commits")
                        .header("Authorization", "Bearer TOKEN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }
}