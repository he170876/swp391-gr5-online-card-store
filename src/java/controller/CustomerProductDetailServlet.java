package controller;

import dao.ProductDAO;
import dao.CardInfoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "CustomerProductDetailServlet", urlPatterns = {"/customer/product-detail"})
public class CustomerProductDetailServlet extends HttpServlet {

    private ProductDAO productDAO;
    private CardInfoDAO cardInfoDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        productDAO = new ProductDAO();
        cardInfoDAO = new CardInfoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String productIdStr = request.getParameter("id");
        if (productIdStr == null || productIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/customer/home");
            return;
        }

        try {
            long productId = Long.parseLong(productIdStr);
            var product = productDAO.findById(productId);
            if (product == null || !"ACTIVE".equals(product.getStatus())) {
                request.setAttribute("error", "Sản phẩm không tồn tại hoặc đã ngừng kinh doanh.");
                request.getRequestDispatcher("/WEB-INF/views/customer/home.jsp").forward(request, response);
                return;
            }

            // Check available stock
            int availableStock = cardInfoDAO.countAvailableByProduct(productId);
            request.setAttribute("product", product);
            request.setAttribute("availableStock", availableStock);

            request.getRequestDispatcher("/WEB-INF/views/customer/product-detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/customer/home");
        }
    }
}


