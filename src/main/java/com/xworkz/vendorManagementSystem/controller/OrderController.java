package com.xworkz.vendorManagementSystem.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.xworkz.vendorManagementSystem.DTO.OrderDTO;
import com.xworkz.vendorManagementSystem.service.OrderService;

@Controller
@RequestMapping("/")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @GetMapping("/getOrderDetailsByEmail")
    @ResponseBody
    public List<OrderDTO> getProductDetailsByEmail(@RequestParam String email) {
        logger.info("Fetching order details for email: {}", email);
        List<OrderDTO> orderDetails = orderService.getOrderDetailesByVendorID(email);
        logger.debug("Order details retrieved: {}", orderDetails);
        return orderDetails;
    }

    @GetMapping("/getOrderHistoryByEmail")
    @ResponseBody
    public List<OrderDTO> getOrderHistoryByEmail(@RequestParam String email) {
        logger.info("Fetching order history for email: {}", email);
        List<OrderDTO> orderDetails = orderService.getOrderHistoryByEmail(email);
        logger.debug("Order history retrieved: {}", orderDetails);
        return orderDetails;
    }

    @GetMapping("/getOrderPaymentListByEmail")
    @ResponseBody
    public List<OrderDTO> getOrderPaymentListByEmail(@RequestParam String email) {
        logger.info("Fetching order payment list for email: {}", email);
        List<OrderDTO> orderDetails = orderService.getOrderPaymentListByEmail(email);
        logger.debug("Order payment list retrieved: {}", orderDetails);
        return orderDetails;
    }

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<Object> uploadInvoice(@RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("orderId") String orderId, @RequestParam("status") String status) {
        try {
            int orderID = Integer.parseInt(orderId);
            logger.info("Uploading invoice for order ID: {}", orderId);
            logger.debug("File received: {}", file);
            boolean updateOrderStatus = orderService.processInvoiceAndSendEmail(file, orderID, status);
            if (updateOrderStatus) {
                return ResponseEntity.ok().body("updateOrderStatusSuccessfully");
            } else {
                return ResponseEntity.ok().body("!Failed to update order status");
            }
        } catch (Exception e) {
            logger.error("Failed to upload invoice and send email", e);
            return ResponseEntity.ok().body("!Failed to upload invoice and send email.");
        }
    }

}
