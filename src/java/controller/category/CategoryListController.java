package controller.category;

import dao.CategoryDAO;
import dto.CategorySearchDTO;
import model.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller for listing categories with search, filter, and pagination
 * URL: /staff/category
 */
@WebServlet(name = "CategoryListController", urlPatterns = {"/staff/category"})
public class CategoryListController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Parse search parameters
        CategorySearchDTO searchDTO = new CategorySearchDTO();
        searchDTO.setKeyword(Optional.ofNullable(request.getParameter("keyword")).orElse(""));
        searchDTO.setStatus(Optional.ofNullable(request.getParameter("status")).orElse(""));
        
        String pageStr = request.getParameter("page");
        int page = 1;
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        searchDTO.setPage(page);
        
        // 2. Get data from DAO
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.search(searchDTO);
        int totalCount = categoryDAO.count(searchDTO);
        int totalPages = (int) Math.ceil((double) totalCount / searchDTO.getPageSize());
        
        // 3. Set attributes for JSP
        request.setAttribute("categories", categories);
        request.setAttribute("searchDTO", searchDTO);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCount", totalCount);
        
        // 4. Forward to view
        request.setAttribute("pageTitle", "Danh sách danh mục");
        request.setAttribute("active", "category");
        request.setAttribute("contentPage", "staff-category-list.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
}
