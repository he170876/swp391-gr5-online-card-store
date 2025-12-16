/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.UserDAO;
import dao.UserOTPDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import model.User;
import model.UserOTP;
import service.EmailService;
import util.OTPGenerator;
import util.PasswordUtil;
import util.Validation;

/**
 *
 * @author hades
 */
@WebServlet(name = "RegisterController", urlPatterns = {"/register"})
public class RegisterController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String fullName = request.getParameter("fullName") == null ? null : request.getParameter("fullName").trim();
        String email = request.getParameter("email") == null ? null : request.getParameter("email").trim();
        String phone = request.getParameter("phone") == null ? null : request.getParameter("phone").trim();
        String address = request.getParameter("address") == null ? null : request.getParameter("address").trim();
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        Validation v = new Validation();
        Map<String, String> errors = new HashMap<>();

        // ===== VALIDATE EMAIL =====
        if (email == null || email.isEmpty()) {
            errors.put("email", "Email không được để trống");
        } else if (!v.isValidEmail(email)) {
            errors.put("email", "Định dạng email không hợp lệ");
        }

        // ===== VALIDATE PASSWORD =====
        if (password == null || password.isEmpty()) {
            errors.put("password", "Mật khẩu không được để trống");
        } else if (!v.isValidPassword(password, confirmPassword)) {
            errors.put("password",
                    "Mật khẩu phải có ít nhất 6 ký tự, bao gồm chữ hoa, chữ thường, số, ký tự đặc biệt và trùng khớp xác nhận");
        }

        // ===== VALIDATE FULL NAME =====
        if (fullName == null || fullName.isEmpty()) {
            errors.put("fullName", "Họ và tên không được để trống");
        } else if (!v.isValidFullName(fullName)) {
            errors.put("fullName", "Họ và tên không hợp lệ. Chỉ chứa chữ cái và khoảng trắng");
        }

        // ===== VALIDATE PHONE =====
        if (phone != null && !phone.isEmpty() && !v.isValidPhoneNumber(phone)) {
            errors.put("phone", "Số điện thoại không hợp lệ");
        }

        // ===== VALIDATE ADDRESS =====
        if (address != null && !address.isEmpty() && !v.isValidAddress(address)) {
            errors.put("address", "Địa chỉ không hợp lệ");
        }

        UserDAO dao = new UserDAO();

        // ===== CHECK EMAIL EXIST =====
        if (!errors.containsKey("email")) {
            User existing = dao.getUserByEmail(email);
            if (existing != null) {
                errors.put("email", "Email đã tồn tại");
            }
        }

        // ===== RETURN FORM IF ERROR =====
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("fullNameValue", fullName);
            request.setAttribute("emailValue", email);
            request.setAttribute("phoneValue", phone);
            request.setAttribute("addressValue", address);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // ===== HASH PASSWORD =====
        String hashedPassword = PasswordUtil.hash(password);
        if (hashedPassword == null) {
            errors.put("general", "Lỗi mã hóa mật khẩu");
            request.setAttribute("errors", errors);
            request.setAttribute("fullNameValue", fullName);
            request.setAttribute("emailValue", email);
            request.setAttribute("phoneValue", phone);
            request.setAttribute("addressValue", address);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // ===== CREATE USER =====
        User u = new User();
        u.setFullName(fullName);
        u.setEmail(email);
        u.setPasswordHash(hashedPassword);
        u.setPhone(phone);
        u.setAddress(address);
        u.setRoleId(3); // CUSTOMER

        boolean inserted = dao.insertUser(u);

        if (!inserted) {
            errors.put("general", "Không thể tạo tài khoản, vui lòng thử lại sau");
            request.setAttribute("errors", errors);
            request.setAttribute("fullNameValue", fullName);
            request.setAttribute("emailValue", email);
            request.setAttribute("phoneValue", phone);
            request.setAttribute("addressValue", address);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // ===== SAVE EMAIL FOR OTP VERIFY =====
        HttpSession session = request.getSession();
        session.setAttribute("registerEmail", email);

        String registerEmail = (String) session.getAttribute("registerEmail");
        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByEmail(registerEmail);

        if (user == null) {
            String error = URLEncoder.encode("Không tìm thấy Người dùng! Vui lòng kiểm tra lại!", "UTF-8");
            response.sendRedirect("/login?error=" + error);
            return;
        }

        if (user.getStatus().equals("LOCKED")) {
            String error = URLEncoder.encode("Tài khoản đã bị khóa! Vui lòng liên hệ Admin!", "UTF-8");
            response.sendRedirect("/login?error=" + error);
            return;
        }

        if (user.getStatus().equals("ACTIVE")) {
            String error = URLEncoder.encode("Tài khoản đã xác thực! Vui lòng đăng nhập!", "UTF-8");
            response.sendRedirect("/login?error=" + error);
            return;
        }

        session.setAttribute("registerUserId", user.getId());
        request.setAttribute("maskedEmail", maskEmail(registerEmail));

        UserOTPDAO otpDAO = new UserOTPDAO();
        UserOTP lastOtp = otpDAO.getByUserId(user.getId());

        if (lastOtp != null) {
            LocalDateTime now = LocalDateTime.now();

            // --- Delay 60 giây ---
            if (lastOtp.getLastSend().plusSeconds(60).isAfter(now)) {
                request.setAttribute("error", "Vui lòng đợi 60 giây trước khi gửi lại OTP!");
                request.getRequestDispatcher("register-verify-otp.jsp").forward(request, response);
                return;
            }

            // --- Kiểm tra gửi quá 5 lần ---
            if (lastOtp.getSendCount() >= 5) {

                LocalDateTime blockUntil = lastOtp.getLastSend().plusMinutes(30);

                // Nếu CHƯA đủ 30 phút → chặn và hiển thị thời gian còn lại
                if (blockUntil.isAfter(now)) {

                    long minutesLeft = java.time.Duration.between(now, blockUntil).toMinutes();
                    long secondsLeft = java.time.Duration.between(now, blockUntil).getSeconds() % 60;

                    String error = "Bạn đã gửi quá nhiều lần! "
                            + "Vui lòng đợi " + minutesLeft + " phút " + secondsLeft + " giây trước khi gửi lại OTP!";

                    request.setAttribute("error", error);
                    request.getRequestDispatcher("register-verify-otp.jsp").forward(request, response);
                    return;
                }

                // Đủ 30 phút rồi → reset
                otpDAO.deleteOTP(user.getId());
            }

        }

        OTPGenerator otpGen = new OTPGenerator();
        String otp = otpGen.getOtpCode();

        UserOTP userOTP = new UserOTP();
        userOTP.setUserId(user.getId());
        userOTP.setOtpCode(otp);

        otpDAO.insertOrUpdate(userOTP);

        // --- Gửi OTP qua email ---
        try {
            EmailService.sendEmail(
                    user.getEmail(),
                    "Xác minh tài khoản",
                    "Mã OTP của bạn là: " + otp + "\nCó hiệu lực trong 5 phút."
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            String error = URLEncoder.encode("Không thể gửi OTP. Vui lòng thử lại!", "UTF-8");
            response.sendRedirect("/register?error=" + error);
            return;
        }

        request.setAttribute("msg", "Mã OTP đã được gửi!");
        request.getRequestDispatcher("register-verify-otp.jsp").forward(request, response);
    }

    public String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 2) {
            return "***" + email.substring(atIndex);
        }
        return email.substring(0, 2) + "****" + email.substring(atIndex);
    }
}
