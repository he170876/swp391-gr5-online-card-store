package controller;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AdminDashboardController", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        UserDAO userDAO = new UserDAO();
        
        // Get statistics
        int totalCustomers = userDAO.countByRole(3); // CUSTOMER role
        int totalStaff = userDAO.countByRole(2); // STAFF role
        
        // Create data object
        DashboardData data = new DashboardData();
        data.setTotalCustomers(totalCustomers);
        data.setTotalStaff(totalStaff);
        
        request.setAttribute("data", data);
        request.setAttribute("pageTitle", "Bảng điều khiển Admin");
        request.setAttribute("active", "dashboard");
        request.setAttribute("contentPage", "admin-dashboard.jsp");
        
        request.getRequestDispatcher("/admin.jsp").forward(request, response);
    }
    
    public static class DashboardData {
        private int totalCustomers;
        private int totalStaff;
        
        public int getTotalCustomers() { return totalCustomers; }
        public void setTotalCustomers(int totalCustomers) { this.totalCustomers = totalCustomers; }
        
        public int getTotalStaff() { return totalStaff; }
        public void setTotalStaff(int totalStaff) { this.totalStaff = totalStaff; }
    }
}

