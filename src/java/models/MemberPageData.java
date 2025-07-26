package models;

import java.util.List;

/**
 * 🚀 ĐƠN GIẢN HÓA: Aggregate DTO class để chứa tất cả data cần thiết cho member page
 * Giảm từ nhiều method calls xuống 1 method call duy nhất
 */
public class MemberPageData {
    private List<DepartmentMember> members;
    private MemberStatistics statistics;
    private int currentPage;
    private int pageSize;
    private int totalMembers;
    private int totalPages;
    private String searchKeyword;
    
    // Constructors
    public MemberPageData() {
    }
    
    // Getters and Setters
    public List<DepartmentMember> getMembers() {
        return members;
    }
    
    public void setMembers(List<DepartmentMember> members) {
        this.members = members;
    }
    
    public MemberStatistics getStatistics() {
        return statistics;
    }
    
    public void setStatistics(MemberStatistics statistics) {
        this.statistics = statistics;
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    public int getTotalMembers() {
        return totalMembers;
    }
    
    public void setTotalMembers(int totalMembers) {
        this.totalMembers = totalMembers;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
    public String getSearchKeyword() {
        return searchKeyword;
    }
    
    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
    
    // Utility methods
    public boolean hasMembers() {
        return members != null && !members.isEmpty();
    }
    
    public boolean isSearch() {
        return searchKeyword != null && !searchKeyword.trim().isEmpty();
    }
    
    public boolean hasPagination() {
        return totalPages > 1;
    }
    
    @Override
    public String toString() {
        return String.format("MemberPageData{members=%d, page=%d/%d, search='%s', stats=%s}", 
                           members != null ? members.size() : 0, currentPage, totalPages, 
                           searchKeyword, statistics);
    }
}
