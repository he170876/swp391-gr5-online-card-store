/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;
import util.DBContext;

/**
 *
 * @author hades
 */
public class UserDAO extends DBContext {

    public UserDAO() {
        super();
    }

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        u.setPhone(rs.getString("phone"));
        u.setAddress(rs.getString("address"));
        u.setStatus(rs.getString("status"));
        u.setWalletBalance(rs.getBigDecimal("wallet_balance"));
        u.setRoleId(rs.getLong("role_id"));
        u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        u.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return u;
    }

    private PreparedStatement stm;
    private ResultSet rs;

    public boolean checkLogin(String email, String passwordHash) {
        try {
            String sql = "SELECT 1 FROM [User] WHERE email = ? AND password_hash = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, email);
            stm.setString(2, passwordHash);
            rs = stm.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("UserDAO.checkLogin: " + e);
        }
        return false;
    }

    public User getUserByEmail(String email) {
        try {
            String sql = "SELECT id, email, password_hash, full_name, phone, address, status, wallet_balance, role_id, created_at, updated_at FROM [User] WHERE email = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, email);
            rs = stm.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        } catch (Exception e) {
            System.out.println("UserDAO.getUserByEmail: " + e);
        }
        return null;
    }

    public User getUserById(long id) {
        try {
            String sql = "SELECT id, email, password_hash, full_name, phone, address, status, wallet_balance, role_id, created_at, updated_at FROM [User] WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        } catch (Exception e) {
            System.out.println("UserDAO.getUserById: " + e);
        }
        return null;
    }

    public boolean insertUser(User user) {
        try {
            String sql = "INSERT INTO [User] (email, password_hash, full_name, phone, address, status, wallet_balance, role_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            stm = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stm.setString(1, user.getEmail());
            stm.setString(2, user.getPasswordHash());
            stm.setString(3, user.getFullName());
            stm.setString(4, user.getPhone());
            stm.setString(5, user.getAddress());
            stm.setString(6, "INACTIVE");
            stm.setBigDecimal(7, BigDecimal.ZERO);
            stm.setLong(8, 3);

            int rows = stm.executeUpdate();
            if (rows == 0) {
                return false;
            }

            rs = stm.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getLong(1));
            }

            return true;
        } catch (Exception e) {
            System.out.println("UserDAO.insertUser: " + e);
        }
        return false;
    }

    public boolean activateUser(long userId) {
        try {
            String sql = "UPDATE [User] SET status = 'ACTIVE', updated_at = GETDATE() WHERE id = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setLong(1, userId);

            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("UserDAO.activateUser: " + e.getMessage());
        }
        return false;
    }

}
