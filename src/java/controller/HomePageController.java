/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import model.User;

/**
 *
 * @author hades
 */
@WebServlet(name = "HomePageController", urlPatterns = {"/home"})
public class HomePageController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Optional<User> logged = Optional.ofNullable(request.getSession().getAttribute("user")).map(obj -> (User) obj);

        //chưa đăng nhập
        if (logged.isEmpty()) {
            request.getRequestDispatcher("homepage.jsp").forward(request, response);
            return;
        }

        //lấy lỗi
        String error = "";
        if (request.getParameter("error") != null && !request.getParameter("error").isBlank()) {
            error = "?error=" + URLEncoder.encode(request.getParameter("error"), StandardCharsets.UTF_8);
        }

        switch ((int) logged.get().getRoleId()) {
            case 3 ->
                response.sendRedirect(request.getContextPath() + "homepage.jsp" + error);
            case 2 ->
                response.sendRedirect(request.getContextPath() + "staff.jsp" + error);
            case 1 ->
                response.sendRedirect(request.getContextPath() + "admin.jsp" + error);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
