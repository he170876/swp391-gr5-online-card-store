package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AdminConfigController", urlPatterns = {"/admin/config"})
public class AdminConfigController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setAttribute("pageTitle", "Cấu hình hệ thống");
        request.setAttribute("active", "config");
        request.setAttribute("contentPage", "admin-config.jsp");
        
        request.getRequestDispatcher("/admin.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Handle configuration updates here
        // For now, just redirect back
        response.sendRedirect(request.getContextPath() + "/admin/config");
    }
}

