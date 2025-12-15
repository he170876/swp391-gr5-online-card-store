package dto;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO for category create/edit form with validation errors
 */
public class CategoryFormDTO {
    private Long id;                // null for create, populated for edit
    private String name;
    private String description;
    private String status;
    
    // Validation errors
    private Map<String, String> errors = new HashMap<>();
    
    // Constructors
    public CategoryFormDTO() {}
    
    // Getters, Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Map<String, String> getErrors() { return errors; }
    public void setErrors(Map<String, String> errors) { this.errors = errors; }
    
    public void addError(String field, String message) {
        this.errors.put(field, message);
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
