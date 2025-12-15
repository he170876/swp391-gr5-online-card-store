package controller.customer;

import dao.CustomerDAO;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.WalletTransaction;

@WebServlet(name = "CustomerTopupController", urlPatterns = {"/customer/wallet/topup"})
public class CustomerTopupController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/customer/wallet");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        User user = (User) session.getAttribute("user");
        request.setAttribute("walletBalance", user.getWalletBalance());

        String amountRaw = request.getParameter("amount");
        double amount;
        try {
            amount = Double.parseDouble(amountRaw);
        } catch (Exception e) {
            request.setAttribute("error", "Số tiền không hợp lệ.");
            request.getRequestDispatcher("/customer/wallet/index.jsp").forward(request, response);
            return;
        }

        if (amount < 10000) {
            request.setAttribute("error", "Số tiền nạp tối thiểu là 10.000 đ.");
            request.getRequestDispatcher("/customer/wallet/index.jsp").forward(request, response);
            return;
        }

        if (amount > 10000000) {
            request.setAttribute("error", "Số tiền nạp tối đa là 10.000.000 đ.");
            request.getRequestDispatcher("/customer/wallet/index.jsp").forward(request, response);
            return;
        }

        String reference = "NAPVI OCS " + user.getId();
        String bankCode = "ICB";
        String encodedInfo = URLEncoder.encode(reference, StandardCharsets.UTF_8);
        String qrUrl = "https://img.vietqr.io/image/970415-109874971099-compact2.png?amount=" + (long) amount
                + "&addInfo=" + encodedInfo + "&accountName=" + URLEncoder.encode("ONLINE CARD STORE", StandardCharsets.UTF_8);

        CustomerDAO dao = new CustomerDAO();
        WalletTransaction tx = dao.createTopupRequest(user.getId(), amount, bankCode, reference, qrUrl);

        if (tx == null) {
            request.setAttribute("error", "Không thể tạo giao dịch nạp tiền. Vui lòng thử lại.");
        } else {
            request.setAttribute("newTopup", tx);
            request.setAttribute("success", "Tạo yêu cầu nạp tiền thành công. Vui lòng quét mã VietQR để thanh toán.");
        }

        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        String type = request.getParameter("type");

        int total = dao.countWalletTransactions(user.getId(),
                status == null ? "" : status,
                keyword == null ? "" : keyword,
                type == null ? "" : type);
        int totalPages = (int) Math.ceil(total / 10.0);
        if (totalPages == 0) totalPages = 1;
        request.setAttribute("transactions", dao.getWalletTransactionsByUser(
                user.getId(),
                status == null ? "" : status,
                keyword == null ? "" : keyword,
                type == null ? "" : type,
                1, 10));
        request.setAttribute("currentPage", 1);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/customer/wallet/index.jsp").forward(request, response);
    }
}

