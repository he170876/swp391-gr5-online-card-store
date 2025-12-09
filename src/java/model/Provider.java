package model;

public class Provider {
    private long id;
    private String name;
    private String contactInfo;
    private String status;

    public Provider() {}

    public Provider(long id, String name, String contactInfo, String status) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
        this.status = status;
    }

    // Getters & Setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Provider{id=" + id + ", name='" + name + "'}";
    }
}
