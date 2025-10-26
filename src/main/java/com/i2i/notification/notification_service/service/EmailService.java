package com.i2i.notification.notification_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails via SMTP.
 * Handles email composition and delivery for user notifications.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 25-10-2025
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends welcome email to new user.
     *
     * @param userEmail the user's email address
     * @param userName the user's name
     * @param userPassword the user's password
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendWelcomeEmail(String userEmail, String userName, String userPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userEmail);
            message.setSubject("Welcome to User Management Service!");
            message.setText(createWelcomeEmailContent(userName, userEmail, userPassword));

            mailSender.send(message);
            logger.info("Welcome email sent successfully to: {}", userEmail);
            return true;
        } catch (Exception e) {
            logger.error("Failed to send welcome email to: {}", userEmail, e);
            return false;
        }
    }

    /**
     * Sends account deletion email to user.
     *
     * @param userEmail the user's email address
     * @param userName the user's name
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendAccountDeletionEmail(String userEmail, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userEmail);
            message.setSubject("Account Deleted");
            message.setText(createAccountDeletionEmailContent(userName, userEmail));

            mailSender.send(message);
            logger.info("Account deletion email sent successfully to: {}", userEmail);
            return true;
        } catch (Exception e) {
            logger.error("Failed to send account deletion email to: {}", userEmail, e);
            return false;
        }
    }

    /**
     * Creates welcome email content.
     *
     * @param userName the user's name
     * @param userEmail the user's email
     * @param userPassword the user's password
     * @return formatted email content
     */
    public String createWelcomeEmailContent(String userName, String userEmail, String userPassword) {
        return String.format(
            "Dear %s,\n\n" +
            "Welcome to User Management! Your account has been successfully created.\n\n" +
            "Your Account Details:\n" +
            "• Username: %s\n" +
            "• Email: %s\n" +
            "• Password: %s\n\n" +
            "Please keep your login credentials safe and secure.\n\n" +
            "You can now log in to your account and start using our services.\n\n" +
            "If you have any questions or need assistance, please don't hesitate to contact our support team.\n\n" +
            "Thank you for joining us!\n\n" +
            "Best regards,\n" +
            "The Management Team",
            userName, userName, userEmail, userPassword
        );
    }

    /**
     * Creates account deletion email content.
     *
     * @param userName the user's name
     * @param userEmail the user's email
     * @return formatted email content
     */
    public String createAccountDeletionEmailContent(String userName, String userEmail) {
        return String.format(
            "Dear %s,\n\n" +
            "Your account has been successfully deleted from our platform.\n\n" +
            "Account Details:\n" +
            "• Email: %s\n\n" +
            "All your personal data and account information have been permanently removed from our systems.\n\n" +
            "If you did not request this account deletion, please contact our support team immediately.\n\n" +
            "We're sorry to see you go, but you're always welcome to create a new account in the future.\n\n" +
            "Best regards,\n" +
            "The Platform Team",
            userName != null ? userName : "User", userEmail
        );
    }
}
