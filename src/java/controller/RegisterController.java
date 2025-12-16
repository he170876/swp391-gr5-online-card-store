/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

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
import model.User;
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
        if (email == null || email.isEmpty()) {
            errors.put("email", "Email không được để trống");
        } else if (!v.isValidEmail(email)) {
            errors.put("email", "Định dạng email không hợp lệ");
        }

        // ===== VALIDATE PASSWORD =====
        if (password == null || password.isEmpty()) {
            errors.put("password", "Mật khẩu không được để trống");
        } else if (!v.isValidPassword(password, confirmPassword)) {
            errors.put("password",
                    "Mật khẩu phải có ít nhất 6 ký tự, bao gồm chữ hoa, chữ thường, số, ký tự đặc biệt và trùng khớp xác nhận");
        }

        // ===== VALIDATE FULL NAME =====
        if (fullName == null || fullName.isEmpty()) {
            errors.put("fullName", "Họ và tên không được để trống");
        } else if (!v.isValidFullName(fullName)) {
            errors.put("fullName", "Họ và tên không hợp lệ. Chỉ chứa chữ cái và khoảng trắng");
        }

        // ===== VALIDATE PHONE =====
        if (phone != null && !phone.isEmpty() && !v.isValidPhoneNumber(phone)) {
            errors.put("phone", "Số điện thoại không hợp lệ");
        }

        // ===== VALIDATE ADDRESS =====
        if (address != null && !address.isEmpty() && !v.isValidAddress(address)) {
            errors.put("address", "Địa chỉ không hợp lệ");
        }

        UserDAO dao = new UserDAO();

        // ===== CHECK EMAIL EXIST =====
        if (!errors.containsKey("email")) {
            User existing = dao.getUserByEmail(email);
            if (existing != null) {
                errors.put("email", "Email đã tồn tại");
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

        // ===== SAVE EMAIL FOR OTP VERIFY =====
        HttpSession session = request.getSession();
        session.setAttribute("registerEmail", email);

        String msg = URLEncoder.encode(
                "Đăng ký thành công! Vui lòng kiểm tra email để nhập mã OTP.",
                "UTF-8"
        );

        response.sendRedirect(request.getContextPath() + "/registerVerifyOTP?msg=" + msg);
    }
}
