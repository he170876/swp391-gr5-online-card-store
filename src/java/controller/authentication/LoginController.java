/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.authentication;

import controller.AdminConfigController;
import controller.AdminConfigController;
import dao.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import model.User;

/**
 *
 * @author hades
 */
@WebServlet(name = "LoginController", urlPatterns = {"/login"})
public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = Optional.ofNullable(request.getParameter("email")).orElse("");
        String password = Optional.ofNullable(request.getParameter("password")).orElse("");

        request.setAttribute("email", email);

        // Kiểm tra dữ liệu đầu vào
        if (email.isEmpty() || password.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập email và mật khẩu.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        UserDAO userDao = new UserDAO();
        User user = userDao.getUserByEmail(email);

        // Kiểm tra user tồn tại và password đúng bằng BCrypt
        if (user == null || !util.PasswordUtil.check(password, user.getPasswordHash())) {
            request.setAttribute("error", "Email hoặc mật khẩu không đúng.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // Kiểm tra trạng thái tài khoản
        if ("LOCKED".equals(user.getStatus())) {
            request.setAttribute("error", "Tài khoản đã bị khóa.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        if ("INACTIVE".equals(user.getStatus())) {
            request.getSession().setAttribute("registerEmail", email);

            String msgs = java.net.URLEncoder.encode(
                    "Đã đăng ký thành công! Vui lòng kiểm tra email để nhập OTP.", "UTF-8");
            response.sendRedirect(request.getContextPath() + "/resendRegisterOTP?msgs=" + msgs);
            return;
        }

        // Check maintenance mode
        boolean maintenanceMode = AdminConfigController.isMaintenanceMode();
        if (maintenanceMode && user.getRoleId() != 1) {
            request.setAttribute("error", "Hệ thống đang bảo trì. Chỉ quản trị viên mới có thể đăng nhập.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // Gửi thông tin user lên session
        request.getSession().setAttribute("user", user);

        // Redirect theo role
        if (user.getRoleId() == 3) {
            response.sendRedirect(request.getContextPath() + "/customer/home");
        } else {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }

}
