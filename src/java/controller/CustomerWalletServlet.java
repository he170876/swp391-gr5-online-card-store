package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.User;
import service.WalletService;

@WebServlet(name = "CustomerWalletServlet", urlPatterns = {"/customer/wallet"})
public class CustomerWalletServlet extends HttpServlet {

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

        // Refresh user data
        user = walletService.getUser(user.getId());
        request.getSession().setAttribute("user", user);

        // Get recent transactions (last 10)
        var transactions = walletService.getTransactions(user.getId());
        if (transactions.size() > 10) {
            transactions = transactions.subList(0, 10);
        }
        request.setAttribute("transactions", transactions);

        request.getRequestDispatcher("/WEB-INF/views/customer/wallet.jsp").forward(request, response);
    }
}


