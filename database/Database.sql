
-- ========================================

-- CREATE DATABASE (MySQL style)

-- ========================================
DROP DATABASE IF EXISTS ClubManagementSystem;
CREATE DATABASE ClubManagementSystem CHARACTER SET utf8mb4 COLLATE=utf8mb4_unicode_ci;
USE ClubManagementSystem;

-- ========================================
-- SEMESTER / TERM
-- ========================================
CREATE TABLE Semesters (
    TermID VARCHAR(10) PRIMARY KEY, -- Ví dụ: SU25,   SP25,         FA24 
    TermName VARCHAR(50), --           Summer 2025,   Spring 2025,  Fall2024
    StartDate DATE NOT NULL,
    EndDate DATE NOT NULL,
    Status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE'
);

INSERT INTO Semesters (TermID, TermName, StartDate, EndDate, Status)
VALUES 
('SP24', 'Spring 2024', '2024-01-01', '2024-04-30', 'INACTIVE'),
('SU24', 'Summer 2024', '2024-05-01', '2024-08-31', 'INACTIVE'),
('FA24', 'Fall 2024', '2024-09-01', '2024-12-31', 'INACTIVE'),
('SP25', 'Spring 2025', '2025-01-01', '2025-04-30', 'INACTIVE'),
('SU25', 'Summer 2025', '2025-05-01', '2025-08-31', 'ACTIVE'),
('FA25', 'Fall 2025', '2025-09-01', '2025-12-31', 'INACTIVE');


-- ========================================
-- USERS, CLUBS, AND ROLES
-- ========================================
CREATE TABLE Permissions (
    PermissionID INT PRIMARY KEY AUTO_INCREMENT,
    PermissionName VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    Description VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL
);
INSERT INTO Permissions (PermissionName) VALUES ('Student'), ('Admin'), ('IC_Officer');

CREATE TABLE Users (
    UserID VARCHAR(10) PRIMARY KEY,
    FullName VARCHAR(100) NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    AvatarSrc VARCHAR(255) DEFAULT 'img/Hinh-anh-dai-dien-mac-dinh-Facebook.jpg',
    DateOfBirth DATE NULL,
    PermissionID INT NOT NULL,
    Status BOOLEAN DEFAULT 1 NOT NULL,
    ResetToken VARCHAR(255),
    TokenExpiry DATETIME,
    FOREIGN KEY (PermissionID) REFERENCES Permissions(PermissionID) ON DELETE CASCADE
);
INSERT INTO Users (UserID, FullName, Email, Password, PermissionID, Status) VALUES
('U001', 'Nguyễn Văn A', 'a@fpt.edu.vn', '123456', 1, 1),
('U002', 'Trần Thị B', 'b@fpt.edu.vn', '123456', 1, 1),
('U005', 'Phạm Văn E', 'e@fpt.edu.vn', '123456', 1, 1),
('U006', 'Đỗ Thị F', 'f@fpt.edu.vn', '123456', 1, 1),
('U007', 'Vũ Văn G', 'g@fpt.edu.vn', '123456', 1, 1),
('U008', 'Lý Thị H', 'h@fpt.edu.vn', '123456', 1, 1),
('U009', 'Trịnh Văn I', 'i@fpt.edu.vn', '123456', 1, 1),
('U010', 'Mai Thị J', 'j@fpt.edu.vn', '123456', 1, 1),
('U011', 'Nguyễn Văn K', 'k@fpt.edu.vn', '123456', 1, 1),
('U012', 'Trần Thị L', 'l@fpt.edu.vn', '123456', 1, 1),
('U013', 'Phạm Văn M', 'm@fpt.edu.vn', '123456', 1, 1),
('U014', 'Đỗ Thị N', 'n@fpt.edu.vn', '123456', 1, 1),
('U015', 'Vũ Văn O', 'o@fpt.edu.vn', '123456', 1, 1),
('U016', 'Lý Thị P', 'p@fpt.edu.vn', '123456', 1, 1),
('U017', 'Nguyễn Văn Q', 'q@fpt.edu.vn', '123456', 1, 1),
('U018', 'Trần Thị R', 'r@fpt.edu.vn', '123456', 1, 1),
('U019', 'Phạm Văn S', 's@fpt.edu.vn', '123456', 1, 1),
('U020', 'Đỗ Thị T', 't@fpt.edu.vn', '123456', 1, 1),
('U021', 'Lê Văn U', 'u@fpt.edu.vn', '123456', 1, 1),
('U022', 'Nguyễn Thị V', 'v@fpt.edu.vn', '123456', 1, 1),
('U023', 'Lê Văn W', 'w@fpt.edu.vn', '123456', 1, 1),
('U024', 'Phạm Thị X', 'x@fpt.edu.vn', '123456', 1, 1),
('U025', 'Nguyễn Văn Y', 'y@fpt.edu.vn', '123456', 1, 1),
('U026', 'Trần Thị Z', 'z@fpt.edu.vn', '123456', 1, 1),
('U027', 'Bùi Thị A1', 'a1@fpt.edu.vn', '123456', 1, 1),
('U028', 'Đặng Văn B1', 'b1@fpt.edu.vn', '123456', 1, 1),
('U029', 'Nguyễn Văn C1', 'c1@fpt.edu.vn', '123456', 1, 1),
('U030', 'Trần Thị D1', 'd1@fpt.edu.vn', '123456', 1, 1),
('U003', 'Ngô Văn C', 'c@fpt.edu.vn', '123456', 2, 1),
('U004', 'Lê Văn D', 'd@fpt.edu.vn', '123456', 3, 1);



CREATE TABLE Roles (
    RoleID INT PRIMARY KEY AUTO_INCREMENT,
    RoleName VARCHAR(20) NOT NULL,
    Description VARCHAR(100) 
);
INSERT INTO Roles (RoleID, RoleName, Description) VALUES
(1, 'Chủ nhiệm', 'Chủ nhiệm CLB, chịu trách nhiệm quản lý toàn bộ CLB'),
(2, 'Phó chủ nhiệm', 'Hỗ trợ chủ nhiệm quản lý CLB'),
(3, 'Trưởng ban', 'Quản lý các ban trong câu lạc bộ’'),
(4, 'Thành viên', 'Thành viên chính thức của CLB');


CREATE TABLE Clubs (
    ClubID INT PRIMARY KEY AUTO_INCREMENT,
    ClubImg VARCHAR(255),
    IsRecruiting BOOLEAN DEFAULT 1,

    ClubName VARCHAR(100) NOT NULL,

    Description TEXT,
    Category ENUM('Thể Thao', 'Học Thuật', 'Phong Trào') DEFAULT 'Học Thuật',
    EstablishedDate DATE,
    ContactPhone VARCHAR(20),
    ContactGmail VARCHAR(50) UNIQUE NOT NULL,
    ContactURL VARCHAR(150),
    ClubStatus BOOLEAN DEFAULT 1
);
INSERT INTO Clubs (ClubImg, IsRecruiting, ClubName, Description, EstablishedDate, ContactPhone, ContactGmail, ContactURL, ClubStatus, Category) VALUES
('/images/clubs/football.jpg', TRUE, 'Câu Lạc Bộ Bóng Đá', '...', '2018-05-10', '0901234567', 'football@club.com', 'https://facebook.com/footballclub', TRUE, 'Thể Thao'),
('/images/clubs/basketball.jpg', TRUE, 'Câu Lạc Bộ Bóng Rổ', '...', '2019-03-15', '0912345678', 'basketball@club.com', 'https://facebook.com/basketballclub', TRUE, 'Thể Thao'),
('/images/clubs/english.jpg', TRUE, 'Câu Lạc Bộ Tiếng Anh', '...', '2017-09-20', '0923456789', 'english@club.com', 'https://facebook.com/englishclub', TRUE, 'Học Thuật'),
('/images/clubs/programming.jpg', FALSE, 'Câu Lạc Bộ Lập Trình', '...', '2020-01-10', '0934567890', 'programming@club.com', 'https://facebook.com/programmingclub', TRUE, 'Học Thuật'),
('/images/clubs/volunteer.jpg', TRUE, 'Câu Lạc Bộ Tình Nguyện', '...', '2016-07-05', '0945678901', 'volunteer@club.com', 'https://facebook.com/volunteerclub', TRUE, 'Phong Trào'),
('/images/clubs/music.jpg', TRUE, 'Câu Lạc Bộ Âm Nhạc', '...', '2018-11-15', '0956789012', 'music@club.com', 'https://facebook.com/musicclub', TRUE, 'Phong Trào'),
('/images/clubs/dance.jpg', FALSE, 'Câu Lạc Bộ Nhảy', '...', '2019-08-20', '0967890123', 'dance@club.com', 'https://facebook.com/danceclub', TRUE, 'Phong Trào'),
('/images/clubs/debate.jpg', TRUE, 'Câu Lạc Bộ Tranh Biện', '...', '2020-02-25', '0978901234', 'debate@club.com', 'https://facebook.com/debateclub', TRUE, 'Học Thuật');




CREATE TABLE Departments (
    DepartmentID INT PRIMARY KEY AUTO_INCREMENT,
    DepartmentName VARCHAR(50) NOT NULL,
    DepartmentStatus BOOLEAN DEFAULT 1,
    Description VARCHAR(200)
);
INSERT INTO Departments (DepartmentName, Description) VALUES
('Ban Nội dung', ''),
('Ban Truyền thông', ''),
('Ban Chủ nhiệm', ''),
('Ban Chuyên môn', ''),
('Ban Hậu cần', ''),
('Ban Đối ngoại', '');

CREATE TABLE ClubDepartments (
    ClubDepartmentID INT PRIMARY KEY AUTO_INCREMENT,
    DepartmentID INT NOT NULL,
    ClubID INT,
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID),
    FOREIGN KEY (DepartmentID) REFERENCES Departments(DepartmentID)
);

INSERT INTO ClubDepartments (DepartmentID, ClubID) VALUES
(3, 1),  -- ClubDepartmentID = 1 → Ban Chủ nhiệm của CLB 1
(2, 1),  -- ClubDepartmentID = 2 → Ban Truyền thông của CLB 1
(3, 2),  -- ClubDepartmentID = 3 → Ban Chủ nhiệm của CLB 2
(4, 2),  -- ClubDepartmentID = 4 → Ban Chuyên môn của CLB 2
(5, 1),  -- ClubDepartmentID = 5 → Ban Hậu cần của CLB 1
(6, 2),  -- ClubDepartmentID = 6 → Ban Đối ngoại của CLB 2
(3, 3),  -- ClubDepartmentID = 7 → Ban Chủ nhiệm của CLB 3
(3, 4),  -- ClubDepartmentID = 8 → Ban Chủ nhiệm của CLB 4
(3, 5),  -- ClubDepartmentID = 9 → Ban Chủ nhiệm của CLB 5
(3, 6),  -- ClubDepartmentID = 10 → Ban Chủ nhiệm của CLB 6
(3, 7),  -- ClubDepartmentID = 11 → Ban Chủ nhiệm của CLB 7
(3, 8),  -- ClubDepartmentID = 12 → Ban Chủ nhiệm của CLB 8
(4, 4),  -- ClubDepartmentID = 13 → Ban Chuyên môn của CLB 4
(2, 4),  -- ClubDepartmentID = 14 → Ban Truyền thông của CLB 4
(1, 4),  -- ClubDepartmentID = 15 → Ban Nội dung của CLB 4
(1, 5),  -- ClubDepartmentID = 16 → Ban Nội dung của CLB 5
(5, 4),  -- ClubDepartmentID = 17 → Ban Hậu cần của CLB 4
(6, 4);  -- ClubDepartmentID = 18 → Ban Đối ngoại của CLB 4



CREATE TABLE UserClubs (
    UserClubID INT PRIMARY KEY AUTO_INCREMENT,
    UserID VARCHAR(10) NOT NULL,
    ClubID INT NOT NULL,
    ClubDepartmentID INT NOT NULL,
    RoleID INT NOT NULL,
    JoinDate DATETIME NOT NULL,
    IsActive BOOLEAN DEFAULT 1,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID),
    FOREIGN KEY (ClubDepartmentID) REFERENCES ClubDepartments(ClubDepartmentID),
    FOREIGN KEY (RoleID) REFERENCES Roles(RoleID)
);


-- sửa ok rồi đừng sửa nữa nhé
INSERT INTO UserClubs (UserID, ClubID, ClubDepartmentID, RoleID, JoinDate, IsActive) VALUES
('U001', 1, 1, 1, '2020-09-01', 1),
('U002', 1, 5, 3, '2021-01-15', 1),
('U002', 2, 3, 2, '2020-10-05', 1),
('U003', 1, 1, 2, '2020-11-10', 1),
('U003', 2, 4, 3, '2021-02-20', 1),
('U004', 1, 2, 3, '2021-03-25', 1),
('U005', 2, 6, 3, '2021-04-30', 1),
('U006', 2, 3, 1, '2021-07-01', 1),
('U007', 1, 5, 4, '2021-07-03', 1),
('U008', 2, 6, 4, '2021-07-04', 1),
('U009', 1, 2, 4, '2021-07-05', 1),
('U010', 2, 4, 4, '2021-07-06', 1),
('U011', 3, 7, 1, '2021-08-01', 1),
('U012', 4, 8, 1, '2021-08-02', 1),
('U013', 5, 9, 1, '2021-08-03', 1),
('U014', 6, 10, 1, '2021-08-04', 1),
('U015', 7, 11, 1, '2021-08-05', 1),
('U016', 8, 12, 1, '2021-08-06', 1),
('U017', 4, 13, 4, '2021-08-10', 1),
('U018', 4, 14, 4, '2021-08-10', 1),
('U019', 4, 13, 4, '2021-08-11', 1),
('U020', 4, 14, 4, '2021-08-11', 1),
('U021', 4, 13, 4, '2021-08-12', 1),
('U022', 4, 15, 3, '2021-09-01', 1),
('U023', 4, 14, 3, '2021-05-01', 1),
('U024', 4, 13, 3, '2021-05-01', 1),
('U025', 4, 17, 3, '2021-05-01', 1),
('U026', 4, 17, 4, '2021-09-01', 1),
('U027', 4, 18, 3, '2021-09-01', 1),
('U028', 4, 18, 4, '2021-09-01', 1),
('U030', 4, 15, 4, '2021-05-06', 1);





