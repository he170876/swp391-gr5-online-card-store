package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.User;
import model.WalletTransaction;
import service.WalletService;

@WebServlet(name = "CustomerWalletTopupServlet", urlPatterns = {"/customer/wallet-topup"})
public class CustomerWalletTopupServlet extends HttpServlet {

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

        String referenceCode = request.getParameter("referenceCode");
        if (referenceCode != null && !referenceCode.isEmpty()) {
            // Show top-up confirmation page
            var transaction = walletService.getTransactions(user.getId()).stream()
                    .filter(t -> referenceCode.equals(t.getReferenceCode()))
                    .findFirst()
                    .orElse(null);
            request.setAttribute("transaction", transaction);
        }

        request.getRequestDispatcher("/WEB-INF/views/customer/wallet-topup.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if ("create".equals(action)) {
            // Create top-up request
            String amountStr = request.getParameter("amount");
            if (amountStr == null || amountStr.isEmpty()) {
                request.setAttribute("error", "Vui lòng nhập số tiền.");
                request.getRequestDispatcher("/WEB-INF/views/customer/wallet-topup.jsp").forward(request, response);
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                if (amount < 10000) {
                    request.setAttribute("error", "Số tiền nạp tối thiểu là 10,000 VNĐ.");
                    request.getRequestDispatcher("/WEB-INF/views/customer/wallet-topup.jsp").forward(request, response);
                    return;
                }
                if (amount > 5000000) {
                    request.setAttribute("error", "Số tiền nạp tối đa là 5,000,000 VNĐ.");
                    request.getRequestDispatcher("/WEB-INF/views/customer/wallet-topup.jsp").forward(request, response);
                    return;
                }

                WalletTransaction transaction = walletService.createTopupRequest(user.getId(), amount);
                if (transaction != null) {
                    response.sendRedirect(request.getContextPath() + "/customer/wallet-topup?referenceCode=" + transaction.getReferenceCode());
                } else {
                    request.setAttribute("error", "Tạo yêu cầu nạp tiền thất bại.");
                    request.getRequestDispatcher("/WEB-INF/views/customer/wallet-topup.jsp").forward(request, response);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Số tiền không hợp lệ.");
                request.getRequestDispatcher("/WEB-INF/views/customer/wallet-topup.jsp").forward(request, response);
            }
        } else if ("confirm".equals(action)) {
            // Confirm payment
            String referenceCode = request.getParameter("referenceCode");
            if (referenceCode != null && !referenceCode.isEmpty()) {
                if (walletService.confirmTopup(referenceCode)) {
                    request.setAttribute("success", "Nạp tiền thành công!");
                    // Refresh user
                    user = walletService.getUser(user.getId());
                    request.getSession().setAttribute("user", user);
                } else {
                    request.setAttribute("error", "Xác nhận thanh toán thất bại.");
                }
            }
            request.getRequestDispatcher("/WEB-INF/views/customer/wallet-topup.jsp").forward(request, response);
        }
    }
}


