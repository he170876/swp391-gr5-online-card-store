package service;

import dao.CardInfoDAO;
import dao.OrderDAO;
import model.CardInfo;
import model.Order;
import util.CardInfoStatus;

/**
 * Service for assigning card to order and managing order.
 */
public class OrderService {

    private final OrderDAO orderDAO;
    private final CardInfoDAO cardInfoDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.cardInfoDAO = new CardInfoDAO();
    }

    /**
     * Assign an available card to an order.
     * Logic:
     * 1. Find AVAILABLE card for the product
     * 2. Update Order.cardinfo_id
     * 3. Update CardInfo status to SOLD
     * 4. Return true if successful
     */
    public boolean assignCardToOrder(long orderId, long productId) {
        try {
            CardInfo available = cardInfoDAO.findAvailableCard(productId);
            if (available == null) {
                System.out.println("No available card found for productId: " + productId);
                return false;
            }

            boolean updateOrder = orderDAO.updateCardInfo(orderId, available.getId());
            if (!updateOrder) {
                System.out.println("Failed to update order " + orderId + " with cardInfoId " + available.getId());
                return false;
            }

            boolean updateCard = cardInfoDAO.updateStatus(available.getId(), CardInfoStatus.SOLD);
            if (!updateCard) {
                System.out.println("Failed to update card " + available.getId() + " to SOLD");
                return false;
            }

            return true;
        } catch (Exception e) {
            System.out.println("Error assigning card to order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create new order.
     */
    public Order createOrder(Order order) {
        long id = orderDAO.create(order);
        if (id > 0) {
            order.setId(id);
            return order;
        }
        return null;
    }

    /**
     * Get order by ID.
     */
    public Order getOrderById(long orderId) {
        return orderDAO.getById(orderId);
    }

    /**
     * Get all orders.
     */
    public java.util.List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }

    /**
     * Get orders by user.
     */
    public java.util.List<Order> getOrdersByUser(long userId) {
        return orderDAO.getOrdersByUser(userId);
    }

    /**
     * Update order status with validation.
     */
    public boolean updateOrderStatus(long orderId, String newStatus) {
        Order current = orderDAO.getById(orderId);
        if (current == null) {
            System.out.println("Order not found: " + orderId);
            return false;
        }

        if (!util.OrderStatus.isValid(newStatus)) {
            System.out.println("Invalid status: " + newStatus);
            return false;
        }

        if (!util.OrderStatus.isAllowedTransition(current.getStatus(), newStatus)) {
            System.out.println("Transition not allowed: " + current.getStatus() + " -> " + newStatus);
            return false;
        }

        return orderDAO.updateStatus(orderId, newStatus);
    }
}
