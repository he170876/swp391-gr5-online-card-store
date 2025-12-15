package controller.provider;

import dao.ProviderDAO;
import dto.ProviderSearchDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import model.Provider;

@WebServlet(name = "ProviderListController", urlPatterns = {"/staff/provider"})
public class ProviderListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Parse search parameters
        ProviderSearchDTO searchDTO = new ProviderSearchDTO();
        searchDTO.setKeyword(Optional.ofNullable(request.getParameter("keyword")).orElse(""));
        searchDTO.setStatus(Optional.ofNullable(request.getParameter("status")).orElse(""));

        String pageStr = request.getParameter("page");
        try {
            searchDTO.setPage(pageStr != null && !pageStr.isEmpty() ? Integer.parseInt(pageStr) : 1);
        } catch (NumberFormatException e) {
            searchDTO.setPage(1);
        }

        // 2. Get data
        ProviderDAO dao = new ProviderDAO();
        List<Provider> providers = dao.search(searchDTO);
        int totalCount = dao.count(searchDTO);
        int totalPages = (int) Math.ceil((double) totalCount / searchDTO.getPageSize());

        // 3. Set attributes
        request.setAttribute("providers", providers);
        request.setAttribute("searchDTO", searchDTO);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", searchDTO.getPage());

        // 4. Forward to JSP (uses staff.jsp as master layout)
        request.setAttribute("pageTitle", "Quản lý nhà cung cấp");
        request.setAttribute("active", "provider");
        request.setAttribute("contentPage", "staff-provider-list.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
}
