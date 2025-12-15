package controller.provider;

import dao.ProviderDAO;
import dto.ProviderFormDTO;
import model.Provider;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@WebServlet(name = "ProviderAddController", urlPatterns = {"/staff/provider/add"})
public class ProviderAddController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("formDTO", new ProviderFormDTO());

        // Forward to JSP
        request.setAttribute("pageTitle", "Thêm nhà cung cấp");
        request.setAttribute("active", "provider");
        request.setAttribute("contentPage", "staff-provider-add.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Parse form data
        ProviderFormDTO formDTO = new ProviderFormDTO();
        formDTO.setName(request.getParameter("name"));
        formDTO.setContactInfo(request.getParameter("contactInfo"));
        formDTO.setStatus(Optional.ofNullable(request.getParameter("status")).orElse("ACTIVE"));

        // 2. Validate
        Map<String, String> errors = validateForm(formDTO, null);

        if (!errors.isEmpty()) {
            request.setAttribute("formDTO", formDTO);
            request.setAttribute("errors", errors);

            request.setAttribute("pageTitle", "Thêm nhà cung cấp");
            request.setAttribute("active", "provider");
            request.setAttribute("contentPage", "staff-provider-add.jsp");
            request.getRequestDispatcher("/staff.jsp").forward(request, response);
            return;
        }

        // 3. Create provider
        Provider provider = new Provider();
        provider.setName(formDTO.getName().trim());
        provider.setContactInfo(formDTO.getContactInfo());
        provider.setStatus(formDTO.getStatus());

        ProviderDAO dao = new ProviderDAO();
        long newId = dao.insert(provider);

        if (newId > 0) {
            request.getSession().setAttribute("successMessage", "Thêm nhà cung cấp thành công!");
            response.sendRedirect(request.getContextPath() + "/staff/provider");
        } else {
            request.setAttribute("errorMessage", "Không thể thêm nhà cung cấp. Vui lòng thử lại.");
            doGet(request, response);
        }
    }

    private Map<String, String> validateForm(ProviderFormDTO dto, Long excludeId) {
        Map<String, String> errors = new HashMap<>();

        // Name validation
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            errors.put("name", "Tên nhà cung cấp không được để trống");
        } else if (dto.getName().trim().length() < 2) {
            errors.put("name", "Tên nhà cung cấp phải có ít nhất 2 ký tự");
        } else if (dto.getName().trim().length() > 100) {
            errors.put("name", "Tên nhà cung cấp không được vượt quá 100 ký tự");
        } else {
            ProviderDAO dao = new ProviderDAO();
            if (dao.isNameExists(dto.getName().trim(), excludeId)) {
                errors.put("name", "Tên nhà cung cấp đã tồn tại");
            }
        }

        // Contact info validation (optional but max length)
        if (dto.getContactInfo() != null && dto.getContactInfo().length() > 255) {
            errors.put("contactInfo", "Thông tin liên hệ không được vượt quá 255 ký tự");
        }

        // Status validation
        if (dto.getStatus() == null || dto.getStatus().isEmpty()) {
            errors.put("status", "Vui lòng chọn trạng thái");
        } else if (!dto.getStatus().equals("ACTIVE") && !dto.getStatus().equals("INACTIVE")) {
            errors.put("status", "Trạng thái không hợp lệ");
        }

        return errors;
    }
}
