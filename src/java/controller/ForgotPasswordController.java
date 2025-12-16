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
            request.setAttribute("error", "Không thể tải thông tin người dùng.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }

        if (user.getStatus().equals("LOCKED")) {
            request.setAttribute("error", "Tài khoản đã bị khóa.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        if (user.getStatus().equals("INACTIVE")) {
            // Lưu email để verify OTP
            request.getSession().setAttribute("registerEmail", email);

            String msg = URLEncoder.encode("Đã đăng ký thành công! Vui lòng kiểm tra email để nhập OTP.", "UTF-8");
            response.sendRedirect(request.getContextPath() + "/register?msg=" + msg);
            return;
        }

        // ===== SAVE EMAIL FOR OTP VERIFY =====
        HttpSession session = request.getSession();
        session.setAttribute("forgotPasswordEmail", email);

        String msg = URLEncoder.encode(
                "Mã OTP đã được gửi!",
                "UTF-8"
        );

        response.sendRedirect(request.getContextPath() + "/forgotPasswordOTP?msg=" + msg);
    }

}
