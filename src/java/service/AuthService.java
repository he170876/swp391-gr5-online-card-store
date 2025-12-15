package service;

import dao.RoleDAO;
import dao.UserDAO;
import model.Role;
import model.User;
import util.PasswordUtil;
import java.math.BigDecimal;

public class AuthService {
    private UserDAO userDAO;
    private RoleDAO roleDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.roleDAO = new RoleDAO();
    }

    public User register(String email, String password, String fullName, String phone, String address) {
        // Validate email uniqueness
        if (userDAO.emailExists(email)) {
            return null; // Email already exists
        }

        // Validate password
        if (password == null || password.length() < 6) {
            return null; // Password too short
        }

        // Get CUSTOMER role
        Role customerRole = roleDAO.findByName("CUSTOMER");
        if (customerRole == null) {
            return null; // CUSTOMER role not found
        }

        // Hash password
        String passwordHash = PasswordUtil.hash(password);
        if (passwordHash == null) {
            return null; // Hashing failed
        }

        // Create user
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setAddress(address);
        user.setStatus("ACTIVE");
        user.setWalletBalance(BigDecimal.ZERO);
        user.setRoleId(customerRole.getId());

        if (userDAO.create(user)) {
            return userDAO.getUserByEmail(email);
        }
        return null;
    }

    public User login(String email, String password) {
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            return null;
        }

        String passwordHash = PasswordUtil.hash(password);
        if (passwordHash == null) {
            return null;
        }

        if (userDAO.checkLogin(email, passwordHash)) {
            return userDAO.getUserByEmail(email);
        }
        return null;
    }

    public boolean changePassword(long userId, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            return false; // New password too short
        }

        User user = userDAO.findById(userId);
        if (user == null) {
            return false;
        }

        // Verify old password
        String oldPasswordHash = PasswordUtil.hash(oldPassword);
        if (oldPasswordHash == null || !oldPasswordHash.equals(user.getPasswordHash())) {
            return false; // Old password incorrect
        }

        // Hash new password
        String newPasswordHash = PasswordUtil.hash(newPassword);
        if (newPasswordHash == null) {
            return false;
        }

        return userDAO.updatePassword(userId, newPasswordHash);
    }

    public boolean updateProfile(long userId, String fullName, String phone, String address) {
        User user = userDAO.findById(userId);
        if (user == null) {
            return false;
        }

        user.setFullName(fullName);
        user.setPhone(phone);
        user.setAddress(address);

        return userDAO.update(user);
    }

    public User getUserById(long userId) {
        return userDAO.findById(userId);
    }

    public boolean emailExists(String email) {
        return userDAO.emailExists(email);
    }
}


