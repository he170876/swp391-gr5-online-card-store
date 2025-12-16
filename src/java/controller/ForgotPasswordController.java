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
import model.SendOTPResult;
import model.User;
import service.SendOTPService;

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
        session.setAttribute("forgotPasswordEmail", user.getEmail());

        // ===== CREATE OTP AND SEND EMAIL FOR OTP VERIFY =====
        SendOTPService otpService = new SendOTPService();
        SendOTPResult result = otpService.sendForgotPasswordOTP(user);

        request.setAttribute("maskedEmail", maskEmail(user.getEmail()));

        if (!result.isSuccess()) {
            request.setAttribute("error", result.getMessage());
            request.getRequestDispatcher("forgot-pass-verify-otp.jsp").forward(request, response);
            return;
        }

        request.setAttribute("msg", result.getMessage());
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
