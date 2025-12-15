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

    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        // Sử dụng biểu thức chính quy để kiểm tra định dạng email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    public boolean isValidPassword(String password, String confirmPassword) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        // Kiểm tra độ dài password (ví dụ: tối thiểu 6 ký tự)
        if (password.length() < 6) {
            return false;
        }

        // Kiểm tra password có chứa ít nhất một chữ cái thường, một chữ cái in hoa, một số và một ký tự đặc biệt
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$")) {
            return false;
        }

        // Kiểm tra password và confirm password có trùng nhau không
        return password.equals(confirmPassword);
    }

    public boolean isValidFullName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return false;
        }

        // Kiểm tra fullname chỉ chứa chữ cái và khoảng trắng, không chứa số hoặc ký tự đặc biệt
        if (!fullName.matches("^[a-zA-Z\\s]+$")) {
            return false;
        }

        // Kiểm tra fullname không chứa hai khoảng trắng liên tiếp
        if (fullName.contains("  ")) {
            return false;
        }

        return true;
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }

        // Kiểm tra số điện thoại chỉ chứa các chữ số và ký tự + (nếu có)
        // Ví dụ: +1234567890 hoặc 1234567890
        if (!phoneNumber.matches("^\\+?\\d+$")) {
            return false;
        }

        // Kiểm tra số điện thoại có độ dài hợp lệ
        // Ví dụ: chiều dài từ 10 đến 15 ký tự (bao gồm cả dấu +)
        if (phoneNumber.length() < 10 || phoneNumber.length() > 15) {
            return false;
        }

        return true;
    }

    public boolean isValidAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }

        // Kiểm tra address chỉ chứa chữ cái, số, khoảng trắng, dấu phẩy và dấu chấm
        if (!address.matches("^[a-zA-Z0-9\\s,\\.]+$")) {
            return false;
        }

        // Kiểm tra address không chứa hai khoảng trắng liên tiếp
        if (address.contains("  ")) {
            return false;
        }

        return true;
    }

}
