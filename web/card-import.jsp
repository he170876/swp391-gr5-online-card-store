<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ page
import="service.CardCodeImportService.ImportResult" %> <%@ page
import="service.CardCodeImportService.ImportError" %> <%@ page
import="java.util.List" %> <%@ page import="model.Product" %> <% String ctx =
request.getContextPath(); %>

<h2 class="mb-4 fw-bold">Nhập mã thẻ (CSV)</h2>

<% String error = (String) request.getAttribute("error"); %> <% ImportResult
result = (ImportResult) request.getAttribute("result"); %> <%
@SuppressWarnings("unchecked") List<model.Product>
  products = (List<model.Product
    >) request.getAttribute("products"); %> <% if (error != null) { %>
    <div class="alert alert-danger" role="alert"><%= error %></div>
    <% } %> <% if (result != null && result.getInsertedRows() > 0) { %>
    <div class="alert alert-success" role="alert">
      Đã nhập <%= result.getInsertedRows() %> mã thẻ.
    </div>
    <% } %>

    <div class="card mb-3">
      <div class="card-body">
        <form
          method="post"
          action="<%= ctx %>/staff/card/import"
          enctype="multipart/form-data"
          class="row g-3"
        >
          <div class="col-md-3">
            <label class="form-label">Sản phẩm</label>
            <select name="productId" required class="form-select">
              <option value="">-- Chọn sản phẩm --</option>
              <% if (products != null) { for (Product p : products) { %>
              <option value="<%= p.getId() %>"><%= p.getName() %></option>
              <% } } %>
            </select>
            <div class="form-text">Áp dụng cho toàn bộ bản ghi trong file.</div>
          </div>
          <div class="col-md-4">
            <label class="form-label">File CSV</label>
            <input
              type="file"
              name="file"
              accept=".csv"
              required
              class="form-control"
            />
            <div class="form-text">
              Định dạng: code,serial,expiry_date (yyyy-MM-dd). Dòng đầu có thể
              là header.
            </div>
          </div>
          <div class="col-md-2 align-self-end">
            <button type="submit" class="btn btn-primary w-100">
              <i class="fa fa-upload"></i> Nhập
            </button>
          </div>
        </form>
      </div>
    </div>

    <% if (result != null) { %>
    <div class="card">
      <div class="card-body">
        <h5>Kết quả</h5>
        <ul>
          <li>Tổng dòng đọc: <%= result.getTotalRows() %></li>
          <li>Dòng hợp lệ: <%= result.getValidRows() %></li>
          <li>Đã chèn: <%= result.getInsertedRows() %></li>
          <li>Bỏ qua do đã tồn tại: <%= result.getSkippedExisting() %></li>
        </ul>
        <% if (!result.getErrors().isEmpty()) { %>
        <div class="table-responsive">
          <table class="table table-bordered">
            <thead>
              <tr>
                <th>Dòng</th>
                <th>Lỗi</th>
              </tr>
            </thead>
            <tbody>
              <% for (ImportError err : result.getErrors()) { %>
              <tr>
                <td><%= err.getRow() %></td>
                <td><%= err.getMessage() %></td>
              </tr>
              <% } %>
            </tbody>
          </table>
        </div>
        <% } %>
      </div>
    </div>
    <% } %>
  </model.Product></model.Product
>
