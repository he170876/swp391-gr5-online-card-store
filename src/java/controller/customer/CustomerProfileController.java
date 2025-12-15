package controller.customer;

import dao.CustomerDAO;
import dao.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

@WebServlet(name = "CustomerProfileController", urlPatterns = {"/customer/profile"})
public class CustomerProfileController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User sessionUser = (User) session.getAttribute("user");
        User user = new UserDAO().getUserById(sessionUser.getId());
        if (user != null) {
            session.setAttribute("user", user);
            request.setAttribute("userProfile", user);
        }

        request.getRequestDispatcher("/customer/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        request.setCharacterEncoding("UTF-8");
        User sessionUser = (User) session.getAttribute("user");

        String fullName = request.getParameter("fullName") == null ? "" : request.getParameter("fullName").trim();
        String phone = request.getParameter("phone") == null ? "" : request.getParameter("phone").trim();
        String address = request.getParameter("address") == null ? "" : request.getParameter("address").trim();

        boolean valid = true;
        if (fullName.isEmpty() || fullName.length() < 3 || fullName.length() > 50 || !fullName.matches("^[\\p{L} ]+$")) {
            request.setAttribute("errorFullName", "Họ và tên không hợp lệ. Vui lòng nhập từ 3–50 ký tự chữ cái.");
            valid = false;
        }
        if (phone.isEmpty() || !phone.matches("^(0[3|5|7|8|9])[0-9]{8}$")) {
            request.setAttribute("errorPhone", "Số điện thoại không hợp lệ.");
            valid = false;
        }
        if (address.isEmpty() || address.length() < 5 || address.length() > 100 || !address.matches("^[\\p{L}0-9 ,]{5,100}$") || address.matches("^(123|abc)$")) {
            request.setAttribute("errorAddress", "Địa chỉ không hợp lệ. Vui lòng nhập đầy đủ địa chỉ.");
            valid = false;
        }

        if (!valid) {
            sessionUser.setFullName(fullName);
            sessionUser.setPhone(phone);
            sessionUser.setAddress(address);
            request.setAttribute("userProfile", sessionUser);
            request.getRequestDispatcher("/customer/profile.jsp").forward(request, response);
            return;
        }

        CustomerDAO dao = new CustomerDAO();
        boolean updated = dao.updateUserProfile(sessionUser.getId(), fullName, phone, address);
        if (updated) {
            User refreshed = new UserDAO().getUserById(sessionUser.getId());
            session.setAttribute("user", refreshed);
            request.setAttribute("userProfile", refreshed);
            request.setAttribute("success", "Cập nhật thông tin thành công.");
        } else {
            request.setAttribute("userProfile", sessionUser);
            request.setAttribute("error", "Không thể cập nhật thông tin. Vui lòng thử lại.");
        }
        request.getRequestDispatcher("/customer/profile.jsp").forward(request, response);
    }
}

