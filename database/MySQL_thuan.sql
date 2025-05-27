CREATE DATABASE ClubManagementSystem
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE ClubManagementSystem;

-- Bảng 1: Permissions
CREATE TABLE Permissions (
    PermissionID INT PRIMARY KEY AUTO_INCREMENT,
    PermissionName VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    Description VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL
);

-- Permissions
INSERT INTO Permissions (PermissionName) VALUES ('Student'), ('Admin'), ('IC_Officer');



-- Bảng 2: Users
CREATE TABLE Users (
    UserID VARCHAR(10) PRIMARY KEY,
    FullName VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    Email VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    DateOfBirth DATETIME NULL,
    PermissionID INT NOT NULL,
    Status BOOLEAN DEFAULT 1 NOT NULL,
    ResetToken VARCHAR(255) NULL,
    TokenExpiry DATETIME NULL,
    FOREIGN KEY (PermissionID) REFERENCES Permissions(PermissionID) ON DELETE CASCADE
);

-- Users
INSERT INTO Users (UserID, FullName, Email, Password, PermissionID, Status)
VALUES 
('U001', 'Nguyễn Văn A', 'a@gmail.com', '123', 1, 1),
('U003', 'Ngô Văn C', 'c@gmail.com', '456', 2, 1),
('U004', 'Lê Văn D', 'd@gmail.com', '456', 3, 1),
('U002', 'Trần Thị B', 'b@gmail.com', '123', 1, 1);




-- Bảng 3: Roles
CREATE TABLE Roles (
    RoleID INT PRIMARY KEY AUTO_INCREMENT,
    RoleName VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL
);
-- Roles
INSERT INTO Roles (RoleName) VALUES ('Chủ nhiệm'), ('Phó chủ nhiệm'), ('Thành viên'), ('Trưởng ban');



-- Bảng 4: Clubs:

CREATE TABLE Clubs (
    ClubID INT PRIMARY KEY AUTO_INCREMENT,
    ClubImg VARCHAR(255) NULL,
    IsRecruiting BOOLEAN DEFAULT 1,
    ClubName VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    Description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    Category ENUM('Thể Thao', 'Học Thuật', 'Phong Trào') NOT NULL DEFAULT 'Học Thuật',
    EstablishedDate DATE,
    ContactPhone VARCHAR(20) NULL,
    ContactGmail VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE,
    ContactURL VARCHAR(150) NULL,
    ClubStatus BOOLEAN DEFAULT 1
);
-- Thêm dữ liệu mẫu vào bảng Clubs
INSERT INTO Clubs (ClubImg, IsRecruiting, ClubName, Description, EstablishedDate, ContactPhone, ContactGmail, ContactURL, ClubStatus, Category) VALUES
('/images/clubs/football.jpg', TRUE, 'Câu Lạc Bộ Bóng Đá', 'Câu lạc bộ dành cho những người yêu thích bóng đá, tổ chức các trận đấu giao hữu và tham gia các giải đấu sinh viên.', '2018-05-10', '0901234567', 'football@club.com', 'https://facebook.com/footballclub', TRUE, 'Thể Thao'),
('/images/clubs/basketball.jpg', TRUE, 'Câu Lạc Bộ Bóng Rổ', 'Tập hợp những người đam mê bóng rổ, luyện tập và thi đấu thường xuyên.', '2019-03-15', '0912345678', 'basketball@club.com', 'https://facebook.com/basketballclub', TRUE, 'Thể Thao'),
('/images/clubs/english.jpg', TRUE, 'Câu Lạc Bộ Tiếng Anh', 'Nơi giao lưu, học hỏi và nâng cao kỹ năng tiếng Anh thông qua các hoạt động thú vị.', '2017-09-20', '0923456789', 'english@club.com', 'https://facebook.com/englishclub', TRUE, 'Học Thuật'),
('/images/clubs/programming.jpg', FALSE, 'Câu Lạc Bộ Lập Trình', 'Chia sẻ kiến thức, kinh nghiệm về lập trình và phát triển phần mềm.', '2020-01-10', '0934567890', 'programming@club.com', 'https://facebook.com/programmingclub', TRUE, 'Học Thuật'),
('/images/clubs/volunteer.jpg', TRUE, 'Câu Lạc Bộ Tình Nguyện', 'Tổ chức các hoạt động tình nguyện, giúp đỡ cộng đồng và phát triển kỹ năng mềm.', '2016-07-05', '0945678901', 'volunteer@club.com', 'https://facebook.com/volunteerclub', TRUE, 'Phong Trào'),
('/images/clubs/music.jpg', TRUE, 'Câu Lạc Bộ Âm Nhạc', 'Nơi giao lưu, học hỏi và thể hiện niềm đam mê với âm nhạc.', '2018-11-15', '0956789012', 'music@club.com', 'https://facebook.com/musicclub', TRUE, 'Phong Trào'),
('/images/clubs/dance.jpg', FALSE, 'Câu Lạc Bộ Nhảy', 'Tập hợp những người yêu thích nhảy múa, luyện tập và biểu diễn.', '2019-08-20', '0967890123', 'dance@club.com', 'https://facebook.com/danceclub', TRUE, 'Phong Trào'),
('/images/clubs/debate.jpg', TRUE, 'Câu Lạc Bộ Tranh Biện', 'Rèn luyện kỹ năng tranh biện, tư duy phản biện và khả năng thuyết trình.', '2020-02-25', '0978901234', 'debate@club.com', 'https://facebook.com/debateclub', TRUE, 'Học Thuật');



