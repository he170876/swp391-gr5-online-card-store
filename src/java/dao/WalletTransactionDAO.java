package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.WalletTransaction;
import util.DBContext;

public class WalletTransactionDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;

    public WalletTransaction findById(long id) {
        try {
            String sql = "SELECT id, user_id, type, amount, balance, status, reference_code, qr_url, bank_code, payment_time, created_at, updated_at "
                    + "FROM WalletTransaction WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                return mapResultSetToWalletTransaction(rs);
            }
        } catch (Exception e) {
            System.out.println("WalletTransactionDAO.findById: " + e.getMessage());
        }
        return null;
    }

    public List<WalletTransaction> findByUserId(long userId) {
        List<WalletTransaction> transactions = new ArrayList<>();
        try {
            String sql = "SELECT id, user_id, type, amount, balance, status, reference_code, qr_url, bank_code, payment_time, created_at, updated_at "
                    + "FROM WalletTransaction WHERE user_id = ? ORDER BY created_at DESC";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, userId);
            rs = stm.executeQuery();
            while (rs.next()) {
                transactions.add(mapResultSetToWalletTransaction(rs));
            }
        } catch (Exception e) {
            System.out.println("WalletTransactionDAO.findByUserId: " + e.getMessage());
        }
        return transactions;
    }

    public List<WalletTransaction> findByUserIdAndType(long userId, String type) {
        List<WalletTransaction> transactions = new ArrayList<>();
        try {
            String sql = "SELECT id, user_id, type, amount, balance, status, reference_code, qr_url, bank_code, payment_time, created_at, updated_at "
                    + "FROM WalletTransaction WHERE user_id = ? AND type = ? ORDER BY created_at DESC";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, userId);
            stm.setString(2, type);
            rs = stm.executeQuery();
            while (rs.next()) {
                transactions.add(mapResultSetToWalletTransaction(rs));
            }
        } catch (Exception e) {
            System.out.println("WalletTransactionDAO.findByUserIdAndType: " + e.getMessage());
        }
        return transactions;
    }

    public WalletTransaction findByReferenceCode(String referenceCode) {
        try {
            String sql = "SELECT id, user_id, type, amount, balance, status, reference_code, qr_url, bank_code, payment_time, created_at, updated_at "
                    + "FROM WalletTransaction WHERE reference_code = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, referenceCode);
            rs = stm.executeQuery();
            if (rs.next()) {
                return mapResultSetToWalletTransaction(rs);
            }
        } catch (Exception e) {
            System.out.println("WalletTransactionDAO.findByReferenceCode: " + e.getMessage());
        }
        return null;
    }

    public boolean create(WalletTransaction transaction) {
        try {
            String sql = "INSERT INTO WalletTransaction (user_id, type, amount, balance, status, reference_code, qr_url, bank_code, payment_time, created_at, updated_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, transaction.getUserId());
            stm.setString(2, transaction.getType());
            stm.setDouble(3, transaction.getAmount());
            stm.setDouble(4, transaction.getBalance());
            stm.setString(5, transaction.getStatus());
            stm.setString(6, transaction.getReferenceCode());
            stm.setString(7, transaction.getQrUrl());
            stm.setString(8, transaction.getBankCode());
            if (transaction.getPaymentTime() != null) {
                stm.setTimestamp(9, Timestamp.valueOf(transaction.getPaymentTime()));
            } else {
                stm.setTimestamp(9, null);
            }
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("WalletTransactionDAO.create: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(long id, String status) {
        try {
            String sql = "UPDATE WalletTransaction SET status = ?, updated_at = GETDATE() WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, status);
            stm.setLong(2, id);
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("WalletTransactionDAO.updateStatus: " + e.getMessage());
            return false;
        }
    }

    public String generateReferenceCode() {
        return "REF" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private WalletTransaction mapResultSetToWalletTransaction(ResultSet rs) throws Exception {
        WalletTransaction transaction = new WalletTransaction();
        transaction.setId(rs.getLong("id"));
        transaction.setUserId(rs.getLong("user_id"));
        transaction.setType(rs.getString("type"));
        transaction.setAmount(rs.getDouble("amount"));
        transaction.setBalance(rs.getDouble("balance"));
        transaction.setStatus(rs.getString("status"));
        transaction.setReferenceCode(rs.getString("reference_code"));
        transaction.setQrUrl(rs.getString("qr_url"));
        transaction.setBankCode(rs.getString("bank_code"));
        Timestamp paymentTime = rs.getTimestamp("payment_time");
        if (paymentTime != null) {
            transaction.setPaymentTime(paymentTime.toLocalDateTime());
        }
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            transaction.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            transaction.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return transaction;
    }
}


