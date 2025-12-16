package service;

import dao.CardInfoDAO;
import dao.ProductDAO;
import dao.ProviderDAO;
import dao.CardInfoDAO.CardInfoListView;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Product;
import model.Provider;
import util.CardInfoStatus;

/**
 * Service layer for card info listing and filters.
 */
public class CardInfoService {

    private final CardInfoDAO cardInfoDAO = new CardInfoDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final ProviderDAO providerDAO = new ProviderDAO();

    public List<CardInfoListView> search(String status, Long productId, Long providerId,
            LocalDate expiryFrom, LocalDate expiryTo, String sort) {
        return cardInfoDAO.search(status, productId, providerId, expiryFrom, expiryTo, sort);
    }

    public List<Product> listProducts() {
        return productDAO.listAll();
    }

    public Map<Long, Provider> mapProviders() {
        Map<Long, Provider> result = new HashMap<>();
        for (Provider provider : providerDAO.listAll()) {
            result.put(provider.getId(), provider);
        }
        return result;
    }

    public String defaultStatus(String status) {
        if (status == null || status.isBlank()) {
            return "";
        }
        for (String s : CardInfoStatus.ALL_STATUSES) {
            if (s.equalsIgnoreCase(status)) {
                return s;
            }
        }
        return "";
    }
}
