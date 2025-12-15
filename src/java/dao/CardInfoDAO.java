package dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    private CardInfo mapResultSetToCardInfo(ResultSet rs) throws Exception {
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
}


