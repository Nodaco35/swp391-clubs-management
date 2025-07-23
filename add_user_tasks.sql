-- Add some test tasks assigned to individual users for testing department tasks page
USE ClubManagementSystem;

-- Add tasks assigned to users in different departments of Club 4 (IT Club)
-- These tasks will be visible to department leaders

-- Tasks for Ban Chuyên môn (ClubDepartmentID = 13) members
USE ClubManagementSystem;
INSERT INTO Tasks (ParentTaskID, TermID, EventID, ClubID, Title, Description, Status, StartDate, EndDate, CreatedBy, AssigneeType, UserID)
VALUES
(2, 1, 11, 4, 'Code module xử lý đăng ký', 'Viết code xử lý logic đăng ký thí sinh cho Hackathon AI', 'InProgress', '2025-07-08 09:00:00', '2025-07-12 17:00:00', 'U012', 'User', 'U017'),
(2, 1, 11, 4, 'Testing và debug hệ thống', 'Kiểm tra và sửa lỗi các module đã được phát triển', 'ToDo', '2025-07-10 09:00:00', '2025-07-13 17:00:00', 'U012', 'User', 'U019'),
(2, 1, 11, 4, 'Setup database cho sự kiện', 'Thiết lập cơ sở dữ liệu lưu trữ thông tin thí sinh và kết quả', 'Done', '2025-07-06 08:00:00', '2025-07-09 17:00:00', 'U012', 'User', 'U021');

-- Tasks for Ban Truyền thông (ClubDepartmentID = 14) members  
USE ClubManagementSystem;
INSERT INTO Tasks (ParentTaskID, TermID, EventID, ClubID, Title, Description, Status, StartDate, EndDate, CreatedBy, AssigneeType, UserID)
VALUES
(1, 2, 11, 4, 'Thiết kế banner Facebook', 'Tạo banner quảng cáo trên Facebook cho sự kiện', 'Review', '2025-07-07 10:00:00', '2025-07-11 17:00:00', 'U012', 'User', 'U018'),
(1, 2, 11, 4, 'Viết content mô tả sự kiện', 'Soạn thảo nội dung mô tả chi tiết về sự kiện Hackathon AI', 'Done', '2025-07-05 14:00:00', '2025-07-08 17:00:00', 'U012', 'User', 'U020'),
(4, 3, 11, 4, 'Chụp ảnh trong sự kiện', 'Ghi lại các khoảnh khắc quan trọng của sự kiện', 'Rejected', '2025-07-15 08:00:00', '2025-07-15 18:00:00', 'U012', 'User', 'U023');

-- Tasks for Ban Nội dung (ClubDepartmentID = 15) members
USE ClubManagementSystem;
INSERT INTO Tasks (ParentTaskID, TermID, EventID, ClubID, Title, Description, Status, StartDate, EndDate, CreatedBy, AssigneeType, UserID)
VALUES
(6, 4, 12, 4, 'Soạn đề thi Code War level 1', 'Tạo đề thi cho thí sinh ở level cơ bản', 'InProgress', '2025-07-22 09:00:00', '2025-07-28 17:00:00', 'U012', 'User', 'U022'),
(6, 4, 12, 4, 'Review đề thi Code War', 'Kiểm tra và đánh giá chất lượng đề thi', 'ToDo', '2025-07-25 09:00:00', '2025-07-29 17:00:00', 'U012', 'User', 'U029');

-- Tasks for Ban Hậu cần (ClubDepartmentID = 17) members
USE ClubManagementSystem;
INSERT INTO Tasks (ParentTaskID, TermID, EventID, ClubID, Title, Description, Status, StartDate, EndDate, CreatedBy, AssigneeType, UserID)
VALUES
(NULL, 2, 11, 4, 'Chuẩn bị đồ ăn cho sự kiện', 'Đặt cơm hộp và nước uống cho thí sinh', 'Done', '2025-07-13 08:00:00', '2025-07-15 07:00:00', 'U012', 'User', 'U025'),
(NULL, 2, 11, 4, 'Setup bàn ghế phòng thi', 'Sắp xếp bàn ghế và không gian thi đấu', 'InProgress', '2025-07-14 08:00:00', '2025-07-15 07:00:00', 'U012', 'User', 'U026');

