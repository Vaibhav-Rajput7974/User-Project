package com.example.GraplerEnhancemet.service;

import com.example.GraplerEnhancemet.Repository.CompanyRepository;
import com.example.GraplerEnhancemet.Repository.ProjectRepository;
import com.example.GraplerEnhancemet.Repository.WorkspaceRepository;
import com.example.GraplerEnhancemet.dto.WorkspaceDTO;
import com.example.GraplerEnhancemet.entity.Company;
import com.example.GraplerEnhancemet.entity.Workspace;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkspaceService {
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceService.class);

    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ModelMapper modelMapper; // Autowire ModelMapper

    public List<WorkspaceDTO> getAllWorkspaces(Long companyId) {
        try {
            List<Workspace> workspaces = workspaceRepository.findByCompany_Id(companyId);
            logger.info("Retrieved all workspaces for company ID : {} successfully.",companyId);

            // Convert Workspace objects to WorkspaceDTO using ModelMapper
            return workspaces.stream()
                    .map(workspace -> modelMapper.map(workspace, WorkspaceDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error occurred while retrieving and returning workspaces for company " + companyId, e);
            return null;
        }
    }

    public WorkspaceDTO getWorkspace(Long companyId, Long workspaceId) {
        try {
            Workspace workspace = workspaceRepository.findByIdAndCompany_Id(workspaceId, companyId).orElse(null);
            if (workspace != null) {
            logger.info("Retrieved workspace ID : {} successfully.",workspaceId);
                return modelMapper.map(workspace, WorkspaceDTO.class);
            }
            logger.info("workspace not found on this ID " + workspaceId + " for company " + companyId);
            return null;
        } catch (Exception e) {
            logger.error("Error while getting workspace with ID " + workspaceId + " for company " + companyId, e);
            return null;
        }
    }

    public WorkspaceDTO createWorkspace(Long companyId, WorkspaceDTO workspaceDTO) {
        try {
            Company company = companyRepository.findById(companyId).orElse(null);
            if (company == null) {
                logger.error("Company Not Exists of id : " + companyId);
                throw new NullPointerException("Company Not Exists");
            }

            // Convert WorkspaceDTO to Workspace using ModelMapper
            Workspace workspace = modelMapper.map(workspaceDTO, Workspace.class);
            workspace.setCreationTime(LocalDateTime.now());
            workspace.setCompany(company);

            Workspace createdWorkspace = workspaceRepository.save(workspace);
            logger.info("Workspace created successfully for company {}: {}", companyId, createdWorkspace.getName());

            return modelMapper.map(createdWorkspace, WorkspaceDTO.class);
        }catch (NullPointerException e) {
            logger.error("Invalid Company ID : {}" , companyId);
            return null;
        } catch (Exception e) {
            logger.error("Error while creating workspace for company " + companyId, e);
            return null;
        }
    }

    public WorkspaceDTO updateWorkspace(Long companyId, Long workspaceId, WorkspaceDTO updatedWorkspaceDTO) {
        try {
            Workspace existingWorkspace = getWorkspaceDB(companyId, workspaceId);
            if (existingWorkspace != null) {
                // Convert WorkspaceDTO to Workspace using ModelMapper
                Workspace updatedWorkspace = modelMapper.map(updatedWorkspaceDTO, Workspace.class);
                updatedWorkspace.setId(existingWorkspace.getId());
                updatedWorkspace.setCompany(existingWorkspace.getCompany());
                updatedWorkspace.setCreationTime(existingWorkspace.getCreationTime());

                Workspace savedWorkspace = workspaceRepository.save(updatedWorkspace);
                logger.info("Workspace updated successfully for company {}: {}", companyId, savedWorkspace.getName());

                return modelMapper.map(savedWorkspace, WorkspaceDTO.class);
            }else {
                logger.error("Workspace not found with ID: {} Company ID: {}" , workspaceId,companyId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error while updating workspace with ID :{}  for company ID : {}" ,workspaceId, companyId, e);
            return null;
        }
    }

    public boolean deleteWorkspace(Long companyId, Long workspaceId) {
        try {
            Workspace existingWorkspace = getWorkspaceDB(companyId, workspaceId);
            if (existingWorkspace != null) {
                Company company = existingWorkspace.getCompany();

                // Check if the workspace being deleted is the default workspace.
                boolean isDefaultWorkspace = existingWorkspace.getName().equals("Default");

                // Remove the workspace from the company's list of workspaces.
                company.getWorkspaces().remove(existingWorkspace);
                projectRepository.deleteByWorkspace_Id(workspaceId);

                // Delete the workspace.
                workspaceRepository.delete(existingWorkspace);

                logger.info((isDefaultWorkspace ? "Default Workspace" : "Workspace") + " deleted successfully with ID {} for company {}", workspaceId, companyId);

                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Error while deleting workspace with ID " + workspaceId + " for company " + companyId, e);
            return false;
        }
    }
    public Workspace getWorkspaceDB(Long companyId, Long workspaceId) {
        try {
            return workspaceRepository.findByIdAndCompany_Id(workspaceId, companyId).orElse(null);
        } catch (Exception e) {
            logger.error("Error while getting workspace with ID " + workspaceId + " for company " + companyId, e);
            return null;
        }
    }
}


/*@Service
@Transactional
public class WorkspaceService {
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceService.class);

    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    ProjectRepository projectRepository;

    public List<Workspace> getAllWorkspaces(Long companyId) {
        try {
            List<Workspace> workspaces = workspaceRepository.findByCompany_Id(companyId);
            logger.info("Retrieved all workspaces for company {} successfully.", companyId);
            return workspaces;
        } catch (Exception e) {
            logger.error("Error occurred while retrieving workspaces for company " + companyId, e);
            return null;
        }
    }

    public Workspace getWorkspace(Long companyId, Long workspaceId) {
        try {
            logger.info("Retrieved workspace successfully.");
            return workspaceRepository.findByIdAndCompany_Id(workspaceId, companyId).orElse(null);
        } catch (Exception e) {
            logger.error("Error while getting workspace with ID " + workspaceId + " for company " + companyId, e);
            return null;
        }
    }

    public Workspace createWorkspace(Long companyId, Workspace workspace) {
        try {
            Company company = companyRepository.findById(companyId).orElse(null);
            if(company == null) {
                logger.error("Company Not Exists of id : " + companyId);
                throw new NullPointerException("Company Not Exists");
            }
            workspace.setCreationTime(LocalDateTime.now());
            workspace.setCompany(company);
            Workspace createdWorkspace = workspaceRepository.save(workspace);
            logger.info("Workspace created successfully for company {}: {}", companyId, createdWorkspace.getName());
            return createdWorkspace;
        } catch (Exception e) {
            logger.error("Error while creating workspace for company " + companyId, e);
            return null;
        }
    }

    public Workspace updateWorkspace(Long companyId, Long workspaceId, Workspace updatedWorkspace) {
        try {
            Workspace existingWorkspace = getWorkspace(companyId, workspaceId);
            if (existingWorkspace != null) {
                updatedWorkspace.setId(existingWorkspace.getId());
                updatedWorkspace.setCompany(existingWorkspace.getCompany());
                updatedWorkspace.setCreationTime(existingWorkspace.getCreationTime());
                Workspace savedWorkspace = workspaceRepository.save(updatedWorkspace);
                logger.info("Workspace updated successfully for company {}: {}", companyId, savedWorkspace.getName());
                return savedWorkspace;
            }
            return null;
        } catch (Exception e) {
            logger.error("Error while updating workspace with ID " + workspaceId + " for company " + companyId, e);
            return null;
        }
    }

    public boolean deleteWorkspace(Long companyId, Long workspaceId) {
        try {
            Workspace existingWorkspace = getWorkspace(companyId, workspaceId);
            if (existingWorkspace != null) {
                Company company = existingWorkspace.getCompany();

                // Check if the workspace being deleted is the default workspace.
                boolean isDefaultWorkspace = existingWorkspace.getName().equals("Default");

                // Remove the workspace from the company's list of workspaces.
                company.getWorkspaces().remove(existingWorkspace);
                projectRepository.deleteByWorkspace_Id(workspaceId);
                // Delete the workspace.
                workspaceRepository.delete(existingWorkspace);

                logger.info(isDefaultWorkspace ? "Default Workspace" : "Workspace" + " deleted successfully with ID {} for company {}", workspaceId, companyId);

                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Error while deleting workspace with ID " + workspaceId + " for company " + companyId, e);
            return false;
        }
    }

}
*/
