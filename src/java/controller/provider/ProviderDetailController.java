package controller.provider;

import dao.ProviderDAO;
import dao.ProductDAO;
import model.Provider;
import model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ProviderDetailController", urlPatterns = {"/staff/provider/detail"})
public class ProviderDetailController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/staff/provider");
            return;
        }

        ProviderDAO providerDAO = new ProviderDAO();
        Provider provider;
        try {
            provider = providerDAO.findById(Long.parseLong(idStr));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/staff/provider");
            return;
        }

        if (provider == null) {
            request.getSession().setAttribute("errorMessage", "Không tìm thấy nhà cung cấp");
            response.sendRedirect(request.getContextPath() + "/staff/provider");
            return;
        }

        // Get related products
        ProductDAO productDAO = new ProductDAO();
        List<Product> products = productDAO.getByProviderId(provider.getId());

        request.setAttribute("provider", provider);
        request.setAttribute("products", products);

        // Forward to JSP
        request.setAttribute("pageTitle", "Chi tiết nhà cung cấp");
        request.setAttribute("active", "provider");
        request.setAttribute("contentPage", "staff-provider-detail.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
}
