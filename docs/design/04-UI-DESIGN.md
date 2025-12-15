# UI/UX Design Guide

> **Version:** 1.0  
> **Date:** December 11, 2025  
> **Purpose:** Comprehensive UI/UX guidelines for OCS Admin Panel

---

## Table of Contents

1. [Design System Overview](#1-design-system-overview)
2. [Layout Structure](#2-layout-structure)
3. [Style Guide](#3-style-guide)
4. [Reusable Components](#4-reusable-components)
5. [Form Design](#5-form-design)
6. [Table Design](#6-table-design)
7. [Responsive Design](#7-responsive-design)
8. [States & Feedback](#8-states--feedback)
9. [JSP Template Structure](#9-jsp-template-structure)

---

## 1. Design System Overview

### 1.1 Technology Stack

| Component | Library | Version | Purpose |
|-----------|---------|---------|---------|
| CSS Framework | Bootstrap | 5.x | Grid, components, utilities |
| Icons (Primary) | Font Awesome | 6.x | Action icons, menu icons |
| Icons (Secondary) | Feather Icons | Latest | Sidebar icons |
| Data Tables | jQuery DataTables | 1.x | Sortable, searchable tables |
| Select Dropdown | Select2 | Latest | Enhanced dropdowns |
| Alerts | SweetAlert2 | Latest | Confirmation dialogs |
| Toast | Toastr | Latest | Notification messages |
| Animations | Animate.css | 4.x | Subtle animations |

### 1.2 Design Principles

```
┌─────────────────────────────────────────────────────────────────────┐
│  1. CONSISTENCY    │  Same patterns across all features            │
│  2. SIMPLICITY     │  Clean layouts, minimal cognitive load        │
│  3. FEEDBACK       │  Clear responses for all user actions         │
│  4. ACCESSIBILITY  │  Proper labels, contrast, keyboard nav        │
│  5. RESPONSIVENESS │  Works on desktop, tablet, mobile             │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 2. Layout Structure

### 2.1 Main Layout Structure

```
┌─────────────────────────────────────────────────────────────────────────┐
│ HEADER (60px height)                                                    │
│ ┌───────────┬─────────────────────────────────────────────────────────┐ │
│ │   LOGO    │  [Search]                    [Bell] [User ▼]           │ │
│ └───────────┴─────────────────────────────────────────────────────────┘ │
├─────────────┬───────────────────────────────────────────────────────────┤
│  SIDEBAR    │                                                           │
│  (260px)    │  CONTENT AREA                                            │
│             │  ┌─────────────────────────────────────────────────────┐  │
│ ┌─────────┐ │  │  PAGE HEADER                                       │  │
│ │Dashboard│ │  │  Title + Breadcrumb + Action Button                │  │
│ ├─────────┤ │  └─────────────────────────────────────────────────────┘  │
│ │Products │ │  ┌─────────────────────────────────────────────────────┐  │
│ │  ├ List │ │  │                                                     │  │
│ │  └ Add  │ │  │  CARD CONTAINER                                     │  │
│ ├─────────┤ │  │  (Main content: tables, forms, etc.)               │  │
│ │Category │ │  │                                                     │  │
│ ├─────────┤ │  │                                                     │  │
│ │Provider │ │  │                                                     │  │
│ ├─────────┤ │  └─────────────────────────────────────────────────────┘  │
│ │Orders   │ │                                                           │
│ ├─────────┤ │                                                           │
│ │Users    │ │                                                           │
│ └─────────┘ │                                                           │
└─────────────┴───────────────────────────────────────────────────────────┘
```

### 2.2 CSS Classes Reference

```css
/* Main Structure */
.main-wrapper        /* Root container */
.header              /* Top header bar */
.sidebar             /* Left sidebar navigation */
.page-wrapper        /* Main content wrapper */
.content             /* Content padding container */

/* Page Elements */
.page-header         /* Title + breadcrumb + action button area */
.page-title          /* Page title container */
.page-btn            /* Action button container */
.card                /* Bootstrap card wrapper */
.card-body           /* Card content area */
```

---

## 3. Style Guide

### 3.1 Color Palette

| Name | Hex Code | CSS Variable | Usage |
|------|----------|--------------|-------|
| Primary | `#FF9F43` | `--primary` | Main brand color, primary buttons |
| Primary Dark | `#E8933D` | `--primary-dark` | Hover states |
| Success | `#28C76F` | `--success` | Success messages, active status |
| Danger | `#EA5455` | `--danger` | Error messages, delete buttons |
| Warning | `#FF9F43` | `--warning` | Warning messages |
| Info | `#00CFE8` | `--info` | Information badges |
| Dark | `#1E1E2D` | `--dark` | Sidebar background, text |
| Light | `#F8F9FA` | `--light` | Page background |
| Muted | `#6E6B7B` | `--muted` | Secondary text |
| Border | `#EBE9F1` | `--border` | Card borders |

### 3.2 Typography

| Element | Font | Size | Weight | Line Height |
|---------|------|------|--------|-------------|
| Body | Nunito | 14px | 400 | 1.5 |
| H1 | Nunito | 32px | 600 | 1.2 |
| H2 | Nunito | 28px | 600 | 1.2 |
| H3 | Nunito | 24px | 600 | 1.3 |
| H4 | Nunito | 20px | 600 | 1.4 |
| H5 | Nunito | 16px | 600 | 1.5 |
| H6 | Nunito | 14px | 600 | 1.5 |
| Small | Nunito | 12px | 400 | 1.5 |
| Label | Nunito | 14px | 500 | 1.5 |

### 3.3 Spacing System

```css
/* Spacing Scale (Bootstrap) */
--spacing-0: 0;
--spacing-1: 0.25rem;  /* 4px */
--spacing-2: 0.5rem;   /* 8px */
--spacing-3: 1rem;     /* 16px */
--spacing-4: 1.5rem;   /* 24px */
--spacing-5: 3rem;     /* 48px */

/* Common Usage */
Card padding:       24px (p-4)
Section margin:     24px (mb-4)
Form group margin:  16px (mb-3)
Input padding:      8px 12px
Button padding:     8px 16px
```

### 3.4 Border Radius

| Element | Radius |
|---------|--------|
| Cards | 8px |
| Buttons | 6px |
| Inputs | 5px |
| Badges | 4px |
| Modals | 10px |
| Tooltips | 4px |

### 3.5 Shadows

```css
/* Card Shadow */
.card {
    box-shadow: 0 4px 24px 0 rgba(34, 41, 47, 0.1);
}

/* Dropdown Shadow */
.dropdown-menu {
    box-shadow: 0 4px 25px 0 rgba(34, 41, 47, 0.1);
}

/* Modal Shadow */
.modal-content {
    box-shadow: 0 5px 20px rgba(34, 41, 47, 0.2);
}
```

---

## 4. Reusable Components

### 4.1 Buttons

```html
<!-- Primary Button -->
<button class="btn btn-primary">
    <i class="fa fa-plus me-1"></i> Add New
</button>

<!-- Secondary Button -->
<button class="btn btn-secondary">Cancel</button>

<!-- Success Button -->
<button class="btn btn-success">Save</button>

<!-- Danger Button -->
<button class="btn btn-danger">Delete</button>

<!-- Outline Buttons -->
<button class="btn btn-outline-primary">Edit</button>

<!-- Icon-only Button -->
<button class="btn btn-sm btn-light">
    <i class="fa fa-eye"></i>
</button>

<!-- With Image (Template Style) -->
<a href="addproduct.html" class="btn btn-added">
    <img src="assets/img/icons/plus.svg" alt="img" class="me-1">Add New Product
</a>
```

### 4.2 Status Badges

```html
<!-- Active Status -->
<span class="badge bg-success-light text-success">ACTIVE</span>

<!-- Inactive Status -->
<span class="badge bg-danger-light text-danger">INACTIVE</span>

<!-- Pending Status -->
<span class="badge bg-warning-light text-warning">PENDING</span>

<!-- Custom Badge CSS -->
<style>
.bg-success-light { background-color: rgba(40, 199, 111, 0.12) !important; }
.bg-danger-light { background-color: rgba(234, 84, 85, 0.12) !important; }
.bg-warning-light { background-color: rgba(255, 159, 67, 0.12) !important; }
</style>
```

### 4.3 Cards

```html
<!-- Standard Card -->
<div class="card">
    <div class="card-header">
        <h5 class="card-title">Card Title</h5>
    </div>
    <div class="card-body">
        <!-- Content here -->
    </div>
</div>

<!-- Card with Table -->
<div class="card">
    <div class="card-body">
        <div class="table-top">
            <!-- Filter/Search controls -->
        </div>
        <div class="table-responsive">
            <table class="table datanew">
                <!-- Table content -->
            </table>
        </div>
    </div>
</div>
```

### 4.4 Modals

```html
<!-- Confirmation Modal (SweetAlert2) -->
<script>
function confirmDelete(id, name) {
    Swal.fire({
        title: 'Are you sure?',
        text: `Delete "${name}"? This action cannot be undone.`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#EA5455',
        cancelButtonColor: '#6E6B7B',
        confirmButtonText: 'Yes, delete it!',
        cancelButtonText: 'Cancel'
    }).then((result) => {
        if (result.isConfirmed) {
            document.getElementById('deleteForm-' + id).submit();
        }
    });
}
</script>

<!-- Standard Bootstrap Modal -->
<div class="modal fade" id="exampleModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Modal Title</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <!-- Modal content -->
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary">Save changes</button>
            </div>
        </div>
    </div>
</div>
```

### 4.5 Toast Notifications

```javascript
// Success Toast
toastr.success('Record saved successfully!', 'Success');

// Error Toast
toastr.error('Failed to save record.', 'Error');

// Warning Toast
toastr.warning('Please fill all required fields.', 'Warning');

// Info Toast
toastr.info('Processing your request...', 'Info');

// Toastr Configuration
toastr.options = {
    "closeButton": true,
    "progressBar": true,
    "positionClass": "toast-top-right",
    "timeOut": "3000"
};
```

### 4.6 Empty State

```html
<div class="empty-state text-center py-5">
    <img src="${pageContext.request.contextPath}/assets/img/empty.svg" 
         alt="No data" style="max-width: 200px;">
    <h5 class="mt-3">No Products Found</h5>
    <p class="text-muted">Try adjusting your search or filter to find what you're looking for.</p>
    <a href="${pageContext.request.contextPath}/admin/product/add" class="btn btn-primary">
        <i class="fa fa-plus me-1"></i> Add First Product
    </a>
</div>
```

---

## 5. Form Design

### 5.1 Form Layout

```html
<form action="submit" method="post" class="needs-validation" novalidate>
    <div class="card">
        <div class="card-body">
            <!-- Section Title -->
            <h5 class="card-title mb-4">Basic Information</h5>
            
            <div class="row">
                <!-- Text Input -->
                <div class="col-lg-6 col-sm-12">
                    <div class="form-group mb-3">
                        <label for="name" class="form-label">
                            Product Name <span class="text-danger">*</span>
                        </label>
                        <input type="text" 
                               class="form-control ${not empty errors.name ? 'is-invalid' : ''}" 
                               id="name" 
                               name="name" 
                               value="${formDTO.name}"
                               placeholder="Enter product name"
                               required>
                        <c:if test="${not empty errors.name}">
                            <div class="invalid-feedback">${errors.name}</div>
                        </c:if>
                    </div>
                </div>
                
                <!-- Select Dropdown -->
                <div class="col-lg-6 col-sm-12">
                    <div class="form-group mb-3">
                        <label for="categoryId" class="form-label">
                            Category <span class="text-danger">*</span>
                        </label>
                        <select class="select form-control ${not empty errors.categoryId ? 'is-invalid' : ''}" 
                                id="categoryId" 
                                name="categoryId" 
                                required>
                            <option value="">Choose Category</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat.id}" 
                                        ${formDTO.categoryId == cat.id ? 'selected' : ''}>
                                    ${cat.name}
                                </option>
                            </c:forEach>
                        </select>
                        <c:if test="${not empty errors.categoryId}">
                            <div class="invalid-feedback">${errors.categoryId}</div>
                        </c:if>
                    </div>
                </div>
            </div>
            
            <!-- Textarea -->
            <div class="row">
                <div class="col-12">
                    <div class="form-group mb-3">
                        <label for="description" class="form-label">Description</label>
                        <textarea class="form-control" 
                                  id="description" 
                                  name="description" 
                                  rows="4"
                                  placeholder="Enter description...">${formDTO.description}</textarea>
                    </div>
                </div>
            </div>
            
            <!-- Radio Buttons -->
            <div class="row">
                <div class="col-12">
                    <div class="form-group mb-3">
                        <label class="form-label">Status</label>
                        <div class="d-flex gap-4">
                            <div class="form-check">
                                <input class="form-check-input" 
                                       type="radio" 
                                       name="status" 
                                       id="statusActive" 
                                       value="ACTIVE"
                                       ${formDTO.status == 'ACTIVE' || empty formDTO.status ? 'checked' : ''}>
                                <label class="form-check-label" for="statusActive">Active</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" 
                                       type="radio" 
                                       name="status" 
                                       id="statusInactive" 
                                       value="INACTIVE"
                                       ${formDTO.status == 'INACTIVE' ? 'checked' : ''}>
                                <label class="form-check-label" for="statusInactive">Inactive</label>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Form Actions -->
            <div class="row mt-4">
                <div class="col-12">
                    <a href="${pageContext.request.contextPath}/admin/product" 
                       class="btn btn-secondary me-2">Cancel</a>
                    <button type="submit" class="btn btn-primary">
                        <i class="fa fa-save me-1"></i> Submit
                    </button>
                </div>
            </div>
        </div>
    </div>
</form>
```

### 5.2 Input States

```html
<!-- Normal State -->
<input type="text" class="form-control">

<!-- Focus State (CSS auto) -->

<!-- Valid State -->
<input type="text" class="form-control is-valid">
<div class="valid-feedback">Looks good!</div>

<!-- Invalid State -->
<input type="text" class="form-control is-invalid">
<div class="invalid-feedback">Please provide a valid name.</div>

<!-- Disabled State -->
<input type="text" class="form-control" disabled>

<!-- Readonly State -->
<input type="text" class="form-control" readonly value="Readonly value">
```

### 5.3 Form Validation (Client-side)

```javascript
// Bootstrap validation
(function() {
    'use strict';
    
    const forms = document.querySelectorAll('.needs-validation');
    
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
})();

// Custom validation
function validateForm() {
    let isValid = true;
    
    // Name validation
    const name = document.getElementById('name');
    if (!name.value.trim()) {
        setInvalid(name, 'Name is required');
        isValid = false;
    } else if (name.value.length < 3) {
        setInvalid(name, 'Name must be at least 3 characters');
        isValid = false;
    } else {
        setValid(name);
    }
    
    return isValid;
}

function setInvalid(input, message) {
    input.classList.remove('is-valid');
    input.classList.add('is-invalid');
    const feedback = input.nextElementSibling;
    if (feedback && feedback.classList.contains('invalid-feedback')) {
        feedback.textContent = message;
    }
}

function setValid(input) {
    input.classList.remove('is-invalid');
    input.classList.add('is-valid');
}
```

---

## 6. Table Design

### 6.1 DataTable Structure

```html
<div class="card">
    <div class="card-body">
        <!-- Table Top Controls -->
        <div class="table-top">
            <div class="search-set">
                <!-- Filter Toggle -->
                <div class="search-path">
                    <a class="btn btn-filter" id="filter_search">
                        <img src="assets/img/icons/filter.svg" alt="img">
                        <span><img src="assets/img/icons/closes.svg" alt="img"></span>
                    </a>
                </div>
                <!-- Search Input -->
                <div class="search-input">
                    <a class="btn btn-searchset">
                        <img src="assets/img/icons/search-white.svg" alt="img">
                    </a>
                </div>
            </div>
            <!-- Export Buttons -->
            <div class="wordset">
                <ul>
                    <li><a data-bs-toggle="tooltip" title="PDF"><img src="assets/img/icons/pdf.svg"></a></li>
                    <li><a data-bs-toggle="tooltip" title="Excel"><img src="assets/img/icons/excel.svg"></a></li>
                    <li><a data-bs-toggle="tooltip" title="Print"><img src="assets/img/icons/printer.svg"></a></li>
                </ul>
            </div>
        </div>
        
        <!-- Filter Panel (Collapsible) -->
        <div class="card mb-0" id="filter_inputs">
            <div class="card-body pb-0">
                <div class="row">
                    <div class="col-lg col-sm-6 col-12">
                        <div class="form-group">
                            <select class="select" name="categoryId">
                                <option value="">All Categories</option>
                                <!-- Options -->
                            </select>
                        </div>
                    </div>
                    <div class="col-lg col-sm-6 col-12">
                        <div class="form-group">
                            <select class="select" name="status">
                                <option value="">All Status</option>
                                <option value="ACTIVE">Active</option>
                                <option value="INACTIVE">Inactive</option>
                            </select>
                        </div>
                    </div>
                    <div class="col-lg-1 col-sm-6 col-12">
                        <div class="form-group">
                            <button class="btn btn-filters">
                                <img src="assets/img/icons/search-whites.svg">
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Data Table -->
        <div class="table-responsive">
            <table class="table datanew">
                <thead>
                    <tr>
                        <th>
                            <label class="checkboxs">
                                <input type="checkbox" id="select-all">
                                <span class="checkmarks"></span>
                            </label>
                        </th>
                        <th>Product Name</th>
                        <th>Category</th>
                        <th>Provider</th>
                        <th>Price</th>
                        <th>Qty</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="product" items="${products}">
                        <tr>
                            <td>
                                <label class="checkboxs">
                                    <input type="checkbox" name="ids" value="${product.id}">
                                    <span class="checkmarks"></span>
                                </label>
                            </td>
                            <td class="productimgname">
                                <a href="javascript:void(0);">${product.name}</a>
                            </td>
                            <td>${product.categoryName}</td>
                            <td>${product.providerName}</td>
                            <td><fmt:formatNumber value="${product.sellPrice}" type="currency"/></td>
                            <td>${product.quantity}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${product.status == 'ACTIVE'}">
                                        <span class="badge bg-success-light text-success">ACTIVE</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-danger-light text-danger">INACTIVE</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a class="me-3" href="${pageContext.request.contextPath}/admin/product/detail?id=${product.id}">
                                    <img src="assets/img/icons/eye.svg" alt="View">
                                </a>
                                <a class="me-3" href="${pageContext.request.contextPath}/admin/product/edit?id=${product.id}">
                                    <img src="assets/img/icons/edit.svg" alt="Edit">
                                </a>
                                <a class="confirm-text" href="javascript:void(0);" 
                                   onclick="confirmDelete(${product.id}, '${product.name}')">
                                    <img src="assets/img/icons/delete.svg" alt="Delete">
                                </a>
                                <!-- Hidden delete form -->
                                <form id="deleteForm-${product.id}" 
                                      action="${pageContext.request.contextPath}/admin/product/delete" 
                                      method="post" style="display:none;">
                                    <input type="hidden" name="id" value="${product.id}">
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
```

### 6.2 DataTable Initialization

```javascript
$(document).ready(function() {
    $('.datanew').DataTable({
        "pageLength": 10,
        "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
        "language": {
            "search": "",
            "searchPlaceholder": "Search...",
            "lengthMenu": "Show _MENU_ entries",
            "info": "Showing _START_ to _END_ of _TOTAL_ entries",
            "paginate": {
                "previous": "<i class='fa fa-chevron-left'></i>",
                "next": "<i class='fa fa-chevron-right'></i>"
            }
        },
        "dom": '<"top"lf>rt<"bottom"ip>',
        "order": [[1, "asc"]]
    });
});
```

---

## 7. Responsive Design

### 7.1 Breakpoints

| Breakpoint | Size | Target |
|------------|------|--------|
| xs | < 576px | Mobile portrait |
| sm | ≥ 576px | Mobile landscape |
| md | ≥ 768px | Tablet |
| lg | ≥ 992px | Desktop |
| xl | ≥ 1200px | Large desktop |
| xxl | ≥ 1400px | Extra large |

### 7.2 Responsive Grid Usage

```html
<!-- Full width mobile, half on tablet, third on desktop -->
<div class="row">
    <div class="col-12 col-md-6 col-lg-4">Column 1</div>
    <div class="col-12 col-md-6 col-lg-4">Column 2</div>
    <div class="col-12 col-md-12 col-lg-4">Column 3</div>
</div>

<!-- Form responsive layout -->
<div class="row">
    <div class="col-lg-3 col-sm-6 col-12">
        <div class="form-group">
            <label>Product Name</label>
            <input type="text" class="form-control">
        </div>
    </div>
    <div class="col-lg-3 col-sm-6 col-12">
        <div class="form-group">
            <label>Category</label>
            <select class="select">...</select>
        </div>
    </div>
</div>
```

### 7.3 Sidebar Behavior

```css
/* Desktop: Sidebar visible, toggle to collapse */
@media (min-width: 992px) {
    .sidebar { width: 260px; }
    .page-wrapper { margin-left: 260px; }
    
    .mini-sidebar .sidebar { width: 80px; }
    .mini-sidebar .page-wrapper { margin-left: 80px; }
}

/* Mobile: Sidebar hidden, overlay on toggle */
@media (max-width: 991.98px) {
    .sidebar { 
        position: fixed;
        left: -260px;
        transition: left 0.3s;
    }
    .sidebar.open { left: 0; }
    .page-wrapper { margin-left: 0; }
}
```

---

## 8. States & Feedback

### 8.1 Loading States

```html
<!-- Full page loader -->
<div id="global-loader">
    <div class="whirly-loader"></div>
</div>

<!-- Button loading state -->
<button type="submit" class="btn btn-primary" id="submitBtn">
    <span class="spinner-border spinner-border-sm d-none" role="status"></span>
    <span class="btn-text">Submit</span>
</button>

<script>
$('#submitBtn').on('click', function() {
    $(this).find('.spinner-border').removeClass('d-none');
    $(this).find('.btn-text').text('Processing...');
    $(this).prop('disabled', true);
});
</script>
```

### 8.2 Success/Error Messages (JSP)

```jsp
<!-- Success Message (from session) -->
<c:if test="${not empty sessionScope.successMessage}">
    <script>
        toastr.success('${sessionScope.successMessage}', 'Success');
    </script>
    <c:remove var="successMessage" scope="session"/>
</c:if>

<!-- Error Message (from session) -->
<c:if test="${not empty sessionScope.errorMessage}">
    <script>
        toastr.error('${sessionScope.errorMessage}', 'Error');
    </script>
    <c:remove var="errorMessage" scope="session"/>
</c:if>

<!-- Form Error (from request) -->
<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="fa fa-exclamation-circle me-2"></i>
        ${errorMessage}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>
```

### 8.3 Confirmation Dialogs

```javascript
// Delete confirmation
function confirmDelete(id, name) {
    Swal.fire({
        title: 'Delete Confirmation',
        html: `Are you sure you want to delete <strong>"${name}"</strong>?<br>This action cannot be undone.`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#EA5455',
        cancelButtonColor: '#6E6B7B',
        confirmButtonText: '<i class="fa fa-trash me-1"></i> Delete',
        cancelButtonText: 'Cancel',
        reverseButtons: true
    }).then((result) => {
        if (result.isConfirmed) {
            document.getElementById('deleteForm-' + id).submit();
        }
    });
}

// Status change confirmation
function confirmStatusChange(id, newStatus) {
    const action = newStatus === 'INACTIVE' ? 'deactivate' : 'activate';
    
    Swal.fire({
        title: 'Confirm Status Change',
        text: `Are you sure you want to ${action} this item?`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: newStatus === 'INACTIVE' ? '#EA5455' : '#28C76F',
        confirmButtonText: 'Yes, ' + action,
        cancelButtonText: 'Cancel'
    }).then((result) => {
        if (result.isConfirmed) {
            // Submit form or make AJAX call
        }
    });
}
```

---

## 9. JSP Template Structure

### 9.1 Base Layout Template

Create a reusable layout structure for all admin pages:

```
web/
├── admin/
│   ├── includes/
│   │   ├── header.jsp          # Common header with meta, CSS imports
│   │   ├── sidebar.jsp         # Sidebar navigation
│   │   ├── topbar.jsp          # Top navigation bar
│   │   ├── footer.jsp          # Common footer with JS imports
│   │   └── scripts.jsp         # Page-specific scripts
│   │
│   ├── product/
│   │   ├── list.jsp
│   │   ├── add.jsp
│   │   ├── edit.jsp
│   │   └── detail.jsp
│   │
│   ├── category/
│   │   ├── list.jsp
│   │   ├── add.jsp
│   │   ├── edit.jsp
│   │   └── detail.jsp
│   │
│   └── provider/
│       ├── list.jsp
│       ├── add.jsp
│       ├── edit.jsp
│       └── detail.jsp
```

### 9.2 header.jsp

```jsp
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0">
    <meta name="description" content="OCS - Online Card Store Admin">
    <title>${param.title} - OCS Admin</title>
    
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/assets/img/favicon.ico">
    
    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/animate.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/select2/css/select2.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome/css/fontawesome.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <div id="global-loader">
        <div class="whirly-loader"></div>
    </div>
    
    <div class="main-wrapper">
```

### 9.3 sidebar.jsp

```jsp
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="sidebar" id="sidebar">
    <div class="sidebar-inner slimscroll">
        <div id="sidebar-menu" class="sidebar-menu">
            <ul>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/dashboard">
                        <img src="${pageContext.request.contextPath}/assets/img/icons/dashboard.svg" alt="img">
                        <span>Dashboard</span>
                    </a>
                </li>
                
                <li class="submenu ${param.menu == 'product' ? 'active' : ''}">
                    <a href="javascript:void(0);">
                        <img src="${pageContext.request.contextPath}/assets/img/icons/product.svg" alt="img">
                        <span>Product</span>
                        <span class="menu-arrow"></span>
                    </a>
                    <ul>
                        <li>
                            <a href="${pageContext.request.contextPath}/admin/product" 
                               class="${param.submenu == 'product-list' ? 'active' : ''}">
                                Product List
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/admin/product/add"
                               class="${param.submenu == 'product-add' ? 'active' : ''}">
                                Add Product
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/admin/category"
                               class="${param.submenu == 'category-list' ? 'active' : ''}">
                                Category List
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/admin/category/add"
                               class="${param.submenu == 'category-add' ? 'active' : ''}">
                                Add Category
                            </a>
                        </li>
                    </ul>
                </li>
                
                <li class="submenu ${param.menu == 'provider' ? 'active' : ''}">
                    <a href="javascript:void(0);">
                        <img src="${pageContext.request.contextPath}/assets/img/icons/users1.svg" alt="img">
                        <span>Provider</span>
                        <span class="menu-arrow"></span>
                    </a>
                    <ul>
                        <li>
                            <a href="${pageContext.request.contextPath}/admin/provider"
                               class="${param.submenu == 'provider-list' ? 'active' : ''}">
                                Provider List
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/admin/provider/add"
                               class="${param.submenu == 'provider-add' ? 'active' : ''}">
                                Add Provider
                            </a>
                        </li>
                    </ul>
                </li>
                
                <!-- More menu items... -->
            </ul>
        </div>
    </div>
</div>
```

### 9.4 footer.jsp

```jsp
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        </div> <!-- End main-wrapper -->
        
        <!-- JavaScript -->
        <script src="${pageContext.request.contextPath}/assets/js/jquery-3.6.0.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/feather.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/jquery.slimscroll.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/plugins/select2/js/select2.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/jquery.dataTables.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/dataTables.bootstrap4.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/plugins/sweetalert/sweetalert2.all.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/plugins/toastr/toastr.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
        
        <!-- Toast Notifications -->
        <c:if test="${not empty sessionScope.successMessage}">
            <script>toastr.success('${sessionScope.successMessage}', 'Success');</script>
            <c:remove var="successMessage" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.errorMessage}">
            <script>toastr.error('${sessionScope.errorMessage}', 'Error');</script>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>
    </body>
</html>
```

### 9.5 Example: Product List Page

```jsp
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../includes/header.jsp">
    <jsp:param name="title" value="Product List"/>
</jsp:include>

<jsp:include page="../includes/topbar.jsp"/>

<jsp:include page="../includes/sidebar.jsp">
    <jsp:param name="menu" value="product"/>
    <jsp:param name="submenu" value="product-list"/>
</jsp:include>

<div class="page-wrapper">
    <div class="content">
        <!-- Page Header -->
        <div class="page-header">
            <div class="page-title">
                <h4>Product List</h4>
                <h6>Manage your products</h6>
            </div>
            <div class="page-btn">
                <a href="${pageContext.request.contextPath}/admin/product/add" class="btn btn-added">
                    <img src="${pageContext.request.contextPath}/assets/img/icons/plus.svg" alt="img" class="me-1">
                    Add New Product
                </a>
            </div>
        </div>
        
        <!-- Table Card -->
        <div class="card">
            <div class="card-body">
                <!-- Filter/Search Controls -->
                <div class="table-top">
                    <form action="${pageContext.request.contextPath}/admin/product" method="get" class="w-100">
                        <div class="row g-2">
                            <div class="col-lg-3 col-sm-6">
                                <input type="text" name="keyword" class="form-control" 
                                       placeholder="Search..." value="${searchDTO.keyword}">
                            </div>
                            <div class="col-lg-2 col-sm-6">
                                <select name="categoryId" class="select">
                                    <option value="">All Categories</option>
                                    <c:forEach var="cat" items="${categories}">
                                        <option value="${cat.id}" 
                                                ${searchDTO.categoryId == cat.id ? 'selected' : ''}>
                                            ${cat.name}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-lg-2 col-sm-6">
                                <select name="status" class="select">
                                    <option value="">All Status</option>
                                    <option value="ACTIVE" ${searchDTO.status == 'ACTIVE' ? 'selected' : ''}>Active</option>
                                    <option value="INACTIVE" ${searchDTO.status == 'INACTIVE' ? 'selected' : ''}>Inactive</option>
                                </select>
                            </div>
                            <div class="col-lg-1 col-sm-6">
                                <button type="submit" class="btn btn-filters">
                                    <i class="fa fa-search"></i>
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
                
                <!-- Data Table -->
                <div class="table-responsive">
                    <c:choose>
                        <c:when test="${empty products}">
                            <div class="empty-state text-center py-5">
                                <img src="${pageContext.request.contextPath}/assets/img/empty.svg" 
                                     alt="No data" style="max-width: 200px;">
                                <h5 class="mt-3">No Products Found</h5>
                                <p class="text-muted">Try adjusting your search or filter.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <table class="table datanew">
                                <thead>
                                    <tr>
                                        <th>Product Name</th>
                                        <th>Category</th>
                                        <th>Provider</th>
                                        <th>Price</th>
                                        <th>Qty</th>
                                        <th>Status</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="product" items="${products}">
                                        <tr>
                                            <td>${product.name}</td>
                                            <td>${product.categoryName}</td>
                                            <td>${product.providerName}</td>
                                            <td>
                                                <fmt:formatNumber value="${product.sellPrice}" 
                                                                  type="currency" currencyCode="VND"/>
                                            </td>
                                            <td>${product.quantity}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${product.status == 'ACTIVE'}">
                                                        <span class="badge bg-success-light text-success">ACTIVE</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-danger-light text-danger">INACTIVE</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <a class="me-2" href="${pageContext.request.contextPath}/admin/product/detail?id=${product.id}" title="View">
                                                    <i class="fa fa-eye text-info"></i>
                                                </a>
                                                <a class="me-2" href="${pageContext.request.contextPath}/admin/product/edit?id=${product.id}" title="Edit">
                                                    <i class="fa fa-edit text-primary"></i>
                                                </a>
                                                <a href="javascript:void(0);" onclick="confirmDelete(${product.id}, '${product.name}')" title="Delete">
                                                    <i class="fa fa-trash text-danger"></i>
                                                </a>
                                                <form id="deleteForm-${product.id}" 
                                                      action="${pageContext.request.contextPath}/admin/product/delete" 
                                                      method="post" style="display:none;">
                                                    <input type="hidden" name="id" value="${product.id}">
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:otherwise>
                    </c:choose>
                </div>
                
                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <nav aria-label="Page navigation">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${searchDTO.page == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="?page=${searchDTO.page - 1}&keyword=${searchDTO.keyword}&categoryId=${searchDTO.categoryId}&status=${searchDTO.status}">
                                    <i class="fa fa-chevron-left"></i>
                                </a>
                            </li>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${searchDTO.page == i ? 'active' : ''}">
                                    <a class="page-link" href="?page=${i}&keyword=${searchDTO.keyword}&categoryId=${searchDTO.categoryId}&status=${searchDTO.status}">${i}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${searchDTO.page == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="?page=${searchDTO.page + 1}&keyword=${searchDTO.keyword}&categoryId=${searchDTO.categoryId}&status=${searchDTO.status}">
                                    <i class="fa fa-chevron-right"></i>
                                </a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </div>
        </div>
    </div>
</div>

<script>
function confirmDelete(id, name) {
    Swal.fire({
        title: 'Delete Confirmation',
        html: 'Are you sure you want to delete <strong>"' + name + '"</strong>?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#EA5455',
        cancelButtonColor: '#6E6B7B',
        confirmButtonText: '<i class="fa fa-trash me-1"></i> Delete',
        cancelButtonText: 'Cancel',
        reverseButtons: true
    }).then((result) => {
        if (result.isConfirmed) {
            document.getElementById('deleteForm-' + id).submit();
        }
    });
}
</script>

<jsp:include page="../includes/footer.jsp"/>
```

---

**End of UI/UX Design Guide**
