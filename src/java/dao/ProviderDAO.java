package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Provider;
import util.DBContext;

/**
 * DAO for Provider table.
 */
public class ProviderDAO extends DBContext {

    public List<Provider> listAll() {
        List<Provider> result = new ArrayList<>();
        String sql = "SELECT id, name, description FROM Provider";
        try (PreparedStatement stm = connection.prepareStatement(sql);
                ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                Provider provider = new Provider();
                provider.setId(rs.getLong("id"));
                provider.setName(rs.getString("name"));
                provider.setContactInfo(rs.getString("description"));
                result.add(provider);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return result;
    }
}
