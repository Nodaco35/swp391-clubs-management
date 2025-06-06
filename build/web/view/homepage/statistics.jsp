<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<section class="section" style="background-color: #f5f5f5;">
    <div class="container">
        <div class="section-header">
            <h2 class="section-title">Thống Kê</h2>
            <p class="section-description">Những con số ấn tượng về cộng đồng câu lạc bộ của chúng tôi</p>
        </div>
        
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon">
                    <i class="fas fa-users"></i>
                </div>
                <div class="stat-value">${totalClubs}</div>
                <div class="stat-label">Câu Lạc Bộ</div>
            </div>
            
            <div class="stat-card">
                <div class="stat-icon">
                    <i class="fas fa-user-graduate"></i>
                </div>
                <div class="stat-value">${totalMembers}</div>
                <div class="stat-label">Thành Viên</div>
            </div>
            
            <div class="stat-card">
                <div class="stat-icon">
                    <i class="fas fa-calendar-alt"></i>
                </div>
                <div class="stat-value">${totalEvents}</div>
                <div class="stat-label">Sự Kiện</div>
            </div>
            
            <div class="stat-card">
                <div class="stat-icon">
                    <i class="fas fa-school"></i>
                </div>
                <div class="stat-value">${totalDepartments}</div>
                <div class="stat-label">Ban Chuyên Môn</div>
            </div>
        </div>
    </div>
</section>