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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String msg = request.getParameter("msg");

        if (msg != null) {
            request.setAttribute("msg", msg);
        }

        HttpSession session = request.getSession(false);

        // Chưa đăng ký ⇒ quay lại
        if (session == null || session.getAttribute("forgotPasswordEmail") == null) {
            String error = URLEncoder.encode("Không tìm thấy Email! Vui lòng kiểm tra lại!", "UTF-8");
            response.sendRedirect("/forgotPassword?error=" + error);
            return;
        }

        String forgotPasswordEmail = (String) session.getAttribute("forgotPasswordEmail");
        request.setAttribute("displayEmail", maskEmail(forgotPasswordEmail));

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByEmail(forgotPasswordEmail);
        session.setAttribute("forgotPasswordUserId", user.getId());

        UserOTPDAO otpDAO = new UserOTPDAO();
        UserOTP lastOtp = otpDAO.getByUserId(user.getId());

        if (lastOtp != null) {
            LocalDateTime now = LocalDateTime.now();

            // --- Delay 60 giây ---
            if (lastOtp.getLastSend().plusSeconds(60).isAfter(now)) {
                request.setAttribute("error", "Vui lòng đợi 60 giây trước khi gửi lại OTP!");
                request.getRequestDispatcher("forgot-pass-verify-otp.jsp").forward(request, response);
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
                    request.getRequestDispatcher("forgot-pass-verify-otp.jsp").forward(request, response);
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
            response.sendRedirect("/forgotPassword?error=" + error);
            return;
        }

        request.getRequestDispatcher("forgot-pass-verify-otp.jsp").forward(request, response);
    }

    public String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 2) {
            return "***" + email.substring(atIndex);
        }
        return email.substring(0, 2) + "****" + email.substring(atIndex);
    }

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

}
