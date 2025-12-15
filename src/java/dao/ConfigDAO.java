package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import model.Config;
import util.DBContext;

public class ConfigDAO extends DBContext {
    
    private PreparedStatement stm;
    private ResultSet rs;
    
    /**
     * Get config value by key
     */
    public String getValue(String key) {
        try {
            String sql = "SELECT value FROM Config WHERE [key] = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, key);
            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getString("value");
            }
        } catch (Exception e) {
            System.out.println("ConfigDAO.getValue: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Set/Update config value by key
     */
    public boolean setValue(String key, String value) {
        try {
            // Check if key exists
            String checkSql = "SELECT [key] FROM Config WHERE [key] = ?";
            stm = connection.prepareStatement(checkSql);
            stm.setString(1, key);
            rs = stm.executeQuery();
            
            if (rs.next()) {
                // Update existing
                String updateSql = "UPDATE Config SET value = ? WHERE [key] = ?";
                stm = connection.prepareStatement(updateSql);
                stm.setString(1, value);
                stm.setString(2, key);
                return stm.executeUpdate() > 0;
            } else {
                // Insert new
                String insertSql = "INSERT INTO Config ([key], value) VALUES (?, ?)";
                stm = connection.prepareStatement(insertSql);
                stm.setString(1, key);
                stm.setString(2, value);
                return stm.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.out.println("ConfigDAO.setValue: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all configs as a map
     */
    public Map<String, String> getAll() {
        Map<String, String> configMap = new HashMap<>();
        try {
            String sql = "SELECT [key], value FROM Config";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                configMap.put(rs.getString("key"), rs.getString("value"));
            }
        } catch (Exception e) {
            System.out.println("ConfigDAO.getAll: " + e.getMessage());
        }
        return configMap;
    }
    
    /**
     * Get config object by key
     */
    public Config findByKey(String key) {
        try {
            String sql = "SELECT [key], value, description FROM Config WHERE [key] = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, key);
            rs = stm.executeQuery();
            if (rs.next()) {
                Config config = new Config();
                config.setKey(rs.getString("key"));
                config.setValue(rs.getString("value"));
                config.setDescription(rs.getString("description"));
                return config;
            }
        } catch (Exception e) {
            System.out.println("ConfigDAO.findByKey: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Initialize default config values if they don't exist
     */
    public void initializeDefaults() {
        try {
            // Check if configs exist
            String checkSql = "SELECT COUNT(*) as count FROM Config";
            stm = connection.prepareStatement(checkSql);
            rs = stm.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt("count");
            }
            
            // If no configs exist, insert defaults
            if (count == 0) {
                String insertSql = "INSERT INTO Config ([key], value, description) VALUES (?, ?, ?)";
                stm = connection.prepareStatement(insertSql);
                
                // Insert default values
                String[][] defaults = {
                    {"systemName", "OCS - Online Card Store", "Tên hệ thống"},
                    {"maintenanceMode", "false", "Chế độ bảo trì"},
                    {"currency", "VND", "Tiền tệ mặc định"},
                    {"maxLoginAttempts", "5", "Số lần đăng nhập tối đa"},
                    {"emailSupport", "support@ocs.com", "Email hỗ trợ"},
                    {"phoneSupport", "1900-xxxx", "Số điện thoại hỗ trợ"},
                    {"pageSize", "20", "Số bản ghi mỗi trang"}
                };
                
                for (String[] def : defaults) {
                    stm.setString(1, def[0]);
                    stm.setString(2, def[1]);
                    stm.setString(3, def[2]);
                    stm.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.out.println("ConfigDAO.initializeDefaults: " + e.getMessage());
        }
    }
}

