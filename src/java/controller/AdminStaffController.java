package controller;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.User;

@WebServlet(name = "AdminStaffController", urlPatterns = {
    "/admin/staff",
    "/admin/staff/block",
    "/admin/staff/unblock"
})
public class AdminStaffController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        UserDAO userDAO = new UserDAO();
        
        if (path.endsWith("/block")) {
            // Block staff
            String idParam = request.getParameter("id");
            String pageParam = request.getParameter("page");
            String keywordParam = request.getParameter("keyword");
            String statusParam = request.getParameter("status");
            
            if (idParam != null) {
                try {
                    long id = Long.parseLong(idParam);
                    User user = userDAO.findById(id);
                    if (user != null && user.getRoleId() == 2) { // Only staff
                        user.setStatus("LOCKED");
                        userDAO.update(user);
                    }
                } catch (Exception e) {
                    System.out.println("AdminStaffController.block: " + e.getMessage());
                }
            }
            
            // Preserve pagination and search parameters
            StringBuilder redirectUrl = new StringBuilder(request.getContextPath() + "/admin/staff");
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
            // Unblock staff
            String idParam = request.getParameter("id");
            String pageParam = request.getParameter("page");
            String keywordParam = request.getParameter("keyword");
            String statusParam = request.getParameter("status");
            
            if (idParam != null) {
                try {
                    long id = Long.parseLong(idParam);
                    User user = userDAO.findById(id);
                    if (user != null && user.getRoleId() == 2) { // Only staff
                        user.setStatus("ACTIVE");
                        userDAO.update(user);
                    }
                } catch (Exception e) {
                    System.out.println("AdminStaffController.unblock: " + e.getMessage());
                }
            }
            
            // Preserve pagination and search parameters
            StringBuilder redirectUrl = new StringBuilder(request.getContextPath() + "/admin/staff");
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
            totalRecords = userDAO.countSearch(keyword, 2L, status);
        } else {
            totalRecords = userDAO.countByRole(2);
        }
        
        int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / pageSize));
        
        // Search staff with filters and pagination
        List<User> staffList;
        if (keyword != null || status != null) {
            staffList = userDAO.search(keyword, 2L, status, offset, pageSize);
        } else {
            staffList = userDAO.findByRole(2, offset, pageSize);
        }
        
        // Convert to view model
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<StaffView> staffViews = new java.util.ArrayList<>();
        for (User u : staffList) {
            StaffView sv = new StaffView();
            sv.setId(u.getId());
            sv.setEmail(u.getEmail());
            sv.setFullName(u.getFullName());
            sv.setPhone(u.getPhone());
            sv.setAddress(u.getAddress());
            sv.setStatus(u.getStatus());
            LocalDateTime createdAt = u.getCreatedAt();
            if (createdAt != null) {
                sv.setCreatedAtFormatted(createdAt.format(formatter));
            } else {
                sv.setCreatedAtFormatted("");
            }
            staffViews.add(sv);
        }
        
        // Set search parameters for form
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("statusFilter", statusFilter != null ? statusFilter : "ALL");
        request.setAttribute("staffList", staffViews);
        
        // Set pagination attributes
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageTitle", "Quản lý nhân viên");
        request.setAttribute("active", "staff");
        request.setAttribute("contentPage", "admin-staff.jsp");
        
        request.getRequestDispatcher("/admin.jsp").forward(request, response);
    }
    
    public static class StaffView {
        private long id;
        private String email;
        private String fullName;
        private String phone;
        private String address;
        private String status;
        private String createdAtFormatted;
        
        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getCreatedAtFormatted() { return createdAtFormatted; }
        public void setCreatedAtFormatted(String createdAtFormatted) { this.createdAtFormatted = createdAtFormatted; }
    }
}

