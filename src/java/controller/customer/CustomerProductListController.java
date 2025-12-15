package controller.customer;

import dao.CustomerDAO;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Category;
import model.Provider;
import model.Product;

@WebServlet(name = "CustomerProductListController", urlPatterns = {"/customer/products"})
public class CustomerProductListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        CustomerDAO dao = new CustomerDAO();

        String keyword = request.getParameter("keyword");
        String categoryRaw = request.getParameter("categoryId");
        String providerRaw = request.getParameter("providerId");
        String priceRange = request.getParameter("priceRange");
        String stockStatus = request.getParameter("stockStatus");
        String pageRaw = request.getParameter("page");

        Long categoryId = null, providerId = null;
        int page = 1;
        int pageSize = 9;
        try {
            if (categoryRaw != null && !categoryRaw.isBlank()) {
                categoryId = Long.parseLong(categoryRaw);
            }
            if (providerRaw != null && !providerRaw.isBlank()) {
                providerId = Long.parseLong(providerRaw);
            }
            if (pageRaw != null) {
                page = Math.max(1, Integer.parseInt(pageRaw));
            }
        } catch (NumberFormatException ignored) {
        }

        int total = dao.countProducts(keyword, categoryId, providerId, priceRange, stockStatus);
        int totalPages = (int) Math.ceil(total / (double) pageSize);
        if (totalPages == 0) {
            totalPages = 1;
        }
        if (page > totalPages) page = totalPages;

        List<Product> products = dao.searchProducts(keyword, categoryId, providerId, priceRange, stockStatus, page, pageSize);
        List<Category> categories = dao.getActiveCategories();
        List<Provider> providers = dao.getActiveProviders();

        request.setAttribute("products", products);
        request.setAttribute("categories", categories);
        request.setAttribute("providers", providers);
        request.setAttribute("keyword", keyword);
        request.setAttribute("categoryId", categoryId);
        request.setAttribute("providerId", providerId);
        request.setAttribute("priceRange", priceRange);
        request.setAttribute("stockStatus", stockStatus);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/customer/product/list.jsp").forward(request, response);
    }
}

