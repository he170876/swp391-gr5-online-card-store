<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ page
import="model.Order" %> <%@ page import="model.User" %> <%@ page
import="model.CardInfo" %> <%@ page import="util.OrderStatus" %> <% String ctx =
request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8" />
    <meta
      name="viewport"
      content="width=device-width, initial-scale=1.0, user-scalable=0"
    />
    <title>Order Detail - Staff</title>
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
    <script>
      function updateStatus(orderId, currentStatus) {
        const newStatus = document.getElementById("statusSelect").value;
        if (!newStatus) {
          alert("Please select a new status");
          return;
        }
        if (newStatus === currentStatus) {
          alert("Please select a different status");
          return;
        }
        if (!confirm("Update order status to " + newStatus + "?")) {
          return;
        }
        fetch("<%= request.getContextPath() %>/staff/update-order-status", {
          method: "POST",
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
          body: "orderId=" + orderId + "&status=" + newStatus,
        })
          .then((r) => r.json())
          .then((data) => {
            if (data.success) {
              alert("Status updated");
              location.reload();
            } else {
              alert("Error: " + (data.error || "Unknown error"));
            }
          })
          .catch((e) => alert("Error: " + e));
      }
      function resendCard(orderId) {
        if (!confirm("Resend card code to customer?")) {
          return;
        }
        fetch("<%= request.getContextPath() %>/staff/resend-card", {
          method: "POST",
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
          body: "orderId=" + orderId,
        })
          .then((r) => r.json())
          .then((data) => {
            if (data.success) {
              alert("Card code resent successfully");
              location.reload();
            } else {
              alert("Error: " + (data.error || "Unknown error"));
            }
          })
          .catch((e) => alert("Error: " + e));
      }
    </script>
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
          <li class="nav-item">
            <div class="top-nav-search">
              <a href="javascript:void(0);" class="responsive-search"
                ><i class="fa fa-search"></i
              ></a>
              <form action="#">
                <div class="searchinputs">
                  <input type="text" placeholder="Search here ..." />
                  <div class="search-addon">
                    <span
                      ><img
                        src="<%= ctx %>/assets/img/icons/closes.svg"
                        alt="img"
                    /></span>
                  </div>
                </div>
                <a class="btn" id="searchdiv"
                  ><img src="<%= ctx %>/assets/img/icons/search.svg" alt="img"
                /></a>
              </form>
            </div>
          </li>

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
                    <h5>Orders</h5>
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
                <a href="<%= ctx %>/staff.jsp"
                  ><img
                    src="<%= ctx %>/assets/img/icons/dashboard.svg"
                    alt="img"
                  /><span>Dashboard</span></a
                >
              </li>
              <li class="active">
                <a href="<%= ctx %>/staff/orders"
                  ><img
                    src="<%= ctx %>/assets/img/icons/sales1.svg"
                    alt="img"
                  /><span>Orders</span></a
                >
              </li>
              <li>
                <a href="<%= ctx %>/staff/cards"
                  ><img
                    src="<%= ctx %>/assets/img/icons/product.svg"
                    alt="img"
                  /><span>Card Info</span></a
                >
              </li>
              <li>
                <a href="<%= ctx %>/staff/cards-import"
                  ><img
                    src="<%= ctx %>/assets/img/icons/product.svg"
                    alt="img"
                  /><span>Import Cards</span></a
                >
              </li>
            </ul>
          </div>
        </div>
      </div>

      <div class="page-wrapper">
        <div class="content container-fluid">
          <div class="page-header">
            <div class="row align-items-center">
              <div class="col-md-6">
                <h3 class="page-title">Order Detail</h3>
                <ul class="breadcrumb">
                  <li class="breadcrumb-item">
                    <a href="<%= ctx %>/staff.jsp">Home</a>
                  </li>
                  <li class="breadcrumb-item">
                    <a href="<%= ctx %>/staff/orders">Orders</a>
                  </li>
                  <li class="breadcrumb-item active">Detail</li>
                </ul>
              </div>
              <div class="col-md-6 text-md-end">
                <a
                  class="btn btn-outline-secondary"
                  href="<%= request.getContextPath() %>/staff/orders"
                  >Back to list</a
                >
              </div>
            </div>
          </div>

          <% String error = (String) request.getAttribute("error"); if (error !=
          null && !error.isEmpty()) { %>
          <div class="alert alert-danger" role="alert"><%= error %></div>
          <% } Order order = (Order) request.getAttribute("order"); User
          customer = (User) request.getAttribute("customer"); CardInfo card =
          (CardInfo) request.getAttribute("card"); %> <% if (order != null) { %>
          <div class="row">
            <div class="col-lg-6">
              <div class="card">
                <div class="card-header">
                  <h5 class="card-title mb-0">Order Info</h5>
                </div>
                <div class="card-body">
                  <p><strong>ID:</strong> <%= order.getId() %></p>
                  <p>
                    <strong>Created:</strong> <%= order.getCreatedAt() != null ? order.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "" %>
                  </p>
                  <p>
                    <strong>Status:</strong>
                    <span class="badge bg-primary"><%= OrderStatus.getDisplayName(order.getStatus()) %></span>
                  </p>
                </div>
              </div>
            </div>
            <div class="col-lg-6">
              <div class="card">
                <div class="card-header">
                  <h5 class="card-title mb-0">Customer</h5>
                </div>
                <div class="card-body">
                  <p><strong>Customer ID:</strong> <%= order.getUserId() %></p>
                  <p>
                    <strong>Name:</strong> <%= customer != null ?
                    customer.getFullName() : "" %>
                  </p>
                  <p>
                    <strong>Email:</strong> <%= customer != null ?
                    customer.getEmail() : "" %>
                  </p>
                  <p>
                    <strong>Receiver Email:</strong> <%=
                    order.getReceiverEmail() %>
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="col-lg-6">
              <div class="card">
                <div class="card-header">
                  <h5 class="card-title mb-0">Pricing</h5>
                </div>
                <div class="card-body">
                  <p>
                    <strong>Original:</strong> <%= String.format("%.2f",
                    order.getOriginalPrice()) %>
                  </p>
                  <p>
                    <strong>Discount %:</strong> <%= String.format("%.2f",
                    order.getDiscountPercent()) %>
                  </p>
                  <p>
                    <strong>Final:</strong>
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
                  <h5 class="card-title mb-0">Card</h5>
                </div>
                <div class="card-body">
                  <% if (card != null) { %>
                  <p><strong>Card ID:</strong> <%= card.getId() %></p>
                  <p>
                    <strong>Code:</strong> <code><%= card.getCode() %></code>
                  </p>
                  <p>
                    <strong>Serial:</strong>
                    <code
                      ><%= card.getSerial() != null &&
                      !card.getSerial().isEmpty() ? card.getSerial() : "N/A"
                      %></code
                    >
                  </p>
                  <p>
                    <strong>Expiry:</strong> <%= card.getExpiryDate() != null ?
                    card.getExpiryDate() : "N/A" %>
                  </p>
                  <p><strong>Status:</strong> <%= card.getStatus() %></p>
                  <% } else if (order.getCardInfoId() > 0) { %>
                  <p class="text-warning fw-bold">Card not yet assigned.</p>
                  <% } else { %>
                  <p class="text-muted">No card assigned.</p>
                  <% } %>
                </div>
              </div>
            </div>
          </div>

          <div class="card">
            <div class="card-header">
              <h5 class="card-title mb-0">Actions</h5>
            </div>
            <div class="card-body">
              <div class="row gy-2 align-items-end">
                <div class="col-md-4">
                  <label class="form-label">Update status</label>
                  <select id="statusSelect" class="form-select">
                    <option value="">Select</option>
                    <option value="PENDING">PENDING</option>
                    <option value="PAID">PAID</option>
                    <option value="COMPLETED">COMPLETED</option>
                    <option value="CANCELED">CANCELED</option>
                    <option value="REFUNDED">REFUNDED</option>
                  </select>
                </div>
                <div class="col-md-3">
                  <button
                    class="btn btn-primary w-100"
                    onclick="updateStatus(<%= order.getId() %>, '<%= order.getStatus() %>')"
                  >
                    Update
                  </button>
                </div>
                <div class="col-md-5 text-md-end">
                  <% if (card != null &&
                  util.OrderStatus.canResendCard(order.getStatus())) { %>
                  <button
                    class="btn btn-outline-secondary"
                    onclick="resendCard(<%= order.getId() %>)"
                  >
                    Resend Card Code
                  </button>
                  <% } %>
                </div>
              </div>
            </div>
          </div>

          <% } else { %>
          <div class="alert alert-warning">Order not found.</div>
          <% } %>
        </div>
      </div>
    </div>

    <script src="<%= ctx %>/assets/js/jquery-3.6.0.min.js"></script>
    <script src="<%= ctx %>/assets/js/feather.min.js"></script>
    <script src="<%= ctx %>/assets/js/jquery.slimscroll.min.js"></script>
    <script src="<%= ctx %>/assets/js/jquery.dataTables.min.js"></script>
    <script src="<%= ctx %>/assets/js/dataTables.bootstrap4.min.js"></script>
    <script src="<%= ctx %>/assets/js/bootstrap.bundle.min.js"></script>
    <script src="<%= ctx %>/assets/plugins/select2/js/select2.min.js"></script>
    <script src="<%= ctx %>/assets/js/moment.min.js"></script>
    <script src="<%= ctx %>/assets/js/bootstrap-datetimepicker.min.js"></script>
    <script src="<%= ctx %>/assets/plugins/sweetalert/sweetalert2.all.min.js"></script>
    <script src="<%= ctx %>/assets/plugins/sweetalert/sweetalerts.min.js"></script>
    <script src="<%= ctx %>/assets/js/script.js"></script>
    <script>
      window.addEventListener("load", function () {
        var loader = document.getElementById("global-loader");
        if (loader) {
          loader.style.display = "none";
        }
      });
    </script>
  </body>
</html>
