package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import model.User;
import model.Order;
import service.OrderService;

/**
 * UC28 - View Orders: Staff views all orders with optional filtering/sorting.
 */
@WebServlet(name = "StaffOrderListController", urlPatterns = { "/staff/orders" })
public class StaffOrderListController extends HttpServlet {

    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = requireStaff(request, response);
        if (user == null) {
            return;
        }

        List<Order> orders = orderService.getAllOrders();

        // Optional: filter by status
        String filterStatus = Optional.ofNullable(request.getParameter("status")).orElse("");
        if (!filterStatus.isEmpty()) {
            orders.removeIf(o -> !o.getStatus().equals(filterStatus));
        }

        // Optional: sort by date or amount
        String sortBy = Optional.ofNullable(request.getParameter("sort")).orElse("date");
        if ("amount".equals(sortBy)) {
            orders.sort((a, b) -> Double.compare(b.getFinalPrice(), a.getFinalPrice()));
        } else {
            orders.sort((a, b) -> {
                if (a.getCreatedAt() == null && b.getCreatedAt() == null)
                    return 0;
                if (a.getCreatedAt() == null)
                    return 1;
                if (b.getCreatedAt() == null)
                    return -1;
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            });
        }

        request.setAttribute("orders", orders);
        request.getRequestDispatcher("/staff-orders.jsp").forward(request, response);
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
        if (user.getRoleId() != 1 && user.getRoleId() != 2) { // admin or staff
            response.sendRedirect(request.getContextPath() + "/home?error=403");
            return null;
        }
        return user;
    }
}
