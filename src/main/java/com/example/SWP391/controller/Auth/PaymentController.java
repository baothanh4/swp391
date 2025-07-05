package com.example.SWP391.controller.Auth;

import com.example.SWP391.DTO.AuthRequest.VNPayRequest;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.repository.BookingRepository.BookingRepository;
import com.example.SWP391.service.Auth.VNPayUtils;
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
    @Autowired private VNPayUtils vnPayUtils;
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

//    @PostMapping("/vnpay")
//    public ResponseEntity<?> createVNPay(@RequestBody VNPayRequest req, HttpServletRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
//        String clientIp = request.getRemoteAddr();
//        String url = vnPayService.createVNPayUrl(req.getPaymentCode(), req.getTotalCost(), clientIp);
//        return ResponseEntity.ok(Map.of("vnpUrl", url));
//    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<String> handleVnpayReturn(HttpServletRequest request) {
        Map<String, String> vnpParams = VNPayUtils.getVNPayResponseParams(request);
        String vnpSecureHash = request.getParameter("vnp_SecureHash");

        // Bước 1: xác thực chữ ký
        boolean isValid = VNPayUtils.verifySignature(vnpParams, vnpSecureHash, "59PJT7JAH0G371AXJT8SMG6S7W3WBF5V");
        if (!isValid) {
            return ResponseEntity.badRequest().body("❌ Invalid VNPay signature");
        }

        // Bước 2: lấy paymentCode từ vnp_OrderInfo
        String paymentCode = vnpParams.get("vnp_OrderInfo").replace("Thanh toan cho ma GD: ", "").trim();

        // Bước 3: tìm Booking và cập nhật trạng thái
        Booking booking = bookingRepository.findByPaymentCode(paymentCode)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        booking.setStatus("Is Paid");
        bookingRepository.save(booking);

        // Trả về trang xác nhận
        return ResponseEntity.ok("✅ Thanh toán thành công. Đơn hàng đã được xác nhận.");
    }


}
