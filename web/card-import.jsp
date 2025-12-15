<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="service.CardCodeImportService.ImportResult" %>
<%@ page import="service.CardCodeImportService.ImportError" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Product" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8" />
    <meta
      name="viewport"
      content="width=device-width, initial-scale=1.0, user-scalable=0"
    />
    <title>Import Card Codes</title>
    <link
      rel="shortcut icon"
      type="image/x-icon"
      href="<%= ctx %>/assets/img/favicon.jpg"
    />
    <link rel="stylesheet" href="<%= ctx %>/assets/css/bootstrap.min.css" />
    <link
      rel="stylesheet"
      href="<%= ctx %>/assets/css/bootstrap-datetimepicker.min.css"
    />
    <link rel="stylesheet" href="<%= ctx %>/assets/css/animate.css" />
    <link
      rel="stylesheet"
      href="<%= ctx %>/assets/css/dataTables.bootstrap4.min.css"
    />
    <link
      rel="stylesheet"
      href="<%= ctx %>/assets/plugins/select2/css/select2.min.css"
    />
    <link
      rel="stylesheet"
      href="<%= ctx %>/assets/plugins/fontawesome/css/fontawesome.min.css"
    />
    <link
      rel="stylesheet"
      href="<%= ctx %>/assets/plugins/fontawesome/css/all.min.css"
    />
    <link rel="stylesheet" href="<%= ctx %>/assets/css/style.css" />
  </head>
  <body>
    <div id="global-loader"><div class="whirly-loader"></div></div>
    <div class="main-wrapper">
      <div class="header">
        <div class="header-left active">
          <a href="<%= ctx %>/staff.jsp" class="logo"
            ><img src="<%= ctx %>/assets/img/logo.png" alt="logo"
          /></a>
          <a href="<%= ctx %>/staff.jsp" class="logo-small"
            ><img src="<%= ctx %>/assets/img/logo-small.png" alt="logo"
          /></a>
          <a id="toggle_btn" href="javascript:void(0);"></a>
        </div>

        <a id="mobile_btn" class="mobile_btn" href="#sidebar">
          <span class="bar-icon"><span></span><span></span><span></span></span>
        </a>

        <ul class="nav user-menu">
          <li class="nav-item dropdown has-arrow main-drop">
            <a
              href="javascript:void(0);"
              class="dropdown-toggle nav-link userset"
              data-bs-toggle="dropdown"
            >
              <span class="user-img"
                ><img
                  src="<%= ctx %>/assets/img/profiles/avator1.jpg"
                  alt="" /><span class="status online"></span
              ></span>
            </a>
            <div class="dropdown-menu menu-drop-user">
              <div class="profilename">
                <div class="profileset">
                  <span class="user-img"
                    ><img
                      src="<%= ctx %>/assets/img/profiles/avator1.jpg"
                      alt="" /><span class="status online"></span
                  ></span>
                  <div class="profilesets">
                    <h6>Staff</h6>
                    <h5>Import Cards</h5>
                  </div>
                </div>
                <hr class="m-0" />
                <a class="dropdown-item logout pb-0" href="<%= ctx %>/logout"
                  ><img
                    src="<%= ctx %>/assets/img/icons/log-out.svg"
                    class="me-2"
                    alt="img"
                  />Logout</a
                >
              </div>
            </div>
          </li>
        </ul>

        <div class="dropdown mobile-user-menu">
          <a
            href="javascript:void(0);"
            class="nav-link dropdown-toggle"
            data-bs-toggle="dropdown"
            aria-expanded="false"
            ><i class="fa fa-ellipsis-v"></i
          ></a>
          <div class="dropdown-menu dropdown-menu-right">
            <a class="dropdown-item" href="<%= ctx %>/logout">Logout</a>
          </div>
        </div>
      </div>

      <div class="sidebar" id="sidebar">
        <div class="sidebar-inner slimscroll">
          <div id="sidebar-menu" class="sidebar-menu">
            <ul>
              <li>
                <a href="<%= ctx %>/staff/cards"
                  ><img
                    src="<%= ctx %>/assets/img/icons/product.svg"
                    alt="img"
                  /><span>Card Info</span></a
                >
              </li>
              <li>
                <a href="<%= ctx %>/staff/orders"
                  ><img
                    src="<%= ctx %>/assets/img/icons/quotation1.svg"
                    alt="img"
                  /><span>Orders</span></a
                >
              </li>
              <li class="active">
                <a href="<%= ctx %>/staff/cards-import"
                  ><img
                    src="<%= ctx %>/assets/img/icons/product.svg"
                    alt="img"
                  /><span>Import Cards</span></a
                >
              </li>
              <li>
                <a href="<%= ctx %>/logout"
                  ><img
                    src="<%= ctx %>/assets/img/icons/log-out.svg"
                    alt="img"
                  /><span>Logout</span></a
                >
              </li>
            </ul>
          </div>
        </div>
      </div>

      <div class="page-wrapper">
        <div class="content container-fluid">
          <div class="page-header">
            <div class="page-title">
              <h4>Import Card Codes (CSV)</h4>
              <h6>Tải file, kiểm tra trùng và ngày hết hạn</h6>
            </div>
          </div>

          <% String error = (String) request.getAttribute("error"); %>
          <% ImportResult result = (ImportResult) request.getAttribute("result"); %>
          <% @SuppressWarnings("unchecked") List<model.Product> products = (List<model.Product>) request.getAttribute("products"); %>
          <% if (error != null) { %>
          <div class="alert alert-danger" role="alert"><%= error %></div>
          <% } %> <% if (result != null && result.getInsertedRows() > 0) { %>
          <div class="alert alert-success" role="alert">
            Đã import <%= result.getInsertedRows() %> card code.
          </div>
          <% } %>

          <div class="card mb-3">
            <div class="card-body">
              <form
                method="post"
                action="<%= ctx %>/staff/cards-import"
                enctype="multipart/form-data"
                class="row g-3"
              >
                <div class="col-md-3">
                  <label class="form-label">Product</label>
                  <select name="productId" required class="form-select">
                    <option value="">-- Chọn sản phẩm --</option>
                    <% if (products != null) { for (Product p : products) { %>
                      <option value="<%= p.getId() %>"><%= p.getName() %></option>
                    <% } } %>
                  </select>
                  <div class="form-text">
                    Áp dụng cho toàn bộ bản ghi trong file.
                  </div>
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
                    Định dạng: code,serial,expiry_date (yyyy-MM-dd). Dòng đầu có
                    thể là header.
                  </div>
                </div>
                <div class="col-md-2 align-self-end">
                  <button type="submit" class="btn btn-primary w-100">
                    Import
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
                <li>
                  Bỏ qua do đã tồn tại: <%= result.getSkippedExisting() %>
                </li>
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
        </div>
      </div>
    </div>

    <script src="<%= ctx %>/assets/js/jquery-3.6.0.min.js"></script>
    <script src="<%= ctx %>/assets/js/feather.min.js"></script>
    <script src="<%= ctx %>/assets/js/jquery.slimscroll.min.js"></script>
    <script src="<%= ctx %>/assets/js/bootstrap.bundle.min.js"></script>
    <script src="<%= ctx %>/assets/plugins/select2/js/select2.min.js"></script>
    <script src="<%= ctx %>/assets/js/script.js"></script>
  </body>
</html>
