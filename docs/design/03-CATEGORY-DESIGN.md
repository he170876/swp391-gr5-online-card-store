# Category Feature - Detailed Design Document

> **Version:** 1.0  
> **Date:** December 11, 2025  
> **Feature:** Category Management (CRUD + Search/Filter)  
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
| **List Categories** | Display all categories with pagination | Staff |
| **Search Categories** | Search by name, description | Staff |
| **Filter Categories** | Filter by status | Staff |
| **View Detail** | View category details with related products | Staff |
| **Create Category** | Add new category to system | Staff |
| **Update Category** | Edit category information | Staff |
| **Delete Category** | Soft delete (set status to INACTIVE) | Staff |

### 1.2 URL Mapping

| URL | Method | Controller | Description |
|-----|--------|------------|-------------|
| `/staff/category` | GET | `CategoryListController` | List all categories |
| `/staff/category/detail` | GET | `CategoryDetailController` | View category detail |
| `/staff/category/add` | GET | `CategoryAddController` | Show add form |
| `/staff/category/add` | POST | `CategoryAddController` | Submit add form |
| `/staff/category/edit` | GET | `CategoryEditController` | Show edit form |
| `/staff/category/edit` | POST | `CategoryEditController` | Submit edit form |
| `/staff/category/delete` | POST | `CategoryDeleteController` | Delete category |

---

## 2. Data Model

### 2.1 Database Table: `Category`

```sql
CREATE TABLE Category (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE','INACTIVE'))
);
```

### 2.2 Java Model: `Category.java`

```java
package model;

public class Category {
    private long id;
    private String name;
    private String description;
    private String status;
    
    // Computed fields (for display)
    private int productCount;    // COUNT of related products
    
    // Constructors
    public Category() {}
    
    public Category(long id, String name, String description, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }
    
    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getProductCount() { return productCount; }
    public void setProductCount(int productCount) { this.productCount = productCount; }
    
    @Override
    public String toString() {
        return "Category{id=" + id + ", name='" + name + "'}";
    }
}
```

### 2.3 DTO Classes

#### CategorySearchDTO (Search Parameters)

```java
package dto;

public class CategorySearchDTO {
    private String keyword;         // Search in name, description
    private String status;          // Filter by status
    private int page = 1;           // Pagination
    private int pageSize = 10;      // Items per page
    private String sortBy = "id";   // Sort column
    private String sortDir = "DESC"; // Sort direction
    
    // Constructors
    public CategorySearchDTO() {}
    
    // Getters, Setters
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

#### CategoryFormDTO (Create/Edit Form)

```java
package dto;

public class CategoryFormDTO {
    private Long id;                // null for create, populated for edit
    private String name;
    private String description;
    private String status;
    
    // Constructors
    public CategoryFormDTO() {}
    
