/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.User;
import util.DBContext;

/**
 *
 * @author hades
 */
public class UserDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;

    public boolean checkLogin(String email, String passwordHash) {
        try {
            String sql = "SELECT 1 FROM [User] WHERE email = ? AND password_hash = ? AND status = 'ACTIVE'";
            stm = connection.prepareStatement(sql);
            stm.setString(1, email);
            stm.setString(2, passwordHash);
            rs = stm.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    public User getUserByEmail(String email) {
        try {
            String sql = "SELECT id, email, password_hash, full_name, phone, address, status, wallet_balance, role_id FROM [User] WHERE email = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, email);
            rs = stm.executeQuery();
            if (rs.next()) {
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
                return u;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

}
