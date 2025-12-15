package dto;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO for provider create/edit form
 */
public class ProviderFormDTO {
    private Long id;                // null for create, populated for edit
    private String name;
    private String contactInfo;
    private String status;
    
    // Validation errors
    private Map<String, String> errors = new HashMap<>();

    public ProviderFormDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public void addError(String field, String message) {
        errors.put(field, message);
    }
}