-- Bảng 5: ClubDepartments
CREATE TABLE ClubDepartments (
    DepartmentID INT PRIMARY KEY AUTO_INCREMENT,
    DepartmentName VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    DepartmentStatus BOOLEAN DEFAULT 1,
    Description VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
    ClubID INT,
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID) ON DELETE CASCADE
);
INSERT INTO ClubDepartments (DepartmentName, ClubID, Description, DepartmentStatus)
VALUES 
('Department A', 1, 'Description A', 1),
('Department B', 1, 'Description B', 1),
('Department C', 2, 'Description C', 1),
('Department D', 2, 'Description D', 1),
('Department E', 3, 'Description E', 1),
('Department F', 3, 'Description F', 1),
('Department G', 4, 'Description G', 1),
('Department H', 4, 'Description H', 1),
('Department I', 5, 'Description I', 1),
('Department J', 5, 'Description J', 1);

select * from ClubDepartments;

-- Bảng 6: UserClubs
CREATE TABLE UserClubs (
    UserClubID INT PRIMARY KEY AUTO_INCREMENT,
    UserID VARCHAR(10) NOT NULL,
    ClubID INT NOT NULL,
    DepartmentID INT NOT NULL,
    RoleID INT NOT NULL,
    JoinDate DATETIME NOT NULL,
    IsActive BOOLEAN DEFAULT 1,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID) ON DELETE CASCADE,
    FOREIGN KEY (DepartmentID) REFERENCES ClubDepartments(DepartmentID) ON DELETE CASCADE,
    FOREIGN KEY (RoleID) REFERENCES Roles(RoleID) ON DELETE CASCADE
);
-- Thêm dữ liệu mẫu vào bảng UserClubs
-- Lưu ý: Bảng UserClubs của bạn có cấu trúc khác với code ban đầu, đã điều chỉnh để phù hợp
INSERT INTO UserClubs (UserID, ClubID, DepartmentID, RoleID, JoinDate, IsActive) VALUES
('U001', 1, 1, 1, '2020-09-01', TRUE),
('U001', 3, 5, 3, '2021-01-15', TRUE),
('U002', 2, 3, 2, '2020-10-05', TRUE),
('U002', 5, 9, 3, '2021-02-20', TRUE),
('U003', 3, 6, 1, '2020-11-10', TRUE),
('U003', 4, 7, 3, '2021-03-25', TRUE),
('U004', 5, 10, 2, '2020-12-15', TRUE),
('U004', 6, 11, 3, '2021-04-30', TRUE);



