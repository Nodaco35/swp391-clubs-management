

 
-- ========================================
-- CREATE DATABASE - MYSQL
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
('SU25', 'Summer 2025', '2025-05-01', '2025-08-31', 'ACTIVE');



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
    RoleID INT PRIMARY KEY,
    RoleName VARCHAR(20) NOT NULL,
    Description VARCHAR(100) 
);
INSERT INTO Roles (RoleID, RoleName, Description) VALUES
(1, 'Chủ nhiệm', 'Chủ nhiệm chính thức CLB, chịu trách nhiệm quản lý toàn bộ CLB'),
(2, 'Phó chủ nhiệm', 'Hỗ trợ chủ nhiệm quản lý CLB'),
(3, 'Trưởng ban', 'Quản lý các ban trong câu lạc bộ’'),
(4, 'Thành viên', 'Thành viên chính thức của CLB');

CREATE TABLE ClubCategories(
	CategoryID INT PRIMARY KEY AUTO_INCREMENT,
	CategoryName varchar(30)
);
INSERT INTO ClubCategories(CategoryName) VALUES ('Học thuật'), ('Phong trào'), ('Thể thao');

CREATE TABLE Clubs (
    ClubID INT PRIMARY KEY AUTO_INCREMENT,
    ClubImg VARCHAR(255),
    IsRecruiting BOOLEAN DEFAULT 1,
    ClubName VARCHAR(100) NOT NULL UNIQUE,
    Description TEXT,
    CategoryID INT,
    EstablishedDate DATE,
    ContactPhone VARCHAR(20),
    ContactGmail VARCHAR(50) UNIQUE NOT NULL,
    ContactURL VARCHAR(150),
    ClubStatus BOOLEAN DEFAULT 1,

    LastRejectReason TEXT NULL,
    ParentClubID INT NULL,
    ClubRequestStatus ENUM('Pending', 'Rejected', 'Approved', 'Done') DEFAULT 'Done',
    CurrentRequestType ENUM('Create', 'Update') DEFAULT NULL,
    UpdateRequestNote TEXT NULL,
    
    FOREIGN KEY (CategoryID) REFERENCES ClubCategories(CategoryID)
);

INSERT INTO Clubs (ClubImg, IsRecruiting, ClubName, Description, EstablishedDate, ContactPhone, ContactGmail, ContactURL, ClubStatus, CategoryID)
VALUES
('/images/clubs/football.jpg', FALSE, 'Câu Lạc Bộ Bóng Đá', 'Nơi quy tụ những sinh viên đam mê bóng đá, thường xuyên tổ chức giao lưu, thi đấu và rèn luyện thể chất.', '2018-05-10', '0901234567', 'football@club.com', 'https://facebook.com/footballclub', TRUE, 3),
('/images/clubs/basketball.jpg', FALSE, 'Câu Lạc Bộ Bóng Rổ', 'Câu lạc bộ dành cho các bạn yêu thích bóng rổ, tổ chức tập luyện và thi đấu thường xuyên trong và ngoài trường.', '2019-03-15', '0912345678', 'basketball@club.com', 'https://facebook.com/basketballclub', TRUE, 3),
('/images/clubs/english.jpg', FALSE, 'Câu Lạc Bộ Tiếng Anh', 'Nơi giúp sinh viên cải thiện kỹ năng tiếng Anh qua các buổi thảo luận, trò chơi, và giao lưu với người nước ngoài.', '2017-09-20', '0923456789', 'english@club.com', 'https://facebook.com/englishclub', TRUE, 1),
('/images/clubs/programming.jpg', FALSE, 'Câu Lạc Bộ Lập Trình', 'Tạo môi trường cho sinh viên học hỏi, chia sẻ kiến thức lập trình, thực hành dự án và tham gia các cuộc thi công nghệ.', '2020-01-10', '0934567890', 'programming@club.com', 'https://facebook.com/programmingclub', TRUE, 1),
('/images/clubs/volunteer.jpg', FALSE, 'Câu Lạc Bộ Tình Nguyện', 'Tổ chức các hoạt động thiện nguyện, hỗ trợ cộng đồng và lan tỏa yêu thương tới những hoàn cảnh khó khăn.', '2016-07-05', '0945678901', 'volunteer@club.com', 'https://facebook.com/volunteerclub', TRUE, 2),
('/images/clubs/music.jpg', FALSE, 'Câu Lạc Bộ Âm Nhạc', 'Không gian cho những tâm hồn yêu âm nhạc thể hiện đam mê qua các buổi trình diễn và sự kiện nghệ thuật.', '2018-11-15', '0956789012', 'music@club.com', 'https://facebook.com/musicclub', TRUE, 2),
('/images/clubs/dance.jpg', FALSE, 'Câu Lạc Bộ Nhảy', 'Quy tụ các bạn trẻ yêu thích nhảy múa, từ hiện đại đến truyền thống, tham gia biểu diễn trong các sự kiện lớn nhỏ.', '2019-08-20', '0967890123', 'dance@club.com', 'https://facebook.com/danceclub', TRUE, 2),
('/images/clubs/debate.jpg', FALSE, 'Câu Lạc Bộ Tranh Biện', 'Phát triển tư duy phản biện, kỹ năng trình bày và lập luận thông qua các buổi tranh biện học thuật.', '2020-02-25', '0978901234', 'debate@club.com', 'https://facebook.com/debateclub', TRUE, 1);



-- Cập nhật giá trị để thực hiện chức năng duyệt Câu lạc bộ
UPDATE Clubs
SET 
    ClubStatus = 0,
    ClubRequestStatus = 'Pending',
    CurrentRequestType = 'Create',
    UpdateRequestNote = 'CLB thử nghiệm để test chức năng duyệt',
    LastRejectReason = NULL
WHERE ClubID = 6;

CREATE TABLE ClubApprovalHistory (
    HistoryID INT PRIMARY KEY AUTO_INCREMENT,
    ClubID INT NOT NULL,

    ActionType ENUM('Pending','Approved', 'Rejected') NOT NULL,
    Reason TEXT,
    RequestType ENUM('Create', 'Update') DEFAULT NULL,
    ActionAt DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID)  ON DELETE CASCADE
);
INSERT INTO ClubApprovalHistory (
    ClubID,
    ActionType,
    Reason,
    RequestType
) VALUES (
    6, -- ClubID bạn vừa set
    'Pending',
    'CLB thử nghiệm để test chức năng duyệt',
    'Create'
);


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
(6, 4),  -- ClubDepartmentID = 18 → Ban Đối ngoại của CLB 4
(2, 3),  -- ClubDepartmentID = 19 → Ban Truyền thông cho CLB 3
(4, 6);  -- ClubDepartmentID = 20 → Ban Chuyên môn của CLB 6

CREATE TABLE UserClubs (
    UserClubID INT PRIMARY KEY AUTO_INCREMENT,
    UserID VARCHAR(10) NOT NULL,
    ClubID INT NOT NULL,
    ClubDepartmentID INT NOT NULL,
    RoleID INT NOT NULL,
    JoinDate DATE NOT NULL,
    IsActive BOOLEAN DEFAULT 1,
    Gen INT,  -- Thêm trường gen kiểu INT ở đây
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID),
    FOREIGN KEY (ClubDepartmentID) REFERENCES ClubDepartments(ClubDepartmentID),
    FOREIGN KEY (RoleID) REFERENCES Roles(RoleID)
);
INSERT INTO UserClubs (UserID, ClubID, ClubDepartmentID, RoleID, JoinDate, IsActive) VALUES
('U001', 1, 1, 1, '2020-09-01', 1),
('U001', 4, 14, 4, '2022-09-01', 1),
('U001', 3, 19, 3, '2022-10-01', 1),
('U002', 1, 5, 3, '2021-01-15', 1),
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
('U029', 1, 2, 4, '2022-01-01', 1),
('U029', 2, 6, 4, '2022-02-01', 1),
('U029', 4, 15, 3, '2022-03-01', 1),
('U030', 4, 15, 4, '2021-05-06', 1);

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
('HA178166', 6, 10, 4, '2024-06-19', 1),
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

-- Cập nhật giá trị cho trường gen dựa trên năm hiện tại và năm thành lập của câu lạc bộ
UPDATE UserClubs uc
JOIN Clubs c ON uc.ClubID = c.ClubID
SET uc.Gen = YEAR(uc.JoinDate) - YEAR(c.EstablishedDate) + 1
WHERE c.EstablishedDate IS NOT NULL;

-- ========================================
-- APPLICATIONS
-- ========================================
CREATE TABLE ApplicationForms (
    FormID INT PRIMARY KEY AUTO_INCREMENT,
    ClubID INT NOT NULL,
    EventID INT,
    FormType ENUM('Club', 'Event', 'Other') NOT NULL,
    Title VARCHAR(100) NOT NULL,
    Published BOOLEAN NOT NULL DEFAULT 0,
    CONSTRAINT fk_form_club FOREIGN KEY (ClubID) 
        REFERENCES Clubs(ClubID) ON DELETE CASCADE
	)CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
INSERT INTO ApplicationForms (FormID, ClubID, EventID, FormType, Title, Published) 
VALUES 
(1, 1, NULL, 'Club', 'Đơn đăng ký tham gia CLB bóng đá', 1), 
(2, 1, NULL, 'Club', 'Đơn đăng ký thành viên clb đá bóng', 1), 
(3, 1, 18, 'Event', 'Đơn đăng ký tham gia giải bóng đá 5x5', 1); 

