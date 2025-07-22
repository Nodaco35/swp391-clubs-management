package models;

/**
 * Model class to hold Google user information from OAuth2
 */
public class GoogleUserInfo {
    private String id;
    private String email;
    private String name;
    private String picture;
    private boolean verified_email;
    
    public GoogleUserInfo() {}
    
    public GoogleUserInfo(String id, String email, String name, String picture, boolean verified_email) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.verified_email = verified_email;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPicture() {
        return picture;
    }
    
    public void setPicture(String picture) {
        this.picture = picture;
    }
    
    public boolean isVerified_email() {
        return verified_email;
    }
    
    public void setVerified_email(boolean verified_email) {
        this.verified_email = verified_email;
    }
    
    /**
     * Check if email domain is allowed (only fpt.edu.vn)
     * @return true if email domain is fpt.edu.vn
     */
    public boolean isValidDomain() {
        return email != null && email.endsWith("@fpt.edu.vn");
    }
    
    @Override
    public String toString() {
        return "GoogleUserInfo{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", picture='" + picture + '\'' +
                ", verified_email=" + verified_email +
                '}';
    }
}
