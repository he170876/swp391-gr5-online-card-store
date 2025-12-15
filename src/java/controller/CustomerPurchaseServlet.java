package controller;

import dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.User;
import model.Order;
import service.OrderService;

@WebServlet(name = "CustomerPurchaseServlet", urlPatterns = {"/customer/purchase"})
public class CustomerPurchaseServlet extends HttpServlet {

    private OrderService orderService;
    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        orderService = new OrderService();
        productDAO = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String productIdStr = request.getParameter("productId");
        if (productIdStr == null || productIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/customer/home");
            return;
        }

        try {
            long productId = Long.parseLong(productIdStr);
            var product = productDAO.findById(productId);
            if (product == null || !"ACTIVE".equals(product.getStatus())) {
                request.setAttribute("error", "Sản phẩm không tồn tại.");
                response.sendRedirect(request.getContextPath() + "/customer/home");
                return;
            }

            // Calculate final price
            double originalPrice = product.getSellPrice();
            double discountPercent = product.getDiscountPercent();
            double finalPrice = originalPrice * (1 - discountPercent / 100.0);

            request.setAttribute("product", product);
            request.setAttribute("originalPrice", originalPrice);
            request.setAttribute("discountPercent", discountPercent);
            request.setAttribute("finalPrice", finalPrice);
            request.setAttribute("receiverEmail", user.getEmail());

            request.getRequestDispatcher("/WEB-INF/views/customer/purchase.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/customer/home");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String productIdStr = request.getParameter("productId");
        String receiverEmail = request.getParameter("receiverEmail");

        if (productIdStr == null || productIdStr.isEmpty()) {
            request.setAttribute("error", "Sản phẩm không hợp lệ.");
            request.getRequestDispatcher("/WEB-INF/views/customer/home.jsp").forward(request, response);
            return;
        }

        if (receiverEmail == null || receiverEmail.trim().isEmpty()) {
            receiverEmail = user.getEmail(); // Default to user's email
        }

        try {
            long productId = Long.parseLong(productIdStr);
            Order order = orderService.purchaseProduct(user.getId(), productId, receiverEmail);

            if (order != null) {
                response.sendRedirect(request.getContextPath() + "/customer/order-detail?id=" + order.getId());
            } else {
                request.setAttribute("error", "Mua hàng thất bại. Vui lòng kiểm tra số dư ví hoặc số lượng sản phẩm.");
                response.sendRedirect(request.getContextPath() + "/customer/product-detail?id=" + productId);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/customer/home");
        }
    }
}

