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

/**
 *
 * @author hades
 */
@WebServlet(name = "RegisterVerifyOTPController", urlPatterns = {"/registerVerifyOTP"})
public class RegisterVerifyOTPController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // Kiểm tra session tồn tại
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

        Long userId = user.getId();
        String otpInput = request.getParameter("otp");

        if (otpInput == null || otpInput.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mã OTP!");
            request.setAttribute("maskedEmail", maskEmail((String) session.getAttribute("registerEmail")));
            request.getRequestDispatcher("register-verify-otp.jsp").forward(request, response);
            return;
        }

        UserOTPDAO otpDAO = new UserOTPDAO();
        UserOTP userOtp = otpDAO.getByUserId(userId);

        if (userOtp == null) {
            request.setAttribute("error", "Mã OTP không tồn tại! Vui lòng gửi lại OTP!");
            request.setAttribute("maskedEmail", maskEmail((String) session.getAttribute("registerEmail")));
            request.getRequestDispatcher("register-verify-otp.jsp").forward(request, response);
            return;
        }

        // Kiểm tra OTP hết hạn 5 phút
        LocalDateTime now = LocalDateTime.now();
        if (userOtp.getLastSend().plusMinutes(5).isBefore(now)) {
            request.setAttribute("error", "Mã OTP đã hết hạn! Vui lòng gửi lại!");
            request.setAttribute("maskedEmail", maskEmail((String) session.getAttribute("registerEmail")));
            request.getRequestDispatcher("register-verify-otp.jsp").forward(request, response);
            return;
        }

        // Kiểm tra chính xác mã OTP
        if (!otpInput.equals(userOtp.getOtpCode())) {
            request.setAttribute("error", "Mã OTP không chính xác!");
            request.setAttribute("maskedEmail", maskEmail((String) session.getAttribute("registerEmail")));
            request.getRequestDispatcher("register-verify-otp.jsp").forward(request, response);
            return;
        }

        userDAO.activateUser(user.getId());

        // Xoá OTP để tránh dùng lại
        otpDAO.deleteOTP(userId);

        // Xoá session đăng ký
        session.removeAttribute("registerEmail");

        // Điều hướng đến login
        String success = URLEncoder.encode("Xác thực thành công! Vui lòng đăng nhập!", "UTF-8");
        response.sendRedirect("/login?success=" + success);
    }

    public String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 2) {
            return "***" + email.substring(atIndex);
        }
        return email.substring(0, 2) + "****" + email.substring(atIndex);
    }

}
