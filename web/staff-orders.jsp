<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Order" %>
<%@ page import="util.OrderStatus" %>

<!-- Page Header -->
<div class="page-header d-flex justify-content-between align-items-center">
    <div>
        <h4>Danh sách đơn hàng</h4>
        <h6>Quản lý tất cả đơn hàng trong hệ thống</h6>
    </div>
    <a href="${pageContext.request.contextPath}/staff/order" class="btn btn-primary">
        <i class="fa fa-list me-2"></i>Quản trị đơn hàng
    </a>
</div>

<!-- Filter Card -->
<div class="filter-card">
    <form class="row g-3" method="get" action="${pageContext.request.contextPath}/staff/order">
        <div class="col-md-4">
            <label class="form-label">Trạng thái</label>
            <select class="form-select" name="status">
                <option value="">Tất cả</option>
                <option value="PENDING">Đang chờ</option>
                <option value="PAID">Đã thanh toán</option>
                <option value="COMPLETED">Hoàn thành</option>
                <option value="CANCELED">Đã hủy</option>
                <option value="REFUNDED">Đã hoàn tiền</option>
            </select>
        </div>
        <div class="col-md-4">
            <label class="form-label">Sắp xếp theo</label>
            <select class="form-select" name="sort">
                <option value="date">Ngày (mới nhất)</option>
                <option value="amount">Số tiền (cao nhất)</option>
            </select>
        </div>
        <div class="col-md-4 d-flex align-items-end">
            <button type="submit" class="btn btn-primary">Áp dụng</button>
            <a href="${pageContext.request.contextPath}/staff/order" class="btn btn-outline-secondary ms-2">Đặt lại</a>
        </div>
    </form>
    
    <c:if test="${totalRecords > 0}">
        <div class="mt-3 text-muted">
            Tổng: ${totalRecords} đơn hàng
        </div>
    </c:if>
    
    <c:if test="${totalRecords == 0}">
        <div class="mt-3 text-muted">Không tìm thấy đơn hàng nào</div>
    </c:if>
</div>

<!-- Orders Table -->
<div class="card">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover">
                <thead>
                <tr>
                    <th width="8%">#</th>
                    <th width="15%">Khách hàng</th>
                    <th width="15%">Ngày tạo</th>
                    <th width="12%">Giá gốc</th>
                    <th width="10%">Giảm giá %</th>
                    <th width="12%">Giá cuối</th>
                    <th width="12%">Trạng thái</th>
                    <th width="16%">Hành động</th>
                </tr>
                </thead>
                <tbody>
                <%
                    List<Order> orders = (List<Order>) request.getAttribute("orders");
                    if (orders == null) { orders = new java.util.ArrayList<>(); }
                    Integer cpObj = (Integer) request.getAttribute("currentPage");
                    Integer psObj = (Integer) request.getAttribute("pageSize");
                    int currentPage = cpObj == null ? 1 : cpObj.intValue();
                    int pageSize = psObj == null ? 10 : psObj.intValue();
                    int idx = 0;
                    if (orders.isEmpty()) {
                %>
                <tr>
                    <td colspan="8" class="text-center py-4">
                        <i class="fa fa-inbox fa-3x text-muted mb-3 d-block"></i>
                        Không tìm thấy đơn hàng nào
                    </td>
                </tr>
                <%
                    } else {
                        for (Order order : orders) {
                            String statusClass = "badge bg-secondary";
                            switch (order.getStatus()) {
                                case "PENDING": statusClass = "badge bg-warning text-dark"; break;
                                case "PAID": statusClass = "badge bg-info text-dark"; break;
                                case "COMPLETED": statusClass = "badge bg-success"; break;
                                case "CANCELED": statusClass = "badge bg-danger"; break;
                                case "REFUNDED": statusClass = "badge bg-primary"; break;
                                default: break;
                            }
                %>
                <tr>
                    <td><%= (currentPage - 1) * pageSize + (++idx) %></td>
                    <td>#<%= order.getUserId() %></td>
                    <td><%= order.getCreatedAt() != null ? order.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "" %></td>
                    <td><%= String.format("%.2f", order.getOriginalPrice()) %></td>
                    <td><%= String.format("%.2f", order.getDiscountPercent()) %>%</td>
                    <td><%= String.format("%.2f", order.getFinalPrice()) %></td>
                    <td><span class="<%= statusClass %>"><%= OrderStatus.getDisplayName(order.getStatus()) %></span></td>
                    <td>
                        <div class="btn-group" role="group">
                            <a class="btn btn-sm btn-outline-info action-btn" href="${pageContext.request.contextPath}/staff/order-detail?id=<%= order.getId() %>">
                                <i class="fa fa-eye"></i>
                            </a>
                        </div>
                    </td>
                </tr>
                <%
                        }
                    }
                %>
                </tbody>
            </table>
        </div>

        <!-- Pagination -->
        <c:if test="${totalPages > 1}">
            <nav aria-label="Page navigation" class="mt-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div class="text-muted">
                        Hiển thị ${(currentPage - 1) * pageSize + 1} - ${currentPage * pageSize > totalRecords ? totalRecords : currentPage * pageSize} / ${totalRecords} đơn hàng
                    </div>
                    <ul class="pagination mb-0">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/staff/order?page=${currentPage - 1}">
                                <i class="fa fa-chevron-left"></i>
                            </a>
                        </li>
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <c:if test="${i <= 5 || i > totalPages - 2 || (i >= currentPage - 1 && i <= currentPage + 1)}">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/staff/order?page=${i}">${i}</a>
                                </li>
                            </c:if>
                        </c:forEach>
                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/staff/order?page=${currentPage + 1}">
                                <i class="fa fa-chevron-right"></i>
                            </a>
                        </li>
                    </ul>
                </div>
            </nav>
        </c:if>
    </div>
</div>
