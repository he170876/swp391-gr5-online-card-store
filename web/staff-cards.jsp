<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.CardInfoDAO.CardInfoListView" %>
<%@ page import="model.Product" %>
<%@ page import="model.Provider" %>
<%@ page import="util.CardInfoStatus" %>
<%
    List<CardInfoListView> cards = (List<CardInfoListView>) request.getAttribute("cards");
    List<String> statuses = (List<String>) request.getAttribute("statuses");
    List<Product> products = (List<Product>) request.getAttribute("products");
    java.util.Collection<Provider> providers = (java.util.Collection<Provider>) request.getAttribute("providers");
    String selectedStatus = (String) request.getAttribute("selectedStatus");
    Long selectedProductId = (Long) request.getAttribute("selectedProductId");
    Long selectedProviderId = (Long) request.getAttribute("selectedProviderId");
    java.time.LocalDate expiryFrom = (java.time.LocalDate) request.getAttribute("expiryFrom");
    java.time.LocalDate expiryTo = (java.time.LocalDate) request.getAttribute("expiryTo");
    String sort = (String) request.getAttribute("sort");
%>

<!-- Page Header -->
<div class="page-header d-flex justify-content-between align-items-center">
    <div>
        <h4>Danh sách thẻ</h4>
        <h6>Quản lý thông tin thẻ trong hệ thống</h6>
    </div>
    <a href="${pageContext.request.contextPath}/staff/card/import" class="btn btn-primary">
        <i class="fa fa-file-import me-2"></i>Nhập thẻ
    </a>


</div>
<c:if test="${not empty param.success}">
    <div class="alert alert-success" role="alert">${param.success}</div>
</c:if>
<c:if test="${not empty param.error}">
    <div class="alert alert-danger" role="alert">${param.error}</div>
</c:if>
<!-- Filter Card -->
<div class="filter-card">
    <form class="row g-3" method="get" action="${pageContext.request.contextPath}/staff/card">
        <div class="col-md-2">
            <label class="form-label">Trạng thái</label>
            <select name="status" class="form-select">
                <option value="">Tất cả</option>
                <% for (String s : statuses) { %>
                <option value="<%= s %>" <%= s.equals(selectedStatus) ? "selected" : "" %>><%= CardInfoStatus.getDisplayName(s) %></option>
                <% } %>
            </select>
        </div>
        <div class="col-md-3">
            <label class="form-label">Sản phẩm</label>
            <select name="productId" class="form-select">
                <option value="">Tất cả</option>
                <% for (Product p : products) { %>
                <option value="<%= p.getId() %>" <%= (selectedProductId != null && selectedProductId == p.getId()) ? "selected" : "" %>><%= p.getName() %></option>
                <% } %>
            </select>
        </div>
        <div class="col-md-3">
            <label class="form-label">Nhà cung cấp</label>
            <select name="providerId" class="form-select">
                <option value="">Tất cả</option>
                <% for (Provider pr : providers) { %>
                <option value="<%= pr.getId() %>" <%= (selectedProviderId != null && selectedProviderId == pr.getId()) ? "selected" : "" %>><%= pr.getName() %></option>
                <% } %>
            </select>
        </div>
        <div class="col-md-2">
            <label class="form-label">Hết hạn từ</label>
            <input type="date" name="expiryFrom" value="<%= expiryFrom == null ? "" : expiryFrom.toString() %>" class="form-control" />
        </div>
        <div class="col-md-2">
            <label class="form-label">Hết hạn đến</label>
            <input type="date" name="expiryTo" value="<%= expiryTo == null ? "" : expiryTo.toString() %>" class="form-control" />
        </div>
        <div class="col-md-2">
            <label class="form-label">Sắp xếp</label>
            <select name="sort" class="form-select">
                <option value="created_desc" <%= "created_desc".equals(sort) ? "selected" : "" %>>Mới nhất</option>
                <option value="expiry_asc" <%= "expiry_asc".equals(sort) ? "selected" : "" %>>Hết hạn tăng dần</option>
                <option value="expiry_desc" <%= "expiry_desc".equals(sort) ? "selected" : "" %>>Hết hạn giảm dần</option>
                <option value="status" <%= "status".equals(sort) ? "selected" : "" %>>Trạng thái</option>
                <option value="provider" <%= "provider".equals(sort) ? "selected" : "" %>>Nhà cung cấp</option>
                <option value="product" <%= "product".equals(sort) ? "selected" : "" %>>Sản phẩm</option>
            </select>
        </div>
        <div class="col-md-2 align-self-end">
            <button class="btn btn-primary" type="submit">Áp dụng</button>
            <a href="${pageContext.request.contextPath}/staff/card" class="btn btn-outline-secondary ms-2">Đặt lại</a>
        </div>
    </form>

    <c:if test="${totalRecords > 0}">
        <div class="mt-3 text-muted">
            Tổng: ${totalRecords} thẻ
        </div>
    </c:if>
    <c:if test="${totalRecords == 0}">
        <div class="mt-3 text-muted">Không tìm thấy thẻ nào</div>
    </c:if>
