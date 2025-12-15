package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.User;
import service.OrderService;

@WebServlet(name = "CustomerOrderHistoryServlet", urlPatterns = {"/customer/orders"})
public class CustomerOrderHistoryServlet extends HttpServlet {

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

        String status = request.getParameter("status");
        if (status != null && !status.isEmpty()) {
            // Filter by status would need OrderDAO.findByUserIdAndStatus
            // For now, show all orders
        }

        var orders = orderService.getOrdersByUserId(user.getId());
        request.setAttribute("orders", orders);

        request.getRequestDispatcher("/WEB-INF/views/customer/orders.jsp").forward(request, response);
    }
}


