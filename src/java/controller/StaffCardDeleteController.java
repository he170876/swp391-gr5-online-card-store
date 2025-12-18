package controller;

import dao.CardInfoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import model.CardInfo;
import model.User;
import util.CardInfoStatus;

/**
 * Staff controller for deleting/inactivating card info. Implements UC26 -
 * Delete Card Code (soft delete).
 */
@WebServlet(name = "StaffCardDeleteController", urlPatterns = {"/staff/card/delete/*"})
public class StaffCardDeleteController extends HttpServlet {

    private final CardInfoDAO cardInfoDAO = new CardInfoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
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

        CardInfo card = cardInfoDAO.getById(cardId);
        if (card == null) {
            String error = URLEncoder.encode("Không tìm thấy Thẻ!", "UTF-8");
            response.sendRedirect(request.getContextPath() + "/staff/card?error=" + error);
            return;
        }

        // UC26: Check if card has been used or assigned
        // (Assuming SOLD status means card has been used/assigned)
        if (CardInfoStatus.SOLD.equals(card.getStatus())) {
            String error = URLEncoder.encode("Thẻ đang được dùng hoặc đã bán!", "UTF-8");
            response.sendRedirect(request.getContextPath() + "/staff/card?error=" + error);
            return;
        }

        // UC26 AF1: Soft delete - mark as INACTIVE instead of hard delete
        card.setStatus(CardInfoStatus.INACTIVE);
        boolean deleted = cardInfoDAO.update(card);

        if (deleted) {
            String success = URLEncoder.encode("Xóa thành công!", "UTF-8");
            response.sendRedirect(request.getContextPath() + "/staff/card?success=" + success);
        } else {
            String error = URLEncoder.encode("Xóa không thành công!", "UTF-8");
            response.sendRedirect(request.getContextPath() + "/staff/card?error=" + error);
        }
    }

    private long extractCardId(HttpServletRequest request) {
        String path = request.getRequestURI();
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length; i++) {
            if ("delete".equals(parts[i]) && i + 1 < parts.length) {
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
