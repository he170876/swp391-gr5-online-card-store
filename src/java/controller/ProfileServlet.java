package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.User;
import service.AuthService;

@WebServlet(name = "ProfileServlet", urlPatterns = {"/auth/profile"})
public class ProfileServlet extends HttpServlet {

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
        // Refresh user data
        user = authService.getUserById(user.getId());
        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/auth/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        // Validation
        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập họ tên.");
            user = authService.getUserById(user.getId());
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/auth/profile.jsp").forward(request, response);
            return;
        }

        // Update profile
        if (authService.updateProfile(user.getId(), fullName, phone, address)) {
            request.setAttribute("success", "Cập nhật thông tin thành công.");
            // Refresh user in session
            user = authService.getUserById(user.getId());
            request.getSession().setAttribute("user", user);
        } else {
            request.setAttribute("error", "Cập nhật thông tin thất bại.");
        }

        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/auth/profile.jsp").forward(request, response);
    }
}


