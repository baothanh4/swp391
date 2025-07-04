package com.example.SWP391.service.Auth;

import com.example.SWP391.service.Booking.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
@Component
public class VNPayUtils {

    public static Map<String, String> getVNPayResponseParams(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> en = request.getParameterNames(); en.hasMoreElements(); ) {
            String fieldName = en.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                fields.put(fieldName, fieldValue);
            }
        }
        return fields;
    }

    public static boolean verifySignature(Map<String, String> fields, String receivedHash, String secretKey) {
        StringBuilder sb = new StringBuilder();
        SortedMap<String, String> sorted = new TreeMap<>(fields);
        sorted.remove("vnp_SecureHash");
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            sb.append('=');
            sb.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            sb.append('&');
        }
        sb.setLength(sb.length() - 1); // remove last &

        String signData = sb.toString();
        String calculatedHash = HmacUtil.hmacSHA512(secretKey, signData);
        return calculatedHash.equalsIgnoreCase(receivedHash);
    }
}
