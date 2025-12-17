<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.CardInfo" %>
<%@ page import="model.Product" %>
<%@ page import="util.CardInfoStatus" %>
<%
    String ctx = request.getContextPath();
    CardInfo card = (CardInfo) request.getAttribute("card");
    List<Product> products = (List<Product>) request.getAttribute("products");
    List<String> statuses = (List<String>) request.getAttribute("statuses");
    String error = (String) request.getAttribute("error");
%>

<h2 class="mb-4 fw-bold">Sửa thông tin thẻ</h2>

<div class="row mb-3">
    <div class="col-md-12 text-end">
        <a class="btn btn-outline-secondary" href="<%= ctx %>/staff/card">
            <i class="fa fa-arrow-left"></i> Quay lại danh sách
        </a>
    </div>
</div>

<% if (error != null && !error.isEmpty()) { %>
    <div class="alert alert-danger" role="alert"><%= error %></div>
<% } %>

<% if (card != null) { %>
<div class="card">
    <div class="card-body">
        <form method="post" action="<%= ctx %>/staff/card/edit/<%= card.getId() %>">
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label">Mã thẻ <span class="text-danger">*</span></label>
                    <input type="text" name="code" value="<%= card.getCode() %>" required class="form-control" />
                </div>
                <div class="col-md-6 mb-3">
                    <label class="form-label">Serial <span class="text-danger">*</span></label>
                    <input type="text" name="serial" value="<%= card.getSerial() %>" required class="form-control" />
                </div>
            </div>

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label">Sản phẩm <span class="text-danger">*</span></label>
                    <select name="productId" required class="form-select">
                        <% if (products != null) { for (Product p : products) { %>
                            <option value="<%= p.getId() %>" <%= (card.getProductId() == p.getId()) ? "selected" : "" %>><%= p.getName() %></option>
                        <% } } %>
                    </select>
                </div>
                <div class="col-md-6 mb-3">
                    <label class="form-label">Ngày hết hạn</label>
                    <input type="date" name="expiryDate" value="<%= card.getExpiryDate() == null ? "" : card.getExpiryDate().toString() %>" class="form-control" />
                </div>
            </div>

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label">Trạng thái <span class="text-danger">*</span></label>
                    <select name="status" required class="form-select">
                        <% if (statuses != null) { for (String s : statuses) { %>
                            <option value="<%= s %>" <%= s.equals(card.getStatus()) ? "selected" : "" %>><%= CardInfoStatus.getDisplayName(s) %></option>
                        <% } } %>
                    </select>
                </div>
            </div>

            <div class="row">
                <div class="col-12">
                    <button type="submit" class="btn btn-primary me-2">
                        <i class="fa fa-save"></i> Lưu thay đổi
                    </button>
                    <a href="<%= ctx %>/staff/card" class="btn btn-secondary">
                        <i class="fa fa-times"></i> Hủy
                    </a>
                </div>
            </div>
        </form>
    </div>
</div>
<% } else { %>
    <div class="alert alert-warning">Không tìm thấy thẻ.</div>
<% } %>
