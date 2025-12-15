<%-- 
    Document   : admin-dashboard
    Created on : Dec 10, 2025, 10:22:12 PM
    Author     : Admin
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2 class="mb-4 fw-bold">Bảng điều khiển</h2>

<div class="row g-4">
    <div class="col-lg-3 col-md-6">
        <div class="card p-3 text-center">
            <i class="fa fa-users fa-2x text-primary mb-2"></i>
            <h6>Tổng khách hàng</h6>
            <h3 class="fw-bold">${data.totalCustomers}</h3>
        </div>
    </div>

    <div class="col-lg-3 col-md-6">
        <div class="card p-3 text-center">
            <i class="fa fa-user-tie fa-2x text-info mb-2"></i>
            <h6>Tổng nhân viên</h6>
            <h3 class="fw-bold">${data.totalStaff}</h3>
        </div>
    </div>

    <div class="col-lg-3 col-md-6">
        <div class="card p-3 text-center">
            <i class="fa fa-coins fa-2x text-success mb-2"></i>
            <h6>Doanh thu hôm nay</h6>
            <h3 class="fw-bold">${data.dailyRevenue}</h3>
        </div>
    </div>

    <div class="col-lg-3 col-md-6">
        <div class="card p-3 text-center">
            <i class="fa fa-cart-shopping fa-2x text-warning mb-2"></i>
            <h6>Đơn hàng hôm nay</h6>
            <h3 class="fw-bold">${data.ordersToday}</h3>
        </div>
    </div>
</div>

<div class="card mt-4 p-4">
    <h5 class="fw-bold mb-3">Biểu đồ doanh thu</h5>
    <canvas id="chartRevenue"></canvas>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    const chartLabels = [
        <c:forEach var="label" items="${chart.labels}" varStatus="loop">
        '${label}'<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
    ];
    
    const chartData = [
        <c:forEach var="value" items="${chart.data}" varStatus="loop">
        ${value}<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
    ];
    
    new Chart(document.getElementById('chartRevenue'), {
        type: 'line',
        data: {
            labels: chartLabels,
            datasets: [{
                label: "Doanh thu (VND)",
                data: chartData,
                borderWidth: 3,
                borderColor: '#4c57ff',
                backgroundColor: 'rgba(76, 87, 255, 0.1)',
                tension: 0.3,
                fill: true
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
</script>

