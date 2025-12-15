package controller;

import dao.CardInfoDAO.CardInfoListView;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import model.Product;
import model.Provider;
import model.User;
import service.CardInfoService;
import util.CardInfoStatus;

/**
 * Staff view for card info list with filters/sorting.
 */
@WebServlet(name = "StaffCardListController", urlPatterns = { "/staff/cards" })
public class StaffCardListController extends HttpServlet {

    private final CardInfoService cardInfoService = new CardInfoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = requireStaff(request, response);
        if (user == null) {
            return;
        }

        String status = Optional.ofNullable(request.getParameter("status")).orElse("");
        Long productId = parseLong(request.getParameter("productId"));
        Long providerId = parseLong(request.getParameter("providerId"));
        LocalDate expiryFrom = parseDate(request.getParameter("expiryFrom"));
        LocalDate expiryTo = parseDate(request.getParameter("expiryTo"));
        String sort = Optional.ofNullable(request.getParameter("sort")).orElse("created_desc");

        List<CardInfoListView> cards = cardInfoService.search(status, productId, providerId, expiryFrom, expiryTo,
                sort);
        List<Product> products = cardInfoService.listProducts();
        Map<Long, Provider> providerMap = cardInfoService.mapProviders();

        request.setAttribute("cards", cards);
        request.setAttribute("statuses", java.util.Arrays.asList(CardInfoStatus.ALL_STATUSES));
        request.setAttribute("products", products);
        request.setAttribute("providers", providerMap.values());
        request.setAttribute("selectedStatus", status);
        request.setAttribute("selectedProductId", productId);
        request.setAttribute("selectedProviderId", providerId);
        request.setAttribute("expiryFrom", expiryFrom);
        request.setAttribute("expiryTo", expiryTo);
        request.setAttribute("sort", sort);

        request.getRequestDispatcher("/staff-cards.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private Long parseLong(String value) {
        try {
            if (value == null || value.isBlank())
                return null;
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate parseDate(String value) {
        try {
            if (value == null || value.isBlank())
                return null;
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private User requireStaff(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Object obj = request.getSession().getAttribute("user");
        if (obj == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        User user = (User) obj;
        if (user.getRoleId() != 1 && user.getRoleId() != 2) { // admin or staff
            response.sendRedirect(request.getContextPath() + "/home?error=403");
            return null;
        }
        return user;
    }
}
