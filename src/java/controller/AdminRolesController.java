package controller;

import dao.RoleDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.Role;

@WebServlet(name = "AdminRolesController", urlPatterns = {
    "/admin/roles",
    "/admin/roles/create",
    "/admin/roles/update",
    "/admin/roles/delete"
})
public class AdminRolesController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        RoleDAO roleDAO = new RoleDAO();
        
        if (path.endsWith("/delete")) {
            // Delete role
            String idParam = request.getParameter("id");
            if (idParam != null) {
                try {
                    long id = Long.parseLong(idParam);
                    roleDAO.delete(id);
                } catch (Exception e) {
                    System.out.println("AdminRolesController.delete: " + e.getMessage());
                }
            }
            response.sendRedirect(request.getContextPath() + "/admin/roles");
            return;
        }
        
        // List all roles or show edit form
        String editId = request.getParameter("edit");
        if (editId != null) {
            try {
                long id = Long.parseLong(editId);
                Role role = roleDAO.findById(id);
                request.setAttribute("editRole", role);
            } catch (Exception e) {
                System.out.println("AdminRolesController.edit: " + e.getMessage());
            }
        }
        
        request.setAttribute("roles", roleDAO.findAll());
        request.setAttribute("pageTitle", "Quản lý vai trò");
        request.setAttribute("active", "roles");
        request.setAttribute("contentPage", "admin-roles.jsp");
        
        request.getRequestDispatcher("/admin.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        RoleDAO roleDAO = new RoleDAO();
        
        if (path.endsWith("/create")) {
            // Create new role
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            
            if (name != null && !name.trim().isEmpty()) {
                Role role = new Role();
                role.setName(name.trim());
                role.setDescription(description != null ? description.trim() : "");
                roleDAO.create(role);
            }
            response.sendRedirect(request.getContextPath() + "/admin/roles");
            return;
        }
        
        if (path.endsWith("/update")) {
            // Update role
            String idParam = request.getParameter("id");
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            
            if (idParam != null && name != null && !name.trim().isEmpty()) {
                try {
                    long id = Long.parseLong(idParam);
                    Role role = new Role();
                    role.setId(id);
                    role.setName(name.trim());
                    role.setDescription(description != null ? description.trim() : "");
                    roleDAO.update(role);
                } catch (Exception e) {
                    System.out.println("AdminRolesController.update: " + e.getMessage());
                }
            }
            response.sendRedirect(request.getContextPath() + "/admin/roles");
            return;
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/roles");
    }
}

