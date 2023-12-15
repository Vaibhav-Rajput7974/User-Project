package com.example.GraplerEnhancemet.service;


import com.example.GraplerEnhancemet.Repository.FolderRepository;
import com.example.GraplerEnhancemet.Repository.ProjectRepository;
import com.example.GraplerEnhancemet.custom_exception.LeafFolderException;
import com.example.GraplerEnhancemet.custom_exception.ParentNotFoundException;
import com.example.GraplerEnhancemet.entity.Company;
import com.example.GraplerEnhancemet.entity.Folder;
import com.example.GraplerEnhancemet.entity.Project;
import com.example.GraplerEnhancemet.entity.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FolderService {
    private static final Logger logger = LoggerFactory.getLogger(FolderService.class);

    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    private FolderRepository folderRepository;

    public List<Folder> getAllFolders(Long projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent()) {
            List<Folder> folders = folderRepository.findAllByParentProject_Id(projectId);
                logger.info("Retrieved all folders successfully with Project ID : {}",projectId);
                return folders;
        } else {
            logger.error("Parent Project Not Found with ID: {}", projectId);
            throw new ParentNotFoundException("Folders Not Found with Project ID : "+projectId);
        }
    }

    public Folder getFolder(Long folderId) {
        Optional<Folder> folder = folderRepository.findById(folderId);
        if (folder.isPresent()) {
            logger.info("Retrieved folder successfully.");
            return folder.get();
        } else {
            logger.warn("Folder not found with Folder ID: {}", folderId);
            return null;
        }
    }

    public Folder createFolder(Long projectId, Folder folder) {

        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            if(!isCompanyStructureTypeHierarchical(project))
            {
               throw new UnsupportedOperationException("Unable to create Folder Company Structure Type is not Hierarchical");
            }
            folder.setParentProject(project);
            Folder createdFolder = folderRepository.save(folder);
            logger.info("Folder created successfully with ID: {}", createdFolder.getId());
            return createdFolder;
        } else {
            logger.error("Parent Project Not Found with ID: {}", projectId);
            throw new ParentNotFoundException("Parent Project Not Found with ID: " + projectId);
        }
    }

    public Folder createSubFolder(Long parentFolderId, Folder folder) {
        Optional<Folder> parentFolderOptional = folderRepository.findById(parentFolderId);
        if (parentFolderOptional.isPresent()) {
            Folder parentFolder = parentFolderOptional.get();
            String parentFolderType = String.valueOf(parentFolder.getFolderType());
            if(parentFolderType.equals("NONLEAF")) {
                folder.setParentFolder(parentFolder);
                Folder createdFolder = folderRepository.save(folder);
                logger.info("Folder created successfully with ID: {}", createdFolder.getId());
                return createdFolder;
            } else {
                throw new LeafFolderException("Folder can't be created, Parent Folder is Leaf with ID: " + parentFolderId);
            }
        } else {
            logger.warn("Parent Folder Not Found with ID: {}", parentFolderId);
            throw new ParentNotFoundException("Parent Folder Not Found with ID: " + parentFolderId);
        }
    }

    /* public Folder createFolder1(Long projectId, Long parentFolderId, Folder folder) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            folder.setParentProject(project);
            if (parentFolderId != -1) {
                Optional<Folder> parentFolderOptional = folderRepository.findById(parentFolderId);
                if (parentFolderOptional.isPresent()) {
                    Folder parentFolder = parentFolderOptional.get();
                    logger.info("Parent Folder added successfully with ID: {}", parentFolderId);
                    folder.setParentfolder(parentFolder);
                } else {
                    logger.warn("Parent Folder Not Found with ID: {}", parentFolderId);
                    throw new ParentNotFoundException("Parent Folder Not Found with ID: " + parentFolderId);
                }
            }
                Folder createdFolder = folderRepository.save(folder);
                logger.info("Folder created successfully with ID: {}", createdFolder.getId());
                return createdFolder;
        } else {
            logger.error("Parent Project Not Found with ID: {}", projectId);
            throw new ParentNotFoundException("Parent Project Not Found with ID: " + projectId);
        }
    }
*/
    public Folder updateFolder(Long folderId, Folder updatedFolder) {
        Optional<Folder> folder = folderRepository.findById(folderId);
        if (folder.isPresent()) {
            Folder existingFolder = folder.get();
            if (updatedFolder.getName() != null) {
                existingFolder.setName(updatedFolder.getName());
            }
            if (updatedFolder.getFolderType() != null) {
                existingFolder.setFolderType(updatedFolder.getFolderType());
            }
            Folder savedFolder = folderRepository.save(existingFolder);
            logger.info("Folder updated successfully with Folder ID: {}", folderId);
            return savedFolder;
        } else {
            logger.warn("Folder not found with Folder ID: {}", folderId);
            return null;
        }
    }
//Point
//Spring Boot doesn't provide specific properties for cascading deletes in self-referencing entities
    public boolean deleteFolder(Long folderId) {
        Optional<Folder> folder = folderRepository.findById(folderId);
        if (folder.isPresent()) {
            // Call a recursive method to delete the folder and its subfolders
            logger.info("Folder is ready to delete with Folder ID: {}", folderId);
            deleteFolderAndSubfoldersRecursive(folder.get());
            return true;
        } else {
            // Handle case where folder with folderId was not found
            return false;
        }
    }

    private void deleteFolderAndSubfoldersRecursive(Folder folder) {
        // Check if the folder has subfolders
        List<Folder> subfolders = folder.getSubFolders();

        if (subfolders != null && !subfolders.isEmpty()) {
            // Recursively delete each subfolder and its subfolders
            subfolders.forEach(this::deleteFolderAndSubfoldersRecursive);

            // Delete the subfolders first
            subfolders.stream()
                    .map(Folder::getId)
                    .forEach(subfolderId -> {
                        folderRepository.deleteFolderById(subfolderId);
                        logger.info("Subfolder is deleted with ID: {}", subfolderId);
                    });
        }

        // Finally, delete the folder itself
        folderRepository.deleteFolderById(folder.getId());
        logger.info("Folder is deleted with ID: {}", folder.getId());
    }

