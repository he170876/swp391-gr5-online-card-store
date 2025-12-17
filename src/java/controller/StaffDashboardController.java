package controller;

import dao.CardInfoDAO;
import dao.OrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import util.OrderStatus;
import util.CardInfoStatus;

/**
 * Dashboard entry for staff role.
 * Map URL /staff (không .jsp) -> staff.jsp
 */
@WebServlet(name = "StaffDashboardController", urlPatterns = {"/staff"})
public class StaffDashboardController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy các thông số thống kê đơn giản cho dashboard staff
        OrderDAO orderDAO = new OrderDAO();
        CardInfoDAO cardInfoDAO = new CardInfoDAO();

        // KPI tổng quan
        int totalOrders = orderDAO.getTotalOrders();
        int totalCards = cardInfoDAO.getTotalCards();
        int ordersToday = orderDAO.getOrdersToday();
        int availableCards = cardInfoDAO.getAvailableCardsCount();

        // Dữ liệu cho chart đơn hàng theo trạng thái
        Map<String, Integer> ordersByStatus = orderDAO.countOrdersByStatus();
        int pendingOrders = ordersByStatus.getOrDefault(OrderStatus.PENDING, 0);
        int paidOrders = ordersByStatus.getOrDefault(OrderStatus.PAID, 0);
        int completedOrders = ordersByStatus.getOrDefault(OrderStatus.COMPLETED, 0);
        int canceledOrders = ordersByStatus.getOrDefault(OrderStatus.CANCELED, 0);
        int refundedOrders = ordersByStatus.getOrDefault(OrderStatus.REFUNDED, 0);

        // Dữ liệu cho chart thẻ theo trạng thái
        Map<String, Integer> cardsByStatus = cardInfoDAO.countCardsByStatus();
        int availableCardsCount = cardsByStatus.getOrDefault(CardInfoStatus.AVAILABLE, 0);
        int soldCards = cardsByStatus.getOrDefault(CardInfoStatus.SOLD, 0);
        int expiredCards = cardsByStatus.getOrDefault(CardInfoStatus.EXPIRED, 0);
        int inactiveCards = cardsByStatus.getOrDefault(CardInfoStatus.INACTIVE, 0);

        // Dữ liệu cho chart đơn hàng 7 ngày gần nhất
        List<Integer> ordersLast7Days = orderDAO.getOrdersLast7Days();

        // Set attributes
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("totalCards", totalCards);
        request.setAttribute("ordersToday", ordersToday);
        request.setAttribute("availableCards", availableCards);

        // Chart data
        request.setAttribute("pendingOrders", pendingOrders);
        request.setAttribute("paidOrders", paidOrders);
        request.setAttribute("completedOrders", completedOrders);
        request.setAttribute("canceledOrders", canceledOrders);
        request.setAttribute("refundedOrders", refundedOrders);

        request.setAttribute("availableCardsCount", availableCardsCount);
        request.setAttribute("soldCards", soldCards);
        request.setAttribute("expiredCards", expiredCards);
        request.setAttribute("inactiveCards", inactiveCards);

        request.setAttribute("ordersLast7Days", ordersLast7Days);

        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}


