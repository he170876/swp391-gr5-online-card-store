# OCS - Online Card Store: Summary Design Document

> **Version:** 1.0  
> **Date:** December 11, 2025  
> **Project:** SWP391 - Online Card Store (OCS)

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Architecture](#2-architecture)
3. [Technology Stack](#3-technology-stack)
4. [Package Structure](#4-package-structure)
5. [Database Schema](#5-database-schema)
6. [Feature Summary](#6-feature-summary)
7. [UI/UX Design Guidelines](#7-uiux-design-guidelines)
8. [Related Documents](#8-related-documents)

---

## 1. Project Overview

**OCS (Online Card Store)** is a Jakarta EE 10 web application for managing and selling digital cards (gift cards, game cards, etc.). The system supports:

- **Admin**: Full system management (users, products, categories, providers, orders)
- **Staff**: Product/inventory management, order processing
- **Customer**: Browse products, purchase cards, wallet management

### Business Domain

```
┌─────────────────────────────────────────────────────────────────┐
│                     Online Card Store                           │
├─────────────────────────────────────────────────────────────────┤
│  Provider ──┬──> Product ──┬──> CardInfo ──> Order              │
│             │              │                   │                │
│  Category ──┘              └──> ProductLog     │                │
│                                                │                │
│  User ─────────────────────────────────────────┴──> Wallet      │
│   │                                                             │
│   └──> Role (ADMIN, STAFF, CUSTOMER)                           │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Architecture

### 2.1 Overall Architecture Pattern

**MVC (Model-View-Controller)** architecture with classic Jakarta EE patterns:

```
┌──────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                        │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                     JSP Views                             │   │
│  │    (JSTL, Bootstrap 5, DataTables, Select2, etc.)        │   │
│  └──────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────────┐
│                        CONTROLLER LAYER                          │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              Servlet Controllers (@WebServlet)            │   │
│  │              Filters (@WebFilter)                         │   │
│  └──────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────────┐
│                        DATA ACCESS LAYER                         │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                  DAO Classes                              │   │
│  │              (extends DBContext)                          │   │
│  └──────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────────┐
│                        DATABASE LAYER                            │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              SQL Server (ocs database)                    │   │
│  └──────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────┘
```

### 2.2 Request Flow

```
Browser Request
      │
      ▼
┌─────────────────┐
│  HTTP Filter    │  ← SessionFilter, RoleFilter
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   Servlet       │  ← @WebServlet Controller
│   Controller    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│      DAO        │  ← Data Access Object
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   SQL Server    │
└─────────────────┘
         │
         ▼
   Response (JSP)
```

---

## 3. Technology Stack

### 3.1 Backend

| Component | Technology | Version |
|-----------|------------|---------|
| Language | Java | 17 |
| Platform | Jakarta EE | 10 (Web Profile) |
| Web Server | Apache Tomcat | 10.x |
| Database | Microsoft SQL Server | Express |
| JDBC Driver | mssql-jdbc | 42.x |
| Build Tool | Apache Ant | (NetBeans) |

### 3.2 Frontend (UI/UX Stack)

| Component | Technology | Version |
|-----------|------------|---------|
| CSS Framework | Bootstrap | 5.x |
| DataTable | jQuery DataTables | Bootstrap 4 |
| Select Dropdown | Select2 | Latest |
| Icons | Font Awesome | 6.x |
| Icons (Secondary) | Feather Icons | Latest |
| Animations | Animate.css | 4.x |
| Alerts | SweetAlert2 | Latest |
| Toast Notifications | Toastr | Latest |
| Date Picker | Bootstrap Datetimepicker | Latest |
| Charts | ApexCharts, Chart.js, Morris | Various |
| Scrollbar | Slim Scroll | Latest |

### 3.3 Key Libraries (Template Plugins)

```
plugins/
├── alertify/          # Alert dialogs
├── apexchart/         # Modern charts
├── chartjs/           # Canvas charts
├── clipboard/         # Copy to clipboard
├── countup/           # Number animations
├── dragula/           # Drag & drop
├── fileupload/        # File upload
├── fontawesome/       # Icons
├── fullcalendar/      # Calendar widget
├── lightbox/          # Image lightbox
├── moment/            # Date handling
├── owlcarousel/       # Carousels
├── select2/           # Enhanced selects
├── summernote/        # Rich text editor
├── sweetalert/        # Beautiful alerts
└── toastr/            # Toast notifications
```

---

## 4. Package Structure

```
src/java/
├── controller/                    # Servlet Controllers
│   ├── HomePageController.java
│   ├── LoginController.java
│   ├── LogoutController.java
│   ├── product/                   # [TO CREATE]
│   │   ├── ProductListController.java
│   │   ├── ProductAddController.java
│   │   ├── ProductEditController.java
│   │   └── ProductDeleteController.java
│   ├── provider/                  # [TO CREATE]
│   │   ├── ProviderListController.java
│   │   ├── ProviderAddController.java
│   │   ├── ProviderEditController.java
│   │   └── ProviderDeleteController.java
│   └── category/                  # [TO CREATE]
│       ├── CategoryListController.java
│       ├── CategoryAddController.java
│       ├── CategoryEditController.java
│       └── CategoryDeleteController.java
│
├── dao/                           # Data Access Objects
│   ├── UserDAO.java
│   ├── ProductDAO.java            # [TO CREATE]
│   ├── ProviderDAO.java           # [TO CREATE]
│   └── CategoryDAO.java           # [TO CREATE]
│
├── model/                         # Domain Models (POJOs)
│   ├── User.java
│   ├── Role.java
│   ├── Product.java
│   ├── Provider.java
│   ├── Category.java
│   ├── CardInfo.java
│   ├── Order.java
│   ├── ProductLog.java
│   └── WalletTransaction.java
│
├── filter/                        # HTTP Filters
│   ├── SessionFilter.java
│   ├── AdminFilter.java
│   └── StaffFilter.java
│
└── util/                          # Utilities
    ├── DBContext.java             # Database connection
    └── PasswordUtil.java          # SHA-256 hashing
```

---

## 5. Database Schema

### 5.1 Entity Relationship Diagram (ERD)

```
┌──────────────┐       ┌──────────────┐
│    Role      │       │   Category   │
├──────────────┤       ├──────────────┤
│ id (PK)      │       │ id (PK)      │
│ name         │       │ name         │
│ description  │       │ description  │
└──────┬───────┘       │ status       │
       │               └──────┬───────┘
       │                      │
       ▼                      │
┌──────────────┐              │       ┌──────────────┐
│    User      │              │       │   Provider   │
├──────────────┤              │       ├──────────────┤
│ id (PK)      │              │       │ id (PK)      │
│ email        │              │       │ name         │
│ password_hash│              │       │ contact_info │
│ full_name    │              │       │ status       │
│ phone        │              │       └──────┬───────┘
│ address      │              │              │
│ status       │              ▼              ▼
│ wallet_balance│       ┌──────────────────────────┐
│ role_id (FK) │───────>│       Product            │
│ created_at   │        ├──────────────────────────┤
│ updated_at   │        │ id (PK)                  │
└──────┬───────┘        │ category_id (FK)         │
       │                │ provider_id (FK)         │
       │                │ name                     │
       │                │ description              │
       │                │ image_url                │
       │                │ cost_price               │
       │                │ sell_price               │
       │                │ discount_percent         │
       │                │ quantity                 │
       │                │ status                   │
       │                └──────────┬───────────────┘
       │                           │
       │                           ▼
       │                ┌──────────────────────────┐
       │                │      CardInfo            │
       │                ├──────────────────────────┤
       │                │ id (PK)                  │
       │                │ product_id (FK)          │
       │                │ code                     │
       │                │ serial                   │
       │                │ expiry_date              │
       │                │ status                   │
       │                │ created_at               │
       │                │ updated_at               │
       │                └──────────┬───────────────┘
       │                           │
       ▼                           ▼
┌──────────────────────────────────────────────────┐
│                    Order                          │
├──────────────────────────────────────────────────┤
│ id (PK)                                          │
│ user_id (FK)                                     │
│ cardinfo_id (FK) [UNIQUE]                        │
│ created_at                                       │
│ original_price                                   │
│ discount_percent                                 │
│ final_price                                      │
│ status                                           │
│ receiver_email                                   │
└──────────────────────────────────────────────────┘
```

### 5.2 Status Values Reference

| Entity | Status Values |
|--------|---------------|
| User | `ACTIVE`, `LOCKED`, `INACTIVE` |
| Product | `ACTIVE`, `INACTIVE` |
| Category | `ACTIVE`, `INACTIVE` |
| Provider | `ACTIVE`, `INACTIVE` |
| CardInfo | `AVAILABLE`, `SOLD`, `EXPIRED`, `INACTIVE` |
| Order | `PENDING`, `PAID`, `COMPLETED`, `CANCELED`, `REFUNDED` |
| WalletTransaction | `PENDING`, `SUCCESS`, `FAILED` |

---

## 6. Feature Summary

### 6.1 Features to Implement

> **Note:** Product, Provider, and Category Management features are **Staff role** features, accessible via `/staff/*` URLs.

| Feature | Priority | Role | Design Doc |
|---------|----------|------|------------|
| Product Management | High | Staff | `01-PRODUCT-DESIGN.md` |
| Provider Management | High | Staff | `02-PROVIDER-DESIGN.md` |
| Category Management | High | Staff | `03-CATEGORY-DESIGN.md` |
| UI/UX Guidelines | High | All | `04-UI-DESIGN.md` |

### 6.2 CRUD Operations Pattern (Staff Features)

Each staff feature (Product, Provider, Category) follows the same pattern:

| Operation | HTTP Method | URL Pattern | Controller |
|-----------|-------------|-------------|------------|
| List/Search | GET | `/staff/{entity}` | `{Entity}ListController` |
| View Detail | GET | `/staff/{entity}/detail?id=X` | `{Entity}DetailController` |
| Add Form | GET | `/staff/{entity}/add` | `{Entity}AddController` |
| Add Submit | POST | `/staff/{entity}/add` | `{Entity}AddController` |
| Edit Form | GET | `/staff/{entity}/edit?id=X` | `{Entity}EditController` |
| Edit Submit | POST | `/staff/{entity}/edit` | `{Entity}EditController` |
| Delete | POST | `/staff/{entity}/delete` | `{Entity}DeleteController` |

---

## 7. UI/UX Design Guidelines

### 7.1 Layout Structure

```
┌─────────────────────────────────────────────────────────────────┐
│                        HEADER                                    │
│  [Logo] [Search]              [Notifications] [User Dropdown]   │
├──────────────────┬──────────────────────────────────────────────┤
│                  │                                              │
│    SIDEBAR       │              CONTENT AREA                    │
│                  │                                              │
│  - Dashboard     │  ┌────────────────────────────────────────┐  │
│  - Products      │  │  Page Header (Title + Breadcrumb)      │  │
│    - List        │  ├────────────────────────────────────────┤  │
│    - Add         │  │                                        │  │
│  - Categories    │  │  Card Container                        │  │
│  - Providers     │  │    - Search/Filter                     │  │
│  - Orders        │  │    - DataTable                         │  │
│  - Users         │  │                                        │  │
│  - Settings      │  │                                        │  │
│                  │  └────────────────────────────────────────┘  │
│                  │                                              │
└──────────────────┴──────────────────────────────────────────────┘
```

### 7.2 Design Principles

1. **Consistency**: Use Bootstrap 5 components consistently
2. **Responsiveness**: Mobile-first approach with breakpoints
3. **Accessibility**: Proper labels, ARIA attributes
4. **Feedback**: Loading states, success/error messages
5. **Simplicity**: Clean forms, minimal steps

### 7.3 Color Scheme

| Color | Hex | Usage |
|-------|-----|-------|
| Primary | `#FF9F43` | Buttons, links, active states |
| Success | `#28C76F` | Success messages, active badges |
| Danger | `#EA5455` | Delete actions, error messages |
| Warning | `#FF9F43` | Warning messages |
| Info | `#00CFE8` | Information badges |
| Dark | `#1E1E2D` | Sidebar, headers |
| Light | `#F8F9FA` | Backgrounds |

---

## 8. Related Documents

| Document | Description |
|----------|-------------|
| `01-PRODUCT-DESIGN.md` | Product feature detailed design |
| `02-PROVIDER-DESIGN.md` | Provider feature detailed design |
| `03-CATEGORY-DESIGN.md` | Category feature detailed design |
| `04-UI-DESIGN.md` | UI/UX components and guidelines |

---

## Appendix A: Coding Conventions

### Controller Pattern (Staff Feature Example)

```java
@WebServlet(name = "EntityListController", urlPatterns = {"/staff/entity"})
public class EntityListController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Get parameters
        String search = Optional.ofNullable(request.getParameter("search")).orElse("");
        
        // 2. Call DAO
        EntityDAO dao = new EntityDAO();
        List<Entity> list = dao.search(search);
        
        // 3. Set attributes
        request.setAttribute("list", list);
        
        // 4. Forward to JSP (uses staff.jsp as master layout)
        request.setAttribute("contentPage", "staff-entity.jsp");
        request.getRequestDispatcher("/staff.jsp").forward(request, response);
    }
}
```

### DAO Pattern

```java
public class EntityDAO extends DBContext {
    
    PreparedStatement stm;
    ResultSet rs;
    
    public List<Entity> getAll() {
        List<Entity> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM Entity WHERE status = 'ACTIVE'";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                Entity e = new Entity();
                e.setId(rs.getLong("id"));
                // ... map other fields
                list.add(e);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }
}
```

---

**End of Summary Design Document**