-- Bảng 7: Events
CREATE TABLE Events (
    EventID INT PRIMARY KEY AUTO_INCREMENT,
    EventName VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    Description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    EventDate DATETIME,
    Location VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    ClubID INT,
    IsPublic BOOLEAN DEFAULT 0,
    URLGGForm VARCHAR(255) NOT NULL,
    Capacity INT,
    Status ENUM('PENDING', 'COMPLETED'),
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID) ON DELETE CASCADE
);
-- Thêm dữ liệu mẫu vào bảng Events
INSERT INTO Events (EventName, Description, EventDate, Location, ClubID, IsPublic, URLGGForm, Capacity, Status) VALUES
('Giải Bóng Đá Sinh Viên 2023', 'Giải đấu bóng đá thường niên dành cho sinh viên toàn trường', '2023-12-15 08:00:00', 'Sân vận động trường', 1, TRUE, 'https://forms.google.com/footballtournament2023', 100, 'PENDING'),
('Workshop Lập Trình Web', 'Hội thảo chia sẻ kiến thức và kinh nghiệm về lập trình web', '2023-11-20 14:00:00', 'Phòng hội thảo A1', 4, TRUE, 'https://forms.google.com/webprogrammingworkshop', 50, 'PENDING'),
('Đêm Nhạc Acoustic', 'Đêm nhạc acoustic với những bản tình ca ngọt ngào', '2023-11-25 19:00:00', 'Sân khấu ngoài trời', 6, TRUE, 'https://forms.google.com/acousticnight', 200, 'PENDING'),
('Chiến Dịch Tình Nguyện Mùa Đông', 'Hoạt động tình nguyện giúp đỡ các em nhỏ vùng cao', '2023-12-10 07:00:00', 'Tỉnh Lào Cai', 5, TRUE, 'https://forms.google.com/wintervolunteer', 30, 'PENDING'),
('Hội Thảo Tiếng Anh Học Thuật', 'Hội thảo về kỹ năng viết và thuyết trình bằng tiếng Anh', '2023-11-18 09:00:00', 'Phòng hội thảo B2', 3, TRUE, 'https://forms.google.com/academicenglish', 80, 'PENDING'),
('Giải Bóng Rổ 3x3', 'Giải đấu bóng rổ 3x3 dành cho sinh viên', '2023-12-05 14:00:00', 'Sân bóng rổ trường', 2, TRUE, 'https://forms.google.com/basketball3x3', 60, 'PENDING'),
('Cuộc Thi Tranh Biện', 'Cuộc thi tranh biện về các vấn đề xã hội', '2023-11-30 13:00:00', 'Hội trường lớn', 8, TRUE, 'https://forms.google.com/debatecompetition', 100, 'PENDING'),
('Workshop Kỹ Năng Mềm', 'Hội thảo về kỹ năng giao tiếp và làm việc nhóm', '2023-12-01 09:00:00', 'Phòng hội thảo C3', 5, TRUE, 'https://forms.google.com/softskillsworkshop', 70, 'PENDING'),
('Ngày Hội Âm Nhạc', 'Ngày hội với nhiều hoạt động và biểu diễn âm nhạc', '2023-12-20 10:00:00', 'Sân trường', 6, TRUE, 'https://forms.google.com/musicfestival', 300, 'PENDING'),
('Giải Đấu Lập Trình', 'Cuộc thi lập trình giải quyết các bài toán thực tế', '2023-12-08 08:00:00', 'Phòng máy tính', 4, TRUE, 'https://forms.google.com/codingcompetition', 40, 'PENDING');
-- Thêm một số sự kiện đã hoàn thành để có dữ liệu thống kê
INSERT INTO Events (EventName, Description, EventDate, Location, ClubID, IsPublic, URLGGForm, Capacity, Status) VALUES
('Giải Bóng Đá Sinh Viên 2022', 'Giải đấu bóng đá thường niên dành cho sinh viên toàn trường', '2022-12-15 08:00:00', 'Sân vận động trường', 1, TRUE, 'https://forms.google.com/footballtournament2022', 100, 'COMPLETED'),
('Workshop Lập Trình Mobile', 'Hội thảo chia sẻ kiến thức và kinh nghiệm về lập trình mobile', '2022-11-20 14:00:00', 'Phòng hội thảo A1', 4, TRUE, 'https://forms.google.com/mobileprogrammingworkshop', 50, 'COMPLETED'),
('Đêm Nhạc Rock', 'Đêm nhạc rock sôi động', '2022-11-25 19:00:00', 'Sân khấu ngoài trời', 6, TRUE, 'https://forms.google.com/rocknight', 200, 'COMPLETED'),
('Chiến Dịch Tình Nguyện Mùa Hè', 'Hoạt động tình nguyện giúp đỡ các em nhỏ vùng cao', '2022-07-10 07:00:00', 'Tỉnh Lào Cai', 5, TRUE, 'https://forms.google.com/summervolunteer', 30, 'COMPLETED'),
('Hội Thảo Tiếng Anh Giao Tiếp', 'Hội thảo về kỹ năng giao tiếp bằng tiếng Anh', '2022-11-18 09:00:00', 'Phòng hội thảo B2', 3, TRUE, 'https://forms.google.com/communicationenglish', 80, 'COMPLETED'),
('Giải Bóng Rổ 5x5', 'Giải đấu bóng rổ 5x5 dành cho sinh viên', '2022-12-05 14:00:00', 'Sân bóng rổ trường', 2, TRUE, 'https://forms.google.com/basketball5x5', 60, 'COMPLETED'),
('Cuộc Thi Hùng Biện', 'Cuộc thi hùng biện về các vấn đề xã hội', '2022-11-30 13:00:00', 'Hội trường lớn', 8, TRUE, 'https://forms.google.com/speechcompetition', 100, 'COMPLETED'),
('Workshop Kỹ Năng Lãnh Đạo', 'Hội thảo về kỹ năng lãnh đạo và quản lý', '2022-12-01 09:00:00', 'Phòng hội thảo C3', 5, TRUE, 'https://forms.google.com/leadershipworkshop', 70, 'COMPLETED'),
('Ngày Hội Văn Hóa', 'Ngày hội với nhiều hoạt động và biểu diễn văn hóa', '2022-12-20 10:00:00', 'Sân trường', 6, TRUE, 'https://forms.google.com/culturalfestival', 300, 'COMPLETED'),
('Giải Đấu AI', 'Cuộc thi phát triển trí tuệ nhân tạo', '2022-12-08 08:00:00', 'Phòng máy tính', 4, TRUE, 'https://forms.google.com/aicompetition', 40, 'COMPLETED');





