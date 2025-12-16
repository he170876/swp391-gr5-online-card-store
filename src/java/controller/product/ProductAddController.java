package controller.product;

import dao.ProductDAO;
import dao.CategoryDAO;
import dao.ProviderDAO;
import dto.ProductFormDTO;
import model.Product;
import model.Category;
import model.Provider;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@WebServlet(name = "ProductAddController", urlPatterns = {"/staff/product/add"})
public class ProductAddController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Load dropdown data
        CategoryDAO categoryDAO = new CategoryDAO();
        ProviderDAO providerDAO = new ProviderDAO();

        request.setAttribute("categories", categoryDAO.findActive());
        request.setAttribute("providers", providerDAO.findActive());
        request.setAttribute("formDTO", new ProductFormDTO());

        // Forward to JSP
        request.setAttribute("pageTitle", "Thêm sản phẩm");
        request.setAttribute("active", "product");
        request.setAttribute("contentPage", "staff-product-add.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Parse form data
        ProductFormDTO formDTO = parseFormData(request);

        // 2. Validate
        Map<String, String> errors = validateForm(formDTO, null);

        if (!errors.isEmpty()) {
            // Reload dropdowns and show errors
            CategoryDAO categoryDAO = new CategoryDAO();
            ProviderDAO providerDAO = new ProviderDAO();

            request.setAttribute("categories", categoryDAO.findActive());
            request.setAttribute("providers", providerDAO.findActive());
            request.setAttribute("formDTO", formDTO);
            request.setAttribute("errors", errors);

            request.setAttribute("pageTitle", "Thêm sản phẩm");
            request.setAttribute("active", "product");
            request.setAttribute("contentPage", "staff-product-add.jsp");
            request.getRequestDispatcher("/staff.jsp").forward(request, response);
            return;
        }

        // 3. Create product
        Product product = new Product();
        product.setCategoryId(formDTO.getCategoryId());
        product.setProviderId(formDTO.getProviderId());
        product.setName(formDTO.getName().trim());
        product.setDescription(formDTO.getDescription());
        product.setImageUrl(formDTO.getImageUrl());
        product.setCostPrice(formDTO.getCostPrice());
        product.setSellPrice(formDTO.getSellPrice());
        product.setDiscountPercent(formDTO.getDiscountPercent() != null ? formDTO.getDiscountPercent() : 0);
//        product.setQuantity(0); // Quantity managed by CardInfo
product.setQuantity(formDTO.getQuantity());

        product.setStatus(formDTO.getStatus());

        ProductDAO dao = new ProductDAO();
        long newId = dao.insert(product);

        if (newId > 0) {
            request.getSession().setAttribute("successMessage", "Thêm sản phẩm thành công!");
            response.sendRedirect(request.getContextPath() + "/staff/product");
        } else {
            request.setAttribute("errorMessage", "Không thể thêm sản phẩm. Vui lòng thử lại.");
            doGet(request, response);
        }
    }

    private ProductFormDTO parseFormData(HttpServletRequest request) {
        ProductFormDTO dto = new ProductFormDTO();

        try {
            dto.setCategoryId(Long.parseLong(request.getParameter("categoryId")));
        } catch (Exception e) {
            dto.setCategoryId(null);
        }

        try {
            dto.setProviderId(Long.parseLong(request.getParameter("providerId")));
        } catch (Exception e) {
            dto.setProviderId(null);
        }

        dto.setName(request.getParameter("name"));
        dto.setDescription(request.getParameter("description"));
        dto.setImageUrl(request.getParameter("imageUrl"));

        try {
            dto.setCostPrice(Double.parseDouble(request.getParameter("costPrice")));
        } catch (Exception e) {
            dto.setCostPrice(null);
        }

        try {
            dto.setSellPrice(Double.parseDouble(request.getParameter("sellPrice")));
        } catch (Exception e) {
            dto.setSellPrice(null);
        }

        try {
            dto.setDiscountPercent(Double.parseDouble(request.getParameter("discountPercent")));
        } catch (Exception e) {
            dto.setDiscountPercent(0.0);
        }

        try {
    dto.setQuantity(Integer.parseInt(request.getParameter("quantity")));
} catch (Exception e) {
    dto.setQuantity(null);
}

        dto.setStatus(Optional.ofNullable(request.getParameter("status")).orElse("ACTIVE"));

        return dto;
    }

    private Map<String, String> validateForm(ProductFormDTO dto, Long excludeId) {
        Map<String, String> errors = new HashMap<>();


        // Category validation
        if (dto.getCategoryId() == null) {
            errors.put("categoryId", "Vui lòng chọn danh mục");
        } else {
            CategoryDAO catDAO = new CategoryDAO();
            Category category = catDAO.findById(dto.getCategoryId());
            if (category == null) {
                errors.put("categoryId", "Danh mục không hợp lệ");
            }
        }

        // Provider validation
        if (dto.getProviderId() == null) {
            errors.put("providerId", "Vui lòng chọn nhà cung cấp");
        } else {
            ProviderDAO provDAO = new ProviderDAO();
            Provider provider = provDAO.findById(dto.getProviderId());
            if (provider == null) {
                errors.put("providerId", "Nhà cung cấp không hợp lệ");
            }
        }

        // Name validation
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            errors.put("name", "Tên sản phẩm không được để trống");
        } else if (dto.getName().trim().length() < 3) {
            errors.put("name", "Tên sản phẩm phải có ít nhất 3 ký tự");
        } else if (dto.getName().trim().length() > 100) {
            errors.put("name", "Tên sản phẩm không được vượt quá 100 ký tự");
        } else {
            ProductDAO dao = new ProductDAO();
            if (dao.isNameExists(dto.getName().trim(), excludeId)) {
                errors.put("name", "Tên sản phẩm đã tồn tại");
            }
        }

        // Description validation
        if (dto.getDescription() != null && dto.getDescription().length() > 255) {
            errors.put("description", "Mô tả không được vượt quá 255 ký tự");
        }

        // Image URL validation
        if (dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty()) {
            String url = dto.getImageUrl().trim();
            if (url.length() > 500) {
                errors.put("imageUrl", "URL ảnh không được vượt quá 500 ký tự");
            } else if (!url.startsWith("http://") && !url.startsWith("https://")) {
                errors.put("imageUrl", "URL ảnh phải bắt đầu bằng http:// hoặc https://");
            }
        }

        // Cost price validation
        if (dto.getCostPrice() == null) {
            errors.put("costPrice", "Giá gốc không được để trống");
        } else if (dto.getCostPrice() <= 0) {
            errors.put("costPrice", "Giá gốc phải lớn hơn 0");
        }

        // Sell price validation
        if (dto.getSellPrice() == null) {
            errors.put("sellPrice", "Giá bán không được để trống");
        } else if (dto.getSellPrice() <= 0) {
            errors.put("sellPrice", "Giá bán phải lớn hơn 0");
        } else if (dto.getCostPrice() != null && dto.getSellPrice() < dto.getCostPrice()) {
            errors.put("sellPrice", "Giá bán phải lớn hơn hoặc bằng giá gốc");
        }

        // Discount percent validation
        if (dto.getDiscountPercent() != null) {
            if (dto.getDiscountPercent() < 0 || dto.getDiscountPercent() > 100) {
                errors.put("discountPercent", "Giảm giá phải từ 0 đến 100");
            }
        }

        // Status validation
        if (dto.getStatus() == null || dto.getStatus().isEmpty()) {
            errors.put("status", "Vui lòng chọn trạng thái");
        } else if (!dto.getStatus().equals("ACTIVE") && !dto.getStatus().equals("INACTIVE")) {
            errors.put("status", "Trạng thái không hợp lệ");
        }
// Quantity validation
if (dto.getQuantity() == null) {
    errors.put("quantity", "Số lượng không được để trống");
} else if (dto.getQuantity() < 0) {
    errors.put("quantity", "Số lượng không được nhỏ hơn 0");
}
        return errors;
    }
}
