package controller.customer;

import dao.CustomerDAO;
import dao.UserDAO;
import java.io.IOException;
import java.math.BigDecimal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Product;
import model.User;

@WebServlet(name = "CustomerProductDetailController", urlPatterns = {"/customer/product/detail"})
public class CustomerProductDetailController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idRaw = request.getParameter("id");
        long id;
        try {
            id = Long.parseLong(idRaw);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/customer/products");
            return;
        }

        CustomerDAO dao = new CustomerDAO();
        Product product = dao.getActiveProductById(id);
        if (product == null) {
            response.sendRedirect(request.getContextPath() + "/customer/products?error=notfound");
            return;
        }

        int available = dao.countAvailableCards(id);
        request.setAttribute("product", product);
        request.setAttribute("availableCount", available);

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User sessionUser = (User) session.getAttribute("user");
            User freshUser = new UserDAO().getUserById(sessionUser.getId());
            if (freshUser != null) {
                session.setAttribute("user", freshUser);
                request.setAttribute("walletBalance", freshUser.getWalletBalance());
            } else {
                request.setAttribute("walletBalance", sessionUser.getWalletBalance());
            }
        }

        request.getRequestDispatcher("/customer/product/detail.jsp").forward(request, response);
    }
}