</div>

<!-- Cards Table -->
<div class="card">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th width="6%">#</th>
                        <th width="18%">Sản phẩm</th>
                        <th width="16%">Nhà cung cấp</th>
                        <th width="18%">Mã thẻ</th>
                        <th width="12%">Serial</th>
                        <th width="10%">Hết hạn</th>
                        <th width="10%">Trạng thái</th>
                        <th width="10%">Tạo lúc</th>
                        <th width="10%">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        if (cards == null) { cards = new java.util.ArrayList<>(); }
                        Integer cpObj = (Integer) request.getAttribute("currentPage");
                        Integer psObj = (Integer) request.getAttribute("pageSize");
                        int currentPage = cpObj == null ? 1 : cpObj.intValue();
                        int pageSize = psObj == null ? 10 : psObj.intValue();
                        int idx = 0;
                        if (cards.isEmpty()) { %>
                    <tr>
                        <td colspan="9" class="text-center py-4">
                            <i class="fa fa-inbox fa-3x text-muted mb-3 d-block"></i>
                            Không tìm thấy thẻ nào
                        </td>
                    </tr>
                    <% } else { for (CardInfoListView c : cards) { %>
                    <tr>
                        <td><%= (currentPage - 1) * pageSize + (++idx) %></td>
                        <td><%= c.getProductName() %></td>
                        <td><%= c.getProviderName() %></td>
                        <td><%= c.getCode() %></td>
                        <td><%= c.getSerial() %></td>
                        <td><%= c.getExpiryDate() == null ? "" : c.getExpiryDate() %></td>
                        <td>
                            <%
                                String status = c.getStatus();
                                String badgeClass = "bg-secondary";

                                if ("AVAILABLE".equals(status)) {
                                    badgeClass = "bg-success";   // xanh
                                } else if ("SOLD".equals(status)) {
                                    badgeClass = "bg-primary";   // xanh dương
                                } else if ("EXPIRED".equals(status)) {
                                    badgeClass = "bg-danger";    // đỏ
                                } else if ("INACTIVE".equals(status)) {
                                    badgeClass = "bg-dark";      // đen
                                }
                            %>
                            <span class="badge <%= badgeClass %>">
                                <%= CardInfoStatus.getDisplayName(status) %>
                            </span>
                        </td>


                        <td><%= c.getCreatedAt() == null ? "" : c.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")) %></td>
                        <td>
                            <div class="btn-group" role="group">
                                <a href="${pageContext.request.contextPath}/staff/card/edit/<%= c.getId() %>" class="btn btn-sm btn-outline-primary action-btn" title="Sửa">
                                    <i class="fa fa-edit"></i>
                                </a>
                                <form action="${pageContext.request.contextPath}/staff/card/delete/<%= c.getId() %>" method="POST" style="display:inline;" 
                                      onsubmit="return confirm('Bạn có chắc muốn xóa thẻ này? (Chỉ có thể xóa thẻ chưa được sử dụng)');">
                                    <button type="submit" class="btn btn-sm btn-outline-danger action-btn" title="Xóa">
                                        <i class="fa fa-trash"></i>
                                    </button>
                                </form>
                            </div>
                        </td>
                    </tr>
                    <% } } %>
                </tbody>
            </table>
        </div>

        <!-- Pagination -->
        <c:if test="${totalPages > 1}">
            <nav aria-label="Page navigation" class="mt-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div class="text-muted">
                        Hiển thị ${(currentPage - 1) * pageSize + 1} - ${currentPage * pageSize > totalRecords ? totalRecords : currentPage * pageSize} / ${totalRecords} thẻ
                    </div>
                    <ul class="pagination mb-0">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/staff/card?page=${currentPage - 1}">
                                <i class="fa fa-chevron-left"></i>
                            </a>
                        </li>
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <c:if test="${i <= 5 || i > totalPages - 2 || (i >= currentPage - 1 && i <= currentPage + 1)}">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/staff/card?page=${i}">${i}</a>
                                </li>
                            </c:if>
                        </c:forEach>
                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/staff/card?page=${currentPage + 1}">
                                <i class="fa fa-chevron-right"></i>
                            </a>
                        </li>
                    </ul>
                </div>
            </nav>
        </c:if>
    </div>
</div>
