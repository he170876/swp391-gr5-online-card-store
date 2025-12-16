package dto;

/**
 * DTO for provider search and filter parameters
 */
public class ProviderSearchDTO {
    private String keyword;         // Search in name, contact_info
    private String status;          // Filter by status
    private int page = 1;           // Pagination
    private int pageSize = 10;      // Items per page
    private String sortBy = "id";   // Sort column
    private String sortDir = "DESC"; // Sort direction

    public ProviderSearchDTO() {
    }

    // Getters and Setters
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }
}
