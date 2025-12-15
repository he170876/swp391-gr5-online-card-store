<%-- 
    Document   : admin-reports
    Created on : Dec 10, 2025, 10:40:00 PM
    Author     : Admin
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2 class="fw-bold mb-4">Báo cáo & Thống kê</h2>

<div class="row g-4 mb-4">
    <div class="col-lg-3 col-md-6">
        <div class="card p-3 text-center">
            <i class="fa fa-users fa-2x text-primary mb-2"></i>
            <h6>Tổng khách hàng</h6>
            <h3 class="fw-bold">${totalCustomers}</h3>
        </div>
    </div>

    <div class="col-lg-3 col-md-6">
        <div class="card p-3 text-center">
            <i class="fa fa-user-tie fa-2x text-info mb-2"></i>
            <h6>Tổng nhân viên</h6>
            <h3 class="fw-bold">${totalStaff}</h3>
        </div>
    </div>

    <div class="col-lg-3 col-md-6">
        <div class="card p-3 text-center">
            <i class="fa fa-coins fa-2x text-success mb-2"></i>
            <h6>Doanh thu hôm nay</h6>
            <h3 class="fw-bold">${dailyRevenue} VND</h3>
        </div>
    </div>

    <div class="col-lg-3 col-md-6">
        <div class="card p-3 text-center">
            <i class="fa fa-cart-shopping fa-2x text-warning mb-2"></i>
            <h6>Đơn hàng hôm nay</h6>
            <h3 class="fw-bold">${ordersToday}</h3>
        </div>
    </div>
</div>

<div class="card p-4">
    <h5 class="fw-bold mb-3">Biểu đồ doanh thu (7 ngày gần nhất)</h5>
    <canvas id="chartRevenue"></canvas>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    const revenueData = [
        <c:forEach var="revenue" items="${revenueData}" varStatus="loop">
        ${revenue}<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
    ];
    
    new Chart(document.getElementById('chartRevenue'), {
        type: 'line',
        data: {
            labels: ['Ngày 1', 'Ngày 2', 'Ngày 3', 'Ngày 4', 'Ngày 5', 'Ngày 6', 'Ngày 7'],
            datasets: [{
                label: "Doanh thu (VND)",
                data: revenueData,
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

