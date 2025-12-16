package controller;

import dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.util.List;
import model.Product;
import model.User;
import service.CardCodeImportService;
import service.CardCodeImportService.ImportResult;

/**
 * Handles CSV import for card codes.
 */
@WebServlet(name = "CardImportController", urlPatterns = { "/staff/cards-import" })
@MultipartConfig
public class CardImportController extends HttpServlet {

    private final CardCodeImportService importService = new CardCodeImportService();
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireStaff(request, response)) {
            return;
        }
        List<Product> products = productDAO.listAll();
        request.setAttribute("products", products);
        request.getRequestDispatcher("/card-import.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireStaff(request, response)) {
            return;
        }

        String productIdRaw = request.getParameter("productId");
        Part filePart = request.getPart("file");

        if (productIdRaw == null || productIdRaw.isBlank()) {
            request.setAttribute("error", "Thiếu productId");
            List<Product> products = productDAO.listAll();
            request.setAttribute("products", products);
            request.getRequestDispatcher("/card-import.jsp").forward(request, response);
            return;
        }

        long productId;
        try {
            productId = Long.parseLong(productIdRaw);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "productId không hợp lệ");
            List<Product> products = productDAO.listAll();
            request.setAttribute("products", products);
            request.getRequestDispatcher("/card-import.jsp").forward(request, response);
            return;
        }

        if (filePart == null || filePart.getSize() == 0) {
            request.setAttribute("error", "Chưa chọn file CSV");
            List<Product> products = productDAO.listAll();
            request.setAttribute("products", products);
            request.getRequestDispatcher("/card-import.jsp").forward(request, response);
            return;
        }

        try {
            ImportResult result = importService.importCsv(productId, filePart.getInputStream());
            request.setAttribute("result", result);
        } catch (IOException e) {
            request.setAttribute("error", "Không đọc được file: " + e.getMessage());
        }

        List<Product> products = productDAO.listAll();
        request.setAttribute("products", products);
        request.getRequestDispatcher("/card-import.jsp").forward(request, response);
    }

    private boolean requireStaff(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Object obj = request.getSession().getAttribute("user");
        if (obj == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        User user = (User) obj;
        if (user.getRoleId() != 1 && user.getRoleId() != 2) {
            response.sendRedirect(request.getContextPath() + "/home?error=403");
            return false;
        }
        return true;
    }
}
