package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Order;
import util.DBContext;

public class OrderDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;

    public Order findById(long id) {
        try {
            String sql = "SELECT id, user_id, cardinfo_id, created_at, original_price, discount_percent, final_price, status, receiver_email "
                    + "FROM [Order] WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                return mapResultSetToOrder(rs);
            }
        } catch (Exception e) {
            System.out.println("OrderDAO.findById: " + e.getMessage());
        }
        return null;
    }

    public List<Order> findByUserId(long userId) {
        List<Order> orders = new ArrayList<>();
        try {
            String sql = "SELECT id, user_id, cardinfo_id, created_at, original_price, discount_percent, final_price, status, receiver_email "
                    + "FROM [Order] WHERE user_id = ? ORDER BY created_at DESC";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, userId);
            rs = stm.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (Exception e) {
            System.out.println("OrderDAO.findByUserId: " + e.getMessage());
        }
        return orders;
    }

    public List<Order> findByUserIdAndStatus(long userId, String status) {
        List<Order> orders = new ArrayList<>();
        try {
            String sql = "SELECT id, user_id, cardinfo_id, created_at, original_price, discount_percent, final_price, status, receiver_email "
                    + "FROM [Order] WHERE user_id = ? AND status = ? ORDER BY created_at DESC";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, userId);
            stm.setString(2, status);
            rs = stm.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (Exception e) {
            System.out.println("OrderDAO.findByUserIdAndStatus: " + e.getMessage());
        }
        return orders;
    }

    public boolean create(Order order) {
        try {
            String sql = "INSERT INTO [Order] (user_id, cardinfo_id, created_at, original_price, discount_percent, final_price, status, receiver_email) "
                    + "VALUES (?, ?, GETDATE(), ?, ?, ?, ?, ?)";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, order.getUserId());
            stm.setLong(2, order.getCardInfoId());
            stm.setDouble(3, order.getOriginalPrice());
            stm.setDouble(4, order.getDiscountPercent());
            stm.setDouble(5, order.getFinalPrice());
            stm.setString(6, order.getStatus());
            stm.setString(7, order.getReceiverEmail());
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("OrderDAO.create: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(long id, String status) {
        try {
            String sql = "UPDATE [Order] SET status = ? WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, status);
            stm.setLong(2, id);
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("OrderDAO.updateStatus: " + e.getMessage());
            return false;
        }
    }

    public double getDailyRevenue() {
        try {
            String sql = "SELECT ISNULL(SUM(final_price), 0) FROM [Order] WHERE CAST(created_at AS DATE) = CAST(GETDATE() AS DATE) AND status IN ('PAID', 'COMPLETED')";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            System.out.println("OrderDAO.getDailyRevenue: " + e.getMessage());
        }
        return 0.0;
    }

    public int getOrdersToday() {
        try {
            String sql = "SELECT COUNT(*) FROM [Order] WHERE CAST(created_at AS DATE) = CAST(GETDATE() AS DATE)";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("OrderDAO.getOrdersToday: " + e.getMessage());
        }
        return 0;
    }

    public List<Double> getRevenueLast7Days() {
        List<Double> revenues = new ArrayList<>();
        try {
            String sql = "SELECT ISNULL(SUM(final_price), 0) as revenue " +
                    "FROM [Order] " +
                    "WHERE created_at >= DATEADD(DAY, -6, CAST(GETDATE() AS DATE)) " +
                    "AND status IN ('PAID', 'COMPLETED') " +
                    "GROUP BY CAST(created_at AS DATE) " +
                    "ORDER BY CAST(created_at AS DATE)";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                revenues.add(rs.getDouble("revenue"));
            }
            // Ensure we have 7 values (fill with 0 if needed)
            while (revenues.size() < 7) {
                revenues.add(0.0);
            }
        } catch (Exception e) {
            System.out.println("OrderDAO.getRevenueLast7Days: " + e.getMessage());
        }
        return revenues;
    }

    public List<Order> findAll(int offset, int limit) {
        List<Order> orders = new ArrayList<>();
        try {
            String sql = "SELECT id, user_id, cardinfo_id, created_at, original_price, discount_percent, final_price, status, receiver_email " +
                    "FROM [Order] ORDER BY created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
            stm = connection.prepareStatement(sql);
            stm.setInt(1, offset);
            stm.setInt(2, limit);
            rs = stm.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (Exception e) {
            System.out.println("OrderDAO.findAll: " + e.getMessage());
        }
        return orders;
    }

    private Order mapResultSetToOrder(ResultSet rs) throws Exception {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setUserId(rs.getLong("user_id"));
        order.setCardInfoId(rs.getLong("cardinfo_id"));
        java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            order.setCreatedAt(createdAt.toLocalDateTime());
        }
        order.setOriginalPrice(rs.getDouble("original_price"));
        order.setDiscountPercent(rs.getDouble("discount_percent"));
        order.setFinalPrice(rs.getDouble("final_price"));
        order.setStatus(rs.getString("status"));
        order.setReceiverEmail(rs.getString("receiver_email"));
        return order;
    }
}


