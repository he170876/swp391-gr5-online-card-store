package controller.provider;

import dao.ProviderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ProviderDeleteController", urlPatterns = {"/staff/provider/delete"})
public class ProviderDeleteController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/staff/provider");
            return;
        }

        try {
            long providerId = Long.parseLong(idStr);
            ProviderDAO dao = new ProviderDAO();

            // Check if provider has active products
            if (dao.hasProducts(providerId)) {
                request.getSession().setAttribute("errorMessage", 
                    "Không thể xóa nhà cung cấp đang có sản phẩm hoạt động. Vui lòng ngừng hoạt động hoặc chuyển sản phẩm trước.");
                response.sendRedirect(request.getContextPath() + "/staff/provider");
                return;
            }

            boolean success = dao.delete(providerId);

            if (success) {
                request.getSession().setAttribute("successMessage", "Xóa nhà cung cấp thành công!");
            } else {
                request.getSession().setAttribute("errorMessage", "Không thể xóa nhà cung cấp");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID nhà cung cấp không hợp lệ");
        }

        response.sendRedirect(request.getContextPath() + "/staff/provider");
    }
}
