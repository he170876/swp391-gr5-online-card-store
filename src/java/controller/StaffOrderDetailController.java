package controller;

import dao.CardInfoDAO;
import dao.UserDAO;
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

/**
 * UC29 - View Order Details: Staff views detailed info of a specific order.
 */
@WebServlet(name = "StaffOrderDetailController", urlPatterns = { "/staff/order-detail" })
public class StaffOrderDetailController extends HttpServlet {

    private final OrderService orderService = new OrderService();
    private final CardInfoDAO cardInfoDAO = new CardInfoDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = requireStaff(request, response);
        if (user == null) {
            return;
        }

        String orderIdRaw = Optional.ofNullable(request.getParameter("id")).orElse("0");
        long orderId;
        try {
            orderId = Long.parseLong(orderIdRaw);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid order ID");
            request.getRequestDispatcher("/staff-order-detail.jsp").forward(request, response);
            return;
        }

        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            request.setAttribute("error", "Order not found");
            request.getRequestDispatcher("/staff-order-detail.jsp").forward(request, response);
            return;
        }

        // Load customer info
        User customer = userDAO.getUserById(order.getUserId());
        request.setAttribute("customer", customer);

        // Load card info if assigned
        if (order.getCardInfoId() > 0) {
            CardInfo card = cardInfoDAO.getById(order.getCardInfoId());
            request.setAttribute("card", card);
        }

        request.setAttribute("order", order);
        request.getRequestDispatcher("/staff-order-detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
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
