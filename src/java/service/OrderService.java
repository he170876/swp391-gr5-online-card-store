package service;

import dao.CardInfoDAO;
import dao.OrderDAO;
import dao.ProductDAO;
import dao.UserDAO;
import dao.WalletTransactionDAO;
import model.CardInfo;
import model.Order;
import model.Product;
import model.User;
import model.WalletTransaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderService {
    private OrderDAO orderDAO;
    private CardInfoDAO cardInfoDAO;
    private ProductDAO productDAO;
    private UserDAO userDAO;
    private WalletTransactionDAO walletTransactionDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.cardInfoDAO = new CardInfoDAO();
        this.productDAO = new ProductDAO();
        this.userDAO = new UserDAO();
        this.walletTransactionDAO = new WalletTransactionDAO();
    }

    public Order purchaseProduct(long userId, long productId, String receiverEmail) {
        // Get product
        Product product = productDAO.findById(productId);
        if (product == null || !"ACTIVE".equals(product.getStatus())) {
            return null; // Product not found or inactive
        }

        // Check stock
        CardInfo availableCard = cardInfoDAO.findAvailableByProduct(productId);
        if (availableCard == null) {
            return null; // No available card
        }

        // Get user
        User user = userDAO.findById(userId);
        if (user == null) {
            return null;
        }

        // Calculate price
        double originalPrice = product.getSellPrice();
        double discountPercent = product.getDiscountPercent();
        double finalPrice = originalPrice * (1 - discountPercent / 100.0);

        // Check wallet balance
        if (user.getWalletBalance().compareTo(BigDecimal.valueOf(finalPrice)) < 0) {
            return null; // Insufficient balance
        }

        try {
            // Start transaction (simplified - in production use proper transaction management)
            // Update card status
            if (!cardInfoDAO.updateStatus(availableCard.getId(), "SOLD")) {
                return null;
            }

            // Create order
            Order order = new Order();
            order.setUserId(userId);
            order.setCardInfoId(availableCard.getId());
            order.setOriginalPrice(originalPrice);
            order.setDiscountPercent(discountPercent);
            order.setFinalPrice(finalPrice);
            order.setStatus("PAID");
            order.setReceiverEmail(receiverEmail);
            order.setCreatedAt(LocalDateTime.now());

            if (!orderDAO.create(order)) {
                // Rollback card status
                cardInfoDAO.updateStatus(availableCard.getId(), "AVAILABLE");
                return null;
            }

            // Update wallet balance
            BigDecimal newBalance = user.getWalletBalance().subtract(BigDecimal.valueOf(finalPrice));
            if (!userDAO.updateWalletBalance(userId, newBalance)) {
                // Rollback
                cardInfoDAO.updateStatus(availableCard.getId(), "AVAILABLE");
                return null;
            }

            // Create wallet transaction
            WalletTransaction transaction = new WalletTransaction();
            transaction.setUserId(userId);
            transaction.setType("PURCHASE");
            transaction.setAmount(-finalPrice);
            transaction.setBalance(newBalance.doubleValue());
            transaction.setStatus("SUCCESS");
            transaction.setCreatedAt(LocalDateTime.now());
            transaction.setUpdatedAt(LocalDateTime.now());
            walletTransactionDAO.create(transaction);

            // Get created order
            return orderDAO.findById(order.getId());
        } catch (Exception e) {
            System.out.println("OrderService.purchaseProduct: " + e.getMessage());
            // Rollback card status
            cardInfoDAO.updateStatus(availableCard.getId(), "AVAILABLE");
            return null;
        }
    }

    public Order getOrderById(long orderId) {
        return orderDAO.findById(orderId);
    }

    public java.util.List<Order> getOrdersByUserId(long userId) {
        return orderDAO.findByUserId(userId);
    }

    public CardInfo getCardInfoByOrderId(long orderId) {
        Order order = orderDAO.findById(orderId);
        if (order != null) {
            return cardInfoDAO.findById(order.getCardInfoId());
        }
        return null;
    }

    public Product getProductByOrderId(long orderId) {
        Order order = orderDAO.findById(orderId);
        if (order != null) {
            CardInfo cardInfo = cardInfoDAO.findById(order.getCardInfoId());
            if (cardInfo != null) {
                return productDAO.findById(cardInfo.getProductId());
            }
        }
        return null;
    }
}


