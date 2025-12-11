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

        if (email == null || email.isEmpty()) {
            errors.put("email", "Email is required");
        } else if (!v.isValidEmail(email)) {
            errors.put("email", "Email format is invalid");
        }

        if (password == null || password.isEmpty()) {
            errors.put("password", "Password is required");
        } else if (!v.isValidPassword(password, confirmPassword)) {
            errors.put("password", "Password must be at least 6 characters, contain lowercase, uppercase, digit and special character and match confirmation");
        }

        if (fullName == null || fullName.isEmpty()) {
            errors.put("fullName", "Full name is required");
        } else if (!v.isValidFullName(fullName)) {
            errors.put("fullName", "Full name is invalid. Use letters and spaces, each word starts with uppercase");
        }

        if (phone != null && !phone.isEmpty() && !v.isValidPhoneNumber(phone)) {
            errors.put("phone", "Phone number is invalid");
        }

        if (address != null && !address.isEmpty() && !v.isValidAddress(address)) {
            errors.put("address", "Address is invalid");
        }

        UserDAO dao = new UserDAO();

        if (!errors.containsKey("email")) {
            User existing = dao.getUserByEmail(email);
            if (existing != null) {
                errors.put("email", "Email already exists");
            }
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("fullNameValue", fullName);
            request.setAttribute("emailValue", email);
            request.setAttribute("phoneValue", phone);
            request.setAttribute("addressValue", address);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        String hashedInput = PasswordUtil.hash(password);
        if (hashedInput == null) {
            errors.put("general", "Password hashing error");
            request.setAttribute("errors", errors);
            request.setAttribute("fullNameValue", fullName);
            request.setAttribute("emailValue", email);
            request.setAttribute("phoneValue", phone);
            request.setAttribute("addressValue", address);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        User u = new User();
        u.setFullName(fullName);
        u.setEmail(email);
        u.setPasswordHash(hashedInput);
        u.setPhone(phone);
        u.setAddress(address);
        u.setRoleId(3);

        boolean inserted = dao.insertUser(u);

        if (!inserted) {
            errors.put("general", "Cannot create account, please try again later");
            request.setAttribute("errors", errors);
            request.setAttribute("fullNameValue", fullName);
            request.setAttribute("emailValue", email);
            request.setAttribute("phoneValue", phone);
            request.setAttribute("addressValue", address);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/login");
    }
}