INSERT INTO UserClubs (UserID, ClubID, ClubDepartmentID, RoleID, JoinDate, IsActive) VALUES
('U001', 1, 1, 1, '2020-09-01', 1), -- Chủ nhiệm CLB 1, Ban Chủ nhiệm
('U001', 1, 1, 3, '2020-09-01', 1), -- Chủ nhiệm kiêm Trưởng ban Chủ nhiệm CLB 1
('U002', 1, 5, 3, '2021-01-15', 1), -- Trưởng ban Hậu cần CLB 1
('U002', 1, 1, 4, '2021-01-15', 1), -- Trưởng ban Hậu cần là thành viên Ban Chủ nhiệm CLB 1
('U002', 2, 3, 2, '2020-10-05', 1), -- Phó chủ nhiệm CLB 2, Ban Chủ nhiệm
('U003', 2, 4, 3, '2021-02-20', 1), -- Trưởng ban Chuyên môn CLB 2
('U003', 2, 3, 4, '2021-02-20', 1), -- Trưởng ban Chuyên môn là thành viên Ban Chủ nhiệm CLB 2
('U003', 1, 1, 2, '2020-11-10', 1), -- Phó chủ nhiệm CLB 1, Ban Chủ nhiệm
('U004', 1, 2, 3, '2021-03-25', 1), -- Trưởng ban Truyền thông CLB 1
('U004', 1, 1, 4, '2021-03-25', 1), -- Trưởng ban Truyền thông là thành viên Ban Chủ nhiệm CLB 1
('U005', 2, 6, 3, '2021-04-30', 1), -- Trưởng ban Đối ngoại CLB 2
('U005', 2, 3, 4, '2021-04-30', 1), -- Trưởng ban Đối ngoại là thành viên Ban Chủ nhiệm CLB 2
('U006', 2, 3, 1, '2021-07-01', 1), -- Chủ nhiệm CLB 2, Ban Chủ nhiệm
('U006', 2, 3, 3, '2021-07-01', 1), -- Chủ nhiệm kiêm Trưởng ban Chủ nhiệm CLB 2
('U007', 1, 5, 4, '2021-07-03', 1), -- Thành viên Ban Hậu cần CLB 1
('U008', 2, 6, 4, '2021-07-04', 1), -- Thành viên Ban Đối ngoại CLB 2
('U009', 1, 2, 4, '2021-07-05', 1), -- Thành viên Ban Truyền thông CLB 1
('U010', 2, 4, 4, '2021-07-06', 1), -- Thành viên Ban Chuyên môn CLB 2
('U011', 3, 7, 1, '2021-08-01', 1), -- Chủ nhiệm CLB 3, Ban Chủ nhiệm
('U011', 3, 7, 3, '2021-08-01', 1), -- Chủ nhiệm kiêm Trưởng ban Chủ nhiệm CLB 3
('U012', 4, 8, 1, '2021-08-02', 1), -- Chủ nhiệm CLB 4, Ban Chủ nhiệm
('U012', 4, 8, 3, '2021-08-02', 1), -- Chủ nhiệm kiêm Trưởng ban Chủ nhiệm CLB 4
('U013', 5, 9, 1, '2021-08-03', 1), -- Chủ nhiệm CLB 5, Ban Chủ nhiệm
('U013', 5, 9, 3, '2021-08-03', 1), -- Chủ nhiệm kiêm Trưởng ban Chủ nhiệm CLB 5
('U014', 6, 10, 1, '2021-08-04', 1), -- Chủ nhiệm CLB 6, Ban Chủ nhiệm
('U014', 6, 10, 3, '2021-08-04', 1), -- Chủ nhiệm kiêm Trưởng ban Chủ nhiệm CLB 6
('U015', 7, 11, 1, '2021-08-05', 1), -- Chủ nhiệm CLB 7, Ban Chủ nhiệm
('U015', 7, 11, 3, '2021-08-05', 1), -- Chủ nhiệm kiêm Trưởng ban Chủ nhiệm CLB 7
('U016', 8, 12, 1, '2021-08-06', 1), -- Chủ nhiệm CLB 8, Ban Chủ nhiệm
('U016', 8, 12, 3, '2021-08-06', 1), -- Chủ nhiệm kiêm Trưởng ban Chủ nhiệm CLB 8
('U017', 4, 13, 4, '2021-08-10', 1), -- Thành viên Ban Chuyên môn CLB 4
('U018', 4, 14, 4, '2021-08-10', 1), -- Thành viên Ban Truyền thông CLB 4
('U019', 4, 13, 4, '2021-08-11', 1), -- Thành viên Ban Chuyên môn CLB 4
('U020', 4, 14, 4, '2021-08-11', 1), -- Thành viên Ban Truyền thông CLB 4
('U021', 4, 13, 4, '2021-08-12', 1), -- Thành viên Ban Chuyên môn CLB 4
('U022', 4, 15, 3, '2021-09-01', 1), -- Trưởng ban Nội dung CLB 4
('U022', 4, 8, 4, '2021-09-01', 1), -- Trưởng ban Nội dung là thành viên Ban Chủ nhiệm CLB 4
('U023', 4, 14, 3, '2021-05-01', 1), -- Trưởng ban Truyền thông CLB 4 (adjusted JoinDate to keep earliest)
('U023', 4, 8, 4, '2021-05-01', 1), -- Trưởng ban Truyền thông là thành viên Ban Chủ nhiệm CLB 4
('U024', 4, 13, 3, '2021-05-01', 1), -- Trưởng ban Chuyên môn CLB 4 (kept U015, removed U017 as duplicate)
('U024', 4, 8, 4, '2021-05-01', 1), -- Trưởng ban Chuyên môn là thành viên Ban Chủ nhiệm CLB 4
('U025', 4, 17, 3, '2021-05-01', 1), -- Trưởng ban Hậu cần CLB 4 (kept U015, removed U027 as duplicate)
('U025', 4, 8, 4, '2021-05-01', 1), -- Trưởng ban Hậu cần là thành viên Ban Chủ nhiệm CLB 4
('U026', 4, 17, 4, '2021-09-01', 1), -- Thành viên Ban Hậu cần CLB 4
('U027', 4, 18, 3, '2021-09-01', 1), -- Trưởng ban Đối ngoại CLB 4 (kept U027, removed U029)
('U027', 4, 8, 4, '2021-09-01', 1), -- Trưởng ban Đối ngoại là thành viên Ban Chủ nhiệm CLB 4
('U028', 4, 18, 4, '2021-09-01', 1), -- Thành viên Ban Đối ngoại CLB 4
('U030', 4, 15, 4, '2021-05-06', 1); -- Thành viên Ban Nội dung CLB 4 (corrected ClubDepartmentID from 18 to 15)




-- ========================================
-- APPLICATIONS
-- ========================================
CREATE TABLE ApplicationFormTemplates (
    TemplateID INT PRIMARY KEY AUTO_INCREMENT,
    ClubID INT NOT NULL,
    EventID INT,
    FormType ENUM('Club', 'Event', 'Other') NOT NULL,
    Title VARCHAR(100) NOT NULL,
    FieldName VARCHAR(50),
    FieldType ENUM('Text', 'Textarea', 'Number', 'Date', 'Checkbox', 'Info', 'Email','Radio'),
    IsRequired BOOLEAN DEFAULT 1,
    Options LONGTEXT,
    Published BOOLEAN NOT NULL DEFAULT 0,
    DisplayOrder INT DEFAULT 0,
    CONSTRAINT fk_formtemplate_club FOREIGN KEY (ClubID) 
        REFERENCES Clubs(ClubID) ON DELETE CASCADE
    -- EventID sẽ được xử lý sau khi bảng Events được tạo
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


-- ========================================
-- EVENTS
-- ========================================
CREATE TABLE Locations (
    LocationID INT PRIMARY KEY AUTO_INCREMENT,
    LocationName VARCHAR(200) NOT NULL,
    TypeLocation ENUM('OnCampus', 'OffCampus') NOT NULL
);

INSERT INTO Locations (LocationName, TypeLocation) VALUES
('Đường 30m, Đại học FPT Hà Nội', 'OnCampus'),
('Sảnh tòa Delta, Đại học FPT Hà Nội', 'OnCampus'),
('Hội trường tầng 5 tòa Gamma, Đại học FPT Hà Nội', 'OnCampus'),
('Sân bóng rổ, Đại học FPT Hà Nội', 'OnCampus'),
('Sân bóng đá cạnh hồ cá Koi, Đại học FPT Hà Nội', 'OnCampus'),
('Nhà võ 1, Đại học FPT Hà Nội', 'OnCampus'),
('Sảnh tòa Alpha, Đại học FPT Hà Nội', 'OnCampus'),
('Sảnh tòa Epsilon, Đại học FPT Hà Nội', 'OnCampus'),
('AL-L101', 'OnCampus'), ('AL-L201', 'OnCampus'), ('AL-L301', 'OnCampus'), ('AL-L401', 'OnCampus'),
('AL-L501', 'OnCampus'), ('AL-L601', 'OnCampus'), ('AL-L701', 'OnCampus'),
('AL-R101', 'OnCampus'), ('AL-R201', 'OnCampus'), ('AL-R301', 'OnCampus'), ('AL-R401', 'OnCampus'),
('AL-R501', 'OnCampus'), ('AL-R601', 'OnCampus'), ('AL-R701', 'OnCampus'),
('BE-101', 'OnCampus'), ('BE-201', 'OnCampus'), ('BE-301', 'OnCampus'), ('BE-401', 'OnCampus'),
('DE-101', 'OnCampus'), ('DE-201', 'OnCampus'), ('DE-301', 'OnCampus'), ('DE-401', 'OnCampus'),
('EP-101', 'OnCampus'), ('EP-201', 'OnCampus'), ('EP-301', 'OnCampus'), ('EP-401', 'OnCampus'),

('Khu vực nông thôn Đà Nẵng', 'OffCampus'),
('Tỉnh Tuyên Quang', 'OffCampus'),
('Sân vận động Thành phố Hà Nội', 'OffCampus'),
('Nhà thi đấu Quận Cầu Giấy, Hà Nội', 'OffCampus'),
('Trung tâm Hội nghị Quốc gia, Hà Nội', 'OffCampus'),
('Nhà Văn hóa Sinh viên TP.HCM', 'OffCampus'),
('Sân vận động Mỹ Đình, Hà Nội', 'OffCampus');

CREATE TABLE Events (
    EventID INT PRIMARY KEY AUTO_INCREMENT,
    EventName VARCHAR(100) NOT NULL,
    EventImg VARCHAR(100),
    Description TEXT,
    EventDate DATETIME,
    EndTime DATETIME,
    LocationID INT,
    ClubID INT NOT NULL, 
    IsPublic BOOLEAN DEFAULT 0,
    FormTemplateID INT,
    Capacity INT,
    Status ENUM('Pending', 'Processing', 'Completed'),
    ApprovalStatus ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    RejectionReason TEXT,
    SemesterID VARCHAR(10),
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID),
    FOREIGN KEY (FormTemplateID) REFERENCES ApplicationFormTemplates(TemplateID),
    FOREIGN KEY (SemesterID) REFERENCES Semesters(TermID),
    FOREIGN KEY (LocationID) REFERENCES Locations(LocationID)
);

INSERT INTO Events (EventName, EventImg, Description, EventDate, EndTime, LocationID, ClubID, IsPublic, Capacity, Status, SemesterID, ApprovalStatus, RejectionReason) VALUES
-- Spring 2025
('Lễ Hội Âm Nhạc Tết 2025', 'images/events/tetmusic2025.jpg', '...', '2025-02-01 19:00:00', '2025-02-01 22:00:00', 2, 6, TRUE, 300, 'COMPLETED', 'SP25', 'APPROVED', NULL),
('Hội Thảo Kỹ Năng Viết Tiếng Anh', 'images/events/englishworkshop2025.jpg', '...', '2025-03-05 09:00:00', '2025-03-05 11:00:00', 9, 3, TRUE, 60, 'COMPLETED', 'SP25', 'APPROVED', NULL),
('Cuộc Thi Tranh Biện Xã Hội 2025', 'images/events/debate2025.jpg', '...', '2025-04-10 13:00:00', '2025-04-10 15:00:00', 3, 8, TRUE, 100, 'COMPLETED', 'SP25', 'APPROVED', NULL),
('Workshop: Xây dựng Website với Spring Boot', 'images/events/springboot_workshop.jpg', '...', '2025-04-20 14:00:00', '2025-04-20 17:00:00', 26, 4, 0, 100, 'COMPLETED', 'SP25', 'APPROVED', NULL),

-- Summer 2025
('FPTU Showcase 2025 Chung Kết', 'images/events/showcase2025.jpg', '...', '2025-08-02 09:00:00', '2025-08-02 13:00:00', 1, 7, TRUE, 200, 'PENDING', 'SU25', 'PENDING', NULL),
('Thử Thách Lập Trình FPTU 2025', 'images/events/coding2025.jpg', '...', '2025-06-21 08:00:00', '2025-06-21 11:00:00', 25, 4, TRUE, 50, 'COMPLETED', 'SU25', 'APPROVED', NULL),
('Giải Bóng Đá Sinh Viên FPTU 2025', 'images/events/football2025.jpg', '...', '2025-08-05 08:00:00', '2025-08-05 10:00:00', 5, 1, TRUE, 120, 'PENDING', 'SU25', 'PENDING', NULL),
('Chiến Dịch Tình Nguyện Xanh 2025', 'images/events/greenfuture2025.jpg', '...', '2025-08-06 07:00:00', '2025-08-06 12:00:00', 35, 5, TRUE, 40, 'PENDING', 'SU25', 'PENDING', NULL),
('Giải Bóng Rổ 3x3 FPTU', 'images/events/basketball2025.jpg', '...', '2025-07-05 14:00:00', '2025-07-05 16:00:00', 4, 2, FALSE, 80, 'COMPLETED', 'SU25', 'APPROVED', NULL),
('Ngày Hội Âm Nhạc FPTU 2025', 'images/events/musicday2025.jpg', '...', '2025-08-08 10:00:00', '2025-08-08 13:00:00', 1, 6, FALSE, 250, 'PENDING', 'SU25', 'PENDING', NULL),
('Hackathon Đổi Mới AI', 'images/events/aihackathon2025.jpg', '...', '2025-08-12 08:00:00', '2025-08-12 14:00:00', 25, 4, TRUE, 45, 'PENDING', 'SU25', 'PENDING', NULL),
('Cuộc thi Code War: Thử thách thuật toán', 'images/events/codewar.jpg', '...', '2025-07-31 09:00:00', '2025-07-31 13:00:00', 27, 4, 1, 120, 'PENDING', 'SU25', 'PENDING', NULL),

