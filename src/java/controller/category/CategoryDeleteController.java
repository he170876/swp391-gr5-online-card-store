package controller.category;

import dao.CategoryDAO;
import model.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller for deleting (soft delete) a category
 * URL: /staff/category/delete
 */
@WebServlet(name = "CategoryDeleteController", urlPatterns = {"/staff/category/delete"})
public class CategoryDeleteController extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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
        
        // 2. Check if category exists
        CategoryDAO categoryDAO = new CategoryDAO();
        Category category = categoryDAO.findById(id);
        
        if (category == null) {
            request.getSession().setAttribute("errorMessage", "Không tìm thấy danh mục!");
            response.sendRedirect(request.getContextPath() + "/staff/category");
            return;
        }
        
        // 3. Check if category has active products
        if (categoryDAO.hasProducts(id)) {
            request.getSession().setAttribute("errorMessage", 
                "Không thể xóa danh mục \"" + category.getName() + "\" vì đang có sản phẩm hoạt động!");
            response.sendRedirect(request.getContextPath() + "/staff/category");
            return;
        }
        
        // 4. Soft delete category
        boolean success = categoryDAO.delete(id);
        
        if (success) {
            request.getSession().setAttribute("successMessage", 
                "Xóa danh mục \"" + category.getName() + "\" thành công!");
        } else {
            request.getSession().setAttribute("errorMessage", "Có lỗi xảy ra khi xóa danh mục!");
        }
        
        response.sendRedirect(request.getContextPath() + "/staff/category");
    }
}