CREATE TABLE ApplicationFormTemplates (
    TemplateID INT PRIMARY KEY AUTO_INCREMENT,
    FormID INT NOT NULL,
    FieldName VARCHAR(50),
    FieldType ENUM('Text', 'Textarea', 'Number', 'Date', 'Checkbox', 'Info', 'Email', 'Radio'),
    IsRequired BOOLEAN DEFAULT 1,
    Options LONGTEXT,
    DisplayOrder INT DEFAULT 0,
    CONSTRAINT fk_template_form FOREIGN KEY (FormID) 
        REFERENCES ApplicationForms(FormID) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;



INSERT INTO ApplicationFormTemplates (TemplateID, FormID, FieldName, FieldType, IsRequired, Options, DisplayOrder) 
VALUES 
(1, 1, 'Chọn ban muốn vào CLB', 'Radio', 1, 'Ban Hậu cần;;Ban Truyền thông', 0), 
(2, 1, 'Lý do bạn chọn CLB này', 'Text', 1, NULL, 1), 
(3, 1, 'Vị trí bạn có thể chơi', 'Checkbox', 1, 'Hậu vệ;;Tiền vệ;;Trung vệ;;Tiền đạo;;Thủ môn', 2), 
(4, 1, '', 'Info', 0, '{"content":"Cảm ơn bạn đã tham gia đăng ký","image":""}', 3), 
(5, 2, 'Chọn ban muốn vào CLB', 'Radio', 1, 'Ban Chủ nhiệm;;Ban Hậu cần;;Ban Truyền thông', 0), 
(6, 2, 'Câu hỏi mới', 'Text', 1, NULL, 1), 
(7, 3, '','Info', 0, '{"content":"Sự kiện đá bóng thường niên của CLB bóng đá","image":""}', 0), 
(8, 3, 'Vị trí bạn có thể chơi', 'Checkbox', 1, 'Hậu vệ;;Tiền vệ;;Trung vệ;;Tiền đạo;;Thủ môn', 1), 
(9, 3, 'Sở trường của bạn', 'Text', 1, NULL, 2), 
(10, 3, '', 'Info', 0, '{"content":"Cảm ơn bạn đã tham gia đăng ký","image":""}', 3); 
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
    ClubID INT NOT NULL, 
    IsPublic BOOLEAN DEFAULT 0,
    FormID INT,
    Capacity INT,
    Status ENUM('Pending', 'Processing', 'Completed'),
    ApprovalStatus ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    RejectionReason TEXT,
    SemesterID VARCHAR(10),
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID),
    FOREIGN KEY (FormID) REFERENCES ApplicationForms(FormID),
    FOREIGN KEY (SemesterID) REFERENCES Semesters(TermID)
);
DELIMITER $$
CREATE TRIGGER trg_auto_approve_private_event_insert
BEFORE INSERT ON Events
FOR EACH ROW
BEGIN
    IF NEW.IsPublic = FALSE THEN
        SET NEW.ApprovalStatus = 'APPROVED';
    END IF;
END$$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER trg_auto_handle_approval_status_update
BEFORE UPDATE ON Events
FOR EACH ROW
BEGIN
    -- If changing from private to public => needs re-approval
    IF NEW.IsPublic = TRUE AND OLD.IsPublic = FALSE THEN
        SET NEW.ApprovalStatus = 'PENDING';
    END IF;

    -- If changing from public to private => auto-approved
    IF NEW.IsPublic = FALSE AND OLD.IsPublic = TRUE AND NEW.ApprovalStatus <> 'APPROVED' THEN
        SET NEW.ApprovalStatus = 'APPROVED';
    END IF;
END$$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER trg_reset_event_approval_on_event_edit
BEFORE UPDATE ON Events
FOR EACH ROW
BEGIN
    IF OLD.ApprovalStatus = 'REJECTED' AND (
        OLD.EventName <> NEW.EventName OR
        OLD.Description <> NEW.Description OR
        OLD.Capacity <> NEW.Capacity
    ) THEN
        SET NEW.ApprovalStatus = 'PENDING';
    END IF;
END$$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER trg_reset_agendas_after_event_approval_pending
AFTER UPDATE ON Events
FOR EACH ROW
BEGIN
    IF OLD.ApprovalStatus = 'REJECTED' AND NEW.ApprovalStatus = 'PENDING' THEN
        UPDATE Agenda a
        JOIN EventSchedules es ON a.ScheduleID = es.ScheduleID
        SET a.Status = 'PENDING'
        WHERE es.EventID = NEW.EventID AND a.Status = 'REJECTED';
    END IF;
END$$
DELIMITER ;

-- Insert vào Events
INSERT INTO Events (EventName, EventImg, Description, ClubID, IsPublic, Capacity, Status, SemesterID, ApprovalStatus, RejectionReason) VALUES
-- Spring 2025
('Lễ Hội Âm Nhạc Tết 2025', 'images/events/tetmusic2025.jpg', 'Lễ hội âm nhạc chào đón Tết 2025 với nhiều tiết mục đặc sắc.', 6, TRUE, 300, 'COMPLETED', 'SP25', 'APPROVED', NULL),
('Hội Thảo Kỹ Năng Viết Tiếng Anh', 'images/events/englishworkshop2025.jpg', 'Hội thảo nâng cao kỹ năng viết tiếng Anh cho sinh viên.', 3, TRUE, 60, 'COMPLETED', 'SP25', 'APPROVED', NULL),
('Cuộc Thi Tranh Biện Xã Hội 2025', 'images/events/debate2025.jpg', 'Cuộc thi tranh biện về các vấn đề xã hội nóng hổi.', 8, TRUE, 100, 'COMPLETED', 'SP25', 'APPROVED', NULL),
('Workshop: Xây dựng Website với Spring Boot', 'images/events/springboot_workshop.jpg', 'Học cách xây dựng website với Spring Boot.', 4, FALSE, 100, 'COMPLETED', 'SP25', 'APPROVED', NULL),
-- Summer 2025
('FPTU Showcase 2025 Chung Kết', 'images/events/showcase2025.jpg', 'Chung kết cuộc thi trình diễn tài năng FPTU.', 7, TRUE, 200, 'PENDING', 'SU25', 'PENDING', NULL),
('Thử Thách Lập Trình FPTU 2025', 'images/events/coding2025.jpg', 'Cuộc thi lập trình dành cho sinh viên.', 4, TRUE, 50, 'COMPLETED', 'SU25', 'APPROVED', NULL),
('Giải Bóng Đá Sinh Viên FPTU 2025', 'images/events/football2025.jpg', 'Giải bóng đá sinh viên FPTU.', 1, TRUE, 120, 'PENDING', 'SU25', 'PENDING', NULL),
('Chiến Dịch Tình Nguyện Xanh 2025', 'images/events/greenfuture2025.jpg', 'Chiến dịch bảo vệ môi trường.', 5, TRUE, 40, 'PENDING', 'SU25', 'PENDING', NULL),
('Giải Bóng Rổ 3x3 FPTU', 'images/events/basketball2025.jpg', 'Giải bóng rổ 3x3 dành cho sinh viên.', 2, FALSE, 80, 'COMPLETED', 'SU25', 'APPROVED', NULL),
('Ngày Hội Âm Nhạc FPTU 2025', 'images/events/musicday2025.jpg', 'Ngày hội âm nhạc với các ban nhạc sinh viên.', 6, FALSE, 250, 'PENDING', 'SU25', 'PENDING', NULL),
('Hackathon Đổi Mới AI', 'images/events/aihackathon2025.jpg', 'Cuộc thi lập trình AI trong 2 ngày.', 4, TRUE, 45, 'PENDING', 'SU25', 'PENDING', NULL),
('Cuộc thi Code War: Thử thách thuật toán', 'images/events/codewar.jpg', 'Cuộc thi giải thuật toán.', 4, TRUE, 120, 'PENDING', 'SU25', 'PENDING', NULL),
('Hội Thảo Công Nghệ Blockchain 2025', 'images/events/blockchain2025.jpg', 'Hội thảo về công nghệ blockchain với lý thuyết và thực hành.', 4, TRUE, 80, 'PENDING', 'SU25', 'PENDING', NULL),
('Cuộc Thi Thiết Kế Đồ Họa 2025', 'images/events/designcontest2025.jpg', 'Cuộc thi thiết kế đồ họa với các vòng thi.', 7, TRUE, 60, 'PENDING', 'SU25', 'PENDING', NULL),
-- Spring 2024
('Lễ Hội Làng Tết 2024', 'images/events/villagefest2024.jpg', 'Lễ hội truyền thống Tết 2024.', 6, TRUE, 500, 'COMPLETED', 'SP24', 'APPROVED', NULL),
-- Summer 2024
('Chiến Dịch Tình Nguyện Hè 2024', 'images/events/summervolunteer2024.jpg', 'Chiến dịch tình nguyện mùa hè.', 5, FALSE, 35, 'COMPLETED', 'SU24', 'APPROVED', NULL),
-- Fall 2024
('Trại Lập Trình FPTU 2024', 'images/events/codingbootcamp2024.jpg', 'Trại huấn luyện lập trình.', 4, TRUE, 60, 'COMPLETED', 'FA24', 'APPROVED', NULL),
('Đêm Nhạc Rock 2024', 'images/events/rocknight2024.jpg', 'Đêm nhạc rock sôi động.', 6, TRUE, 200, 'COMPLETED', 'FA24', 'APPROVED', NULL),
('Cuộc Thi Nói Tiếng Anh 2024', 'images/events/englishcontest2024.jpg', 'Cuộc thi nói tiếng Anh.', 3, TRUE, 70, 'COMPLETED', 'FA24', 'APPROVED', NULL),
('Giải Bóng Đá 5x5 FPTU 2024', 'images/events/football2024.jpg', 'Giải bóng đá 5x5.', 1, FALSE, 100, 'COMPLETED', 'FA24', 'APPROVED', NULL),
('Ngày Hội Nhảy FPTU 2024', 'images/events/dancefest2024.jpg', 'Ngày hội nhảy hiện đại.', 7, TRUE, 400, 'COMPLETED', 'FA24', 'APPROVED', NULL),
('Hội Thảo Phát Triển Web 2024', 'images/events/webdev2024.jpg', 'Hội thảo phát triển web.', 4, TRUE, 50, 'COMPLETED', 'FA24', 'APPROVED', NULL),
('FPTU Dance Battle 2024', 'images/events/dancebattle2024.jpg', 'Cuộc thi nhảy đối kháng.', 7, TRUE, 150, 'COMPLETED', 'FA24', 'APPROVED', NULL),
('Cuộc Thi Tranh Biện 2024', 'images/events/debate2024.jpg', 'Cuộc thi tranh biện.', 8, TRUE, 65, 'COMPLETED', 'FA24', 'APPROVED', NULL);


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
SET Description = 'Hội Thảo Công Nghệ Blockchain 2025 là sự kiện chuyên sâu dành cho sinh viên yêu thích công nghệ chuỗi khối. Chương trình gồm các phiên thảo luận về ứng dụng thực tiễn của blockchain trong tài chính, giáo dục, và quản lý dữ liệu. Diễn giả là chuyên gia đến từ các công ty công nghệ hàng đầu sẽ chia sẻ kiến thức nền tảng, phân tích xu hướng tương lai, và trình diễn các giải pháp blockchain hiện đại. Người tham dự có cơ hội thực hành qua các workshop, kết nối mạng lưới chuyên môn, và định hướng nghề nghiệp trong lĩnh vực tiềm năng này.'
WHERE EventName = 'Hội Thảo Công Nghệ Blockchain 2025' AND SemesterID = 'SU25';
UPDATE Events 
SET Description = 'Cuộc Thi Thiết Kế Đồ Họa 2025 là sân chơi sáng tạo dành cho sinh viên đam mê mỹ thuật số và thiết kế truyền thông. Thí sinh sẽ tranh tài qua nhiều vòng thi như thiết kế poster, giao diện ứng dụng, và nhận diện thương hiệu. Ban giám khảo là các chuyên gia thiết kế sẽ đánh giá dựa trên tính sáng tạo, kỹ thuật và khả năng truyền tải thông điệp. Bên cạnh cuộc thi, còn có các workshop hướng dẫn sử dụng công cụ thiết kế hiện đại và chia sẻ kinh nghiệm nghề nghiệp, tạo điều kiện phát triển kỹ năng chuyên môn và xây dựng portfolio.'
WHERE EventName = 'Cuộc Thi Thiết Kế Đồ Họa 2025' AND SemesterID = 'SU25';

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



