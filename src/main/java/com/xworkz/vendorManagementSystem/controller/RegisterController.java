package com.xworkz.vendorManagementSystem.controller;

import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.xworkz.vendorManagementSystem.DTO.VendorDTO;
import com.xworkz.vendorManagementSystem.service.VendorService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/")
@Slf4j
@EnableWebMvc
public class RegisterController {

    @Autowired
    private VendorService vendorService;

    @GetMapping(value = "/checkEmailExistence/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String checkEmailExistence(@PathVariable String email) {
        log.info("Received request to check email existence for email: {}", email);
        boolean emailExistsInDatabase = vendorService.checkEmailExistence(email);
        if (emailExistsInDatabase) {
            log.info("Email exists in the database: {}", email);
            return "EmailIDexists";
        } else {
            log.info("Email does not exist in the database: {}", email);
            return "EmailID not exists";
        }
    }

    @GetMapping(value = "/checkGSTNumberExistence/{GSTNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String checkGSTNumberExistence(@PathVariable String GSTNumber) {
        log.info("Received request to check GST Number existence for GST Number: {}", GSTNumber);
        boolean exists = vendorService.checkGSTNumberExistence(GSTNumber);
        if (exists) {
            log.info("GST Number exists in the database: {}", GSTNumber);
            return "GSTNumberExist";
        } else {
            log.info("GST Number does not exist in the database: {}", GSTNumber);
            return "GSTNumberExists is not exit";
        }
    }

    @GetMapping(value = "/checkContactNumberExistence/{contactNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String checkContactNumberExistence(@PathVariable Long contactNumber) {
        log.info("Received request to check contact number existence for contact number: {}", contactNumber);
        boolean exists = vendorService.checkContactNumberExistence(contactNumber);
        if (exists) {
            log.info("Contact number exists in the database: {}", contactNumber);
            return "contactNumberexists";
        } else {
            log.info("Contact number does not exist in the database: {}", contactNumber);
            return "contactNumberexists is not exit";
        }
    }

    @GetMapping(value = "/checkWebsiteExistence/{website}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String checkWebsiteExistence(@PathVariable String website) {
        log.info("Received request to check website existence for website: {}", website);
        boolean exists = vendorService.checkWebsiteExistence(website);
        if (exists) {
            log.info("Website exists in the database: {}", website);
            return "websiteExists";
        } else {
            log.info("Website does not exist in the database: {}", website);
            return "website is not exit";
        }
    }
    
    @PostMapping("/register")
    public String saveVendorDTO(@ModelAttribute VendorDTO dto, Model model, HttpSession session) {
        log.info("Registering vendor with data: {}", dto);
        Set<ConstraintViolation<VendorDTO>> constraintViolations = vendorService.validateAndSaveVendorDTO(dto);
        if (constraintViolations.isEmpty()) {
            session.setAttribute("accountCreateMessage", "Account created successfully. Please sign in.");
            log.info("Vendor registration successful: {}", dto.getEmailId());
            return "logIn";
        } else {
            model.addAttribute("errors", constraintViolations);
            log.warn("Vendor registration failed with errors: {}", constraintViolations);
            return "redirect:/registerPage";
        }
    }
}
