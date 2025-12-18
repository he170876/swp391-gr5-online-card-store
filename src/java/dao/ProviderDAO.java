package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Provider;
import dto.ProviderSearchDTO;
import util.DBContext;

public class ProviderDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;

    public List<Provider> listAll() {
        List<Provider> result = new ArrayList<>();
        String sql = "SELECT id, name FROM Provider";
        try ( PreparedStatement stm = connection.prepareStatement(sql);  ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                Provider provider = new Provider();
                provider.setId(rs.getLong("id"));
                provider.setName(rs.getString("name"));
                result.add(provider);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return result;
    }

    // =========================================
    // GET PROVIDER BY ID (with product count)
    // =========================================
    public Provider findById(long id) {
        try {
            String sql = "SELECT p.id, p.name, p.contact_info, p.status, "
                    + "(SELECT COUNT(*) FROM Product WHERE provider_id = p.id) AS product_count "
                    + "FROM Provider p WHERE p.id = ?";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                return mapResultSetToProviderWithCount(rs);
            }
        } catch (Exception e) {
            System.out.println("ProviderDAO.findById: " + e.getMessage());
        }
        return null;
    }

    // =========================================
    // GET ALL PROVIDERS (with product count)
    // =========================================
    public List<Provider> getAll() {
        List<Provider> list = new ArrayList<>();
        try {
            String sql = "SELECT p.id, p.name, p.contact_info, p.status, "
                    + "(SELECT COUNT(*) FROM Product WHERE provider_id = p.id) AS product_count "
                    + "FROM Provider p ORDER BY p.id DESC";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProviderWithCount(rs));
            }
        } catch (Exception e) {
            System.out.println("ProviderDAO.getAll: " + e.getMessage());
        }
        return list;
    }

    public List<Provider> findAll() {
        List<Provider> providers = new ArrayList<>();
        try {
            String sql = "SELECT id, name, contact_info, status FROM Provider ORDER BY name";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                providers.add(mapResultSetToProvider(rs));
            }
        } catch (Exception e) {
            System.out.println("ProviderDAO.findAll: " + e.getMessage());
        }
        return providers;
    }

    public List<Provider> findActive() {
        List<Provider> providers = new ArrayList<>();
        try {
            String sql = "SELECT id, name, contact_info, status FROM Provider WHERE status = 'ACTIVE' ORDER BY name";
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                providers.add(mapResultSetToProvider(rs));
            }
        } catch (Exception e) {
            System.out.println("ProviderDAO.findActive: " + e.getMessage());
        }
        return providers;
    }

    // =========================================
    // SEARCH & FILTER WITH PAGINATION
    // =========================================
    public List<Provider> search(ProviderSearchDTO dto) {
        List<Provider> list = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder(
                "SELECT p.id, p.name, p.contact_info, p.status, "
                + "(SELECT COUNT(*) FROM Product WHERE provider_id = p.id) AS product_count "
                + "FROM Provider p WHERE 1=1 ");
            
            List<Object> params = new ArrayList<>();
            
            // Dynamic WHERE clauses
            if (dto.getKeyword() != null && !dto.getKeyword().trim().isEmpty()) {
                sql.append(" AND (p.name LIKE ? OR p.contact_info LIKE ?)");
                params.add("%" + dto.getKeyword().trim() + "%");
                params.add("%" + dto.getKeyword().trim() + "%");
            }
            
            if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
                sql.append(" AND p.status = ?");
                params.add(dto.getStatus().trim());
            }
            
            // Sorting
            String sortColumn;
            switch (dto.getSortBy()) {
                case "name":
                    sortColumn = "p.name";
                    break;
                case "status":
                    sortColumn = "p.status";
                    break;
                default:
                    sortColumn = "p.id";
            }
            sql.append(" ORDER BY ").append(sortColumn);
            sql.append("ASC".equalsIgnoreCase(dto.getSortDir()) ? " ASC" : " DESC");
            
            // Pagination
            sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            params.add((dto.getPage() - 1) * dto.getPageSize());
            params.add(dto.getPageSize());
            
            stm = connection.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stm.setObject(i + 1, params.get(i));
            }
            
            rs = stm.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProviderWithCount(rs));
            }
        } catch (Exception e) {
            System.out.println("ProviderDAO.search: " + e.getMessage());
        }
        return list;
    }

    // =========================================
    // COUNT FOR PAGINATION
    // =========================================
    public int count(ProviderSearchDTO dto) {
        try {
            StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM Provider p WHERE 1=1 ");
            
            List<Object> params = new ArrayList<>();
            
            if (dto.getKeyword() != null && !dto.getKeyword().trim().isEmpty()) {
                sql.append(" AND (p.name LIKE ? OR p.contact_info LIKE ?)");
                params.add("%" + dto.getKeyword().trim() + "%");
                params.add("%" + dto.getKeyword().trim() + "%");
            }
            
            if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
                sql.append(" AND p.status = ?");
                params.add(dto.getStatus().trim());
            }
            
            stm = connection.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stm.setObject(i + 1, params.get(i));
            }
            
            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("ProviderDAO.count: " + e.getMessage());
        }
        return 0;
    }

    // =========================================
    // INSERT NEW PROVIDER
    // =========================================
    public long insert(Provider p) {
        try {
            String sql = "INSERT INTO Provider (name, contact_info, status) VALUES (?, ?, ?)";
            stm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, p.getName());
            stm.setString(2, p.getContactInfo());
            stm.setString(3, p.getStatus());
            
            int rows = stm.executeUpdate();
            if (rows > 0) {
                rs = stm.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (Exception e) {
            System.out.println("ProviderDAO.insert: " + e.getMessage());
        }
        return -1;
    }

    // =========================================
    // UPDATE PROVIDER
    // =========================================
    public boolean update(Provider p) {
        try {
            String sql = "UPDATE Provider SET name = ?, contact_info = ?, status = ? WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, p.getName());
            stm.setString(2, p.getContactInfo());
            stm.setString(3, p.getStatus());
            stm.setLong(4, p.getId());
            
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ProviderDAO.update: " + e.getMessage());
        }
        return false;
    }

    // =========================================
    // SOFT DELETE (Set status to INACTIVE)
    // =========================================
    public boolean delete(long id) {
        try {
            String sql = "UPDATE Provider SET status = 'INACTIVE' WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ProviderDAO.delete: " + e.getMessage());
        }
        return false;
    }

    // =========================================
    // CHECK NAME EXISTS (for validation)
    // =========================================
    public boolean isNameExists(String name, Long excludeId) {
        try {
            String sql = "SELECT 1 FROM Provider WHERE name = ? AND id != ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, name);
            stm.setLong(2, excludeId == null ? -1 : excludeId);
            rs = stm.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("ProviderDAO.isNameExists: " + e.getMessage());
        }
        return false;
    }

    // =========================================
    // CHECK IF PROVIDER HAS ACTIVE PRODUCTS
    // =========================================
    public boolean hasProducts(long providerId) {
        try {
            String sql = "SELECT 1 FROM Product WHERE provider_id = ? AND status = 'ACTIVE'";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, providerId);
            rs = stm.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("ProviderDAO.hasProducts: " + e.getMessage());
        }
        return false;
    }

    // =========================================
    // HELPER: Map ResultSet to Provider (basic)
    // =========================================
    private Provider mapResultSetToProvider(ResultSet rs) throws Exception {
        Provider provider = new Provider();
        provider.setId(rs.getLong("id"));
        provider.setName(rs.getString("name"));
        provider.setContactInfo(rs.getString("contact_info"));
        provider.setStatus(rs.getString("status"));
        return provider;
    }

    // =========================================
    // HELPER: Map ResultSet to Provider (with product count)
    // =========================================
    private Provider mapResultSetToProviderWithCount(ResultSet rs) throws Exception {
        Provider provider = new Provider();
        provider.setId(rs.getLong("id"));
        provider.setName(rs.getString("name"));
        provider.setContactInfo(rs.getString("contact_info"));
        provider.setStatus(rs.getString("status"));
        provider.setProductCount(rs.getInt("product_count"));
        return provider;
    }
}