CREATE TABLE EventSchedules (
    ScheduleID INT PRIMARY KEY AUTO_INCREMENT,
    EventID INT NOT NULL,
    EventDate DATE NOT NULL,
    LocationID INT NOT NULL,
    StartTime TIME NOT NULL,
    EndTime TIME NOT NULL,
    FOREIGN KEY (EventID) REFERENCES Events(EventID),
    FOREIGN KEY (LocationID) REFERENCES Locations(LocationID)
);
INSERT INTO EventSchedules (EventID, EventDate, LocationID, StartTime, EndTime) VALUES
-- Spring 2025 (1 ngày, 1 địa điểm)
((SELECT EventID FROM Events WHERE EventName = 'Lễ Hội Âm Nhạc Tết 2025' AND SemesterID = 'SP25'), '2025-02-01', 2, '19:00:00', '22:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Hội Thảo Kỹ Năng Viết Tiếng Anh' AND SemesterID = 'SP25'), '2025-03-05', 9, '09:00:00', '11:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Cuộc Thi Tranh Biện Xã Hội 2025' AND SemesterID = 'SP25'), '2025-04-10', 3, '13:00:00', '15:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Workshop: Xây dựng Website với Spring Boot' AND SemesterID = 'SP25'), '2025-04-20', 26, '14:00:00', '17:00:00'),
-- Summer 2025
-- FPTU Showcase 2025 Chung Kết: 1 ngày, 2 địa điểm
((SELECT EventID FROM Events WHERE EventName = 'FPTU Showcase 2025 Chung Kết' AND SemesterID = 'SU25'), '2025-08-02', 1, '09:00:00', '12:00:00'), -- Hội trường
((SELECT EventID FROM Events WHERE EventName = 'FPTU Showcase 2025 Chung Kết' AND SemesterID = 'SU25'), '2025-08-02', 2, '13:00:00', '17:00:00'), -- Sảnh tòa Delta
-- Thử Thách Lập Trình FPTU 2025: 2 ngày, 1 địa điểm
((SELECT EventID FROM Events WHERE EventName = 'Thử Thách Lập Trình FPTU 2025' AND SemesterID = 'SU25'), '2025-06-21', 25, '08:00:00', '17:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Thử Thách Lập Trình FPTU 2025' AND SemesterID = 'SU25'), '2025-06-22', 25, '08:00:00', '12:00:00'),
-- Giải Bóng Đá Sinh Viên FPTU 2025: 1 ngày, 1 địa điểm
((SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Đá Sinh Viên FPTU 2025' AND SemesterID = 'SU25'), '2025-08-05', 5, '08:00:00', '17:00:00'),
-- Chiến Dịch Tình Nguyện Xanh 2025: 1 ngày, 2 địa điểm
((SELECT EventID FROM Events WHERE EventName = 'Chiến Dịch Tình Nguyện Xanh 2025' AND SemesterID = 'SU25'), '2025-08-06', 35, '07:00:00', '12:00:00'), -- Khu vực nông thôn Đà Nẵng
((SELECT EventID FROM Events WHERE EventName = 'Chiến Dịch Tình Nguyện Xanh 2025' AND SemesterID = 'SU25'), '2025-08-06', 36, '13:00:00', '17:00:00'), -- Tỉnh Tuyên Quang
-- Giải Bóng Rổ 3x3 FPTU: 1 ngày, 1 địa điểm
((SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Rổ 3x3 FPTU' AND SemesterID = 'SU25'), '2025-07-05', 4, '14:00:00', '18:00:00'),
-- Ngày Hội Âm Nhạc FPTU 2025: 1 ngày, 1 địa điểm
((SELECT EventID FROM Events WHERE EventName = 'Ngày Hội Âm Nhạc FPTU 2025' AND SemesterID = 'SU25'), '2025-08-08', 1, '10:00:00', '22:00:00'),
-- Hackathon Đổi Mới AI: 2 ngày, 1 địa điểm
((SELECT EventID FROM Events WHERE EventName = 'Hackathon Đổi Mới AI' AND SemesterID = 'SU25'), '2025-08-12', 25, '08:00:00', '20:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Hackathon Đổi Mới AI' AND SemesterID = 'SU25'), '2025-08-13', 25, '08:00:00', '14:00:00'),
-- Cuộc thi Code War: Thử thách thuật toán: 1 ngày, 1 địa điểm
((SELECT EventID FROM Events WHERE EventName = 'Cuộc thi Code War: Thử thách thuật toán' AND SemesterID = 'SU25'), '2025-07-31', 27, '09:00:00', '17:00:00'),
-- Hội Thảo Công Nghệ Blockchain 2025: 1 ngày, 2 địa điểm
((SELECT EventID FROM Events WHERE EventName = 'Hội Thảo Công Nghệ Blockchain 2025' AND SemesterID = 'SU25'), '2025-08-15', 3, '09:00:00', '11:00:00'), -- Hội trường tầng 5 tòa Gamma
((SELECT EventID FROM Events WHERE EventName = 'Hội Thảo Công Nghệ Blockchain 2025' AND SemesterID = 'SU25'), '2025-08-15', 25, '11:30:00', '13:30:00'), -- Phòng BE-101
-- Cuộc Thi Thiết Kế Đồ Họa 2025: 2 ngày, 2 địa điểm
((SELECT EventID FROM Events WHERE EventName = 'Cuộc Thi Thiết Kế Đồ Họa 2025' AND SemesterID = 'SU25'), '2025-08-20', 9, '09:00:00', '17:00:00'), -- Phòng AL-L101
((SELECT EventID FROM Events WHERE EventName = 'Cuộc Thi Thiết Kế Đồ Họa 2025' AND SemesterID = 'SU25'), '2025-08-21', 10, '09:00:00', '14:00:00'), -- Phòng AL-L201
-- Spring 2024
((SELECT EventID FROM Events WHERE EventName = 'Lễ Hội Làng Tết 2024' AND SemesterID = 'SP24'), '2024-01-20', 36, '10:00:00', '22:00:00'),
-- Summer 2024
((SELECT EventID FROM Events WHERE EventName = 'Chiến Dịch Tình Nguyện Hè 2024' AND SemesterID = 'SU24'), '2024-07-15', 36, '07:00:00', '17:00:00'),
-- Fall 2024
((SELECT EventID FROM Events WHERE EventName = 'Trại Lập Trình FPTU 2024' AND SemesterID = 'FA24'), '2024-11-20', 25, '08:00:00', '17:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Đêm Nhạc Rock 2024' AND SemesterID = 'FA24'), '2024-12-10', 1, '19:00:00', '22:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Cuộc Thi Nói Tiếng Anh 2024' AND SemesterID = 'FA24'), '2024-11-25', 10, '09:00:00', '17:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Đá 5x5 FPTU 2024' AND SemesterID = 'FA24'), '2024-12-05', 5, '14:00:00', '18:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Ngày Hội Nhảy FPTU 2024' AND SemesterID = 'FA24'), '2024-12-15', 2, '10:00:00', '17:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Hội Thảo Phát Triển Web 2024' AND SemesterID = 'FA24'), '2024-11-15', 11, '14:00:00', '17:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'FPTU Dance Battle 2024' AND SemesterID = 'FA24'), '2024-11-30', 1, '18:00:00', '22:00:00'),
((SELECT EventID FROM Events WHERE EventName = 'Cuộc Thi Tranh Biện 2024' AND SemesterID = 'FA24'), '2024-12-01', 12, '09:00:00', '17:00:00');


CREATE TABLE Agenda (
    AgendaID INT PRIMARY KEY AUTO_INCREMENT,
    ScheduleID INT NOT NULL,
    Title VARCHAR(100) NOT NULL,
    Description TEXT,
    StartTime DATETIME NOT NULL,
    EndTime DATETIME NOT NULL,
    Status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    Reason TEXT,
    FOREIGN KEY (ScheduleID) REFERENCES EventSchedules(ScheduleID)
);


DELIMITER $$
CREATE TRIGGER trg_auto_approve_agenda_for_private_event
BEFORE INSERT ON Agenda
FOR EACH ROW
BEGIN
    DECLARE eventIsPublic BOOLEAN;

    SELECT e.IsPublic INTO eventIsPublic
    FROM Events e
    JOIN EventSchedules es ON e.EventID = es.EventID
    WHERE es.ScheduleID = NEW.ScheduleID;

    IF eventIsPublic = FALSE THEN
        SET NEW.Status = 'APPROVED';
    END IF;
END$$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER trg_set_agenda_pending_on_public_event
AFTER UPDATE ON Events
FOR EACH ROW
BEGIN
    IF NEW.IsPublic = TRUE AND OLD.IsPublic = FALSE THEN
        UPDATE Agenda a
        JOIN EventSchedules es ON a.ScheduleID = es.ScheduleID
        SET a.Status = 'PENDING'
        WHERE es.EventID = NEW.EventID AND a.Status != 'REJECTED';
    END IF;
