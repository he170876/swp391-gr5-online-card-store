<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title><c:out value="${param.title != null ? param.title : 'Online Card Store'}"/></title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
        <style>
            body { background-color: #f8f9fa; }
            .navbar-brand span { font-weight: 700; }
            .card-img-top { object-fit: cover; height: 180px; }
            .hero { background: linear-gradient(120deg, #e8f0ff, #f7fbff); }
        </style>
    </head>
    <body>

