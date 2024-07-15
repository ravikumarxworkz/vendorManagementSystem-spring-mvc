package com.xworkz.vendorManagementSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.xworkz.vendorManagementSystem.service.VendorService;

@RestController
@RequestMapping("/loginOTP")
@EnableWebMvc
public class LogInController {

	@Autowired
	private VendorService vendorService;

	@GetMapping("/genrateLoginOTPAndSave")
	public ResponseEntity<String> genrateOTPAndSave(@RequestParam String email) {
		boolean emailExistsInDatabase = vendorService.saveLoginOTP(email);
		if (emailExistsInDatabase) {
			return ResponseEntity.ok("OPTSentSuccessfully");
		} else {
			return ResponseEntity.ok("OPTSentnotSuccessfully");
		}
	}
}
