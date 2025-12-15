package util;

/**
 * Order status constants and allowed transitions.
 */
public class OrderStatus {
    // Status values
    public static final String PENDING = "PENDING";
    public static final String PAID = "PAID";
    public static final String COMPLETED = "COMPLETED";
    public static final String CANCELED = "CANCELED";
    public static final String REFUNDED = "REFUNDED";

    public static final String[] ALL_STATUSES = { PENDING, PAID, COMPLETED, CANCELED, REFUNDED };

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
     * Check if transition from->to is allowed.
     * State machine:
     * PENDING -> PAID, CANCELED
     * PAID -> COMPLETED, REFUNDED
     * COMPLETED -> REFUNDED
     * CANCELED, REFUNDED -> (no more transitions)
     */
    public static boolean isAllowedTransition(String fromStatus, String toStatus) {
        if (fromStatus == null || toStatus == null) {
            return false;
        }
        if (fromStatus.equals(toStatus)) {
            return false; // no change
        }
        return switch (fromStatus) {
            case PENDING -> toStatus.equals(PAID) || toStatus.equals(CANCELED);
            case PAID -> toStatus.equals(COMPLETED) || toStatus.equals(REFUNDED);
            case COMPLETED -> toStatus.equals(REFUNDED);
            case CANCELED, REFUNDED -> false; // terminal states
            default -> false;
        };
    }

    /**
     * Display text for status.
     */
    public static String getDisplayName(String status) {
        return switch (status) {
            case PENDING -> "Chờ thanh toán";
            case PAID -> "Đã thanh toán";
            case COMPLETED -> "Hoàn thành";
            case CANCELED -> "Hủy";
            case REFUNDED -> "Hoàn tiền";
            default -> status;
        };
    }

    /**
     * Check if resending card code is allowed for this status.
     */
    public static boolean canResendCard(String status) {
        return status.equals(PAID) || status.equals(COMPLETED);
    }
}
