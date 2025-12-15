package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import model.User;
import model.Order;
import service.OrderService;
import util.OrderStatus;

/**
 * UC30 - Update Order Status: Staff updates order status with validation.
 */
@WebServlet(name = "StaffOrderStatusController", urlPatterns = { "/staff/update-order-status" })
public class StaffOrderStatusController extends HttpServlet {

    private final OrderService orderService = new OrderService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = requireStaff(request, response);
        if (user == null) {
            return;
        }

        String orderIdRaw = Optional.ofNullable(request.getParameter("orderId")).orElse("0");
        String newStatus = Optional.ofNullable(request.getParameter("status")).orElse("");

        long orderId;
        try {
            orderId = Long.parseLong(orderIdRaw);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid order ID\"}");
            return;
        }

        if (newStatus.isEmpty() || !OrderStatus.isValid(newStatus)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid status\"}");
            return;
        }

        Order current = orderService.getOrderById(orderId);
        if (current == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Order not found\"}");
            return;
        }

        if (!OrderStatus.isAllowedTransition(current.getStatus(), newStatus)) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter()
                    .write("{\"error\": \"Transition not allowed: " + current.getStatus() + " -> " + newStatus + "\"}");
            return;
        }

        boolean updated = orderService.updateOrderStatus(orderId, newStatus);
        if (updated) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\": true}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to update order status\"}");
        }
    }

    private User requireStaff(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Object obj = request.getSession().getAttribute("user");
        if (obj == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        User user = (User) obj;
        if (user.getRoleId() != 1 && user.getRoleId() != 2) {
            response.sendRedirect(request.getContextPath() + "/home?error=403");
            return null;
        }
        return user;
    }
}
