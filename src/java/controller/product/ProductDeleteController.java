package controller.product;

import dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ProductDeleteController", urlPatterns = {"/staff/product/delete"})
public class ProductDeleteController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/staff/product");
            return;
        }

        try {
            long id = Long.parseLong(idStr);
            ProductDAO dao = new ProductDAO();
            boolean success = dao.delete(id);

            if (success) {
                request.getSession().setAttribute("successMessage", "Xóa sản phẩm thành công!");
            } else {
                request.getSession().setAttribute("errorMessage", "Không thể xóa sản phẩm");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID sản phẩm không hợp lệ");
        }

        response.sendRedirect(request.getContextPath() + "/staff/product");
    }
}
