package service;

import dao.CardInfoDAO;
import dao.ProductDAO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import model.CardInfo;
import model.Product;
import util.CardInfoStatus;

/**
 * Service for importing card codes from CSV.
 */
public class CardCodeImportService {

    private final CardInfoDAO cardInfoDAO = new CardInfoDAO();
    private final ProductDAO productDAO = new ProductDAO();

    public ImportResult importCsv(long productId, InputStream inputStream) throws IOException {
        ImportResult result = new ImportResult();
        if (inputStream == null) {
            result.getErrors().add(new ImportError(0, "Không tìm thấy file upload"));
            return result;
        }

        Product product = productDAO.getById(productId);
        if (product == null) {
            result.getErrors().add(new ImportError(0, "productId không tồn tại"));
            return result;
        }

        List<CardInfo> validCards = new ArrayList<>();
        Set<String> codesInFile = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                row++;
                String rawLine = line.trim();
                if (rawLine.isEmpty()) {
                    continue;
                }
                String[] parts = rawLine.split(",");
                if (parts.length < 3) {
                    result.getErrors()
                            .add(new ImportError(row, "Sai định dạng, cần đủ 3 cột: code, serial, expiry_date"));
                    result.increaseTotal();
                    continue;
                }

                String code = parts[0].trim();
                String serial = parts[1].trim().toUpperCase();
                String expiryRaw = parts[2].trim();

                // Skip header row if present
                if (row == 1 && "code".equalsIgnoreCase(code) && "serial".equalsIgnoreCase(serial)) {
                    continue;
                }

                result.increaseTotal();

                if (code.isEmpty() || serial.isEmpty()) {
                    result.getErrors().add(new ImportError(row, "Code/Serial không được trống"));
                    continue;
                }

                LocalDate expiry = null;
                if (!expiryRaw.isEmpty()) {
                    try {
                        expiry = LocalDate.parse(expiryRaw);
                    } catch (DateTimeParseException e) {
                        result.getErrors().add(new ImportError(row, "Ngày hết hạn không hợp lệ (yyyy-MM-dd)"));
                        continue;
                    }
                }

                if (codesInFile.contains(code)) {
                    result.getErrors().add(new ImportError(row, "Trùng code trong file"));
                    continue;
                }

                codesInFile.add(code);
                CardInfo card = new CardInfo();
                card.setProductId(productId);
                card.setCode(code);
                card.setSerial(serial);
                card.setExpiryDate(expiry);
                card.setStatus(CardInfoStatus.AVAILABLE);
                validCards.add(card);
                result.increaseValid();
            }
        }

        // Check duplicates in DB
        Set<String> codeSet = validCards.stream().map(CardInfo::getCode).collect(Collectors.toSet());
        List<String> existing = cardInfoDAO.findExistingCodes(codeSet);
        Set<String> existingSet = new HashSet<>(existing);

        List<CardInfo> toInsert = new ArrayList<>();
        for (CardInfo card : validCards) {
            if (existingSet.contains(card.getCode())) {
                result.increaseSkippedExisting();
                result.getErrors().add(new ImportError(0, "Code đã tồn tại: " + card.getCode()));
            } else {
                toInsert.add(card);
            }
        }

        int inserted = cardInfoDAO.bulkInsert(toInsert);
        result.setInsertedRows(inserted);
        if (inserted == 0 && !toInsert.isEmpty()) {
            result.getErrors()
                    .add(new ImportError(0, "Không chèn được vào DB (kiểm tra productId và ràng buộc dữ liệu)"));
        }
        return result;
    }

    // DTOs for result reporting
    public static class ImportResult {
        private int totalRows;
        private int validRows;
        private int insertedRows;
        private int skippedExisting;
        private final List<ImportError> errors = new ArrayList<>();

        public int getTotalRows() {
            return totalRows;
        }

        public void increaseTotal() {
            this.totalRows++;
        }

        public int getValidRows() {
            return validRows;
        }

        public void increaseValid() {
            this.validRows++;
        }

        public int getInsertedRows() {
            return insertedRows;
        }

        public void setInsertedRows(int insertedRows) {
            this.insertedRows = insertedRows;
        }

        public int getSkippedExisting() {
            return skippedExisting;
        }

        public void increaseSkippedExisting() {
            this.skippedExisting++;
        }

        public List<ImportError> getErrors() {
            return errors;
        }
    }

    public static class ImportError {
        private final int row;
        private final String message;

        public ImportError(int row, String message) {
            this.row = row;
            this.message = message;
        }

        public int getRow() {
            return row;
        }

        public String getMessage() {
            return message;
        }
    }
}