public boolean isCompanyStructureTypeHierarchical(Project project)
{
    Workspace workspace = project.getWorkspace();
    Company company = workspace.getCompany();
    String companyStructureType = String.valueOf(company.getStructureType());
    if(companyStructureType.equals("HIERARCHICAL")) {
        return true;
    }
    else
        return false;
}
//public boolean deleteFolder(Long projectId,Long folderId) {
//    Optional<Folder> optionalFolder = folderRepository.findById(folderId);
//    if (optionalFolder.isPresent()) {
//        Folder folder = optionalFolder.get();
//        folderRepository.deleteByParentFolderId(folderId);
//        logger.info("Sub Folder deleted successfully with Parent Folder ID: {}", folderId);
//
//        folderRepository.deleteByParentProjectIdAndParentFolderId(projectId,folderId);
//        logger.info("Folder deleted successfully with ID: {}", folderId);
//        return true;
//    } else {
//        logger.warn("Folder not found with Folder ID: {}", folderId);
//        return false;
//    }
//}
}



/*
@Service
@Transactional
public class FolderService {
    private static final Logger logger = LoggerFactory.getLogger(FolderService.class);

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Folder> getAllFolders(Long projectId) {
        try {
            Optional<Project> project = projectRepository.findById(projectId);
            if (project.isPresent()) {
                List<Folder> folders = folderRepository.findAllByParentProject_Id(projectId);
                if(folders != null && !folders.isEmpty()){
                logger.info("Retrieved all folders successfully with Project ID : {}",projectId);
                    return folders;
                } else{
                    logger.error("Folders not Available having Project ID: " + projectId);
                    return null;
                }
                } else {
                throw new ParentNotFoundException("Folders Not Found with Project ID : "+projectId);
            }
        } catch (ParentNotFoundException e) {
            logger.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error occurred while retrieving folders", e);
            throw e;
        }
    }

    public Folder getFolder(Long projectId, Long folderId) {
        try {
            Optional<Folder> folder = folderRepository.findById(folderId);
            if (folder.isPresent() && folder.get().getParentProject().getId().equals(projectId)) {
                logger.info("Retrieved folder successfully.");
                return folder.get();
            } else {
                logger.error("Folder not found with Project ID: {} and Folder ID: {}",projectId, folderId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error while getting folder with ID: " + folderId, e);
            throw e;
        }
    }

    public Folder createFolder(Long projectId, Long parentFolderId, Folder folder) {
        try {
            Optional<Project> projectOptional = projectRepository.findById(projectId);
            if (projectOptional.isPresent()) {
                Project project = projectOptional.get();
                folder.setParentProject(project);
                if (parentFolderId != -1) {
                    Optional<Folder> parentFolderOptional = folderRepository.findById(parentFolderId);
                    if (parentFolderOptional.isPresent()) {
                        Folder parentFolder = parentFolderOptional.get();
                        folder.setParentfolder(parentFolder);
                    } else {
                        throw new ParentNotFoundException("Parent Folder Not Found with ID : "+parentFolderId);
                    }
                }
                return folderRepository.save(folder);
            } else {
                throw new ParentNotFoundException("Parent Project Not Found with ID : "+projectId);
            }
        } catch (ParentNotFoundException e) {
            logger.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating folder", e);
            throw e;
        }
    }

        public Folder updateFolder(Long projectId, Long folderId, Folder updatedFolder) {
        try {
            Optional<Folder> folder = folderRepository.findById(folderId);
            if (folder.isPresent() && folder.get().getParentProject().getId().equals(projectId)) {
                Folder existingFolder = folder.get();
                if(updatedFolder.getName()!= null)
                {
                    existingFolder.setName(updatedFolder.getName());
                }
                if(updatedFolder.getFolderType()!=null)
                {
                    existingFolder.setFolderType(updatedFolder.getFolderType());
                }
                Folder savedFolder = folderRepository.save(existingFolder);
                logger.info("Folder updated successfully with Project ID : {} and Folder ID : {}", projectId,folderId);
                return savedFolder;
            } else {
                throw new ParentNotFoundException("Parent Project Not Found with ID : "+projectId);
            }
        } catch (ParentNotFoundException e) {
            logger.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating folder with Project ID: " + projectId+"and Folder ID: "+folderId, e);
            throw e;
        }
    }

    public boolean deleteFolder(Long projectId, Long folderId) {
        try {
            Optional<Folder> folder = folderRepository.findById(folderId);
            if (folder.isPresent() && folder.get().getParentProject().getId().equals(projectId)) {
                folderRepository.delete(folder.get());
                logger.info("Folder deleted successfully with ID: {}", folderId);
                return true;
            } else {
                logger.error("Folder not found with Project ID: " + projectId+ "and Folder ID:" +folderId);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error while deleting folder with ID: " + folderId, e);
            throw e;
        }
    }
}
*/
