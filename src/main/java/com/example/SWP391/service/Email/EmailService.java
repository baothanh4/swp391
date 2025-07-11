package com.example.SWP391.service.Email;

import com.example.SWP391.entity.Booking.Booking;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendBookingConfirmationEnglish(String toEmail, Booking booking) {
        String subject = "✅ DNA Test Booking Confirmation";

        String customerName = booking.getCustomer().getFullName();
        String serviceName = booking.getService().getName();
        String kitName = booking.getBioKit().getName();
        String date = booking.getAppointmentTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String timeRange = booking.getTimeRange();
        String collectionMethod = booking.getCollectionMethod();
        String address = booking.getAddress();
        String paymentMethod = booking.getPaymentMethod();
        String paymentCode = booking.getPaymentCode();
        float cost = booking.getCost();
        boolean isExpress = booking.isExpressService();
        float expressFee = isExpress ? booking.getService().getExpressPrice() : 0f;
        float mediationFee = getMediationFee(collectionMethod, isExpress);
        float additionalCost = booking.getAdditionalCost();
        float totalCost = booking.getTotalCost();

        String expressRow = isExpress
                ? "<tr><td><strong>Express Service Fee:</strong></td><td>" + String.format("%.0f VND", expressFee) + "</td></tr>"
                : "";

        String content = String.format("""
        <div style='font-family:sans-serif;'>
            <h2>🔬 Your DNA Test Booking is Confirmed!</h2>
            <p>Dear <strong>%s</strong>,</p>
            <p>We have received your DNA test booking with the following details:</p>

            <table cellpadding='6' style='border-collapse:collapse;'>
                <tr><td><strong>Service Name:</strong></td><td>%s</td></tr>
                <tr><td><strong>Kit Name:</strong></td><td>%s</td></tr>
                <tr><td><strong>Sample Collection Date:</strong></td><td>%s</td></tr>
                <tr><td><strong>Time Slot:</strong></td><td>%s</td></tr>
                <tr><td><strong>Collection Method:</strong></td><td>%s</td></tr>
                <tr><td><strong>Collection Address:</strong></td><td>%s</td></tr>
                <tr><td><strong>Payment Method:</strong></td><td>%s</td></tr>
                <tr><td><strong>Booking Code:</strong></td><td>%s</td></tr>
                %s
                <tr><td><strong>Mediation Fee:</strong></td><td>%.0f VND</td></tr>
                <tr><td><strong>Service Cost:</strong></td><td>%.0f VND</td></tr>
                <tr><td><strong>Additional Fee:</strong></td><td>%.0f VND</td></tr>
                <tr><td><strong>Total Cost:</strong></td><td><strong style='color:blue;'>%.0f VND</strong></td></tr>
            </table>

            <hr style='margin-top:20px;'/>
            <p><strong>📞 For inquiries, please contact us at:</strong><br/>
            Email: genetixcontactsp@gmail.com<br/>
            Phone: 0901452366</p>

            <p style='font-size:small;'>ADN Testing Center – Accurate. Private. Trusted.</p>
        </div>
        """,
                customerName,
                serviceName,
                kitName,
                date,
                timeRange,
                collectionMethod,
                address,
                paymentMethod,
                paymentCode,
                expressRow,
                mediationFee,
                cost,
                additionalCost,
                totalCost
        );

        try {
            sendHtmlEmail(toEmail, subject, content);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
    private float getMediationFee(String method, boolean isExpress) {
        if (method == null) return 0;
        return switch (method.trim().toLowerCase()) {
            case "staffarrival" -> isExpress ? 0f : 500_000f;
            case "postal" -> 250_000f;
            case "walkin" -> 0f;
            default -> 0f;
        };
    }
    public void sendResultAvailableEmail(String toEmail, String customerName) {
        String subject = "📢 DNA Test Result Available";

        String content = String.format("""
        <div style='font-family:sans-serif;'>
            <h2>🧬 Your DNA Test Result is Ready</h2>
            <p>Dear <strong>%s</strong>,</p>
            <p>Your DNA test result is now available. Please log in to our website to view the full result.</p>

            <p>
                🔗 <a href="http://localhost:5173/login">Click here to log in</a>
            </p>

            <p>If you have any questions, please contact our support team.</p>

            <hr/>
            <p><strong>Genetix Testing Center</strong><br/>
            Email: genetixcontactsp@gmail.com<br/>
            Phone: 0901452366</p>
        </div>
        """, customerName);

        try {
            sendHtmlEmail(toEmail, subject, content);
        } catch (Exception e) {
            System.err.println("❌ Failed to send result notification: " + e.getMessage());
        }
    }



    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    public String buildRefundEmail(String customerName, String paymentCode, String appointmentDate, String serviceName) {
        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <title>Refund Notification</title>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body {
                    margin: 0;
                    padding: 0;
                    font-family: 'Segoe UI', sans-serif;
                    background-color: #f4f4f4;
                }
                .container {
                    max-width: 600px;
                    margin: 40px auto;
                    background-color: #fff;
                    padding: 30px;
                    border-radius: 12px;
                    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                }
                h2 {
                    text-align: center;
                    color: #2c3e50;
                }
                .content {
                    color: #333;
                    line-height: 1.7;
                    margin-top: 20px;
                }
                .highlight {
                    color: #e74c3c;
                    font-weight: bold;
                }
                .info {
                    background-color: #f9f9f9;
                    padding: 15px;
                    border-left: 5px solid #3498db;
                    margin: 20px 0;
                    border-radius: 5px;
                }
                .footer {
                    text-align: center;
                    font-size: 13px;
                    color: #888;
                    margin-top: 30px;
                }
                ul {
                    padding-left: 20px;
                }
                @media (max-width: 600px) {
                    .container {
                        padding: 20px;
                        margin: 20px;
                    }
                }
            </style>
        </head>
        <body>
            <div class="container">
                <h2>Refund Notification (Hoàn tiền qua VNPay)</h2>
                <div class="content">
                    <p>Xin chào <strong>%s</strong>,</p>

                    <p>Yêu cầu huỷ lịch xét nghiệm của bạn đã được xác nhận thành công.</p>

                    <div class="info">
                        <p>Hệ thống sẽ xử lý <span class="highlight">hoàn tiền trong vòng 72 giờ</span> thông qua VNPay.</p>
                        <p><strong>Mã thanh toán:</strong> %s</p>
                        <p><strong>Ngày đặt lịch:</strong> %s</p>
                        <p><strong>Dịch vụ:</strong> %s</p>
                    </div>

                    <p>Nếu bạn có bất kỳ câu hỏi hoặc cần hỗ trợ, vui lòng liên hệ với chúng tôi qua:</p>
                    <ul>
                        <li>Email: <a href="mailto:genetixcontactsp@gmail.com">genetixcontactsp@gmail.com</a></li>
                        <li>Số điện thoại: 0901 452 366</li>
                    </ul>

                    <p>Cảm ơn bạn đã tin tưởng sử dụng dịch vụ tại Genetix Testing Center.</p>
                </div>

                <div class="footer">
                    &copy; 2025 Genetix Testing Center. All rights reserved.
                </div>
            </div>
        </body>
        </html>
        """.formatted(customerName, paymentCode, appointmentDate, serviceName);
    }
}