-- Bảng 8: TaskAssignment
CREATE TABLE TaskAssignment (
    TaskAssignmentID INT PRIMARY KEY AUTO_INCREMENT,
    EventID INT NOT NULL,
    DepartmentID INT NOT NULL,
    Description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    TaskName VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    DueDate DATETIME,
    Status ENUM('PENDING', 'COMPLETED'),
    FOREIGN KEY (EventID) REFERENCES Events(EventID) ON DELETE CASCADE,
    FOREIGN KEY (DepartmentID) REFERENCES ClubDepartments(DepartmentID) ON DELETE CASCADE
);
-- Thêm dữ liệu mẫu vào bảng TaskAssignment
INSERT INTO TaskAssignment (EventID, DepartmentID, TaskName, Description, DueDate, Status) VALUES
(1, 1, 'Chuẩn bị sân bãi', 'Kiểm tra và chuẩn bị sân bãi cho giải đấu', '2023-12-10 12:00:00', 'PENDING'),
(1, 2, 'Truyền thông sự kiện', 'Đăng bài quảng cáo sự kiện trên các kênh truyền thông', '2023-12-05 12:00:00', 'PENDING'),
(2, 7, 'Chuẩn bị tài liệu', 'Chuẩn bị tài liệu và slide cho workshop', '2023-11-15 12:00:00', 'PENDING'),
(2, 8, 'Đăng ký người tham dự', 'Quản lý danh sách đăng ký tham dự workshop', '2023-11-18 12:00:00', 'PENDING'),
(3, 11, 'Chuẩn bị âm thanh', 'Kiểm tra và chuẩn bị hệ thống âm thanh cho đêm nhạc', '2023-11-20 12:00:00', 'PENDING'),
(3, 12, 'Trang trí sân khấu', 'Trang trí sân khấu cho đêm nhạc', '2023-11-22 12:00:00', 'PENDING'),
(4, 9, 'Chuẩn bị quà tặng', 'Chuẩn bị quà tặng cho các em nhỏ', '2023-12-05 12:00:00', 'PENDING'),
(4, 10, 'Lên kế hoạch di chuyển', 'Lên kế hoạch di chuyển và logistics cho chiến dịch', '2023-12-08 12:00:00', 'PENDING');




