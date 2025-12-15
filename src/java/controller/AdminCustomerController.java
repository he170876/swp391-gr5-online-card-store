package controller;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import model.User;

@WebServlet(name = "AdminCustomerController", urlPatterns = {
    "/admin/customer",
    "/admin/customer/block",
    "/admin/customer/unblock"
})
public class AdminCustomerController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        UserDAO userDAO = new UserDAO();
        
        if (path.endsWith("/block")) {
            // Block customer
            String idParam = request.getParameter("id");
            String pageParam = request.getParameter("page");
            String keywordParam = request.getParameter("keyword");
            String statusParam = request.getParameter("status");
            
            if (idParam != null) {
                try {
                    long id = Long.parseLong(idParam);
                    User user = userDAO.findById(id);
                    if (user != null && user.getRoleId() == 3) { // Only customers
                        user.setStatus("LOCKED");
                        userDAO.update(user);
                    }
                } catch (Exception e) {
                    System.out.println("AdminCustomerController.block: " + e.getMessage());
                }
            }
            
            // Preserve pagination and search parameters
            StringBuilder redirectUrl = new StringBuilder(request.getContextPath() + "/admin/customer");
            StringBuilder queryParams = new StringBuilder();
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                queryParams.append("page=").append(pageParam);
            }
            if (keywordParam != null && !keywordParam.trim().isEmpty()) {
                if (queryParams.length() > 0) queryParams.append("&");
                queryParams.append("keyword=").append(URLEncoder.encode(keywordParam, StandardCharsets.UTF_8));
            }
            if (statusParam != null && !statusParam.trim().isEmpty()) {
                if (queryParams.length() > 0) queryParams.append("&");
                queryParams.append("status=").append(statusParam);
            }
            if (queryParams.length() > 0) {
                redirectUrl.append("?").append(queryParams);
            }
            response.sendRedirect(redirectUrl.toString());
            return;
        }
        
        if (path.endsWith("/unblock")) {
            // Unblock customer
            String idParam = request.getParameter("id");
            String pageParam = request.getParameter("page");
            String keywordParam = request.getParameter("keyword");
            String statusParam = request.getParameter("status");
            
            if (idParam != null) {
                try {
                    long id = Long.parseLong(idParam);
                    User user = userDAO.findById(id);
                    if (user != null && user.getRoleId() == 3) { // Only customers
                        user.setStatus("ACTIVE");
                        userDAO.update(user);
                    }
                } catch (Exception e) {
                    System.out.println("AdminCustomerController.unblock: " + e.getMessage());
                }
            }
            
            // Preserve pagination and search parameters
            StringBuilder redirectUrl = new StringBuilder(request.getContextPath() + "/admin/customer");
            StringBuilder queryParams = new StringBuilder();
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                queryParams.append("page=").append(pageParam);
            }
            if (keywordParam != null && !keywordParam.trim().isEmpty()) {
                if (queryParams.length() > 0) queryParams.append("&");
                queryParams.append("keyword=").append(URLEncoder.encode(keywordParam, StandardCharsets.UTF_8));
            }
            if (statusParam != null && !statusParam.trim().isEmpty()) {
                if (queryParams.length() > 0) queryParams.append("&");
                queryParams.append("status=").append(statusParam);
            }
            if (queryParams.length() > 0) {
                redirectUrl.append("?").append(queryParams);
            }
            response.sendRedirect(redirectUrl.toString());
            return;
        }
        
        // Get search parameters
        String keyword = request.getParameter("keyword");
        String statusFilter = request.getParameter("status");
        
        // Get pagination parameters
        int page = 1;
        int pageSize = 5; // Default: 5 records per page
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            }
            String pageSizeParam = request.getParameter("pageSize");
            if (pageSizeParam != null && !pageSizeParam.trim().isEmpty()) {
                pageSize = Integer.parseInt(pageSizeParam);
                if (pageSize < 5) pageSize = 5;
                if (pageSize > 100) pageSize = 100;
            }
        } catch (NumberFormatException e) {
            // Use default values
        }
        
        // If keyword is empty, set to null
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }
        
        // If status is "ALL" or empty, set to null
        String status = null;
        if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equals(statusFilter)) {
            status = statusFilter;
        }
        
        // Calculate offset
        int offset = (page - 1) * pageSize;
        
        // Get total count for pagination
        int totalRecords;
        if (keyword != null || status != null) {
            totalRecords = userDAO.countSearch(keyword, 3L, status);
        } else {
            totalRecords = userDAO.countByRole(3);
        }
        
        int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / pageSize));
        
        // Search customers with filters and pagination
        List<User> customers;
        if (keyword != null || status != null) {
            customers = userDAO.search(keyword, 3L, status, offset, pageSize);
        } else {
            customers = userDAO.findByRole(3, offset, pageSize);
        }
        
        // Convert to view model
        List<CustomerView> customerViews = new java.util.ArrayList<>();
        for (User u : customers) {
            CustomerView cv = new CustomerView();
            cv.setId(u.getId());
            cv.setEmail(u.getEmail());
            cv.setFullName(u.getFullName());
            cv.setActive("ACTIVE".equals(u.getStatus()));
            cv.setWalletBalance(u.getWalletBalance());
            customerViews.add(cv);
        }
        
        // Set search parameters for form
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("statusFilter", statusFilter != null ? statusFilter : "ALL");
        request.setAttribute("customers", customerViews);
        
        // Set pagination attributes
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageTitle", "Quản lý khách hàng");
        request.setAttribute("active", "customers");
        request.setAttribute("contentPage", "admin-customer.jsp");
        
        request.getRequestDispatcher("/admin.jsp").forward(request, response);
    }
    
    public static class CustomerView {
        private long id;
        private String email;
        private String fullName;
        private boolean active;
        private BigDecimal walletBalance;
        
        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        
        public BigDecimal getWalletBalance() { return walletBalance; }
        public void setWalletBalance(BigDecimal walletBalance) { this.walletBalance = walletBalance; }
    }
}

