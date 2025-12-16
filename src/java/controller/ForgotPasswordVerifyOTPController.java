/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

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
import service.EmailService;
import util.OTPGenerator;
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
        if (session == null || session.getAttribute("forgotPasswordUserId") == null) {
            String error = URLEncoder.encode("Không tìm thấy Id! Vui lòng kiểm tra lại!", "UTF-8");
            response.sendRedirect("/forgotPassword?error=" + error);
            return;
        }

        Long userId = (Long) session.getAttribute("forgotPasswordUserId");
        String otpInput = request.getParameter("otp");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserById(userId);
        Validation v = new Validation();

        if (user == null) {
            String error = URLEncoder.encode("Không tìm thấy người dùng!", "UTF-8");
            response.sendRedirect("/forgotPassword?error=" + error);
            return;
        }

        UserOTPDAO otpDAO = new UserOTPDAO();
        UserOTP userOtp = otpDAO.getByUserId(userId);

        if (userOtp == null) {
            request.setAttribute("error", "Mã OTP không tồn tại! Vui lòng gửi lại OTP!");
            request.setAttribute("maskedEmail", maskEmail((String) session.getAttribute("forgotPasswordEmail")));
            request.getRequestDispatcher("forgot-pass-verify-otp.jsp").forward(request, response);
            return;
        }

        // Kiểm tra OTP hết hạn 5 phút
        LocalDateTime now = LocalDateTime.now();
        if (userOtp.getLastSend().plusMinutes(5).isBefore(now)) {
            request.setAttribute("error", "Mã OTP đã hết hạn! Vui lòng gửi lại!");
            request.setAttribute("maskedEmail", maskEmail((String) session.getAttribute("forgotPasswordEmail")));
            request.getRequestDispatcher("forgot-pass-verify-otp.jsp").forward(request, response);
            return;
        }

        // Kiểm tra chính xác mã OTP
        if (otpInput == null || !otpInput.equals(userOtp.getOtpCode())) {
            request.setAttribute("error", "Mã OTP không chính xác!");
            request.setAttribute("maskedEmail", maskEmail((String) session.getAttribute("forgotPasswordEmail")));
            request.getRequestDispatcher("forgot-pass-verify-otp.jsp").forward(request, response);
            return;
        }

        // ===== VALIDATE PASSWORD =====
        if (newPassword == null || newPassword.isEmpty()) {
            request.setAttribute("error", "Mật khẩu không được để trống!");
            request.setAttribute("maskedEmail", maskEmail((String) session.getAttribute("forgotPasswordEmail")));
            request.getRequestDispatcher("forgot-pass-verify-otp.jsp").forward(request, response);
            return;
        } else if (!v.isValidPassword(newPassword, confirmPassword)) {
            request.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự, bao gồm chữ hoa, chữ thường, số, ký tự đặc biệt và trùng khớp xác nhận!");
            request.setAttribute("maskedEmail", maskEmail((String) session.getAttribute("forgotPasswordEmail")));
            request.getRequestDispatcher("forgot-pass-verify-otp.jsp").forward(request, response);
            return;
        }

        // ===== HASH PASSWORD =====
        String hashedPassword = PasswordUtil.hash(newPassword);
        if (hashedPassword == null) {
            request.setAttribute("error", "Lỗi mã hóa mật khẩu!");
            request.setAttribute("maskedEmail", maskEmail((String) session.getAttribute("forgotPasswordEmail")));
            request.getRequestDispatcher("forgot-pass-verify-otp.jsp").forward(request, response);
            return;
        }

        boolean update = userDAO.resetPassword(userId, hashedPassword);

        if (!update) {
            request.setAttribute("error", "Lỗi cập nhật mật khẩu!");
            request.setAttribute("maskedEmail", maskEmail((String) session.getAttribute("forgotPasswordEmail")));
            request.getRequestDispatcher("forgot-pass-verify-otp.jsp").forward(request, response);
            return;
        }

        otpDAO.deleteOTP(userId);
        session.removeAttribute("forgotPasswordEmail");
        session.removeAttribute("forgotPasswordUserId");

        // Điều hướng đến login
        String success = URLEncoder.encode("Đổi mật khẩu thành công! Vui lòng đăng nhập!", "UTF-8");
        response.sendRedirect(request.getContextPath() + "/login?success=" + success);

    }

    public String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 2) {
            return "***" + email.substring(atIndex);
        }
        return email.substring(0, 2) + "****" + email.substring(atIndex);
    }

}
