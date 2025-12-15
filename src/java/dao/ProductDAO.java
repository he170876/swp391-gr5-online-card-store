package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Product;
import dto.ProductSearchDTO;
import util.DBContext;

public class ProductDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;

    // =========================================
    // GET PRODUCT BY ID (with Category/Provider names)
    // =========================================
    public Product findById(long id) {
        try {
            String sql = "SELECT p.id, p.category_id, p.provider_id, p.name, p.description, p.image_url, "
                    + "p.cost_price, p.sell_price, p.discount_percent, p.quantity, p.status, "
                    + "c.name AS category_name, pr.name AS provider_name "
                    + "FROM Product p "
                    + "INNER JOIN Category c ON p.category_id = c.id "
                    + "INNER JOIN Provider pr ON p.provider_id = pr.id "
                    + "WHERE p.id = ?";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                return mapResultSetToProductWithJoin(rs);
            }
        } catch (Exception e) {
            System.out.println("ProductDAO.findById: " + e.getMessage());
        }
        return null;
    }

    // =========================================
    // GET ALL PRODUCTS (with Category/Provider names)
    // =========================================
    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        try {
            String sql = "SELECT p.id, p.category_id, p.provider_id, p.name, p.description, p.image_url, "
                    + "p.cost_price, p.sell_price, p.discount_percent, p.quantity, p.status, "
                    + "c.name AS category_name, pr.name AS provider_name "
                    + "FROM Product p "
                    + "INNER JOIN Category c ON p.category_id = c.id "
                    + "INNER JOIN Provider pr ON p.provider_id = pr.id "
                    + "ORDER BY p.id DESC";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProductWithJoin(rs));
            }
        } catch (Exception e) {
            System.out.println("ProductDAO.getAll: " + e.getMessage());
        }
        return list;
    }

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        try {
            String sql = "SELECT id, category_id, provider_id, name, description, image_url, cost_price, sell_price, discount_percent, quantity, status "
                    + "FROM Product ORDER BY name";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (Exception e) {
            System.out.println("ProductDAO.findAll: " + e.getMessage());
        }
        return products;
    }

    public List<Product> findActive() {
        List<Product> products = new ArrayList<>();
        try {
            String sql = "SELECT id, category_id, provider_id, name, description, image_url, cost_price, sell_price, discount_percent, quantity, status "
                    + "FROM Product WHERE status = 'ACTIVE' ORDER BY name";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (Exception e) {
            System.out.println("ProductDAO.findActive: " + e.getMessage());
        }
        return products;
    }

    public List<Product> findByCategory(long categoryId) {
        List<Product> products = new ArrayList<>();
        try {
            String sql = "SELECT id, category_id, provider_id, name, description, image_url, cost_price, sell_price, discount_percent, quantity, status "
                    + "FROM Product WHERE category_id = ? AND status = 'ACTIVE' ORDER BY name";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, categoryId);
            rs = stm.executeQuery();
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (Exception e) {
            System.out.println("ProductDAO.findByCategory: " + e.getMessage());
        }
        return products;
    }

    // =========================================
    // GET PRODUCTS BY PROVIDER ID
    // =========================================
    public List<Product> getByProviderId(long providerId) {
        List<Product> products = new ArrayList<>();
        try {
            String sql = "SELECT p.id, p.category_id, p.provider_id, p.name, p.description, p.image_url, "
                    + "p.cost_price, p.sell_price, p.discount_percent, p.quantity, p.status, "
                    + "c.name AS category_name, pr.name AS provider_name "
                    + "FROM Product p "
                    + "INNER JOIN Category c ON p.category_id = c.id "
                    + "INNER JOIN Provider pr ON p.provider_id = pr.id "
                    + "WHERE p.provider_id = ? ORDER BY p.name";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, providerId);
            rs = stm.executeQuery();
            while (rs.next()) {
                products.add(mapResultSetToProductWithJoin(rs));
            }
        } catch (Exception e) {
            System.out.println("ProductDAO.getByProviderId: " + e.getMessage());
        }
        return products;
    }

    public List<Product> search(String keyword) {
        List<Product> products = new ArrayList<>();
        try {
            String sql = "SELECT p.id, p.category_id, p.provider_id, p.name, p.description, p.image_url, p.cost_price, p.sell_price, p.discount_percent, p.quantity, p.status "
                    + "FROM Product p "
                    + "LEFT JOIN Provider pr ON p.provider_id = pr.id "
                    + "WHERE p.status = 'ACTIVE' AND (p.name LIKE ? OR pr.name LIKE ?) "
                    + "ORDER BY p.name";
            stm = connection.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            stm.setString(1, searchPattern);
            stm.setString(2, searchPattern);
            rs = stm.executeQuery();
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (Exception e) {
            System.out.println("ProductDAO.search: " + e.getMessage());
        }
        return products;
    }

    // =========================================
    // SEARCH & FILTER WITH PAGINATION
    // =========================================
    public List<Product> search(ProductSearchDTO dto) {
        List<Product> list = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder(
                "SELECT p.id, p.category_id, p.provider_id, p.name, p.description, p.image_url, "
                + "p.cost_price, p.sell_price, p.discount_percent, p.quantity, p.status, "
                + "c.name AS category_name, pr.name AS provider_name "
                + "FROM Product p "
                + "INNER JOIN Category c ON p.category_id = c.id "
                + "INNER JOIN Provider pr ON p.provider_id = pr.id "
                + "WHERE 1=1 ");
            
            List<Object> params = new ArrayList<>();
            
            // Dynamic WHERE clauses
            if (dto.getKeyword() != null && !dto.getKeyword().trim().isEmpty()) {
                sql.append(" AND (p.name LIKE ? OR p.description LIKE ?)");
                params.add("%" + dto.getKeyword().trim() + "%");
                params.add("%" + dto.getKeyword().trim() + "%");
            }
            
            if (dto.getCategoryId() != null) {
                sql.append(" AND p.category_id = ?");
                params.add(dto.getCategoryId());
            }
            
            if (dto.getProviderId() != null) {
                sql.append(" AND p.provider_id = ?");
                params.add(dto.getProviderId());
            }
            
            if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
                sql.append(" AND p.status = ?");
                params.add(dto.getStatus().trim());
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
            String sortColumn;
            switch (dto.getSortBy()) {
                case "name":
                    sortColumn = "p.name";
                    break;
                case "price":
                    sortColumn = "p.sell_price";
                    break;
                case "quantity":
                    sortColumn = "p.quantity";
                    break;
                case "category":
                    sortColumn = "c.name";
                    break;
                default:
                    sortColumn = "p.id";
            }
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
                list.add(mapResultSetToProductWithJoin(rs));
            }
        } catch (Exception e) {
            System.out.println("ProductDAO.search(DTO): " + e.getMessage());
        }
        return list;
    }

    // =========================================
    // COUNT FOR PAGINATION
    // =========================================
    public int count(ProductSearchDTO dto) {
        try {
            StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM Product p WHERE 1=1 ");
            
            List<Object> params = new ArrayList<>();
            
            if (dto.getKeyword() != null && !dto.getKeyword().trim().isEmpty()) {
                sql.append(" AND (p.name LIKE ? OR p.description LIKE ?)");
                params.add("%" + dto.getKeyword().trim() + "%");
                params.add("%" + dto.getKeyword().trim() + "%");
            }
            
            if (dto.getCategoryId() != null) {
                sql.append(" AND p.category_id = ?");
                params.add(dto.getCategoryId());
            }
            
            if (dto.getProviderId() != null) {
                sql.append(" AND p.provider_id = ?");
                params.add(dto.getProviderId());
            }
            
            if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
                sql.append(" AND p.status = ?");
                params.add(dto.getStatus().trim());
            }
            
            if (dto.getMinPrice() != null) {
                sql.append(" AND p.sell_price >= ?");
                params.add(dto.getMinPrice());
            }
            
            if (dto.getMaxPrice() != null) {
                sql.append(" AND p.sell_price <= ?");
                params.add(dto.getMaxPrice());
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
    // INSERT NEW PRODUCT
    // =========================================
    public long insert(Product p) {
        try {
            String sql = "INSERT INTO Product (category_id, provider_id, name, description, image_url, "
                    + "cost_price, sell_price, discount_percent, quantity, status) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
    // UPDATE PRODUCT
    // =========================================
    public boolean update(Product p) {
        try {
            String sql = "UPDATE Product SET category_id = ?, provider_id = ?, name = ?, description = ?, "
                    + "image_url = ?, cost_price = ?, sell_price = ?, discount_percent = ?, status = ? "
                    + "WHERE id = ?";
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
    // SOFT DELETE (Set status to INACTIVE)
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
    // CHECK NAME EXISTS (for validation)
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
    // GET PRODUCTS BY CATEGORY ID
    // =========================================
    public List<Product> getByCategoryId(long categoryId) {
        List<Product> list = new ArrayList<>();
        try {
            String sql = "SELECT p.*, c.name AS category_name, pr.name AS provider_name " +
                        "FROM Product p " +
                        "LEFT JOIN Category c ON p.category_id = c.id " +
                        "LEFT JOIN Provider pr ON p.provider_id = pr.id " +
                        "WHERE p.category_id = ? AND p.status = 'ACTIVE' " +
                        "ORDER BY p.name ASC";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, categoryId);
            rs = stm.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProductWithJoin(rs));
            }
        } catch (Exception e) {
            System.out.println("ProductDAO.getByCategoryId: " + e.getMessage());
        }
        return list;
    }

    // =========================================
    // HELPER: Map ResultSet to Product (basic)
    // =========================================
    private Product mapResultSetToProduct(ResultSet rs) throws Exception {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setCategoryId(rs.getLong("category_id"));
        product.setProviderId(rs.getLong("provider_id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        try {
            product.setImageUrl(rs.getString("image_url"));
        } catch (Exception ignored) {}
        product.setCostPrice(rs.getDouble("cost_price"));
        product.setSellPrice(rs.getDouble("sell_price"));
        product.setDiscountPercent(rs.getDouble("discount_percent"));
        product.setQuantity(rs.getInt("quantity"));
        product.setStatus(rs.getString("status"));
        return product;
    }

    // =========================================
    // HELPER: Map ResultSet to Product (with JOIN fields)
    // =========================================
    private Product mapResultSetToProductWithJoin(ResultSet rs) throws Exception {
        Product product = mapResultSetToProduct(rs);
        try {
            product.setCategoryName(rs.getString("category_name"));
            product.setProviderName(rs.getString("provider_name"));
        } catch (Exception ignored) {}
        return product;
    }
}


