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

/**
 *
 * @author hades
 */
@WebServlet(name = "ResendRegisterOTPController", urlPatterns = {"/resendRegisterOTP"})
public class ResendRegisterOTPController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String msg = request.getParameter("msg");

        if (msg != null) {
            request.setAttribute("msg", msg);
        }

        HttpSession session = request.getSession(false);

        // Chưa đăng ký ⇒ quay lại
        if (session == null || session.getAttribute("registerEmail") == null) {
            String error = URLEncoder.encode("Không tìm thấy Email! Vui lòng kiểm tra lại!", "UTF-8");
            response.sendRedirect("/login?error=" + error);
            return;
        }

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
        request.setAttribute("msg", "Mã OTP đã được gửi lại!");
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
