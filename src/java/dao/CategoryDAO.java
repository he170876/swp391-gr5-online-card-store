package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Category;
import dto.CategorySearchDTO;
import util.DBContext;

public class CategoryDAO extends DBContext {

    private PreparedStatement stm;
    private ResultSet rs;

    // =========================================
    // 1. GET CATEGORY BY ID (Legacy method)
    // =========================================
    public Category findById(long id) {
        try {
            String sql = "SELECT c.*, " +
                        "(SELECT COUNT(*) FROM Product WHERE category_id = c.id) AS product_count " +
                        "FROM Category c WHERE c.id = ?";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                return mapResultSetToCategory(rs);
            }
        } catch (Exception e) {
            System.out.println("CategoryDAO.findById: " + e.getMessage());
        }
        return null;
    }

    // =========================================
    // 2. GET ALL CATEGORIES (Legacy method)
    // =========================================
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        try {
            String sql = "SELECT c.*, " +
                        "(SELECT COUNT(*) FROM Product WHERE category_id = c.id) AS product_count " +
                        "FROM Category c ORDER BY c.name";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        } catch (Exception e) {
            System.out.println("CategoryDAO.findAll: " + e.getMessage());
        }
        return categories;
    }

    // =========================================
    // 3. GET ALL ACTIVE CATEGORIES (for dropdowns)
    // =========================================
    public List<Category> findActive() {
        List<Category> categories = new ArrayList<>();
        try {
            String sql = "SELECT id, name, description, status FROM Category WHERE status = 'ACTIVE' ORDER BY name";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getLong("id"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                category.setStatus(rs.getString("status"));
                categories.add(category);
            }
        } catch (Exception e) {
            System.out.println("CategoryDAO.findActive: " + e.getMessage());
        }
        return categories;
    }

    // =========================================
    // 4. SEARCH & FILTER WITH PAGINATION
    // =========================================
    public List<Category> search(CategorySearchDTO dto) {
        List<Category> list = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder(
                "SELECT c.*, " +
                "(SELECT COUNT(*) FROM Product WHERE category_id = c.id) AS product_count " +
                "FROM Category c WHERE 1=1"
            );
            
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
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Category c WHERE 1=1");
            
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


