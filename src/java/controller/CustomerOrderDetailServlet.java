package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.User;
import model.Order;
import model.CardInfo;
import model.Product;
import service.OrderService;

@WebServlet(name = "CustomerOrderDetailServlet", urlPatterns = {"/customer/order-detail"})
public class CustomerOrderDetailServlet extends HttpServlet {

    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        super.init();
        orderService = new OrderService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String orderIdStr = request.getParameter("id");
        if (orderIdStr == null || orderIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/customer/orders");
            return;
        }

        try {
            long orderId = Long.parseLong(orderIdStr);
            Order order = orderService.getOrderById(orderId);

            if (order == null || order.getUserId() != user.getId()) {
                request.setAttribute("error", "Đơn hàng không tồn tại hoặc không thuộc về bạn.");
                request.getRequestDispatcher("/WEB-INF/views/customer/orders.jsp").forward(request, response);
                return;
            }

            CardInfo cardInfo = orderService.getCardInfoByOrderId(orderId);
            Product product = orderService.getProductByOrderId(orderId);

            request.setAttribute("order", order);
            request.setAttribute("cardInfo", cardInfo);
            request.setAttribute("product", product);

            request.getRequestDispatcher("/WEB-INF/views/customer/order-detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/customer/orders");
        }
    }
}


