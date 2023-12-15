package com.example.GraplerEnhancemet.service;

import com.example.GraplerEnhancemet.Repository.ProjectRepository;
import com.example.GraplerEnhancemet.Repository.WorkspaceRepository;
import com.example.GraplerEnhancemet.entity.Project;
import com.example.GraplerEnhancemet.entity.Workspace;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
public class ProjectService {
    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private ModelMapper modelMapper;

        public List<Project> getAllProjects(Long workspaceId) {
        try {
            List<Project> projects = projectRepository.findAllByWorkspace_Id(workspaceId);
            logger.info("Retrieved all projects successfully.");
            return projects;
            } catch (Exception e) {
            logger.error("Error occurred while retrieving all projects", e);
            return null;
        }
    }
        public Project getProject(Long workspaceId, Long projectId) {
        try {
            Project project = projectRepository.findByWorkspace_IdAndId(workspaceId, projectId);
            if (project != null) {
                logger.info("Retrieved project successfully.");
                  return project;
            } else {
                logger.error("Project not found with ID: " + projectId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error while getting project with ID: " + projectId, e);
            return null;
        }
    }
    public Project createProject(Long workspaceId, Project project) {
        try {
            // Check if the workspace exists
            Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);
            if (workspace == null) {
                logger.error("Workspace not found with ID: " + workspaceId);
                return null;
            }
            project.setWorkspace(workspace);
            project.setCreationTime(LocalDateTime.now());

            Project createdProject = projectRepository.save(project);
            logger.info("Project created successfully");
              return createdProject;
        } catch (Exception e) {
            logger.error("Error while creating project", e);
            return null;
        }
    }


        public Project updateProject(Long workspaceId, Long projectId, Project updatedProject) {
        try {
            Project existingProject = projectRepository.findByWorkspace_IdAndId(workspaceId, projectId);
            if (existingProject != null) {
                if (updatedProject.getName() != null) {
                    existingProject.setName(updatedProject.getName());
                }
                if (updatedProject.getType() != null) {
                    existingProject.setType(updatedProject.getType());
                }
                if (updatedProject.getSubType() != null) {
                    existingProject.setSubType(updatedProject.getSubType());
                }
                Project savedProject = projectRepository.save(existingProject);
                logger.info("Project updated successfully");
                return savedProject;
            } else {
                logger.error("Project not found with ID: " + projectId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error while updating project with ID: " + projectId, e);
            return null;
        }
    }

    public boolean deleteProject(Long workspaceId, Long projectId) {
        try {
            Project existingProject = projectRepository.findByWorkspace_IdAndId(workspaceId, projectId);
            if (existingProject != null) {
                projectRepository.delete(existingProject);
                logger.info("Project deleted successfully with ID: {}", projectId);
                return true;
            } else {
                logger.error("Project not found with ID: " + projectId);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error while deleting project with ID: " + projectId, e);
            return false;
        }
    }
}


/*
@Service
@Transactional
public class ProjectService {
    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private ModelMapper modelMapper; // Autowire ModelMapper

    public List<ProjectDTO> getAllProjects(Long workspaceId) {
        try {
            List<Project> projects = projectRepository.findAllByWorkspace_Id(workspaceId);
            logger.info("Retrieved all projects successfully.");
            // Convert Project objects to ProjectDTO using ModelMapper
            return projects.stream()
                    .map(project -> modelMapper.map(project, ProjectDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error occurred while retrieving all projects", e);
            return null;
        }
    }

    public ProjectDTO getProject(Long workspaceId, Long projectId) {
        try {
            Project project = projectRepository.findByWorkspace_IdAndId(workspaceId, projectId);
            if (project != null) {
                ProjectDTO projectDTO = modelMapper.map(project, ProjectDTO.class);
                logger.info("Retrieved project successfully.");
                return projectDTO;
            } else {
                logger.error("Project not found with ID: " + projectId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error while getting project with ID: " + projectId, e);
            return null;
        }
    }

    public ProjectDTO createProject(Long workspaceId, ProjectDTO projectDTO) {
        try {
            // Check if the workspace exists
            Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);
            if (workspace == null) {
                logger.error("Workspace not found with ID: " + workspaceId);
                return null;
            }

            Project project = modelMapper.map(projectDTO, Project.class);
            project.setWorkspace(workspace);
            project.setCreationTime(LocalDateTime.now());

            Project createdProject = projectRepository.save(project);
            ProjectDTO createdProjectDTO = modelMapper.map(createdProject, ProjectDTO.class);
            logger.info("Project created successfully: {}", createdProjectDTO.getName());
            return createdProjectDTO;
        } catch (Exception e) {
            logger.error("Error while creating project", e);
            return null;
        }
    }


    public ProjectDTO updateProject(Long workspaceId, Long projectId, ProjectDTO updatedProjectDTO) {
        try {
            Project existingProject = projectRepository.findByWorkspace_IdAndId(workspaceId, projectId);
            if (existingProject != null) {
                if (updatedProjectDTO.getName() != null) {
                    existingProject.setName(updatedProjectDTO.getName());
                }
                if (updatedProjectDTO.getType() != null) {
                    existingProject.setType(updatedProjectDTO.getType());
                }
                if (updatedProjectDTO.getSubType() != null) {
                    existingProject.setSubType(updatedProjectDTO.getSubType());
                }
                Project savedProject = projectRepository.save(existingProject);

                ProjectDTO savedProjectDTO = modelMapper.map(savedProject, ProjectDTO.class);
                logger.info("Project updated successfully: {}", savedProjectDTO.getName());
                return savedProjectDTO;
            } else {
                logger.error("Project not found with ID: " + projectId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error while updating project with ID: " + projectId, e);
            return null;
        }
    }

    public boolean deleteProject(Long workspaceId, Long projectId) {
        try {
            Project existingProject = projectRepository.findByWorkspace_IdAndId(workspaceId, projectId);
            if (existingProject != null) {
                projectRepository.delete(existingProject);
                logger.info("Project deleted successfully with ID: {}", projectId);
                return true;
            } else {
                logger.error("Project not found with ID: " + projectId);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error while deleting project with ID: " + projectId, e);
            return false;
        }
    }
}
*/


