package controller;

import dao.CardInfoDAO;
import dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import model.CardInfo;
import model.Product;
import model.User;
import util.CardInfoStatus;

/**
 * Staff controller for updating card info.
 */
@WebServlet(name = "StaffCardUpdateController", urlPatterns = {"/staff/card/edit/*"})
public class StaffCardUpdateController extends HttpServlet {

    private final CardInfoDAO cardInfoDAO = new CardInfoDAO();
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireStaff(request, response)) {
            return;
        }

        long cardId = extractCardId(request);
        if (cardId == 0) {
            String error = URLEncoder.encode("Không tìm thấy Id!", "UTF-8");
            response.sendRedirect(request.getContextPath() + "/staff/card?error=" + error);
            return;
        }

        CardInfo card = cardInfoDAO.getById(cardId);
        if (card == null) {
            String error = URLEncoder.encode("Không tìm thấy Thẻ!", "UTF-8");
            response.sendRedirect(request.getContextPath() + "/staff/card?error=" + error);
            return;
        }
        // Không cho sửa thẻ đã bán
        if (CardInfoStatus.SOLD.equals(card.getStatus())) {
            String error = URLEncoder.encode("Thẻ đang được dùng hoặc đã bán!", "UTF-8");
            response.sendRedirect(request.getContextPath() + "/staff/card?error=" + error);
            return;
        }

        List<Product> products = productDAO.listAll();
        request.setAttribute("card", card);
        request.setAttribute("products", products);
        request.setAttribute("statuses", Arrays.asList(CardInfoStatus.ALL_STATUSES));
        request.setAttribute("pageTitle", "Sửa thông tin thẻ");
        request.setAttribute("active", "card");
        request.setAttribute("contentPage", "staff-card-edit.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireStaff(request, response)) {
            return;
        }

        long cardId = extractCardId(request);
        if (cardId == 0) {
            String error = URLEncoder.encode("Không tìm thấy Id!", "UTF-8");
            response.sendRedirect(request.getContextPath() + "/staff/card?error=" + error);
            return;
        }

        CardInfo existing = cardInfoDAO.getById(cardId);
        if (existing == null) {
            String error = URLEncoder.encode("Không tìm thấy Thẻ!", "UTF-8");
            response.sendRedirect(request.getContextPath() + "/staff/card?error=" + error);
            return;
        }
        // Không cho sửa thẻ đã bán
        if (CardInfoStatus.SOLD.equals(existing.getStatus())) {
            String error = URLEncoder.encode("Thẻ đang được dùng hoặc đã bán!", "UTF-8");
            response.sendRedirect(request.getContextPath() + "/staff/card?error=" + error);
            return;
        }

        String code = request.getParameter("code");
        String serial = request.getParameter("serial");
        String productIdRaw = request.getParameter("productId");
        String expiryDateRaw = request.getParameter("expiryDate");
        String status = request.getParameter("status");

        StringBuilder errors = new StringBuilder();

        if (code == null || code.trim().isEmpty()) {
            errors.append("Code không được trống. ");
        }
        if (serial == null || serial.trim().isEmpty()) {
            errors.append("Serial không được trống. ");
        }

        long productId = 0;
        try {
            productId = Long.parseLong(productIdRaw);
        } catch (NumberFormatException e) {
            errors.append("Product không hợp lệ. ");
        }

        LocalDate expiryDate = null;
        if (expiryDateRaw != null && !expiryDateRaw.trim().isEmpty()) {
            try {
                expiryDate = LocalDate.parse(expiryDateRaw);
            } catch (DateTimeParseException e) {
                errors.append("Ngày hết hạn không hợp lệ (yyyy-MM-dd). ");
            }
        }

        LocalDate today = LocalDate.now();

        // Nếu có ngày hết hạn
        if (expiryDate != null) {

            // 🔴 ĐÃ HẾT HẠN
            if (expiryDate.isBefore(today)) {

                // Không cho mở bán
                if ("AVAILABLE".equals(status)) {
                    errors.append("Thẻ đã hết hạn, không thể mở bán. ");
                }

                // Tự động set EXPIRED
                status = CardInfoStatus.EXPIRED;
            } // 🟢 CHƯA HẾT HẠN
            else {
                // Nếu staff chọn EXPIRED nhưng chưa hết hạn → không hợp lệ
                if (CardInfoStatus.EXPIRED.equals(status)) {
                    errors.append("Thẻ chưa hết hạn, không thể chọn trạng thái hết hạn. ");
                }
            }
        }

        if (status == null || status.trim().isEmpty()) {
            errors.append("Status không được trống. ");
        } else {
            boolean validStatus = false;
            for (String s : CardInfoStatus.ALL_STATUSES) {
                if (s.equals(status)) {
                    validStatus = true;
                    break;
                }
            }
            if (!validStatus) {
                errors.append("Status không hợp lệ. ");
            }
        }

        // Check code unique (if changed)
        if (!code.trim().equals(existing.getCode())) {
            List<String> existingCodes = cardInfoDAO.findExistingCodes(java.util.Set.of(code.trim()));
            if (!existingCodes.isEmpty()) {
                errors.append("Code đã tồn tại. ");
            }
        }

        if (errors.length() > 0) {
            List<Product> products = productDAO.listAll();
            request.setAttribute("card", existing);
            request.setAttribute("products", products);
            request.setAttribute("statuses", Arrays.asList(CardInfoStatus.ALL_STATUSES));
            request.setAttribute("error", errors.toString());
            request.setAttribute("pageTitle", "Sửa thông tin thẻ");
            request.setAttribute("active", "card");
            request.setAttribute("contentPage", "staff-card-edit.jsp");
            request.getRequestDispatcher("/staff.jsp").forward(request, response);
            return;
        }

        existing.setCode(code.trim());
        existing.setSerial(serial.trim().toUpperCase());
        existing.setProductId(productId);
        existing.setExpiryDate(expiryDate);
        existing.setStatus(status);

        boolean updated = cardInfoDAO.update(existing);
        if (updated) {
            response.sendRedirect(request.getContextPath() + "/staff/card?success=Cập nhập thành công!");
        } else {
            List<Product> products = productDAO.listAll();
            request.setAttribute("card", existing);
            request.setAttribute("products", products);
            request.setAttribute("statuses", Arrays.asList(CardInfoStatus.ALL_STATUSES));
            request.setAttribute("error", "Không thể cập nhật card (kiểm tra ràng buộc DB)");
            request.setAttribute("pageTitle", "Sửa thông tin thẻ");
            request.setAttribute("active", "card");
            request.setAttribute("contentPage", "staff-card-edit.jsp");
            request.getRequestDispatcher("/staff.jsp").forward(request, response);
        }
    }

    private long extractCardId(HttpServletRequest request) {
        String path = request.getRequestURI();
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length; i++) {
            if ("edit".equals(parts[i]) && i + 1 < parts.length) {
                try {
                    return Long.parseLong(parts[i + 1]);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
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
