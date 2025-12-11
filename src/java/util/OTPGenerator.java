/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 *
 * @author hades
 */
public class OTPGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final int OTP_LENGTH = 6; // số chữ số OTP
    private static final int EXPIRE_MINUTES = 5; // OTP hết hạn sau 5 phút

    private String otpCode;
    private LocalDateTime createdAt;

    public OTPGenerator() {
        this.otpCode = generateOTP();
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Sinh OTP mới
     *
     * @return OTP 6 chữ số dạng String
     */
    public static String generateOTP() {
        int number = 100000 + random.nextInt(900000);
        return String.valueOf(number);
    }

    /**
     * Kiểm tra OTP có còn hiệu lực hay không
     *
     * @param otpCode OTP cần kiểm tra
     * @param createdAt thời điểm OTP được tạo
     * @return true nếu còn hiệu lực, false nếu quá hạn
     */
    public static boolean isValid(String otpCode, LocalDateTime createdAt) {
        if (otpCode == null || createdAt == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return createdAt.plusMinutes(EXPIRE_MINUTES).isAfter(now);
    }

    // Getter
    public String getOtpCode() {
        return otpCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "OTP: " + otpCode + " | Created At: " + createdAt;
    }
}