-- Spring 2024
('Lễ Hội Làng Tết 2024', 'images/events/villagefest2024.jpg', '...', '2024-01-20 10:00:00', '2024-01-20 13:00:00', 36, 6, TRUE, 500, 'COMPLETED', 'SP24', 'APPROVED', NULL),

-- Summer 2024
('Chiến Dịch Tình Nguyện Hè 2024', 'images/events/summervolunteer2024.jpg', '...', '2024-07-15 07:00:00', '2024-07-15 12:00:00', 36, 5, FALSE, 35, 'COMPLETED', 'SU24', 'APPROVED', NULL),

-- Fall 2024
('Trại Lập Trình FPTU 2024', 'images/events/codingbootcamp2024.jpg', '...', '2024-11-20 08:00:00', '2024-11-20 12:00:00', 25, 4, TRUE, 60, 'COMPLETED', 'FA24', 'APPROVED', NULL),
('Đêm Nhạc Rock 2024', 'images/events/rocknight2024.jpg', '...', '2024-12-10 19:00:00', '2024-12-10 22:00:00', 1, 6, TRUE, 200, 'COMPLETED', 'FA24', 'APPROVED', NULL),
('Cuộc Thi Nói Tiếng Anh 2024', 'images/events/englishcontest2024.jpg', '...', '2024-11-25 09:00:00', '2024-11-25 11:00:00', 10, 3, TRUE, 70, 'COMPLETED', 'FA24', 'APPROVED', NULL),
('Giải Bóng Đá 5x5 FPTU 2024', 'images/events/football2024.jpg', '...', '2024-12-05 14:00:00', '2024-12-05 16:00:00', 5, 1, FALSE, 100, 'COMPLETED', 'FA24', 'APPROVED', NULL),
('Ngày Hội Nhảy FPTU 2024', 'images/events/dancefest2024.jpg', '...', '2024-12-15 10:00:00', '2024-12-15 13:00:00', 2, 7, TRUE, 400, 'COMPLETED', 'FA24', 'APPROVED', NULL),
('Hội Thảo Phát Triển Web 2024', 'images/events/webdev2024.jpg', '...', '2024-11-15 14:00:00', '2024-11-15 16:00:00', 11, 4, TRUE, 50, 'COMPLETED', 'FA24', 'APPROVED', NULL),
('FPTU Dance Battle 2024', 'images/events/dancebattle2024.jpg', '...', '2024-11-30 18:00:00', '2024-11-30 21:00:00', 1, 7, TRUE, 150, 'COMPLETED', 'FA24', 'APPROVED', NULL),
('Cuộc Thi Tranh Biện 2024', 'images/events/debate2024.jpg', '...', '2024-12-01 09:00:00', '2024-12-01 11:00:00', 12, 8, TRUE, 65, 'COMPLETED', 'FA24', 'APPROVED', NULL);


UPDATE Events 
SET Description = 'Lễ Hội Âm Nhạc Tết 2025 mang đến không gian âm nhạc sống động kết hợp tinh hoa truyền thống và xu hướng hiện đại. Người tham dự sẽ được thưởng thức các tiết mục dân gian hòa quyện với âm nhạc điện tử, rap, ballad… từ các câu lạc bộ nghệ thuật sinh viên. Không khí ngày Tết lan tỏa qua các gian hàng trò chơi dân gian, trình diễn áo dài, và các hoạt động giao lưu văn hóa vùng miền. Đây là dịp để sinh viên FPTU cùng nhau đón Tết sớm, lan tỏa niềm vui và gắn kết cộng đồng.' 
WHERE EventName = 'Lễ Hội Âm Nhạc Tết 2025' AND SemesterID = 'SP25';
UPDATE Events 
SET Description = 'Hội Thảo Kỹ Năng Viết Tiếng Anh 2025 giúp sinh viên nâng cao khả năng viết học thuật qua các chủ đề phổ biến trong môi trường học thuật quốc tế. Diễn giả là các giảng viên, cựu sinh viên xuất sắc sẽ chia sẻ phương pháp viết luận logic, cách triển khai ý tưởng, và kỹ năng phản biện trong tiếng Anh. Người tham dự sẽ được thực hành trực tiếp, nhận phản hồi từ chuyên gia và tài liệu học tập chất lượng. Hội thảo phù hợp với sinh viên chuẩn bị cho IELTS, học bổng, hoặc nghiên cứu.' 
WHERE EventName = 'Hội Thảo Kỹ Năng Viết Tiếng Anh' AND SemesterID = 'SP25';
UPDATE Events 
SET Description = 'Cuộc Thi Tranh Biện Xã Hội 2025 mở ra diễn đàn lý tưởng cho những sinh viên yêu thích tranh luận, tư duy phản biện và quan tâm đến các vấn đề xã hội. Với các chủ đề nóng như giáo dục, môi trường, bình đẳng giới, đội thi sẽ đối đầu trực tiếp qua các vòng debate gay cấn. Sự kiện góp phần nâng cao khả năng lập luận, thuyết phục và trình bày logic cho người trẻ. Ban giám khảo là giảng viên và chuyên gia, đảm bảo tính chuyên môn và công tâm.' 
WHERE EventName = 'Cuộc Thi Tranh Biện Xã Hội 2025' AND SemesterID = 'SP25';
UPDATE Events 
SET Description = 'Workshop: Xây dựng Website với Spring Boot là cơ hội để sinh viên khám phá framework Spring Boot trong phát triển ứng dụng web. Diễn giả là các lập trình viên chuyên nghiệp sẽ hướng dẫn cách xây dựng website từ cơ bản đến nâng cao, bao gồm thiết kế API, tích hợp cơ sở dữ liệu, và triển khai ứng dụng. Người tham gia sẽ được thực hành trực tiếp, nhận tài liệu học tập, và thảo luận với các chuyên gia, giúp nắm vững kỹ năng phát triển web hiện đại.' 
WHERE EventName = 'Workshop: Xây dựng Website với Spring Boot' AND SemesterID = 'SP25';
UPDATE Events 
SET Description = 'FPTU Showcase 2025 Chung Kết là sự kiện nghệ thuật lớn nhất trong năm, nơi quy tụ những tiết mục đặc sắc từ các vòng thi trước đó trên khắp các cơ sở FPTU toàn quốc. Với chủ đề ''Bùng cháy đam mê, lan tỏa cảm hứng'', chương trình không chỉ là sân chơi thể hiện tài năng mà còn là nơi gắn kết cộng đồng sinh viên thông qua âm nhạc, vũ đạo, sân khấu hóa, và các màn trình diễn đỉnh cao. Khán giả sẽ được mãn nhãn với hệ thống âm thanh ánh sáng hiện đại, tương tác sân khấu chuyên nghiệp và đặc biệt là những khoảnh khắc thăng hoa của tuổi trẻ. Sự kiện dự kiến thu hút hàng trăm sinh viên và khách mời, tạo nên một đêm diễn bùng nổ, đầy cảm xúc.' 
WHERE EventName = 'FPTU Showcase 2025 Chung Kết' AND SemesterID = 'SU25';
UPDATE Events 
SET Description = 'Thử Thách Lập Trình FPTU 2025 là sân chơi trí tuệ quy tụ những bạn trẻ đam mê công nghệ và giải thuật. Các đội thi sẽ đối mặt với các bài toán lập trình thực tiễn liên quan đến xử lý dữ liệu, trí tuệ nhân tạo, và bảo mật. Cuộc thi không chỉ rèn luyện kỹ năng coding mà còn thúc đẩy tư duy phản biện, làm việc nhóm và sáng tạo giải pháp. Bên cạnh đó, chương trình còn có workshop chia sẻ kinh nghiệm từ các chuyên gia, tạo môi trường học hỏi bổ ích.' 
WHERE EventName = 'Thử Thách Lập Trình FPTU 2025' AND SemesterID = 'SU25';
UPDATE Events 
SET Description = 'Giải Bóng Đá Sinh Viên FPTU 2025 quy tụ các đội bóng sinh viên tài năng đến từ các khoa và cơ sở khác nhau. Đây là dịp để sinh viên rèn luyện thể chất, xây dựng tinh thần đồng đội và giao lưu giữa các cộng đồng học tập. Các trận đấu diễn ra hấp dẫn với sự cổ vũ nhiệt tình từ khán giả. Sự kiện hứa hẹn tạo ra không khí thể thao sôi động, kết nối đam mê và lan tỏa năng lượng tích cực trong sinh viên.' 
WHERE EventName = 'Giải Bóng Đá Sinh Viên FPTU 2025' AND SemesterID = 'SU25';
UPDATE Events 
SET Description = 'Chiến Dịch Tình Nguyện Xanh 2025 hướng đến mục tiêu nâng cao nhận thức bảo vệ môi trường và phát triển cộng đồng bền vững. Sinh viên sẽ tham gia các hoạt động trồng cây xanh, tái chế rác thải, làm sạch môi trường tại các vùng nông thôn. Ngoài ra, chiến dịch còn kết hợp tổ chức lớp học kỹ năng sống, tặng quà cho học sinh khó khăn, góp phần lan tỏa tinh thần trách nhiệm xã hội. Đây là dịp để sinh viên thể hiện lòng nhiệt huyết, sẻ chia và cống hiến cho cộng đồng.' 
WHERE EventName = 'Chiến Dịch Tình Nguyện Xanh 2025' AND SemesterID = 'SU25';
UPDATE Events 
SET Description = 'Giải Bóng Rổ 3x3 FPTU là sân chơi thể thao hấp dẫn thu hút những bạn trẻ yêu thích vận động, chiến thuật và tinh thần đồng đội. Với thể thức thi đấu 3x3 hiện đại, trận đấu diễn ra nhanh, căng thẳng và đầy kịch tính. Các đội bóng đến từ nhiều cơ sở FPTU sẽ tranh tài quyết liệt để giành chức vô địch. Bên cạnh đó, sự kiện còn có các hoạt động cổ động, tặng quà cho khán giả và phần biểu diễn sôi động giữa trận đấu, tạo nên bầu không khí sôi nổi và đầy hào hứng.' 
WHERE EventName = 'Giải Bóng Rổ 3x3 FPTU' AND SemesterID = 'SU25';
UPDATE Events 
SET Description = 'Ngày Hội Âm Nhạc FPTU 2025 là lễ hội nghệ thuật mở rộng với sân khấu hoành tráng, hệ thống âm thanh – ánh sáng chuyên nghiệp. Sinh viên sẽ được đắm chìm trong các màn trình diễn nhạc trẻ, acoustic, rap, EDM đến từ các ban nhạc và nghệ sĩ trẻ FPTU. Ngoài ra còn có các khu vui chơi, gian hàng ẩm thực và khu check-in nghệ thuật, tạo nên một không gian văn hóa – giải trí đặc sắc. Đây là dịp để sinh viên thể hiện đam mê và thư giãn sau những giờ học căng thẳng.' 
WHERE EventName = 'Ngày Hội Âm Nhạc FPTU 2025' AND SemesterID = 'SU25';
UPDATE Events 
SET Description = 'Hackathon Đổi Mới AI là sự kiện công nghệ nổi bật dành cho sinh viên yêu thích lập trình và sáng tạo với trí tuệ nhân tạo. Trong vòng 24-48 giờ, các đội sẽ xây dựng sản phẩm AI giải quyết bài toán xã hội như chatbot giáo dục, nhận diện cảm xúc, phân tích dữ liệu. Cuộc thi khuyến khích tư duy đổi mới, kỹ năng teamwork và khả năng hiện thực hóa ý tưởng thành sản phẩm. Các dự án xuất sắc sẽ được mentor hỗ trợ phát triển tiếp và có cơ hội trình bày trước nhà đầu tư.' 
WHERE EventName = 'Hackathon Đổi Mới AI' AND SemesterID = 'SU25';
UPDATE Events 
SET Description = 'Cuộc thi Code War: Thử thách thuật toán là sân chơi công nghệ dành cho các lập trình viên trẻ đam mê giải thuật và lập trình thi đấu. Các thí sinh sẽ đối mặt với các bài toán phức tạp về cấu trúc dữ liệu, tối ưu hóa thuật toán, và tư duy logic. Cuộc thi không chỉ kiểm tra kỹ năng coding mà còn khuyến khích sự sáng tạo và khả năng làm việc dưới áp lực. Các workshop từ chuyên gia và giải thưởng hấp dẫn sẽ là động lực lớn cho người tham gia.' 
WHERE EventName = 'Cuộc thi Code War: Thử thách thuật toán' AND SemesterID = 'SU25';
UPDATE Events 
SET Description = 'Lễ Hội Làng Tết 2024 tái hiện không gian Tết truyền thống Việt Nam với các gian hàng thủ công, trò chơi dân gian, và trình diễn nghệ thuật đặc sắc như múa lân, hát chèo, và biểu diễn áo dài. Sự kiện mang đến cơ hội trải nghiệm văn hóa vùng miền, thưởng thức ẩm thực Tết như bánh chưng, bánh tét, và giao lưu cùng sinh viên từ các câu lạc bộ văn hóa. Đây là dịp để sinh viên FPTU hòa mình vào không khí Tết cổ truyền, lan tỏa niềm vui và kết nối cộng đồng.' 
WHERE EventName = 'Lễ Hội Làng Tết 2024' AND SemesterID = 'SP24';
UPDATE Events 
SET Description = 'Chiến Dịch Tình Nguyện Hè 2024 là hành trình đầy ý nghĩa đưa sinh viên FPTU đến với các vùng nông thôn, tham gia các hoạt động thiện nguyện như xây nhà tình thương, dạy học cho trẻ em khó khăn, và hỗ trợ cộng đồng địa phương. Chương trình không chỉ lan tỏa tinh thần sẻ chia mà còn giúp sinh viên rèn luyện kỹ năng lãnh đạo, làm việc nhóm, và cảm nhận sâu sắc giá trị của sự cống hiến. Các hoạt động giao lưu văn hóa và khám phá vùng đất mới cũng là điểm nhấn đặc biệt.' 
WHERE EventName = 'Chiến Dịch Tình Nguyện Hè 2024' AND SemesterID = 'SU24';
UPDATE Events 
SET Description = 'Trại Lập Trình FPTU 2024 là sân chơi công nghệ quy tụ các coder trẻ đam mê lập trình và sáng tạo phần mềm. Trong 3 ngày, sinh viên sẽ tham gia các thử thách coding, workshop về công nghệ mới như blockchain, DevOps, và phát triển ứng dụng thực tế. Dưới sự hướng dẫn của các mentor là chuyên gia công nghệ, trại hè mang đến cơ hội học hỏi, kết nối và phát triển kỹ năng lập trình chuyên sâu, đồng thời xây dựng tinh thần đồng đội và sáng tạo.' 
WHERE EventName = 'Trại Lập Trình FPTU 2024' AND SemesterID = 'FA24';
UPDATE Events 
SET Description = 'Đêm Nhạc Rock 2024 là sự kiện âm nhạc bùng nổ với các màn trình diễn đầy năng lượng từ các ban nhạc rock sinh viên FPTU và khách mời nổi tiếng. Không gian sân khấu được đầu tư hoành tráng với hệ thống âm thanh, ánh sáng đỉnh cao, mang đến trải nghiệm cuồng nhiệt cho khán giả. Bên cạnh âm nhạc, sự kiện còn có các khu vực tương tác, check-in nghệ thuật và gian hàng lưu niệm, tạo nên một đêm rock sôi động, đậm chất tuổi trẻ.' 
WHERE EventName = 'Đêm Nhạc Rock 2024' AND SemesterID = 'FA24';
UPDATE Events 
SET Description = 'Cuộc Thi Nói Tiếng Anh 2024 là sân chơi giúp sinh viên FPTU rèn luyện kỹ năng giao tiếp tiếng Anh thông qua các bài thuyết trình, tranh luận, và trả lời tình huống. Với các chủ đề đa dạng từ học thuật, văn hóa đến đời sống, thí sinh sẽ thể hiện khả năng sử dụng ngôn ngữ lưu loát, tự tin và sáng tạo. Sự kiện còn có sự góp mặt của giám khảo là các chuyên gia ngôn ngữ, mang đến phản hồi giá trị và cơ hội học hỏi cho người tham gia.' 
WHERE EventName = 'Cuộc Thi Nói Tiếng Anh 2024' AND SemesterID = 'FA24';
UPDATE Events 
SET Description = 'Giải Bóng Đá 5x5 FPTU 2024 là giải đấu thể thao sôi động quy tụ các đội bóng sinh viên từ các khoa và cơ sở FPTU. Với thể thức 5x5, các trận đấu diễn ra nhanh, kịch tính, đòi hỏi sự phối hợp chiến thuật và kỹ năng cá nhân. Sự kiện không chỉ là nơi tranh tài mà còn là cơ hội giao lưu, xây dựng tinh thần đoàn kết giữa các sinh viên. Không khí sôi nổi với sự cổ vũ nhiệt tình từ khán giả và các hoạt động bên lề sẽ làm nên dấu ấn đặc biệt.' 
WHERE EventName = 'Giải Bóng Đá 5x5 FPTU 2024' AND SemesterID = 'FA24';
UPDATE Events 
SET Description = 'Ngày Hội Nhảy FPTU 2024 là lễ hội vũ đạo hoành tráng, nơi các nhóm nhảy sinh viên thể hiện tài năng qua các thể loại như hip-hop, contemporary, và K-pop. Sân khấu được đầu tư chuyên nghiệp với ánh sáng, âm thanh hiện đại, kết hợp cùng các màn trình diễn mãn nhãn. Sự kiện còn có khu vực giao lưu, workshop dạy nhảy và các trò chơi tương tác, mang đến không gian giải trí sôi động và cơ hội khám phá đam mê cho sinh viên.' 
WHERE EventName = 'Ngày Hội Nhảy FPTU 2024' AND SemesterID = 'FA24';
UPDATE Events 
SET Description = 'Hội Thảo Phát Triển Web 2024 mang đến cơ hội học hỏi về các công nghệ phát triển web tiên tiến như React, Node.js, và thiết kế giao diện người dùng. Diễn giả là các lập trình viên dày dạn kinh nghiệm sẽ chia sẻ kiến thức thực tiễn, từ xây dựng website responsive đến tối ưu hóa hiệu suất. Người tham gia sẽ được thực hành coding trực tiếp, nhận phản hồi từ chuyên gia, và kết nối với cộng đồng yêu công nghệ, giúp định hướng nghề nghiệp trong lĩnh vực phát triển web.' 
WHERE EventName = 'Hội Thảo Phát Triển Web 2024' AND SemesterID = 'FA24';
UPDATE Events 
SET Description = 'FPTU Dance Battle 2024 là cuộc thi vũ đạo đối kháng kịch tính, nơi các nhóm nhảy sinh viên tranh tài qua các vòng loại và chung kết. Với các thể loại đa dạng như street dance, popping, và breakdance, sự kiện mang đến những màn trình diễn bùng nổ và sáng tạo. Khán giả sẽ được hòa mình vào không khí sôi động với âm nhạc, ánh sáng chuyên nghiệp, và các hoạt động bên lề như giao lưu với vũ công, tạo nên một ngày hội vũ đạo đáng nhớ.' 
WHERE EventName = 'FPTU Dance Battle 2024' AND SemesterID = 'FA24';
UPDATE Events 
SET Description = 'Cuộc Thi Tranh Biện 2024 là diễn đàn trí tuệ nơi sinh viên FPTU thể hiện khả năng lập luận, phản biện, và thuyết trình về các vấn đề xã hội, kinh tế, và giáo dục. Với format tranh biện chuyên nghiệp, các đội thi sẽ đối đầu qua các vòng thi căng thẳng, được chấm điểm bởi ban giám khảo là các chuyên gia và giảng viên. Sự kiện không chỉ nâng cao kỹ năng tư duy logic mà còn khuyến khích sinh viên bày tỏ quan điểm và kết nối với cộng đồng.' 
WHERE EventName = 'Cuộc Thi Tranh Biện 2024' AND SemesterID = 'FA24';

