package model;

import java.time.LocalDateTime;

public class Order {
    private long id;
    private long userId;
    private long cardInfoId;
    private LocalDateTime createdAt;
    private double originalPrice;
    private double discountPercent;
    private double finalPrice;
    private String status;
    private String receiverEmail;
    
    // Computed fields for display
    private String productName;
    private String cardCode;

    public Order() {}

    public Order(long id, long userId, long cardInfoId, LocalDateTime createdAt,
                 double originalPrice, double discountPercent, double finalPrice,
                 String status, String receiverEmail) {
        this.id = id;
        this.userId = userId;
        this.cardInfoId = cardInfoId;
        this.createdAt = createdAt;
        this.originalPrice = originalPrice;
        this.discountPercent = discountPercent;
        this.finalPrice = finalPrice;
        this.status = status;
        this.receiverEmail = receiverEmail;
    }

    // Getters & Setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getCardInfoId() { return cardInfoId; }
    public void setCardInfoId(long cardInfoId) { this.cardInfoId = cardInfoId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }

    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }

    public double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(double finalPrice) { this.finalPrice = finalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReceiverEmail() { return receiverEmail; }
    public void setReceiverEmail(String receiverEmail) { this.receiverEmail = receiverEmail; }
    
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    @Override
    public String toString() {
        return "Order{id=" + id + ", userId=" + userId + ", cardInfoId=" + cardInfoId + "}";
    }
}
