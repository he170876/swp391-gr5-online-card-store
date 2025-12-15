package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author FPT University - PRJ30X
 */
public class DBContext {

    protected Connection connection;

    public DBContext() {
        // @Students: You are allowed to edit user, pass, url variables to fit
        // your system configuration
        // You can also add more methods for Database Interaction tasks.
        // But we recommend you to do it in another class
        // For example : StudentDBContext extends DBContext ,
        // where StudentDBContext is located in dal package,
        try {
            String user = "sa";
            String pass = "123";

            String url = "jdbc:sqlserver://localhost\\TUVT:1433;databaseName=ocs";
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        DBContext dbContext = new DBContext();
        if (dbContext.connection != null) {
            try {
                if (!dbContext.connection.isClosed()) {
                    System.out.println("Kết nối tới database OCS thành công!");
                } else {
                    System.out.println("Kết nối tới database OCS bị đóng.");
                }
            } catch (Exception e) {
                System.out.println("Lỗi kiểm tra kết nối: " + e.getMessage());
            }
        } else {
            System.out.println("Không thể tạo kết nối tới database OCS.");
        }
    }
}
