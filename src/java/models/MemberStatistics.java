package models;

/**
 * Simple DTO class để chứa member statistics
 * Đơn giản hóa việc lấy thống kê thành viên trong 1 query
 */
public class MemberStatistics {
    private int totalMembers;
    private int activeMembers;
    private int inactiveMembers;
    
    // Default constructor
    public MemberStatistics() {
        this.totalMembers = 0;
        this.activeMembers = 0;
        this.inactiveMembers = 0;
    }
    
    // Constructor with parameters
    public MemberStatistics(int totalMembers, int activeMembers, int inactiveMembers) {
        this.totalMembers = totalMembers;
        this.activeMembers = activeMembers;
        this.inactiveMembers = inactiveMembers;
    }
    
    // Getters and Setters
    public int getTotalMembers() {
        return totalMembers;
    }
    
    public void setTotalMembers(int totalMembers) {
        this.totalMembers = totalMembers;
    }
    
    public int getActiveMembers() {
        return activeMembers;
    }
    
    public void setActiveMembers(int activeMembers) {
        this.activeMembers = activeMembers;
    }
    
    public int getInactiveMembers() {
        return inactiveMembers;
    }
    
    public void setInactiveMembers(int inactiveMembers) {
        this.inactiveMembers = inactiveMembers;
    }
    
    // Validation method
    public boolean isValid() {
        return totalMembers == (activeMembers + inactiveMembers);
    }
    
    // Utility methods
    public double getActivePercentage() {
        if (totalMembers == 0) return 0.0;
        return (double) activeMembers / totalMembers * 100;
    }
    
    public double getInactivePercentage() {
        if (totalMembers == 0) return 0.0;
        return (double) inactiveMembers / totalMembers * 100;
    }
    
    @Override
    public String toString() {
        return String.format("MemberStatistics{total=%d, active=%d, inactive=%d}", 
                           totalMembers, activeMembers, inactiveMembers);
    }
}
