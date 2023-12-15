package com.example.GraplerEnhancemet.controller;

import com.example.GraplerEnhancemet.custom_exception.DuplicateCompanyException;
import com.example.GraplerEnhancemet.entity.Company;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.GraplerEnhancemet.service.CompanyService;
import com.example.GraplerEnhancemet.util.ApiResponse;

import java.util.List;
import com.example.GraplerEnhancemet.dto.CompanyDTO;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/companies")
@EnableTransactionManagement
@Validated
@CrossOrigin(origins = "http://localhost:3000")
public class CompanyController {
	private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);

	@Autowired
	private CompanyService companyService;

	@GetMapping
	public ResponseEntity<ApiResponse<List<CompanyDTO>>> getAllCompanies() {
		try {
			List<CompanyDTO> companies = companyService.getAllCompanies();
			if (companies != null && !companies.isEmpty()) {
				logger.info("All companies successfully retrieved");
				return ResponseEntity.ok(new ApiResponse<>(true, companies, "All companies retrieved successfully"));
			}
			else {
				logger.error("No companies found");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "No companies found"));
			}
		} catch (Exception e) {
			logger.error("Error occurred while retrieving all companies", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<CompanyDTO>> getCompany(@PathVariable Long id) {
		try {
			CompanyDTO company = companyService.getCompany(id);
			if (company != null) {
				logger.info("Company retrieved successfully: {}", company.getName());
				return ResponseEntity.ok(new ApiResponse<>(true, company, "Company retrieved successfully"));
			} else {
				logger.warn("Company not found with ID: {}", id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Company not found"));
			}
		} catch (Exception e) {
			logger.error("Internal Server Error while retrieving company with ID: " + id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
		}
	}

	@PostMapping
	public ResponseEntity<ApiResponse<CompanyDTO>> createCompany(@Valid @RequestBody Company company) {
		try {
			CompanyDTO createdCompany = companyService.createCompany(company);
			if (createdCompany != null) {
				logger.info("Company created successfully: {}", createdCompany.getName());
				return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, createdCompany, "Company created successfully"));
			} else {
				logger.error("Internal Server Error while creating company");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
			}
		} catch (DuplicateCompanyException ex) {
			logger.error("Duplicate company creation attempt: {}", ex.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(false, null, ex.getMessage()));
		} catch (Exception e) {
			logger.error("Internal Server Error while creating company", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<CompanyDTO>> updateCompany(@PathVariable Long id,@Valid @RequestBody Company company) {
		try {
			CompanyDTO updatedCompany = companyService.updateCompany(id, company);
			if (updatedCompany != null) {
				logger.info("Company updated successfully: {}", updatedCompany.getName());
				return ResponseEntity.ok(new ApiResponse<>(true, updatedCompany, "Company updated successfully"));
			} else {
				logger.warn("Company not found with ID: {}", id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Company not found"));
			}
		} catch (DuplicateCompanyException ex) {
			logger.error("Duplicate company creation attempt: {}", ex.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(false, null, ex.getMessage()));
		} catch (Exception e) {
			logger.error("Internal Server Error while updating company with ID: " + id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
		}
	}

	@PutMapping("/addImage/{id}")
	public ResponseEntity<ApiResponse<?>> addLogo(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
		try {
			if (file != null && !file.isEmpty()) {
				CompanyDTO companyLogoAddedDTO = companyService.AddLogo(file, id);

				if (companyLogoAddedDTO != null) {
					return ResponseEntity.ok(new ApiResponse<>(true, companyLogoAddedDTO, "Company Logo updated successfully"));
				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Error in adding Logo"));
				}
			} else {
				// Handle the case where the input file is null or empty
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, "Invalid file provided"));
			}
		}  catch (Exception e) {
			// Handle general exceptions with a generic message
			logger.error("Internal Server Error while adding company logo", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
		}
	}


	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<?>> deleteCompany(@PathVariable Long id) {
		try {
			boolean deleted = companyService.deleteCompany(id);
			if (deleted) {
				logger.info("Company deleted successfully with ID: {}", id);
				return ResponseEntity.noContent().build();
			} else {
				logger.warn("Company not found with ID: {}", id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Company not found"));
			}
		} catch (Exception e) {
			logger.error("Internal Server Error while deleting company with ID: " + id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
		}
	}
}

/*
@RestController
@RequestMapping("/companies")
@EnableTransactionManagement
public class CompanyController {
	private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);

	@Autowired
	private CompanyService companyService;

	@GetMapping
	public ResponseEntity<ApiResponse<List<Company>>> getAllCompanies() {
		try {
			List<Company> companies = companyService.getAllCompanies();
			logger.error("All companies successfully retrieved");
			return ResponseEntity.ok(new ApiResponse<>(true, companies, "All companies retrieved successfully"));
		} catch (Exception e) {
			logger.error("Error occurred while retrieving all companies", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
		}
	}
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Company>> getCompany(@PathVariable Long id) {
		try {
			Company company = companyService.getCompany(id);
			if (company != null) {
				logger.info("Company retrieved successfully: {}", company.getName());
				return ResponseEntity.ok(new ApiResponse<>(true, company, "Company retrieved successfully"));
			} else {
				logger.warn("Company not found with ID: {}", id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Company not found"));
			}
		} catch (Exception e) {
			logger.error("Internal Server Error while retrieving company with ID: " + id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
		}
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Company>> createCompany(@RequestBody Company company) {
		try {
			Company createdCompany = companyService.createCompany(company);
			logger.info("Company created successfully: {}", createdCompany.getName());
			return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, createdCompany, "Company created successfully"));
		} catch (Exception e) {
			logger.error("Internal Server Error while creating company", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<Company>> updateCompany(@PathVariable Long id, @RequestBody Company company) {
		try {
			Company updatedCompany = companyService.updateCompany(id, company);
			if (updatedCompany != null) {
				logger.info("Company updated successfully: {}", updatedCompany.getName());
				return ResponseEntity.ok(new ApiResponse<>(true, updatedCompany, "Company updated successfully"));
			} else {
				logger.warn("Company not found with ID: {}", id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Company not found"));
			}
		} catch (Exception e) {
			logger.error("Internal Server Error while updating company with ID: " + id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
		}
	}

	@PutMapping("/addImage/{id}")
	public ResponseEntity<ApiResponse<?>> addLogo(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
		try {
			if (file != null && !file.isEmpty()) {
				Company companyLogoAdded = companyService.AddLogo(file, id);

				if (companyLogoAdded != null) {
					return ResponseEntity.ok(new ApiResponse<>(true, companyLogoAdded, "Company Logo updated successfully"));
				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Error in adding Logo"));
				}
			} else {
				// Handle the case where the input file is null or empty
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, "Invalid file provided"));
			}
		}  catch (Exception e) {
			// Handle general exceptions with a generic message
			logger.error("Internal Server Error while adding company logo", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> deleteCompany(@PathVariable Long id) {
		try {
			boolean deleted = companyService.deleteCompany(id);
			if (deleted) {
				logger.info("Company deleted successfully with ID: {}", id);
				return ResponseEntity.noContent().build();
			} else {
				logger.warn("Company not found with ID: {}", id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Company not found"));
			}
		} catch (Exception e) {
			logger.error("Internal Server Error while deleting company with ID: " + id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
		}
	}
}
*/