-- Bảng 9: EventParticipants
CREATE TABLE EventParticipants (
    EventParticipantID INT PRIMARY KEY AUTO_INCREMENT,
    EventID INT,
    UserID VARCHAR(10),
    Status ENUM('REGISTERED', 'ATTENDED', 'ABSENT'),
    FOREIGN KEY (EventID) REFERENCES Events(EventID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);
-- Thêm dữ liệu mẫu vào bảng EventParticipants
INSERT INTO EventParticipants (EventID, UserID, Status) VALUES
(1, 'U001', 'REGISTERED'),
(1, 'U002', 'REGISTERED'),
(2, 'U003', 'REGISTERED'),
(2, 'U004', 'REGISTERED'),
(3, 'U001', 'REGISTERED'),
(3, 'U003', 'REGISTERED'),
(4, 'U002', 'REGISTERED'),
(4, 'U004', 'REGISTERED');



-- Bảng 10: Notifications
CREATE TABLE Notifications (
    NotificationID INT PRIMARY KEY AUTO_INCREMENT,
    Title VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    Content TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    ReceiverID VARCHAR(10) NULL DEFAULT NULL,
    Priority ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM',
    Status ENUM('UNREAD', 'READ') DEFAULT 'UNREAD',
    FOREIGN KEY (ReceiverID) REFERENCES Users(UserID) ON DELETE CASCADE
);
-- Thêm dữ liệu mẫu vào bảng Notifications
INSERT INTO Notifications (Title, Content, CreatedDate, ReceiverID, Priority, Status) VALUES
('Thông báo đăng ký thành công', 'Bạn đã đăng ký tham gia sự kiện Giải Bóng Đá Sinh Viên 2023 thành công', '2023-11-01 10:00:00', 'U001', 'MEDIUM', 'UNREAD'),
('Thông báo đăng ký thành công', 'Bạn đã đăng ký tham gia sự kiện Workshop Lập Trình Web thành công', '2023-11-02 11:00:00', 'U003', 'MEDIUM', 'UNREAD'),
('Thông báo sự kiện mới', 'Câu lạc bộ Âm Nhạc vừa tạo sự kiện mới: Đêm Nhạc Acoustic', '2023-11-03 09:00:00', 'U001', 'LOW', 'UNREAD'),
('Thông báo sự kiện mới', 'Câu lạc bộ Tình Nguyện vừa tạo sự kiện mới: Chiến Dịch Tình Nguyện Mùa Đông', '2023-11-04 14:00:00', 'U002', 'LOW', 'UNREAD'),
('Nhắc nhở sự kiện', 'Sự kiện Giải Bóng Đá Sinh Viên 2023 sẽ diễn ra trong 2 ngày nữa', '2023-12-13 08:00:00', 'U001', 'HIGH', 'UNREAD'),
('Nhắc nhở sự kiện', 'Sự kiện Workshop Lập Trình Web sẽ diễn ra trong 2 ngày nữa', '2023-11-18 08:00:00', 'U003', 'HIGH', 'UNREAD');



-- Bảng 11: ClubApplications
CREATE TABLE ClubApplications (
    ApplicationID INT PRIMARY KEY AUTO_INCREMENT,
    UserID VARCHAR(10),
    ClubID INT,
    Email VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    Status ENUM('PENDING', 'CANDIDATE', 'COLLABORATOR', 'APPROVED', 'REJECTED'),
    SubmitDate DATETIME,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID) ON DELETE CASCADE
);
-- Thêm dữ liệu mẫu vào bảng ClubApplications
INSERT INTO ClubApplications (UserID, ClubID, Email, Status, SubmitDate) VALUES
('U001', 4, 'a@gmail.com', 'PENDING', '2023-11-01 10:00:00'),
('U002', 6, 'b@gmail.com', 'PENDING', '2023-11-02 11:00:00'),
('U003', 8, 'c@gmail.com', 'PENDING', '2023-11-03 09:00:00'),
('U004', 7, 'd@gmail.com', 'PENDING', '2023-11-04 14:00:00');