END$$
DELIMITER ;
-- Insert vào Agenda
-- Spring 2025: Lễ Hội Âm Nhạc Tết 2025 (1 ngày, 1 địa điểm)
INSERT INTO Agenda (ScheduleID, Title, Description, StartTime, EndTime, Status) VALUES
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Lễ Hội Âm Nhạc Tết 2025' AND SemesterID = 'SP25') AND LocationID = 2), 'Khai mạc lễ hội', 'Phát biểu khai mạc và giới thiệu chương trình', '2025-02-01 19:00:00', '2025-02-01 19:30:00', 'APPROVED'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Lễ Hội Âm Nhạc Tết 2025' AND SemesterID = 'SP25') AND LocationID = 2), 'Biểu diễn văn nghệ', 'Các tiết mục văn nghệ đặc sắc', '2025-02-01 19:30:00', '2025-02-01 21:30:00', 'APPROVED'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Lễ Hội Âm Nhạc Tết 2025' AND SemesterID = 'SP25') AND LocationID = 2), 'Bế mạc và trao giải', 'Tổng kết và trao giải cho các tiết mục xuất sắc', '2025-02-01 21:30:00', '2025-02-01 22:00:00', 'APPROVED');

-- Summer 2025: FPTU Showcase 2025 Chung Kết (1 ngày, 2 địa điểm)
INSERT INTO Agenda (ScheduleID, Title, Description, StartTime, EndTime, Status) VALUES
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'FPTU Showcase 2025 Chung Kết' AND SemesterID = 'SU25') AND LocationID = 1), 'Khai mạc tại hội trường', 'Phát biểu khai mạc và giới thiệu chương trình', '2025-08-02 09:00:00', '2025-08-02 09:30:00', 'PENDING'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'FPTU Showcase 2025 Chung Kết' AND SemesterID = 'SU25') AND LocationID = 1), 'Triển lãm sản phẩm', 'Trưng bày và giới thiệu sản phẩm', '2025-08-02 09:30:00', '2025-08-02 12:00:00', 'PENDING'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'FPTU Showcase 2025 Chung Kết' AND SemesterID = 'SU25') AND LocationID = 2), 'Giao lưu với khách mời', 'Gặp gỡ và giao lưu với các khách mời đặc biệt', '2025-08-02 13:00:00', '2025-08-02 14:30:00', 'PENDING'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'FPTU Showcase 2025 Chung Kết' AND SemesterID = 'SU25') AND LocationID = 2), 'Biểu diễn nghệ thuật', 'Các tiết mục biểu diễn đặc sắc', '2025-08-02 14:30:00', '2025-08-02 17:00:00', 'PENDING');

-- Summer 2025: Thử Thách Lập Trình FPTU 2025 (2 ngày, 1 địa điểm)
INSERT INTO Agenda (ScheduleID, Title, Description, StartTime, EndTime, Status) VALUES
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Thử Thách Lập Trình FPTU 2025' AND SemesterID = 'SU25') AND EventDate = '2025-06-21'), 'Check-in & Khai mạc', 'Đăng ký tham dự và phát biểu khai mạc', '2025-06-21 08:00:00', '2025-06-21 08:30:00', 'APPROVED'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Thử Thách Lập Trình FPTU 2025' AND SemesterID = 'SU25') AND EventDate = '2025-06-21'), 'Thi vòng 1', 'Các thí sinh tham gia thi vòng loại', '2025-06-21 08:30:00', '2025-06-21 12:00:00', 'APPROVED'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Thử Thách Lập Trình FPTU 2025' AND SemesterID = 'SU25') AND EventDate = '2025-06-21'), 'Thi vòng 2', 'Các thí sinh tiếp tục thi vòng 2', '2025-06-21 13:00:00', '2025-06-21 17:00:00', 'APPROVED'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Thử Thách Lập Trình FPTU 2025' AND SemesterID = 'SU25') AND EventDate = '2025-06-22'), 'Thi vòng chung kết', 'Các thí sinh xuất sắc thi đấu vòng chung kết', '2025-06-22 08:00:00', '2025-06-22 10:30:00', 'APPROVED'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Thử Thách Lập Trình FPTU 2025' AND SemesterID = 'SU25') AND EventDate = '2025-06-22'), 'Trao giải và bế mạc', 'Tổng kết và trao giải cho các thí sinh xuất sắc', '2025-06-22 10:30:00', '2025-06-22 12:00:00', 'APPROVED');

-- Summer 2025: Chiến Dịch Tình Nguyện Xanh 2025 (1 ngày, 2 địa điểm)
INSERT INTO Agenda (ScheduleID, Title, Description, StartTime, EndTime, Status) VALUES
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Chiến Dịch Tình Nguyện Xanh 2025' AND SemesterID = 'SU25') AND LocationID = 35), 'Tập trung và phân công', 'Phổ biến kế hoạch và phân công nhiệm vụ', '2025-08-06 07:00:00', '2025-08-06 07:30:00', 'PENDING'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Chiến Dịch Tình Nguyện Xanh 2025' AND SemesterID = 'SU25') AND LocationID = 35), 'Dọn dẹp khu vực chính', 'Thu gom rác và làm sạch môi trường', '2025-08-06 07:30:00', '2025-08-06 12:00:00', 'PENDING'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Chiến Dịch Tình Nguyện Xanh 2025' AND SemesterID = 'SU25') AND LocationID = 36), 'Trồng cây xanh', 'Trồng cây và chăm sóc cây xanh', '2025-08-06 13:00:00', '2025-08-06 15:00:00', 'PENDING'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Chiến Dịch Tình Nguyện Xanh 2025' AND SemesterID = 'SU25') AND LocationID = 36), 'Tổng kết chiến dịch', 'Đánh giá kết quả và rút kinh nghiệm', '2025-08-06 15:00:00', '2025-08-06 17:00:00', 'PENDING');

-- Summer 2025: Giải Bóng Rổ 3x3 FPTU (1 ngày, 1 địa điểm)
INSERT INTO Agenda (ScheduleID, Title, Description, StartTime, EndTime, Status) VALUES
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Rổ 3x3 FPTU' AND SemesterID = 'SU25') AND LocationID = 4), 'Check-in & Phân đội', 'Đăng ký và phân đội thi đấu', '2025-07-05 14:00:00', '2025-07-05 14:30:00', 'APPROVED'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Rổ 3x3 FPTU' AND SemesterID = 'SU25') AND LocationID = 4), 'Thi đấu vòng loại', 'Các đội thi đấu vòng loại', '2025-07-05 14:30:00', '2025-07-05 16:00:00', 'APPROVED'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Rổ 3x3 FPTU' AND SemesterID = 'SU25') AND LocationID = 4), 'Thi đấu chung kết', 'Các đội xuất sắc thi đấu chung kết', '2025-07-05 16:00:00', '2025-07-05 17:30:00', 'APPROVED'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Giải Bóng Rổ 3x3 FPTU' AND SemesterID = 'SU25') AND LocationID = 4), 'Trao giải', 'Trao giải và chụp ảnh lưu niệm', '2025-07-05 17:30:00', '2025-07-05 18:00:00', 'APPROVED');

-- Summer 2025: Hackathon Đổi Mới AI (2 ngày, 1 địa điểm)
INSERT INTO Agenda (ScheduleID, Title, Description, StartTime, EndTime, Status) VALUES
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Hackathon Đổi Mới AI' AND SemesterID = 'SU25') AND EventDate = '2025-08-12'), 'Khai mạc và phổ biến thể lệ', 'Giới thiệu chương trình và phổ biến quy định', '2025-08-12 08:00:00', '2025-08-12 08:30:00', 'PENDING'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Hackathon Đổi Mới AI' AND SemesterID = 'SU25') AND EventDate = '2025-08-12'), 'Lập trình và phát triển sản phẩm', 'Các đội thực hiện phát triển sản phẩm', '2025-08-12 08:30:00', '2025-08-12 20:00:00', 'PENDING'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Hackathon Đổi Mới AI' AND SemesterID = 'SU25') AND EventDate = '2025-08-13'), 'Hoàn thiện sản phẩm', 'Các đội hoàn thiện và chuẩn bị trình bày', '2025-08-13 08:00:00', '2025-08-13 11:00:00', 'PENDING'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Hackathon Đổi Mới AI' AND SemesterID = 'SU25') AND EventDate = '2025-08-13'), 'Trình bày sản phẩm và trao giải', 'Các đội trình bày sản phẩm và nhận giải thưởng', '2025-08-13 11:00:00', '2025-08-13 14:00:00', 'PENDING');

-- Summer 2025: Hội Thảo Công Nghệ Blockchain 2025 (1 ngày, 2 địa điểm)
INSERT INTO Agenda (ScheduleID, Title, Description, StartTime, EndTime, Status) VALUES
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Hội Thảo Công Nghệ Blockchain 2025' AND SemesterID = 'SU25') AND LocationID = 3), 'Khai mạc và giới thiệu', 'Phát biểu khai mạc và giới thiệu về blockchain', '2025-08-15 09:00:00', '2025-08-15 09:30:00', 'PENDING'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Hội Thảo Công Nghệ Blockchain 2025' AND SemesterID = 'SU25') AND LocationID = 3), 'Phần lý thuyết: Ứng dụng blockchain', 'Giới thiệu ứng dụng blockchain trong thực tế', '2025-08-15 09:30:00', '2025-08-15 10:30:00', 'PENDING'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Hội Thảo Công Nghệ Blockchain 2025' AND SemesterID = 'SU25') AND LocationID = 3), 'Q&A lý thuyết', 'Hỏi đáp về nội dung lý thuyết', '2025-08-15 10:30:00', '2025-08-15 11:00:00', 'PENDING'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Hội Thảo Công Nghệ Blockchain 2025' AND SemesterID = 'SU25') AND LocationID = 25), 'Thực hành: Xây dựng smart contract', 'Thực hành lập trình smart contract cơ bản', '2025-08-15 11:30:00', '2025-08-15 12:30:00', 'PENDING'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Hội Thảo Công Nghệ Blockchain 2025' AND SemesterID = 'SU25') AND LocationID = 25), 'Trình bày kết quả thực hành', 'Các nhóm trình bày smart contract đã xây dựng', '2025-08-15 12:30:00', '2025-08-15 13:00:00', 'PENDING'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Hội Thảo Công Nghệ Blockchain 2025' AND SemesterID = 'SU25') AND LocationID = 25), 'Tổng kết và chụp ảnh', 'Tổng kết hội thảo, chụp ảnh lưu niệm', '2025-08-15 13:00:00', '2025-08-15 13:30:00', 'PENDING');

