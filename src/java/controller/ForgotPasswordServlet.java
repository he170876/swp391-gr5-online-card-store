package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import service.AuthService;
import dao.UserDAO;
import util.PasswordUtil;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/auth/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {

    private AuthService authService;
    // Simple in-memory token storage (for demo - use database in production)
    private static final ConcurrentHashMap<String, String> resetTokens = new ConcurrentHashMap<>();

    @Override
    public void init() throws ServletException {
        super.init();
        authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        if (token != null && resetTokens.containsKey(token)) {
            // Show reset password form
            request.setAttribute("token", token);
            request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(request, response);
        } else {
            // Show forgot password form
            request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if ("reset".equals(action)) {
            // Handle password reset
            handleResetPassword(request, response);
        } else {
            // Handle forgot password request
            handleForgotPassword(request, response);
        }
    }

    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập email.");
            request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        if (!userDAO.emailExists(email)) {
            // Don't reveal if email exists for security
            request.setAttribute("success", "Nếu email tồn tại, chúng tôi đã gửi link đặt lại mật khẩu.");
            request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request, response);
            return;
        }

        // Generate reset token
        String token = UUID.randomUUID().toString();
        resetTokens.put(token, email);

        // In production: send email with reset link
        // For demo: just log it
        System.out.println("Reset password token for " + email + ": " + token);
        System.out.println("Reset link: " + request.getRequestURL() + "?token=" + token);

        request.setAttribute("success", "Chúng tôi đã gửi link đặt lại mật khẩu đến email của bạn. Vui lòng kiểm tra hộp thư.");
        request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request, response);
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (token == null || !resetTokens.containsKey(token)) {
            request.setAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(request, response);
            return;
        }

        if (newPassword == null || newPassword.length() < 6) {
            request.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(request, response);
            return;
        }

        String email = resetTokens.get(token);
        UserDAO userDAO = new UserDAO();
        var user = userDAO.getUserByEmail(email);
        if (user == null) {
            request.setAttribute("error", "Người dùng không tồn tại.");
            request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(request, response);
            return;
        }

        String newPasswordHash = PasswordUtil.hash(newPassword);
        if (newPasswordHash == null) {
            request.setAttribute("error", "Lỗi xử lý mật khẩu.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(request, response);
            return;
        }

        if (userDAO.updatePassword(user.getId(), newPasswordHash)) {
            resetTokens.remove(token); // Remove used token
            request.setAttribute("success", "Đặt lại mật khẩu thành công. Vui lòng đăng nhập.");
            response.sendRedirect(request.getContextPath() + "/login?success=reset");
        } else {
            request.setAttribute("error", "Đặt lại mật khẩu thất bại.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(request, response);
        }
    }
}


