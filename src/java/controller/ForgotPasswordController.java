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
@WebServlet(name = "ForgotPasswordController", urlPatterns = {"/forgotPassword"})
public class ForgotPasswordController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email") == null ? null : request.getParameter("email").trim();

        //Kiểm tra xem dữ liệu truyền vào có rỗng không
        if (email == null || email.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập email!");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }

        UserDAO userDao = new UserDAO();

        //lỗi khi lấy thông tin người dùng
        User user = userDao.getUserByEmail(email);
        if (user == null) {
            request.setAttribute("error", "Email không tồn tại trong hệ thống.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
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

        // ===== SAVE EMAIL FOR OTP VERIFY =====
        HttpSession session = request.getSession();
        session.setAttribute("forgotPasswordEmail", email);
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

        request.setAttribute("displayEmail", maskEmail(email));
        request.setAttribute("msg", "Mã OTP đã được gửi!");
        request.getRequestDispatcher("forgot-pass-verify-otp.jsp").forward(request, response);

    }

    public String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 2) {
            return "***" + email.substring(atIndex);
        }
        return email.substring(0, 2) + "****" + email.substring(atIndex);
    }

}
