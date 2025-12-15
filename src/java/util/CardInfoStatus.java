package util;

/**
 * CardInfo status constants and transitions.
 */
public class CardInfoStatus {
    // Status values
    public static final String AVAILABLE = "AVAILABLE";
    public static final String SOLD = "SOLD";
    public static final String EXPIRED = "EXPIRED";
    public static final String INACTIVE = "INACTIVE";

    public static final String[] ALL_STATUSES = { AVAILABLE, SOLD, EXPIRED, INACTIVE };

    /**
     * Check if status is valid.
     */
    public static boolean isValid(String status) {
        for (String s : ALL_STATUSES) {
            if (s.equals(status)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Display text for status.
     */
    public static String getDisplayName(String status) {
        return switch (status) {
            case AVAILABLE -> "Có sẵn";
            case SOLD -> "Đã bán";
            case EXPIRED -> "Hết hạn";
            case INACTIVE -> "Không hoạt động";
            default -> status;
        };
    }
}
