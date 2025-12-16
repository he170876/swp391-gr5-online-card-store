/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.authentication;

import dao.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import model.SendOTPResult;
import model.User;
import service.SendOTPService;
import util.PasswordUtil;
import util.Validation;

/**
 *
 * @author hades
 */
@WebServlet(name = "RegisterController", urlPatterns = {"/register"})
public class RegisterController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String fullName = request.getParameter("fullName") == null ? null : request.getParameter("fullName").trim();
        String email = request.getParameter("email") == null ? null : request.getParameter("email").trim();
        String phone = request.getParameter("phone") == null ? null : request.getParameter("phone").trim();
        String address = request.getParameter("address") == null ? null : request.getParameter("address").trim();
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        Validation v = new Validation();
        Map<String, String> errors = new HashMap<>();

        // ===== VALIDATE EMAIL =====
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email không được để trống");
        } else if (email.length() < 5 || email.length() > 255) {
            errors.put("email", "Email phải có độ dài từ 5 đến 255 ký tự");
        } else if (!v.isValidEmail(email)) {
            errors.put("email", "Email không đúng định dạng (vd: example@gmail.com)");
        }

        // ===== VALIDATE PASSWORD =====
        if (password == null || password.isEmpty()) {
            errors.put("password", "Mật khẩu không được để trống");
        } else if (password.length() < 8 || password.length() > 64) {
            errors.put("password", "Mật khẩu phải có từ 8 đến 64 ký tự");
        } else if (!v.isValidPassword(password, confirmPassword)) {
            errors.put("password",
                    "Mật khẩu phải gồm chữ hoa, chữ thường, số, ký tự đặc biệt và trùng khớp xác nhận");
        }

        // ===== VALIDATE FULL NAME =====
        if (fullName == null || fullName.trim().isEmpty()) {
            errors.put("fullName", "Họ và tên không được để trống");
        } else if (fullName.length() < 2 || fullName.length() > 100) {
            errors.put("fullName", "Họ và tên phải có độ dài từ 2 đến 100 ký tự");
        } else if (!v.isValidFullName(fullName)) {
            errors.put("fullName",
                    "Họ và tên chỉ được chứa chữ cái và khoảng trắng, không có khoảng trắng kép");
        }

        // ===== VALIDATE PHONE =====
        if (phone != null && !phone.trim().isEmpty()) {
            if (phone.length() < 10 || phone.length() > 15) {
                errors.put("phone", "Số điện thoại phải có từ 10 đến 15 chữ số");
            } else if (!v.isValidPhoneNumber(phone)) {
                errors.put("phone", "Số điện thoại chỉ được chứa số và có thể bắt đầu bằng dấu +");
            }
        }

        // ===== VALIDATE ADDRESS =====
        if (address != null && !address.trim().isEmpty()) {
            if (address.length() < 5 || address.length() > 255) {
                errors.put("address", "Địa chỉ phải có độ dài từ 5 đến 255 ký tự");
            } else if (!v.isValidAddress(address)) {
                errors.put("address",
                        "Địa chỉ chỉ được chứa chữ, số, dấu phẩy, dấu chấm và khoảng trắng");
            }
        }

        UserDAO dao = new UserDAO();

        // ===== CHECK EMAIL EXIST =====
        if (!errors.containsKey("email")) {
            User existing = dao.getUserByEmail(email);
            if (existing != null) {
                errors.put("email", "Email đã tồn tại!");
            }
        }

        // ===== RETURN FORM IF ERROR =====
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("fullNameValue", fullName);
            request.setAttribute("emailValue", email);
            request.setAttribute("phoneValue", phone);
            request.setAttribute("addressValue", address);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // ===== HASH PASSWORD =====
        String hashedPassword = PasswordUtil.hash(password);
        if (hashedPassword == null) {
            errors.put("general", "Lỗi mã hóa mật khẩu");
            request.setAttribute("errors", errors);
            request.setAttribute("fullNameValue", fullName);
            request.setAttribute("emailValue", email);
            request.setAttribute("phoneValue", phone);
            request.setAttribute("addressValue", address);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // ===== CREATE USER =====
        User u = new User();
        u.setFullName(fullName);
        u.setEmail(email);
        u.setPasswordHash(hashedPassword);
        u.setPhone(phone);
        u.setAddress(address);
        u.setRoleId(3); // CUSTOMER
        u.setStatus("INACTIVE");
        boolean inserted = dao.insertUser(u);

        if (!inserted) {
            errors.put("general", "Không thể tạo tài khoản, vui lòng thử lại sau");
            request.setAttribute("errors", errors);
            request.setAttribute("fullNameValue", fullName);
            request.setAttribute("emailValue", email);
            request.setAttribute("phoneValue", phone);
            request.setAttribute("addressValue", address);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByEmail(email);

        if (user == null) {
            String error = URLEncoder.encode("Không tìm thấy Người dùng! Vui lòng kiểm tra lại!", "UTF-8");
            response.sendRedirect("/login?error=" + error);
            return;
        }

        if (user.getStatus().equals("LOCKED")) {
            String error = URLEncoder.encode("Tài khoản đã bị khóa! Vui lòng liên hệ Admin!", "UTF-8");
            response.sendRedirect("/login?error=" + error);
            return;
        }

        if (user.getStatus().equals("ACTIVE")) {
            String error = URLEncoder.encode("Tài khoản đã xác thực! Vui lòng đăng nhập!", "UTF-8");
            response.sendRedirect("/login?error=" + error);
            return;
        }

        // ===== SAVE EMAIL FOR OTP VERIFY =====
        HttpSession session = request.getSession();
        session.setAttribute("registerEmail", user.getEmail());

        // ===== SEND OTP USING SERVICE =====
        SendOTPService otpService = new SendOTPService();
        SendOTPResult result = otpService.sendRegisterOTP(user);

        if (!result.isSuccess()) {
            request.setAttribute("error", result.getMessage());
            request.getRequestDispatcher("register-verify-otp.jsp")
                    .forward(request, response);
            return;
        }

        request.setAttribute("maskedEmail", maskEmail(user.getEmail()));
        request.setAttribute("msg", result.getMessage());
        request.getRequestDispatcher("register-verify-otp.jsp")
                .forward(request, response);
    }

    public String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 2) {
            return "***" + email.substring(atIndex);
        }
        return email.substring(0, 2) + "****" + email.substring(atIndex);
    }
}
