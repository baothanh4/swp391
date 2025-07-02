package com.example.SWP391.service.Booking;

import com.example.SWP391.entity.Booking.Booking;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VNPayService {
    @Value("${vnpay.tmncode}") private String tmnCode;
    @Value("${vnpay.hashSecret}") private String hashSecret;
    @Value("${vnpay.payUrl}") private String payUrl;
    @Value("${vnpay.returnUrl}") private String returnUrl;
    @Value("${vnpay.ipnUrl}") private String ipnUrl;

    public String createVNPayUrl(String orderID,long amount,String clientIp){
        Map<String,String> params=new HashMap<>();
        params.put("vnp_Version","2.1.0");
        params.put("vnp_Command","pay");
        params.put("vnp_TmnCode",tmnCode);
        params.put("vnp_Amount",String.valueOf(amount*100));
        params.put("vnp_CurrCode","VND");
        params.put("vnp_TxnRef",orderID);
        params.put("vnp_OrderInfo","Thanh toan don hang:"+orderID);
        params.put("vnp_OrderType","other");
        params.put("vnp_Locale","vn");
        params.put("vnp_ReturnUrl",returnUrl);
        params.put("vnp_IpnUrl",ipnUrl);
        params.put("vnp_CreateDate",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        params.put("vnp_IpAddr",clientIp);


        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String name : fieldNames) {
            String value = URLEncoder.encode(params.get(name), StandardCharsets.UTF_8);
            hashData.append(name).append('=').append(value);
            query.append(name).append('=').append(value).append('&');
        }
        String secureHash = hmacSHA512(hashSecret, hashData.toString());
        query.append("vnp_SecureHash=").append(secureHash);

        return payUrl + "?" + query.toString();
    }
    public boolean validateSignature(Map<String, String> params) {
        String receivedHash = params.remove("vnp_SecureHash");
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder data = new StringBuilder();
        for (String key : keys) {
            data.append(key).append('=').append(params.get(key)).append('&');
        }
        if (data.length() > 0) data.setLength(data.length() - 1); // remove last '&'

        String expectedHash = hmacSHA512(hashSecret, data.toString());
        return expectedHash.equalsIgnoreCase(receivedHash);
    }
    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printHexBinary(bytes).toLowerCase();
        } catch (Exception e) {
            throw new RuntimeException("HMAC error: " + e.getMessage());
        }
    }

}