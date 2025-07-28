package com.example.SWP391.controller.Auth;

import com.example.SWP391.DTO.AuthRequest.VNPayRequest;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.Booking.BookingAssigned;
import com.example.SWP391.repository.BookingRepository.BookingAssignedRepository;
import com.example.SWP391.repository.BookingRepository.BookingRepository;
import com.example.SWP391.service.Auth.VNPayUtils;
import com.example.SWP391.service.Booking.BookingService;
import com.example.SWP391.service.Booking.QRService;
import com.example.SWP391.service.Booking.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
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
    @Autowired private VNPayUtils vnPayUtils;
    @Autowired private BookingAssignedRepository bookingAssignedRepository;
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



    @GetMapping("/vnpay-return")
    public ResponseEntity<String> handleVnpayReturn(HttpServletRequest request) {
        Map<String, String> vnpParams = VNPayUtils.getVNPayResponseParams(request);
        String vnpSecureHash = request.getParameter("vnp_SecureHash");

        boolean isValid = VNPayUtils.verifySignature(vnpParams, vnpSecureHash, "59PJT7JAH0G371AXJT8SMG6S7W3WBF5V");
        if (!isValid) {
            return ResponseEntity.badRequest().body(" Invalid VNPay signature");
        }

        String paymentCode = vnpParams.get("vnp_OrderInfo").replace("Thanh toan cho ma GD: ", "").trim();

        Booking booking = bookingRepository.findByPaymentCode(paymentCode)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));



        booking.setStatus("Payment Confirmed");
        bookingRepository.save(booking);


        String redirectUrl = "http://localhost:5173/booking?paymentCode=" + booking.getPaymentCode();
        return ResponseEntity.status(302).header("Location", redirectUrl).build();
    }
    @PostMapping("/vnpay/confirm/{paymentCode}")
    public ResponseEntity<String> confirmPaymentByCode(@PathVariable String paymentCode) {
        Booking booking = bookingRepository.findByPaymentCode(paymentCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy booking"));
        BookingAssigned bookingAssigned=bookingAssignedRepository.findByBooking(booking);
        bookingAssigned.setStatus("Payment Confirmed");
        booking.setStatus("Payment Confirmed");
        bookingRepository.save(booking);
        bookingAssignedRepository.save(bookingAssigned);
        return ResponseEntity.ok(" Booking status updated to Payment Confirmed");
    }
    @GetMapping("/by-payment-code/{paymentCode}")
    public ResponseEntity<Booking> getBookingByPaymentCode(@PathVariable String paymentCode) {
        Booking booking = bookingRepository.findByPaymentCode(paymentCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy booking"));
        return ResponseEntity.ok(booking);
    }




}
