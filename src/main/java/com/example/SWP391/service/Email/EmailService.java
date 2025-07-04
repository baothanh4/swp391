package com.example.SWP391.service.Email;

import com.example.SWP391.entity.Booking.Booking;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendBookingConfirmationEnglish(String toEmail, Booking booking) {
        String subject = "✅ DNA Test Booking Confirmation";

        String customerName = booking.getCustomer().getFullName();
        String serviceName = booking.getService().getName();
        String kitName = booking.getBioKit().getName();
        String date = booking.getAppointmentTime().toString();
        String timeRange = booking.getTimeRange();
        String collectionMethod = booking.getCollectionMethod();
        String address = booking.getAddress();
        String paymentMethod = booking.getPaymentMethod();
        String paymentCode = booking.getPaymentCode();
        float cost = booking.getCost();
        boolean isExpress = booking.isExpressService();
        float expressFee = isExpress ? booking.getService().getExpressPrice() : 0f;
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


    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}
