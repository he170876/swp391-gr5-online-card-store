package controller;

import dao.CardInfoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import model.User;
import model.Order;
import model.CardInfo;
import service.OrderService;
import service.EmailService;

/**
 * UC31 - Resend Card Code: Staff resends purchased card code to customer.
 */
@WebServlet(name = "StaffResendCardController", urlPatterns = { "/staff/resend-card" })
public class StaffResendCardController extends HttpServlet {

    private final OrderService orderService = new OrderService();
    private final CardInfoDAO cardInfoDAO = new CardInfoDAO();
    private final EmailService emailService = new EmailService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = requireStaff(request, response);
        if (user == null) {
            return;
        }

        response.setContentType("application/json");

        String orderIdRaw = Optional.ofNullable(request.getParameter("orderId")).orElse("0");
        long orderId;
        try {
            orderId = Long.parseLong(orderIdRaw);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid order ID\"}");
            return;
        }

        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Order not found\"}");
            return;
        }

        // Check if order status allows resending
        if (!util.OrderStatus.canResendCard(order.getStatus())) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write("{\"error\": \"Cannot resend card for status: " + order.getStatus() + "\"}");
            return;
        }

        // Check if card is assigned
        if (order.getCardInfoId() <= 0) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write("{\"error\": \"No card assigned to this order\"}");
            return;
        }

        // Get card info
        CardInfo card = cardInfoDAO.getById(order.getCardInfoId());
        if (card == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Card not found\"}");
            return;
        }

        // Send email
        boolean sent = emailService.sendCardCodeEmail(order.getReceiverEmail(), card.getCode(), card.getSerial());
        if (sent) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\": true}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to send email\"}");
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
