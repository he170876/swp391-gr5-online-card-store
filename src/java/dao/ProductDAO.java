package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Product;
import util.DBContext;

/**
 * DAO for Product table.
 */
public class ProductDAO extends DBContext {

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
