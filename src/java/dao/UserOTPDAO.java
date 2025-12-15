/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.UserOTP;
import util.DBContext;

/**
 *
 * @author hades
 */
public class UserOTPDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;

    public UserOTP getByUserId(long userId) {
        String sql = "SELECT user_id, otp_code, otp_created_at, send_count, last_send "
                + "FROM UserOTP WHERE user_id = ?";
        try {
            stm = connection.prepareStatement(sql);
            stm.setLong(1, userId);
            rs = stm.executeQuery();
            if (rs.next()) {
                UserOTP userOTP = new UserOTP();
                userOTP.setUserId(rs.getLong("user_id"));
                userOTP.setOtpCode(rs.getString("otp_code"));
                userOTP.setOtpCreatedAt(rs.getTimestamp("otp_created_at").toLocalDateTime());
                userOTP.setSendCount(rs.getInt("send_count"));
                userOTP.setLastSend(rs.getTimestamp("last_send").toLocalDateTime());
                return userOTP;
            }
        } catch (SQLException e) {
            System.out.println("UserOTPDAO.getByUserId: " + e.getMessage());
        }

        return null;
    }

    public void insertOrUpdate(UserOTP userOTP) {
        String sql = "MERGE UserOTP AS target "
                + "USING (SELECT ? AS user_id) AS src "
                + "ON target.user_id = src.user_id "
                + "WHEN MATCHED THEN UPDATE SET otp_code=?, otp_created_at=SYSDATETIME(), "
                + "send_count = target.send_count + 1, last_send = SYSDATETIME() "
                + "WHEN NOT MATCHED THEN INSERT (user_id, otp_code, otp_created_at, send_count, last_send) "
                + "VALUES (?, ?, SYSDATETIME(), 1, SYSDATETIME());";
        try {
            stm = connection.prepareStatement(sql);
            stm.setLong(1, userOTP.getUserId());
            stm.setString(2, userOTP.getOtpCode());
            stm.setLong(3, userOTP.getUserId());
            stm.setString(4, userOTP.getOtpCode());
            stm.executeUpdate();
        } catch (SQLException e) {
            System.out.println("UserOTPDAO.insertOrUpdate: " + e.getMessage());
        }

    }

    public void deleteOTP(long userId) {
        String sql = "DELETE FROM UserOTP WHERE user_id = ?";
        try {
            stm = connection.prepareStatement(sql);
            stm.setLong(1, userId);
            stm.executeUpdate();
        } catch (SQLException e) {
            System.out.println("UserOTPDAO.deleteOTP: " + e.getMessage());
        }
    }

    public void resetSendCount(long userId) {
        String sql = """
        UPDATE UserOTP
        SET send_count = 0
        WHERE user_id = ?
    """;

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, userId);
            stm.executeUpdate();
        } catch (SQLException e) {
            System.out.println("UserOTPDAO.resetSendCount: " + e.getMessage());
        }
    }

    public void resetLastSent(long userId) {
        String sql = """
        UPDATE UserOTP
        SET last_send = NULL
        WHERE user_id = ?
    """;

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, userId);
            stm.executeUpdate();
        } catch (SQLException e) {
            System.out.println("UserOTPDAO.resetLastSent: " + e.getMessage());
        }
    }

}
