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
import java.util.List;
import model.User;
import model.WalletTransaction;

@WebServlet(name = "CustomerWalletController", urlPatterns = {"/customer/wallet"})
public class CustomerWalletController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User sessionUser = (User) session.getAttribute("user");
            User fresh = new UserDAO().getUserById(sessionUser.getId());
            if (fresh != null) {
                session.setAttribute("user", fresh);
                request.setAttribute("walletBalance", fresh.getWalletBalance());
            } else {
                request.setAttribute("walletBalance", sessionUser.getWalletBalance());
            }
        }
        if (request.getAttribute("walletBalance") == null && session != null && session.getAttribute("user") != null) {
            request.setAttribute("walletBalance", ((User) session.getAttribute("user")).getWalletBalance());
        }

        CustomerDAO dao = new CustomerDAO();
        User u = (User) request.getSession().getAttribute("user");
        String status = request.getParameter("status");
        String keyword = request.getParameter("keyword");
        String type = request.getParameter("type");
        String pageRaw = request.getParameter("page");
        int page = 1;
        int pageSize = 10;
        try {
            if (pageRaw != null) {
                page = Math.max(1, Integer.parseInt(pageRaw));
            }
        } catch (NumberFormatException ignored) {
        }

        int total = dao.countWalletTransactions(u.getId(), status, keyword, type);
        int totalPages = (int) Math.ceil(total / (double) pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        List<WalletTransaction> history = dao.getWalletTransactionsByUser(u.getId(), status, keyword, type, page, pageSize);
        request.setAttribute("transactions", history);
        request.setAttribute("status", status);
        request.setAttribute("keyword", keyword);
        request.setAttribute("type", type);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/customer/wallet/index.jsp").forward(request, response);
    }
}

