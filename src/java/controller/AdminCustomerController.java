package controller;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
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
            response.sendRedirect(request.getContextPath() + "/admin/customer");
            return;
        }
        
        if (path.endsWith("/unblock")) {
            // Unblock customer
            String idParam = request.getParameter("id");
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
            response.sendRedirect(request.getContextPath() + "/admin/customer");
            return;
        }
        
        // List all customers
        List<User> customers = userDAO.findByRole(3, 0, 1000); // Get all customers
        
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
        
        request.setAttribute("customers", customerViews);
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

