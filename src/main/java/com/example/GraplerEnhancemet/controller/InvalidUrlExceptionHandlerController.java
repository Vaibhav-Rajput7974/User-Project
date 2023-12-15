package com.example.GraplerEnhancemet.controller;

import com.example.GraplerEnhancemet.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class InvalidUrlExceptionHandlerController {

    private static final Logger logger = LoggerFactory.getLogger(InvalidUrlExceptionHandlerController.class);

    @RequestMapping("/**")
    public ResponseEntity<ApiResponse<String>> handleInvalidUrl() {
        logger.warn("Invalid URL requested");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Invalid URL requested"));
    }
}
