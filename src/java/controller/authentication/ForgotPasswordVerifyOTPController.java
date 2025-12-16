/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.authentication;

import dao.UserDAO;
import dao.UserOTPDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import model.User;
import model.UserOTP;
import util.PasswordUtil;
import util.Validation;

/**
 *
 * @author hades
 */
@WebServlet(name = "ForgotPasswordVerifyOTPController", urlPatterns = {"/forgotPasswordOTP"})
public class ForgotPasswordVerifyOTPController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        // Kiểm tra session tồn tại
        if (session == null || session.getAttribute("forgotPasswordEmail") == null) {
            String error = URLEncoder.encode("Phiên làm việc đã hết hạn! Vui lòng thực hiện lại.!", "UTF-8");
            response.sendRedirect("/forgotPassword?error=" + error);
            return;
        }

        String forgotPasswordEmail = (String) session.getAttribute("forgotPasswordEmail");
        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByEmail(forgotPasswordEmail);
        Validation v = new Validation();

        if (user == null) {
            String error = URLEncoder.encode("Không tìm thấy người dùng!", "UTF-8");
            response.sendRedirect("/forgotPassword?error=" + error);
            return;
        }

        if (user.getStatus().equals("LOCKED")) {
            request.setAttribute("error", "Tài khoản đã bị khóa.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        if (user.getStatus().equals("INACTIVE")) {
            request.setAttribute("error", "Tài khoản chưa được kích hoạt.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }

        Long userId = user.getId();
        String otpInput = request.getParameter("otp");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        UserOTPDAO otpDAO = new UserOTPDAO();
        UserOTP userOtp = otpDAO.getByUserId(userId);

        // ===== CHECK OTP EXISTS =====
        if (userOtp == null) {
            forwardWithError(request, response,
                    "Mã OTP không tồn tại. Vui lòng gửi lại mã OTP.", user);
            return;
        }

        // ===== CHECK OTP EXPIRED (5 minutes) =====
        LocalDateTime now = LocalDateTime.now();
        if (userOtp.getLastSend().plusMinutes(5).isBefore(now)) {
            forwardWithError(request, response,
                    "Mã OTP đã hết hạn. Vui lòng gửi lại mã OTP mới.", user);
            return;
        }

        // ===== CHECK OTP INPUT =====
        if (otpInput == null || otpInput.trim().isEmpty()) {
            forwardWithError(request, response,
                    "Vui lòng nhập mã OTP.", user);
            return;
        }

        // ===== CHECK OTP MATCH =====
        if (!otpInput.equals(userOtp.getOtpCode())) {
            forwardWithError(request, response,
                    "Mã OTP không chính xác.", user);
            return;
        }

        // ===== VALIDATE PASSWORD =====
        if (newPassword == null || newPassword.trim().isEmpty()) {
            forwardWithError(request, response,
                    "Mật khẩu không được để trống.", user);
            return;
        }

        if (newPassword.length() < 8 || newPassword.length() > 64) {
            forwardWithError(request, response,
                    "Mật khẩu phải có độ dài từ 8 đến 64 ký tự.", user);
            return;
        }

        if (!v.isValidPassword(newPassword, confirmPassword)) {
            forwardWithError(request, response,
                    "Mật khẩu phải gồm chữ hoa, chữ thường, số, ký tự đặc biệt và trùng khớp xác nhận.",
                    user);
            return;
        }

        // ===== CHECK NEW PASSWORD != OLD PASSWORD =====
        if (PasswordUtil.check(newPassword, user.getPasswordHash())) {
            forwardWithError(request, response,
                    "Mật khẩu mới phải khác mật khẩu hiện tại.", user);
            return;
        }

        // ===== HASH PASSWORD =====
        String hashedPassword = PasswordUtil.hash(newPassword);
        if (hashedPassword == null) {
            forwardWithError(request, response,
                    "Lỗi hệ thống khi mã hóa mật khẩu. Vui lòng thử lại.", user);
            return;
        }

        // ===== UPDATE PASSWORD =====
        boolean update = userDAO.resetPassword(userId, hashedPassword);
        if (!update) {
            forwardWithError(request, response,
                    "Không thể cập nhật mật khẩu. Vui lòng thử lại sau.", user);
            return;
        }

        otpDAO.deleteOTP(userId);
        session.removeAttribute("forgotPasswordEmail");

        // Điều hướng đến login
        String success = URLEncoder.encode("Đổi mật khẩu thành công! Vui lòng đăng nhập!", "UTF-8");
        response.sendRedirect(request.getContextPath() + "/login?success=" + success);

    }

    private void forwardWithError(HttpServletRequest request,
            HttpServletResponse response,
            String message,
            User user) throws ServletException, IOException {

        request.setAttribute("error", message);
        request.setAttribute("maskedEmail", maskEmail(user.getEmail()));
        request.getRequestDispatcher("forgot-pass-verify-otp.jsp")
                .forward(request, response);
    }

    public String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 2) {
            return "***" + email.substring(atIndex);
        }
        return email.substring(0, 2) + "****" + email.substring(atIndex);
    }

}
