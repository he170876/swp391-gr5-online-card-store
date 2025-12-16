package controller.product;

import dao.ProductDAO;
import dao.CategoryDAO;
import dao.ProviderDAO;
import dto.ProductSearchDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import model.Product;

@WebServlet(name = "ProductListController", urlPatterns = {"/staff/product"})
public class ProductListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Parse search parameters
        ProductSearchDTO searchDTO = new ProductSearchDTO();
        searchDTO.setKeyword(Optional.ofNullable(request.getParameter("keyword")).orElse(""));

        String categoryIdStr = request.getParameter("categoryId");
        if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            try {
                searchDTO.setCategoryId(Long.parseLong(categoryIdStr));
            } catch (NumberFormatException e) {
                // Ignore invalid category id
            }
        }

        String providerIdStr = request.getParameter("providerId");
        if (providerIdStr != null && !providerIdStr.isEmpty()) {
            try {
                searchDTO.setProviderId(Long.parseLong(providerIdStr));
            } catch (NumberFormatException e) {
                // Ignore invalid provider id
            }
        }

        searchDTO.setStatus(Optional.ofNullable(request.getParameter("status")).orElse(""));

        String pageStr = request.getParameter("page");
        int page = 1;
        try {
            page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        } catch (NumberFormatException e) {
            page = 1;
        }
        // Ensure page is at least 1
        if (page < 1) {
            page = 1;
        }
        searchDTO.setPage(page);

        // 2. Get data
        ProductDAO productDAO = new ProductDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        ProviderDAO providerDAO = new ProviderDAO();

        int totalCount = productDAO.count(searchDTO);
        int totalPages = (int) Math.ceil((double) totalCount / searchDTO.getPageSize());
        
        // Ensure page doesn't exceed total pages
        if (totalPages > 0 && searchDTO.getPage() > totalPages) {
            searchDTO.setPage(totalPages);
        }
        
        List<Product> products = productDAO.search(searchDTO);

        // 3. Set attributes
        request.setAttribute("products", products);
        request.setAttribute("searchDTO", searchDTO);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", searchDTO.getPage());
        request.setAttribute("categories", categoryDAO.findActive());
        request.setAttribute("providers", providerDAO.findActive());

        // 4. Forward to JSP (uses staff.jsp as master layout)
        request.setAttribute("pageTitle", "Quản lý sản phẩm");
        request.setAttribute("active", "product");
        request.setAttribute("contentPage", "staff-product-list.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
}
