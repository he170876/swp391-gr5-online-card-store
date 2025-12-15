package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Order;
import util.DBContext;

/**
 * Data access for Order.
 */
public class OrderDAO extends DBContext {

    public Order getById(long orderId) {
        try {
            String sql = "SELECT id, user_id, cardinfo_id, created_at, original_price, discount_percent, final_price, status, receiver_email FROM [Order] WHERE id = ?";
            try (PreparedStatement stm = connection.prepareStatement(sql)) {
                stm.setLong(1, orderId);
                try (ResultSet rs = stm.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToOrder(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        try {
            String sql = "SELECT id, user_id, cardinfo_id, created_at, original_price, discount_percent, final_price, status, receiver_email FROM [Order] ORDER BY created_at DESC";
            try (PreparedStatement stm = connection.prepareStatement(sql)) {
                try (ResultSet rs = stm.executeQuery()) {
                    while (rs.next()) {
                        orders.add(mapResultSetToOrder(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return orders;
    }

    public List<Order> getOrdersByUser(long userId) {
        List<Order> orders = new ArrayList<>();
        try {
            String sql = "SELECT id, user_id, cardinfo_id, created_at, original_price, discount_percent, final_price, status, receiver_email FROM [Order] WHERE user_id = ? ORDER BY created_at DESC";
            try (PreparedStatement stm = connection.prepareStatement(sql)) {
                stm.setLong(1, userId);
                try (ResultSet rs = stm.executeQuery()) {
                    while (rs.next()) {
                        orders.add(mapResultSetToOrder(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return orders;
    }

    public long create(Order order) {
        try {
            String sql = "INSERT INTO [Order] (user_id, cardinfo_id, created_at, original_price, discount_percent, final_price, status, receiver_email) VALUES (?,?,?,?,?,?,?,?)";
            try (PreparedStatement stm = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stm.setLong(1, order.getUserId());
                stm.setLong(2, order.getCardInfoId());
                stm.setTimestamp(3,
                        Timestamp.valueOf(order.getCreatedAt() != null ? order.getCreatedAt() : LocalDateTime.now()));
                stm.setDouble(4, order.getOriginalPrice());
                stm.setDouble(5, order.getDiscountPercent());
                stm.setDouble(6, order.getFinalPrice());
                stm.setString(7, order.getStatus());
                stm.setString(8, order.getReceiverEmail());
                stm.executeUpdate();
                try (ResultSet rs = stm.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return -1;
    }

    public boolean updateStatus(long orderId, String newStatus) {
        try {
            String sql = "UPDATE [Order] SET status = ? WHERE id = ?";
            try (PreparedStatement stm = connection.prepareStatement(sql)) {
                stm.setString(1, newStatus);
                stm.setLong(2, orderId);
                return stm.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean updateCardInfo(long orderId, long cardInfoId) {
        try {
            String sql = "UPDATE [Order] SET cardinfo_id = ? WHERE id = ?";
            try (PreparedStatement stm = connection.prepareStatement(sql)) {
                stm.setLong(1, cardInfoId);
                stm.setLong(2, orderId);
                return stm.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setUserId(rs.getLong("user_id"));
        order.setCardInfoId(rs.getLong("cardinfo_id"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            order.setCreatedAt(ts.toLocalDateTime());
        }
        order.setOriginalPrice(rs.getDouble("original_price"));
        order.setDiscountPercent(rs.getDouble("discount_percent"));
        order.setFinalPrice(rs.getDouble("final_price"));
        order.setStatus(rs.getString("status"));
        order.setReceiverEmail(rs.getString("receiver_email"));
        return order;
    }
}
