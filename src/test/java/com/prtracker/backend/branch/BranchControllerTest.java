package com.prtracker.backend.branch;

import com.prtracker.backend.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BranchController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class BranchControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BranchService service;

    @Test
    @DisplayName("GET /api/v1/repos/{owner}/{repo}/branches → 200 + JSON list of branches with filter params")
    void getBranches_withFilter_returnsList() throws Exception {
        // Arrange
        BranchDto.CommitRef ref1 = new BranchDto.CommitRef("sha1", "url1");
        BranchDto.CommitRef ref2 = new BranchDto.CommitRef("sha2", "url2");
        BranchDto b1 = new BranchDto("main", ref1, true);
        BranchDto b2 = new BranchDto("dev", ref2, false);
        when(service.listBranches(eq("Bearer TOKEN"), eq("ownerX"), eq("repoY"), any(BranchFilter.class)))
                .thenReturn(List.of(b1, b2));

        // Act & Assert
        mvc.perform(get("/api/v1/repos/ownerX/repoY/branches?protected=true&page=2")
                        .header("Authorization", "Bearer TOKEN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("main"))
                .andExpect(jsonPath("$[0].commit.sha").value("sha1"))
                .andExpect(jsonPath("$[0].protected").value(true))
                .andExpect(jsonPath("$[1].name").value("dev"))
                .andExpect(jsonPath("$[1].commit.url").value("url2"))
                .andExpect(jsonPath("$[1].protected").value(false));

        verify(service).listBranches(eq("Bearer TOKEN"), eq("ownerX"), eq("repoY"), any(BranchFilter.class));
    }

    @Test
    @DisplayName("GET /api/v1/repos/{owner}/{repo}/branches → 200 + JSON list with default filter values")
    void getBranches_defaultFilter_returnsList() throws Exception {
        // Arrange
        BranchDto.CommitRef ref = new BranchDto.CommitRef("shaX", "urlX");
        BranchDto b = new BranchDto("feature", ref, false);
        when(service.listBranches(eq("Bearer TOKEN"), eq("ownerA"), eq("repoB"), any(BranchFilter.class)))
                .thenReturn(List.of(b));

        // Act & Assert
        mvc.perform(get("/api/v1/repos/ownerA/repoB/branches")
                        .header("Authorization", "Bearer TOKEN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("feature"))
                .andExpect(jsonPath("$[0].protected").value(false));

        verify(service).listBranches(eq("Bearer TOKEN"), eq("ownerA"), eq("repoB"), any(BranchFilter.class));
    }

    @Test
    @DisplayName("GET /api/v1/repos/{owner}/{repo}/branches missing Authorization header → 400 Bad Request")
    void getBranches_missingAuthHeader_returnsBadRequest() throws Exception {
        mvc.perform(get("/api/v1/repos/ownerX/repoY/branches"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/repos/{owner}/{repo}/branches service throws Exception → 500 + ErrorDto")
    void getBranches_serviceThrowsInternalError_returnsErrorDto() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Unexpected failure"))
                .when(service).listBranches(any(), any(), any(), any());

        // Act & Assert
        mvc.perform(get("/api/v1/repos/ownerX/repoY/branches")
                        .header("Authorization", "Bearer TOKEN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }
}