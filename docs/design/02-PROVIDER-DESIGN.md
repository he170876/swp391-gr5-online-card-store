# Provider Feature - Detailed Design Document

> **Version:** 1.0  
> **Date:** December 11, 2025  
> **Feature:** Provider Management (CRUD + Search/Filter)  
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
| **List Providers** | Display all providers with pagination | Staff |
| **Search Providers** | Search by name, contact info | Staff |
| **Filter Providers** | Filter by status | Staff |
| **View Detail** | View provider details with related products | Staff |
| **Create Provider** | Add new provider to system | Staff |
| **Update Provider** | Edit provider information | Staff |
| **Delete Provider** | Soft delete (set status to INACTIVE) | Staff |

### 1.2 URL Mapping

| URL | Method | Controller | Description |
|-----|--------|------------|-------------|
| `/staff/provider` | GET | `ProviderListController` | List all providers |
| `/staff/provider/detail` | GET | `ProviderDetailController` | View provider detail |
| `/staff/provider/add` | GET | `ProviderAddController` | Show add form |
| `/staff/provider/add` | POST | `ProviderAddController` | Submit add form |
| `/staff/provider/edit` | GET | `ProviderEditController` | Show edit form |
| `/staff/provider/edit` | POST | `ProviderEditController` | Submit edit form |
| `/staff/provider/delete` | POST | `ProviderDeleteController` | Delete provider |

---

## 2. Data Model

### 2.1 Database Table: `Provider`

```sql
CREATE TABLE Provider (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_info VARCHAR(255),
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE','INACTIVE'))
);
```

### 2.2 Java Model: `Provider.java`

```java
package model;

public class Provider {
    private long id;
    private String name;
    private String contactInfo;
    private String status;
    
    // Computed fields (for display)
    private int productCount;    // COUNT of related products
    
    // Constructors
    public Provider() {}
    
    public Provider(long id, String name, String contactInfo, String status) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
        this.status = status;
    }
    
    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getProductCount() { return productCount; }
    public void setProductCount(int productCount) { this.productCount = productCount; }
    
    @Override
    public String toString() {
        return "Provider{id=" + id + ", name='" + name + "'}";
    }
}
```

### 2.3 DTO Classes

#### ProviderSearchDTO (Search Parameters)

```java
package dto;

public class ProviderSearchDTO {
    private String keyword;         // Search in name, contact_info
    private String status;          // Filter by status
    private int page = 1;           // Pagination
    private int pageSize = 10;      // Items per page
    private String sortBy = "id";   // Sort column
    private String sortDir = "DESC"; // Sort direction
    
    // Constructors, Getters, Setters
    public ProviderSearchDTO() {}
    
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    
    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
}
```

#### ProviderFormDTO (Create/Edit Form)

```java
package dto;

import java.util.HashMap;
import java.util.Map;

public class ProviderFormDTO {
    private Long id;                // null for create, populated for edit
    private String name;
    private String contactInfo;
    private String status;
    
    // Constructors
    public ProviderFormDTO() {}
    
    // Getters, Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

---

## 3. DAO Layer Design

### 3.1 ProviderDAO.java

```java
package dao;

import java.sql.*;
import java.util.*;
import model.Provider;
import dto.ProviderSearchDTO;
import util.DBContext;

public class ProviderDAO extends DBContext {
    
    PreparedStatement stm;
    ResultSet rs;
    
