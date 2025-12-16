/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author hades
 */
public class Validation {

    /* ================= EMAIL ================= */
    public boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }

        email = email.trim();

        // Giới hạn độ dài
        if (email.length() < 5 || email.length() > 255) {
            return false;
        }

        String emailRegex
                = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        return email.matches(emailRegex);
    }

    /* ================= PASSWORD ================= */
    public boolean isValidPassword(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return false;
        }

        // Giới hạn độ dài
        if (password.length() < 8 || password.length() > 64) {
            return false;
        }

        // Ít nhất: thường, hoa, số, ký tự đặc biệt
        if (!password.matches(
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$")) {
            return false;
        }

        return password.equals(confirmPassword);
    }

    /* ================= FULL NAME ================= */
    public boolean isValidFullName(String fullName) {
        if (fullName == null) {
            return false;
        }

        fullName = fullName.trim();

        // Giới hạn độ dài
        if (fullName.length() < 2 || fullName.length() > 100) {
            return false;
        }

        // Chỉ chữ + khoảng trắng (có dấu)
        if (!fullName.matches("^[\\p{L}\\s]+$")) {
            return false;
        }

        // Không có 2 space liên tiếp
        return !fullName.contains("  ");
    }

    /* ================= PHONE ================= */
    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }

        phoneNumber = phoneNumber.trim();

        // Giới hạn độ dài
        if (phoneNumber.length() < 10 || phoneNumber.length() > 15) {
            return false;
        }

        // Chỉ số và +
        return phoneNumber.matches("^\\+?\\d+$");
    }

    /* ================= ADDRESS ================= */
    public boolean isValidAddress(String address) {
        if (address == null) {
            return false;
        }

        address = address.trim();

        // Giới hạn độ dài
        if (address.length() < 5 || address.length() > 255) {
            return false;
        }

        // Cho phép chữ, số, dấu phẩy, chấm, gạch ngang
        if (!address.matches("^[\\p{L}\\d\\s,.-]+$")) {
            return false;
        }

        return !address.contains("  ");
    }
}
