package com.example.GraplerEnhancemet.dto;

import com.example.GraplerEnhancemet.entity.enums.StructureType;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private byte[] logo;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description should not exceed 255 characters")
    private String description;

    @NotNull(message = "Structure type must be provided")
    private StructureType structureType;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "\\d{10}", message = "Invalid contact number")
    private String contactNumber;

    @NotBlank(message = "Address is required")
    private String address;
    private LocalDateTime creationTime;
}


/*
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {
    private Long id;
    private String name;
    private String email;
    private byte[] logo;
    private String description;
    private StructureType structureType;
    private String contactNumber;
    private String address;
    private LocalDateTime creationTime;
}
*/