SELECT 'User tasks added successfully!' as Result;

-- ========================================
-- Tasks for Club 1, ClubDepartmentID 5 (Ban Hậu cần của CLB 1)
-- ========================================

-- Tasks for Ban Hậu cần (ClubDepartmentID = 5) members of Club 1
USE ClubManagementSystem;
INSERT INTO Tasks (ParentTaskID, TermID, EventID, ClubID, Title, Description, Status, StartDate, EndDate, CreatedBy, AssigneeType, UserID)
VALUES
-- Tasks cho U007 (thành viên)
(NULL, NULL, NULL, 1, 'Chuẩn bị dụng cụ thể thao', 'Kiểm tra và chuẩn bị bóng đá, áo thi đấu cho hoạt động tuần', 'InProgress', '2025-07-23 08:00:00', '2025-07-25 17:00:00', 'U002', 'User', 'U007'),
(NULL, NULL, NULL, 1, 'Kiểm tra sân bóng', 'Kiểm tra tình trạng sân bóng và đặt lịch sử dụng', 'ToDo', '2025-07-24 09:00:00', '2025-07-26 16:00:00', 'U002', 'User', 'U007'),

-- Tasks cho HS193994 (thành viên)
(NULL, NULL, NULL, 1, 'Tổ chức dọn dẹp phòng CLB', 'Dọn dẹp và sắp xếp lại không gian hoạt động của câu lạc bộ', 'ToDo', '2025-07-24 14:00:00', '2025-07-26 16:00:00', 'U002', 'User', 'HS193994'),
(NULL, NULL, NULL, 1, 'Sắp xếp tủ đồ dùng', 'Phân loại và sắp xếp lại các đồ dùng trong tủ', 'InProgress', '2025-07-23 10:00:00', '2025-07-25 15:00:00', 'U002', 'User', 'HS193994'),

-- Tasks cho HA180043 (thành viên)
(NULL, NULL, NULL, 1, 'Mua sắm đồ ăn nhẹ cho buổi họp', 'Chuẩn bị đồ ăn nhẹ và nước uống cho buổi họp hàng tuần', 'Done', '2025-07-22 09:00:00', '2025-07-23 12:00:00', 'U002', 'User', 'HA180043'),
(NULL, NULL, NULL, 1, 'Lập danh sách mua sắm tháng', 'Tổng hợp nhu cầu và lập danh sách mua sắm cho tháng tới', 'Review', '2025-07-23 13:00:00', '2025-07-25 17:00:00', 'U002', 'User', 'HA180043'),

-- Tasks cho HA193827 (thành viên)
(NULL, NULL, NULL, 1, 'Kiểm tra tình trạng ghế bàn', 'Kiểm tra và sửa chữa bàn ghế hư hỏng trong phòng sinh hoạt', 'Review', '2025-07-23 10:00:00', '2025-07-24 17:00:00', 'U002', 'User', 'HA193827'),
(NULL, NULL, NULL, 1, 'Bảo trì thiết bị âm thanh', 'Kiểm tra và vệ sinh hệ thống âm thanh của CLB', 'ToDo', '2025-07-25 08:00:00', '2025-07-27 16:00:00', 'U002', 'User', 'HA193827'),

-- Tasks cho HS191748 (thành viên)
(NULL, NULL, NULL, 1, 'Chuẩn bị backdrop cho sự kiện', 'Thiết kế và in backdrop cho sự kiện sắp tới của CLB', 'InProgress', '2025-07-25 08:00:00', '2025-07-28 17:00:00', 'U002', 'User', 'HS191748'),
(NULL, NULL, NULL, 1, 'Trang trí không gian sinh hoạt', 'Trang trí lại không gian sinh hoạt cho phù hợp với hoạt động mới', 'ToDo', '2025-07-26 09:00:00', '2025-07-29 17:00:00', 'U002', 'User', 'HS191748');

SELECT 'Tasks for Club 1 ClubDepartmentID 5 added successfully!' as Result;
