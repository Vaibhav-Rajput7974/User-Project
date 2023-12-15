package com.example.GraplerEnhancemet.controller;

import com.example.GraplerEnhancemet.custom_exception.DuplicateDataException;
import com.example.GraplerEnhancemet.custom_exception.NotFoundException;
import com.example.GraplerEnhancemet.dto.CompanyDTO;
import com.example.GraplerEnhancemet.dto.UserDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.GraplerEnhancemet.entity.User;
import com.example.GraplerEnhancemet.service.UserService;
import com.example.GraplerEnhancemet.util.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@EnableTransactionManagement
@Validated
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/users")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@GetMapping
	public ResponseEntity<ApiResponse<List<?>>> getAllUsers() {
		try {
			List<UserDTO> users = userService.getAllUsers();
			if (!users.isEmpty()) {
				logger.info("All users successfully retrieved");
				return ResponseEntity.ok(new ApiResponse<>(true, users, "All users retrieved successfully"));
			} else {
				logger.warn("No users available.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "No users available"));
			}
		} catch (Exception e) {
			logger.error("Error occurred while retrieving all users", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
		}
	}

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<?>> getUser(@PathVariable Long userId) {
		try {
			UserDTO user = userService.getUser(userId);
			if (user != null) {
				logger.info("User retrieved successfully with ID: {}", userId);
				return ResponseEntity.ok(new ApiResponse<>(true, user, "User retrieved successfully with ID: " + userId));
			} else {
				logger.warn("User not found with ID: {}", userId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "User not found with ID: " + userId));
			}
		}  catch (Exception e) {
			logger.error("Internal Server Error while retrieving user with ID: " + userId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
		}
	}

	@PostMapping
	public ResponseEntity<ApiResponse<?>> createUser(@Valid @RequestBody User user) {
		try {
			UserDTO createdUser = userService.createUser(user);
			if (createdUser != null) {
				logger.info("User created successfully with ID: {}", createdUser.getId());
				return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, createdUser, "User created successfully with ID: " + createdUser.getId()));
			} else {
				logger.warn("Failed to create user.");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Failed to create user"));
			}
		} catch (DuplicateDataException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(false, null, ex.getMessage()));
		} catch (Exception e) {
			logger.error("Internal Server Error while creating a user", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
		}
	}

	@PutMapping("/{userId}")
	public ResponseEntity<ApiResponse<?>> updateUser(@PathVariable Long userId, @Valid @RequestBody User user) {
		try {
			UserDTO updatedUser = userService.updateUser(userId, user);
			if (updatedUser != null) {
				logger.info("User updated successfully with ID: {}", userId);
				return ResponseEntity.ok(new ApiResponse<>(true, updatedUser, "User updated successfully with ID: " + userId));
			} else {
				logger.warn("User not found with ID: {}", userId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "User not found with ID: " + userId));
			}
		} catch (DuplicateDataException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(false, null, ex.getMessage()));
		} catch (NotFoundException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, ex.getMessage()));
		} catch (Exception e) {
			logger.error("Internal Server Error while updating user with ID: " + userId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
		}
	}
	@PutMapping("/addImage/{id}")
	public ResponseEntity<ApiResponse<?>> addLogo(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
		try {
			if (file != null && !file.isEmpty()) {
				UserDTO userProfileAddedDTO = userService.AddProfile(file, id);

				if (userProfileAddedDTO != null) {
					return ResponseEntity.ok(new ApiResponse<>(true, userProfileAddedDTO, "User profile updated successfully"));
				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Error in adding profile"));
				}
			} else {
				// Handle the case where the input file is null or empty
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, "Invalid file provided"));
			}
		}  catch (Exception e) {
			// Handle general exceptions with a generic message
			logger.error("Internal Server Error while adding User profile", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
		}
	}
	@DeleteMapping("/{userId}")
	public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable Long userId) {
		try {
			boolean deleted = userService.deleteUser(userId);
			if (deleted) {
				logger.info("User deleted successfully with ID: {}", userId);
				return ResponseEntity.noContent().build();
			} else {
				logger.warn("User not found with ID: {}", userId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "User not found with ID: " + userId));
			}
		} catch (Exception e) {
			logger.error("Internal Server Error while deleting user with ID: " + userId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
		}
	}
}
