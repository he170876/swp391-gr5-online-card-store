<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Order" %>
<%@ page import="model.User" %>
<%@ page import="model.CardInfo" %>
<%@ page import="util.OrderStatus" %>

<%
    Order order = (Order) request.getAttribute("order");
    User customer = (User) request.getAttribute("customer");
    CardInfo card = (CardInfo) request.getAttribute("card");

    java.time.format.DateTimeFormatter dtf =
        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    java.time.format.DateTimeFormatter dateFmt =
        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

    String createdAtStr = (order != null && order.getCreatedAt() != null)
            ? order.getCreatedAt().format(dtf) : "";

    String statusStr = (order != null)
            ? OrderStatus.getDisplayName(order.getStatus()) : "";

    String expiryStr = (card != null && card.getExpiryDate() != null)
            ? card.getExpiryDate().format(dateFmt) : "N/A";

    String serialStr = (card != null && card.getSerial() != null && !card.getSerial().isEmpty())
            ? card.getSerial() : "N/A";
%>


<h2 class="mb-4 fw-bold">Chi tiết đơn hàng</h2>

<% if (order == null) { %>
<div class="alert alert-warning">Không tìm thấy đơn hàng.</div>
<% } else { %>

<div class="row mb-3">
    <div class="col-md-12 text-end">
        <a
            class="btn btn-outline-secondary"
            href="<%= request.getContextPath() %>/staff/order"
            >
            <i class="fa fa-arrow-left"></i> Quay lại danh sách
        </a>
    </div>
</div>

<div class="row mb-3">
    <div class="col-lg-6">
        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">Thông tin đơn hàng</h5>
            </div>
            <div class="card-body">
                <p><strong>ID:</strong> <%= order.getId() %></p>
                <p><strong>Ngày tạo:</strong> <%= createdAtStr %></p>
                <p>
                    <strong>Trạng thái:</strong>
                    <span class="badge bg-primary"><%= statusStr %></span>
                </p>
            </div>
        </div>
    </div>
    <div class="col-lg-6">
        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">Khách hàng</h5>
            </div>
            <div class="card-body">
                <p><strong>ID khách hàng:</strong> <%= order.getUserId() %></p>
                <p>
                    <strong>Tên:</strong> <%= customer != null ? customer.getFullName() :
          "" %>
                </p>
                <p>
                    <strong>Email:</strong> <%= customer != null ? customer.getEmail() :
          "" %>
                </p>
                <p><strong>Email nhận thẻ:</strong> <%= order.getReceiverEmail() %></p>
            </div>
        </div>
    </div>
</div>

<div class="row mb-3">
    <div class="col-lg-6">
        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">Giá cả</h5>
            </div>
            <div class="card-body">
                <p>
                    <strong>Giá gốc:</strong> <%= String.format("%.2f",
          order.getOriginalPrice()) %>
                </p>
                <p>
                    <strong>Giảm giá %:</strong> <%= String.format("%.2f",
          order.getDiscountPercent()) %>
                </p>
                <p>
                    <strong>Giá cuối:</strong>
                    <span class="text-danger fw-bold"
                          ><%= String.format("%.2f", order.getFinalPrice()) %></span
                    >
                </p>
            </div>
        </div>
    </div>

    <div class="col-lg-6">
        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">Thẻ</h5>
            </div>
            <div class="card-body">
                <% if (card != null) { %>
                <p><strong>ID thẻ:</strong> <%= card.getId() %></p>
                <p><strong>Mã thẻ:</strong> <code><%= card.getCode() %></code></p>
                <p><strong>Serial:</strong> <code><%= serialStr %></code></p>
                <p><strong>Ngày hết hạn:</strong> <%= expiryStr %></p>
                <p><strong>Trạng thái:</strong>
                    <%
                        String status = card.getStatus();
                        if ("AVAILABLE".equals(status)) {
                            out.print("Còn hiệu lực");
                        } else if ("SOLD".equals(status)) {
                            out.print("Đã bán");
                        } else if ("EXPIRED".equals(status)) {
                            out.print("Hết hạn");
                        } else if ("INACTIVE".equals(status)) {
                            out.print("Ngừng hoạt động");
                        } else {
                            out.print("Không xác định");
                        }
                    %>
                </p>

                <% } else if (order.getCardInfoId() > 0) { %>
                <p class="text-warning fw-bold">Thẻ chưa được gán.</p>
                <% } else { %>
                <p class="text-muted">Không có thẻ được gán.</p>
                <% } %>
            </div>
        </div>
    </div>
</div>

<div class="card">
    <div class="card-header">
        <h5 class="card-title mb-0">Thao tác</h5>
    </div>
    <div class="card-body">
        <div class="row gy-2 align-items-end">
            <div class="col-md-4">
                <label class="form-label">Cập nhật trạng thái</label>
                <select id="statusSelect" class="form-select">
                    <option value="">Chọn trạng thái</option>
                    <option value="PENDING">Đang chờ</option>
                    <option value="PAID">Đã thanh toán</option>
                    <option value="COMPLETED">Hoàn thành</option>
                    <option value="CANCELED">Đã hủy</option>
                    <option value="REFUNDED">Đã hoàn tiền</option>
                </select>
            </div>
            <div class="col-md-3">
                <button
                    class="btn btn-primary w-100"
                    onclick="updateStatus(<%= order.getId() %>, '<%= order.getStatus() %>')"
                    >
                    Cập nhật
                </button>
            </div>
            <div class="col-md-5 text-md-end">
                <% if (card != null && OrderStatus.canResendCard(order.getStatus())) {
                %>
                <button
                    class="btn btn-outline-secondary"
                    onclick="resendCard(<%= order.getId() %>)"
                    >
                    Gửi lại mã thẻ
                </button>
                <% } %>
            </div>
        </div>
    </div>
</div>
<% } %>


