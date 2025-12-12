package model;

public class Product {
    private long id;
    private long categoryId;
    private long providerId;
    private String name;
    private String description;
    private String imageUrl;
    private double costPrice;
    private double sellPrice;
    private double discountPercent;
    private int quantity;
    private String status;
    
    // Computed fields (for display with JOIN)
    private String categoryName;
    private String providerName;

    public Product() {}

    public Product(long id, long categoryId, long providerId, String name, String description,
                   double costPrice, double sellPrice, double discountPercent, int quantity, String status) {
        this.id = id;
        this.categoryId = categoryId;
        this.providerId = providerId;
        this.name = name;
        this.description = description;
        this.costPrice = costPrice;
        this.sellPrice = sellPrice;
        this.discountPercent = discountPercent;
        this.quantity = quantity;
        this.status = status;
    }

    // Getters & Setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getProviderId() {
        return providerId;
    }

    public void setProviderId(long providerId) {
        this.providerId = providerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    // Image URL
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    // Computed fields getters/setters
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
    
    // Computed: final price after discount
    public double getFinalPrice() {
        return sellPrice * (1 - discountPercent / 100);
    }
}
