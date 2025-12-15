package service;

import dao.UserDAO;
import dao.WalletTransactionDAO;
import model.User;
import model.WalletTransaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class WalletService {
    private WalletTransactionDAO walletTransactionDAO;
    private UserDAO userDAO;

    public WalletService() {
        this.walletTransactionDAO = new WalletTransactionDAO();
        this.userDAO = new UserDAO();
    }

    public WalletTransaction createTopupRequest(long userId, double amount) {
        User user = userDAO.findById(userId);
        if (user == null) {
            return null;
        }

        // Generate reference code
        String referenceCode = walletTransactionDAO.generateReferenceCode();

        // Create transaction
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUserId(userId);
        transaction.setType("TOPUP");
        transaction.setAmount(amount);
        transaction.setBalance(user.getWalletBalance().doubleValue()); // Current balance (not updated yet)
        transaction.setStatus("PENDING");
        transaction.setReferenceCode(referenceCode);
        transaction.setQrUrl("https://api.vietqr.io/image/" + referenceCode); // Dummy QR URL
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());

        if (walletTransactionDAO.create(transaction)) {
            return walletTransactionDAO.findByReferenceCode(referenceCode);
        }
        return null;
    }

    public boolean confirmTopup(String referenceCode) {
        WalletTransaction transaction = walletTransactionDAO.findByReferenceCode(referenceCode);
        if (transaction == null || !"PENDING".equals(transaction.getStatus())) {
            return false;
        }

        // Update transaction status
        if (!walletTransactionDAO.updateStatus(transaction.getId(), "SUCCESS")) {
            return false;
        }

        // Update user wallet balance
        User user = userDAO.findById(transaction.getUserId());
        BigDecimal newBalance = user.getWalletBalance().add(BigDecimal.valueOf(transaction.getAmount()));
        return userDAO.updateWalletBalance(transaction.getUserId(), newBalance);
    }

    public List<WalletTransaction> getTransactions(long userId) {
        return walletTransactionDAO.findByUserId(userId);
    }

    public List<WalletTransaction> getTransactionsByType(long userId, String type) {
        return walletTransactionDAO.findByUserIdAndType(userId, type);
    }

    public User getUser(long userId) {
        return userDAO.findById(userId);
    }
}

