-- Tạo bảng DepartmentMeetingParticipant cho lưu thông tin người tham gia cuộc họp
CREATE TABLE IF NOT EXISTS DepartmentMeetingParticipant (
    ParticipantID INT PRIMARY KEY AUTO_INCREMENT,
    MeetingID INT NOT NULL,
    UserID VARCHAR(10) NOT NULL,
    Status ENUM('Confirmed', 'Declined', 'Pending') DEFAULT 'Pending',
    JoinedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MeetingID) REFERENCES DepartmentMeeting(DepartmentMeetingID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- Chỉ mục để tăng tốc độ truy vấn
CREATE INDEX idx_meeting_participant ON DepartmentMeetingParticipant(MeetingID, UserID);
