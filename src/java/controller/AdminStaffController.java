package controller;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.User;

@WebServlet(name = "AdminStaffController", urlPatterns = {"/admin/staff"})
public class AdminStaffController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        UserDAO userDAO = new UserDAO();
        
        // Get all staff (role_id = 2)
        List<User> staffList = userDAO.findByRole(2, 0, 1000);
        
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
        
        request.setAttribute("staffList", staffViews);
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

