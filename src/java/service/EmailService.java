/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 *
 * @author hades
 */
public class EmailService {

    // Email: onlinecardstore01@gmail.com
    // Password: onlinecard123
    // AppPassword: sbkltmoqmsqollok
    public static void sendEmail(String toEmail, String subject, String messageContent)
            throws MessagingException, UnsupportedEncodingException {
        final String fromEmail = "onlinecardstore01@gmail.com";
        final String appPassword = "sbkltmoqmsqollok"; // App password 16 ký tự

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromEmail));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        msg.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
        msg.setContent(messageContent, "text/plain; charset=UTF-8");

        Transport.send(msg);
    }

    /**
     * Convenience wrapper to send a card code email. Returns true if sent, false on
     * failure.
     */
    public boolean sendCardCodeEmail(String toEmail, String code, String serial) {
        StringBuilder body = new StringBuilder();
        body.append("Thank you for your purchase.\n");
        body.append("Card code: ").append(code).append("\n");
        if (serial != null && !serial.isEmpty()) {
            body.append("Serial: ").append(serial).append("\n");
        }
        body.append("Please keep this information secure.");

        try {
            sendEmail(toEmail, "Your card code", body.toString());
            return true;
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
            return false;
        }
    }
}
