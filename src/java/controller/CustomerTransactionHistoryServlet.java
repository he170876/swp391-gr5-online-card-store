package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.User;
import service.WalletService;

@WebServlet(name = "CustomerTransactionHistoryServlet", urlPatterns = {"/customer/transactions"})
public class CustomerTransactionHistoryServlet extends HttpServlet {

    private WalletService walletService;

    @Override
    public void init() throws ServletException {
        super.init();
        walletService = new WalletService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String type = request.getParameter("type");
        if (type != null && !type.isEmpty()) {
            var transactions = walletService.getTransactionsByType(user.getId(), type);
            request.setAttribute("transactions", transactions);
            request.setAttribute("filterType", type);
        } else {
            var transactions = walletService.getTransactions(user.getId());
            request.setAttribute("transactions", transactions);
        }

        request.getRequestDispatcher("/WEB-INF/views/customer/transactions.jsp").forward(request, response);
    }
}