-- Summer 2025: Cuộc Thi Thiết Kế Đồ Họa 2025 (2 ngày, 2 địa điểm)
INSERT INTO Agenda (ScheduleID, Title, Description, StartTime, EndTime, Status) VALUES
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Cuộc Thi Thiết Kế Đồ Họa 2025' AND SemesterID = 'SU25') AND EventDate = '2025-08-20'), 'Khai mạc và phổ biến thể lệ', 'Giới thiệu cuộc thi và quy định', '2025-08-20 09:00:00', '2025-08-20 09:30:00', 'PENDING'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Cuộc Thi Thiết Kế Đồ Họa 2025' AND SemesterID = 'SU25') AND EventDate = '2025-08-20'), 'Thiết kế vòng sơ loại', 'Thí sinh thực hiện bài thiết kế sơ loại', '2025-08-20 09:30:00', '2025-08-20 12:00:00', 'PENDING'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Cuộc Thi Thiết Kế Đồ Họa 2025' AND SemesterID = 'SU25') AND EventDate = '2025-08-20'), 'Đánh giá vòng sơ loại', 'Ban giám khảo đánh giá và chọn đội vào chung kết', '2025-08-20 13:00:00', '2025-08-20 17:00:00', 'PENDING'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Cuộc Thi Thiết Kế Đồ Họa 2025' AND SemesterID = 'SU25') AND EventDate = '2025-08-21'), 'Thiết kế vòng chung kết', 'Thí sinh thực hiện bài thiết kế chung kết', '2025-08-21 09:00:00', '2025-08-21 11:30:00', 'PENDING'),
((SELECT ScheduleID FROM EventSchedules WHERE EventID = (SELECT EventID FROM Events WHERE EventName = 'Cuộc Thi Thiết Kế Đồ Họa 2025' AND SemesterID = 'SU25') AND EventDate = '2025-08-21'), 'Trình bày và trao giải', 'Thí sinh trình bày sản phẩm và nhận giải', '2025-08-21 11:30:00', '2025-08-21 14:00:00', 'PENDING');


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
(15,'U002','REGISTERED'),
(15,'U005','REGISTERED'),
(15,'U006','REGISTERED'),
(15,'U007','REGISTERED'),
(15,'U008','REGISTERED');

update EventParticipants set Status = 'ATTENDED' where UserID = 'U002';

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
(11, 'Trước sự kiện', '2025-08-01', '2025-08-11'),
(11, 'Trong sự kiện', '2025-08-12', '2025-08-12'),
(11, 'Sau sự kiện', '2025-08-13', '2025-08-14'),
-- Giai đoạn cho Code War
(12, 'Trước sự kiện', '2025-07-20', '2025-07-30'),
(12, 'Trong sự kiện', '2025-07-31', '2025-07-31'),
(12, 'Sau sự kiện', '2025-08-01', '2025-08-02');

CREATE TABLE Tasks (
    TaskID INT PRIMARY KEY AUTO_INCREMENT,
    ParentTaskID INT, -- NULL nếu là nhiệm vụ dành cho ban, không null - nhiệm vụ cho thành viên trong từng ban
    TermID INT,
    EventID INT,

    AssigneeType ENUM('User', 'Department') NOT NULL,
    UserID VARCHAR(10),
    DepartmentID INT,
    ClubID INT,

    Title VARCHAR(100) NOT NULL,
    Description TEXT,
    Content TEXT,

    Status ENUM('ToDo', 'InProgress', 'Review', 'Rejected','Done') DEFAULT 'ToDo',
    Rating ENUM('Positive', 'Neutral', 'Negative') ,
    LastRejectReason TEXT NULL,

    ReviewComment TEXT,
    StartDate DATETIME,
    EndDate DATETIME,
    CreatedBy VARCHAR(10),
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (EventID) REFERENCES Events(EventID),
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID),
    FOREIGN KEY (CreatedBy) REFERENCES Users(UserID),
    FOREIGN KEY (TermID) REFERENCES EventTerms(TermID),

    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (DepartmentID) REFERENCES Departments(DepartmentID),

    CHECK (
        (AssigneeType = 'User' AND UserID IS NOT NULL AND DepartmentID IS NULL) OR
        (AssigneeType = 'Department' AND DepartmentID IS NOT NULL AND UserID IS NULL)
    )
);
INSERT INTO Tasks (ParentTaskID, TermID, EventID, ClubID, Title, Description, Status, StartDate, EndDate, CreatedBy, AssigneeType, DepartmentID)
VALUES
(NULL, 1, 11, 4, 'Thiết kế poster sự kiện', 'Thiết kế và duyệt poster truyền thông cho sự kiện Hackathon AI.', 'Done', '2025-07-05 08:00:00', '2025-07-10 17:00:00', 'U012', 'Department', 2),
(NULL, 1, 11, 4, 'Chuẩn bị đề bài Hackathon', 'Xây dựng bộ đề thi cho Hackathon AI', 'Review', '2025-07-01 09:00:00', '2025-07-14 17:00:00', 'U012', 'Department', 1),
(NULL, 2, 11, 4, 'Hỗ trợ kỹ thuật tại sự kiện', 'Giải quyết các sự cố kỹ thuật trong suốt sự kiện', 'ToDo', '2025-07-15 07:30:00', '2025-07-15 14:00:00', 'U012', 'Department', 5),
(NULL, 2, 11, 4, 'Ghi hình & Livestream sự kiện', 'Phụ trách quay video và livestream trên fanpage CLB.', 'Rejected', '2025-07-15 07:45:00', '2025-07-15 14:00:00', 'U012', 'Department', 2),
(NULL, 3, 11, 4, 'Tổng kết kết quả và gửi email cảm ơn', 'Tổng hợp kết quả thi, gửi thư cảm ơn đến người tham gia', 'ToDo', '2025-07-16 09:00:00', '2025-07-17 17:00:00', 'U012', 'Department', 1),

-- Code War
(NULL, 4, 12, 4, 'Ra đề và chuẩn bị test case', 'Xây dựng đề thi và bộ test case cho cuộc thi Code War.', 'InProgress', '2025-07-20 08:00:00', '2025-07-30 17:00:00', 'U012', 'Department', 1),
(NULL, 5, 12, 4, 'Quản lý hệ thống thi và hỗ trợ thí sinh', 'Theo dõi hệ thống thi, xử lý lỗi kỹ thuật và hỗ trợ thí sinh khi cần.', 'ToDo', '2025-07-31 08:00:00', '2025-07-31 13:00:00', 'U012', 'Department', 5);

-- Tasks for individual members of Ban Hậu cần (ClubDepartmentID=5) of CLB 1
INSERT INTO Tasks (ParentTaskID, TermID, EventID, ClubID, Title, Description, Status, StartDate, EndDate, CreatedBy, AssigneeType, UserID)
VALUES
(NULL, NULL, NULL, 1, 'Chuẩn bị dụng cụ thể thao', 'Kiểm tra và chuẩn bị bóng đá, áo thi đấu cho hoạt động tuần', 'InProgress', '2025-07-23 08:00:00', '2025-07-25 17:00:00', 'U002', 'User', 'U007'),
(NULL, NULL, NULL, 1, 'Tổ chức dọn dẹp phòng CLB', 'Dọn dẹp và sắp xếp lại không gian hoạt động của câu lạc bộ', 'ToDo', '2025-07-24 14:00:00', '2025-07-26 16:00:00', 'U002', 'User', 'HS193994'),
(NULL, NULL, NULL, 1, 'Mua sắm đồ ăn nhẹ cho buổi họp', 'Chuẩn bị đồ ăn nhẹ và nước uống cho buổi họp hàng tuần', 'Done', '2025-07-22 09:00:00', '2025-07-23 12:00:00', 'U002', 'User', 'HA180043'),
(NULL, NULL, NULL, 1, 'Kiểm tra tình trạng ghế bàn', 'Kiểm tra và sửa chữa bàn ghế hư hỏng trong phòng sinh hoạt', 'Review', '2025-07-23 10:00:00', '2025-07-24 17:00:00', 'U002', 'User', 'HA193827'),
(NULL, NULL, NULL, 1, 'Chuẩn bị backdrop cho sự kiện', 'Thiết kế và in backdrop cho sự kiện sắp tới của CLB', 'InProgress', '2025-07-25 08:00:00', '2025-07-28 17:00:00', 'U002', 'User', 'HS191748');




CREATE TABLE TaskFeedbacks (
    FeedbackID INT PRIMARY KEY AUTO_INCREMENT,
    TaskID INT NOT NULL,

    Comment TEXT,
    Status ENUM('ToDo', 'InProgress', 'Review', 'Rejected','Done') DEFAULT 'ToDo',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (TaskID) REFERENCES Tasks(TaskID)
);

