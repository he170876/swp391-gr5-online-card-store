/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.math.BigDecimal;
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
  
    public User findById(long id) {
        try {
            String sql = "SELECT id, email, password_hash, full_name, phone, address, status, wallet_balance, role_id, created_at, updated_at FROM [User] WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (Exception e) {
            System.out.println("UserDAO.findById: " + e.getMessage());
        }
        return null;
    }

    public boolean emailExists(String email) {
        try {
            String sql = "SELECT 1 FROM [User] WHERE email = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, email);
            rs = stm.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("UserDAO.emailExists: " + e.getMessage());
        }
        return false;
    }

    public boolean create(User user) {
        try {
            String sql = "INSERT INTO [User] (email, password_hash, full_name, phone, address, status, wallet_balance, role_id, created_at, updated_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";
            stm = connection.prepareStatement(sql);
            stm.setString(1, user.getEmail());
            stm.setString(2, user.getPasswordHash());
            stm.setString(3, user.getFullName());
            stm.setString(4, user.getPhone());
            stm.setString(5, user.getAddress());
            stm.setString(6, user.getStatus());
            stm.setBigDecimal(7, user.getWalletBalance());
            stm.setLong(8, user.getRoleId());
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("UserDAO.create: " + e.getMessage());
            return false;
        }
    }

    public boolean update(User user) {
        try {
            String sql = "UPDATE [User] SET full_name = ?, phone = ?, address = ?, status = ?, updated_at = GETDATE() WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, user.getFullName());
            stm.setString(2, user.getPhone());
            stm.setString(3, user.getAddress());
            stm.setString(4, user.getStatus());
            stm.setLong(5, user.getId());
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("UserDAO.update: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePassword(long userId, String newPasswordHash) {
        try {
            String sql = "UPDATE [User] SET password_hash = ?, updated_at = GETDATE() WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, newPasswordHash);
            stm.setLong(2, userId);
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("UserDAO.updatePassword: " + e.getMessage());
            return false;
        }
    }

    public boolean updateWalletBalance(long userId, BigDecimal newBalance) {
        try {
            String sql = "UPDATE [User] SET wallet_balance = ?, updated_at = GETDATE() WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setBigDecimal(1, newBalance);
            stm.setLong(2, userId);
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("UserDAO.updateWalletBalance: " + e.getMessage());
            return false;
        }
    }

    public List<User> findAll(int offset, int limit) {
        List<User> users = new ArrayList<>();
        try {
            String sql = "SELECT id, email, password_hash, full_name, phone, address, status, wallet_balance, role_id, created_at, updated_at "
                    + "FROM [User] ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
            stm = connection.prepareStatement(sql);
            stm.setInt(1, offset);
            stm.setInt(2, limit);
            rs = stm.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (Exception e) {
            System.out.println("UserDAO.findAll: " + e.getMessage());
        }
        return users;
    }

    public List<User> findByRole(long roleId, int offset, int limit) {
        List<User> users = new ArrayList<>();
        try {
            String sql = "SELECT id, email, password_hash, full_name, phone, address, status, wallet_balance, role_id, created_at, updated_at "
                    + "FROM [User] WHERE role_id = ? ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, roleId);
            stm.setInt(2, offset);
            stm.setInt(3, limit);
            rs = stm.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (Exception e) {
            System.out.println("UserDAO.findByRole: " + e.getMessage());
        }
        return users;
    }

    public List<User> search(String keyword, Long roleId, String status, int offset, int limit) {
        List<User> users = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder(
                    "SELECT id, email, password_hash, full_name, phone, address, status, wallet_balance, role_id, created_at, updated_at "
                    + "FROM [User] WHERE 1=1");
            List<Object> params = new ArrayList<>();
            int paramIndex = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (full_name LIKE ? OR email LIKE ? OR phone LIKE ?)");
                String searchPattern = "%" + keyword + "%";
                params.add(searchPattern);
                params.add(searchPattern);
                params.add(searchPattern);
            }
            if (roleId != null) {
                sql.append(" AND role_id = ?");
                params.add(roleId);
            }
            if (status != null && !status.trim().isEmpty()) {
                sql.append(" AND status = ?");
                params.add(status);
            }
            sql.append(" ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            params.add(offset);
            params.add(limit);

            stm = connection.prepareStatement(sql.toString());
            for (Object param : params) {
                if (param instanceof String) {
                    stm.setString(paramIndex++, (String) param);
                } else if (param instanceof Long) {
                    stm.setLong(paramIndex++, (Long) param);
                } else if (param instanceof Integer) {
                    stm.setInt(paramIndex++, (Integer) param);
                }
            }

            rs = stm.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (Exception e) {
            System.out.println("UserDAO.search: " + e.getMessage());
        }
        return users;
    }

    public int countByRole(long roleId) {
        try {
            String sql = "SELECT COUNT(*) FROM [User] WHERE role_id = ?";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, roleId);
            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("UserDAO.countByRole: " + e.getMessage());
        }
        return 0;
    }
    
    public int countSearch(String keyword, Long roleId, String status) {
        try {
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM [User] WHERE 1=1");
            List<Object> params = new ArrayList<>();
            int paramIndex = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (full_name LIKE ? OR email LIKE ? OR phone LIKE ?)");
                String searchPattern = "%" + keyword + "%";
                params.add(searchPattern);
                params.add(searchPattern);
                params.add(searchPattern);
            }
            if (roleId != null) {
                sql.append(" AND role_id = ?");
                params.add(roleId);
            }
            if (status != null && !status.trim().isEmpty()) {
                sql.append(" AND status = ?");
                params.add(status);
            }

            stm = connection.prepareStatement(sql.toString());
            for (Object param : params) {
                if (param instanceof String) {
                    stm.setString(paramIndex++, (String) param);
                } else if (param instanceof Long) {
                    stm.setLong(paramIndex++, (Long) param);
                }
            }

            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("UserDAO.countSearch: " + e.getMessage());
        }
        return 0;
    }

    private User mapResultSetToUser(ResultSet rs) throws Exception {
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
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            u.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            u.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return u;
    }

}