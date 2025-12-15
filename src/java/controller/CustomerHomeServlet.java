package controller;

import dao.CategoryDAO;
import dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "CustomerHomeServlet", urlPatterns = {"/customer/home"})
public class CustomerHomeServlet extends HttpServlet {

    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        productDAO = new ProductDAO();
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String categoryIdStr = request.getParameter("categoryId");

        if (keyword != null && !keyword.trim().isEmpty()) {
            // Search products
            request.setAttribute("products", productDAO.search(keyword));
            request.setAttribute("keyword", keyword);
        } else if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            try {
                long categoryId = Long.parseLong(categoryIdStr);
                request.setAttribute("products", productDAO.findByCategory(categoryId));
                request.setAttribute("categoryId", categoryId);
            } catch (NumberFormatException e) {
                request.setAttribute("products", productDAO.findActive());
            }
        } else {
            // Show all active products
            request.setAttribute("products", productDAO.findActive());
        }

        // Always load categories for sidebar/filter
        request.setAttribute("categories", categoryDAO.findActive());

        request.getRequestDispatcher("/WEB-INF/views/customer/home.jsp").forward(request, response);
    }
}