CREATE TABLE ApplicationResponses (
    ResponseID INT PRIMARY KEY AUTO_INCREMENT,
    FormID INT NOT NULL,
    UserID VARCHAR(10) NOT NULL,
    ClubID INT NOT NULL, -- Có thể cân nhắc xóa nếu không cần
    EventID INT,         -- Có thể cân nhắc xóa nếu không cần
    Responses TEXT NOT NULL,
    Status ENUM('Pending', 'Candidate', 'Collaborator', 'Approved', 'Rejected') DEFAULT 'Pending',
    SubmitDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (FormID) REFERENCES ApplicationForms(FormID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (EventID) REFERENCES Events(EventID) ON DELETE SET NULL ON UPDATE CASCADE
);



INSERT INTO ApplicationResponses VALUES 
(1,1,'U005',1,NULL,'{\"1\":{\"type\":\"radio\",\"value\":\"Ban Hậu cần\",\"fieldId\":\"1\"},\"2\":\"Yêu CLB\",\"3\":{\"type\":\"checkbox\",\"values\":[\"Tiền vệ\",\"Trung vệ\",\"Tiền đạo\"],\"fieldId\":\"3\"}}','Candidate','2025-07-12 00:11:45'),
(2,1,'U012',1,NULL,'{\"1\":{\"type\":\"radio\",\"value\":\"Ban Truyền thông\",\"fieldId\":\"1\"},\"2\":\"Đam mê bóng đá, thể dục thể thao\",\"3\":{\"type\":\"checkbox\",\"values\":[\"Trung vệ\",\"Thủ môn\"],\"fieldId\":\"3\"}}','Rejected','2025-07-12 00:12:12'),
(3,1,'U011',1,NULL,'{\"1\":{\"type\":\"radio\",\"value\":\"Ban Hậu cần\",\"fieldId\":\"1\"},\"2\":\"Tìm kiếm đồng đội tham gia thể thao\",\"3\":{\"type\":\"checkbox\",\"values\":[\"Hậu vệ\",\"Tiền vệ\",\"Trung vệ\",\"Tiền đạo\",\"Thủ môn\"],\"fieldId\":\"3\"}}','Collaborator','2025-07-12 00:12:53');



CREATE TABLE ClubApplications (
    ApplicationID INT PRIMARY KEY AUTO_INCREMENT,
    UserID VARCHAR(10),
    ClubID INT,
    Email VARCHAR(100) NOT NULL,
    EventID INT,
    ResponseID INT,			
    Status ENUM('PENDING', 'CANDIDATE', 'COLLABORATOR', 'APPROVED', 'REJECTED'),
    SubmitDate DATETIME,
    ReviewNote TEXT NULL,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID),
    FOREIGN KEY (EventID) REFERENCES Events(EventID),
    FOREIGN KEY (ResponseID) REFERENCES ApplicationResponses(ResponseID)
);
INSERT INTO ClubApplications (ApplicationID, UserID, ClubID, Email, EventID, ResponseID, Status, SubmitDate) VALUES
(16,'U005',1,'e@fpt.edu.vn',NULL,1,'CANDIDATE','2025-07-12 00:11:46'),
(17,'U012',1,'l@fpt.edu.vn',NULL,2,'REJECTED','2025-07-12 00:12:13'),
(18,'U011',1,'k@fpt.edu.vn',NULL,3,'COLLABORATOR','2025-07-12 00:12:54');

-- ================================================================================
-- ========================================
-- MEETINGS	
-- sửa thêm title cho các cuộc họp để biết cuộc họp họp về chủ đề gì ========================================
CREATE TABLE ClubMeeting (
    ClubMeetingID INT PRIMARY KEY AUTO_INCREMENT,
    ClubID INT NOT NULL,
    MeetingTitle VARCHAR(100) NOT NULL,
    Description TEXT,
    URLMeeting VARCHAR(255),
    StartedTime DATETIME NOT NULL,
    EndTime DATETIME,
    Document VARCHAR(255),
FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID)
);
INSERT INTO ClubMeeting (
    ClubID,
    MeetingTitle,
    Description,
    URLMeeting,
    StartedTime,
    EndTime,
    Document
) VALUES (
    1,
    'Cuộc họp chuẩn bị sự kiện Mùa hè xanh',
    'Thảo luận về phân công nhiệm vụ cho sự kiện Mùa hè xanh',
    'https://zoom.us/j/123456789',
    '2025-07-20 14:00:00',
    '2025-07-20 16:00:00',
    'https://example.com/docs/kehoach.pdf'
);

CREATE TABLE ClubMeetingParticipants (
    ClubMeetingID INT,
    ClubDepartmentID INT,

    PRIMARY KEY (ClubMeetingID, ClubDepartmentID ),
    FOREIGN KEY (ClubMeetingID) REFERENCES ClubMeeting(ClubMeetingID) ON DELETE CASCADE,
    FOREIGN KEY (ClubDepartmentID) REFERENCES ClubDepartments(ClubDepartmentID)
);




CREATE TABLE DepartmentMeeting (
    DepartmentMeetingID INT PRIMARY KEY AUTO_INCREMENT,
    ClubDepartmentID INT NOT NULL,
    Title VARCHAR(100) NOT NULL,
    URLMeeting VARCHAR(255),
    DocumentLink VARCHAR(255),
    StartedTime DATETIME,
    FOREIGN KEY (ClubDepartmentID) REFERENCES ClubDepartments(ClubDepartmentID)
);

CREATE TABLE DepartmentMeetingParticipants (
    DepartmentMeetingID INT,
    UserID VARCHAR(50),
    PRIMARY KEY (DepartmentMeetingID, UserID),
    FOREIGN KEY (DepartmentMeetingID) REFERENCES DepartmentMeeting(DepartmentMeetingID),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

INSERT INTO DepartmentMeeting (ClubDepartmentID, Title, URLMeeting, DocumentLink, StartedTime) VALUES
(3, 'Weekly Sync Meeting', 'https://meet.google.com/dept1', 'https://drive.google.com/file/d/12345', '2025-06-30 10:00:00'),
(1, 'Project Kickoff', 'https://meet.google.com/dept2', 'https://drive.google.com/file/d/67890', '2025-06-29 14:00:00'),
(5, 'Strategy Planning', 'https://meet.google.com/dept3', NULL, '2025-06-28 09:00:00');


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
    CategoryID int,
    Status ENUM('PENDING', 'APPROVED', 'REJECTED', 'ACTIVE', 'USED', 'INACTIVE') DEFAULT 'PENDING',
    RequestDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ProcessedDate DATETIME,
    ProcessedBy VARCHAR(10),
    GrantedDate DATETIME,
    UsedDate DATETIME,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (ProcessedBy) REFERENCES Users(UserID),
    FOREIGN KEY (CategoryID) REFERENCES ClubCategories(CategoryID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

INSERT INTO ClubCreationPermissions (UserID, ClubName, CategoryID,  Status, RequestDate, ProcessedBy, GrantedDate, UsedDate)
VALUES
  ('U001', 'CLB Bóng Đá', 3, 'USED', '2025-01-01 10:00:00', 'U004', '2025-01-02 10:00:00', '2025-01-03 10:00:00'),
  ('U002', 'CLB Âm Nhạc',2,'USED', '2025-02-01 10:00:00', 'U004', '2025-02-02 10:00:00', '2025-02-03 10:00:00'),
  ('U003', 'CLB Kỹ Năng', 1, 'USED', '2025-03-01 10:00:00', 'U004', '2025-03-02 10:00:00', '2025-03-03 10:00:00');
-- Đơn trạng thái khác (CLB không trùng)
INSERT INTO ClubCreationPermissions (UserID, ClubName, CategoryID, Status, RequestDate, ProcessedBy, GrantedDate)
VALUES
  ('U004', 'CLB Cờ Vua', 1, 'PENDING', '2025-06-20 10:00:00', NULL, NULL),
  ('U005', 'CLB Lập Trình',1, 'PENDING', '2025-06-21 10:00:00', NULL, NULL),
  ('U001', 'CLB Nhiếp Ảnh', 2, 'APPROVED', '2025-06-15 10:00:00', 'U004', '2025-06-16 10:00:00'),
  ('U002', 'CLB Môi Trường',2,  'REJECTED', '2025-06-10 10:00:00', 'U004', '2025-06-11 10:00:00');



CREATE TABLE ActivedMemberClubs (
    ActiveID INT PRIMARY KEY AUTO_INCREMENT,
    UserID VARCHAR(10) NOT NULL,
    ClubID INT NOT NULL,
    ActiveDate DATE NOT NULL,
    TermID VARCHAR(10) ,
    IsActive BOOLEAN DEFAULT TRUE,
    ProgressPoint INT DEFAULT NULL, -- Điểm rèn luyện, cho phép NULL
    FOREIGN KEY (TermID) REFERENCES Semesters(TermID),
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
-- FINANCIAL
-- ========================================
CREATE TABLE Expenses (
    ExpenseID INT PRIMARY KEY AUTO_INCREMENT,
    ClubID INT NOT NULL,
    TermID VARCHAR(10),
    Purpose ENUM('Sự kiện', 'Vật tư', 'Thuê địa điểm', 'Khác') NOT NULL,
    Amount DECIMAL(15, 2) NOT NULL,
    ExpenseDate DATETIME NOT NULL,
    Description TEXT,
    Attachment VARCHAR(255),
    CreatedBy VARCHAR(10) NOT NULL,
    Status ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    ApprovedBy VARCHAR(10),
    ApprovedAt DATETIME,
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID) ON DELETE CASCADE,
    FOREIGN KEY (TermID) REFERENCES Semesters(TermID) ON DELETE SET NULL,
    FOREIGN KEY (CreatedBy) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (ApprovedBy) REFERENCES Users(UserID) ON DELETE SET NULL
);


-- ========================================
-- INCOME
-- ========================================

CREATE TABLE Income (
    IncomeID INT PRIMARY KEY AUTO_INCREMENT,
    ClubID INT NOT NULL,
    TermID VARCHAR(10),
    Source ENUM('Phí thành viên', 'Tài trợ', 'Doanh thu sự kiện', 'Khác') NOT NULL,
    Amount DECIMAL(15, 2) NOT NULL,
    IncomeDate DATETIME NOT NULL,
    Description TEXT,
    Attachment VARCHAR(255),
    Status ENUM('Đã nhận', 'Đang chờ', 'Quá hạn') DEFAULT 'Đang chờ',
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID) ON DELETE CASCADE,
    FOREIGN KEY (TermID) REFERENCES Semesters(TermID) ON DELETE SET NULL
);
CREATE TABLE Transactions (
    TransactionID INT PRIMARY KEY AUTO_INCREMENT,
    ClubID INT NOT NULL,
    TermID VARCHAR(10),
    Type ENUM('Income', 'Expense') NOT NULL,
    Amount DECIMAL(15, 2) NOT NULL,
    TransactionDate DATETIME,
    Description TEXT,
    Attachment VARCHAR(255),
    CreatedBy VARCHAR(10) not null,
    Status ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',  -- Trạng thái chung
    ReferenceID INT,  -- Tham chiếu đến IncomeID hoặc ExpenseID nếu cần
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (CreatedBy) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID) ON DELETE CASCADE,
    FOREIGN KEY (TermID) REFERENCES Semesters(TermID) ON DELETE SET NULL
);
CREATE TABLE MemberIncomeContributions (
    ContributionID INT PRIMARY KEY AUTO_INCREMENT,
    IncomeID INT NOT NULL,
    UserID VARCHAR(10) NOT NULL,
    ClubID INT NOT NULL,
    TermID VARCHAR(10),
    Amount DECIMAL(15, 2),  -- Số tiền thành viên cần nộp (hoặc đã nộp)
    ContributionStatus ENUM('Pending', 'Paid', 'Exempted') DEFAULT 'Pending',  -- Trạng thái: Chưa nộp, Đã nộp, Miễn phí
    PaidDate DATETIME,  -- Ngày nộp (nếu đã nộp)
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    DueDate DATETIME not null,
    FOREIGN KEY (IncomeID) REFERENCES Income(IncomeID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID) ON DELETE CASCADE,
    FOREIGN KEY (TermID) REFERENCES Semesters(TermID) ON DELETE SET NULL,
    UNIQUE (IncomeID, UserID)  
);


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

