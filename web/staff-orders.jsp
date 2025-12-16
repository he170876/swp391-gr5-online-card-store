<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Order" %>
<%@ page import="util.OrderStatus" %>
<%
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0">
    <title>Orders - Staff</title>
    <link rel="shortcut icon" type="image/x-icon" href="<%= ctx %>/assets/img/favicon.jpg">
    <link rel="stylesheet" href="<%= ctx %>/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="<%= ctx %>/assets/css/bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" href="<%= ctx %>/assets/css/animate.css">
    <link rel="stylesheet" href="<%= ctx %>/assets/css/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="<%= ctx %>/assets/plugins/select2/css/select2.min.css">
    <link rel="stylesheet" href="<%= ctx %>/assets/plugins/fontawesome/css/fontawesome.min.css">
    <link rel="stylesheet" href="<%= ctx %>/assets/plugins/fontawesome/css/all.min.css">
    <link rel="stylesheet" href="<%= ctx %>/assets/css/style.css">
</head>
<body>
<div id="global-loader"><div class="whirly-loader"></div></div>

<div class="main-wrapper">

    <div class="header">
        <div class="header-left active">
            <a href="<%= ctx %>/staff.jsp" class="logo"><img src="<%= ctx %>/assets/img/logo.png" alt="logo"></a>
            <a href="<%= ctx %>/staff.jsp" class="logo-small"><img src="<%= ctx %>/assets/img/logo-small.png" alt="logo"></a>
            <a id="toggle_btn" href="javascript:void(0);"></a>
        </div>

        <a id="mobile_btn" class="mobile_btn" href="#sidebar">
            <span class="bar-icon"><span></span><span></span><span></span></span>
        </a>

        <ul class="nav user-menu">
            <li class="nav-item">
                <div class="top-nav-search">
                    <a href="javascript:void(0);" class="responsive-search"><i class="fa fa-search"></i></a>
                    <form action="#">
                        <div class="searchinputs">
                            <input type="text" placeholder="Search here ...">
                            <div class="search-addon"><span><img src="<%= ctx %>/assets/img/icons/closes.svg" alt="img"></span></div>
                        </div>
                        <a class="btn" id="searchdiv"><img src="<%= ctx %>/assets/img/icons/search.svg" alt="img"></a>
                    </form>
                </div>
            </li>

            <li class="nav-item dropdown has-arrow main-drop">
                <a href="javascript:void(0);" class="dropdown-toggle nav-link userset" data-bs-toggle="dropdown">
                    <span class="user-img"><img src="<%= ctx %>/assets/img/profiles/avator1.jpg" alt=""><span class="status online"></span></span>
                </a>
                <div class="dropdown-menu menu-drop-user">
                    <div class="profilename">
                        <div class="profileset">
                            <span class="user-img"><img src="<%= ctx %>/assets/img/profiles/avator1.jpg" alt=""><span class="status online"></span></span>
                            <div class="profilesets">
                                <h6>Staff</h6>
                                <h5>Orders</h5>
                            </div>
                        </div>
                        <hr class="m-0">
                        <a class="dropdown-item logout pb-0" href="<%= ctx %>/logout"><img src="<%= ctx %>/assets/img/icons/log-out.svg" class="me-2" alt="img">Logout</a>
                    </div>
                </div>
            </li>
        </ul>

        <div class="dropdown mobile-user-menu">
            <a href="javascript:void(0);" class="nav-link dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false"><i class="fa fa-ellipsis-v"></i></a>
            <div class="dropdown-menu dropdown-menu-right">
                <a class="dropdown-item" href="<%= ctx %>/logout">Logout</a>
            </div>
        </div>
    </div>

    <div class="sidebar" id="sidebar">
        <div class="sidebar-inner slimscroll">
            <div id="sidebar-menu" class="sidebar-menu">
                <ul>
                    <li><a href="<%= ctx %>/staff.jsp"><img src="<%= ctx %>/assets/img/icons/dashboard.svg" alt="img"><span>Dashboard</span></a></li>
                    <li class="active"><a href="<%= ctx %>/staff/orders"><img src="<%= ctx %>/assets/img/icons/sales1.svg" alt="img"><span>Orders</span></a></li>
                    <li><a href="<%= ctx %>/staff/cards"><img src="<%= ctx %>/assets/img/icons/product.svg" alt="img"><span>Card Info</span></a></li>
                    <li><a href="<%= ctx %>/staff/cards-import"><img src="<%= ctx %>/assets/img/icons/product.svg" alt="img"><span>Import Cards</span></a></li>
                </ul>
            </div>
        </div>
    </div>

    <div class="page-wrapper">
        <div class="content container-fluid">
            <div class="page-header">
                <div class="row align-items-center">
                    <div class="col-md-6">
                        <h3 class="page-title">Order Management</h3>
                        <ul class="breadcrumb">
                            <li class="breadcrumb-item"><a href="<%= ctx %>/staff.jsp">Home</a></li>
                            <li class="breadcrumb-item active">Orders</li>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="card mb-3">
                <div class="card-body">
                    <form class="row g-3" method="get" action="<%= request.getContextPath() %>/staff/orders">
                        <div class="col-md-4">
                            <label class="form-label">Filter status</label>
                            <select class="select form-select" name="status">
                                <option value="">All</option>
                                <option value="PENDING">Pending</option>
                                <option value="PAID">Paid</option>
                                <option value="COMPLETED">Completed</option>
                                <option value="CANCELED">Canceled</option>
                                <option value="REFUNDED">Refunded</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">Sort by</label>
                            <select class="select form-select" name="sort">
                                <option value="date">Date (newest)</option>
                                <option value="amount">Amount (highest)</option>
                            </select>
                        </div>
                        <div class="col-md-4 d-flex align-items-end">
                            <button type="submit" class="btn btn-primary w-100">Apply</button>
                        </div>
                    </form>
                </div>
            </div>

            <div class="card">
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table datatable">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Customer</th>
                                <th>Created</th>
                                <th>Original</th>
                                <th>Discount %</th>
                                <th>Final</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                            </thead>
                            <tbody>
                            <%
                                List<Order> orders = (List<Order>) request.getAttribute("orders");
                                if (orders == null) { orders = new java.util.ArrayList<>(); }
                                if (orders.isEmpty()) {
                            %>
                            <tr><td colspan="8" class="text-center">No orders found</td></tr>
                            <%
                                } else {
                                    for (Order order : orders) {
                                        String statusClass = "badge bg-secondary";
                                        switch (order.getStatus()) {
                                            case "PENDING":
                                                statusClass = "badge bg-warning text-dark";
                                                break;
                                            case "PAID":
                                                statusClass = "badge bg-info text-dark";
                                                break;
                                            case "COMPLETED":
                                                statusClass = "badge bg-success";
                                                break;
                                            case "CANCELED":
                                                statusClass = "badge bg-danger";
                                                break;
                                            case "REFUNDED":
                                                statusClass = "badge bg-primary";
                                                break;
                                            default:
                                                break;
                                        }
                            %>
                            <tr>
                                <td><%= order.getId() %></td>
                                <td>#<%= order.getUserId() %></td>
                                <td><%= order.getCreatedAt() != null ? order.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "" %></td>
                                <td><%= String.format("%.2f", order.getOriginalPrice()) %></td>
                                <td><%= String.format("%.2f", order.getDiscountPercent()) %>%</td>
                                <td><%= String.format("%.2f", order.getFinalPrice()) %></td>
                                <td><span class="<%= statusClass %>"><%= OrderStatus.getDisplayName(order.getStatus()) %></span></td>
                                <td>
                                    <a class="btn btn-sm btn-outline-primary" href="<%= request.getContextPath() %>/staff/order-detail?id=<%= order.getId() %>">View</a>
                                </td>
                            </tr>
                            <%
                                    }
                                }
                            %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

        </div>
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
    window.addEventListener('load', function () {
        var loader = document.getElementById('global-loader');
        if (loader) {
            loader.style.display = 'none';
        }
    });
</script>
</body>
</html>