-- Bảng 12: ApplicationFormTemplates
CREATE TABLE ApplicationFormTemplates (
    TemplateID INT PRIMARY KEY AUTO_INCREMENT,
    ClubID INT NOT NULL,
    FormType ENUM('CLUB','EVENT', 'OTHER') DEFAULT 'CLUB' NOT NULL,
    FieldName VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    FieldType ENUM('TEXT', 'TEXTAREA', 'DROPDOWN', 'CHECKBOX', 'FILEUPLOAD', 'INFO') NOT NULL,
    IsRequired BOOLEAN DEFAULT 1 NOT NULL,
    Options TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID) ON DELETE CASCADE
);
-- Thêm dữ liệu mẫu vào bảng ApplicationFormTemplates
INSERT INTO ApplicationFormTemplates (ClubID, FormType, FieldName, FieldType, IsRequired, Options) VALUES
(1, 'CLUB', 'Họ và tên', 'TEXT', TRUE, NULL),
(1, 'CLUB', 'Email', 'TEXT', TRUE, NULL),
(1, 'CLUB', 'Số điện thoại', 'TEXT', TRUE, NULL),
(1, 'CLUB', 'Lý do tham gia', 'TEXTAREA', TRUE, NULL),
(1, 'CLUB', 'Kinh nghiệm', 'TEXTAREA', FALSE, NULL),
(1, 'CLUB', 'Kỹ năng', 'CHECKBOX', FALSE, 'Kỹ năng 1, Kỹ năng 2, Kỹ năng 3, Kỹ năng 4'),
(2, 'CLUB', 'Họ và tên', 'TEXT', TRUE, NULL),
(2, 'CLUB', 'Email', 'TEXT', TRUE, NULL),
(2, 'CLUB', 'Số điện thoại', 'TEXT', TRUE, NULL),
(2, 'CLUB', 'Lý do tham gia', 'TEXTAREA', TRUE, NULL),
(2, 'CLUB', 'Kinh nghiệm', 'TEXTAREA', FALSE, NULL),
(2, 'CLUB', 'Kỹ năng', 'CHECKBOX', FALSE, 'Kỹ năng 1, Kỹ năng 2, Kỹ năng 3, Kỹ năng 4');



