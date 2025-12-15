package filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.User;

@WebFilter(urlPatterns = {
    "/admin.jsp",
    "/admin/*"
})
public class OnlyAdminFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Check if user is logged in
        if (request.getSession().getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) request.getSession().getAttribute("user");
        
        // Check if user is admin (role_id = 1)
        if (user.getRoleId() != 1) {
            response.sendRedirect(request.getContextPath() + "/home?error=403");
            return;
        }
        
        // Check if admin account is active
        if (!"ACTIVE".equals(user.getStatus())) {
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/login?error=account_locked");
            return;
        }
        
        chain.doFilter(request, response);
    }
}
