package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Provider;
import util.DBContext;

public class ProviderDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;

    public Provider findById(long id) {
        try {
            String sql = "SELECT id, name, contact_info, status FROM Provider WHERE id = ?";
            stm = connection.prepareStatement(sql);
            stm.setLong(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                return mapResultSetToProvider(rs);
            }
        } catch (Exception e) {
            System.out.println("ProviderDAO.findById: " + e.getMessage());
        }
        return null;
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

    private Provider mapResultSetToProvider(ResultSet rs) throws Exception {
        Provider provider = new Provider();
        provider.setId(rs.getLong("id"));
        provider.setName(rs.getString("name"));
        provider.setContactInfo(rs.getString("contact_info"));
        provider.setStatus(rs.getString("status"));
        return provider;
    }
}


