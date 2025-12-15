package controller.category;

import dao.CategoryDAO;
import dto.CategoryFormDTO;
import model.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Controller for adding a new category
 * URL: /staff/category/add
 */
@WebServlet(name = "CategoryAddController", urlPatterns = {"/staff/category/add"})
public class CategoryAddController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Initialize empty form
        CategoryFormDTO formDTO = new CategoryFormDTO();
        formDTO.setStatus("ACTIVE"); // Default status
        request.setAttribute("formDTO", formDTO);
        
        // Forward to view
        request.setAttribute("pageTitle", "Thêm danh mục");
        request.setAttribute("active", "category");
        request.setAttribute("contentPage", "staff-category-add.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        // 1. Get form data
        CategoryFormDTO formDTO = new CategoryFormDTO();
        formDTO.setName(Optional.ofNullable(request.getParameter("name")).orElse("").trim());
        formDTO.setDescription(Optional.ofNullable(request.getParameter("description")).orElse("").trim());
        formDTO.setStatus(Optional.ofNullable(request.getParameter("status")).orElse("ACTIVE"));
        
        // 2. Validate
        CategoryDAO categoryDAO = new CategoryDAO();
        
        if (formDTO.getName().isEmpty()) {
            formDTO.addError("name", "Tên danh mục không được để trống");
        } else if (formDTO.getName().length() > 100) {
            formDTO.addError("name", "Tên danh mục không được vượt quá 100 ký tự");
        } else if (categoryDAO.isNameExists(formDTO.getName(), null)) {
            formDTO.addError("name", "Tên danh mục đã tồn tại");
        }
        
        if (formDTO.getDescription() != null && formDTO.getDescription().length() > 255) {
            formDTO.addError("description", "Mô tả không được vượt quá 255 ký tự");
        }
        
        if (!"ACTIVE".equals(formDTO.getStatus()) && !"INACTIVE".equals(formDTO.getStatus())) {
            formDTO.addError("status", "Trạng thái không hợp lệ");
        }
        
        // 3. If validation fails, return to form
        if (formDTO.hasErrors()) {
            request.setAttribute("formDTO", formDTO);
            request.setAttribute("errors", formDTO.getErrors());
            request.setAttribute("pageTitle", "Thêm danh mục");
            request.setAttribute("active", "category");
            request.setAttribute("contentPage", "staff-category-add.jsp");
            request.getRequestDispatcher("/staff.jsp").forward(request, response);
            return;
        }
        
        // 4. Create category
        Category category = new Category();
        category.setName(formDTO.getName());
        category.setDescription(formDTO.getDescription().isEmpty() ? null : formDTO.getDescription());
        category.setStatus(formDTO.getStatus());
        
        long newId = categoryDAO.insert(category);
        
        if (newId > 0) {
            request.getSession().setAttribute("successMessage", "Thêm danh mục thành công!");
            response.sendRedirect(request.getContextPath() + "/staff/category");
        } else {
            request.getSession().setAttribute("errorMessage", "Có lỗi xảy ra khi thêm danh mục!");
            request.setAttribute("formDTO", formDTO);
            request.setAttribute("pageTitle", "Thêm danh mục");
            request.setAttribute("active", "category");
            request.setAttribute("contentPage", "staff-category-add.jsp");
            request.getRequestDispatcher("/staff.jsp").forward(request, response);
        }
    }
}
