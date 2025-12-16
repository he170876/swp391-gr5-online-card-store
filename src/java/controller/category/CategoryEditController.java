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
 * Controller for editing a category
 * URL: /staff/category/edit
 */
@WebServlet(name = "CategoryEditController", urlPatterns = {"/staff/category/edit"})
public class CategoryEditController extends HttpServlet {
    
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
        
        // 3. Populate form DTO
        CategoryFormDTO formDTO = new CategoryFormDTO();
        formDTO.setId(category.getId());
        formDTO.setName(category.getName());
        formDTO.setDescription(category.getDescription());
        formDTO.setStatus(category.getStatus());
        
        request.setAttribute("formDTO", formDTO);
        
        // 4. Forward to view
        request.setAttribute("pageTitle", "Sửa danh mục");
        request.setAttribute("active", "category");
        request.setAttribute("contentPage", "staff-category-edit.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        // 1. Get form data
        CategoryFormDTO formDTO = new CategoryFormDTO();
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            request.getSession().setAttribute("errorMessage", "ID danh mục không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/staff/category");
            return;
        }
        
        try {
            formDTO.setId(Long.parseLong(idStr));
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID danh mục không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/staff/category");
            return;
        }
        
        formDTO.setName(Optional.ofNullable(request.getParameter("name")).orElse("").trim());
        formDTO.setDescription(Optional.ofNullable(request.getParameter("description")).orElse("").trim());
        formDTO.setStatus(Optional.ofNullable(request.getParameter("status")).orElse("ACTIVE"));
        
        // 2. Validate
        CategoryDAO categoryDAO = new CategoryDAO();
        
        // Check if category exists
        Category existingCategory = categoryDAO.findById(formDTO.getId());
        if (existingCategory == null) {
            request.getSession().setAttribute("errorMessage", "Không tìm thấy danh mục!");
            response.sendRedirect(request.getContextPath() + "/staff/category");
            return;
        }
        
        if (formDTO.getName().isEmpty()) {
            formDTO.addError("name", "Tên danh mục không được để trống");
        } else if (formDTO.getName().length() > 100) {
            formDTO.addError("name", "Tên danh mục không được vượt quá 100 ký tự");
        } else if (categoryDAO.isNameExists(formDTO.getName(), formDTO.getId())) {
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
            request.setAttribute("pageTitle", "Sửa danh mục");
            request.setAttribute("active", "category");
            request.setAttribute("contentPage", "staff-category-edit.jsp");
            request.getRequestDispatcher("/staff.jsp").forward(request, response);
            return;
        }
        
        // 4. Update category
        Category category = new Category();
        category.setId(formDTO.getId());
        category.setName(formDTO.getName());
        category.setDescription(formDTO.getDescription().isEmpty() ? null : formDTO.getDescription());
        category.setStatus(formDTO.getStatus());
        
        boolean success = categoryDAO.update(category);
        
        if (success) {
            request.getSession().setAttribute("successMessage", "Cập nhật danh mục thành công!");
            response.sendRedirect(request.getContextPath() + "/staff/category");
        } else {
            request.getSession().setAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật danh mục!");
            request.setAttribute("formDTO", formDTO);
            request.setAttribute("pageTitle", "Sửa danh mục");
            request.setAttribute("active", "category");
            request.setAttribute("contentPage", "staff-category-edit.jsp");
            request.getRequestDispatcher("/staff.jsp").forward(request, response);
        }
    }
}
