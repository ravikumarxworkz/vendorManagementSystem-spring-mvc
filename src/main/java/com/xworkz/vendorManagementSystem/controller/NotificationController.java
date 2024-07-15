package com.xworkz.vendorManagementSystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xworkz.vendorManagementSystem.DTO.UserNotificationDTO;
import com.xworkz.vendorManagementSystem.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/notification")
@Slf4j
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/fetchUserNotifications")
    @ResponseBody
    public List<UserNotificationDTO> fetchUserNotifications(@RequestParam String email) {
        log.info("Fetching notifications for email: {}", email);
        List<UserNotificationDTO> notifications = notificationService.getNotifcationMessageByEmail(email);
        log.debug("Notifications fetched: {}", notifications);
        return notifications;
    }

    @GetMapping("/markNotificationAsViewed")
    @ResponseBody
    public List<UserNotificationDTO> markNotificationAsViewed(@RequestParam int id, String userEmail) {
        log.info("Marking notification ID {} as viewed for user email: {}", id, userEmail);
        notificationService.markNotificationAsViewed(id);
        List<UserNotificationDTO> updatedNotifications = notificationService.getNotifcationMessageByEmail(userEmail);
        log.debug("Updated notifications: {}", updatedNotifications);
        return updatedNotifications;
    }

}
