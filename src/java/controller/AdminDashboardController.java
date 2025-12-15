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
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AdminDashboardController", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        OrderDAO orderDAO = new OrderDAO();
        UserDAO userDAO = new UserDAO();
        
        // Get statistics
        int totalCustomers = userDAO.countByRole(3); // CUSTOMER role
        int totalStaff = userDAO.countByRole(2); // STAFF role
        double dailyRevenue = orderDAO.getDailyRevenue();
        int ordersToday = orderDAO.getOrdersToday();
        
        // Get revenue chart data (last 7 days)
        List<Double> revenueData = orderDAO.getRevenueLast7Days();
        List<String> labels = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            labels.add("Ngày " + (i + 1));
        }
        
        // Format revenue
        DecimalFormat df = new DecimalFormat("#,###");
        
        // Create data object
        DashboardData data = new DashboardData();
        data.setTotalCustomers(totalCustomers);
        data.setTotalStaff(totalStaff);
        data.setDailyRevenue(df.format(dailyRevenue));
        data.setOrdersToday(ordersToday);
        
        ChartData chart = new ChartData();
        chart.setLabels(labels);
        chart.setData(revenueData);
        
        request.setAttribute("data", data);
        request.setAttribute("chart", chart);
        request.setAttribute("pageTitle", "Bảng điều khiển Admin");
        request.setAttribute("active", "dashboard");
        request.setAttribute("contentPage", "admin-dashboard.jsp");
        
        request.getRequestDispatcher("/admin.jsp").forward(request, response);
    }
    
    public static class DashboardData {
        private int totalCustomers;
        private int totalStaff;
        private String dailyRevenue;
        private int ordersToday;
        
        public int getTotalCustomers() { return totalCustomers; }
        public void setTotalCustomers(int totalCustomers) { this.totalCustomers = totalCustomers; }
        
        public int getTotalStaff() { return totalStaff; }
        public void setTotalStaff(int totalStaff) { this.totalStaff = totalStaff; }
        
        public String getDailyRevenue() { return dailyRevenue; }
        public void setDailyRevenue(String dailyRevenue) { this.dailyRevenue = dailyRevenue; }
        
        public int getOrdersToday() { return ordersToday; }
        public void setOrdersToday(int ordersToday) { this.ordersToday = ordersToday; }
    }
    
    public static class ChartData {
        private List<String> labels;
        private List<Double> data;
        
        public List<String> getLabels() { return labels; }
        public void setLabels(List<String> labels) { this.labels = labels; }
        
        public List<Double> getData() { return data; }
        public void setData(List<Double> data) { this.data = data; }
    }
}

