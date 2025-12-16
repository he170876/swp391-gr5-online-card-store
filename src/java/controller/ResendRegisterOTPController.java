/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import model.User;
import model.SendOTPResult;
import service.SendOTPService;

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

        // ===== RESEND OTP USING SERVICE =====
        SendOTPService otpService = new SendOTPService();
        SendOTPResult result = otpService.sendRegisterOTP(user);

        if (!result.isSuccess()) {
            request.setAttribute("error", result.getMessage());
            request.getRequestDispatcher("register-verify-otp.jsp")
                    .forward(request, response);
            return;
        }

        request.setAttribute("maskedEmail", maskEmail(user.getEmail()));
        request.setAttribute("msg", result.getMessage());
        request.getRequestDispatcher("register-verify-otp.jsp")
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
