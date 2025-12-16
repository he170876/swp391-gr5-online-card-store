/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.authentication;

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
import model.SendOTPResult;
import model.User;
import service.SendOTPService;

/**
 *
 * @author hades
 */
@WebServlet(name = "ResendForgotPassOTPController", urlPatterns = {"/resendForgotPassOtp"})
public class ResendForgotPassOTPController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // ===== CHECK SESSION =====
        if (session == null || session.getAttribute("forgotPasswordEmail") == null) {
            String error = URLEncoder.encode(
                    "Phiên làm việc đã hết hạn! Vui lòng thực hiện lại.",
                    "UTF-8"
            );
            response.sendRedirect("/forgotPassword?error=" + error);
            return;
        }

        String email = (String) session.getAttribute("forgotPasswordEmail");
        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByEmail(email);

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

        // ===== SEND OTP VIA SERVICE =====
        SendOTPService otpService = new SendOTPService();
        SendOTPResult result = otpService.sendForgotPasswordOTP(user);

        request.setAttribute("maskedEmail", maskEmail(email));

        if (!result.isSuccess()) {
            request.setAttribute("error", result.getMessage());
            request.getRequestDispatcher("forgot-pass-verify-otp.jsp")
                    .forward(request, response);
            return;
        }

        request.setAttribute("msg", result.getMessage());
        request.getRequestDispatcher("forgot-pass-verify-otp.jsp")
                .forward(request, response);
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 2) {
            return "***" + email.substring(atIndex);
        }
        return email.substring(0, 2) + "****" + email.substring(atIndex);
    }
}
