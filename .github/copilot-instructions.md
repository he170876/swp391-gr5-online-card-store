# OCS - Online Card Store: Copilot Instructions

## Project Overview
Jakarta EE 10 web application for selling digital cards (gift cards, game cards). Uses classic MVC pattern with Servlets, JSP, and SQL Server.

**Tech Stack:** Java 17, Tomcat 10, SQL Server (database: `ocs`), Bootstrap 5, JSTL, NetBeans/Ant build

## Architecture Patterns

### Layer Structure
```
Controller (@WebServlet) → DAO (extends DBContext) → SQL Server
    ↓
JSP Views (with JSTL)
```

### Key Conventions

**Controllers** - Use `@WebServlet` annotation with `/admin/{entity}` pattern:
```java
@WebServlet(name = "EntityListController", urlPatterns = {"/admin/entity"})
public class EntityListController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        EntityDAO dao = new EntityDAO();
        request.setAttribute("list", dao.getAll());
        request.setAttribute("contentPage", "admin-entity.jsp");
        request.getRequestDispatcher("/admin.jsp").forward(request, response);
    }
}
```

**DAOs** - Always extend `DBContext`, use instance fields `stm` and `rs`:
```java
public class EntityDAO extends DBContext {
    private PreparedStatement stm;
    private ResultSet rs;
    
    public Entity findById(long id) {
        try {
            String sql = "SELECT * FROM Entity WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            rs = stm.executeQuery();
            if (rs.next()) return map(rs);
        } catch (Exception e) {
            System.out.println("EntityDAO.findById: " + e);
        }
        return null;
    }
}
```

**Models** - Plain POJOs with getters/setters. Map SQL columns with snake_case to Java camelCase.

## Role-Based Access Control

| Role ID | Name | Access |
|---------|------|--------|
| 1 | ADMIN | `/admin/*`, `/admin.jsp` |
| 2 | STAFF | `/staff/*`, `/staff.jsp` |
| 3 | CUSTOMER | Customer pages |

**Filters** implement role checks - see [OnlyAdminFilter.java](src/java/filter/OnlyAdminFilter.java):
- Check `session.getAttribute("user")` for login
- Check `user.getRoleId()` for authorization
- Use `@WebFilter(urlPatterns = {...})` to protect routes

## Database Conventions

- Table `[User]` uses brackets due to reserved word
- All tables use `BIGINT IDENTITY(1,1)` for primary keys
- Status fields use CHECK constraints: `ACTIVE`, `INACTIVE`, `LOCKED`, etc.
- Passwords stored as SHA-256 hash via `PasswordUtil.hash()`
- Default test password: `123456` (hash: `8d969eef6ecad3c29a...`)

**Connection** ([DBContext.java](src/java/util/DBContext.java)):
```
jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=ocs
```

## Admin UI Pattern

Admin pages use a master layout (`admin.jsp`) with dynamic content:
1. Controller sets `contentPage` attribute to the content JSP name
2. Controller sets `active` attribute for sidebar highlighting
3. Main template includes content via `<jsp:include page="${contentPage}" />`

```java
request.setAttribute("pageTitle", "Dashboard");
request.setAttribute("active", "dashboard");
request.setAttribute("contentPage", "admin-dashboard.jsp");
request.getRequestDispatcher("/admin.jsp").forward(request, response);
```

## File Organization

| Directory | Purpose |
|-----------|---------|
| `src/java/controller/` | Servlet controllers |
| `src/java/dao/` | Data access objects |
| `src/java/model/` | POJOs/entities |
| `src/java/filter/` | Security filters |
| `src/java/service/` | Business logic (Auth, Email, Wallet) |
| `src/java/util/` | Utilities (DBContext, PasswordUtil) |
| `web/` | JSP pages, assets |
| `web/template/` | HTML templates (reference only) |
| `database/` | SQL scripts |
| `docs/design/` | Feature design documents |

## Build & Run

**NetBeans:** Right-click project → Run (deploys to Tomcat)

**Manual setup:**
1. Run `database/OCS.sql` in SQL Server
2. Update connection in [DBContext.java](src/java/util/DBContext.java) if needed
3. Deploy WAR to Tomcat 10

## Important Patterns

### Maintenance Mode
Check `AdminConfigController.isMaintenanceMode()` - only admin (role_id=1) can login during maintenance.

### OTP Verification
Registration requires email OTP verification. See `RegisterVerifyOTPController` and `EmailService`.

### Parameter Handling
Use `Optional.ofNullable()` for null-safe parameter reading:
```java
String email = Optional.ofNullable(request.getParameter("email")).orElse("");
```

## Design Documents
Consult `docs/design/` for detailed specifications:
- [00-SUMMARY-DESIGN.md](docs/design/00-SUMMARY-DESIGN.md) - Architecture overview
- [01-PRODUCT-DESIGN.md](docs/design/01-PRODUCT-DESIGN.md) - Product CRUD
- [02-PROVIDER-DESIGN.md](docs/design/02-PROVIDER-DESIGN.md) - Provider management
- [03-CATEGORY-DESIGN.md](docs/design/03-CATEGORY-DESIGN.md) - Category management
- [04-UI-DESIGN.md](docs/design/04-UI-DESIGN.md) - UI components
