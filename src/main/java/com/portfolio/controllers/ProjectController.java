package com.portfolio.controllers;

import com.portfolio.dtos.ProjectRequest;
import com.portfolio.dtos.ProjectResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @PostMapping("")
    public ResponseEntity<ResponseModel<ProjectResponse>> createProject(@RequestBody ProjectRequest projectRequest) throws GenericException {
        return projectService.createProject(projectRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<ProjectResponse>> getProjectById(@PathVariable Integer id) throws GenericException {
        return projectService.getProjectById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ProjectResponse>> updateProject(@PathVariable Integer id,@RequestBody ProjectRequest projectRequest) throws GenericException {
        return projectService.updateProjectById(id, projectRequest);
    }

    @GetMapping("")
    public ResponseEntity<ResponseModel<List<ProjectResponse>>> getAllProjects() throws GenericException {
        return projectService.getAllProjects();
    }
}
