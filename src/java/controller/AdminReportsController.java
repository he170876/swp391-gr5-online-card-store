package controller;

import dao.OrderDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

@WebServlet(name = "AdminReportsController", urlPatterns = {"/admin/reports"})
public class AdminReportsController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        OrderDAO orderDAO = new OrderDAO();
        UserDAO userDAO = new UserDAO();
        DecimalFormat df = new DecimalFormat("#,###");
        
        // Get statistics
        int totalCustomers = userDAO.countByRole(3);
        int totalStaff = userDAO.countByRole(2);
        double dailyRevenue = orderDAO.getDailyRevenue();
        int ordersToday = orderDAO.getOrdersToday();
        
        // Get revenue data for last 30 days
        List<Double> revenueData = orderDAO.getRevenueLast7Days(); // Can be extended to 30 days
        
        request.setAttribute("totalCustomers", totalCustomers);
        request.setAttribute("totalStaff", totalStaff);
        request.setAttribute("dailyRevenue", df.format(dailyRevenue));
        request.setAttribute("ordersToday", ordersToday);
        request.setAttribute("revenueData", revenueData);
        request.setAttribute("pageTitle", "Báo cáo & Thống kê");
        request.setAttribute("active", "reports");
        request.setAttribute("contentPage", "admin-reports.jsp");
        
        request.getRequestDispatcher("/admin.jsp").forward(request, response);
    }
}

