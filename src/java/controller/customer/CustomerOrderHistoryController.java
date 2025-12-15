package controller.customer;

import dao.CustomerDAO;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Order;
import model.User;

@WebServlet(name = "CustomerOrderHistoryController", urlPatterns = {"/customer/orders"})
public class CustomerOrderHistoryController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        User user = (User) session.getAttribute("user");

        CustomerDAO dao = new CustomerDAO();
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        String pageRaw = request.getParameter("page");
        int page = 1;
        int pageSize = 10;
        try {
            if (pageRaw != null) {
                page = Math.max(1, Integer.parseInt(pageRaw));
            }
        } catch (NumberFormatException ignored) {
        }

        if (status != null && !(status.equals("PENDING") || status.equals("COMPLETED"))) {
            status = null;
        }

        int total = dao.countOrders(user.getId(), keyword, status);
        int totalPages = (int) Math.ceil(total / (double) pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        List<Order> orders = dao.getOrdersByUser(user.getId(), keyword, status, page, pageSize);
        request.setAttribute("orders", orders);
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/customer/order/history.jsp").forward(request, response);
    }
}

