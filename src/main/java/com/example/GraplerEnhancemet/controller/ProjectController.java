package com.example.GraplerEnhancemet.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.GraplerEnhancemet.entity.Project;
import com.example.GraplerEnhancemet.service.ProjectService;
import com.example.GraplerEnhancemet.util.ApiResponse;

import java.util.List;


@RestController
@EnableTransactionManagement
@Validated
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/workspaces/{workspaceId}/projects")


public class ProjectController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<?>>> getAllProjects(@PathVariable Long workspaceId) {
        try {
            List<Project> projects = projectService.getAllProjects(workspaceId);
            if(projects != null && !projects.isEmpty()) {
                logger.info("All projects successfully retrieved");
            return ResponseEntity.ok(new ApiResponse<>(true, projects, "All projects retrieved successfully"));
            } else{
                logger.warn("No projects Available on this Workspace ID : {}",workspaceId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "No projects Available on this Workspace ID : "+workspaceId));
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving all projects", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
        }
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Project>> getProject(@PathVariable Long workspaceId, @PathVariable Long projectId) {
        try {
            Project project = projectService.getProject(workspaceId, projectId);
            if (project != null) {
                logger.info("Project retrieved successfully having ID : {}", projectId);
                return ResponseEntity.ok(new ApiResponse<>(true, project, "Project retrieved successfully having ID : "+projectId));
            } else {
                logger.warn("Project not found with WorkSpace ID : {} and  Project ID : {}",workspaceId, projectId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Project not found with WorkSpace ID : "+workspaceId+" and  Project ID : "+projectId));
            }
        } catch (Exception e) {
            logger.error("Internal Server Error while retrieving project with ID: " + projectId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Project>> createProject(@PathVariable Long workspaceId,@Valid @RequestBody Project project) {
        try {
            Project createdProject = projectService.createProject(workspaceId, project);
            logger.info("Project created successfully having ID : {}", createdProject.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, createdProject, "Project created successfully having ID : "+createdProject.getId()));
        } catch (Exception e) {
            logger.error("Internal Server Error while creating a project", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
        }
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Project>> updateProject(@PathVariable Long workspaceId, @PathVariable Long projectId,@Valid @RequestBody Project project) {
        try {
            Project updatedProject = projectService.updateProject(workspaceId, projectId, project);
            if (updatedProject != null) {
                logger.info("Project updated successfully having ID : {}", projectId);
                return ResponseEntity.ok(new ApiResponse<>(true, updatedProject, "Project updated successfully having ID : "+projectId));
            } else {
                logger.warn("Project not found with ID: {}", projectId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Project not found with ID : "+projectId));
            }
        } catch (Exception e) {
            logger.error("Internal Server Error while updating project with ID: " + projectId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
        }
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<String>> deleteProject(@PathVariable Long workspaceId, @PathVariable Long projectId) {
        try {
            boolean deleted = projectService.deleteProject(workspaceId, projectId);
            if (deleted) {
                logger.info("Project deleted successfully with ID: {}", projectId);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Project not found with ID: {}", projectId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Project not found with ID : "+projectId));
            }
        } catch (Exception e) {
            logger.error("Internal Server Error while deleting project with ID: " + projectId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
        }
    }
}


/*
@RestController
@RequestMapping("/workspaces/{workspaceId}/projects")
@Validated
public class ProjectController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getAllProjects(@PathVariable Long workspaceId) {
        try {
            List<ProjectDTO> projects = projectService.getAllProjects(workspaceId);
            if(projects != null && projects.isEmpty())
            {
                logger.info("No projects available for workspace {} ", workspaceId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "No projects found"));
            }
            else if (projects != null) {
                logger.info("All projects for workspace {} retrieved successfully", workspaceId);
                return ResponseEntity.ok(new ApiResponse<>(true, projects, "All projects retrieved successfully"));
            } else {
                logger.warn("No projects found for workspace: {}", workspaceId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "No projects found"));
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving all projects for workspace " + workspaceId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDTO>> getProject(@PathVariable Long workspaceId, @PathVariable Long projectId) {
        try {
            ProjectDTO project = projectService.getProject(workspaceId, projectId);
            if (project != null) {
                logger.info("Project retrieved successfully: {}", project.getName());
                return ResponseEntity.ok(new ApiResponse<>(true, project, "Project retrieved successfully"));
            } else {
                logger.warn("Project not found with ID: {}", projectId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Project not found"));
            }
        } catch (Exception e) {
            logger.error("Internal Server Error while retrieving project with ID: " + projectId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectDTO>> createProject(@PathVariable Long workspaceId, @Valid @RequestBody ProjectDTO projectDTO) {
        try {
            ProjectDTO createdProject = projectService.createProject(workspaceId, projectDTO);
            if (createdProject != null) {
                logger.info("Project created successfully: {}", createdProject.getName());
                return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, createdProject, "Project created successfully"));
            } else {
                logger.error("Project not created due to Workspace not found with ID : {}", workspaceId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Project not created due to Workspace not found with ID : " + workspaceId));
            }
        } catch (Exception e) {
            logger.error("Internal Server Error while creating project", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProject(@PathVariable Long workspaceId, @PathVariable Long projectId, @Valid @RequestBody ProjectDTO projectDTO) {
        try {
            ProjectDTO updatedProject = projectService.updateProject(workspaceId, projectId, projectDTO);
            if (updatedProject != null) {
                logger.info("Project updated successfully: {}", updatedProject.getName());
                return ResponseEntity.ok(new ApiResponse<>(true, updatedProject, "Project updated successfully"));
            } else {
                logger.warn("Project not found with ID: {}", projectId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Project not found"));
            }
        } catch (Exception e) {
            logger.error("Internal Server Error while updating project with ID: " + projectId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<?>> deleteProject(@PathVariable Long workspaceId, @PathVariable Long projectId) {
        try {
            boolean deleted = projectService.deleteProject(workspaceId, projectId);
            if (deleted) {
                logger.info("Project deleted successfully with ID: {}", projectId);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Project not found with ID: {}", projectId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Project not found"));
            }
        } catch (Exception e) {
            logger.error("Internal Server Error while deleting project with ID: " + projectId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }
}
*/
