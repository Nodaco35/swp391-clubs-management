package models;

/**
 * Model class for Department Dashboard statistics
 * Contains all metrics needed for Department Leader dashboard
 * @author Department Management Module
 */
public class DepartmentDashboard {
    // Department info
    private int clubDepartmentId;
    private int departmentId;
    private String departmentName;
    private int clubId;
    private String clubName;
    
    // Member statistics
    private int totalMembers;
    private int activeMembers;
    private double memberActivityRate;
    
    // Task statistics
    private int totalTasks;
    private int todoTasks;
    private int inProgressTasks;
    private int reviewTasks;
    private int doneTasks;
    private double averageProgress;
    
    // Event statistics
    private int totalEvents;
    private int upcomingEvents;
    private int completedEvents;
    
    // Meeting statistics (placeholder for future)
    private int weeklyMeetings;

    public DepartmentDashboard() {
    }

    public DepartmentDashboard(int clubDepartmentId, int departmentId, String departmentName, 
                             int clubId, String clubName) {
        this.clubDepartmentId = clubDepartmentId;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.clubId = clubId;
        this.clubName = clubName;
    }

    // Getters and Setters for Department Info
    public int getClubDepartmentId() {
        return clubDepartmentId;
    }

    public void setClubDepartmentId(int clubDepartmentId) {
        this.clubDepartmentId = clubDepartmentId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    // Getters and Setters for Member Statistics
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

    public double getMemberActivityRate() {
        return memberActivityRate;
    }

    public void setMemberActivityRate(double memberActivityRate) {
        this.memberActivityRate = memberActivityRate;
    }

    // Getters and Setters for Task Statistics
    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public int getTodoTasks() {
        return todoTasks;
    }

    public void setTodoTasks(int todoTasks) {
        this.todoTasks = todoTasks;
    }

    public int getInProgressTasks() {
        return inProgressTasks;
    }

    public void setInProgressTasks(int inProgressTasks) {
        this.inProgressTasks = inProgressTasks;
    }

    public int getReviewTasks() {
        return reviewTasks;
    }

    public void setReviewTasks(int reviewTasks) {
        this.reviewTasks = reviewTasks;
    }

    public int getDoneTasks() {
        return doneTasks;
    }

    public void setDoneTasks(int doneTasks) {
        this.doneTasks = doneTasks;
    }

    public double getAverageProgress() {
        return averageProgress;
    }

    public void setAverageProgress(double averageProgress) {
        this.averageProgress = averageProgress;
    }

    // Getters and Setters for Event Statistics
    public int getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(int totalEvents) {
        this.totalEvents = totalEvents;
    }

    public int getUpcomingEvents() {
        return upcomingEvents;
    }

    public void setUpcomingEvents(int upcomingEvents) {
        this.upcomingEvents = upcomingEvents;
    }

    public int getCompletedEvents() {
        return completedEvents;
    }

    public void setCompletedEvents(int completedEvents) {
        this.completedEvents = completedEvents;
    }

    // Getters and Setters for Meeting Statistics
    public int getWeeklyMeetings() {
        return weeklyMeetings;
    }

    public void setWeeklyMeetings(int weeklyMeetings) {
        this.weeklyMeetings = weeklyMeetings;
    }

    // Calculated properties
    public double getTaskCompletionRate() {
        if (totalTasks == 0) return 0.0;
        return (double) doneTasks / totalTasks * 100;
    }

    public double getEventCompletionRate() {
        if (totalEvents == 0) return 0.0;
        return (double) completedEvents / totalEvents * 100;
    }

    @Override
    public String toString() {
        return "DepartmentDashboard{" +
                "clubDepartmentId=" + clubDepartmentId +
                ", departmentName='" + departmentName + '\'' +
                ", clubName='" + clubName + '\'' +
                ", totalMembers=" + totalMembers +
                ", activeMembers=" + activeMembers +
                ", totalTasks=" + totalTasks +
                ", doneTasks=" + doneTasks +
                ", averageProgress=" + averageProgress +
                '}';
    }
}
