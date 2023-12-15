package com.example.GraplerEnhancemet.controller;

import com.example.GraplerEnhancemet.custom_exception.DuplicateDataException;
import com.example.GraplerEnhancemet.custom_exception.LeafFolderException;
import com.example.GraplerEnhancemet.custom_exception.ParentNotFoundException;
import com.example.GraplerEnhancemet.entity.Folder;
import com.example.GraplerEnhancemet.service.FolderService;
import com.example.GraplerEnhancemet.util.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/projects")
@EnableTransactionManagement
@Validated
@CrossOrigin(origins = "http://localhost:3000")
public class FolderController {
    private static final Logger logger = LoggerFactory.getLogger(FolderController.class);

    @Autowired
    private FolderService folderService;

    @GetMapping("/{projectId}/folders")
    public ResponseEntity<ApiResponse<List<Folder>>> getAllFolders(@PathVariable Long projectId) {
        try {
            List<Folder> folders = folderService.getAllFolders(projectId);
            if (!folders.isEmpty()) {
                logger.info("All folders successfully retrieved for Project ID: {}", projectId);
                return ResponseEntity.ok(new ApiResponse<>(true, folders, "All folders retrieved successfully"));
            } else {
                logger.warn("No folders found for Project ID: {}", projectId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "No folders found  with Project ID : "+projectId));
            }
        } catch (ParentNotFoundException ex) {
            logger.error("Parent not found : {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, ex.getMessage()));
        } catch (Exception e) {
            logger.error("Error occurred while retrieving all folders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @GetMapping("/folders/{folderId}")
    public ResponseEntity<ApiResponse<Folder>> getFolder(@PathVariable Long folderId) {
        try {
            Folder folder = folderService.getFolder(folderId);
            if (folder != null) {
                logger.info("Folder retrieved successfully with ID: {}", folderId);
                return ResponseEntity.ok(new ApiResponse<>(true, folder, "Folder retrieved successfully"));
            } else {
                logger.warn("Folder not found with Folder ID: {}", folderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Folder not found with ID : "+folderId));
            }
        } catch (Exception e) {
            logger.error("Internal Server Error while retrieving folder with ID: " + folderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @PostMapping("/{projectId}/folders")
    public ResponseEntity<ApiResponse<Folder>> createFolder(@PathVariable Long projectId, @Valid @RequestBody Folder folder) {
        logger.info("logger folder {} " , folder);
        try {
            Folder createdFolder = folderService.createFolder(projectId, folder);
                logger.info("Folder created successfully with ID: {}", createdFolder.getId());
                return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, createdFolder, "Folder created successfully"));
        } catch (ParentNotFoundException ex) {
            logger.error("Parent not found : {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, ex.getMessage()));
        }  catch (UnsupportedOperationException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>(false, null, ex.getMessage()));
        }catch (Exception e) {
            logger.error("Internal Server Error while creating folder", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }
    @PostMapping("/folders/{parentFolderId}/sub-folders")
    public ResponseEntity<ApiResponse<Folder>> createSubFolder(@PathVariable Long parentFolderId, @Valid @RequestBody Folder folder) {
        try {
            Folder createdFolder = folderService.createSubFolder(parentFolderId, folder);
            logger.info("Folder created successfully with ID: {}", createdFolder.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, createdFolder, "Folder created successfully"));
        } catch (LeafFolderException ex) {
            logger.error("Parent is Leaf Type : {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>(false, null, ex.getMessage()));
        } catch (ParentNotFoundException ex) {
            logger.error("Parent not found : {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, ex.getMessage()));
        } catch (Exception e) {
            logger.error("Internal Server Error while creating folder", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }
//    for Creating Folder having projectId Compulsory
    //    @PostMapping("/{projectId}/folders")
//    public ResponseEntity<ApiResponse<Folder>> createFolder1(@PathVariable Long projectId, @RequestParam(value = "parentFolderId", defaultValue = "-1") Long parentFolderId, @Valid @RequestBody Folder folder) {
//        try {
//            Folder createdFolder = folderService.createFolder1(projectId, parentFolderId, folder);
//            logger.info("Folder created successfully with ID: {}", createdFolder.getId());
//            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, createdFolder, "Folder created successfully"));
//        } catch (ParentNotFoundException ex) {
//            logger.error("Parent not found : {}", ex.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, ex.getMessage()));
//        } catch (Exception e) {
//            logger.error("Internal Server Error while creating folder", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
//        }
//    }

    @PutMapping("/folders/{folderId}")
    public ResponseEntity<ApiResponse<Folder>> updateFolder( @PathVariable Long folderId,@Valid @RequestBody Folder folder) {
        try {
            Folder updatedFolder = folderService.updateFolder(folderId, folder);
            if (updatedFolder != null) {
                logger.info("Folder updated successfully with ID: {}", folderId);
                return ResponseEntity.ok(new ApiResponse<>(true, updatedFolder, "Folder updated successfully with ID : "+folderId));
            } else {
                logger.warn("Folder not found with Folder ID: {}", folderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Folder not found with ID : "+folderId));
            }
        }  catch (Exception e) {
            logger.error("Internal Server Error while updating folder with ID: " + folderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @DeleteMapping("/folders/{folderId}")
    public ResponseEntity<ApiResponse<?>> deleteFolder(@PathVariable Long folderId) {
        try {
            boolean deleted = folderService.deleteFolder(folderId);
            if (deleted) {
                logger.info("Folder deleted successfully with ID: {}", folderId);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Folder not found with Folder ID: {}", folderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Folder not found with ID : "+folderId));
            }
        } catch (Exception e) {
            logger.error("Internal Server Error while deleting folder with ID: " + folderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }
}
//    @DeleteMapping("{projectId}/folders/{folderId}")
//    public ResponseEntity<ApiResponse<?>> deleteFolder(@PathVariable Long projectId,@PathVariable Long folderId) {
//        try {
//            boolean deleted = folderService.deleteFolder(projectId,folderId);
//            if (deleted) {
//                logger.info("Folder deleted successfully with ID: {}", folderId);
//                return ResponseEntity.noContent().build();
//            } else {
//                logger.warn("Folder not found with Folder ID: {}", folderId);
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Folder not found with ID : "+folderId));
//            }
//        } catch (Exception e) {
//            logger.error("Internal Server Error while deleting folder with ID: " + folderId, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
//        }
//    }
//}

