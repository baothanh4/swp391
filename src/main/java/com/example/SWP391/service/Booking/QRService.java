package com.example.SWP391.service.Booking;

import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.PaymentProperties;
import com.example.SWP391.repository.BookingRepository.BookingRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QRService {
    @Autowired private final BookingRepository bookingRepository;
    @Autowired private final PaymentProperties paymentProperties;

    public String generatedQRUrl(int bookingID){
        Booking booking=bookingRepository.findById(bookingID).orElseThrow(()->new IllegalArgumentException("Booking not found"));

        String bankId= paymentProperties.getBankId();
        String accountNumber= paymentProperties.getAccountNumber();
        String accountName=URLEncoder.encode(paymentProperties.getAccountName(),StandardCharsets.UTF_8);

        long amount=Math.round(booking.getTotalCost());
        String addInfo = URLEncoder.encode(
                booking.getPaymentCode() != null ? booking.getPaymentCode() : "B0000",
                StandardCharsets.UTF_8
        );

        return String.format(
                "https://img.vietqr.io/image/%s-%s-compact2.png?amount=%d&addInfo=%s&accountName=%s",
                bankId, accountNumber, amount, addInfo, accountName
        );
    }
    public String generateQRCodeBase64(String qrContent, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, width, height);

        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        byte[] qrBytes = baos.toByteArray();

        return Base64.getEncoder().encodeToString(qrBytes);
    }

}