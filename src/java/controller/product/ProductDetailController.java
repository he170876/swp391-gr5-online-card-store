package controller.product;

import dao.ProductDAO;
import model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ProductDetailController", urlPatterns = {"/staff/product/detail"})
public class ProductDetailController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/staff/product");
            return;
        }

        ProductDAO dao = new ProductDAO();
        Product product;
        try {
            product = dao.findById(Long.parseLong(idStr));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/staff/product");
            return;
        }

        if (product == null) {
            request.getSession().setAttribute("errorMessage", "Không tìm thấy sản phẩm");
            response.sendRedirect(request.getContextPath() + "/staff/product");
            return;
        }

        request.setAttribute("product", product);

        // Forward to JSP
        request.setAttribute("pageTitle", "Chi tiết sản phẩm");
        request.setAttribute("active", "product");
        request.setAttribute("contentPage", "staff-product-detail.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
}