INSERT INTO Feedbacks VALUES 
(1,15,'U005',0,4,'Sự kiện khá vui và thú vị',5,3,4,5,4,3,5,3,4,'2025-06-20 22:48:28'),
(2,15,'U006',1,2,'Tôi không thích sự kiện lắm, trải nghiệm của tôi khá tệ',2,1,3,2,1,3,2,4,3,'2025-06-21 00:58:14'),
(3,15,'U007',0,1,'Sự kiện không vui như tôi nghĩ',2,3,4,3,1,2,3,1,2,'2025-06-21 00:58:54'),
(4,15,'U008',0,3,'SỰ kiện ok',5,3,4,3,3,4,3,5,3,'2025-06-21 01:00:14');


INSERT INTO Notifications (Title, Content, CreatedDate, ReceiverID, Priority, Status) VALUES
('Thông báo đăng ký thành công', 'Bạn đã đăng ký tham gia sự kiện Giải Bóng Đá Sinh Viên 2025 thành công', '2025-06-23 10:00:00', 'U001', 'MEDIUM', 'UNREAD'),
('Thông báo sự kiện mới', 'Câu lạc bộ Âm Nhạc vừa tạo sự kiện mới: Đêm Nhạc Acoustic', '2025-06-26 09:00:00', 'U001', 'LOW', 'UNREAD'),
('Thông báo đăng ký thành công', 'Bạn đã đăng ký tham gia sự kiện Giải Bóng Đá Sinh Viên 2025 thành công', '2025-06-23 10:00:00', 'U002', 'MEDIUM', 'UNREAD'),
('Thông báo sự kiện mới', 'Câu lạc bộ Âm Nhạc vừa tạo sự kiện mới: Đêm Nhạc Acoustic', '2025-06-26 09:00:00', 'U002', 'LOW', 'UNREAD');



INSERT INTO ActivedMemberClubs (UserID, ClubID, ActiveDate, IsActive) VALUES
('HA178166', 5, '2025-05-01', TRUE),
('HA178166', 6, '2025-05-01', TRUE),
('HA188338', 5, '2025-05-01', TRUE),
('HA188338', 7, '2025-05-01', TRUE),
('HA186808', 2, '2025-05-01', TRUE),
('HS193994', 1, '2025-05-01', TRUE),
('HS193994', 5, '2025-05-01', TRUE),
('HS202370', 4, '2025-05-01', TRUE),
('HS179821', 3, '2025-05-01', TRUE),
('HS179821', 8, '2025-05-01', TRUE),
('HA171144', 3, '2025-05-01', TRUE),
('HS195569', 2, '2025-05-01', TRUE),
('HS195569', 7, '2025-05-01', TRUE),
('HA180043', 6, '2025-05-01', TRUE),
('HA180043', 1, '2025-05-01', TRUE),
('HS189617', 8, '2025-05-01', TRUE),
('HA192435', 1, '2025-05-01', TRUE),
('HS207936', 2, '2025-05-01', TRUE),
('HS189043', 8, '2025-05-01', TRUE),
('HS189043', 3, '2025-05-01', TRUE),
('HA197461', 5, '2025-05-01', TRUE),
('HA197461', 1, '2025-05-01', TRUE),
('HS173913', 6, '2025-05-01', TRUE),
('HS173913', 1, '2025-05-01', TRUE),
('HA193827', 1, '2025-05-01', TRUE),
('HS165214', 2, '2025-05-01', TRUE),
('HA196247', 6, '2025-05-01', TRUE),
('HS178116', 3, '2025-05-01', TRUE),
('HS178116', 2, '2025-05-01', TRUE),
('HE178230', 6, '2025-05-01', TRUE),
('HA181645', 5, '2025-05-01', TRUE),
('HS168618', 2, '2025-05-01', TRUE),
('HS162362', 8, '2025-05-01', TRUE),
('HS171226', 8, '2025-05-01', TRUE),
('HS171226', 6, '2025-05-01', TRUE),
('HS185547', 8, '2025-05-01', TRUE),
('HS185547', 1, '2025-05-01', TRUE),
('HA191316', 7, '2025-05-01', TRUE),
('HA191316', 5, '2025-05-01', TRUE),
('HS206227', 7, '2025-05-01', TRUE),
('HS198865', 8, '2025-05-01', TRUE),
('HS198865', 4, '2025-05-01', TRUE),
('HE201122', 3, '2025-05-01', TRUE),
('HE201122', 6, '2025-05-01', TRUE),
('HE183728', 1, '2025-05-01', TRUE),
('HA167111', 2, '2025-05-01', TRUE),
('HA167111', 4, '2025-05-01', TRUE),
('HE167270', 2, '2025-05-01', TRUE),
('HE167270', 5, '2025-05-01', TRUE),
('HE180475', 2, '2025-05-01', TRUE),
('HE180475', 7, '2025-05-01', TRUE),
('HE180920', 3, '2025-05-01', TRUE),
('HS186142', 6, '2025-05-01', TRUE),
('HS186142', 1, '2025-05-01', TRUE),
('HS174001', 4, '2025-05-01', TRUE),
('HA199273', 6, '2025-05-01', TRUE),
('HA172786', 3, '2025-05-01', TRUE),
('HE169986', 5, '2025-05-01', TRUE),
('HS203946', 7, '2025-05-01', TRUE),
('HS203946', 3, '2025-05-01', TRUE),
('HS202345', 5, '2025-05-01', TRUE),
('HS202345', 1, '2025-05-01', TRUE),
('HE194162', 5, '2025-05-01', TRUE),
('HS180154', 7, '2025-05-01', TRUE),
('HS180154', 2, '2025-05-01', TRUE),
('HS188956', 7, '2025-05-01', TRUE),
('HE167238', 6, '2025-05-01', TRUE),
('HS204902', 2, '2025-05-01', TRUE),
('HS204902', 4, '2025-05-01', TRUE),
('HA194098', 7, '2025-05-01', TRUE),
('HS183264', 7, '2025-05-01', TRUE),
('HS191748', 3, '2025-05-01', TRUE),
('HS191748', 1, '2025-05-01', TRUE),
('HE199371', 2, '2025-05-01', TRUE);

-- ========================================
-- Thêm thông tin cuộc họp câu lạc bộ (ClubMeeting)
-- Đảm bảo không trùng ClubID và StartedTime
-- ========================================

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


-- ========================================
-- RECRUITMENT ACTIVITY
-- ========================================
-- Hoạt động tuyển quân: theo CLB, Gen, liên kết Form đăng ký đã xuất bản
CREATE TABLE IF NOT EXISTS RecruitmentCampaigns (
    RecruitmentID INT PRIMARY KEY AUTO_INCREMENT,
    ClubID INT NOT NULL,
    Gen INT NOT NULL,
    FormID INT NOT NULL,
    Title VARCHAR(255),
    Description TEXT,
    StartDate DATE,
    EndDate DATE,
    Status ENUM('UPCOMING','ONGOING', 'CLOSED') DEFAULT 'UPCOMING',
    CreatedBy VARCHAR(10),
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID) ON DELETE CASCADE,
    FOREIGN KEY (FormID) REFERENCES ApplicationForms(FormID) ON DELETE CASCADE,
    FOREIGN KEY (CreatedBy) REFERENCES Users(UserID) ON DELETE SET NULL
);

INSERT INTO RecruitmentCampaigns VALUES 
(1,1,2023,1,'Tuyển quân Gen 2023','Chiến dịch tuyển thành viên CLB Bóng đá Gen 2023','2023-07-01','2023-09-30','CLOSED','U001','2025-07-12 00:10:14'),
(2,1,2024,2,'Tuyển quân Gen 2024','Chiến dịch tuyển thành viên CLB Bóng đá Gen 2024','2024-07-01','2024-09-30','CLOSED','U001','2025-07-12 00:10:14'),
(3,1,9,1,'Hoạt động tuyển quân câu lạc bộ bóng đá 2025','Hoạt động tuyển thành viên mới cho câu lạc bộ bóng đá gen 9 năm 2025','2025-07-12','2025-07-18','ONGOING','U001','2025-07-12 00:11:13');


-- ========================================
-- Các giai đoạn: Vòng đơn, Phỏng vấn, Thử thách
CREATE TABLE IF NOT EXISTS RecruitmentStages (
    StageID INT PRIMARY KEY AUTO_INCREMENT,
    RecruitmentID INT NOT NULL,
    StageName ENUM('APPLICATION', 'INTERVIEW', 'CHALLENGE') NOT NULL,
    Status ENUM('UPCOMING', 'ONGOING', 'CLOSED') DEFAULT 'UPCOMING',
    StartDate DATE,
    EndDate DATE,
    LocationID INT,
    Description TEXT,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (RecruitmentID) REFERENCES RecruitmentCampaigns(RecruitmentID) ON DELETE CASCADE,
    FOREIGN KEY (LocationID) REFERENCES Locations(LocationID) ON DELETE SET NULL
);

