package com.xworkz.vendorManagementSystem.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xworkz.vendorManagementSystem.DTO.VendorDTO;
import com.xworkz.vendorManagementSystem.DTO.AnnouncementDTO;
import com.xworkz.vendorManagementSystem.Exception.AccountUnderVerificationException;
import com.xworkz.vendorManagementSystem.Exception.InValidateOTPException;
import com.xworkz.vendorManagementSystem.Exception.OTPExpiredException;
import com.xworkz.vendorManagementSystem.service.AnnouncementService;
import com.xworkz.vendorManagementSystem.service.VendorService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/")
@Slf4j
public class VendorController {

    @Autowired
    private VendorService vendorService;
    
    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("homePage")
    public String homePage() {
        log.info("Accessing homePage");
        return "index";
    }

    @GetMapping("logInPage")
    public String logInPage() {
        log.info("Accessing logInPage");
        return "logIn";
    }

    @GetMapping("registerPage")
    public String registerPage() {
        log.info("Accessing registerPage");
        return "register";
    }

   

    @PostMapping("/logInProfile")
    public String logIn(@RequestParam String email, String OTP, Model model, HttpSession session,
            HttpServletRequest request) {
        log.info("Attempting login for email: {}", email);
        try {
            boolean login = vendorService.validateOTPAndLogin(email, OTP);
            if (login) {
                HttpSession httpSession = request.getSession(true);
                httpSession.setAttribute("email", email);
                log.info("Login successful for email: {}", email);
                return "profile";
            } else {
                HttpSession httpSession = request.getSession(true);
                httpSession.setAttribute("errorMessage", "Login failed. Please check your credentials.");
                log.warn("Login failed for email: {}", email);
                return "redirect:/logInPage";
            }
        } catch (InValidateOTPException | AccountUnderVerificationException | OTPExpiredException e) {
            HttpSession httpSession = request.getSession(true);
            httpSession.setAttribute("errorMessage", e.getMessage());
            log.error("Login error for email: {}. Error: {}", email, e.getMessage());
            return "redirect:/logInPage";
        }
    }

    // Profile page actions start///
    @GetMapping("actionServlet")
    public String handleAction(@RequestParam String action, Model model, HttpServletRequest request,
            HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            log.warn("Session not found or email not found in session for action: {}", action);
            return "index";
        }
        String emailID = (String) session.getAttribute("email");
        log.info("Handling action: {} for email: {}", action, emailID);
        switch (action) {
            case "edit":
                if (emailID != null) {
                    VendorDTO userProfile = vendorService.getVendorDetailsByEmail(emailID);
                    session.setAttribute("userProfile", userProfile);
                    return "update";
                }
                return "redirect:/homePage";

            case "profile":
                return "profile";
            case "logout":
                session.setAttribute("email", null);
                session.setAttribute("userProfile", null);
                session.invalidate(); // Invalidate the session
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "0");
                log.info("User logged out for email: {}", emailID);
                return "redirect:/homePage";

            default:
                return "redirect:/homePage";
        }
    }

    @GetMapping("/display")
    public void displayUserImageByEmail(HttpServletResponse response, @RequestParam String email) throws IOException {
        log.info("Displaying image for email: {}", email);
        String imagePath = vendorService.findImagePathByEmail(email);
        File file = new File("D:\\vendorManageMentUserProfileImage\\" + imagePath);
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        ServletOutputStream out = response.getOutputStream();
        IOUtils.copy(in, out);
        response.flushBuffer();
    }

    @GetMapping("/updatePage")
    public String updatePage() {
        log.info("Accessing updatePage");
        return "update";
    }

    @PostMapping("/updateVendorProfile")
    public String updateVendorProfile(@ModelAttribute VendorDTO dto, Model model) {
        log.info("Updating vendor profile for email: {}", dto.getEmailId());
        try {
            Set<ConstraintViolation<VendorDTO>> violations = vendorService
                    .validateAndVendorUpdateProfile(dto.getEmailId(), dto);

            if (!violations.isEmpty()) {
                VendorDTO oldDTO = vendorService.getVendorDetailsByEmail(dto.getEmailId());
                model.addAttribute("errors", violations);
                model.addAttribute("userProfile", oldDTO);
                log.warn("Profile update failed for email: {} with errors: {}", dto.getEmailId(), violations);
                return "redirect:/updatePage";
            } else {
                VendorDTO updatedDTO = vendorService.getVendorDetailsByEmail(dto.getEmailId());
                model.addAttribute("userProfile", updatedDTO);
                model.addAttribute("message", "Your data has been updated successfully!!!");
                log.info("Profile updated successfully for email: {}", dto.getEmailId());
                return "update";
            }
        } catch (IOException e) {
            VendorDTO oldDTO = vendorService.getVendorDetailsByEmail(dto.getEmailId());
            model.addAttribute("errors", e.getMessage());
            model.addAttribute("userProfile", oldDTO);
            log.error("Profile update failed for email: {} with error: {}", dto.getEmailId(), e.getMessage());
            return "redirect:/updatePage";
        }
    }

    @GetMapping("/sendMessage")
    @ResponseBody
    public ResponseEntity<String> sendMessage(@RequestParam String email, @RequestParam String message) {
        log.info("Sending message to email: {}", email);
        boolean sendMessage = vendorService.Sendmessage(email, message);
        if (sendMessage) {
            log.info("Message sent successfully to email: {}", email);
            return ResponseEntity.ok().body("message sent successfully");
        }
        log.warn("Message sending failed to email: {}", email);
        return ResponseEntity.ok().body("!message sent successfully");
    }

    @GetMapping("/getVendorAnnouncements")
    @ResponseBody
    public List<AnnouncementDTO> getAnnouncements() {
        log.info("Fetching all vendor announcements");
        List<AnnouncementDTO> announcements = this.announcementService.getAllAnnouncements();
        return announcements;
    }
}
