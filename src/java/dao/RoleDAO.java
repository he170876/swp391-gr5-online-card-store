package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Role;
import util.DBContext;

public class RoleDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;

    public Role findById(long id) {
        try {
            String sql = "SELECT id, name, description FROM Role WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                Role role = new Role();
                role.setId(rs.getLong("id"));
                role.setName(rs.getString("name"));
                role.setDescription(rs.getString("description"));
                return role;
            }
        } catch (Exception e) {
            System.out.println("RoleDAO.findById: " + e.getMessage());
        }
        return null;
    }

    public Role findByName(String name) {
        try {
            String sql = "SELECT id, name, description FROM Role WHERE name = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, name);
            rs = stm.executeQuery();
            if (rs.next()) {
                Role role = new Role();
                role.setId(rs.getLong("id"));
                role.setName(rs.getString("name"));
                role.setDescription(rs.getString("description"));
                return role;
            }
        } catch (Exception e) {
            System.out.println("RoleDAO.findByName: " + e.getMessage());
        }
        return null;
    }

    public List<Role> findAll() {
        List<Role> roles = new ArrayList<>();
        try {
            String sql = "SELECT id, name, description FROM Role ORDER BY id";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getLong("id"));
                role.setName(rs.getString("name"));
                role.setDescription(rs.getString("description"));
                roles.add(role);
            }
        } catch (Exception e) {
            System.out.println("RoleDAO.findAll: " + e.getMessage());
        }
        return roles;
    }

    public boolean create(Role role) {
        try {
            String sql = "INSERT INTO Role (name, description) VALUES (?, ?)";
            stm = connection.prepareStatement(sql);
            stm.setString(1, role.getName());
            stm.setString(2, role.getDescription());
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("RoleDAO.create: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Role role) {
        try {
            String sql = "UPDATE Role SET name = ?, description = ? WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, role.getName());
            stm.setString(2, role.getDescription());
            stm.setLong(3, role.getId());
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("RoleDAO.update: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(long id) {
        try {
            String sql = "DELETE FROM Role WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("RoleDAO.delete: " + e.getMessage());
            return false;
        }
    }
}


