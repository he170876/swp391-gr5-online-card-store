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
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0">
    <title>Edit Card Info</title>
    <link rel="shortcut icon" type="image/x-icon" href="<%= ctx %>/assets/img/favicon.jpg">
    <link rel="stylesheet" href="<%= ctx %>/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="<%= ctx %>/assets/css/animate.css">
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
                                <h5>Edit Card</h5>
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
                    <li class="active">
                        <a href="<%= ctx %>/staff/cards"><img src="<%= ctx %>/assets/img/icons/product.svg" alt="img"><span>Card Info</span></a>
                    </li>
                    <li>
                        <a href="<%= ctx %>/staff/orders"><img src="<%= ctx %>/assets/img/icons/quotation1.svg" alt="img"><span>Orders</span></a>
                    </li>
                    <li>
                        <a href="<%= ctx %>/staff/cards-import"><img src="<%= ctx %>/assets/img/icons/product.svg" alt="img"><span>Import Cards</span></a>
                    </li>
                    <li>
                        <a href="<%= ctx %>/logout"><img src="<%= ctx %>/assets/img/icons/log-out.svg" alt="img"><span>Logout</span></a>
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
                        <h3 class="page-title">Edit Card Info</h3>
                    </div>
                    <div class="col-md-6 text-md-end">
                        <a class="btn btn-outline-secondary" href="<%= ctx %>/staff/cards">Back to list</a>
                    </div>
                </div>
            </div>

            <% if (error != null && !error.isEmpty()) { %>
                <div class="alert alert-danger" role="alert"><%= error %></div>
            <% } %>

            <% if (card != null) { %>
            <div class="card">
                <div class="card-body">
                    <form method="post" action="<%= ctx %>/staff/cards/edit/<%= card.getId() %>">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Code <span class="text-danger">*</span></label>
                                <input type="text" name="code" value="<%= card.getCode() %>" required class="form-control" />
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Serial <span class="text-danger">*</span></label>
                                <input type="text" name="serial" value="<%= card.getSerial() %>" required class="form-control" />
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Product <span class="text-danger">*</span></label>
                                <select name="productId" required class="form-select">
                                    <% if (products != null) { for (Product p : products) { %>
                                        <option value="<%= p.getId() %>" <%= (card.getProductId() == p.getId()) ? "selected" : "" %>><%= p.getName() %></option>
                                    <% } } %>
                                </select>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Expiry Date</label>
                                <input type="date" name="expiryDate" value="<%= card.getExpiryDate() == null ? "" : card.getExpiryDate().toString() %>" class="form-control" />
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Status <span class="text-danger">*</span></label>
                                <select name="status" required class="form-select">
                                    <% if (statuses != null) { for (String s : statuses) { %>
                                        <option value="<%= s %>" <%= s.equals(card.getStatus()) ? "selected" : "" %>><%= CardInfoStatus.getDisplayName(s) %></option>
                                    <% } } %>
                                </select>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-12">
                                <button type="submit" class="btn btn-primary me-2">Save Changes</button>
                                <a href="<%= ctx %>/staff/cards" class="btn btn-secondary">Cancel</a>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <% } else { %>
                <div class="alert alert-warning">Card not found</div>
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
