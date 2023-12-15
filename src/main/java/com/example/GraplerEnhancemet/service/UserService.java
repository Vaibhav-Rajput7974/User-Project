package com.example.GraplerEnhancemet.service;

import com.example.GraplerEnhancemet.Repository.UserRepository;
import com.example.GraplerEnhancemet.custom_exception.DuplicateDataException;
import com.example.GraplerEnhancemet.custom_exception.NotFoundException;
import com.example.GraplerEnhancemet.custom_exception.ParentNotFoundException;
import com.example.GraplerEnhancemet.dto.CompanyDTO;
import com.example.GraplerEnhancemet.dto.UserDTO;
import com.example.GraplerEnhancemet.entity.Company;
import com.example.GraplerEnhancemet.entity.User;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<UserDTO> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            logger.info("Retrieved all users successfully.");
            return users.stream()
                    .map(user -> modelMapper.map(user, UserDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error occurred while retrieving all users", e);
            return null;
        }
    }

    public UserDTO getUser(Long id) {
            User user = userRepository.findById(id).orElse(null);
            if (user != null) {
                UserDTO userDTO = modelMapper.map(user, UserDTO.class);
                logger.info("Retrieved user details successfully.");
                return userDTO;
            } else {
                logger.warn("User not found with ID: " + id);
                return null;
            }
    }

    public UserDTO createUser(User user) {
            User existingUserByEmail = getUserByEmail(user.getEmail());
            if (existingUserByEmail != null) {
                logger.error("User email already exist with : {}", user.getEmail());
                throw new DuplicateDataException("A user with the same email already exists :  "+user.getEmail());
            }
            User createdUser = userRepository.save(user);
            UserDTO createdUserDTO = modelMapper.map(createdUser, UserDTO.class);
            logger.info("User created successfully: {}", createdUserDTO.getName());
            return createdUserDTO;
    }

    public UserDTO updateUser(Long id, User updatedUser) {
            User existingUser = userRepository.findById(id).orElse(null);
            if (existingUser != null) {
                User existingUserByEmail = getUserByEmail(updatedUser.getEmail());
                if (existingUserByEmail != null && !existingUserByEmail.getId().equals(id)) {
                    logger.error("A user with the same email already exists : "+updatedUser.getEmail());
                    throw new DuplicateDataException("A user with the same email already exists : "+updatedUser.getEmail());
                }
                if (updatedUser.getName() != null) {
                    existingUser.setName(updatedUser.getName());
                }
                if (updatedUser.getEmail() != null) {
                    existingUser.setEmail(updatedUser.getEmail());
                }
                if (updatedUser.getProfile() != null) {
                    existingUser.setProfile(updatedUser.getProfile());
                }
                if (updatedUser.getPassword() != null) {
                    existingUser.setPassword(updatedUser.getPassword());
                }
                User savedUser = userRepository.save(existingUser);
                UserDTO savedUserDTO = modelMapper.map(savedUser, UserDTO.class);
                logger.info("User updated successfully with ID : {}", id);
                return savedUserDTO;
            } else {
                logger.warn("User not found with ID: " + id);
                throw new NotFoundException("User not found with ID: " + id);
              }
    }

    public boolean deleteUser(Long id) {
            Optional<User> existingUser = userRepository.findById(id);
            if (existingUser.isPresent()) {
                userRepository.delete(existingUser.get());
                logger.info("User deleted successfully with ID: {}", id);
                return true;
            } else {
                logger.warn("User not found with ID: " + id);
                return false;
            }
   }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public UserDTO AddProfile(MultipartFile logo, Long userId) {
        GenerateThumbnail generate = new GenerateThumbnail();
        try {
            if (logo != null && !logo.isEmpty()) {
                try (InputStream logoInputStream = logo.getInputStream()) {
                    BufferedImage originalImage = ImageIO.read(logoInputStream);
                    int thumbnailWidth = 100; // Adjust to your desired thumbnail width
                    int thumbnailHeight = 100; // Adjust to your desired thumbnail height
                    byte[] thumbnailData = generate.generateThumbnail(originalImage, thumbnailWidth, thumbnailHeight);

                    Optional<User> userOptional = userRepository.findById(userId);
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        user.setProfile(thumbnailData);
                        userRepository.save(user);

                        UserDTO addProfileUserDTO = modelMapper.map(user, UserDTO.class);
                        logger.info("User Profile updated successfully: {}", addProfileUserDTO.getName());
                        return addProfileUserDTO;
                    } else {
                        // Handle the case where the company with the given ID is not found
                        logger.error("User not found with ID: {}", userId);
                        return null;
                    }
                }
            } else {
                // Handle the case where the input logo is null or empty
                logger.error("Invalid profile file provided");
                return null;
            }
        } catch (Exception e) {
            // Handle exceptions with specific error messages
            logger.error("Error adding user profile : {}", e.getMessage(), e);
            return null;
        }
    }
}

