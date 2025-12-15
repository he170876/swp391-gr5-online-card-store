package filter;

import controller.AdminConfigController;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.User;

@WebFilter(urlPatterns = {
    "/*"
})
public class MaintenanceFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        
        // Allow access to login, admin config, and static resources
        if (path.equals(contextPath + "/login") || 
            path.equals(contextPath + "/admin/config") ||
            path.startsWith(contextPath + "/assets/") ||
            path.startsWith(contextPath + "/template/") ||
            path.endsWith(".css") ||
            path.endsWith(".js") ||
            path.endsWith(".jpg") ||
            path.endsWith(".png") ||
            path.endsWith(".svg") ||
            path.endsWith(".ico")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check maintenance mode
        boolean maintenanceMode = AdminConfigController.isMaintenanceMode();
        
        if (maintenanceMode) {
            // Check if user is logged in and is admin
            User user = (User) request.getSession().getAttribute("user");
            
            if (user == null || user.getRoleId() != 1) {
                // Not admin, redirect to maintenance page
                if (!path.equals(contextPath + "/maintenance") && 
                    !path.equals(contextPath + "/login") &&
                    !path.startsWith(contextPath + "/admin/config")) {
                    response.sendRedirect(contextPath + "/maintenance");
                    return;
                }
            } else {
                // Admin can access everything
                chain.doFilter(request, response);
                return;
            }
        }
        
        chain.doFilter(request, response);
    }
}

