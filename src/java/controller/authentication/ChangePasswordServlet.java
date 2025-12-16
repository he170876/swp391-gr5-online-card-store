package controller.authentication;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.User;
import service.AuthService;

@WebServlet(name = "ChangePasswordServlet", urlPatterns = {"/auth/change-password"})
public class ChangePasswordServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        super.init();
        authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/auth/change-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validation
        if (oldPassword == null || oldPassword.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mật khẩu cũ.");
            request.getRequestDispatcher("/WEB-INF/views/auth/change-password.jsp").forward(request, response);
            return;
        }

        if (newPassword == null || newPassword.length() < 6) {
            request.setAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            request.getRequestDispatcher("/WEB-INF/views/auth/change-password.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
            request.getRequestDispatcher("/WEB-INF/views/auth/change-password.jsp").forward(request, response);
            return;
        }

        // Change password
        if (authService.changePassword(user.getId(), oldPassword, newPassword)) {
            request.setAttribute("success", "Đổi mật khẩu thành công.");
            // Refresh user in session
            user = authService.getUserById(user.getId());
            request.getSession().setAttribute("user", user);
        } else {
            request.setAttribute("error", "Mật khẩu cũ không đúng hoặc có lỗi xảy ra.");
        }

        request.getRequestDispatcher("/WEB-INF/views/auth/change-password.jsp").forward(request, response);
    }
}