INSERT INTO RecruitmentStages (RecruitmentID, StageName, Status, StartDate, EndDate, LocationID, Description, CreatedAt)
VALUES
-- Gen 2023
(1, 'APPLICATION', 'CLOSED', '2023-07-01', '2023-07-31',1, 'Vòng nộp đơn Gen 2023', '2023-07-01 00:11:13'),
(1, 'INTERVIEW', 'CLOSED', '2023-08-01', '2023-08-15',1, 'Phỏng vấn trực tiếp Gen 2023', '2023-08-01 00:11:13'),
(1, 'CHALLENGE', 'CLOSED', '2023-08-16', '2023-10-10',1,'Vòng thử thách cộng tác viên 2023', '2023-08-16 00:11:13'),
-- Gen 2024
(2, 'APPLICATION', 'CLOSED', '2024-07-01', '2024-07-31',1, 'Vòng nộp đơn Gen 2024', '2024-07-01 00:11:13'),
(2, 'INTERVIEW', 'CLOSED', '2024-08-01', '2024-08-15',1, 'Phỏng vấn trực tiếp Gen 2024', '2024-08-01 00:11:13'),
(2, 'CHALLENGE', 'CLOSED', '2024-08-16', '2024-10-17',1, 'Vòng thử thách cộng tác viên Gen 2024', '2024-08-16 00:11:13'),
-- Gen 2025
(3,'APPLICATION','CLOSED','2025-07-12','2025-07-13',1,'Vòng nộp đơn đăng ký','2025-07-12 00:11:13'),
(3,'INTERVIEW','ONGOING','2025-07-14','2025-07-16',18,'Vòng phỏng vấn ứng viên','2025-07-12 00:11:13'),
(3,'CHALLENGE','UPCOMING','2025-07-17','2025-07-18',1,'Vòng thử thách đánh giá năng lực','2025-07-12 00:11:13');
-- ========================================
-- Trạng thái đơn ứng viên ở từng vòng
CREATE TABLE IF NOT EXISTS ApplicationStages (
    ApplicationStageID INT PRIMARY KEY AUTO_INCREMENT,
    ApplicationID INT NOT NULL,
    StageID INT NOT NULL,
    Status ENUM('PENDING',  'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedBy VARCHAR(10),
    FOREIGN KEY (ApplicationID) REFERENCES ClubApplications(ApplicationID) ON DELETE CASCADE,
    FOREIGN KEY (StageID) REFERENCES RecruitmentStages(StageID) ON DELETE CASCADE,
    FOREIGN KEY (UpdatedBy) REFERENCES Users(UserID) ON DELETE SET NULL
);

INSERT INTO `applicationstages` VALUES 
(1,16,7,'APPROVED','2025-07-12 00:11:46',NULL),
(2,17,7,'REJECTED','2025-07-12 00:12:13',NULL),(3,18,7,'APPROVED','2025-07-12 00:12:54',NULL),
(9,18,8,'APPROVED','2025-07-14 00:28:20',NULL),(10,16,8,'PENDING','2025-07-14 00:38:20',NULL);

-- Tạo chỉ mục cho truy vấn nhanh
CREATE INDEX idx_application_stage ON ApplicationStages (ApplicationID, StageID, Status);
-- ========================================
-- Mẫu thông báo lưu trữ tái sử dụng
CREATE TABLE IF NOT EXISTS NotificationTemplates (
    TemplateID INT PRIMARY KEY AUTO_INCREMENT,
    ClubID INT NOT NULL,
    TemplateName VARCHAR(100) NOT NULL,
    Title VARCHAR(200) NOT NULL,
    Content TEXT,
    CreatedBy VARCHAR(10),
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    IsReusable BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID) ON DELETE CASCADE,
    FOREIGN KEY (CreatedBy) REFERENCES Users(UserID) ON DELETE SET NULL
);

INSERT INTO NotificationTemplates (ClubID, TemplateName, Title, Content, CreatedBy)
VALUES
(1, 'Mẫu Được Duyệt', 'Chúc mừng bạn đã vượt qua vòng', 'Xin chúc mừng {Tên ứng viên}, bạn đã vượt qua vòng {Tên vòng}!', 'U001'),
(1, 'Mẫu Bị Từ Chối', 'Rất tiếc, bạn chưa vượt qua', 'Xin lỗi {Tên ứng viên}, bạn chưa vượt qua vòng {Tên vòng}. Hẹn gặp lại bạn ở các cơ hội tiếp theo.', 'U001');

-- ========================================
-- Mẫu thông báo cho từng vòng
CREATE TABLE IF NOT EXISTS StageNotifications (
    StageNotificationID INT PRIMARY KEY AUTO_INCREMENT,
    StageID INT NOT NULL,
    TemplateID INT NULL,
    Title VARCHAR(200) NOT NULL,
    Content TEXT,
    LastSentAt DATETIME DEFAULT NULL,
    TotalSent INT DEFAULT 0,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    CreatedBy VARCHAR(10),
    FOREIGN KEY (StageID) REFERENCES RecruitmentStages(StageID) ON DELETE CASCADE,
    FOREIGN KEY (CreatedBy) REFERENCES Users(UserID) ON DELETE SET NULL,
    FOREIGN KEY (TemplateID) REFERENCES NotificationTemplates(TemplateID) ON DELETE SET NULL
);



-- ========================================
-- DUYỆT Ở CLUBAPPLICATIONS → SYNC XUỐNG STAGE
DROP TRIGGER IF EXISTS trg_sync_application_stages_status;
DELIMITER //

CREATE TRIGGER trg_sync_application_stages_status
AFTER UPDATE ON ClubApplications
FOR EACH ROW
BEGIN
    DECLARE stage1 INT;
    DECLARE stage2 INT;
    DECLARE stage3 INT;

    -- Lấy các StageID theo thứ tự tăng dần (đơn, phỏng vấn, thử thách)
    SELECT StageID INTO stage1
    FROM ApplicationStages
    WHERE ApplicationID = NEW.ApplicationID
    ORDER BY StageID ASC
    LIMIT 1 OFFSET 0;

    SELECT StageID INTO stage2
    FROM ApplicationStages
    WHERE ApplicationID = NEW.ApplicationID
    ORDER BY StageID ASC
    LIMIT 1 OFFSET 1;

    SELECT StageID INTO stage3
    FROM ApplicationStages
    WHERE ApplicationID = NEW.ApplicationID
    ORDER BY StageID ASC
    LIMIT 1 OFFSET 2;

    -- Khi bị từ chối toàn bộ
    IF NEW.Status = 'REJECTED' THEN
        UPDATE ApplicationStages
        SET Status = 'REJECTED'
        WHERE ApplicationID = NEW.ApplicationID;

    -- Khi chuyển thành ứng viên tiềm năng
    ELSEIF NEW.Status = 'CANDIDATE' THEN
        UPDATE ApplicationStages
        SET Status = CASE 
                        WHEN StageID = stage1 THEN 'APPROVED'
                        WHEN StageID = stage2 THEN 'PENDING'
                        ELSE Status
                    END
        WHERE ApplicationID = NEW.ApplicationID;

    -- Khi chuyển thành cộng tác viên
    ELSEIF NEW.Status = 'COLLABORATOR' THEN
        UPDATE ApplicationStages
        SET Status = CASE 
                        WHEN StageID = stage2 THEN 'APPROVED'
                        WHEN StageID = stage3 THEN 'PENDING'
                        ELSE Status
                    END
        WHERE ApplicationID = NEW.ApplicationID;

    -- Khi hoàn toàn được duyệt
    ELSEIF NEW.Status = 'APPROVED' THEN
        UPDATE ApplicationStages
        SET Status = CASE 
                        WHEN StageID = stage3 THEN 'APPROVED'
                        ELSE Status
                    END
        WHERE ApplicationID = NEW.ApplicationID;
    END IF;
END;
//
DELIMITER ;
-- ======================================== 
-- Event Scheduler: Cập nhật trạng thái vòng 
DROP EVENT IF EXISTS update_campaign_and_stage_status; 
DELIMITER $$ 
CREATE EVENT update_campaign_and_stage_status ON SCHEDULE 
EVERY 5 MINUTE 
STARTS CURRENT_TIMESTAMP DO BEGIN 
-- Cập nhật vòng đã kết thúc 
UPDATE RecruitmentStages SET Status = 'CLOSED' WHERE EndDate < CURRENT_DATE(); 
-- Cập nhật vòng đang diễn ra (hôm nay nằm trong khoảng Start → End) 
UPDATE RecruitmentStages SET Status = 'ONGOING' WHERE StartDate <= CURRENT_DATE() AND EndDate >= CURRENT_DATE(); 
-- Cập nhật vòng sắp tới 
UPDATE RecruitmentStages SET Status = 'UPCOMING' WHERE StartDate > CURRENT_DATE(); 
-- Cập nhật trạng thái CHIẾN DỊCH (RecruitmentCampaigns) 
UPDATE RecruitmentCampaigns SET Status = 'CLOSED' 
WHERE EndDate < CURRENT_DATE(); 
UPDATE RecruitmentCampaigns SET Status = 'ONGOING' WHERE StartDate <= CURRENT_DATE() AND EndDate >= CURRENT_DATE(); 
UPDATE RecruitmentCampaigns SET Status = 'UPCOMING' WHERE StartDate > CURRENT_DATE(); 
END$$ 
DELIMITER ; 

-- ========================================
-- Event Scheduler: Tự move ứng viên pass sang vòng tiếp theo
DROP EVENT IF EXISTS auto_move_to_next_stage;
DELIMITER $$

CREATE EVENT auto_move_to_next_stage
ON SCHEDULE EVERY 5 MINUTE
STARTS CURRENT_TIMESTAMP
ON COMPLETION PRESERVE
DO
BEGIN
  INSERT INTO ApplicationStages (ApplicationID, StageID, Status, UpdatedAt)
  SELECT 
    ast.ApplicationID,
    s_next.StageID,
    'PENDING',
    NOW()
  FROM ApplicationStages ast
  JOIN RecruitmentStages s_cur ON ast.StageID = s_cur.StageID
  JOIN RecruitmentStages s_next 
    ON s_next.RecruitmentID = s_cur.RecruitmentID 
    AND s_next.StartDate > s_cur.EndDate
  WHERE
    ast.Status = 'APPROVED'
    AND s_cur.EndDate < CURRENT_DATE()
    AND s_next.StartDate = (
      SELECT MIN(s2.StartDate)
      FROM RecruitmentStages s2
      WHERE s2.RecruitmentID = s_cur.RecruitmentID
        AND s2.StartDate > s_cur.EndDate
    )
    AND NOT EXISTS (
      SELECT 1
      FROM ApplicationStages ast2
      WHERE ast2.ApplicationID = ast.ApplicationID
        AND ast2.StageID = s_next.StageID
    );
END $$

DELIMITER ;


-- ========================================
-- Bật Scheduler
SET GLOBAL event_scheduler = ON;

