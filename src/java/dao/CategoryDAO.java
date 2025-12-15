package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Category;
import util.DBContext;

public class CategoryDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;

    public Category findById(long id) {
        try {
            String sql = "SELECT id, name, description, status FROM Category WHERE id = ?";
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

    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        try {
            String sql = "SELECT id, name, description, status FROM Category ORDER BY name";
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

    public List<Category> findActive() {
        List<Category> categories = new ArrayList<>();
        try {
            String sql = "SELECT id, name, description, status FROM Category WHERE status = 'ACTIVE' ORDER BY name";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        } catch (Exception e) {
            System.out.println("CategoryDAO.findActive: " + e.getMessage());
        }
        return categories;
    }

    private Category mapResultSetToCategory(ResultSet rs) throws Exception {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        category.setStatus(rs.getString("status"));
        return category;
    }
}


