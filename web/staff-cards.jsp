<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.CardInfoDAO.CardInfoListView" %>
<%@ page import="model.Product" %>
<%@ page import="model.Provider" %>
<%@ page import="util.CardInfoStatus" %>
<%
    String ctx = request.getContextPath();
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
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0">
    <title>Card Info - Staff</title>
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
                                <h5>Cards</h5>
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
                <div class="page-title">
                    <h4>Card Info</h4>
                    <h6>View card codes with filters</h6>
                </div>
            </div>

            <% 
                String success = request.getParameter("success");
                String error = request.getParameter("error");
            %>
            <% if ("updated".equals(success)) { %>
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle"></i> Card updated successfully!
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } else if ("deleted".equals(success)) { %>
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle"></i> Card deleted successfully!
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } else if ("used_card".equals(error)) { %>
                <div class="alert alert-warning alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-triangle"></i> Cannot delete a card that has been used or assigned!
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } else if ("delete_failed".equals(error)) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-times-circle"></i> Failed to delete card. Please try again.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>

            <div class="card mb-3">
                <div class="card-body">
                    <form class="row g-3" method="get" action="<%= ctx %>/staff/cards">
                        <div class="col-md-2">
                            <label class="form-label">Status</label>
                            <select name="status" class="form-select">
                                <option value="">All</option>
                                <% for (String s : statuses) { %>
                                    <option value="<%= s %>" <%= s.equals(selectedStatus) ? "selected" : "" %>><%= CardInfoStatus.getDisplayName(s) %></option>
                                <% } %>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Product</label>
                            <select name="productId" class="form-select select">
                                <option value="">All</option>
                                <% for (Product p : products) { %>
                                    <option value="<%= p.getId() %>" <%= (selectedProductId != null && selectedProductId == p.getId()) ? "selected" : "" %>><%= p.getName() %></option>
                                <% } %>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Provider</label>
                            <select name="providerId" class="form-select select">
                                <option value="">All</option>
                                <% for (Provider pr : providers) { %>
                                    <option value="<%= pr.getId() %>" <%= (selectedProviderId != null && selectedProviderId == pr.getId()) ? "selected" : "" %>><%= pr.getName() %></option>
                                <% } %>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">Expiry From</label>
                            <input type="date" name="expiryFrom" value="<%= expiryFrom == null ? "" : expiryFrom.toString() %>" class="form-control" />
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">Expiry To</label>
                            <input type="date" name="expiryTo" value="<%= expiryTo == null ? "" : expiryTo.toString() %>" class="form-control" />
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">Sort</label>
                            <select name="sort" class="form-select">
                                <option value="created_desc" <%= "created_desc".equals(sort) ? "selected" : "" %>>Newest</option>
                                <option value="expiry_asc" <%= "expiry_asc".equals(sort) ? "selected" : "" %>>Expiry Asc</option>
                                <option value="expiry_desc" <%= "expiry_desc".equals(sort) ? "selected" : "" %>>Expiry Desc</option>
                                <option value="status" <%= "status".equals(sort) ? "selected" : "" %>>Status</option>
                                <option value="provider" <%= "provider".equals(sort) ? "selected" : "" %>>Provider</option>
                                <option value="product" <%= "product".equals(sort) ? "selected" : "" %>>Product</option>
                            </select>
                        </div>
                        <div class="col-md-2 align-self-end">
                            <button class="btn btn-primary w-100" type="submit">Apply</button>
                        </div>
                    </form>
                </div>
            </div>

            <div class="card">
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-striped">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Product</th>
                                <th>Provider</th>
                                <th>Code</th>
                                <th>Serial</th>
                                <th>Expiry</th>
                                <th>Status</th>
                                <th>Created</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <% if (cards == null || cards.isEmpty()) { %>
                                <tr><td colspan="9" class="text-center">No cards found</td></tr>
                            <% } else { for (CardInfoListView c : cards) { %>
                                <tr>
                                    <td><%= c.getId() %></td>
                                    <td><%= c.getProductName() %></td>
                                    <td><%= c.getProviderName() %></td>
                                    <td><%= c.getCode() %></td>
                                    <td><%= c.getSerial() %></td>
                                    <td><%= c.getExpiryDate() == null ? "" : c.getExpiryDate() %></td>
                                    <td><span class="badge bg-info"><%= CardInfoStatus.getDisplayName(c.getStatus()) %></span></td>
                                    <td><%= c.getCreatedAt() == null ? "" : c.getCreatedAt() %></td>
                                    <td>
                                        <a href="<%= ctx %>/staff/cards/edit/<%= c.getId() %>" class="btn btn-sm btn-primary">
                                            <i class="fas fa-edit"></i> Edit
                                        </a>
                                        <form action="<%= ctx %>/staff/cards/delete/<%= c.getId() %>" method="POST" style="display:inline;" 
                                            onsubmit="return confirm('Bạn có chắc muốn xóa card này? (Chỉ có thể xóa card chưa được sử dụng)');">
                                            <button type="submit" class="btn btn-sm btn-danger">
                                                <i class="fas fa-trash"></i> Delete
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            <% } } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

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
