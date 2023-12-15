package com.example.GraplerEnhancemet.service;

import com.example.GraplerEnhancemet.Repository.CompanyRepository;
import com.example.GraplerEnhancemet.Repository.WorkspaceRepository;
import com.example.GraplerEnhancemet.custom_exception.DuplicateCompanyException;
import com.example.GraplerEnhancemet.entity.Workspace;
import com.example.GraplerEnhancemet.util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.GraplerEnhancemet.entity.Company;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.GraplerEnhancemet.dto.CompanyDTO;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;


@Service
@Transactional
public class CompanyService {
    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private ModelMapper modelMapper; // Autowire ModelMapper

    public List<CompanyDTO> getAllCompanies() {
        try {
            List<Company> companies = companyRepository.findAll();
            logger.info("Retrieved all companies successfully.");
            // Convert Company objects to CompanyDTO using ModelMapper
            return companies.stream()
                    .map(company -> modelMapper.map(company, CompanyDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error occurred while retrieving all companies", e);
            return null;
        }
    }

    public CompanyDTO getCompany(Long id) {
        try {
            Company company = companyRepository.findById(id).orElse(null);
            if (company != null) {
                CompanyDTO companyDTO = modelMapper.map(company, CompanyDTO.class);
                logger.info("Retrieved company details successfully.");
                return companyDTO;
            } else {
                logger.warn("Company not found with ID: " + id);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error while getting company details with ID: " + id, e);
            return null;
        }
    }

    public CompanyDTO createCompany(Company company) {
        try {
            company.setName(company.getName().trim());
            Company existingCompanyByName = getCompanyByName(company.getName());
            if (existingCompanyByName != null) {
                throw new DuplicateCompanyException("A company with the same name already exists.");
            }

            // Check if another company with the same email exists
            Company existingCompanyByEmail = getCompanyByEmail(company.getEmail());
            if (existingCompanyByEmail != null ) {
                throw new DuplicateCompanyException("A company with the same email already exists.");
            }
            company.setCreationTime(LocalDateTime.now());
            Workspace workspace = new Workspace();
            workspace.setName("Default");
            workspace.setCreationTime(LocalDateTime.now());
            workspace.setCompany(company);
            workspaceRepository.save(workspace);
            Company createdCompany = companyRepository.save(company);
            CompanyDTO createdCompanyDTO = modelMapper.map(createdCompany, CompanyDTO.class);
            logger.info("Company created successfully: {}", createdCompanyDTO.getName());
            return createdCompanyDTO;
        } catch (DuplicateCompanyException ex) {
            logger.error("A company with the same name or email already exists.");
            throw ex;
        } catch (DataIntegrityViolationException ex) {
//            throw new DuplicateCompanyException("A company with the same email or name already exists.");
            logger.error("Unexpected DataIntegrityViolation during company creation: " + ex.getMessage(), ex);
            throw ex;
        } catch (UnexpectedRollbackException ex) {
            logger.error("Unexpected rollback during company creation: " + ex.getMessage(), ex);
            return null;
        } catch (Exception e) {
            logger.error("Error while creating company", e);
           return null;
        }
        }

    public CompanyDTO updateCompany(Long id, Company updatedCompany) {
        try {
            updatedCompany.setName(updatedCompany.getName().trim());
            Company existingCompany = companyRepository.findById(id).orElse(null);
            if (existingCompany != null) {

                Company existingCompanyByName = getCompanyByName(updatedCompany.getName());
                if (existingCompanyByName != null && !existingCompanyByName.getId().equals(id)) {
                    throw new DuplicateCompanyException("A company with the same name already exists.");
                }

                // Check if another company with the same email exists
                Company existingCompanyByEmail = getCompanyByEmail(updatedCompany.getEmail());
                if (existingCompanyByEmail != null && !existingCompanyByEmail.getId().equals(id)) {
                    throw new DuplicateCompanyException("A company with the same email already exists.");
                }

                if (updatedCompany.getName() != null) {
                    existingCompany.setName(updatedCompany.getName());
                }
                if (updatedCompany.getEmail() != null) {
                    existingCompany.setEmail(updatedCompany.getEmail());
                }
                if (updatedCompany.getLogo() != null) {
                    existingCompany.setLogo(updatedCompany.getLogo());
                }
                if (updatedCompany.getDescription() != null) {
                    existingCompany.setDescription(updatedCompany.getDescription());
                }
                if (updatedCompany.getStructureType() != null) {
                    existingCompany.setStructureType(updatedCompany.getStructureType());
                }
                if (updatedCompany.getContactNumber() != null) {
                    existingCompany.setContactNumber(updatedCompany.getContactNumber());
                }
                if (updatedCompany.getAddress() != null) {
                    existingCompany.setAddress(updatedCompany.getAddress());
                }

                // Save the updated entity
                Company savedCompany = companyRepository.save(existingCompany);

                CompanyDTO savedCompanyDTO = modelMapper.map(savedCompany, CompanyDTO.class);
                logger.info("Company updated successfully: {}", savedCompanyDTO.getName());
                return savedCompanyDTO;
            } else {
                logger.warn("Company not found with ID: " + id);
                return null;
            }
        } catch (DuplicateCompanyException ex) {
            logger.error("A company with the same name or email already exists.");
            throw ex;
        } catch (DataIntegrityViolationException ex) {
//            throw new DuplicateCompanyException("A company with the same email or name already exists.");
            logger.error("Unexpected DataIntegrityViolation during company updation: " + ex.getMessage(), ex);
            throw ex;
        } catch (UnexpectedRollbackException ex) {
            logger.error("Unexpected rollback during company updation : " + ex.getMessage(), ex);
            return null;
        }  catch (Exception e) {
            logger.error("Error while updating company with ID: " + id, e);
            return null;
        }
    }

    public boolean deleteCompany(Long id) {
        try {
            Company existingCompany = companyRepository.findById(id).orElse(null);
            if (existingCompany != null) {
                workspaceRepository.deleteByCompany_Id(id);
                companyRepository.delete(existingCompany);
                logger.info("Company deleted successfully with ID: {}", id);
                return true;
            } else {
                logger.warn("Company not found with ID: " + id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error while deleting company with ID: " + id, e);
            return false;
        }
    }
    public Company getCompanyByName(String name) {
        return companyRepository.findByName(name).orElse(null);
    }
    public Company getCompanyByEmail(String email) {
        return companyRepository.findByEmail(email).orElse(null);
    }

    public Company getCompanyById(Long id) {
        return companyRepository.findById(id).orElse(null);
    }
    public CompanyDTO AddLogo(MultipartFile logo, Long companyId) {
        GenerateThumbnail generate = new GenerateThumbnail();
        try {
            if (logo != null && !logo.isEmpty()) {
                try (InputStream logoInputStream = logo.getInputStream()) {
                    BufferedImage originalImage = ImageIO.read(logoInputStream);
                    int thumbnailWidth = 100; // Adjust to your desired thumbnail width
                    int thumbnailHeight = 100; // Adjust to your desired thumbnail height
                    byte[] thumbnailData = generate.generateThumbnail(originalImage, thumbnailWidth, thumbnailHeight);

                    Optional<Company> companyOptional = companyRepository.findById(companyId);
                    if (companyOptional.isPresent()) {
                        Company company = companyOptional.get();
                        company.setLogo(thumbnailData);
                        companyRepository.save(company);

                        CompanyDTO addLogoCompanyDTO = modelMapper.map(company, CompanyDTO.class);
                        logger.info("Company Logo updated successfully: {}", addLogoCompanyDTO.getName());
                        return addLogoCompanyDTO;
                    } else {
                        // Handle the case where the company with the given ID is not found
                        logger.error("Company not found with ID: {}", companyId);
                        return null;
                    }
                }
            } else {
                // Handle the case where the input logo is null or empty
                logger.error("Invalid logo file provided");
                return null;
            }
        } catch (Exception e) {
            // Handle exceptions with specific error messages
            logger.error("Error adding company logo: {}", e.getMessage(), e);
            return null;
        }
    }
}

/*
@Service
@Transactional
public class CompanyService {
    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;


    public List<Company> getAllCompanies() {
        try {
            List<Company> companies = companyRepository.findAll();
            logger.info("Retrieved all companies successfully.");
            return companies;
        } catch (Exception e) {
            logger.error("Error occurred while retrieving all companies", e);
            return null;
        }
    }
    public Company getCompany(Long id) {
        try {
            logger.info("Retrieved company successfully.");
            return companyRepository.findById(id).orElse(null);
        } catch (Exception e) {
            logger.error("Error while getting company with ID: " + id, e);
            return null;
        }
    }

    public Company createCompany(Company company) {
        try {
            company.setCreationTime(LocalDateTime.now());
            Workspace workspace = new Workspace();
            workspace.setName("Default");
            workspace.setCreationTime(LocalDateTime.now());
            workspace.setCompany(company);
            workspaceRepository.save(workspace);
            Company createdCompany = companyRepository.save(company);
            logger.info("Company created successfully: {}", createdCompany.getName());
            return createdCompany;
        } catch (Exception e) {
            logger.error("Error while creating company", e);
            return null;
        }
    }

    public Company updateCompany(Long id, Company updatedCompany) {
        try {
            Company existingCompany = getCompany(id);
            if (existingCompany != null) {
                updatedCompany.setId(existingCompany.getId());
                updatedCompany.setCreationTime(existingCompany.getCreationTime());
                Company savedCompany = companyRepository.save(updatedCompany);
                logger.info("Company updated successfully: {}", savedCompany.getName());
                return savedCompany;
            }
            return null;
        } catch (Exception e) {
            logger.error("Error while updating company with ID: " + id, e);
            return null;
        }
    }

    public boolean deleteCompany(Long id) {
        try {
            Company existingCompany = getCompany(id);
            if (existingCompany != null) {
                workspaceRepository.deleteByCompany_Id(id);
                companyRepository.delete(existingCompany);
                logger.info("Company deleted successfully with ID: {}", id);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Error while deleting company with ID: " + id, e);
            return false;

        }
    }

    public Company getCompanyByName(String name) {
        return companyRepository.findByName(name).orElse(null);
    }
    public Company getCompanyByEmail(String email) {
        return companyRepository.findByEmail(email).orElse(null);
    }
    public Company AddLogo(MultipartFile logo, Long companyId) {
        GenerateThumbnail generate = new GenerateThumbnail();
        try {
            if (logo != null && !logo.isEmpty()) {
                try (InputStream logoInputStream = logo.getInputStream()) {
                    BufferedImage originalImage = ImageIO.read(logoInputStream);
                    int thumbnailWidth = 100; // Adjust to your desired thumbnail width
                    int thumbnailHeight = 100; // Adjust to your desired thumbnail height
                    byte[] thumbnailData = generate.generateThumbnail(originalImage, thumbnailWidth, thumbnailHeight);

                    Optional<Company> companyOptional = companyRepository.findById(companyId);
                    if (companyOptional.isPresent()) {
                        Company company = companyOptional.get();
                        company.setLogo(thumbnailData);
                        companyRepository.save(company);

                        logger.info("Company Logo updated successfully: {}", addLogoCompanyDTO.getName());
                        return company;
                    } else {
                        // Handle the case where the company with the given ID is not found
                        logger.error("Company not found with ID: {}", companyId);
                        return null;
                    }
                }
            } else {
                // Handle the case where the input logo is null or empty
                logger.error("Invalid logo file provided");
                return null;
            }
        } catch (Exception e) {
            // Handle exceptions with specific error messages
            logger.error("Error adding company logo: {}", e.getMessage(), e);
            return null;
        }
    }
}

*/