    // Getters, Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

---

## 3. DAO Layer Design

### 3.1 CategoryDAO.java

```java
package dao;

import java.sql.*;
import java.util.*;
import model.Category;
import dto.CategorySearchDTO;
import util.DBContext;

public class CategoryDAO extends DBContext {
    
    PreparedStatement stm;
    ResultSet rs;
    
    // =========================================
    // 1. GET ALL CATEGORIES
    // =========================================
    public List<Category> getAll() {
        List<Category> list = new ArrayList<>();
        try {
            String sql = """
                SELECT c.*, 
                       (SELECT COUNT(*) FROM Product WHERE category_id = c.id) AS product_count
                FROM Category c
                ORDER BY c.id DESC
                """;
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToCategory(rs));
            }
        } catch (Exception e) {
            System.out.println("CategoryDAO.getAll: " + e.getMessage());
        }
        return list;
    }
    
    // =========================================
    // 2. GET ALL ACTIVE CATEGORIES (for dropdowns)
    // =========================================
    public List<Category> getAllActive() {
        List<Category> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM Category WHERE status = 'ACTIVE' ORDER BY name ASC";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToCategory(rs));
            }
        } catch (Exception e) {
            System.out.println("CategoryDAO.getAllActive: " + e.getMessage());
        }
        return list;
    }
    
    // =========================================
    // 3. GET CATEGORY BY ID
    // =========================================
    public Category getById(long id) {
        try {
            String sql = """
                SELECT c.*, 
                       (SELECT COUNT(*) FROM Product WHERE category_id = c.id) AS product_count
                FROM Category c
                WHERE c.id = ?
                """;
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                return mapResultSetToCategory(rs);
            }
        } catch (Exception e) {
            System.out.println("CategoryDAO.getById: " + e.getMessage());
        }
        return null;
    }
    
    // =========================================
    // 4. SEARCH & FILTER WITH PAGINATION
    // =========================================
    public List<Category> search(CategorySearchDTO dto) {
        List<Category> list = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder("""
                SELECT c.*, 
                       (SELECT COUNT(*) FROM Product WHERE category_id = c.id) AS product_count
                FROM Category c
                WHERE 1=1
                """);
            
            List<Object> params = new ArrayList<>();
            
            // Dynamic WHERE clauses
            if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
                sql.append(" AND (c.name LIKE ? OR c.description LIKE ?)");
                params.add("%" + dto.getKeyword() + "%");
                params.add("%" + dto.getKeyword() + "%");
            }
            
            if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
                sql.append(" AND c.status = ?");
                params.add(dto.getStatus());
            }
            
            // Sorting
            String sortColumn = switch (dto.getSortBy()) {
                case "name" -> "c.name";
                case "status" -> "c.status";
                default -> "c.id";
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
                list.add(mapResultSetToCategory(rs));
            }
        } catch (Exception e) {
            System.out.println("CategoryDAO.search: " + e.getMessage());
        }
        return list;
    }
    
    // =========================================
    // 5. COUNT FOR PAGINATION
    // =========================================
    public int count(CategorySearchDTO dto) {
        try {
            StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*) FROM Category c
                WHERE 1=1
                """);
            
            List<Object> params = new ArrayList<>();
            
            if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
                sql.append(" AND (c.name LIKE ? OR c.description LIKE ?)");
                params.add("%" + dto.getKeyword() + "%");
                params.add("%" + dto.getKeyword() + "%");
            }
            
            if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
                sql.append(" AND c.status = ?");
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
            System.out.println("CategoryDAO.count: " + e.getMessage());
        }
        return 0;
    }
    
    // =========================================
    // 6. INSERT NEW CATEGORY
    // =========================================
    public long insert(Category c) {
        try {
            String sql = "INSERT INTO Category (name, description, status) VALUES (?, ?, ?)";
            stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, c.getName());
            stm.setString(2, c.getDescription());
            stm.setString(3, c.getStatus());
            
            int rows = stm.executeUpdate();
            if (rows > 0) {
                rs = stm.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (Exception e) {
            System.out.println("CategoryDAO.insert: " + e.getMessage());
        }
        return -1;
    }
    
    // =========================================
    // 7. UPDATE CATEGORY
    // =========================================
    public boolean update(Category c) {
        try {
            String sql = "UPDATE Category SET name = ?, description = ?, status = ? WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, c.getName());
            stm.setString(2, c.getDescription());
            stm.setString(3, c.getStatus());
            stm.setLong(4, c.getId());
            
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("CategoryDAO.update: " + e.getMessage());
        }
        return false;
    }
    
    // =========================================
    // 8. SOFT DELETE (Set status to INACTIVE)
    // =========================================
    public boolean delete(long id) {
        try {
            String sql = "UPDATE Category SET status = 'INACTIVE' WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("CategoryDAO.delete: " + e.getMessage());
        }
        return false;
    }
    
    // =========================================
    // 9. CHECK NAME EXISTS (for validation)
    // =========================================
    public boolean isNameExists(String name, Long excludeId) {
        try {
            String sql = "SELECT 1 FROM Category WHERE name = ? AND id != ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, name);
            stm.setLong(2, excludeId == null ? -1 : excludeId);
            rs = stm.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("CategoryDAO.isNameExists: " + e.getMessage());
        }
        return false;
    }
    
    // =========================================
    // 10. CHECK IF CATEGORY HAS PRODUCTS
    // =========================================
    public boolean hasProducts(long categoryId) {
        try {
            String sql = "SELECT 1 FROM Product WHERE category_id = ? AND status = 'ACTIVE'";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, categoryId);
            rs = stm.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("CategoryDAO.hasProducts: " + e.getMessage());
        }
        return false;
    }
    
    // =========================================
    // HELPER: Map ResultSet to Category
    // =========================================
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        c.setStatus(rs.getString("status"));
        
        // Computed field
        try {
            c.setProductCount(rs.getInt("product_count"));
        } catch (SQLException ignored) {}
        
        return c;
    }
}
```

---

## 4. Controller Layer Design

### 4.1 CategoryListController.java

```java
package controller.category;

import dao.CategoryDAO;
import dto.CategorySearchDTO;
import model.Category;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet(name = "CategoryListController", urlPatterns = {"/staff/category"})
public class CategoryListController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Parse search parameters
        CategorySearchDTO searchDTO = new CategorySearchDTO();
        searchDTO.setKeyword(Optional.ofNullable(request.getParameter("keyword")).orElse(""));
        searchDTO.setStatus(Optional.ofNullable(request.getParameter("status")).orElse(""));
        
        String pageStr = request.getParameter("page");
        searchDTO.setPage(pageStr != null && !pageStr.isEmpty() ? Integer.parseInt(pageStr) : 1);
        
        // 2. Get data
        CategoryDAO dao = new CategoryDAO();
        List<Category> categories = dao.search(searchDTO);
        int totalCount = dao.count(searchDTO);
        int totalPages = (int) Math.ceil((double) totalCount / searchDTO.getPageSize());
        
        // 3. Set attributes
        request.setAttribute("categories", categories);
        request.setAttribute("searchDTO", searchDTO);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("totalPages", totalPages);
        
        // 4. Forward to JSP (uses staff.jsp as master layout)
        request.setAttribute("contentPage", "staff-category-list.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
}
```

### 4.2 CategoryAddController.java

```java
package controller.category;

import dao.CategoryDAO;
import dto.CategoryFormDTO;
import model.Category;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet(name = "CategoryAddController", urlPatterns = {"/staff/category/add"})
public class CategoryAddController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setAttribute("formDTO", new CategoryFormDTO());
        // Forward to JSP (uses staff.jsp as master layout)
        request.setAttribute("contentPage", "staff-category-add.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Parse form data
        CategoryFormDTO formDTO = new CategoryFormDTO();
        formDTO.setName(request.getParameter("name"));
        formDTO.setDescription(request.getParameter("description"));
        formDTO.setStatus(Optional.ofNullable(request.getParameter("status")).orElse("ACTIVE"));
        
        // 2. Validate
        Map<String, String> errors = validateForm(formDTO, null);
        
        if (!errors.isEmpty()) {
            request.setAttribute("formDTO", formDTO);
            request.setAttribute("errors", errors);
            // Forward to JSP (uses staff.jsp as master layout)
            request.setAttribute("contentPage", "staff-category-add.jsp");
            request.getRequestDispatcher("/staff.jsp").forward(request, response);
            return;
        }
        
        // 3. Create category
        Category category = new Category();
        category.setName(formDTO.getName().trim());
        category.setDescription(formDTO.getDescription());
        category.setStatus(formDTO.getStatus());
        
        CategoryDAO dao = new CategoryDAO();
        long newId = dao.insert(category);
        
        if (newId > 0) {
            request.getSession().setAttribute("successMessage", "Category created successfully!");
            response.sendRedirect(request.getContextPath() + "/staff/category");
        } else {
            request.setAttribute("errorMessage", "Failed to create category. Please try again.");
            doGet(request, response);
        }
    }
    
    private Map<String, String> validateForm(CategoryFormDTO dto, Long excludeId) {
        Map<String, String> errors = new HashMap<>();
        
        // Name validation
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            errors.put("name", "Category name is required");
        } else if (dto.getName().trim().length() < 2) {
            errors.put("name", "Category name must be at least 2 characters");
        } else if (dto.getName().trim().length() > 100) {
            errors.put("name", "Category name must not exceed 100 characters");
        } else {
            CategoryDAO dao = new CategoryDAO();
            if (dao.isNameExists(dto.getName().trim(), excludeId)) {
                errors.put("name", "Category name already exists");
            }
        }
        
        // Description validation (optional but max length)
        if (dto.getDescription() != null && dto.getDescription().length() > 255) {
            errors.put("description", "Description must not exceed 255 characters");
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

### 4.3 CategoryEditController.java

```java
package controller.category;

import dao.CategoryDAO;
import dto.CategoryFormDTO;
import model.Category;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet(name = "CategoryEditController", urlPatterns = {"/staff/category/edit"})
public class CategoryEditController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/staff/category");
            return;
        }
        
        CategoryDAO dao = new CategoryDAO();
        Category category = dao.getById(Long.parseLong(idStr));
        
        if (category == null) {
            request.getSession().setAttribute("errorMessage", "Category not found");
            response.sendRedirect(request.getContextPath() + "/staff/category");
            return;
        }
        
        // Convert to DTO
        CategoryFormDTO formDTO = new CategoryFormDTO();
        formDTO.setId(category.getId());
        formDTO.setName(category.getName());
        formDTO.setDescription(category.getDescription());
        formDTO.setStatus(category.getStatus());
        
        request.setAttribute("formDTO", formDTO);
        // Forward to JSP (uses staff.jsp as master layout)
        request.setAttribute("contentPage", "staff-category-edit.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Parse form data
        CategoryFormDTO formDTO = new CategoryFormDTO();
        try {
            formDTO.setId(Long.parseLong(request.getParameter("id")));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/staff/category");
            return;
        }
        formDTO.setName(request.getParameter("name"));
        formDTO.setDescription(request.getParameter("description"));
        formDTO.setStatus(Optional.ofNullable(request.getParameter("status")).orElse("ACTIVE"));
        
        // 2. Validate
        Map<String, String> errors = validateForm(formDTO, formDTO.getId());
        
        if (!errors.isEmpty()) {
            request.setAttribute("formDTO", formDTO);
            request.setAttribute("errors", errors);
            // Forward to JSP (uses staff.jsp as master layout)
            request.setAttribute("contentPage", "staff-category-edit.jsp");
            request.getRequestDispatcher("/staff.jsp").forward(request, response);
            return;
        }
        
        // 3. Update category
        Category category = new Category();
        category.setId(formDTO.getId());
        category.setName(formDTO.getName().trim());
        category.setDescription(formDTO.getDescription());
        category.setStatus(formDTO.getStatus());
        
        CategoryDAO dao = new CategoryDAO();
        boolean success = dao.update(category);
        
        if (success) {
            request.getSession().setAttribute("successMessage", "Category updated successfully!");
            response.sendRedirect(request.getContextPath() + "/staff/category");
        } else {
            request.setAttribute("errorMessage", "Failed to update category. Please try again.");
            request.setAttribute("formDTO", formDTO);
            // Forward to JSP (uses staff.jsp as master layout)
            request.setAttribute("contentPage", "staff-category-edit.jsp");
            request.getRequestDispatcher("/staff.jsp").forward(request, response);
        }
    }
    
    private Map<String, String> validateForm(CategoryFormDTO dto, Long excludeId) {
        Map<String, String> errors = new HashMap<>();
        
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            errors.put("name", "Category name is required");
        } else if (dto.getName().trim().length() < 2) {
            errors.put("name", "Category name must be at least 2 characters");
        } else if (dto.getName().trim().length() > 100) {
            errors.put("name", "Category name must not exceed 100 characters");
        } else {
            CategoryDAO dao = new CategoryDAO();
            if (dao.isNameExists(dto.getName().trim(), excludeId)) {
                errors.put("name", "Category name already exists");
            }
        }
        
        if (dto.getDescription() != null && dto.getDescription().length() > 255) {
            errors.put("description", "Description must not exceed 255 characters");
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

### 4.4 CategoryDeleteController.java

```java
package controller.category;

import dao.CategoryDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;

@WebServlet(name = "CategoryDeleteController", urlPatterns = {"/staff/category/delete"})
public class CategoryDeleteController extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/staff/category");
            return;
        }
        
        long categoryId = Long.parseLong(idStr);
        CategoryDAO dao = new CategoryDAO();
        
        // Check if category has active products
        if (dao.hasProducts(categoryId)) {
            request.getSession().setAttribute("errorMessage", 
                "Cannot delete category with active products. Please deactivate or reassign products first.");
            response.sendRedirect(request.getContextPath() + "/staff/category");
            return;
        }
        
        boolean success = dao.delete(categoryId);
        
        if (success) {
            request.getSession().setAttribute("successMessage", "Category deleted successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to delete category");
        }
        
        response.sendRedirect(request.getContextPath() + "/staff/category");
    }
}
```

### 4.5 CategoryDetailController.java

```java
package controller.category;

import dao.CategoryDAO;
import dao.ProductDAO;
import model.Category;
import model.Product;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet(name = "CategoryDetailController", urlPatterns = {"/staff/category/detail"})
public class CategoryDetailController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/staff/category");
            return;
        }
        
        CategoryDAO categoryDAO = new CategoryDAO();
        Category category = categoryDAO.getById(Long.parseLong(idStr));
        
        if (category == null) {
            request.getSession().setAttribute("errorMessage", "Category not found");
            response.sendRedirect(request.getContextPath() + "/staff/category");
            return;
        }
        
        // Get related products
        ProductDAO productDAO = new ProductDAO();
        List<Product> products = productDAO.getByCategoryId(category.getId());
        
        request.setAttribute("category", category);
        request.setAttribute("products", products);
        
        // Forward to JSP (uses staff.jsp as master layout)
        request.setAttribute("contentPage", "staff-category-detail.jsp");
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
| `description` | String | - | 0 | 255 | Optional |
| `status` | String | ✓ | - | - | Must be 'ACTIVE' or 'INACTIVE' |

### 5.2 Business Rules

| Rule | Description |
|------|-------------|
| Unique Name | Category name must be unique across all categories |
| Delete Constraint | Cannot delete category with active products |
| Status Change | Can change status at any time |

### 5.3 Client-Side Validation (JavaScript)

```javascript
function validateCategoryForm() {
    let isValid = true;
    const errors = {};
    
    // Name validation
    const name = document.getElementById('name').value.trim();
    if (!name) {
        errors.name = 'Category name is required';
        isValid = false;
    } else if (name.length < 2) {
        errors.name = 'Category name must be at least 2 characters';
        isValid = false;
    } else if (name.length > 100) {
        errors.name = 'Category name must not exceed 100 characters';
        isValid = false;
    }
    
    // Description validation
    const description = document.getElementById('description').value;
    if (description && description.length > 255) {
        errors.description = 'Description must not exceed 255 characters';
        isValid = false;
    }
    
    // Display errors
    displayValidationErrors(errors);
    
    return isValid;
}
```

---

## 6. UI Design

### 6.1 List Page (`staff-category-list.jsp`)

```
┌─────────────────────────────────────────────────────────────────────┐
│ Page Header                                                         │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ Category List                                  [+ Add Category] │ │
│ │ Manage your product categories                                  │ │
│ └─────────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────┤
│ Filter Card                                                         │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ [Search by name or description...]  [Status ▼]  [🔍 Search]    │ │
│ └─────────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────┤
│ Data Table                                                          │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ □ │ Category Name │ Description          │ Products │ Status │ ⚡│ │
│ ├───┼───────────────┼──────────────────────┼──────────┼────────┼───┤ │
│ │ □ │ Game Cards    │ Gaming gift cards    │    25    │ ACTIVE │ ⋮ │ │
│ │ □ │ Telco Cards   │ Phone recharge cards │    30    │ ACTIVE │ ⋮ │ │
│ │ □ │ Shopping      │ E-commerce vouchers  │    15    │ ACTIVE │ ⋮ │ │
│ └─────────────────────────────────────────────────────────────────┘ │
│ Showing 1-10 of 20 entries          [< 1 2 >]                      │
└─────────────────────────────────────────────────────────────────────┘

Action Dropdown (⋮):
┌───────────────┐
│ 👁 View       │
│ ✏️ Edit       │
│ 🗑️ Delete     │
└───────────────┘
```

### 6.2 Add/Edit Form (`staff-category-add.jsp`)

```
┌─────────────────────────────────────────────────────────────────────┐
│ Page Header                                                         │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ Add New Category                                                │ │
│ │ Create a new product category                                   │ │
│ └─────────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────┤
│ Form Card                                                           │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ Category Information                                            │ │
│ │ ┌───────────────────────────────────┐                          │ │
│ │ │ Category Name *                   │                          │ │
│ │ │ [_______________________________] │                          │ │
│ │ └───────────────────────────────────┘                          │ │
│ │                                                                 │ │
│ │ ┌───────────────────────────────────────────────────────────┐  │ │
│ │ │ Description                                               │  │ │
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

### 6.3 Detail Page (`staff-category-detail.jsp`)

```
┌─────────────────────────────────────────────────────────────────────┐
│ Page Header                                                         │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ Category Details                             [Edit] [Back]      │ │
│ └─────────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────┤
│ Category Info Card                                                  │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ Name: Game Cards                                                │ │
│ │ Description: Digital cards for gaming platforms                 │ │
│ │ Status: ● ACTIVE                                                │ │
│ │ Total Products: 25                                              │ │
│ └─────────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────┤
│ Related Products                                                    │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │ Product Name   │ Provider │ Price    │ Qty │ Status            │ │
│ ├────────────────┼──────────┼──────────┼─────┼───────────────────┤ │
│ │ Garena 50K     │ Garena   │ 50,000   │ 100 │ ACTIVE            │ │
│ │ Steam $20      │ Steam    │ 500,000  │  50 │ ACTIVE            │ │
│ │ PlayStation $25│ Sony     │ 625,000  │  30 │ ACTIVE            │ │
│ └─────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
```

### 6.4 JSP File Structure

```
web/
├── staff.jsp                     # Master layout for staff pages
├── staff-category-list.jsp       # Category list with DataTable
├── staff-category-add.jsp        # Add category form
├── staff-category-edit.jsp       # Edit category form
└── staff-category-detail.jsp     # Category detail with related products
```

---

## 7. Implementation Checklist

### 7.1 Backend Tasks

- [ ] Create `CategorySearchDTO.java` in `dto/` package
- [ ] Create `CategoryFormDTO.java` in `dto/` package
- [ ] Create `CategoryDAO.java` with all methods
- [ ] Add `productCount` field to `Category.java` model
- [ ] Create `CategoryListController.java`
- [ ] Create `CategoryAddController.java`
- [ ] Create `CategoryEditController.java`
- [ ] Create `CategoryDeleteController.java`
- [ ] Create `CategoryDetailController.java`
- [ ] Add `getByCategoryId()` method to `ProductDAO.java`

### 7.2 Frontend Tasks

- [ ] Create `staff-category-list.jsp` with DataTable integration
- [ ] Create `staff-category-add.jsp` with form validation
- [ ] Create `staff-category-edit.jsp` with pre-populated data
- [ ] Create `staff-category-detail.jsp` with category info + product list
- [ ] Add Category menu item to staff sidebar

### 7.3 Testing Tasks

- [ ] Test CRUD operations
- [ ] Test search/filter functionality
- [ ] Test pagination
- [ ] Test validation (client + server)
- [ ] Test delete constraint (cannot delete with active products)
- [ ] Test authorization (Staff only)

---

**End of Category Feature Design Document**
