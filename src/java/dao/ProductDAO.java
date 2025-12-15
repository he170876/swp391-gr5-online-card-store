package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Product;
import util.DBContext;

public class ProductDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;

    public Product findById(long id) {
        try {
            String sql = "SELECT id, category_id, provider_id, name, description, cost_price, sell_price, discount_percent, quantity, status "
                    + "FROM Product WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
        } catch (Exception e) {
            System.out.println("ProductDAO.findById: " + e.getMessage());
        }
        return null;
    }

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        try {
            String sql = "SELECT id, category_id, provider_id, name, description, cost_price, sell_price, discount_percent, quantity, status "
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
            String sql = "SELECT id, category_id, provider_id, name, description, cost_price, sell_price, discount_percent, quantity, status "
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
            String sql = "SELECT id, category_id, provider_id, name, description, cost_price, sell_price, discount_percent, quantity, status "
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

    public List<Product> search(String keyword) {
        List<Product> products = new ArrayList<>();
        try {
            String sql = "SELECT p.id, p.category_id, p.provider_id, p.name, p.description, p.cost_price, p.sell_price, p.discount_percent, p.quantity, p.status "
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

    private Product mapResultSetToProduct(ResultSet rs) throws Exception {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setCategoryId(rs.getLong("category_id"));
        product.setProviderId(rs.getLong("provider_id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setCostPrice(rs.getDouble("cost_price"));
        product.setSellPrice(rs.getDouble("sell_price"));
        product.setDiscountPercent(rs.getDouble("discount_percent"));
        product.setQuantity(rs.getInt("quantity"));
        product.setStatus(rs.getString("status"));
        return product;
    }
    
    public List<Product> listAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT id, name, provider_id, category_id, cost_price, sell_price, quantity, description, status FROM Product";
        try (PreparedStatement stm = connection.prepareStatement(sql);
                ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }

    public Product getById(long id) {
        String sql = "SELECT id, name, provider_id, category_id, cost_price, sell_price, quantity, description, status FROM Product WHERE id = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return mapProduct(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setName(rs.getString("name"));
        p.setProviderId(rs.getLong("provider_id"));
        p.setCategoryId(rs.getLong("category_id"));
        p.setCostPrice(rs.getDouble("cost_price"));
        p.setSellPrice(rs.getDouble("sell_price"));
        p.setQuantity(rs.getInt("quantity"));
        p.setDescription(rs.getString("description"));
        p.setStatus(rs.getString("status"));
        return p;
    }
}

