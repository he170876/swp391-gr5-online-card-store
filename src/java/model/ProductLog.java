package model;

import java.time.LocalDateTime;

public class ProductLog {
    private long id;
    private long productId;
    private Long userId; // nullable
    private String action;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private String note;
    private LocalDateTime createdAt;

    public ProductLog() {}

    public ProductLog(long id, long productId, Long userId, String action, String fieldName,
                      String oldValue, String newValue, String note, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.action = action;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.note = note;
        this.createdAt = createdAt;
    }

    // Getters & Setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "ProductLog{id=" + id + ", productId=" + productId + ", action='" + action + "'}";
    }
}
