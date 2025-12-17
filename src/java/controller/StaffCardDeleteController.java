package controller;

import dao.CardInfoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.CardInfo;
import model.User;
import util.CardInfoStatus;

/**
 * Staff controller for deleting/inactivating card info.
 * Implements UC26 - Delete Card Code (soft delete).
 */
@WebServlet(name = "StaffCardDeleteController", urlPatterns = { "/staff/card/delete/*" })
public class StaffCardDeleteController extends HttpServlet {

    private final CardInfoDAO cardInfoDAO = new CardInfoDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireStaff(request, response)) {
            return;
        }

        long cardId = extractCardId(request);
        if (cardId == 0) {
            response.sendRedirect(request.getContextPath() + "/staff/card?error=invalid_id");
            return;
        }

        CardInfo card = cardInfoDAO.getById(cardId);
        if (card == null) {
            response.sendRedirect(request.getContextPath() + "/staff/card?error=not_found");
            return;
        }

        // UC26: Check if card has been used or assigned
        // (Assuming SOLD status means card has been used/assigned)
        if (CardInfoStatus.SOLD.equals(card.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/staff/card?error=used_card");
            return;
        }

        // UC26 AF1: Soft delete - mark as INACTIVE instead of hard delete
        card.setStatus(CardInfoStatus.INACTIVE);
        boolean deleted = cardInfoDAO.update(card);

        if (deleted) {
            response.sendRedirect(request.getContextPath() + "/staff/card?success=deleted");
        } else {
            response.sendRedirect(request.getContextPath() + "/staff/card?error=delete_failed");
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
