package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.User;
import service.AuthService;

@WebServlet(name = "AdminChangePasswordController", urlPatterns = {"/admin/change-password"})
public class AdminChangePasswordController extends HttpServlet {

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
        
        if (user == null || user.getRoleId() != 1) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Get messages from session if any
        String successMessage = (String) request.getSession().getAttribute("successMessage");
        String errorMessage = (String) request.getSession().getAttribute("errorMessage");
        
        if (successMessage != null) {
            request.setAttribute("successMessage", successMessage);
            request.getSession().removeAttribute("successMessage");
        }
        
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            request.getSession().removeAttribute("errorMessage");
        }
        
        request.setAttribute("pageTitle", "Đổi mật khẩu");
        request.setAttribute("active", "change-password");
        request.setAttribute("contentPage", "admin-change-password.jsp");
        
        request.getRequestDispatcher("/admin.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        User user = (User) request.getSession().getAttribute("user");
        
        if (user == null || user.getRoleId() != 1) {
            request.getSession().setAttribute("errorMessage", "Bạn không có quyền thực hiện thao tác này.");
            response.sendRedirect(request.getContextPath() + "/admin/change-password");
            return;
        }
        
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validation
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            request.getSession().setAttribute("errorMessage", "Vui lòng nhập mật khẩu hiện tại.");
            response.sendRedirect(request.getContextPath() + "/admin/change-password");
            return;
        }
        
        if (newPassword == null || newPassword.trim().length() < 6) {
            request.getSession().setAttribute("errorMessage", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            response.sendRedirect(request.getContextPath() + "/admin/change-password");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            request.getSession().setAttribute("errorMessage", "Mật khẩu xác nhận không khớp.");
            response.sendRedirect(request.getContextPath() + "/admin/change-password");
            return;
        }
        
        if (oldPassword.equals(newPassword)) {
            request.getSession().setAttribute("errorMessage", "Mật khẩu mới phải khác mật khẩu hiện tại.");
            response.sendRedirect(request.getContextPath() + "/admin/change-password");
            return;
        }
        
        // Change password using AuthService
        if (authService.changePassword(user.getId(), oldPassword, newPassword)) {
            request.getSession().setAttribute("successMessage", "Đổi mật khẩu thành công!");
            // Refresh user in session
            user = authService.getUserById(user.getId());
            request.getSession().setAttribute("user", user);
        } else {
            request.getSession().setAttribute("errorMessage", "Mật khẩu hiện tại không đúng hoặc có lỗi xảy ra.");
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/change-password");
    }
}

