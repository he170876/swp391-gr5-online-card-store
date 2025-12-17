package dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.CardInfo;
import util.DBContext;

public class CardInfoDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;

    public CardInfo findById(long id) {
        try {
            String sql = "SELECT id, product_id, code, serial, expiry_date, status, created_at, updated_at "
                    + "FROM CardInfo WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                return mapResultSetToCardInfo(rs);
            }
        } catch (Exception e) {
            System.out.println("CardInfoDAO.findById: " + e.getMessage());
        }
        return null;
    }

    public CardInfo findAvailableByProduct(long productId) {
        try {
            String sql = "SELECT TOP 1 id, product_id, code, serial, expiry_date, status, created_at, updated_at "
                    + "FROM CardInfo WHERE product_id = ? AND status = 'AVAILABLE' ORDER BY id";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, productId);
            rs = stm.executeQuery();
            if (rs.next()) {
                return mapResultSetToCardInfo(rs);
            }
        } catch (Exception e) {
            System.out.println("CardInfoDAO.findAvailableByProduct: " + e.getMessage());
        }
        return null;
    }

    public boolean updateStatus(long id, String status) {
        try {
            String sql = "UPDATE CardInfo SET status = ?, updated_at = GETDATE() WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, status);
            stm.setLong(2, id);
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("CardInfoDAO.updateStatus: " + e.getMessage());
            return false;
        }
    }

    public int countAvailableByProduct(long productId) {
        try {
            String sql = "SELECT COUNT(*) FROM CardInfo WHERE product_id = ? AND status = 'AVAILABLE'";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, productId);
            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("CardInfoDAO.countAvailableByProduct: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Đếm tổng số thẻ nạp trong hệ thống.
     */
    public int getTotalCards() {
        try {
            String sql = "SELECT COUNT(*) FROM CardInfo";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("CardInfoDAO.getTotalCards: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Đếm số thẻ theo từng trạng thái.
     * @return Map với key là status, value là số lượng
     */
    public java.util.Map<String, Integer> countCardsByStatus() {
        java.util.Map<String, Integer> result = new java.util.HashMap<>();
        try {
            String sql = "SELECT status, COUNT(*) as count FROM CardInfo GROUP BY status";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (Exception e) {
            System.out.println("CardInfoDAO.countCardsByStatus: " + e.getMessage());
        }
        return result;
    }

    /**
     * Đếm số thẻ còn sẵn (AVAILABLE).
     */
    public int getAvailableCardsCount() {
        try {
            String sql = "SELECT COUNT(*) FROM CardInfo WHERE status = 'AVAILABLE'";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("CardInfoDAO.getAvailableCardsCount: " + e.getMessage());
        }
        return 0;
    }

    private CardInfo mapResultSetToCardInfo(ResultSet rs) throws SQLException {
        CardInfo cardInfo = new CardInfo();
        cardInfo.setId(rs.getLong("id"));
        cardInfo.setProductId(rs.getLong("product_id"));
        cardInfo.setCode(rs.getString("code"));
        cardInfo.setSerial(rs.getString("serial"));
        Date expiryDate = rs.getDate("expiry_date");
        if (expiryDate != null) {
            cardInfo.setExpiryDate(expiryDate.toLocalDate());
        }
        cardInfo.setStatus(rs.getString("status"));
        java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            cardInfo.setCreatedAt(createdAt.toLocalDateTime());
        }
        java.sql.Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            cardInfo.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return cardInfo;
    }
    
    public List<String> findExistingCodes(Set<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        String placeholders = String.join(",", Collections.nCopies(codes.size(), "?"));
        String sql = "SELECT code FROM CardInfo WHERE code IN (" + placeholders + ")";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            int idx = 1;
            for (String code : codes) {
                stm.setString(idx++, code);
            }
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getString("code"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return result;
    }

    public int bulkInsert(List<CardInfo> cards) {
        if (cards == null || cards.isEmpty()) {
            return 0;
        }
        String sql = "INSERT INTO CardInfo (product_id, code, serial, expiry_date, status) VALUES (?,?,?,?,?)";
        int inserted = 0;
        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (PreparedStatement stm = connection.prepareStatement(sql)) {
                for (CardInfo card : cards) {
                    stm.setLong(1, card.getProductId());
                    stm.setString(2, card.getCode());
                    stm.setString(3, card.getSerial());
                    if (card.getExpiryDate() != null) {
                        stm.setDate(4, Date.valueOf(card.getExpiryDate()));
                    } else {
                        stm.setDate(4, null);
                    }
                    stm.setString(5, card.getStatus());
                    stm.addBatch();
                }
                int[] counts = stm.executeBatch();
                for (int c : counts) {
                    inserted += c;
                }
            }
            connection.commit();
        } catch (SQLException e) {
            inserted = 0;
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
            System.out.println(e);
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                System.out.println(e);
            }
        }
        return inserted;
    }

    public CardInfo getById(long id) {
        try {
            String sql = "SELECT id, product_id, code, serial, expiry_date, status, created_at, updated_at FROM CardInfo WHERE id = ?";
            try (PreparedStatement stm = connection.prepareStatement(sql)) {
                stm.setLong(1, id);
                try (ResultSet rs = stm.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToCardInfo(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    /**
     * Search card infos with optional filters and sorting.
     */
    public List<CardInfoListView> search(String status, Long productId, Long providerId,
            java.time.LocalDate expiryFrom, java.time.LocalDate expiryTo,
            String sort) {
        List<CardInfoListView> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(
                "SELECT ci.id, ci.product_id, ci.code, ci.serial, ci.expiry_date, ci.status, ci.created_at, ci.updated_at, ");
        sql.append("p.name AS product_name, pr.name AS provider_name ");
        sql.append("FROM CardInfo ci ");
        sql.append("JOIN Product p ON ci.product_id = p.id ");
        sql.append("JOIN Provider pr ON p.provider_id = pr.id ");
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (status != null && !status.isEmpty()) {
            sql.append("AND ci.status = ? ");
            params.add(status);
        }
        if (productId != null && productId > 0) {
            sql.append("AND ci.product_id = ? ");
            params.add(productId);
        }
        if (providerId != null && providerId > 0) {
            sql.append("AND pr.id = ? ");
            params.add(providerId);
        }
        if (expiryFrom != null) {
            sql.append("AND ci.expiry_date >= ? ");
            params.add(Date.valueOf(expiryFrom));
        }
        if (expiryTo != null) {
            sql.append("AND ci.expiry_date <= ? ");
            params.add(Date.valueOf(expiryTo));
        }

        sql.append("ORDER BY ");
        switch (sort == null ? "created_desc" : sort) {
            case "expiry_asc":
                sql.append("ci.expiry_date ASC");
                break;
            case "expiry_desc":
                sql.append("ci.expiry_date DESC");
                break;
            case "status":
                sql.append("ci.status ASC, ci.id DESC");
                break;
            case "provider":
                sql.append("pr.name ASC, ci.id DESC");
                break;
            case "product":
                sql.append("p.name ASC, ci.id DESC");
                break;
            default:
                sql.append("ci.created_at DESC");
                break;
        }

        try (PreparedStatement stm = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof java.sql.Date) {
                    stm.setDate(i + 1, (java.sql.Date) param);
                } else if (param instanceof Long) {
                    stm.setLong(i + 1, (Long) param);
                } else if (param instanceof String) {
                    stm.setString(i + 1, (String) param);
                }
            }
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToListView(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }

    public CardInfo findAvailableCard(long productId) {
        try {
            String sql = "SELECT TOP 1 id, product_id, code, serial, expiry_date, status, created_at, updated_at FROM CardInfo WHERE product_id = ? AND status = 'AVAILABLE' ORDER BY id";
            try (PreparedStatement stm = connection.prepareStatement(sql)) {
                stm.setLong(1, productId);
                try (ResultSet rs = stm.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToCardInfo(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public boolean update(CardInfo card) {
        try {
            String sql = "UPDATE CardInfo SET product_id = ?, code = ?, serial = ?, expiry_date = ?, status = ?, updated_at = SYSDATETIME() WHERE id = ?";
            try (PreparedStatement stm = connection.prepareStatement(sql)) {
                stm.setLong(1, card.getProductId());
                stm.setString(2, card.getCode());
                stm.setString(3, card.getSerial());
                if (card.getExpiryDate() != null) {
                    stm.setDate(4, Date.valueOf(card.getExpiryDate()));
                } else {
                    stm.setDate(4, null);
                }
                stm.setString(5, card.getStatus());
                stm.setLong(6, card.getId());
                return stm.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    private CardInfoListView mapResultSetToListView(ResultSet rs) throws SQLException {
        CardInfoListView view = new CardInfoListView();
        view.setId(rs.getLong("id"));
        view.setProductId(rs.getLong("product_id"));
        view.setCode(rs.getString("code"));
        view.setSerial(rs.getString("serial"));
        java.sql.Date sqlDate = rs.getDate("expiry_date");
        if (sqlDate != null) {
            view.setExpiryDate(sqlDate.toLocalDate());
        }
        view.setStatus(rs.getString("status"));
        java.sql.Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            view.setCreatedAt(ts.toLocalDateTime());
        }
        ts = rs.getTimestamp("updated_at");
        if (ts != null) {
            view.setUpdatedAt(ts.toLocalDateTime());
        }
        view.setProductName(rs.getString("product_name"));
        view.setProviderName(rs.getString("provider_name"));
        return view;
    }

    /** Lightweight view for listing cards with product/provider info. */
    public static class CardInfoListView {
        private long id;
        private long productId;
        private String code;
        private String serial;
        private java.time.LocalDate expiryDate;
        private String status;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;
        private String productName;
        private String providerName;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getProductId() {
            return productId;
        }

        public void setProductId(long productId) {
            this.productId = productId;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getSerial() {
            return serial;
        }

        public void setSerial(String serial) {
            this.serial = serial;
        }

        public java.time.LocalDate getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(java.time.LocalDate expiryDate) {
            this.expiryDate = expiryDate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public java.time.LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(java.time.LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public java.time.LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProviderName() {
            return providerName;
        }

        public void setProviderName(String providerName) {
            this.providerName = providerName;
        }
    }
}

