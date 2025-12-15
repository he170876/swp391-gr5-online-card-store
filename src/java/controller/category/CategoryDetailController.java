package controller.category;

import dao.CategoryDAO;
import dao.ProductDAO;
import model.Category;
import model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller for viewing category details with related products
 * URL: /staff/category/detail
 */
@WebServlet(name = "CategoryDetailController", urlPatterns = {"/staff/category/detail"})
public class CategoryDetailController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Get category ID
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            request.getSession().setAttribute("errorMessage", "ID danh mục không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/staff/category");
            return;
        }
        
        long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID danh mục không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/staff/category");
            return;
        }
        
        // 2. Get category from database
        CategoryDAO categoryDAO = new CategoryDAO();
        Category category = categoryDAO.findById(id);
        
        if (category == null) {
            request.getSession().setAttribute("errorMessage", "Không tìm thấy danh mục!");
            response.sendRedirect(request.getContextPath() + "/staff/category");
            return;
        }
        
        // 3. Get related products
        ProductDAO productDAO = new ProductDAO();
        List<Product> products = productDAO.getByCategoryId(id);
        
        // 4. Set attributes
        request.setAttribute("category", category);
        request.setAttribute("products", products);
        
        // 5. Forward to view
        request.setAttribute("pageTitle", "Chi tiết danh mục");
        request.setAttribute("active", "category");
        request.setAttribute("contentPage", "staff-category-detail.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
}
