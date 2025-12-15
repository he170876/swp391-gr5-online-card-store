package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.CardInfo;
import model.Order;
import model.Product;
import model.Category;
import model.Provider;
import model.User;
import model.WalletTransaction;
import util.DBContext;

/**
 * Data access for customer-facing flows (products, wallet, orders).
 * 
 */
public class CustomerDAO extends DBContext {

    public CustomerDAO() {
        super();
    }

    public CustomerDAO(Connection connection) {
        this.connection = connection;
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setCategoryId(rs.getLong("category_id"));
        p.setProviderId(rs.getLong("provider_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setCostPrice(rs.getDouble("cost_price"));
        p.setSellPrice(rs.getDouble("sell_price"));
        p.setDiscountPercent(rs.getDouble("discount_percent"));
        p.setQuantity(rs.getInt("quantity"));
        p.setStatus(rs.getString("status"));
        p.setImageUrl(rs.getString("image_url"));
        p.setCategoryName(rs.getString("category_name"));
        p.setProviderName(rs.getString("provider_name"));
        return p;
    }

    private Category mapCategory(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        c.setStatus(rs.getString("status"));
        return c;
    }

    private Provider mapProvider(ResultSet rs) throws SQLException {
        Provider p = new Provider();
        p.setId(rs.getLong("id"));
        p.setName(rs.getString("name"));
        p.setContactInfo(rs.getString("contact_info"));
        p.setStatus(rs.getString("status"));
        return p;
    }

    private CardInfo mapCardInfo(ResultSet rs) throws SQLException {
        CardInfo c = new CardInfo();
        c.setId(rs.getLong("id"));
        c.setProductId(rs.getLong("product_id"));
        c.setCode(rs.getString("code"));
        c.setSerial(rs.getString("serial"));
        java.sql.Date expiry = rs.getDate("expiry_date");
        if (expiry != null) {
            c.setExpiryDate(expiry.toLocalDate());
        }
        c.setStatus(rs.getString("status"));
        c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        c.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return c;
    }

    private WalletTransaction mapWalletTransaction(ResultSet rs) throws SQLException {
        WalletTransaction tx = new WalletTransaction();
        tx.setId(rs.getLong("id"));
        tx.setUserId(rs.getLong("user_id"));
        tx.setType(rs.getString("type"));
        tx.setAmount(rs.getDouble("amount"));
        tx.setBalance(rs.getDouble("balance"));
        tx.setStatus(rs.getString("status"));
        tx.setReferenceCode(rs.getString("reference_code"));
        tx.setQrUrl(rs.getString("qr_url"));
        tx.setBankCode(rs.getString("bank_code"));
        Timestamp paymentTime = rs.getTimestamp("payment_time");
        if (paymentTime != null) {
            tx.setPaymentTime(paymentTime.toLocalDateTime());
        }
        tx.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        tx.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return tx;
    }

    public List<Product> getActiveProducts(int limit) {
        List<Product> products = new ArrayList<>();
        String sql = """
                SELECT p.*, c.name AS category_name, pr.name AS provider_name
                FROM Product p
                JOIN Category c ON p.category_id = c.id
                JOIN Provider pr ON p.provider_id = pr.id
                WHERE p.status = 'ACTIVE' AND c.status = 'ACTIVE' AND pr.status = 'ACTIVE'
                ORDER BY p.id DESC""";

        if (limit > 0) {
            sql += " OFFSET 0 ROWS FETCH NEXT " + limit + " ROWS ONLY";
        }

        try (PreparedStatement stm = connection.prepareStatement(sql);
                ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                products.add(mapProduct(rs));
            }
        } catch (Exception e) {
            System.out.println("CustomerDAO.getActiveProducts: " + e.getMessage());
        }
        return products;
    }

    public List<Product> searchProducts(String keyword, Long categoryId, Long providerId, String priceRange, String stockStatus, int page, int pageSize) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT p.*, c.name AS category_name, pr.name AS provider_name
                FROM Product p
                JOIN Category c ON p.category_id = c.id
                JOIN Provider pr ON p.provider_id = pr.id
                WHERE p.status = 'ACTIVE' AND c.status = 'ACTIVE' AND pr.status = 'ACTIVE'
                """);
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND p.name LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND p.category_id = ? ");
            params.add(categoryId);
        }
        if (providerId != null && providerId > 0) {
            sql.append(" AND p.provider_id = ? ");
            params.add(providerId);
        }
        if (priceRange != null && !priceRange.isBlank()) {
            switch (priceRange) {
                case "UNDER_50":
                    sql.append(" AND p.sell_price * (1 - p.discount_percent/100) < 50000 ");
                    break;
                case "50_100":
                    sql.append(" AND p.sell_price * (1 - p.discount_percent/100) BETWEEN 50000 AND 100000 ");
                    break;
                case "100_500":
                    sql.append(" AND p.sell_price * (1 - p.discount_percent/100) BETWEEN 100000 AND 500000 ");
                    break;
                case "OVER_500":
                    sql.append(" AND p.sell_price * (1 - p.discount_percent/100) > 500000 ");
                    break;
                default:
                    break;
            }
        }
        if (stockStatus != null && !stockStatus.isBlank()) {
            switch (stockStatus) {
                case "IN":
                    sql.append(" AND p.quantity > 0 ");
                    break;
                case "OUT":
                    sql.append(" AND p.quantity = 0 ");
                    break;
                default:
                    break;
            }
        }
        sql.append(" ORDER BY p.id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (PreparedStatement stm = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stm.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    list.add(mapProduct(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("CustomerDAO.searchProducts: " + e.getMessage());
        }
        return list;
    }

    public int countProducts(String keyword, Long categoryId, Long providerId, String priceRange, String stockStatus) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) AS total
                FROM Product p
                JOIN Category c ON p.category_id = c.id
                JOIN Provider pr ON p.provider_id = pr.id
                WHERE p.status = 'ACTIVE' AND c.status = 'ACTIVE' AND pr.status = 'ACTIVE'
                """);
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND p.name LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND p.category_id = ? ");
            params.add(categoryId);
        }
        if (providerId != null && providerId > 0) {
            sql.append(" AND p.provider_id = ? ");
            params.add(providerId);
        }
        if (priceRange != null && !priceRange.isBlank()) {
            switch (priceRange) {
                case "UNDER_50":
                    sql.append(" AND p.sell_price * (1 - p.discount_percent/100) < 50000 ");
                    break;
                case "50_100":
                    sql.append(" AND p.sell_price * (1 - p.discount_percent/100) BETWEEN 50000 AND 100000 ");
                    break;
                case "100_500":
                    sql.append(" AND p.sell_price * (1 - p.discount_percent/100) BETWEEN 100000 AND 500000 ");
                    break;
                case "OVER_500":
                    sql.append(" AND p.sell_price * (1 - p.discount_percent/100) > 500000 ");
                    break;
                default:
                    break;
            }
        }
        if (stockStatus != null && !stockStatus.isBlank()) {
            switch (stockStatus) {
                case "IN":
                    sql.append(" AND p.quantity > 0 ");
                    break;
                case "OUT":
                    sql.append(" AND p.quantity = 0 ");
                    break;
                default:
                    break;
            }
        }

        try (PreparedStatement stm = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stm.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (Exception e) {
            System.out.println("CustomerDAO.countProducts: " + e.getMessage());
        }
        return 0;
    }

    public List<Category> getActiveCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Category WHERE status = 'ACTIVE' ORDER BY name";
        try (PreparedStatement stm = connection.prepareStatement(sql);
                ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                list.add(mapCategory(rs));
            }
        } catch (Exception e) {
            System.out.println("CustomerDAO.getActiveCategories: " + e.getMessage());
        }
        return list;
    }

    public List<Provider> getActiveProviders() {
        List<Provider> list = new ArrayList<>();
        String sql = "SELECT * FROM Provider WHERE status = 'ACTIVE' ORDER BY name";
        try (PreparedStatement stm = connection.prepareStatement(sql);
                ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                list.add(mapProvider(rs));
            }
        } catch (Exception e) {
            System.out.println("CustomerDAO.getActiveProviders: " + e.getMessage());
        }
        return list;
    }

    public Product getActiveProductById(long id) {
        String sql = """
                SELECT p.*, c.name AS category_name, pr.name AS provider_name
                FROM Product p
                JOIN Category c ON p.category_id = c.id
                JOIN Provider pr ON p.provider_id = pr.id
                WHERE p.id = ? AND p.status = 'ACTIVE' AND c.status = 'ACTIVE' AND pr.status = 'ACTIVE'""";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return mapProduct(rs);
                }
            }
        } catch (Exception e) {
            System.out.println("CustomerDAO.getActiveProductById: " + e.getMessage());
        }
        return null;
    }

    public int countAvailableCards(long productId) {
        String sql = "SELECT COUNT(1) AS total FROM CardInfo WHERE product_id = ? AND status = 'AVAILABLE'";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, productId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (Exception e) {
            System.out.println("CustomerDAO.countAvailableCards: " + e.getMessage());
        }
        return 0;
    }

    public CardInfo getAvailableCard(long productId) {
        String sql = """
                SELECT TOP 1 * FROM CardInfo
                WHERE product_id = ? AND status = 'AVAILABLE'
                ORDER BY created_at ASC""";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, productId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return mapCardInfo(rs);
                }
            }
        } catch (Exception e) {
            System.out.println("CustomerDAO.getAvailableCard: " + e.getMessage());
        }
        return null;
    }

    public boolean markCardSold(long cardInfoId) throws SQLException {
        String sql = "UPDATE CardInfo SET status = 'SOLD', updated_at = SYSDATETIME() WHERE id = ? AND status = 'AVAILABLE'";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, cardInfoId);
            return stm.executeUpdate() > 0;
        }
    }

    public boolean decreaseProductQuantity(long productId) throws SQLException {
        String sql = "UPDATE Product SET quantity = quantity - 1 WHERE id = ? AND quantity > 0";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, productId);
            return stm.executeUpdate() > 0;
        }
    }

    public boolean updateUserBalance(long userId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE [User] SET wallet_balance = ?, updated_at = SYSDATETIME() WHERE id = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setBigDecimal(1, newBalance);
            stm.setLong(2, userId);
            return stm.executeUpdate() > 0;
        }
    }

    public Order insertOrder(Order order) throws SQLException {
        String sql = """
                INSERT INTO [Order] (user_id, cardinfo_id, original_price, discount_percent, final_price, status, receiver_email)
                VALUES (?, ?, ?, ?, ?, ?, ?)""";
        try (PreparedStatement stm = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stm.setLong(1, order.getUserId());
            stm.setLong(2, order.getCardInfoId());
            stm.setDouble(3, order.getOriginalPrice());
            stm.setDouble(4, order.getDiscountPercent());
            stm.setDouble(5, order.getFinalPrice());
            stm.setString(6, order.getStatus());
            stm.setString(7, order.getReceiverEmail());
            int affected = stm.executeUpdate();
            if (affected == 0) {
                return null;
            }
            try (ResultSet rs = stm.getGeneratedKeys()) {
                if (rs.next()) {
                    order.setId(rs.getLong(1));
                }
            }
            return order;
        }
    }

    public WalletTransaction insertWalletTransaction(WalletTransaction tx) throws SQLException {
        String sql = """
                INSERT INTO WalletTransaction (user_id, type, amount, balance, status, reference_code, qr_url, bank_code, payment_time)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""";
        try (PreparedStatement stm = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stm.setLong(1, tx.getUserId());
            stm.setString(2, tx.getType());
            stm.setDouble(3, tx.getAmount());
            stm.setDouble(4, tx.getBalance());
            stm.setString(5, tx.getStatus());
            stm.setString(6, tx.getReferenceCode());
            stm.setString(7, tx.getQrUrl());
            stm.setString(8, tx.getBankCode());
            if (tx.getPaymentTime() != null) {
                stm.setTimestamp(9, Timestamp.valueOf(tx.getPaymentTime()));
            } else {
                stm.setTimestamp(9, null);
            }
            int affected = stm.executeUpdate();
            if (affected == 0) {
                return null;
            }
            try (ResultSet rs = stm.getGeneratedKeys()) {
                if (rs.next()) {
                    tx.setId(rs.getLong(1));
                }
            }
            return tx;
        }
    }

    public List<Order> getOrdersByUser(long userId, String keyword, String status, int page, int pageSize) {
        List<Order> orders = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT o.*, c.code AS card_code, p.name AS product_name
                FROM [Order] o
                JOIN CardInfo c ON o.cardinfo_id = c.id
                JOIN Product p ON c.product_id = p.id
                WHERE o.user_id = ?
                """);
        List<Object> params = new ArrayList<>();
        params.add(userId);
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND p.name LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND o.status = ? ");
            params.add(status);
        }
        sql.append(" ORDER BY o.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (PreparedStatement stm = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stm.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getLong("id"));
                    order.setUserId(rs.getLong("user_id"));
                    order.setCardInfoId(rs.getLong("cardinfo_id"));
                    order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    order.setOriginalPrice(rs.getDouble("original_price"));
                    order.setDiscountPercent(rs.getDouble("discount_percent"));
                    order.setFinalPrice(rs.getDouble("final_price"));
                    order.setStatus(rs.getString("status"));
                    order.setReceiverEmail(rs.getString("receiver_email"));
                    order.setProductName(rs.getString("product_name"));
                    order.setCardCode(rs.getString("card_code"));
                    orders.add(order);
                }
            }
        } catch (Exception e) {
            System.out.println("CustomerDAO.getOrdersByUser: " + e.getMessage());
        }
        return orders;
    }

    public int countOrders(long userId, String keyword, String status) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) AS total
                FROM [Order] o
                JOIN CardInfo c ON o.cardinfo_id = c.id
                JOIN Product p ON c.product_id = p.id
                WHERE o.user_id = ?
                """);
        List<Object> params = new ArrayList<>();
        params.add(userId);
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND p.name LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND o.status = ? ");
            params.add(status);
        }
        try (PreparedStatement stm = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stm.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (Exception e) {
            System.out.println("CustomerDAO.countOrders: " + e.getMessage());
        }
        return 0;
    }

    public List<WalletTransaction> getWalletTransactionsByUser(long userId, String status, String keyword, String typeDirection, int page, int pageSize) {
        List<WalletTransaction> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM WalletTransaction WHERE user_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(userId);
        if (status != null && !status.isBlank()) {
            sql.append(" AND status = ? ");
            params.add(status);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND reference_code LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }
        if (typeDirection != null && !typeDirection.isBlank()) {
            if (typeDirection.equals("IN")) {
                sql.append(" AND type = 'TOPUP' ");
            } else if (typeDirection.equals("OUT")) {
                sql.append(" AND type = 'PURCHASE' ");
            }
        }
        sql.append(" ORDER BY created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (PreparedStatement stm = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stm.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    list.add(mapWalletTransaction(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("CustomerDAO.getWalletTransactionsByUser: " + e.getMessage());
        }
        return list;
    }

    public int countWalletTransactions(long userId, String status, String keyword, String typeDirection) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) AS total FROM WalletTransaction WHERE user_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(userId);
        if (status != null && !status.isBlank()) {
            sql.append(" AND status = ? ");
            params.add(status);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND reference_code LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }
        if (typeDirection != null && !typeDirection.isBlank()) {
            if (typeDirection.equals("IN")) {
                sql.append(" AND type = 'TOPUP' ");
            } else if (typeDirection.equals("OUT")) {
                sql.append(" AND type = 'PURCHASE' ");
            }
        }
        try (PreparedStatement stm = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stm.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (Exception e) {
            System.out.println("CustomerDAO.countWalletTransactions: " + e.getMessage());
        }
        return 0;
    }

    public PurchaseResult purchaseProduct(long userId, long productId, String receiverEmail) {
        PurchaseResult result = new PurchaseResult();
        try {
            connection.setAutoCommit(false);

            Product product = getActiveProductById(productId);
            if (product == null) {
                result.setMessage("Sản phẩm không tồn tại hoặc không khả dụng.");
                connection.rollback();
                return result;
            }

            CardInfo card = getAvailableCard(productId);
            if (card == null) {
                result.setMessage("Sản phẩm đã hết mã. Vui lòng chọn sản phẩm khác.");
                connection.rollback();
                return result;
            }

            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserById(userId);
            if (user == null) {
                result.setMessage("Không tìm thấy người dùng.");
                connection.rollback();
                return result;
            }

            BigDecimal price = BigDecimal.valueOf(product.getFinalPrice());
            if (user.getWalletBalance().compareTo(price) < 0) {
                result.setMessage("Số dư không đủ. Vui lòng nạp thêm tiền vào ví.");
                connection.rollback();
                return result;
            }

            BigDecimal newBalance = user.getWalletBalance().subtract(price);

            boolean cardUpdated = markCardSold(card.getId());
            boolean quantityUpdated = decreaseProductQuantity(productId);
            boolean balanceUpdated = updateUserBalance(userId, newBalance);

            if (!cardUpdated || !quantityUpdated || !balanceUpdated) {
                result.setMessage("Không thể xử lý giao dịch. Vui lòng thử lại.");
                connection.rollback();
                return result;
            }

            Order order = new Order();
            order.setUserId(userId);
            order.setCardInfoId(card.getId());
            order.setOriginalPrice(product.getSellPrice());
            order.setDiscountPercent(product.getDiscountPercent());
            order.setFinalPrice(product.getFinalPrice());
            order.setStatus("COMPLETED");
            order.setReceiverEmail(receiverEmail != null && !receiverEmail.isBlank() ? receiverEmail : user.getEmail());

            order = insertOrder(order);
            if (order == null) {
                result.setMessage("Không thể tạo đơn hàng.");
                connection.rollback();
                return result;
            }

            WalletTransaction tx = new WalletTransaction();
            tx.setUserId(userId);
            tx.setType("PURCHASE");
            tx.setAmount(price.doubleValue());
            tx.setBalance(newBalance.doubleValue());
            tx.setStatus("SUCCESS");
            tx.setReferenceCode("PUR-" + System.currentTimeMillis());
            tx.setQrUrl(null);
            tx.setBankCode(null);
            tx.setPaymentTime(null);

            tx = insertWalletTransaction(tx);
            if (tx == null) {
                result.setMessage("Không thể ghi nhận giao dịch ví.");
                connection.rollback();
                return result;
            }

            connection.commit();
            order.setProductName(product.getName());
            order.setCardCode(card.getCode());
            result.setSuccess(true);
            result.setOrder(order);
            result.setCardInfo(card);
            result.setNewBalance(newBalance);
            return result;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("CustomerDAO.purchaseProduct rollback: " + ex.getMessage());
            }
            result.setMessage("Có lỗi xảy ra: " + e.getMessage());
            return result;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("CustomerDAO.purchaseProduct setAutoCommit: " + e.getMessage());
            }
        }
    }

    public WalletTransaction createTopupRequest(long userId, double amount, String bankCode, String referenceCode, String qrUrl) {
        WalletTransaction tx = new WalletTransaction();
        tx.setUserId(userId);
        tx.setType("TOPUP");
        tx.setAmount(amount);
        tx.setBalance(0); // balance updated when success
        tx.setStatus("PENDING");
        tx.setReferenceCode(referenceCode);
        tx.setQrUrl(qrUrl);
        tx.setBankCode(bankCode);
        tx.setPaymentTime(null);

        try {
            return insertWalletTransaction(tx);
        } catch (Exception e) {
            System.out.println("CustomerDAO.createTopupRequest: " + e.getMessage());
            return null;
        }
    }

    public boolean updateUserProfile(long userId, String fullName, String phone, String address) {
        String sql = "UPDATE [User] SET full_name = ?, phone = ?, address = ?, updated_at = SYSDATETIME() WHERE id = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, fullName);
            stm.setString(2, phone);
            stm.setString(3, address);
            stm.setLong(4, userId);
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("CustomerDAO.updateUserProfile: " + e.getMessage());
        }
        return false;
    }

    public static class PurchaseResult {
        private boolean success;
        private String message;
        private Order order;
        private CardInfo cardInfo;
        private BigDecimal newBalance;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Order getOrder() {
            return order;
        }

        public void setOrder(Order order) {
            this.order = order;
        }

        public CardInfo getCardInfo() {
            return cardInfo;
        }

        public void setCardInfo(CardInfo cardInfo) {
            this.cardInfo = cardInfo;
        }

        public BigDecimal getNewBalance() {
            return newBalance;
        }

        public void setNewBalance(BigDecimal newBalance) {
            this.newBalance = newBalance;
        }
    }
}