    // =========================================
    // 1. GET ALL PROVIDERS
    // =========================================
    public List<Provider> getAll() {
        List<Provider> list = new ArrayList<>();
        try {
            String sql = """
                SELECT p.*, 
                       (SELECT COUNT(*) FROM Product WHERE provider_id = p.id) AS product_count
                FROM Provider p
                ORDER BY p.id DESC
                """;
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProvider(rs));
            }
        } catch (Exception e) {
            System.out.println("ProviderDAO.getAll: " + e.getMessage());
        }
        return list;
    }
    
    // =========================================
    // 2. GET ALL ACTIVE PROVIDERS (for dropdowns)
    // =========================================
    public List<Provider> getAllActive() {
        List<Provider> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM Provider WHERE status = 'ACTIVE' ORDER BY name ASC";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProvider(rs));
            }
        } catch (Exception e) {
            System.out.println("ProviderDAO.getAllActive: " + e.getMessage());
        }
        return list;
    }
    
    // =========================================
    // 3. GET PROVIDER BY ID
    // =========================================
    public Provider getById(long id) {
        try {
            String sql = """
                SELECT p.*, 
                       (SELECT COUNT(*) FROM Product WHERE provider_id = p.id) AS product_count
                FROM Provider p
                WHERE p.id = ?
                """;
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                return mapResultSetToProvider(rs);
            }
        } catch (Exception e) {
            System.out.println("ProviderDAO.getById: " + e.getMessage());
        }
        return null;
    }
    
    // =========================================
    // 4. SEARCH & FILTER WITH PAGINATION
    // =========================================
    public List<Provider> search(ProviderSearchDTO dto) {
        List<Provider> list = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder("""
                SELECT p.*, 
                       (SELECT COUNT(*) FROM Product WHERE provider_id = p.id) AS product_count
                FROM Provider p
                WHERE 1=1
                """);
            
            List<Object> params = new ArrayList<>();
            
            // Dynamic WHERE clauses
            if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
                sql.append(" AND (p.name LIKE ? OR p.contact_info LIKE ?)");
                params.add("%" + dto.getKeyword() + "%");
                params.add("%" + dto.getKeyword() + "%");
            }
            
            if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
                sql.append(" AND p.status = ?");
                params.add(dto.getStatus());
            }
            
            // Sorting
            String sortColumn = switch (dto.getSortBy()) {
                case "name" -> "p.name";
                case "status" -> "p.status";
                default -> "p.id";
            };
            sql.append(" ORDER BY ").append(sortColumn);
            sql.append("ASC".equalsIgnoreCase(dto.getSortDir()) ? " ASC" : " DESC");
            
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
                list.add(mapResultSetToProvider(rs));
            }
        } catch (Exception e) {
            System.out.println("ProviderDAO.search: " + e.getMessage());
        }
        return list;
    }
    
    // =========================================
    // 5. COUNT FOR PAGINATION
    // =========================================
    public int count(ProviderSearchDTO dto) {
        try {
            StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) FROM Provider p
                WHERE 1=1
                """);
            
            List<Object> params = new ArrayList<>();
            
            if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
                sql.append(" AND (p.name LIKE ? OR p.contact_info LIKE ?)");
                params.add("%" + dto.getKeyword() + "%");
                params.add("%" + dto.getKeyword() + "%");
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
            System.out.println("ProviderDAO.count: " + e.getMessage());
        }
        return 0;
    }
    
    // =========================================
    // 6. INSERT NEW PROVIDER
    // =========================================
    public long insert(Provider p) {
        try {
            String sql = "INSERT INTO Provider (name, contact_info, status) VALUES (?, ?, ?)";
            stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, p.getName());
            stm.setString(2, p.getContactInfo());
            stm.setString(3, p.getStatus());
            
            int rows = stm.executeUpdate();
            if (rows > 0) {
                rs = stm.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (Exception e) {
            System.out.println("ProviderDAO.insert: " + e.getMessage());
        }
        return -1;
    }
    
    // =========================================
    // 7. UPDATE PROVIDER
    // =========================================
    public boolean update(Provider p) {
        try {
            String sql = "UPDATE Provider SET name = ?, contact_info = ?, status = ? WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, p.getName());
            stm.setString(2, p.getContactInfo());
            stm.setString(3, p.getStatus());
            stm.setLong(4, p.getId());
            
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ProviderDAO.update: " + e.getMessage());
        }
        return false;
    }
    
    // =========================================
    // 8. SOFT DELETE (Set status to INACTIVE)
    // =========================================
    public boolean delete(long id) {
        try {
            String sql = "UPDATE Provider SET status = 'INACTIVE' WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ProviderDAO.delete: " + e.getMessage());
        }
        return false;
    }
    
    // =========================================
    // 9. CHECK NAME EXISTS (for validation)
    // =========================================
    public boolean isNameExists(String name, Long excludeId) {
        try {
            String sql = "SELECT 1 FROM Provider WHERE name = ? AND id != ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, name);
            stm.setLong(2, excludeId == null ? -1 : excludeId);
            rs = stm.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("ProviderDAO.isNameExists: " + e.getMessage());
        }
        return false;
    }
    
    // =========================================
    // 10. CHECK IF PROVIDER HAS PRODUCTS
    // =========================================
    public boolean hasProducts(long providerId) {
        try {
            String sql = "SELECT 1 FROM Product WHERE provider_id = ? AND status = 'ACTIVE'";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, providerId);
            rs = stm.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("ProviderDAO.hasProducts: " + e.getMessage());
        }
        return false;
    }
    
    // =========================================
    // HELPER: Map ResultSet to Provider
    // =========================================
    private Provider mapResultSetToProvider(ResultSet rs) throws SQLException {
        Provider p = new Provider();
        p.setId(rs.getLong("id"));
        p.setName(rs.getString("name"));
        p.setContactInfo(rs.getString("contact_info"));
        p.setStatus(rs.getString("status"));
        
        // Computed field
        try {
            p.setProductCount(rs.getInt("product_count"));
        } catch (SQLException ignored) {}
        
        return p;
    }
}
```

---

## 4. Controller Layer Design

### 4.1 ProviderListController.java

```java
package controller.provider;

import dao.ProviderDAO;
import dto.ProviderSearchDTO;
import model.Provider;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet(name = "ProviderListController", urlPatterns = {"/staff/provider"})
public class ProviderListController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Parse search parameters
        ProviderSearchDTO searchDTO = new ProviderSearchDTO();
        searchDTO.setKeyword(Optional.ofNullable(request.getParameter("keyword")).orElse(""));
        searchDTO.setStatus(Optional.ofNullable(request.getParameter("status")).orElse(""));
        
        String pageStr = request.getParameter("page");
        searchDTO.setPage(pageStr != null && !pageStr.isEmpty() ? Integer.parseInt(pageStr) : 1);
        
        // 2. Get data
        ProviderDAO dao = new ProviderDAO();
        List<Provider> providers = dao.search(searchDTO);
        int totalCount = dao.count(searchDTO);
        int totalPages = (int) Math.ceil((double) totalCount / searchDTO.getPageSize());
        
        // 3. Set attributes
        request.setAttribute("providers", providers);
        request.setAttribute("searchDTO", searchDTO);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("totalPages", totalPages);
        
        // 4. Forward to JSP (uses staff.jsp as master layout)
        request.setAttribute("contentPage", "staff-provider-list.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
}
```

### 4.2 ProviderAddController.java

```java
package controller.provider;

import dao.ProviderDAO;
import dto.ProviderFormDTO;
import model.Provider;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet(name = "ProviderAddController", urlPatterns = {"/staff/provider/add"})
public class ProviderAddController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setAttribute("formDTO", new ProviderFormDTO());
        // Forward to JSP (uses staff.jsp as master layout)
        request.setAttribute("contentPage", "staff-provider-add.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Parse form data
        ProviderFormDTO formDTO = new ProviderFormDTO();
        formDTO.setName(request.getParameter("name"));
        formDTO.setContactInfo(request.getParameter("contactInfo"));
        formDTO.setStatus(Optional.ofNullable(request.getParameter("status")).orElse("ACTIVE"));
        
        // 2. Validate
        Map<String, String> errors = validateForm(formDTO, null);
        
        if (!errors.isEmpty()) {
            request.setAttribute("formDTO", formDTO);
            request.setAttribute("errors", errors);
            // Forward to JSP (uses staff.jsp as master layout)
            request.setAttribute("contentPage", "staff-provider-add.jsp");
            request.getRequestDispatcher("/staff.jsp").forward(request, response);
            return;
        }
        
        // 3. Create provider
        Provider provider = new Provider();
        provider.setName(formDTO.getName().trim());
        provider.setContactInfo(formDTO.getContactInfo());
        provider.setStatus(formDTO.getStatus());
        
        ProviderDAO dao = new ProviderDAO();
        long newId = dao.insert(provider);
        
        if (newId > 0) {
            request.getSession().setAttribute("successMessage", "Provider created successfully!");
            response.sendRedirect(request.getContextPath() + "/staff/provider");
        } else {
            request.setAttribute("errorMessage", "Failed to create provider. Please try again.");
            doGet(request, response);
        }
    }
    
    private Map<String, String> validateForm(ProviderFormDTO dto, Long excludeId) {
        Map<String, String> errors = new HashMap<>();
        
        // Name validation
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            errors.put("name", "Provider name is required");
        } else if (dto.getName().trim().length() < 2) {
            errors.put("name", "Provider name must be at least 2 characters");
        } else if (dto.getName().trim().length() > 100) {
            errors.put("name", "Provider name must not exceed 100 characters");
        } else {
            ProviderDAO dao = new ProviderDAO();
            if (dao.isNameExists(dto.getName().trim(), excludeId)) {
                errors.put("name", "Provider name already exists");
            }
        }
        
        // Contact info validation (optional but max length)
        if (dto.getContactInfo() != null && dto.getContactInfo().length() > 255) {
            errors.put("contactInfo", "Contact info must not exceed 255 characters");
        }
        
        // Status validation
        if (dto.getStatus() == null || dto.getStatus().isEmpty()) {
            errors.put("status", "Status is required");
        } else if (!dto.getStatus().equals("ACTIVE") && !dto.getStatus().equals("INACTIVE")) {
            errors.put("status", "Invalid status");
        }
        
        return errors;
    }
}
```

### 4.3 ProviderEditController.java

```java
package controller.provider;

import dao.ProviderDAO;
import dto.ProviderFormDTO;
import model.Provider;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet(name = "ProviderEditController", urlPatterns = {"/staff/provider/edit"})
public class ProviderEditController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/staff/provider");
            return;
        }
        
        ProviderDAO dao = new ProviderDAO();
        Provider provider = dao.getById(Long.parseLong(idStr));
        
        if (provider == null) {
            request.getSession().setAttribute("errorMessage", "Provider not found");
            response.sendRedirect(request.getContextPath() + "/staff/provider");
            return;
        }
        
        // Convert to DTO
        ProviderFormDTO formDTO = new ProviderFormDTO();
        formDTO.setId(provider.getId());
        formDTO.setName(provider.getName());
        formDTO.setContactInfo(provider.getContactInfo());
        formDTO.setStatus(provider.getStatus());
        
        request.setAttribute("formDTO", formDTO);
        // Forward to JSP (uses staff.jsp as master layout)
        request.setAttribute("contentPage", "staff-provider-edit.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Parse form data
        ProviderFormDTO formDTO = new ProviderFormDTO();
        try {
            formDTO.setId(Long.parseLong(request.getParameter("id")));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/staff/provider");
            return;
        }
        formDTO.setName(request.getParameter("name"));
        formDTO.setContactInfo(request.getParameter("contactInfo"));
        formDTO.setStatus(Optional.ofNullable(request.getParameter("status")).orElse("ACTIVE"));
        
        // 2. Validate
        Map<String, String> errors = validateForm(formDTO, formDTO.getId());
        
        if (!errors.isEmpty()) {
            request.setAttribute("formDTO", formDTO);
            request.setAttribute("errors", errors);
            // Forward to JSP (uses staff.jsp as master layout)
            request.setAttribute("contentPage", "staff-provider-edit.jsp");
            request.getRequestDispatcher("/staff.jsp").forward(request, response);
            return;
        }
        
        // 3. Update provider
        Provider provider = new Provider();
        provider.setId(formDTO.getId());
        provider.setName(formDTO.getName().trim());
        provider.setContactInfo(formDTO.getContactInfo());
        provider.setStatus(formDTO.getStatus());
        
        ProviderDAO dao = new ProviderDAO();
        boolean success = dao.update(provider);
        
        if (success) {
            request.getSession().setAttribute("successMessage", "Provider updated successfully!");
            response.sendRedirect(request.getContextPath() + "/staff/provider");
        } else {
            request.setAttribute("errorMessage", "Failed to update provider. Please try again.");
            request.setAttribute("formDTO", formDTO);
            // Forward to JSP (uses staff.jsp as master layout)
            request.setAttribute("contentPage", "staff-provider-edit.jsp");
            request.getRequestDispatcher("/staff.jsp").forward(request, response);
        }
    }
    
    private Map<String, String> validateForm(ProviderFormDTO dto, Long excludeId) {
        // Same validation as ProviderAddController
        Map<String, String> errors = new HashMap<>();
        
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            errors.put("name", "Provider name is required");
        } else if (dto.getName().trim().length() < 2) {
            errors.put("name", "Provider name must be at least 2 characters");
        } else if (dto.getName().trim().length() > 100) {
            errors.put("name", "Provider name must not exceed 100 characters");
        } else {
            ProviderDAO dao = new ProviderDAO();
            if (dao.isNameExists(dto.getName().trim(), excludeId)) {
                errors.put("name", "Provider name already exists");
            }
        }
        
        if (dto.getContactInfo() != null && dto.getContactInfo().length() > 255) {
            errors.put("contactInfo", "Contact info must not exceed 255 characters");
        }
        
        if (dto.getStatus() == null || dto.getStatus().isEmpty()) {
            errors.put("status", "Status is required");
        } else if (!dto.getStatus().equals("ACTIVE") && !dto.getStatus().equals("INACTIVE")) {
            errors.put("status", "Invalid status");
        }
        
        return errors;
    }
}
```

### 4.4 ProviderDeleteController.java

```java
package controller.provider;

import dao.ProviderDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;

@WebServlet(name = "ProviderDeleteController", urlPatterns = {"/staff/provider/delete"})
public class ProviderDeleteController extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/staff/provider");
            return;
        }
        
        long providerId = Long.parseLong(idStr);
        ProviderDAO dao = new ProviderDAO();
        
        // Check if provider has active products
        if (dao.hasProducts(providerId)) {
            request.getSession().setAttribute("errorMessage", 
                "Cannot delete provider with active products. Please deactivate or reassign products first.");
            response.sendRedirect(request.getContextPath() + "/staff/provider");
            return;
        }
        
        boolean success = dao.delete(providerId);
        
        if (success) {
            request.getSession().setAttribute("successMessage", "Provider deleted successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to delete provider");
        }
        
        response.sendRedirect(request.getContextPath() + "/staff/provider");
    }
}
```

### 4.5 ProviderDetailController.java

```java
package controller.provider;

import dao.ProviderDAO;
import dao.ProductDAO;
import model.Provider;
import model.Product;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet(name = "ProviderDetailController", urlPatterns = {"/staff/provider/detail"})
public class ProviderDetailController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/staff/provider");
            return;
        }
        
        ProviderDAO providerDAO = new ProviderDAO();
        Provider provider = providerDAO.getById(Long.parseLong(idStr));
        
        if (provider == null) {
            request.getSession().setAttribute("errorMessage", "Provider not found");
            response.sendRedirect(request.getContextPath() + "/staff/provider");
            return;
        }
        
        // Get related products
        ProductDAO productDAO = new ProductDAO();
        List<Product> products = productDAO.getByProviderId(provider.getId());
        
        request.setAttribute("provider", provider);
        request.setAttribute("products", products);
        
        // Forward to JSP (uses staff.jsp as master layout)
        request.setAttribute("contentPage", "staff-provider-detail.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
}
```

---

## 5. Validation Rules

### 5.1 Field Validation Table

| Field | Type | Required | Min | Max | Rules |
|-------|------|----------|-----|-----|-------|
| `name` | String | ✓ | 2 | 100 | Unique, not empty |
| `contactInfo` | String | - | 0 | 255 | Optional |
| `status` | String | ✓ | - | - | Must be 'ACTIVE' or 'INACTIVE' |

### 5.2 Business Rules

| Rule | Description |
|------|-------------|
| Unique Name | Provider name must be unique across all providers |
| Delete Constraint | Cannot delete provider with active products |
| Status Change | Can change status at any time |

### 5.3 Client-Side Validation (JavaScript)

```javascript
function validateProviderForm() {
    let isValid = true;
    const errors = {};
    
    // Name validation
    const name = document.getElementById('name').value.trim();
    if (!name) {
        errors.name = 'Provider name is required';
        isValid = false;
    } else if (name.length < 2) {
        errors.name = 'Provider name must be at least 2 characters';
        isValid = false;
    } else if (name.length > 100) {
        errors.name = 'Provider name must not exceed 100 characters';
        isValid = false;
    }
    
    // Contact info validation
    const contactInfo = document.getElementById('contactInfo').value;
    if (contactInfo && contactInfo.length > 255) {
        errors.contactInfo = 'Contact info must not exceed 255 characters';
        isValid = false;
    }
    
    // Display errors
    displayValidationErrors(errors);
    
    return isValid;
}
```

---

## 6. UI Design

### 6.1 List Page (`staff-provider-list.jsp`)

```
┌─────────────────────────────────────────────────────────────────────┐
│ Page Header                                                         │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ Provider List                                   [+ Add Provider]│ │
│ │ Manage your providers                                           │ │
│ └─────────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────┤
│ Filter Card                                                         │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ [Search by name or contact...]  [Status ▼]  [🔍 Search]        │ │
│ └─────────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────┤
│ Data Table                                                          │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ □ │ Provider Name │ Contact Info        │ Products │ Status │ ⚡│ │
│ ├───┼───────────────┼─────────────────────┼──────────┼────────┼───┤ │
│ │ □ │ Garena        │ support@garena.com  │    15    │ ACTIVE │ ⋮ │ │
│ │ □ │ Viettel       │ 1800-8098           │    20    │ ACTIVE │ ⋮ │ │
│ │ □ │ Mobifone      │ 1800-1090           │    12    │ ACTIVE │ ⋮ │ │
│ └─────────────────────────────────────────────────────────────────┘ │
│ Showing 1-10 of 25 entries          [< 1 2 3 >]                    │
└─────────────────────────────────────────────────────────────────────┘

Action Dropdown (⋮):
┌───────────────┐
│ 👁 View       │
│ ✏️ Edit       │
│ 🗑️ Delete     │
└───────────────┘
```

### 6.2 Add/Edit Form (`staff-provider-add.jsp`)

```
┌─────────────────────────────────────────────────────────────────────┐
│ Page Header                                                         │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ Add New Provider                                                │ │
│ │ Create a new provider                                           │ │
│ └─────────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────┤
│ Form Card                                                           │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ Provider Information                                            │ │
│ │ ┌───────────────────────────────────┐                          │ │
│ │ │ Provider Name *                   │                          │ │
│ │ │ [_______________________________] │                          │ │
│ │ └───────────────────────────────────┘                          │ │
│ │                                                                 │ │
│ │ ┌───────────────────────────────────────────────────────────┐  │ │
│ │ │ Contact Information                                       │  │ │
│ │ │ [                                                       ] │  │ │
│ │ │ [                                                       ] │  │ │
│ │ └───────────────────────────────────────────────────────────┘  │ │
│ │                                                                 │ │
│ │ Status                                                          │ │
│ │ [● Active  ○ Inactive]                                         │ │
│ │                                                                 │ │
│ │              [Cancel]  [Submit]                                │ │
│ └─────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
```

### 6.3 Detail Page (`staff-provider-detail.jsp`)

```
┌─────────────────────────────────────────────────────────────────────┐
│ Page Header                                                         │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ Provider Details                             [Edit] [Back]      │ │
│ └─────────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────┤
│ Provider Info Card                                                  │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ Name: Garena Vietnam                                            │ │
│ │ Contact: support@garena.vn                                      │ │
│ │ Status: ● ACTIVE                                                │ │
│ │ Total Products: 15                                              │ │
│ └─────────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────┤
│ Related Products                                                    │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ Product Name   │ Category │ Price    │ Qty │ Status            │ │
│ ├────────────────┼──────────┼──────────┼─────┼───────────────────┤ │
│ │ Garena 50K     │ Game     │ 50,000   │ 100 │ ACTIVE            │ │
│ │ Garena 100K    │ Game     │ 100,000  │  50 │ ACTIVE            │ │
│ │ Garena 200K    │ Game     │ 200,000  │  25 │ INACTIVE          │ │
│ └─────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
```

### 6.4 JSP File Structure

```
web/
├── staff.jsp                     # Master layout for staff pages
├── staff-provider-list.jsp       # Provider list with DataTable
├── staff-provider-add.jsp        # Add provider form
├── staff-provider-edit.jsp       # Edit provider form
└── staff-provider-detail.jsp     # Provider detail with related products
```

---

## 7. Implementation Checklist

### 7.1 Backend Tasks

- [ ] Create `ProviderSearchDTO.java` in `dto/` package
- [ ] Create `ProviderFormDTO.java` in `dto/` package
- [ ] Create `ProviderDAO.java` with all methods
- [ ] Add `productCount` field to `Provider.java` model
- [ ] Create `ProviderListController.java`
- [ ] Create `ProviderAddController.java`
- [ ] Create `ProviderEditController.java`
- [ ] Create `ProviderDeleteController.java`
- [ ] Create `ProviderDetailController.java`
- [ ] Add `getByProviderId()` method to `ProductDAO.java`

### 7.2 Frontend Tasks

- [ ] Create `staff-provider-list.jsp` with DataTable integration
- [ ] Create `staff-provider-add.jsp` with form validation
- [ ] Create `staff-provider-edit.jsp` with pre-populated data
- [ ] Create `staff-provider-detail.jsp` with provider info + product list
- [ ] Add Provider menu item to staff sidebar

### 7.3 Testing Tasks

- [ ] Test CRUD operations
- [ ] Test search/filter functionality
- [ ] Test pagination
- [ ] Test validation (client + server)
- [ ] Test delete constraint (cannot delete with active products)
- [ ] Test authorization (Staff only)

---

**End of Provider Feature Design Document**
