package controller;

import dao.ConfigDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "AdminConfigController", urlPatterns = {"/admin/config"})
public class AdminConfigController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        ConfigDAO configDAO = new ConfigDAO();
        
        // Initialize defaults if needed
        configDAO.initializeDefaults();
        
        // Load all configs from database
        Map<String, String> configMap = configDAO.getAll();
        
        // Convert to Map<String, Object> for JSP
        Map<String, Object> config = new HashMap<>();
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            // Convert string values to appropriate types
            if ("maintenanceMode".equals(key)) {
                config.put(key, "true".equalsIgnoreCase(value));
            } else if ("maxLoginAttempts".equals(key) || "pageSize".equals(key)) {
                try {
                    config.put(key, Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    config.put(key, value);
                }
            } else {
                config.put(key, value);
            }
        }
        
        // Set default values if not found
        if (!config.containsKey("systemName")) {
            config.put("systemName", "OCS - Online Card Store");
        }
        if (!config.containsKey("maintenanceMode")) {
            config.put("maintenanceMode", false);
        }
        if (!config.containsKey("currency")) {
            config.put("currency", "VND");
        }
        if (!config.containsKey("maxLoginAttempts")) {
            config.put("maxLoginAttempts", 5);
        }
        if (!config.containsKey("emailSupport")) {
            config.put("emailSupport", "support@ocs.com");
        }
        if (!config.containsKey("phoneSupport")) {
            config.put("phoneSupport", "1900-xxxx");
        }
        if (!config.containsKey("pageSize")) {
            config.put("pageSize", 20);
        }
        
        request.setAttribute("config", config);
        
        // Get messages from session if any
        String successMessage = (String) request.getSession().getAttribute("successMessage");
        String errorMessage = (String) request.getSession().getAttribute("errorMessage");
        
        if (successMessage != null) {
            request.setAttribute("successMessage", successMessage);
            request.getSession().removeAttribute("successMessage");
        }
        
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            request.getSession().removeAttribute("errorMessage");
        }
        
        request.setAttribute("pageTitle", "Cấu hình hệ thống");
        request.setAttribute("active", "config");
        request.setAttribute("contentPage", "admin-config.jsp");
        
        request.getRequestDispatcher("/admin.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        ConfigDAO configDAO = new ConfigDAO();
        
        try {
            // Get form parameters
            String systemName = request.getParameter("systemName");
            String maintenanceModeStr = request.getParameter("maintenanceMode");
            String currency = request.getParameter("currency");
            String maxLoginAttemptsStr = request.getParameter("maxLoginAttempts");
            String emailSupport = request.getParameter("emailSupport");
            String phoneSupport = request.getParameter("phoneSupport");
            String pageSizeStr = request.getParameter("pageSize");
            
            boolean hasError = false;
            StringBuilder errorMsg = new StringBuilder();
            
            // Validate and save config
            if (systemName != null && !systemName.trim().isEmpty()) {
                configDAO.setValue("systemName", systemName.trim());
            }
            
            if (maintenanceModeStr != null) {
                configDAO.setValue("maintenanceMode", maintenanceModeStr);
            }
            
            if (currency != null && !currency.trim().isEmpty()) {
                configDAO.setValue("currency", currency.trim().toUpperCase());
            }
            
            if (maxLoginAttemptsStr != null && !maxLoginAttemptsStr.trim().isEmpty()) {
                try {
                    int maxLoginAttempts = Integer.parseInt(maxLoginAttemptsStr);
                    if (maxLoginAttempts >= 1 && maxLoginAttempts <= 10) {
                        configDAO.setValue("maxLoginAttempts", String.valueOf(maxLoginAttempts));
                    } else {
                        hasError = true;
                        errorMsg.append("Số lần đăng nhập tối đa phải từ 1 đến 10. ");
                    }
                } catch (NumberFormatException e) {
                    hasError = true;
                    errorMsg.append("Số lần đăng nhập tối đa không hợp lệ. ");
                }
            }
            
            if (emailSupport != null) {
                configDAO.setValue("emailSupport", emailSupport.trim());
            }
            
            if (phoneSupport != null) {
                configDAO.setValue("phoneSupport", phoneSupport.trim());
            }
            
            if (pageSizeStr != null && !pageSizeStr.trim().isEmpty()) {
                try {
                    int pageSize = Integer.parseInt(pageSizeStr);
                    if (pageSize >= 10 && pageSize <= 100) {
                        configDAO.setValue("pageSize", String.valueOf(pageSize));
                    } else {
                        hasError = true;
                        errorMsg.append("Số bản ghi mỗi trang phải từ 10 đến 100. ");
                    }
                } catch (NumberFormatException e) {
                    hasError = true;
                    errorMsg.append("Số bản ghi mỗi trang không hợp lệ. ");
                }
            }
            
            if (hasError) {
                request.getSession().setAttribute("errorMessage", errorMsg.toString());
            } else {
                request.getSession().setAttribute("successMessage", "Cấu hình đã được lưu thành công!");
            }
            
        } catch (Exception e) {
            System.out.println("AdminConfigController.doPost: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Có lỗi xảy ra khi lưu cấu hình: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/config");
    }
    
    // Getter method to access config from other parts of the application
    public static String getConfig(String key) {
        ConfigDAO configDAO = new ConfigDAO();
        return configDAO.getValue(key);
    }
    
    public static boolean isMaintenanceMode() {
        String mode = getConfig("maintenanceMode");
        return "true".equalsIgnoreCase(mode);
    }
}