-- Bảng 13: ApplicationResponses
CREATE TABLE ApplicationResponses (
    ResponseID INT PRIMARY KEY AUTO_INCREMENT,
    ApplicationID INT NOT NULL,
    TemplateID INT NOT NULL,
    FieldValue TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
    FOREIGN KEY (ApplicationID) REFERENCES ClubApplications(ApplicationID) ON DELETE CASCADE,
    FOREIGN KEY (TemplateID) REFERENCES ApplicationFormTemplates(TemplateID) ON DELETE CASCADE
);
-- Thêm dữ liệu mẫu vào bảng ApplicationResponses
INSERT INTO ApplicationResponses (ApplicationID, TemplateID, FieldValue) VALUES
(1, 1, 'Nguyễn Văn A'),
(1, 2, 'a@gmail.com'),
(1, 3, '0901234567'),
(1, 4, 'Tôi muốn tham gia câu lạc bộ để phát triển kỹ năng lập trình'),
(1, 5, 'Tôi đã có 2 năm kinh nghiệm lập trình'),
(1, 6, 'Kỹ năng 1, Kỹ năng 3'),
(2, 7, 'Trần Thị B'),
(2, 8, 'b@gmail.com'),
(2, 9, '0912345678'),
(2, 10, 'Tôi muốn tham gia câu lạc bộ để phát triển kỹ năng âm nhạc'),
(2, 11, 'Tôi đã có 1 năm kinh nghiệm chơi guitar'),
(2, 12, 'Kỹ năng 2, Kỹ năng 4');

select * from Users;
select * from Events;

select * from Events where EventName like "Bóng";




INSERT INTO Users (FullName, Email, Password, PermissionID, Status, ResetToken, TokenExpiry)
VALUES 
('Nguyễn Văn A', 'a@example.com', '123456', 2, 1, NULL, NULL),
('Trần Thị B', 'b@example.com', '123456', 2, 1, NULL, NULL),
('Lê Văn C', 'c@example.com', '123456', 2, 1, NULL, NULL),
('Phạm Thị D', 'd@example.com', '123456', 2, 1, NULL, NULL);

-- Đề xuất thêm table: PeriodicReports: Dùng cho IC;

INSERT INTO Clubs (
    ClubImg,
    IsRecruiting,
    ClubName,
    Description,
    EstablishedDate,
    ContactPhone,
    ContactGmail,
    ContactURL,
    ClubStatus
)
VALUES (
    'https://example.com/logo.jpg',
    1,
    'CLB Khoa học Dữ liệu',
    'Câu lạc bộ chuyên về nghiên cứu, học thuật và ứng dụng Trí tuệ nhân tạo, Khoa học dữ liệu.',
    '2022-09-01',
    '0123456789',
    'khoahocdulieu@example.com',
    'https://facebook.com/clbkhoahocdulieu',
    1
);

INSERT INTO ApplicationFormTemplates (ClubID, FieldName, FieldType, IsRequired, Options)
VALUES 
(1, 'Giới thiệu CLB', 'INFO', 0, '{\"text\": \"CLB Khoa học Dữ liệu chuyên về AI.\", \"imageUrl\": \"https://example.com/logo.jpg\"}'),
(1, 'Kỹ năng lập trình', 'DROPDOWN', 1, 'Python,R,Java');

INSERT INTO Permissions (PermissionName, Description)
VALUES 
('Admin', 'Quản trị hệ thống'),
('Student', 'Người dùng sinh viên'),
('IC_Officer', 'Cán bộ khoa hoặc trường');
 
 INSERT INTO Roles (RoleName)
VALUES 
('Chủ nhiệm'),
('Phó chủ nhiệm'),
('Trưởng ban Văn hóa');

INSERT INTO Users (
    UserID,
    FullName,
    Email,
    Password,
    DateOfBirth,
    PermissionID,
    Status
)
VALUES (
    'chairman1',
    'Nguyễn Văn A',
    'chairman1@example.com',
    'hashed_password_123',  -- nên dùng bcrypt hoặc SHA khi thực tế
    '1998-05-20 00:00:00',
    2,
    1
);

INSERT INTO ClubDepartments (
    DepartmentName,
    DepartmentStatus,
    Description,
    ClubID
)
VALUES (
    'Ban chu nhiem',
    1,
    'Phụ trách quan ly, chi dao CLB',
    1
);


INSERT INTO UserClubs (
    UserID,
    ClubID,
    DepartmentID,
    RoleID,
    JoinDate
)
VALUES (
    'chairman1',
    1,
    1,
    1,
    NOW()
);