CREATE TABLE Agenda (
    AgendaID INT PRIMARY KEY AUTO_INCREMENT,
    EventID INT NOT NULL,
    Title VARCHAR(100) NOT NULL,
    Description TEXT,
    StartTime DATETIME NOT NULL,
    EndTime DATETIME NOT NULL,
    FOREIGN KEY (EventID) REFERENCES Events(EventID)
);

-- Thử Thách Lập Trình FPTU 2025
INSERT INTO Agenda (EventID, Title, Description, StartTime, EndTime) VALUES
((SELECT EventID FROM Events WHERE EventName = 'Thử Thách Lập Trình FPTU 2025'), 'Check-in & Nhận thẻ', 'Người tham dự check-in và nhận thẻ tham dự.', '2025-03-15 07:30:00', '2025-03-15 08:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Thử Thách Lập Trình FPTU 2025'), 'Phát biểu khai mạc', 'Ban tổ chức giới thiệu chương trình và mục tiêu cuộc thi.', '2025-03-15 08:00:00', '2025-03-15 08:15:00'),
((SELECT EventID FROM Events WHERE EventName = 'Thử Thách Lập Trình FPTU 2025'), 'Phổ biến thể lệ & luật chơi', 'Giới thiệu thể lệ cuộc thi và các quy tắc đánh giá.', '2025-03-15 08:15:00', '2025-03-15 08:45:00'),
((SELECT EventID FROM Events WHERE EventName = 'Thử Thách Lập Trình FPTU 2025'), 'Bắt đầu thi lập trình', 'Các đội/thí sinh bắt đầu làm bài.', '2025-03-15 08:45:00', '2025-03-15 11:30:00'),
((SELECT EventID FROM Events WHERE EventName = 'Thử Thách Lập Trình FPTU 2025'), 'Chấm điểm & chờ kết quả', 'Ban giám khảo chấm điểm và tổng hợp kết quả.', '2025-03-15 11:30:00', '2025-03-15 11:50:00'),
((SELECT EventID FROM Events WHERE EventName = 'Thử Thách Lập Trình FPTU 2025'), 'Tổng kết & chụp ảnh lưu niệm', 'Trao giải, chụp ảnh, kết thúc chương trình.', '2025-03-15 11:50:00', '2025-03-15 12:30:00');
-- Hackathon Đổi Mới AI
INSERT INTO Agenda (EventID, Title, Description, StartTime, EndTime) VALUES
((SELECT EventID FROM Events WHERE EventName = 'Hackathon Đổi Mới AI'), 'Check-in & Giới thiệu', 'Người tham gia đến check-in và nhận hướng dẫn.', '2025-06-05 07:30:00', '2025-06-05 08:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Hackathon Đổi Mới AI'), 'Phát động & chia đội', 'Ban tổ chức phổ biến đề bài và chia đội.', '2025-06-05 08:00:00', '2025-06-05 08:30:00'),
((SELECT EventID FROM Events WHERE EventName = 'Hackathon Đổi Mới AI'), 'Coding Marathon', 'Các đội bắt đầu lập trình và triển khai ý tưởng AI.', '2025-06-05 08:30:00', '2025-06-05 17:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Hackathon Đổi Mới AI'), 'Trình bày sản phẩm', 'Các đội trình bày giải pháp của mình.', '2025-06-05 17:00:00', '2025-06-05 18:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Hackathon Đổi Mới AI'), 'Trao giải & nhận xét', 'Ban giám khảo nhận xét và trao giải cho đội xuất sắc.', '2025-06-05 18:00:00', '2025-06-05 18:30:00'),
((SELECT EventID FROM Events WHERE EventName = 'Hackathon Đổi Mới AI'), 'Tổng kết & chụp ảnh lưu niệm', 'Tổng kết sự kiện, chụp ảnh, dọn dẹp địa điểm.', '2025-06-05 18:30:00', '2025-06-05 19:30:00');
-- Workshop: Xây dựng Website với Spring Boot
INSERT INTO Agenda (EventID, Title, Description, StartTime, EndTime) VALUES
((SELECT EventID FROM Events WHERE EventName = 'Workshop: Xây dựng Website với Spring Boot'), 'Chuẩn bị đón khách', 'Sắp xếp thiết bị, banner và kiểm tra âm thanh.', '2025-06-20 13:00:00', '2025-06-20 13:45:00'),
((SELECT EventID FROM Events WHERE EventName = 'Workshop: Xây dựng Website với Spring Boot'), 'Chào mừng và giới thiệu', 'MC giới thiệu sự kiện và diễn giả.', '2025-06-20 13:45:00', '2025-06-20 14:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Workshop: Xây dựng Website với Spring Boot'), 'Tổng quan Spring Boot', 'Giới thiệu về Spring Boot và kiến trúc cơ bản.', '2025-06-20 14:00:00', '2025-06-20 14:30:00'),
((SELECT EventID FROM Events WHERE EventName = 'Workshop: Xây dựng Website với Spring Boot'), 'Cấu hình dự án', 'Hướng dẫn cấu hình Maven và Spring Initializr.', '2025-06-20 14:30:00', '2025-06-20 15:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Workshop: Xây dựng Website với Spring Boot'), 'Kết nối Database', 'Tích hợp với MySQL/PostgreSQL sử dụng Spring Data JPA.', '2025-06-20 15:00:00', '2025-06-20 15:30:00'),
((SELECT EventID FROM Events WHERE EventName = 'Workshop: Xây dựng Website với Spring Boot'), 'Thực hành API CRUD', 'Tạo controller, service, repository cho quản lý người dùng.', '2025-06-20 15:30:00', '2025-06-20 16:15:00'),
((SELECT EventID FROM Events WHERE EventName = 'Workshop: Xây dựng Website với Spring Boot'), 'Q&A và chia sẻ kinh nghiệm', 'Tham gia hỏi đáp trực tiếp với diễn giả.', '2025-06-20 16:15:00', '2025-06-20 16:30:00'),
((SELECT EventID FROM Events WHERE EventName = 'Workshop: Xây dựng Website với Spring Boot'), 'Giải lao và networking', 'Tự do giao lưu, trao đổi kết nối.', '2025-06-20 16:30:00', '2025-06-20 17:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Workshop: Xây dựng Website với Spring Boot'), 'Dọn dẹp và kết thúc', 'Thu dọn thiết bị, chụp ảnh kỷ niệm và đóng sự kiện.', '2025-06-20 17:00:00', '2025-06-20 17:45:00');
-- Cuộc thi Code War: Thử thách thuật toán
INSERT INTO Agenda (EventID, Title, Description, StartTime, EndTime) VALUES
((SELECT EventID FROM Events WHERE EventName = 'Cuộc thi Code War: Thử thách thuật toán'), 'Check-in & Nhận số báo danh', 'Thí sinh đăng ký tại bàn check-in.', '2025-06-25 08:00:00', '2025-06-25 08:30:00'),
((SELECT EventID FROM Events WHERE EventName = 'Cuộc thi Code War: Thử thách thuật toán'), 'Khai mạc cuộc thi', 'Giới thiệu thể lệ, quy định và cơ cấu giải thưởng.', '2025-06-25 08:30:00', '2025-06-25 08:50:00'),
((SELECT EventID FROM Events WHERE EventName = 'Cuộc thi Code War: Thử thách thuật toán'), 'Làm bài Round 1', 'Giải 3 bài toán cơ bản trong 45 phút.', '2025-06-25 08:50:00', '2025-06-25 09:35:00'),
((SELECT EventID FROM Events WHERE EventName = 'Cuộc thi Code War: Thử thách thuật toán'), 'Giải lao ngắn', 'Nghỉ ngơi, ăn nhẹ.', '2025-06-25 09:35:00', '2025-06-25 09:50:00'),
((SELECT EventID FROM Events WHERE EventName = 'Cuộc thi Code War: Thử thách thuật toán'), 'Làm bài Round 2', 'Giải bài toán khó hơn và có tính ứng dụng.', '2025-06-25 09:50:00', '2025-06-25 10:40:00'),
((SELECT EventID FROM Events WHERE EventName = 'Cuộc thi Code War: Thử thách thuật toán'), 'Chấm điểm và giải lao', 'BTC chấm bài tự động + nghỉ ngơi.', '2025-06-25 10:40:00', '2025-06-25 11:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Cuộc thi Code War: Thử thách thuật toán'), 'Công bố kết quả', 'Công bố top 5 thí sinh xuất sắc.', '2025-06-25 11:00:00', '2025-06-25 11:15:00'),
((SELECT EventID FROM Events WHERE EventName = 'Cuộc thi Code War: Thử thách thuật toán'), 'Trao thưởng', 'Trao giải nhất, nhì, ba và quà lưu niệm.', '2025-06-25 11:15:00', '2025-06-25 11:30:00'),
((SELECT EventID FROM Events WHERE EventName = 'Cuộc thi Code War: Thử thách thuật toán'), 'Giao lưu & chụp hình', 'Giao lưu các bạn cùng đam mê lập trình.', '2025-06-25 11:30:00', '2025-06-25 12:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Cuộc thi Code War: Thử thách thuật toán'), 'Thu dọn và kết thúc sự kiện', 'Thu dọn bàn ghế, thiết bị, vệ sinh phòng.', '2025-06-25 12:00:00', '2025-06-25 12:45:00');
-- Giải Bóng Đá Sinh Viên FPTU 2025
INSERT INTO Agenda (EventID, Title, Description, StartTime, EndTime) VALUES
((SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Đá Sinh Viên FPTU 2025'), 'Check-in & chia đội', 'Thí sinh đến check-in và nhận áo thi đấu.', '2025-07-05 07:15:00', '2025-07-05 07:45:00'),
((SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Đá Sinh Viên FPTU 2025'), 'Phát biểu khai mạc', 'Ban tổ chức phát biểu và tuyên bố khai mạc.', '2025-07-05 07:45:00', '2025-07-05 08:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Đá Sinh Viên FPTU 2025'), 'Trận vòng loại', 'Thi đấu các trận vòng loại.', '2025-07-05 08:00:00', '2025-07-05 11:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Đá Sinh Viên FPTU 2025'), 'Nghỉ trưa', 'Nghỉ ăn nhẹ và chuẩn bị vòng tiếp theo.', '2025-07-05 11:00:00', '2025-07-05 12:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Đá Sinh Viên FPTU 2025'), 'Bán kết & chung kết', 'Các đội mạnh nhất tranh tài.', '2025-07-05 12:00:00', '2025-07-05 14:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Đá Sinh Viên FPTU 2025'), 'Lễ trao giải & bế mạc', 'Trao cúp, chụp ảnh, tổng kết & dọn dẹp.', '2025-07-05 14:00:00', '2025-07-05 15:30:00');


CREATE TABLE EventParticipants (
    EventParticipantID INT PRIMARY KEY AUTO_INCREMENT,
    EventID INT,
    UserID VARCHAR(10),
    Status ENUM('REGISTERED', 'ATTENDED', 'ABSENT'),
    FOREIGN KEY (EventID) REFERENCES Events(EventID),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
INSERT INTO EventParticipants (EventID, UserID, Status) VALUES
(1, 'U001', 'REGISTERED'),
(1, 'U002', 'REGISTERED'),
(2, 'U003', 'REGISTERED'),
(2, 'U004', 'REGISTERED'),
(3, 'U001', 'REGISTERED'),
(3, 'U003', 'REGISTERED'),
(4, 'U002', 'REGISTERED'),
(4, 'U004', 'REGISTERED'),
(13,'U002','REGISTERED'),
(13,'U005','REGISTERED'),
(13,'U006','REGISTERED'),
(13,'U007','REGISTERED'),
(13,'U008','REGISTERED');

-- ================================================================================
-- ========================================
-- TASK ASSIGNMENTS
-- ========================================

CREATE TABLE EventTerms (
    TermID INT PRIMARY KEY AUTO_INCREMENT,
    EventID INT NOT NULL,
    TermName ENUM('Trước sự kiện', 'Trong sự kiện', 'Sau sự kiện'),
    TermStart DATE,
    TermEnd DATE,
    FOREIGN KEY (EventID) REFERENCES Events(EventID)
);
-- Giai đoạn cho Hackathon AI
INSERT INTO EventTerms (EventID, TermName, TermStart, TermEnd) VALUES
(11, 'Trước sự kiện', '2025-07-01', '2025-07-14'),
(11, 'Trong sự kiện', '2025-07-15', '2025-07-15'),
(11, 'Sau sự kiện', '2025-07-16', '2025-07-17'),
-- Giai đoạn cho Code War
(12, 'Trước sự kiện', '2025-07-20', '2025-07-30'),
(12, 'Trong sự kiện', '2025-07-31', '2025-07-31');

CREATE TABLE Tasks (
    TaskID INT PRIMARY KEY AUTO_INCREMENT,
    ParentTaskID INT, -- NULL nếu là nhiệm vụ cấp cao nhất (dành cho ban)
    TermID INT,
    EventID INT,
    ClubID INT,
    Title VARCHAR(100) NOT NULL,
    Description TEXT,
    Status ENUM('ToDo', 'InProgress', 'Review', 'Done') DEFAULT 'ToDo',
    Priority ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM',
    ProgressPercent INT DEFAULT 0,
    StartDate DATETIME,
    EndDate DATETIME,
    CreatedBy VARCHAR(10),
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (EventID) REFERENCES Events(EventID),
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID),
    FOREIGN KEY (CreatedBy) REFERENCES Users(UserID),
    FOREIGN KEY (TermID) REFERENCES EventTerms(TermID)
);
-- Hackathon AI
INSERT INTO Tasks (ParentTaskID, TermID, EventID, ClubID, Title, Description, Status, Priority, ProgressPercent, StartDate, EndDate, CreatedBy) VALUES
(NULL, 1, 11, 4, 'Thiết kế poster sự kiện', 'Thiết kế và duyệt poster truyền thông cho sự kiện Hackathon AI.', 'ToDo', 'MEDIUM', 0, '2025-07-05 08:00:00', '2025-07-10 17:00:00', 'U012'),
(NULL, 1, 11, 4, 'Chuẩn bị đề bài Hackathon', 'Xây dựng bộ đề thi cho Hackathon AI', 'ToDo', 'HIGH', 0, '2025-07-01 09:00:00', '2025-07-14 17:00:00', 'U012'),
(NULL, 2, 11, 4, 'Hỗ trợ kỹ thuật tại sự kiện', 'Giải quyết các sự cố kỹ thuật trong suốt sự kiện', 'ToDo', 'MEDIUM', 0, '2025-07-15 07:30:00', '2025-07-15 14:00:00', 'U012'),
(NULL, 2, 11, 4, 'Ghi hình & Livestream sự kiện', 'Phụ trách quay video và livestream trên fanpage CLB.', 'ToDo', 'HIGH', 0, '2025-07-15 07:45:00', '2025-07-15 14:00:00', 'U012'),
(NULL, 3, 11, 4, 'Tổng kết kết quả và gửi email cảm ơn', 'Tổng hợp kết quả thi, gửi thư cảm ơn đến người tham gia', 'ToDo', 'LOW', 0, '2025-07-16 09:00:00', '2025-07-17 17:00:00', 'U012'),
-- Code War
(NULL, 4, 12, 4, 'Ra đề và chuẩn bị test case', 'Xây dựng đề thi và bộ test case cho cuộc thi Code War.', 'ToDo', 'HIGH', 0, '2025-07-20 08:00:00', '2025-07-30 17:00:00', 'U012'),
(NULL, 5, 12, 4, 'Quản lý hệ thống thi và hỗ trợ thí sinh', 'Theo dõi hệ thống thi, xử lý lỗi kỹ thuật và hỗ trợ thí sinh khi cần.', 'ToDo', 'MEDIUM', 0, '2025-07-31 08:00:00', '2025-07-31 13:00:00', 'U012');


CREATE TABLE TaskAssignees (
    TaskAssigneeID INT PRIMARY KEY AUTO_INCREMENT,
    TaskID INT NOT NULL,
    AssigneeType ENUM('User', 'Department') NOT NULL,
    UserID VARCHAR(10),
    DepartmentID INT,

    FOREIGN KEY (TaskID) REFERENCES Tasks(TaskID),
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (DepartmentID) REFERENCES Departments(DepartmentID),

    CHECK (
        (AssigneeType = 'User' AND UserID IS NOT NULL AND DepartmentID IS NULL) OR
        (AssigneeType = 'Department' AND DepartmentID IS NOT NULL AND UserID IS NULL)
    )
);
INSERT INTO TaskAssignees (TaskID, AssigneeType, DepartmentID) VALUES 
(1, 'Department', 2),   -- Ban Truyền thông
(2, 'Department', 1),   -- Ban Nội dung
(3, 'Department', 5),   -- Ban Hậu cần
(3, 'Department', 2),   -- Ban Truyền thông
(4, 'Department', 2),   -- Ban Truyền thông
(5, 'Department', 1),   -- Ban Nội dung
(6, 'Department', 1),   -- Ban Nội dung
(7, 'Department', 5);   -- Ban Hậu cần
CREATE TABLE TaskProgressLogs (
    LogID INT PRIMARY KEY AUTO_INCREMENT,
    TaskID INT NOT NULL,
    UserID VARCHAR(10) NOT NULL,
    Progress INT NOT NULL, -- 0 ~ 100 (%)
    Note TEXT, -- “Đã làm phần báo cáo xong…”
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (TaskID) REFERENCES Tasks(TaskID),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

CREATE TABLE TaskFeedbacks (
    FeedbackID INT PRIMARY KEY AUTO_INCREMENT,
    TaskID INT NOT NULL,
    ReviewerID VARCHAR(10) NOT NULL,
    Rating ENUM('Positive', 'Neutral', 'Negative') DEFAULT 'Neutral',
    Comment TEXT,
    ProgressAdjustment INT DEFAULT 0, -- ±%
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (TaskID) REFERENCES Tasks(TaskID),
    FOREIGN KEY (ReviewerID) REFERENCES Users(UserID)
);
-- ================================================================================
-- ========================================
-- MEETINGS
-- ========================================
CREATE TABLE ClubMeeting (
	ClubMeetingID INT PRIMARY KEY AUTO_INCREMENT,
	ClubID INT NOT NULL,
	URLMeeting varchar(50),
	StartedTime DATETIME, 
FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID)

);

CREATE TABLE DepartmentMeeting (
	DepartmentMeetingID INT PRIMARY KEY AUTO_INCREMENT,
	DepartmentID INT NOT NULL,
URLMeeting varchar(50),
StartedTime DATETIME,
FOREIGN KEY (DepartmentID) REFERENCES Departments(DepartmentID)
);

 INSERT INTO DepartmentMeeting (ClubDepartmentID, URLMeeting, StartedTime) VALUES
(3, 'https://meet.google.com/dept1', '2025-06-30 10:00:00'), 
(1, 'https://meet.google.com/dept2', '2025-06-29 14:00:00'), 
(5, 'https://meet.google.com/dept3', '2025-06-28 09:00:00');

-- ================================================================================
-- ========================================
-- NOTIFICATIONS
-- ========================================
CREATE TABLE Notifications (
    NotificationID INT PRIMARY KEY AUTO_INCREMENT,
    Title VARCHAR(200) NOT NULL,
    Content TEXT,
    CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    ReceiverID VARCHAR(10),
    SenderID VARCHAR(10) NULL DEFAULT NULL,
    Priority ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM',
    Status ENUM('UNREAD', 'READ') DEFAULT 'UNREAD',
    FOREIGN KEY (ReceiverID) REFERENCES Users(UserID),
    FOREIGN KEY (SenderID) REFERENCES Users(UserID)
);
INSERT INTO Notifications (Title, Content, CreatedDate, ReceiverID, Priority, Status) VALUES
('Thông báo đăng ký thành công', 'Bạn đã đăng ký tham gia sự kiện Giải Bóng Đá Sinh Viên 2023 thành công', '2023-11-01 10:00:00', 'U001', 'MEDIUM', 'UNREAD'),
('Thông báo đăng ký thành công', 'Bạn đã đăng ký tham gia sự kiện Workshop Lập Trình Web thành công', '2023-11-02 11:00:00', 'U003', 'MEDIUM', 'UNREAD'),
('Thông báo sự kiện mới', 'Câu lạc bộ Âm Nhạc vừa tạo sự kiện mới: Đêm Nhạc Acoustic', '2023-11-03 09:00:00', 'U001', 'LOW', 'UNREAD'),
('Thông báo sự kiện mới', 'Câu lạc bộ Tình Nguyện vừa tạo sự kiện mới: Chiến Dịch Tình Nguyện Mùa Đông', '2023-11-04 14:00:00', 'U002', 'LOW', 'UNREAD'),
('Nhắc nhở sự kiện', 'Sự kiện Giải Bóng Đá Sinh Viên 2023 sẽ diễn ra trong 2 ngày nữa', '2023-12-13 08:00:00', 'U001', 'HIGH', 'UNREAD'),
('Nhắc nhở sự kiện', 'Sự kiện Workshop Lập Trình Web sẽ diễn ra trong 2 ngày nữa', '2023-11-18 08:00:00', 'U003', 'HIGH', 'UNREAD');



CREATE TABLE ApplicationResponses (
    ResponseID INT PRIMARY KEY AUTO_INCREMENT,
    TemplateID INT NOT NULL,
    UserID VARCHAR(10) NOT NULL,
    ClubID INT NOT NULL,
    EventID INT,
    Responses TEXT NOT NULL,
    Status ENUM('Pending', 'Candidate', 'Collaborator', 'Approved', 'Rejected') DEFAULT 'Pending',
    SubmitDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (TemplateID) REFERENCES ApplicationFormTemplates(TemplateID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (EventID) REFERENCES Events(EventID) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE ClubApplications (
    ApplicationID INT PRIMARY KEY AUTO_INCREMENT,
    UserID VARCHAR(10),
    ClubID INT,
    Email VARCHAR(100) NOT NULL,
    EventID INT,
    ResponseID INT,			
    Status ENUM('PENDING', 'CANDIDATE', 'COLLABORATOR', 'APPROVED', 'REJECTED'),
    SubmitDate DATETIME,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID),
    FOREIGN KEY (EventID) REFERENCES Events(EventID),
    FOREIGN KEY (ResponseID) REFERENCES ApplicationResponses(ResponseID)
);
INSERT INTO ClubApplications (UserID, ClubID, Email, Status, SubmitDate) VALUES
('U001', 4, 'a@gmail.com', 'PENDING', '2023-11-01 10:00:00'),
('U002', 6, 'b@gmail.com', 'PENDING', '2023-11-02 11:00:00'),
('U003', 8, 'c@gmail.com', 'PENDING', '2023-11-03 09:00:00'),
('U004', 7, 'd@gmail.com', 'PENDING', '2023-11-04 14:00:00');


-- ================================================================================
-- ========================================
-- PERIODIC REPORTS
-- ========================================
CREATE TABLE PeriodicClubReport (
    ReportID INT PRIMARY KEY AUTO_INCREMENT,
    ClubID INT NOT NULL,
    SubmittedBy VARCHAR(10),
    Term VARCHAR(10),
    SubmissionDate DATETIME,
    Status ENUM('PENDING', 'APPROVED') DEFAULT 'PENDING',
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID),
    FOREIGN KEY (SubmittedBy) REFERENCES Users(UserID),
   FOREIGN KEY (Term) REFERENCES Semesters(TermID)
);

CREATE TABLE PeriodicReport_MemberAchievements (
    AchievementID INT PRIMARY KEY AUTO_INCREMENT,
    ReportID INT,
    MemberID VARCHAR(10),
    Role VARCHAR(100), -- ví dụ: Chủ nhiệm, Trưởng ban,...
    ProgressPoint INT,
    FOREIGN KEY (ReportID) REFERENCES PeriodicClubReport(ReportID),
    FOREIGN KEY (MemberID) REFERENCES Users(UserID)
);

CREATE TABLE PeriodicReportEvents (
    ReportEventID INT PRIMARY KEY AUTO_INCREMENT,
    ReportID INT,
    EventName VARCHAR(100),
    EventDate DATETIME,
    EventType ENUM('Internal', 'Public'),
    ParticipantCount INT,
    ProofLink VARCHAR(100),
    FOREIGN KEY (ReportID) REFERENCES PeriodicClubReport(ReportID)
);


CREATE TABLE ClubCreationPermissions  (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    UserID VARCHAR(10) NOT NULL,
    ClubName VARCHAR(100) NOT NULL UNIQUE,
    Category ENUM('Thể Thao', 'Học Thuật', 'Phong Trào') DEFAULT 'Học Thuật',
    Status ENUM('PENDING', 'APPROVED', 'REJECTED', 'ACTIVE', 'USED', 'INACTIVE') DEFAULT 'PENDING',
    RequestDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ProcessedDate DATETIME,
    ProcessedBy VARCHAR(10),
    GrantedDate DATETIME,
    UsedDate DATETIME,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (ProcessedBy) REFERENCES Users(UserID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

INSERT INTO ClubCreationPermissions (UserID, ClubName, Category,  Status, RequestDate, ProcessedBy, GrantedDate, UsedDate)
VALUES
  ('U001', 'CLB Bóng Đá', 'Thể Thao', 'USED', '2025-01-01 10:00:00', 'U004', '2025-01-02 10:00:00', '2025-01-03 10:00:00'),
  ('U002', 'CLB Âm Nhạc', 'Phong Trào','USED', '2025-02-01 10:00:00', 'U004', '2025-02-02 10:00:00', '2025-02-03 10:00:00'),
  ('U003', 'CLB Kỹ Năng', 'Học Thuật', 'USED', '2025-03-01 10:00:00', 'U004', '2025-03-02 10:00:00', '2025-03-03 10:00:00');
-- Đơn trạng thái khác (CLB không trùng)
INSERT INTO ClubCreationPermissions (UserID, ClubName, Category, Status, RequestDate, ProcessedBy, GrantedDate)
VALUES
  ('U004', 'CLB Cờ Vua', 'Học Thuật', 'PENDING', '2025-06-20 10:00:00', NULL, NULL),
  ('U005', 'CLB Lập Trình', 'Học Thuật', 'PENDING', '2025-06-21 10:00:00', NULL, NULL),
  ('U001', 'CLB Nhiếp Ảnh', 'Phong Trào', 'APPROVED', '2025-06-15 10:00:00', 'U004', '2025-06-16 10:00:00'),
  ('U002', 'CLB Môi Trường', 'Phong Trào', 'REJECTED', '2025-06-10 10:00:00', 'U004', '2025-06-11 10:00:00');



CREATE TABLE ActivedMemberClubs (
    ActiveID INT PRIMARY KEY AUTO_INCREMENT,
    UserID VARCHAR(10) NOT NULL,
    ClubID INT NOT NULL,
    ActiveDate DATE NOT NULL,
    LeaveDate DATE DEFAULT NULL, -- NULL nếu vẫn còn hoạt động
    IsActive BOOLEAN DEFAULT TRUE,
    ProgressPoint INT DEFAULT NULL, -- Điểm rèn luyện, cho phép NULL
   FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID)
);

-- ================================================================================
-- ========================================
-- FAVORITES
-- ========================================
CREATE TABLE FavoriteClubs (
    FavoriteID INT PRIMARY KEY AUTO_INCREMENT,
    UserID VARCHAR(10) NOT NULL,
    ClubID INT NOT NULL,
    AddedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID) ON DELETE CASCADE,
    UNIQUE (UserID, ClubID) -- Đảm bảo mỗi người dùng chỉ thêm một CLB yêu thích một lần
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
INSERT INTO FavoriteClubs (UserID, ClubID, AddedDate) VALUES
('U001', 1, '2025-06-08 16:00:00'),
('U001', 3, '2025-06-08 16:05:00'),
('U002', 2, '2025-06-08 16:10:00'),
('U002', 5, '2025-06-08 16:15:00');

CREATE TABLE FavoriteEvents (
    FavoriteEventID INT PRIMARY KEY AUTO_INCREMENT,
    UserID VARCHAR(10) NOT NULL,
    EventID INT NOT NULL,
    AddedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (EventID) REFERENCES Events(EventID) ON DELETE CASCADE,
    UNIQUE (UserID, EventID) -- Đảm bảo mỗi người dùng chỉ thêm một sự kiện yêu thích một lần
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
INSERT INTO FavoriteEvents (UserID, EventID, AddedDate) VALUES
('U001', 1, '2025-06-15 10:00:00'),
('U001', 3, '2025-06-15 10:05:00'),
('U002', 2, '2025-06-15 10:10:00'),
('U002', 5, '2025-06-15 10:15:00');

-- ================================================================================
-- ========================================
-- FEEDBACK
-- ========================================
CREATE TABLE Feedbacks (
    FeedbackID INT PRIMARY KEY AUTO_INCREMENT,
    EventID INT NOT NULL,
    UserID VARCHAR(10) NOT NULL,
    IsAnonymous BOOLEAN DEFAULT FALSE,
    Rating INT NOT NULL CHECK (Rating BETWEEN 1 AND 5), -- Tổng quan
    Content TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    
    -- 9 câu hỏi đánh giá chi tiết (1–5 điểm)
    Q1_Organization INT CHECK (Q1_Organization BETWEEN 1 AND 5),
    Q2_Communication INT CHECK (Q2_Communication BETWEEN 1 AND 5),
    Q3_Support INT CHECK (Q3_Support BETWEEN 1 AND 5),
    Q4_Relevance INT CHECK (Q4_Relevance BETWEEN 1 AND 5),
    Q5_Welcoming INT CHECK (Q5_Welcoming BETWEEN 1 AND 5),
    Q6_Value INT CHECK (Q6_Value BETWEEN 1 AND 5),
    Q7_Timing INT CHECK (Q7_Timing BETWEEN 1 AND 5),
    Q8_Participation INT CHECK (Q8_Participation BETWEEN 1 AND 5),
    Q9_WillingnessToReturn INT CHECK (Q9_WillingnessToReturn BETWEEN 1 AND 5),

    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (EventID) REFERENCES Events(EventID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

INSERT INTO `Feedbacks` VALUES 
(1,13,'U005',0,4,'Sự kiện khá vui và thú vị',5,3,4,5,4,3,5,3,4,'2025-06-20 22:48:28'),
(2,13,'U006',1,2,'Tôi không thích sự kiện lắm, trải nghiệm của tôi khá tệ',2,1,3,2,1,3,2,4,3,'2025-06-21 00:58:14'),
(3,13,'U007',0,1,'Sự kiện không vui như tôi nghĩ',2,3,4,3,1,2,3,1,2,'2025-06-21 00:58:54'),
(4,13,'U008',0,3,'SỰ kiện ok',5,3,4,3,3,4,3,5,3,'2025-06-21 01:00:14');

INSERT INTO ClubMeeting (ClubID, URLMeeting, StartedTime)
VALUES (1, 'https://meet.example.com/club1', '2025-12-12 23:00:00');

INSERT INTO ClubApplications (UserID, ClubID, Email, Status, SubmitDate) VALUES

('U002', 1, 'b@gmail.com', 'PENDING', '2023-11-02 11:00:00'),
('U003', 1, 'c@gmail.com', 'PENDING', '2023-11-03 09:00:00'),
('U004', 1, 'd@gmail.com', 'PENDING', '2023-11-04 14:00:00');
INSERT INTO Notifications (Title, Content, CreatedDate, ReceiverID, Priority, Status) VALUES
('Thông báo đăng ký thành công', 'Bạn đã đăng ký tham gia sự kiện Giải Bóng Đá Sinh Viên 2025 thành công', '2025-06-23 10:00:00', 'U001', 'MEDIUM', 'UNREAD'),
('Thông báo sự kiện mới', 'Câu lạc bộ Âm Nhạc vừa tạo sự kiện mới: Đêm Nhạc Acoustic', '2025-06-26 09:00:00', 'U001', 'LOW', 'UNREAD'),
('Thông báo đăng ký thành công', 'Bạn đã đăng ký tham gia sự kiện Giải Bóng Đá Sinh Viên 2025 thành công', '2025-06-23 10:00:00', 'U002', 'MEDIUM', 'UNREAD'),
('Thông báo sự kiện mới', 'Câu lạc bộ Âm Nhạc vừa tạo sự kiện mới: Đêm Nhạc Acoustic', '2025-06-26 09:00:00', 'U002', 'LOW', 'UNREAD');

INSERT INTO Users (UserID, FullName, Email, Password, PermissionID, Status) VALUES
('HA178166', 'Nguyễn Văn An', 'annvha178166@fpt.edu.vn', '123456', 1, 1),
('HA188338', 'Trần Thị Bích', 'bichttha188338@fpt.edu.vn', '123456', 1, 1),
('HA186808', 'Lê Mai Hương', 'huonglmha186808@fpt.edu.vn', '123456', 1, 1),
('HS193994', 'Phạm Văn Cường', 'cuongpvhs193994@fpt.edu.vn', '123456', 1, 1),
('HS202370', 'Đỗ Thị Dung', 'dungdths202370@fpt.edu.vn', '123456', 1, 1),
('HS179821', 'Vũ Minh Hoa', 'hoavmhs179821@fpt.edu.vn', '123456', 1, 1),
('HA171144', 'Ngô Thế Hùng', 'hungntha171144@fpt.edu.vn', '123456', 1, 1),
('HS195569', 'Phạm Quốc Việt', 'vietpqhs195569@fpt.edu.vn', '123456', 1, 1),
('HA180043', 'Nguyễn Thị Lan', 'lanntha180043@fpt.edu.vn', '123456', 1, 1),
('HS189617', 'Lý Văn Tuấn', 'tuanlvhs189617@fpt.edu.vn', '123456', 1, 1),
('HA192435', 'Bùi Minh Trí', 'tribmha192435@fpt.edu.vn', '123456', 1, 1),
('HS207936', 'Hoàng Thị Hồng', 'honghths207936@fpt.edu.vn', '123456', 1, 1),
('HS189043', 'Đinh Văn Đức', 'ducdvhs189043@fpt.edu.vn', '123456', 1, 1),
('HA197461', 'Lê Thị Thu', 'thultha197461@fpt.edu.vn', '123456', 1, 1),
('HS173913', 'Nguyễn Minh Quân', 'quannmhs173913@fpt.edu.vn', '123456', 1, 1),
('HA193827', 'Trần Văn Khánh', 'khanhtvha193827@fpt.edu.vn', '123456', 1, 1),
('HS165214', 'Phạm Thị Hà', 'hapths165214@fpt.edu.vn', '123456', 1, 1),
('HA196247', 'Nguyễn Đức Anh', 'anhndha196247@fpt.edu.vn', '123456', 1, 1),
('HS178116', 'Vũ Thị Mai', 'maivths178116@fpt.edu.vn', '123456', 1, 1),
('HE178230', 'Lê Thanh Hùng', 'hunglthe178230@fpt.edu.vn', '123456', 1, 1),
('HA181645', 'Trần Đình Hòa', 'hoatdha181645@fpt.edu.vn', '123456', 1, 1),
('HS168618', 'Nguyễn Thị Yến', 'yennths168618@fpt.edu.vn', '123456', 1, 1),
('HS162362', 'Bùi Văn Nam', 'nambvhs162362@fpt.edu.vn', '123456', 1, 1),
('HS171226', 'Phạm Hồng Sơn', 'sonphhs171226@fpt.edu.vn', '123456', 1, 1),
('HS185547', 'Nguyễn Thị Hạnh', 'hanhnths185547@fpt.edu.vn', '123456', 1, 1),
('HA191316', 'Hoàng Văn Lâm', 'lamhvha191316@fpt.edu.vn', '123456', 1, 1),
('HS206227', 'Đoàn Thị Tuyết', 'tuyetdths206227@fpt.edu.vn', '123456', 1, 1),
('HS198865', 'Ngô Thị Hương', 'huongnths198865@fpt.edu.vn', '123456', 1, 1),
('HE201122', 'Trịnh Văn Thắng', 'thangtvhe201122@fpt.edu.vn', '123456', 1, 1),
('HE183728', 'Phan Thị Trang', 'trangpthe183728@fpt.edu.vn', '123456', 1, 1),
('HA167111', 'Nguyễn Văn An', 'annvha167111@fpt.edu.vn', '123456', 1, 1),
('HE167270', 'Trần Thị Bích', 'bichtthe167270@fpt.edu.vn', '123456', 1, 1),
('HE180475', 'Lê Mai Hương', 'huonglmhe180475@fpt.edu.vn', '123456', 1, 1),
('HE180920', 'Phạm Văn Cường', 'cuongpvhe180920@fpt.edu.vn', '123456', 1, 1),
('HS186142', 'Đỗ Thị Dung', 'dungdths186142@fpt.edu.vn', '123456', 1, 1),
('HS174001', 'Vũ Minh Hoa', 'hoavmhs174001@fpt.edu.vn', '123456', 1, 1),
('HA199273', 'Ngô Thế Hùng', 'hungntha199273@fpt.edu.vn', '123456', 1, 1),
('HA172786', 'Phạm Quốc Việt', 'vietpqha172786@fpt.edu.vn', '123456', 1, 1),
('HE169986', 'Nguyễn Thị Lan', 'lannthe169986@fpt.edu.vn', '123456', 1, 1),
('HS203946', 'Lý Văn Tuấn', 'tuanlvhs203946@fpt.edu.vn', '123456', 1, 1),
('HS202345', 'Bùi Minh Trí', 'tribmhs202345@fpt.edu.vn', '123456', 1, 1),
('HE194162', 'Hoàng Thị Hồng', 'honghthe194162@fpt.edu.vn', '123456', 1, 1),
('HS180154', 'Đinh Văn Đức', 'ducdvhs180154@fpt.edu.vn', '123456', 1, 1),
('HS188956', 'Lê Thị Thu', 'thulths188956@fpt.edu.vn', '123456', 1, 1),
('HE167238', 'Nguyễn Minh Quân', 'quannmhe167238@fpt.edu.vn', '123456', 1, 1),
('HS204902', 'Trần Văn Khánh', 'khanhtvhs204902@fpt.edu.vn', '123456', 1, 1),
('HA194098', 'Phạm Thị Hà', 'haptha194098@fpt.edu.vn', '123456', 1, 1),
('HS183264', 'Nguyễn Đức Anh', 'anhndhs183264@fpt.edu.vn', '123456', 1, 1),
('HS191748', 'Vũ Thị Mai', 'maivths191748@fpt.edu.vn', '123456', 1, 1),
('HE199371', 'Lê Thanh Hùng', 'hunglthe199371@fpt.edu.vn', '123456', 1, 1);

INSERT INTO UserClubs (UserID, ClubID, ClubDepartmentID, RoleID, JoinDate, IsActive) VALUES
('HA178166', 5, 16, 2, '2023-01-05', 1),
('HA178166', 6, 10, 1, '2024-06-19', 1),
('HA188338', 5, 9, 4, '2024-08-25', 1),
('HA188338', 7, 11, 4, '2023-11-01', 1),
('HA186808', 2, 6, 4, '2023-03-11', 1),
('HS193994', 1, 5, 4, '2023-03-07', 1),
('HS193994', 5, 16, 4, '2025-03-22', 1),
('HS202370', 4, 13, 4, '2025-01-27', 1),
('HS179821', 3, 7, 4, '2024-03-29', 1),
('HS179821', 8, 12, 4, '2024-03-07', 1),
('HA171144', 3, 7, 4, '2023-11-25', 1),
('HS195569', 2, 4, 2, '2023-08-12', 1),
('HS195569', 7, 11, 4, '2022-08-08', 1),
('HA180043', 6, 10, 4, '2024-05-10', 1),
('HA180043', 1, 5, 4, '2023-02-10', 1),
('HS189617', 8, 12, 4, '2024-07-27', 1),
('HA192435', 1, 1, 4, '2023-07-30', 1),
('HS207936', 2, 3, 4, '2024-04-26', 1),
('HS189043', 8, 12, 4, '2023-09-28', 1),
('HS189043', 3, 7, 4, '2024-12-23', 1),
('HA197461', 5, 16, 4, '2023-10-16', 1),
('HA197461', 1, 1, 4, '2024-02-05', 1),
('HS173913', 6, 10, 4, '2023-04-01', 1),
('HS173913', 1, 5, 3, '2023-06-25', 1),
('HA193827', 1, 5, 4, '2022-12-04', 1),
('HS165214', 2, 6, 4, '2025-01-28', 1),
('HA196247', 6, 10, 4, '2024-10-02', 1),
('HS178116', 3, 7, 4, '2024-04-22', 1),
('HS178116', 2, 6, 4, '2023-08-18', 1),
('HE178230', 6, 10, 4, '2022-10-04', 1),
('HA181645', 5, 9, 3, '2023-02-15', 1),
('HS168618', 2, 4, 4, '2024-07-11', 1),
('HS162362', 8, 12, 4, '2022-09-13', 1),
('HS171226', 8, 12, 3, '2023-10-31', 1),
('HS171226', 6, 10, 4, '2023-11-18', 1),
('HS185547', 8, 12, 4, '2022-08-06', 1),
('HS185547', 1, 1, 3, '2024-12-26', 1),
('HA191316', 7, 11, 4, '2024-11-21', 1),
('HA191316', 5, 9, 4, '2022-11-19', 1),
('HS206227', 7, 11, 4, '2024-02-13', 1),
('HS198865', 8, 12, 4, '2023-09-15', 1),
('HS198865', 4, 15, 4, '2023-05-04', 1),
('HE201122', 3, 7, 3, '2022-08-16', 1),
('HE201122', 6, 10, 4, '2023-10-09', 1),
('HE183728', 1, 2, 4, '2023-04-13', 1),
('HA167111', 2, 4, 4, '2022-08-19', 1),
('HA167111', 4, 8, 4, '2022-08-29', 1),
('HE167270', 2, 6, 4, '2025-02-04', 1),
('HE167270', 5, 9, 4, '2022-11-22', 1),
('HE180475', 2, 4, 4, '2022-09-17', 1),
('HE180475', 7, 11, 4, '2025-02-16', 1),
('HE180920', 3, 7, 4, '2023-10-10', 1),
('HS186142', 6, 10, 4, '2025-03-07', 1),
('HS186142', 1, 1, 4, '2022-08-05', 1),
('HS174001', 4, 15, 4, '2022-10-01', 1),
('HA199273', 6, 10, 4, '2024-01-13', 1),
('HA172786', 3, 7, 4, '2024-09-03', 1),
('HE169986', 5, 16, 4, '2023-09-06', 1),
('HS203946', 7, 11, 4, '2024-04-17', 1),
('HS203946', 3, 7, 4, '2025-02-11', 1),
('HS202345', 5, 9, 4, '2024-09-24', 1),
('HS202345', 1, 1, 4, '2023-09-22', 1),
('HE194162', 5, 16, 4, '2022-08-01', 1),
('HS180154', 7, 11, 4, '2025-01-20', 1),
('HS180154', 2, 6, 4, '2025-06-15', 1),
('HS188956', 7, 11, 4, '2024-02-10', 1),
('HE167238', 6, 10, 4, '2024-01-18', 1),
('HS204902', 2, 3, 4, '2023-04-20', 1),
('HS204902', 4, 15, 4, '2024-09-08', 1),
('HA194098', 7, 11, 4, '2022-10-14', 1),
('HS183264', 7, 11, 4, '2023-10-31', 1),
('HS191748', 3, 7, 4, '2023-03-13', 1),
('HS191748', 1, 5, 4, '2025-02-05', 1),
('HE199371', 2, 3, 4, '2024-12-23', 1);

INSERT INTO ActivedMemberClubs (UserID, ClubID, ActiveDate, LeaveDate, IsActive) VALUES
('HA178166', 5, '2025-05-01', NULL, TRUE),
('HA178166', 6, '2025-05-01', NULL, TRUE),
('HA188338', 5, '2025-05-01', NULL, TRUE),
('HA188338', 7, '2025-05-01', NULL, TRUE),
('HA186808', 2, '2025-05-01', NULL, TRUE),
('HS193994', 1, '2025-05-01', NULL, TRUE),
('HS193994', 5, '2025-05-01', NULL, TRUE),
('HS202370', 4, '2025-05-01', NULL, TRUE),
('HS179821', 3, '2025-05-01', NULL, TRUE),
('HS179821', 8, '2025-05-01', NULL, TRUE),
('HA171144', 3, '2025-05-01', NULL, TRUE),
('HS195569', 2, '2025-05-01', NULL, TRUE),
('HS195569', 7, '2025-05-01', NULL, TRUE),
('HA180043', 6, '2025-05-01', NULL, TRUE),
('HA180043', 1, '2025-05-01', NULL, TRUE),
('HS189617', 8, '2025-05-01', NULL, TRUE),
('HA192435', 1, '2025-05-01', NULL, TRUE),
('HS207936', 2, '2025-05-01', NULL, TRUE),
('HS189043', 8, '2025-05-01', NULL, TRUE),
('HS189043', 3, '2025-05-01', NULL, TRUE),
('HA197461', 5, '2025-05-01', NULL, TRUE),
('HA197461', 1, '2025-05-01', NULL, TRUE),
('HS173913', 6, '2025-05-01', NULL, TRUE),
('HS173913', 1, '2025-05-01', NULL, TRUE),
('HA193827', 1, '2025-05-01', NULL, TRUE),
('HS165214', 2, '2025-05-01', NULL, TRUE),
('HA196247', 6, '2025-05-01', NULL, TRUE),
('HS178116', 3, '2025-05-01', NULL, TRUE),
('HS178116', 2, '2025-05-01', NULL, TRUE),
('HE178230', 6, '2025-05-01', NULL, TRUE),
('HA181645', 5, '2025-05-01', NULL, TRUE),
('HS168618', 2, '2025-05-01', NULL, TRUE),
('HS162362', 8, '2025-05-01', NULL, TRUE),
('HS171226', 8, '2025-05-01', NULL, TRUE),
('HS171226', 6, '2025-05-01', NULL, TRUE),
('HS185547', 8, '2025-05-01', NULL, TRUE),
('HS185547', 1, '2025-05-01', NULL, TRUE),
('HA191316', 7, '2025-05-01', NULL, TRUE),
('HA191316', 5, '2025-05-01', NULL, TRUE),
('HS206227', 7, '2025-05-01', NULL, TRUE),
('HS198865', 8, '2025-05-01', NULL, TRUE),
('HS198865', 4, '2025-05-01', NULL, TRUE),
('HE201122', 3, '2025-05-01', NULL, TRUE),
('HE201122', 6, '2025-05-01', NULL, TRUE),
('HE183728', 1, '2025-05-01', NULL, TRUE),
('HA167111', 2, '2025-05-01', NULL, TRUE),
('HA167111', 4, '2025-05-01', NULL, TRUE),
('HE167270', 2, '2025-05-01', NULL, TRUE),
('HE167270', 5, '2025-05-01', NULL, TRUE),
('HE180475', 2, '2025-05-01', NULL, TRUE),
('HE180475', 7, '2025-05-01', NULL, TRUE),
('HE180920', 3, '2025-05-01', NULL, TRUE),
('HS186142', 6, '2025-05-01', NULL, TRUE),
('HS186142', 1, '2025-05-01', NULL, TRUE),
('HS174001', 4, '2025-05-01', NULL, TRUE),
('HA199273', 6, '2025-05-01', NULL, TRUE),
('HA172786', 3, '2025-05-01', NULL, TRUE),
('HE169986', 5, '2025-05-01', NULL, TRUE),
('HS203946', 7, '2025-05-01', NULL, TRUE),
('HS203946', 3, '2025-05-01', NULL, TRUE),
('HS202345', 5, '2025-05-01', NULL, TRUE),
('HS202345', 1, '2025-05-01', NULL, TRUE),
('HE194162', 5, '2025-05-01', NULL, TRUE),
('HS180154', 7, '2025-05-01', NULL, TRUE),
('HS180154', 2, '2025-05-01', NULL, TRUE),
('HS188956', 7, '2025-05-01', NULL, TRUE),
('HE167238', 6, '2025-05-01', NULL, TRUE),
('HS204902', 2, '2025-05-01', NULL, TRUE),
('HS204902', 4, '2025-05-01', NULL, TRUE),
('HA194098', 7, '2025-05-01', NULL, TRUE),
('HS183264', 7, '2025-05-01', NULL, TRUE),
('HS191748', 3, '2025-05-01', NULL, TRUE),
('HS191748', 1, '2025-05-01', NULL, TRUE),
('HE199371', 2, '2025-05-01', NULL, TRUE);


-- ========================================
-- Thêm người dùng mới (Users)
-- Tổng hợp các người dùng từ U036 đến U045, đảm bảo không trùng email hoặc UserID
-- ========================================
INSERT INTO Users (UserID, FullName, Email, Password, PermissionID, Status) VALUES
('U036', 'Lê Thị FF', 'ff@fpt.edu.vn', '123456', 1, 1),
('U037', 'Hoàng Văn GG', 'gg@fpt.edu.vn', '123456', 1, 1),
('U038', 'Bùi Thị HH', 'hh@fpt.edu.vn', '123456', 1, 1),
('U039', 'Ngô Văn II', 'ii@fpt.edu.vn', '123456', 1, 1),
('U040', 'Trịnh Thị JJ', 'jj@fpt.edu.vn', '123456', 1, 1),
('U041', 'Nguyễn Thị KK', 'kk@fpt.edu.vn', '123456', 1, 1),
('U042', 'Trần Văn LL', 'll@fpt.edu.vn', '123456', 1, 1),
('U043', 'Phạm Thị MM', 'mm@fpt.edu.vn', '123456', 1, 1),
('U044', 'Lê Văn NN', 'nn@fpt.edu.vn', '123456', 1, 1),
('U045', 'Đỗ Thị OO', 'oo@fpt.edu.vn', '123456', 1, 1);

-- ========================================
-- Liên kết người dùng với câu lạc bộ và ban (UserClubs)
-- Đảm bảo không trùng UserID với ClubID và ClubDepartmentID
-- ========================================
INSERT INTO UserClubs (UserID, ClubID, ClubDepartmentID, RoleID, JoinDate, IsActive) VALUES
('U036', 1, 5, 4, '2025-07-01 00:00:00', 1),  -- Thành viên Ban Hậu cần CLB Bóng Đá
('U037', 2, 4, 4, '2025-07-01 00:00:00', 1),  -- Thành viên Ban Chuyên môn CLB Bóng Rổ
('U038', 3, 7, 4, '2025-07-01 00:00:00', 1),  -- Thành viên Ban Chủ nhiệm CLB Tiếng Anh
('U039', 4, 13, 4, '2025-07-01 00:00:00', 1), -- Thành viên Ban Chuyên môn CLB Lập Trình
('U040', 1, 2, 4, '2025-07-01 00:00:00', 1),  -- Thành viên Ban Truyền thông CLB Bóng Đá
('U041', 1, 5, 4, '2025-07-10 00:00:00', 1),  -- Thành viên Ban Hậu cần CLB Bóng Đá
('U042', 1, 5, 4, '2025-07-10 00:00:00', 1),  -- Thành viên Ban Hậu cần CLB Bóng Đá
('U043', 1, 5, 4, '2025-07-10 00:00:00', 1),  -- Thành viên Ban Hậu cần CLB Bóng Đá
('U044', 1, 5, 4, '2025-07-10 00:00:00', 1),  -- Thành viên Ban Hậu cần CLB Bóng Đá
('U045', 1, 5, 4, '2025-07-10 00:00:00', 1);  -- Thành viên Ban Hậu cần CLB Bóng Đá

-- ========================================
-- Thêm nhiệm vụ mới (Tasks)
-- Nhiệm vụ liên quan đến Ban Hậu cần và các CLB khác, đảm bảo không trùng Title
-- ========================================
INSERT INTO Tasks (ParentTaskID, EventID, ClubID, Title, Description, Status, Priority, ProgressPercent, StartDate, EndDate, CreatedBy) VALUES
(NULL, (SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Đá Sinh Viên FPTU 2025'), 1, 'Sắp xếp lịch thi đấu', 'Lên kế hoạch lịch thi đấu cho giải', 'ToDo', 'HIGH', 0, '2025-08-05 08:00:00', '2025-08-07 17:00:00', 'U001'),
(NULL, NULL, 2, 'Chuẩn bị tài liệu huấn luyện', 'Soạn tài liệu cho buổi huấn luyện', 'ToDo', 'MEDIUM', 0, '2025-07-02 09:00:00', '2025-07-04 17:00:00', 'U003'),
(NULL, (SELECT EventID FROM Events WHERE EventName = 'Hội thảo AI 2025'), 4, 'Liên hệ diễn giả', 'Mời diễn giả tham gia hội thảo', 'ToDo', 'HIGH', 0, '2025-09-01 08:00:00', '2025-09-03 17:00:00', 'U012'),
(NULL, (SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Đá Sinh Viên FPTU 2025'), 1, 'Chuẩn bị nước uống', 'Sắp xếp nước uống cho các đội thi đấu', 'ToDo', 'HIGH', 0, '2025-08-03 08:00:00', '2025-08-05 12:00:00', 'U002'),
(NULL, (SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Đá Sinh Viên FPTU 2025'), 1, 'Kiểm tra dụng cụ y tế', 'Kiểm tra và bổ sung hộp sơ cứu', 'ToDo', 'MEDIUM', 0, '2025-08-04 09:00:00', '2025-08-05 10:00:00', 'U002'),
(NULL, NULL, 1, 'Dọn dẹp sân sau trận đấu', 'Dọn dẹp sân bãi sau các trận đấu', 'ToDo', 'MEDIUM', 0, '2025-08-05 16:00:00', '2025-08-05 18:00:00', 'U002');

-- ========================================
-- Phân công nhiệm vụ (TaskAssignees)
-- Phân công nhiệm vụ cho các thành viên hoặc ban, đảm bảo không trùng TaskID và UserID/DepartmentID
-- ========================================
INSERT INTO TaskAssignees (TaskID, AssigneeType, UserID, DepartmentID) VALUES
((SELECT TaskID FROM Tasks WHERE Title = 'Sắp xếp lịch thi đấu'), 'User', 'U036', NULL),
((SELECT TaskID FROM Tasks WHERE Title = 'Chuẩn bị tài liệu huấn luyện'), 'Department', NULL, 4),
((SELECT TaskID FROM Tasks WHERE Title = 'Liên hệ diễn giả'), 'User', 'U039', NULL),
((SELECT TaskID FROM Tasks WHERE Title = 'Chuẩn bị nước uống'), 'User', 'U041', NULL),
((SELECT TaskID FROM Tasks WHERE Title = 'Chuẩn bị nước uống'), 'User', 'U042', NULL),
((SELECT TaskID FROM Tasks WHERE Title = 'Kiểm tra dụng cụ y tế'), 'User', 'U043', NULL),
((SELECT TaskID FROM Tasks WHERE Title = 'Dọn dẹp sân sau trận đấu'), 'User', 'U044', NULL),
((SELECT TaskID FROM Tasks WHERE Title = 'Dọn dẹp sân sau trận đấu'), 'User', 'U045', NULL);

-- ========================================
-- Ghi lại tiến độ nhiệm vụ (TaskProgressLogs)
-- Cập nhật tiến độ cho các nhiệm vụ, đảm bảo không trùng TaskID và UserID
-- ========================================
INSERT INTO TaskProgressLogs (TaskID, UserID, Progress, Note) VALUES
((SELECT TaskID FROM Tasks WHERE Title = 'Sắp xếp lịch thi đấu'), 'U036', 40, 'Đã sắp xếp 40% lịch thi đấu'),
((SELECT TaskID FROM Tasks WHERE Title = 'Chuẩn bị tài liệu huấn luyện'), 'U037', 60, 'Tài liệu huấn luyện hoàn thành 60%'),
((SELECT TaskID FROM Tasks WHERE Title = 'Liên hệ diễn giả'), 'U039', 25, 'Đã liên hệ được 1 diễn giả'),
((SELECT TaskID FROM Tasks WHERE Title = 'Chuẩn bị nước uống'), 'U041', 50, 'Đã đặt mua 50% số lượng nước cần thiết'),
((SELECT TaskID FROM Tasks WHERE Title = 'Chuẩn bị nước uống'), 'U042', 30, 'Đang liên hệ nhà cung cấp nước'),
((SELECT TaskID FROM Tasks WHERE Title = 'Kiểm tra dụng cụ y tế'), 'U043', 70, 'Đã kiểm tra xong hộp sơ cứu, cần bổ sung băng gạc'),
((SELECT TaskID FROM Tasks WHERE Title = 'Dọn dẹp sân sau trận đấu'), 'U044', 0, 'Chưa bắt đầu, chờ sau trận đấu'),
((SELECT TaskID FROM Tasks WHERE Title = 'Dọn dẹp sân sau trận đấu'), 'U045', 0, 'Chưa bắt đầu, chờ sau trận đấu');

-- ========================================
-- Phản hồi nhiệm vụ (TaskFeedbacks)
-- Trưởng ban hoặc chủ nhiệm đưa ra phản hồi, đảm bảo không trùng TaskID và ReviewerID
-- ========================================
INSERT INTO TaskFeedbacks (TaskID, ReviewerID, Rating, Comment, ProgressAdjustment) VALUES
((SELECT TaskID FROM Tasks WHERE Title = 'Sắp xếp lịch thi đấu'), 'U001', 'Positive', 'Công việc đang đi đúng hướng', 10),
((SELECT TaskID FROM Tasks WHERE Title = 'Chuẩn bị tài liệu huấn luyện'), 'U003', 'Neutral', 'Cần chú ý chi tiết hơn', 0),
((SELECT TaskID FROM Tasks WHERE Title = 'Liên hệ diễn giả'), 'U012', 'Positive', 'Tốt, tiếp tục nỗ lực', 5),
((SELECT TaskID FROM Tasks WHERE Title = 'Chuẩn bị nước uống'), 'U002', 'Positive', 'Tốt, hãy đảm bảo đủ số lượng trước ngày thi đấu', 5),
((SELECT TaskID FROM Tasks WHERE Title = 'Kiểm tra dụng cụ y tế'), 'U002', 'Positive', 'Công việc ổn, cần bổ sung nhanh các vật dụng còn thiếu', 10),
((SELECT TaskID FROM Tasks WHERE Title = 'Dọn dẹp sân sau trận đấu'), 'U002', 'Neutral', 'Chưa bắt đầu, cần chuẩn bị nhân sự sẵn sàng', 0);

-- ========================================
-- Thêm thông tin cuộc họp câu lạc bộ (ClubMeeting)
-- Đảm bảo không trùng ClubID và StartedTime
-- ========================================
INSERT INTO ClubMeeting (ClubID, URLMeeting, StartedTime) VALUES
(1, 'https://meet.example.com/club1_meeting3', '2025-07-03'),
(2, 'https://meet.example.com/club4_meeting2', '2025-07-04');

-- ========================================
-- Thêm thông báo (Notifications)
-- Đảm bảo không trùng ReceiverID, Title, và CreatedDate
-- ========================================
INSERT INTO Notifications (Title, Content, CreatedDate, ReceiverID, Priority, Status) VALUES
('Nhắc nhở nhiệm vụ', 'Sắp xếp lịch thi đấu cần hoàn thành trước 07/08/2025', '2025-07-01 08:00:00', 'U036', 'HIGH', 'UNREAD'),
('Thông báo nhiệm vụ', 'Chuẩn bị tài liệu huấn luyện trước 04/07/2025', '2025-07-01 08:00:00', 'U037', 'MEDIUM', 'UNREAD'),
('Nhắc nhở nhiệm vụ', 'Liên hệ diễn giả trước 03/09/2025', '2025-07-01 08:00:00', 'U039', 'HIGH', 'UNREAD'),
('Nhắc nhở nhiệm vụ', 'Chuẩn bị nước uống cho giải bóng đá cần hoàn thành trước 05/08/2025', '2025-08-01 08:00:00', 'U041', 'HIGH', 'UNREAD'),
('Nhắc nhở nhiệm vụ', 'Chuẩn bị nước uống cho giải bóng đá cần hoàn thành trước 05/08/2025', '2025-08-01 08:00:00', 'U042', 'HIGH', 'UNREAD'),
('Nhắc nhở nhiệm vụ', 'Kiểm tra dụng cụ y tế trước 05/08/2025', '2025-08-01 08:00:00', 'U043', 'MEDIUM', 'UNREAD'),
('Nhắc nhở nhiệm vụ', 'Dọn dẹp sân sau trận đấu vào 05/08/2025', '2025-08-01 08:00:00', 'U044', 'MEDIUM', 'UNREAD'),
('Nhắc nhở nhiệm vụ', 'Dọn dẹp sân sau trận đấu vào 05/08/2025', '2025-08-01 08:00:00', 'U045', 'MEDIUM', 'UNREAD'),
('Nhắc nhở cuộc họp', 'Cuộc họp Ban Hậu cần vào 02/08/2025 lúc 14:00', '2025-08-01 08:00:00', 'U041', 'HIGH', 'UNREAD'),
('Nhắc nhở cuộc họp', 'Cuộc họp Ban Hậu cần vào 02/08/2025 lúc 14:00', '2025-08-01 08:00:00', 'U042', 'HIGH', 'UNREAD'),
('Nhắc nhở cuộc họp', 'Cuộc họp Ban Hậu cần vào 02/08/2025 lúc 14:00', '2025-08-01 08:00:00', 'U043', 'HIGH', 'UNREAD'),
('Nhắc nhở cuộc họp', 'Cuộc họp Ban Hậu cần vào 02/08/2025 lúc 14:00', '2025-08-01 08:00:00', 'U044', 'HIGH', 'UNREAD'),
('Nhắc nhở cuộc họp', 'Cuộc họp Ban Hậu cần vào 02/08/2025 lúc 14:00', '2025-08-01 08:00:00', 'U045', 'HIGH', 'UNREAD');

-- ========================================
-- Thêm đơn đăng ký (ClubApplications)
-- Đảm bảo không trùng UserID và ClubID
-- ========================================
INSERT INTO ClubApplications (UserID, ClubID, Email, Status, SubmitDate) VALUES
('U036', 1, 'ff@fpt.edu.vn', 'APPROVED', '2025-06-25 10:00:00'),
('U037', 2, 'gg@fpt.edu.vn', 'APPROVED', '2025-06-25 10:00:00'),
('U039', 4, 'ii@fpt.edu.vn', 'APPROVED', '2025-06-25 10:00:00'),
('U041', 1, 'kk@fpt.edu.vn', 'APPROVED', '2025-07-05 10:00:00'),
('U042', 1, 'll@fpt.edu.vn', 'APPROVED', '2025-07-05 10:00:00'),
('U043', 1, 'mm@fpt.edu.vn', 'APPROVED', '2025-07-05 10:00:00'),
('U044', 1, 'nn@fpt.edu.vn', 'APPROVED', '2025-07-05 10:00:00'),
('U045', 1, 'oo@fpt.edu.vn', 'APPROVED', '2025-07-05 10:00:00');

-- ========================================
-- Thêm phản hồi cho sự kiện (Feedbacks)
-- Đảm bảo không trùng EventID và UserID
-- ========================================
INSERT INTO Feedbacks (EventID, UserID, IsAnonymous, Rating, Content, Q1_Organization, Q2_Communication, Q3_Support, Q4_Relevance, Q5_Welcoming, Q6_Value, Q7_Timing, Q8_Participation, Q9_WillingnessToReturn, CreatedAt) VALUES
((SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Đá Sinh Viên FPTU 2025'), 'U041', 0, 4, 'Sự kiện rất sôi động, cần cải thiện khâu hậu cần', 4, 4, 3, 5, 4, 4, 4, 4, 4, '2025-08-06 10:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Đá Sinh Viên FPTU 2025'), 'U042', 0, 5, 'Tuyệt vời, mọi thứ được tổ chức tốt', 5, 5, 5, 5, 5, 5, 5, 5, 5, '2025-08-06 10:05:00'),
((SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Đá Sinh Viên FPTU 2025'), 'U043', 1, 3, 'Cần chuẩn bị kỹ hơn về nước uống', 3, 3, 2, 4, 3, 3, 3, 3, 3, '2025-08-06 10:10:00');

-- ========================================
-- Thêm thành viên hoạt động (ActivedMemberClubs)
-- Đảm bảo không trùng UserID và ClubID
-- ========================================
INSERT INTO ActivedMemberClubs (UserID, ClubID, ActiveDate, IsActive) VALUES
('U036', 1, '2025-07-01', TRUE),
('U037', 2, '2025-07-01', TRUE),
('U038', 3, '2025-07-01', TRUE),
('U039', 4, '2025-07-01', TRUE),
('U040', 1, '2025-07-01', TRUE),
('U041', 1, '2025-07-10', TRUE),
('U042', 1, '2025-07-10', TRUE),
('U043', 1, '2025-07-10', TRUE),
('U044', 1, '2025-07-10', TRUE),
('U045', 1, '2025-07-10', TRUE);


