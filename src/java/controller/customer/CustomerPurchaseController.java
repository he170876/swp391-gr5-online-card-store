package controller.customer;

import dao.CustomerDAO;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.CardInfo;
import model.Order;
import model.User;

@WebServlet(name = "CustomerPurchaseController", urlPatterns = {"/customer/purchase"})
public class CustomerPurchaseController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        User user = (User) session.getAttribute("user");

        String productIdRaw = request.getParameter("productId");
        String receiverEmail = request.getParameter("receiverEmail");
        long productId;
        try {
            productId = Long.parseLong(productIdRaw);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/customer/products");
            return;
        }

        CustomerDAO dao = new CustomerDAO();
        CustomerDAO.PurchaseResult result = dao.purchaseProduct(user.getId(), productId, receiverEmail);

        if (!result.isSuccess()) {
            String encoded = URLEncoder.encode(result.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/customer/product/detail?id=" + productId + "&error=" + encoded);
            return;
        }

        // Update session balance
        User refreshed = new dao.UserDAO().getUserById(user.getId());
        if (refreshed == null) {
            refreshed = user;
            refreshed.setWalletBalance(result.getNewBalance() == null ? BigDecimal.ZERO : result.getNewBalance());
        }
        session.setAttribute("user", refreshed);

        Order order = result.getOrder();
        CardInfo card = result.getCardInfo();

        request.setAttribute("order", order);
        request.setAttribute("card", card);
        request.setAttribute("purchaseSuccess", true);
        request.setAttribute("walletBalance", refreshed.getWalletBalance());

        // Reload order history to display latest purchase
        request.setAttribute("orders", new CustomerDAO().getOrdersByUser(user.getId(), null, null, 1, 10));
        request.setAttribute("currentPage", 1);
        request.setAttribute("totalPages", (int) Math.ceil(new CustomerDAO().countOrders(user.getId(), null, null) / 10.0));
        request.getRequestDispatcher("/customer/order/history.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/customer/products");
    }
}

