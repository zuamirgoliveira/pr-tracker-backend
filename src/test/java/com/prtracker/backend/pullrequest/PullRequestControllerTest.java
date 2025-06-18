package com.prtracker.backend.pullrequest;

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

@WebMvcTest(controllers = {PullRequestController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class PullRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PullRequestService service;

    @Test
    @DisplayName("GET /api/v1/github/repos/{owner}/{repo}/pulls → 200 + JSON list of pull requests with default state")
    void listPullRequests_defaultState_returnsList() throws Exception {
        // Arrange
        PullRequestService.PullRequestWithAge entry = new PullRequestService.PullRequestWithAge(null, 7L);
        when(service.listPullRequests(eq("owner"), eq("repo"), eq("all"), eq("Bearer TOKEN")))
                .thenReturn(List.of(entry));

        // Act & Assert
        mvc.perform(get("/api/v1/github/repos/owner/repo/pulls")
                        .header("Authorization", "Bearer TOKEN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hoursOpen").value(7));

        // Verify delegation with default state
        verify(service).listPullRequests("owner", "repo", "all", "Bearer TOKEN");
    }

    @Test
    @DisplayName("GET .../pulls?state=open → 200 + JSON list with custom state")
    void listPullRequests_customState_delegatesAndReturnsList() throws Exception {
        // Arrange
        PullRequestService.PullRequestWithAge entry = new PullRequestService.PullRequestWithAge(null, 3L);
        when(service.listPullRequests(eq("owner"), eq("repo"), eq("open"), eq("Bearer TOKEN")))
                .thenReturn(List.of(entry));

        // Act & Assert
        mvc.perform(get("/api/v1/github/repos/owner/repo/pulls?state=open")
                        .header("Authorization", "Bearer TOKEN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hoursOpen").value(3));

        // Verify delegation with custom state
        verify(service).listPullRequests("owner", "repo", "open", "Bearer TOKEN");
    }

    @Test
    @DisplayName("GET .../pulls without Authorization header → 400 Bad Request")
    void listPullRequests_missingAuthHeader_returnsBadRequest() throws Exception {
        mvc.perform(get("/api/v1/github/repos/owner/repo/pulls"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET .../pulls service throws Exception → 500 + ErrorDto")
    void listPullRequests_serviceThrowsInternalError_returnsErrorDto() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Unexpected failure"))
                .when(service).listPullRequests(any(), any(), any(), any());

        // Act & Assert
        mvc.perform(get("/api/v1/github/repos/owner/repo/pulls")
                        .header("Authorization", "Bearer TOKEN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }
}