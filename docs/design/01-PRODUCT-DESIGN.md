# Product Feature - Detailed Design Document

> **Version:** 1.0  
> **Date:** December 11, 2025  
> **Feature:** Product Management (CRUD + Search/Filter)  
> **Roles:** Staff

---

## Table of Contents

1. [Feature Overview](#1-feature-overview)
2. [Data Model](#2-data-model)
3. [DAO Layer Design](#3-dao-layer-design)
4. [Controller Layer Design](#4-controller-layer-design)
5. [Validation Rules](#5-validation-rules)
6. [UI Design](#6-ui-design)
7. [Implementation Checklist](#7-implementation-checklist)

---

## 1. Feature Overview

### 1.1 Functional Requirements

| Function | Description | Roles |
|----------|-------------|-------|
| **List Products** | Display all products with pagination | Staff |
| **Search Products** | Search by name, category, provider | Staff |
| **Filter Products** | Filter by category, provider, status, price range | Staff |
| **View Detail** | View full product details with card inventory | Staff |
| **Create Product** | Add new product to system | Staff |
| **Update Product** | Edit product information | Staff |
| **Delete Product** | Soft delete (set status to INACTIVE) | Staff |

### 1.2 URL Mapping

| URL | Method | Controller | Description |
|-----|--------|------------|-------------|
| `/staff/product` | GET | `ProductListController` | List all products |
| `/staff/product/detail` | GET | `ProductDetailController` | View product detail |
| `/staff/product/add` | GET | `ProductAddController` | Show add form |
| `/staff/product/add` | POST | `ProductAddController` | Submit add form |
| `/staff/product/edit` | GET | `ProductEditController` | Show edit form |
| `/staff/product/edit` | POST | `ProductEditController` | Submit edit form |
| `/staff/product/delete` | POST | `ProductDeleteController` | Delete product |

---

## 2. Data Model

### 2.1 Database Table: `Product`

```sql
CREATE TABLE Product (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    category_id BIGINT NOT NULL,
    provider_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    image_url VARCHAR(500),
    cost_price DECIMAL(15,2) NOT NULL,
    sell_price DECIMAL(15,2) NOT NULL,
    discount_percent DECIMAL(5,2) NOT NULL DEFAULT 0,
    quantity INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE','INACTIVE')),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES Category(id),
    CONSTRAINT fk_product_provider FOREIGN KEY (provider_id) REFERENCES Provider(id)
);
```

### 2.2 Java Model: `Product.java`

```java
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
    
    // Computed fields (for display)
    private String categoryName;   // JOIN from Category
    private String providerName;   // JOIN from Provider
    
    // Constructors, Getters, Setters
}
```

### 2.3 DTO Classes

#### ProductSearchDTO (Search Parameters)

```java
package dto;

public class ProductSearchDTO {
    private String keyword;         // Search in name, description
    private Long categoryId;        // Filter by category
    private Long providerId;        // Filter by provider
    private String status;          // Filter by status
    private Double minPrice;        // Price range min
    private Double maxPrice;        // Price range max
    private int page = 1;           // Pagination
    private int pageSize = 10;      // Items per page
    private String sortBy = "id";   // Sort column
    private String sortDir = "DESC"; // Sort direction
    
    // Getters, Setters
}
```

#### ProductFormDTO (Create/Edit Form)

```java
package dto;

public class ProductFormDTO {
    private Long id;                // null for create, populated for edit
    private Long categoryId;
    private Long providerId;
    private String name;
    private String description;
    private String imageUrl;
    private Double costPrice;
    private Double sellPrice;
    private Double discountPercent;
    private String status;
    
    // Validation errors
    private Map<String, String> errors = new HashMap<>();
    
    // Getters, Setters
}
```

---

## 3. DAO Layer Design

### 3.1 ProductDAO.java

```java
package dao;

import java.sql.*;
import java.util.*;
import model.Product;
import dto.ProductSearchDTO;
import util.DBContext;

public class ProductDAO extends DBContext {
    
    PreparedStatement stm;
    ResultSet rs;
    
    // =========================================
    // 1. GET ALL PRODUCTS (with Category/Provider names)
    // =========================================
    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        try {
            String sql = """
                SELECT p.*, c.name AS category_name, pr.name AS provider_name
                FROM Product p
                INNER JOIN Category c ON p.category_id = c.id
                INNER JOIN Provider pr ON p.provider_id = pr.id
                ORDER BY p.id DESC
                """;
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (Exception e) {
            System.out.println("ProductDAO.getAll: " + e.getMessage());
        }
        return list;
    }
    
    // =========================================
    // 2. GET PRODUCT BY ID
    // =========================================
    public Product getById(long id) {
        try {
            String sql = """
                SELECT p.*, c.name AS category_name, pr.name AS provider_name
                FROM Product p
                INNER JOIN Category c ON p.category_id = c.id
                INNER JOIN Provider pr ON p.provider_id = pr.id
                WHERE p.id = ?
                """;
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
        } catch (Exception e) {
            System.out.println("ProductDAO.getById: " + e.getMessage());
        }
        return null;
    }
    
    // =========================================
    // 3. SEARCH & FILTER WITH PAGINATION
    // =========================================
    public List<Product> search(ProductSearchDTO dto) {
        List<Product> list = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder("""
                SELECT p.*, c.name AS category_name, pr.name AS provider_name
                FROM Product p
                INNER JOIN Category c ON p.category_id = c.id
                INNER JOIN Provider pr ON p.provider_id = pr.id
                WHERE 1=1
                """);
            
            List<Object> params = new ArrayList<>();
            
            // Dynamic WHERE clauses
            if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
                sql.append(" AND (p.name LIKE ? OR p.description LIKE ?)");
                params.add("%" + dto.getKeyword() + "%");
                params.add("%" + dto.getKeyword() + "%");
            }
            
            if (dto.getCategoryId() != null) {
                sql.append(" AND p.category_id = ?");
                params.add(dto.getCategoryId());
            }
            
            if (dto.getProviderId() != null) {
                sql.append(" AND p.provider_id = ?");
                params.add(dto.getProviderId());
            }
            
            if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
                sql.append(" AND p.status = ?");
                params.add(dto.getStatus());
            }
            
            if (dto.getMinPrice() != null) {
                sql.append(" AND p.sell_price >= ?");
                params.add(dto.getMinPrice());
            }
            
            if (dto.getMaxPrice() != null) {
                sql.append(" AND p.sell_price <= ?");
                params.add(dto.getMaxPrice());
            }
            
            // Sorting
            String sortColumn = switch (dto.getSortBy()) {
                case "name" -> "p.name";
                case "price" -> "p.sell_price";
                case "quantity" -> "p.quantity";
                case "category" -> "c.name";
                default -> "p.id";
            };
            sql.append(" ORDER BY ").append(sortColumn);
            sql.append(" ".equals(dto.getSortDir()) || "ASC".equalsIgnoreCase(dto.getSortDir()) ? " ASC" : " DESC");
            
            // Pagination
            sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            params.add((dto.getPage() - 1) * dto.getPageSize());
            params.add(dto.getPageSize());
            
            stm = connection.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stm.setObject(i + 1, params.get(i));
            }
            
            rs = stm.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (Exception e) {
            System.out.println("ProductDAO.search: " + e.getMessage());
        }
        return list;
    }
    
    // =========================================
    // 4. COUNT FOR PAGINATION
    // =========================================
    public int count(ProductSearchDTO dto) {
        try {
            StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) FROM Product p
                WHERE 1=1
                """);
            
            List<Object> params = new ArrayList<>();
            
            if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
                sql.append(" AND (p.name LIKE ? OR p.description LIKE ?)");
                params.add("%" + dto.getKeyword() + "%");
                params.add("%" + dto.getKeyword() + "%");
            }
            
            if (dto.getCategoryId() != null) {
                sql.append(" AND p.category_id = ?");
                params.add(dto.getCategoryId());
            }
            
            if (dto.getProviderId() != null) {
                sql.append(" AND p.provider_id = ?");
                params.add(dto.getProviderId());
            }
            
            if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
                sql.append(" AND p.status = ?");
                params.add(dto.getStatus());
            }
            
            stm = connection.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stm.setObject(i + 1, params.get(i));
            }
            
            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("ProductDAO.count: " + e.getMessage());
        }
        return 0;
    }
    
    // =========================================
    // 5. INSERT NEW PRODUCT
    // =========================================
    public long insert(Product p) {
        try {
            String sql = """
                INSERT INTO Product (category_id, provider_id, name, description, image_url,
                                    cost_price, sell_price, discount_percent, quantity, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
            stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stm.setLong(1, p.getCategoryId());
            stm.setLong(2, p.getProviderId());
            stm.setString(3, p.getName());
            stm.setString(4, p.getDescription());
            stm.setString(5, p.getImageUrl());
            stm.setDouble(6, p.getCostPrice());
            stm.setDouble(7, p.getSellPrice());
            stm.setDouble(8, p.getDiscountPercent());
            stm.setInt(9, p.getQuantity());
            stm.setString(10, p.getStatus());
            
            int rows = stm.executeUpdate();
            if (rows > 0) {
                rs = stm.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (Exception e) {
            System.out.println("ProductDAO.insert: " + e.getMessage());
        }
        return -1;
    }
    
    // =========================================
    // 6. UPDATE PRODUCT
    // =========================================
    public boolean update(Product p) {
        try {
            String sql = """
                UPDATE Product 
                SET category_id = ?, provider_id = ?, name = ?, description = ?, image_url = ?,
                    cost_price = ?, sell_price = ?, discount_percent = ?, status = ?
                WHERE id = ?
                """;
            stm = connection.prepareStatement(sql);
            stm.setLong(1, p.getCategoryId());
            stm.setLong(2, p.getProviderId());
            stm.setString(3, p.getName());
            stm.setString(4, p.getDescription());
            stm.setString(5, p.getImageUrl());
            stm.setDouble(6, p.getCostPrice());
            stm.setDouble(7, p.getSellPrice());
            stm.setDouble(8, p.getDiscountPercent());
            stm.setString(9, p.getStatus());
            stm.setLong(10, p.getId());
            
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ProductDAO.update: " + e.getMessage());
        }
        return false;
    }
    
    // =========================================
    // 7. SOFT DELETE (Set status to INACTIVE)
    // =========================================
    public boolean delete(long id) {
        try {
            String sql = "UPDATE Product SET status = 'INACTIVE' WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ProductDAO.delete: " + e.getMessage());
        }
        return false;
    }
    
    // =========================================
    // 8. CHECK NAME EXISTS (for validation)
    // =========================================
    public boolean isNameExists(String name, Long excludeId) {
        try {
            String sql = "SELECT 1 FROM Product WHERE name = ? AND id != ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, name);
            stm.setLong(2, excludeId == null ? -1 : excludeId);
            rs = stm.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("ProductDAO.isNameExists: " + e.getMessage());
        }
        return false;
    }
    
    // =========================================
    // HELPER: Map ResultSet to Product
    // =========================================
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setCategoryId(rs.getLong("category_id"));
        p.setProviderId(rs.getLong("provider_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setImageUrl(rs.getString("image_url"));
        p.setCostPrice(rs.getDouble("cost_price"));
        p.setSellPrice(rs.getDouble("sell_price"));
        p.setDiscountPercent(rs.getDouble("discount_percent"));
        p.setQuantity(rs.getInt("quantity"));
        p.setStatus(rs.getString("status"));
        
        // Join fields
        try {
            p.setCategoryName(rs.getString("category_name"));
            p.setProviderName(rs.getString("provider_name"));
        } catch (SQLException ignored) {}
        
        return p;
    }
}
```

---

## 4. Controller Layer Design

### 4.1 ProductListController.java

```java
package controller.product;

import dao.ProductDAO;
import dao.CategoryDAO;
import dao.ProviderDAO;
import dto.ProductSearchDTO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet(name = "ProductListController", urlPatterns = {"/staff/product"})
public class ProductListController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Parse search parameters
        ProductSearchDTO searchDTO = new ProductSearchDTO();
        searchDTO.setKeyword(Optional.ofNullable(request.getParameter("keyword")).orElse(""));
        
        String categoryIdStr = request.getParameter("categoryId");
        if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            searchDTO.setCategoryId(Long.parseLong(categoryIdStr));
        }
        
        String providerIdStr = request.getParameter("providerId");
        if (providerIdStr != null && !providerIdStr.isEmpty()) {
            searchDTO.setProviderId(Long.parseLong(providerIdStr));
        }
        
        searchDTO.setStatus(Optional.ofNullable(request.getParameter("status")).orElse(""));
        
        String pageStr = request.getParameter("page");
        searchDTO.setPage(pageStr != null ? Integer.parseInt(pageStr) : 1);
        
        // 2. Get data
        ProductDAO productDAO = new ProductDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        ProviderDAO providerDAO = new ProviderDAO();
        
        List<Product> products = productDAO.search(searchDTO);
        int totalCount = productDAO.count(searchDTO);
        int totalPages = (int) Math.ceil((double) totalCount / searchDTO.getPageSize());
        
        // 3. Set attributes
        request.setAttribute("products", products);
        request.setAttribute("searchDTO", searchDTO);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("categories", categoryDAO.getAllActive());
        request.setAttribute("providers", providerDAO.getAllActive());
        
        // 4. Forward to JSP (uses staff.jsp as master layout)
        request.setAttribute("contentPage", "staff-product-list.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
}
```

### 4.2 ProductAddController.java

```java
package controller.product;

import dao.ProductDAO;
import dao.CategoryDAO;
import dao.ProviderDAO;
import dto.ProductFormDTO;
import model.Product;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet(name = "ProductAddController", urlPatterns = {"/staff/product/add"})
public class ProductAddController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Load dropdown data
        CategoryDAO categoryDAO = new CategoryDAO();
        ProviderDAO providerDAO = new ProviderDAO();
        
        request.setAttribute("categories", categoryDAO.getAllActive());
        request.setAttribute("providers", providerDAO.getAllActive());
        request.setAttribute("formDTO", new ProductFormDTO());
        
        // Forward to JSP (uses staff.jsp as master layout)
        request.setAttribute("contentPage", "staff-product-add.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Parse form data
        ProductFormDTO formDTO = parseFormData(request);
        
        // 2. Validate
        Map<String, String> errors = validateForm(formDTO, null);
        
        if (!errors.isEmpty()) {
            // Reload dropdowns and show errors
            CategoryDAO categoryDAO = new CategoryDAO();
            ProviderDAO providerDAO = new ProviderDAO();
            
            request.setAttribute("categories", categoryDAO.getAllActive());
            request.setAttribute("providers", providerDAO.getAllActive());
            request.setAttribute("formDTO", formDTO);
            request.setAttribute("errors", errors);
            
            // Forward to JSP (uses staff.jsp as master layout)
            request.setAttribute("contentPage", "staff-product-add.jsp");
            request.getRequestDispatcher("/staff.jsp").forward(request, response);
            return;
        }
        
        // 3. Create product
        Product product = new Product();
        product.setCategoryId(formDTO.getCategoryId());
        product.setProviderId(formDTO.getProviderId());
        product.setName(formDTO.getName().trim());
        product.setDescription(formDTO.getDescription());
        product.setImageUrl(formDTO.getImageUrl());
        product.setCostPrice(formDTO.getCostPrice());
        product.setSellPrice(formDTO.getSellPrice());
        product.setDiscountPercent(formDTO.getDiscountPercent() != null ? formDTO.getDiscountPercent() : 0);
        product.setQuantity(0); // Quantity managed by CardInfo
        product.setStatus(formDTO.getStatus());
        
        ProductDAO dao = new ProductDAO();
        long newId = dao.insert(product);
        
        if (newId > 0) {
            request.getSession().setAttribute("successMessage", "Product created successfully!");
            response.sendRedirect(request.getContextPath() + "/staff/product");
        } else {
            request.setAttribute("errorMessage", "Failed to create product. Please try again.");
            doGet(request, response);
        }
    }
    
    private ProductFormDTO parseFormData(HttpServletRequest request) {
        ProductFormDTO dto = new ProductFormDTO();
        
        try {
            dto.setCategoryId(Long.parseLong(request.getParameter("categoryId")));
        } catch (Exception e) {
            dto.setCategoryId(null);
        }
        
        try {
            dto.setProviderId(Long.parseLong(request.getParameter("providerId")));
        } catch (Exception e) {
            dto.setProviderId(null);
        }
        
        dto.setName(request.getParameter("name"));
        dto.setDescription(request.getParameter("description"));
        dto.setImageUrl(request.getParameter("imageUrl"));
        
        try {
            dto.setCostPrice(Double.parseDouble(request.getParameter("costPrice")));
        } catch (Exception e) {
            dto.setCostPrice(null);
        }
        
        try {
            dto.setSellPrice(Double.parseDouble(request.getParameter("sellPrice")));
        } catch (Exception e) {
            dto.setSellPrice(null);
        }
        
        try {
            dto.setDiscountPercent(Double.parseDouble(request.getParameter("discountPercent")));
        } catch (Exception e) {
            dto.setDiscountPercent(0.0);
        }
        
        dto.setStatus(Optional.ofNullable(request.getParameter("status")).orElse("ACTIVE"));
        
        return dto;
    }
    
    private Map<String, String> validateForm(ProductFormDTO dto, Long excludeId) {
        Map<String, String> errors = new HashMap<>();
        
        // See Section 5 for validation rules
        
        return errors;
    }
}
```

### 4.3 ProductEditController.java

```java
package controller.product;

@WebServlet(name = "ProductEditController", urlPatterns = {"/staff/product/edit"})
public class ProductEditController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/staff/product");
            return;
        }
        
        ProductDAO dao = new ProductDAO();
        Product product = dao.getById(Long.parseLong(idStr));
        
        if (product == null) {
            request.getSession().setAttribute("errorMessage", "Product not found");
            response.sendRedirect(request.getContextPath() + "/staff/product");
            return;
        }
        
        // Convert to DTO
        ProductFormDTO formDTO = new ProductFormDTO();
        formDTO.setId(product.getId());
        formDTO.setCategoryId(product.getCategoryId());
        formDTO.setProviderId(product.getProviderId());
        formDTO.setName(product.getName());
        formDTO.setDescription(product.getDescription());
        formDTO.setImageUrl(product.getImageUrl());
        formDTO.setCostPrice(product.getCostPrice());
        formDTO.setSellPrice(product.getSellPrice());
        formDTO.setDiscountPercent(product.getDiscountPercent());
        formDTO.setStatus(product.getStatus());
        
        // Load dropdowns
        CategoryDAO categoryDAO = new CategoryDAO();
        ProviderDAO providerDAO = new ProviderDAO();
        
        request.setAttribute("categories", categoryDAO.getAllActive());
        request.setAttribute("providers", providerDAO.getAllActive());
        request.setAttribute("formDTO", formDTO);
        
        // Forward to JSP (uses staff.jsp as master layout)
        request.setAttribute("contentPage", "staff-product-edit.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Similar to ProductAddController.doPost() but calls dao.update()
    }
}
```

### 4.4 ProductDeleteController.java

```java
package controller.product;

@WebServlet(name = "ProductDeleteController", urlPatterns = {"/staff/product/delete"})
public class ProductDeleteController extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/staff/product");
            return;
        }
        
        ProductDAO dao = new ProductDAO();
        boolean success = dao.delete(Long.parseLong(idStr));
        
        if (success) {
            request.getSession().setAttribute("successMessage", "Product deleted successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to delete product");
        }
        
        response.sendRedirect(request.getContextPath() + "/staff/product");
    }
}
```

---

## 5. Validation Rules

### 5.1 Field Validation Table

| Field | Type | Required | Min | Max | Rules |
|-------|------|----------|-----|-----|-------|
| `categoryId` | Long | ✓ | - | - | Must exist in Category table, must be ACTIVE |
| `providerId` | Long | ✓ | - | - | Must exist in Provider table, must be ACTIVE |
| `name` | String | ✓ | 3 | 100 | Unique, alphanumeric + spaces |
| `description` | String | - | 0 | 255 | - |
| `imageUrl` | String | - | 0 | 500 | Valid URL format (http/https), or empty |
| `costPrice` | Double | ✓ | 0.01 | 999999999 | Must be > 0 |
| `sellPrice` | Double | ✓ | 0.01 | 999999999 | Must be >= costPrice |
| `discountPercent` | Double | - | 0 | 100 | Default: 0 |
| `status` | String | ✓ | - | - | Must be 'ACTIVE' or 'INACTIVE' |

### 5.2 Validation Code

```java
private Map<String, String> validateForm(ProductFormDTO dto, Long excludeId) {
    Map<String, String> errors = new HashMap<>();
    
    // Category validation
    if (dto.getCategoryId() == null) {
        errors.put("categoryId", "Category is required");
    } else {
        CategoryDAO catDAO = new CategoryDAO();
        if (catDAO.getById(dto.getCategoryId()) == null) {
            errors.put("categoryId", "Invalid category selected");
        }
    }
    
    // Provider validation
    if (dto.getProviderId() == null) {
        errors.put("providerId", "Provider is required");
    } else {
        ProviderDAO provDAO = new ProviderDAO();
        if (provDAO.getById(dto.getProviderId()) == null) {
            errors.put("providerId", "Invalid provider selected");
        }
    }
    
    // Name validation
    if (dto.getName() == null || dto.getName().trim().isEmpty()) {
        errors.put("name", "Product name is required");
    } else if (dto.getName().trim().length() < 3) {
        errors.put("name", "Product name must be at least 3 characters");
    } else if (dto.getName().trim().length() > 100) {
        errors.put("name", "Product name must not exceed 100 characters");
    } else {
        ProductDAO dao = new ProductDAO();
        if (dao.isNameExists(dto.getName().trim(), excludeId)) {
            errors.put("name", "Product name already exists");
        }
    }
    
    // Description validation
    if (dto.getDescription() != null && dto.getDescription().length() > 255) {
        errors.put("description", "Description must not exceed 255 characters");
    }
    
    // Image URL validation
    if (dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty()) {
        String url = dto.getImageUrl().trim();
        if (url.length() > 500) {
            errors.put("imageUrl", "Image URL must not exceed 500 characters");
        } else if (!url.startsWith("http://") && !url.startsWith("https://")) {
            errors.put("imageUrl", "Image URL must start with http:// or https://");
        }
    }
    
    // Cost price validation
    if (dto.getCostPrice() == null) {
        errors.put("costPrice", "Cost price is required");
    } else if (dto.getCostPrice() <= 0) {
        errors.put("costPrice", "Cost price must be greater than 0");
    }
    
    // Sell price validation
    if (dto.getSellPrice() == null) {
        errors.put("sellPrice", "Sell price is required");
    } else if (dto.getSellPrice() <= 0) {
        errors.put("sellPrice", "Sell price must be greater than 0");
    } else if (dto.getCostPrice() != null && dto.getSellPrice() < dto.getCostPrice()) {
        errors.put("sellPrice", "Sell price must be greater than or equal to cost price");
    }
    
    // Discount percent validation
    if (dto.getDiscountPercent() != null) {
        if (dto.getDiscountPercent() < 0 || dto.getDiscountPercent() > 100) {
            errors.put("discountPercent", "Discount must be between 0 and 100");
        }
    }
    
    // Status validation
    if (dto.getStatus() == null || dto.getStatus().isEmpty()) {
        errors.put("status", "Status is required");
    } else if (!dto.getStatus().equals("ACTIVE") && !dto.getStatus().equals("INACTIVE")) {
        errors.put("status", "Invalid status");
    }
    
    return errors;
}
```

---

## 6. UI Design

### 6.1 List Page (`/admin/product/list.jsp`)

```
┌─────────────────────────────────────────────────────────────────────┐
│ Page Header                                                         │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ Product List                                    [+ Add Product] │ │
│ │ Manage your products                                            │ │
│ └─────────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────┤
│ Filter Card (Collapsible)                                           │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ [Search...] [Category ▼] [Provider ▼] [Status ▼] [🔍 Search]   │ │
│ └─────────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────┤
│ Data Table                                                          │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ □ │ Name      │ Category │ Provider │ Price  │ Qty │ Status │ ⚡│ │
│ ├───┼───────────┼──────────┼──────────┼────────┼─────┼────────┼───┤ │
│ │ □ │ Garena 50 │ Game     │ Garena   │ 50,000 │ 100 │ ACTIVE │ ⋮ │ │
│ │ □ │ Garena100 │ Game     │ Garena   │100,000 │  50 │ ACTIVE │ ⋮ │ │
│ │ □ │ Viettel50 │ Telco    │ Viettel  │ 50,000 │ 200 │ ACTIVE │ ⋮ │ │
│ └─────────────────────────────────────────────────────────────────┘ │
│ Showing 1-10 of 50 entries          [< 1 2 3 4 5 >]                │
└─────────────────────────────────────────────────────────────────────┘

Action Dropdown (⋮):
┌───────────────┐
│ 👁 View       │
│ ✏️ Edit       │
│ 🗑️ Delete     │
└───────────────┘
```

### 6.2 Add/Edit Form (`/admin/product/add.jsp`)

```
┌─────────────────────────────────────────────────────────────────────┐
│ Page Header                                                         │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ Add New Product                                                 │ │
│ │ Create a new product                                            │ │
│ └─────────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────┤
│ Form Card                                                           │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ Basic Information                                               │ │
│ │ ┌─────────────────────┐  ┌─────────────────────┐               │ │
│ │ │ Product Name *      │  │ Category *          │               │ │
│ │ │ [________________]  │  │ [Select Category ▼] │               │ │
│ │ └─────────────────────┘  └─────────────────────┘               │ │
│ │ ┌─────────────────────┐  ┌─────────────────────┐               │ │
│ │ │ Provider *          │  │ Status *            │               │ │
│ │ │ [Select Provider ▼] │  │ [● Active ○ Inactive]│               │ │
│ │ └─────────────────────┘  └─────────────────────┘               │ │
│ │                                                                 │ │
│ │ Description                                                     │ │
│ │ ┌───────────────────────────────────────────────────────────┐  │ │
│ │ │                                                           │  │ │
│ │ └───────────────────────────────────────────────────────────┘  │ │
│ │                                                                 │ │
│ │ Image URL                                                       │ │
│ │ ┌───────────────────────────────────────────────────────────┐  │ │
│ │ │ https://example.com/image.jpg                             │  │ │
│ │ └───────────────────────────────────────────────────────────┘  │ │
│ │                                                                 │ │
│ │ Pricing Information                                             │ │
│ │ ┌────────────────┐  ┌────────────────┐  ┌────────────────┐     │ │
│ │ │ Cost Price *   │  │ Sell Price *   │  │ Discount (%)   │     │ │
│ │ │ [__________]   │  │ [__________]   │  │ [__________]   │     │ │
│ │ └────────────────┘  └────────────────┘  └────────────────┘     │ │
│ │                                                                 │ │
│ │              [Cancel]  [Submit]                                │ │
│ └─────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
```

### 6.3 JSP File Structure

```
web/
├── staff.jsp                    # Master layout for staff pages
├── staff-product-list.jsp       # Product list with DataTable
├── staff-product-add.jsp        # Add product form
├── staff-product-edit.jsp       # Edit product form
└── staff-product-detail.jsp     # Product detail view
```

---

## 7. Implementation Checklist

### 7.1 Backend Tasks

- [ ] Create `ProductSearchDTO.java` in `dto/` package
- [ ] Create `ProductFormDTO.java` in `dto/` package
- [ ] Create `ProductDAO.java` with all methods
- [ ] Create `ProductListController.java`
- [ ] Create `ProductAddController.java`
- [ ] Create `ProductEditController.java`
- [ ] Create `ProductDeleteController.java`
- [ ] Create `ProductDetailController.java`
- [ ] Add `categoryName`, `providerName` fields to `Product.java`

### 7.2 Frontend Tasks

- [ ] Create `web/admin/product/` directory
- [ ] Create `list.jsp` with DataTable integration
- [ ] Create `add.jsp` with form validation
- [ ] Create `edit.jsp` with pre-populated data
- [ ] Create `detail.jsp` with product info + card inventory
- [ ] Add Product menu item to sidebar

### 7.3 Testing Tasks

- [ ] Test CRUD operations
- [ ] Test search/filter functionality
- [ ] Test pagination
- [ ] Test validation (client + server)
- [ ] Test authorization (Staff only)

---

**End of Product Feature Design Document**
