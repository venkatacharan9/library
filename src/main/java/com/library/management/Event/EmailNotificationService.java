package com.library.management.Event;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    public void sendReturnEmail(String to, String bookTitle, double lateFee) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            StringBuilder emailText = new StringBuilder();
            emailText.append("Dear User,\n\nYou have successfully returned the book: '")
                    .append(bookTitle).append("'.");

            if (lateFee > 0) {
                emailText.append("\nLate Fee: ").append(lateFee).append(" DKK.");
                emailText.append("\nPlease ensure timely returns to avoid late fees.");
            }

            emailText.append("\n\nThank you!\nLibrary Team");

            helper.setTo(to);
            helper.setSubject("Book Return Confirmation");
            helper.setText(emailText.toString());

            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email: {}", e.getMessage());
        }
    }

    public void sendQueueNotification(String to, String bookTitle) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Book Available for Borrowing!");
            helper.setText("Dear User,\n\nThe book '" + bookTitle + "' is now available for borrowing." +
                    "\nPlease visit the library before someone else takes it.\n\nThank you!\nLibrary Team");

            mailSender.send(message);
            log.info("Queue Notification Email sent to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send queue notification email: {}", e.getMessage());
        }
    }
}
