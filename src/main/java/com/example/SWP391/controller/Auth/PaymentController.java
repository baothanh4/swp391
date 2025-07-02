package com.example.SWP391.controller.Auth;

import com.example.SWP391.DTO.AuthRequest.VNPayRequest;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.repository.BookingRepository.BookingRepository;
import com.example.SWP391.service.Booking.BookingService;
import com.example.SWP391.service.Booking.QRService;
import com.example.SWP391.service.Booking.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private QRService qrService;
    @Autowired private BookingService bookingService;
    @Autowired private VNPayService vnPayService;

    @GetMapping("/{bookingID}")
    public ResponseEntity<Map<String, String>> getQRUrl(@PathVariable int bookingID) {
        Booking booking = bookingRepository.findById(bookingID)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getPaymentCode() == null || booking.getPaymentCode().isBlank()) {
            booking.setPaymentCode(bookingService.generateNextPaymentCode());
            bookingRepository.save(booking);
        }

        String qrUrl = qrService.generatedQRUrl(bookingID);
        return ResponseEntity.ok(Collections.singletonMap("qrUrl", qrUrl));
    }

    @PostMapping("/qr/success/{bookingID}")
    public ResponseEntity<String> handleQRSuccess(@PathVariable int bookingID) {
        Booking booking = bookingRepository.findById(bookingID)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getPaymentCode() == null || booking.getPaymentCode().isBlank()) {
            booking.setPaymentCode(bookingService.generateNextPaymentCode());
            bookingRepository.save(booking);
        }

        return ResponseEntity.ok("QR payment confirmed");
    }

    @PostMapping("/vnpay")
    public ResponseEntity<?> createVNPay(@RequestBody VNPayRequest req, HttpServletRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String clientIp = request.getRemoteAddr();
        String url = vnPayService.createVNPayUrl(req.getOrderId(), req.getAmount(), clientIp);
        return ResponseEntity.ok(Map.of("vnpUrl", url));
    }


}
