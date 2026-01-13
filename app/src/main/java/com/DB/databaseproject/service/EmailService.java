package com.DB.databaseproject.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmailService {
    
    private static EmailService instance;
    private final ExecutorService emailExecutor;
    
    // IMPORTANT: This must be a Gmail App Password, NOT your normal Gmail password
    // Gmail blocks normal passwords for security reasons
    // To generate App Password: Google Account > Security > 2-Step Verification > App passwords
    private static final String EMAIL_USERNAME = "mohammedsholi05@gmail.com";
    private static final String EMAIL_PASSWORD = "tbkvivsxbgzidezg";

    private EmailService() {
        this.emailExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "EmailSender");
            thread.setDaemon(true);
            return thread;
        });
    }
    
    public static EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }
    
    /**
     * Send order completed notification email (async)
     * Does NOT block the calling thread
     * 
     * @param customerEmail Customer's email address
     * @param customerFirstName Customer's first name
     * @param orderId Order ID
     */
    public void sendOrderCompletedNotification(String customerEmail, String customerFirstName, int orderId) {
        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            System.out.println("⚠️ Skipping email: Customer email is null or empty");
            return;
        }
        
        emailExecutor.submit(() -> {
            try {
                sendEmailSync(customerEmail, customerFirstName, orderId);
            } catch (Exception e) {
                String errorMsg = e.getMessage();
                if (errorMsg != null && (errorMsg.contains("535") || 
                                         errorMsg.contains("Username and Password not accepted") || 
                                         errorMsg.contains("Authentication failed"))) {
                    System.err.println("❌ Email not sent: Invalid Gmail App Password");
                    System.err.println("   Please configure a valid Gmail App Password in EmailService.java");
                } else {
                    System.err.println("⚠️ Failed to send email to " + customerEmail + ": " + errorMsg);
                }
            }
        });
    }
    
    private void sendEmailSync(String toEmail, String customerFirstName, int orderId) {
        try {
            System.out.println("\n═══════════════════════════════════════════════");
            System.out.println("📧 Sending Order Completed Email");
            System.out.println("═══════════════════════════════════════════════");
            System.out.println("To: " + toEmail);
            System.out.println("Order ID: " + orderId);
            
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME, "Stone Sales"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Your order has been completed");
            
            String emailBody = createEmailBody(customerFirstName, orderId);
            message.setText(emailBody);
            
            Transport.send(message);
            
            System.out.println("✅ Email sent successfully to " + toEmail);
            System.out.println("═══════════════════════════════════════════════\n");
        } catch (MessagingException e) {
            if (e.getMessage() != null && e.getMessage().contains("535")) {
                System.err.println("❌ Email not sent: Invalid Gmail App Password");
                System.err.println("   Please update EMAIL_PASSWORD in EmailService.java with a valid Gmail App Password");
            } else {
                System.err.println("❌ Email not sent: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("❌ Email not sent: " + e.getMessage());
        }
    }
    
    /**
     * Create professional email body
     */
    private String createEmailBody(String customerFirstName, int orderId) {
        return String.format("""
            Hello %s,
            
            Your order (Order ID: %d) has been completed successfully.
            
            Thank you for using Stone Sales.
            
            Best regards,
            Stone Sales Team
            """, 
            customerFirstName, 
            orderId
        );
    }
    
    /**
     * Shutdown executor gracefully
     */
    public void shutdown() {
        emailExecutor.shutdown();
    }
}
