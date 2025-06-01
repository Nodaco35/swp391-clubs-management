CREATE DATABASE ClubManagementSystem
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE ClubManagementSystem;

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





CREATE TABLE ClubDepartments (
    DepartmentID INT PRIMARY KEY AUTO_INCREMENT,
    DepartmentName VARCHAR(50) NOT NULL,
    DepartmentStatus BOOLEAN DEFAULT 1,
    Description VARCHAR(200),
    ClubID INT,
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID) ON DELETE CASCADE
);
INSERT INTO ClubDepartments (ClubID, DepartmentName, Description) VALUES
(1, 'Ban Huấn Luyện', 'Huấn luyện và đào tạo'),
(1, 'Ban Sự Kiện', 'Tổ chức sự kiện'),
(2, 'Ban Huấn Luyện', '...'),
(2, 'Ban Sự Kiện', '...'),
(3, 'Ban Học Thuật', '...'),
(3, 'Ban Sự Kiện', '...'),
(4, 'Ban Dự Án', '...'),
(4, 'Ban Đào Tạo', '...'),
(5, 'Ban Dự Án', '...'),
(5, 'Ban Truyền Thông', '...'),
(6, 'Ban Biểu Diễn', '...'),
(6, 'Ban Đào Tạo', '...'),
(7, 'Ban Biểu Diễn', 'Chuyên biểu diễn và dàn dựng'),
(8, 'Ban Học Thuật', 'Phụ trách học thuật tranh biện');



CREATE TABLE UserClubs (
    UserClubID INT PRIMARY KEY AUTO_INCREMENT,
    UserID VARCHAR(10) NOT NULL,
    ClubID INT NOT NULL,
    DepartmentID INT,
    RoleID INT NOT NULL,
    JoinDate DATETIME NOT NULL,
    IsActive BOOLEAN DEFAULT 1,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID),
    FOREIGN KEY (DepartmentID) REFERENCES ClubDepartments(DepartmentID),
    FOREIGN KEY (RoleID) REFERENCES Roles(RoleID)
);
INSERT INTO UserClubs (UserID, ClubID, DepartmentID, RoleID, JoinDate) VALUES
('U001', 1, null, 1, '2020-09-01'),
('U001', 3, 5, 3, '2021-01-15'),
('U002', 2, 3, 2, '2020-10-05'),
('U002', 5, 9, 3, '2021-02-20'),
('U003', 3, 6, 1, '2020-11-10'),
('U003', 4, 7, 3, '2021-03-25'),
('U004', 5, 10, 2, '2020-12-15'),
('U004', 6, 11, 3, '2021-04-30'),
('U005', 2, 3, 1, '2021-07-01'),
('U006', 4, 7, 1, '2021-07-02'),
('U007', 5, 9, 1, '2021-07-03'),
('U008', 6, 11, 1, '2021-07-04'),
('U009', 7, 13, 1, '2021-07-05'),
('U010', 8, 14, 1, '2021-07-06');



CREATE TABLE ApplicationFormTemplates (
    TemplateID INT PRIMARY KEY AUTO_INCREMENT,
    ClubID INT NOT NULL,
    EventID INT,
    FormType ENUM('Club', 'Event', 'Other') NOT NULL,
    Title VARCHAR(100) NOT NULL,
    FieldName VARCHAR(50),
    FieldType ENUM('Text', 'Textarea', 'Dropdown', 'PhoneNumber', 'Number', 'Date', 'Checkbox', 'Info', 'Email','Radio'),
    IsRequired BOOLEAN DEFAULT 1,
    Options LONGTEXT,
    Published BOOLEAN NOT NULL DEFAULT 0,
    CONSTRAINT fk_formtemplate_club FOREIGN KEY (ClubID) 
        REFERENCES Clubs(ClubID) ON DELETE CASCADE
    -- EventID sẽ được xử lý sau khi bảng Events được tạo
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


CREATE TABLE Events (
    EventID INT PRIMARY KEY AUTO_INCREMENT,
    EventName VARCHAR(100) NOT NULL,
    EventImg VARCHAR(100) ,
    Description TEXT,
    EventDate DATETIME,
    Location VARCHAR(200),
    ClubID INT NOT NULL,
    IsPublic BOOLEAN DEFAULT 0,
    FormTemplateID INT,
    Capacity INT,
    Status ENUM('Pending', 'Completed'),
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID),
    FOREIGN KEY (FormTemplateID) REFERENCES ApplicationFormTemplates(TemplateID)
);
INSERT INTO Events (EventName, EventImg, Description, EventDate, Location, ClubID, IsPublic, Capacity, Status) VALUES
-- Sự kiện sắp tới (Pending) cho năm 2025
('FPTU Showcase 2025 Chung Kết', 'images/events/showcase2025.jpg', 'FPTU Showcase 2025 Chung Kết là sự kiện nghệ thuật lớn nhất trong năm, nơi quy tụ những tiết mục đặc sắc từ các vòng thi trước đó trên khắp các cơ sở FPTU toàn quốc. Với chủ đề ''Bùng cháy đam mê, lan tỏa cảm hứng'', chương trình không chỉ là sân chơi thể hiện tài năng mà còn là nơi gắn kết cộng đồng sinh viên thông qua âm nhạc, vũ đạo, sân khấu hóa, và các màn trình diễn đỉnh cao. Khán giả sẽ được mãn nhãn với hệ thống âm thanh ánh sáng hiện đại, tương tác sân khấu chuyên nghiệp và đặc biệt là những khoảnh khắc thăng hoa của tuổi trẻ. Sự kiện dự kiến thu hút hàng trăm sinh viên và khách mời, tạo nên một đêm diễn bùng nổ, đầy cảm xúc.', '2025-04-27 09:00:00', 'Sân khấu chính, Campus Hà Nội', 7, TRUE, 200, 'PENDING'),
('Lễ Hội Âm Nhạc Tết 2025', 'images/events/tetmusic2025.jpg', 'Lễ Hội Âm Nhạc Tết 2025 mang đến không gian âm nhạc sống động kết hợp tinh hoa truyền thống và xu hướng hiện đại. Người tham dự sẽ được thưởng thức những tiết mục dân gian hòa quyện với âm nhạc điện tử, rap, ballad… từ các câu lạc bộ nghệ thuật sinh viên. Không khí ngày Tết lan tỏa qua các gian hàng trò chơi dân gian, trình diễn áo dài, và các hoạt động giao lưu văn hóa vùng miền. Đây là dịp để sinh viên FPTU cùng nhau đón Tết sớm, lan tỏa niềm vui và gắn kết cộng đồng.', '2025-01-15 19:00:00', 'Campus Quy Nhơn', 6, TRUE, 300, 'PENDING'),
('Thử Thách Lập Trình FPTU 2025', 'images/events/coding2025.jpg', 'Cuộc thi lập trình giải quyết các vấn đề thực tế.', '2025-03-15 08:00:00', 'Phòng máy tính, Campus TP.HCM', 4, TRUE, 50, 'PENDING'),
('Chiến Dịch Tình Nguyện Xanh 2025', 'images/events/greenfuture2025.jpg', 'Hoạt động tình nguyện thúc đẩy phát triển bền vững cho cộng đồng địa phương.', '2025-06-10 07:00:00', 'Khu vực nông thôn Đà Nẵng', 5, TRUE, 40, 'PENDING'),
('Hội Thảo Kỹ Năng Viết Tiếng Anh', 'images/events/englishworkshop2025.jpg', 'Hội thảo nâng cao kỹ năng viết và thuyết trình tiếng Anh học thuật.', '2025-02-20 09:00:00', 'Phòng hội thảo B1, Campus Hà Nội', 3, TRUE, 60, 'PENDING'),
('Giải Bóng Rổ 3x3 FPTU', 'images/events/basketball2025.jpg', 'Giải đấu bóng rổ 3x3 sôi động dành cho sinh viên FPTU.', '2025-05-10 14:00:00', 'Sân thể thao, Campus TP.HCM', 2, FALSE, 80, 'PENDING'),
('Cuộc Thi Tranh Biện Xã Hội 2025', 'images/events/debate2025.jpg', 'Cuộc thi tranh biện về các vấn đề xã hội nóng hổi.', '2025-04-15 13:00:00', 'Hội trường lớn, Campus Hà Nội', 8, TRUE, 100, 'PENDING'),
('Giải Bóng Đá Sinh Viên FPTU 2025', 'images/events/football2025.jpg', 'Giải đấu bóng đá thường niên dành cho sinh viên toàn trường.', '2025-03-25 08:00:00', 'Sân vận động, Campus Hà Nội', 1, TRUE, 120, 'PENDING'),
('Ngày Hội Âm Nhạc FPTU 2025', 'images/events/musicday2025.jpg', 'Ngày hội với các màn trình diễn âm nhạc và hoạt động tương tác.', '2025-05-20 10:00:00', 'Sân khấu ngoài trời, Campus Cần Thơ', 6, FALSE, 250, 'PENDING'),
('Hackathon Đổi Mới AI', 'images/events/aihackathon2025.jpg', 'Cuộc thi phát triển các giải pháp trí tuệ nhân tạo sáng tạo.', '2025-06-05 08:00:00', 'Phòng máy tính, Campus Hà Nội', 4, TRUE, 45, 'PENDING'),
-- Sự kiện đã hoàn thành (Completed) trong năm 2024
('Lễ Hội Làng Tết 2024', 'images/events/villagefest2024.jpg', 'Sự kiện tái hiện không khí Tết truyền thống với các tiết mục âm nhạc và nhảy.', '2024-01-20 10:00:00', 'Campus Đà Nẵng', 6, TRUE, 500, 'COMPLETED'),
('Trại Lập Trình FPTU 2024', 'images/events/codingbootcamp2024.jpg', 'Trại huấn luyện chuyên sâu về kỹ thuật lập trình nâng cao.', '2024-11-20 08:00:00', 'Phòng máy tính, Campus TP.HCM', 4, TRUE, 60, 'COMPLETED'),
('Đêm Nhạc Rock 2024', 'images/events/rocknight2024.jpg', 'Đêm nhạc rock bùng nổ với các ban nhạc sinh viên.', '2024-12-10 19:00:00', 'Sân khấu ngoài trời, Campus Hà Nội', 6, TRUE, 200, 'COMPLETED'),
('Chiến Dịch Tình Nguyện Hè 2024', 'images/events/summervolunteer2024.jpg', 'Hoạt động tình nguyện hỗ trợ trẻ em vùng sâu vùng xa.', '2024-07-15 07:00:00', 'Tỉnh Tuyên Quang', 5, FALSE, 35, 'COMPLETED'),
('Cuộc Thi Nói Tiếng Anh 2024', 'images/events/englishcontest2024.jpg', 'Cuộc thi thể hiện kỹ năng giao tiếp tiếng Anh.', '2024-11-25 09:00:00', 'Phòng hội thảo B3, Campus Hà Nội', 3, TRUE, 70, 'COMPLETED'),
('Giải Bóng Đá 5x5 FPTU 2024', 'images/events/football2024.jpg', 'Giải đấu bóng đá 5x5 hấp dẫn dành cho sinh viên FPTU.', '2024-12-05 14:00:00', 'Sân thể thao, Campus Cần Thơ', 1, FALSE, 100, 'COMPLETED'),
('Ngày Hội Nhảy FPTU 2024', 'images/events/dancefest2024.jpg', 'Ngày hội nhảy với các màn trình diễn độc đáo từ sinh viên.', '2024-12-15 10:00:00', 'Sân chính, Campus Quy Nhơn', 7, TRUE, 400, 'COMPLETED'),
('Hội Thảo Phát Triển Web 2024', 'images/events/webdev2024.jpg', 'Hội thảo thực hành về công nghệ phát triển web hiện đại.', '2024-11-15 14:00:00', 'Phòng hội thảo A2, Campus Đà Nẵng', 4, TRUE, 50, 'COMPLETED'),
('FPTU Dance Battle 2024', 'images/events/dancebattle2024.jpg', 'Cuộc thi nhảy thể hiện tài năng của sinh viên.', '2024-11-30 18:00:00', 'Sân khấu chính, Campus TP.HCM', 7, TRUE, 150, 'COMPLETED'),
('Cuộc Thi Tranh Biện 2024', 'images/events/debate2024.jpg', 'Cuộc thi tranh biện về các chủ đề học thuật và xã hội.', '2024-12-01 09:00:00', 'Phòng hội thảo C1, Campus Hà Nội', 8, TRUE, 65, 'COMPLETED');

UPDATE Events SET Description = 'FPTU Showcase 2025 Chung Kết là sự kiện nghệ thuật lớn nhất trong năm, nơi quy tụ những tiết mục đặc sắc từ các vòng thi trước đó trên khắp các cơ sở FPTU toàn quốc. Với chủ đề ''Bùng cháy đam mê, lan tỏa cảm hứng'', chương trình không chỉ là sân chơi thể hiện tài năng mà còn là nơi gắn kết cộng đồng sinh viên thông qua âm nhạc, vũ đạo, sân khấu hóa, và các màn trình diễn đỉnh cao. Khán giả sẽ được mãn nhãn với hệ thống âm thanh ánh sáng hiện đại, tương tác sân khấu chuyên nghiệp và đặc biệt là những khoảnh khắc thăng hoa của tuổi trẻ. Sự kiện dự kiến thu hút hàng trăm sinh viên và khách mời, tạo nên một đêm diễn bùng nổ, đầy cảm xúc.' WHERE EventID = 1;
UPDATE Events SET Description = 'Lễ Hội Âm Nhạc Tết 2025 mang đến không gian âm nhạc sống động kết hợp tinh hoa truyền thống và xu hướng hiện đại. Người tham dự sẽ được thưởng thức những tiết mục dân gian hòa quyện với âm nhạc điện tử, rap, ballad… từ các câu lạc bộ nghệ thuật sinh viên. Không khí ngày Tết lan tỏa qua các gian hàng trò chơi dân gian, trình diễn áo dài, và các hoạt động giao lưu văn hóa vùng miền. Đây là dịp để sinh viên FPTU cùng nhau đón Tết sớm, lan tỏa niềm vui và gắn kết cộng đồng.' WHERE EventID = 2;
UPDATE Events SET Description = 'Thử Thách Lập Trình FPTU 2025 là sân chơi trí tuệ quy tụ những bạn trẻ đam mê công nghệ và giải thuật. Các đội thi sẽ đối mặt với các bài toán lập trình thực tiễn liên quan đến xử lý dữ liệu, trí tuệ nhân tạo, và bảo mật. Cuộc thi không chỉ rèn luyện kỹ năng coding mà còn thúc đẩy tư duy phản biện, làm việc nhóm và sáng tạo giải pháp. Bên cạnh đó, chương trình còn có workshop chia sẻ kinh nghiệm từ các chuyên gia, tạo môi trường học hỏi bổ ích.' WHERE EventID = 3;
UPDATE Events SET Description = 'Chiến Dịch Tình Nguyện Xanh 2025 hướng đến mục tiêu nâng cao nhận thức bảo vệ môi trường và phát triển cộng đồng bền vững. Sinh viên sẽ tham gia các hoạt động trồng cây xanh, tái chế rác thải, làm sạch môi trường tại các vùng nông thôn. Ngoài ra, chiến dịch còn kết hợp tổ chức lớp học kỹ năng sống, tặng quà cho học sinh khó khăn, góp phần lan tỏa tinh thần trách nhiệm xã hội. Đây là dịp để sinh viên thể hiện lòng nhiệt huyết, sẻ chia và cống hiến cho cộng đồng.' WHERE EventID = 4;
UPDATE Events SET Description = 'Hội Thảo Kỹ Năng Viết Tiếng Anh 2025 giúp sinh viên nâng cao khả năng viết học thuật qua các chủ đề phổ biến trong môi trường học thuật quốc tế. Diễn giả là các giảng viên, cựu sinh viên xuất sắc sẽ chia sẻ phương pháp viết luận logic, cách triển khai ý tưởng, và kỹ năng phản biện trong tiếng Anh. Người tham dự sẽ được thực hành trực tiếp, nhận phản hồi từ chuyên gia và tài liệu học tập chất lượng. Hội thảo phù hợp với sinh viên chuẩn bị cho IELTS, học bổng, hoặc nghiên cứu.' WHERE EventID = 5;
UPDATE Events SET Description = 'Giải Bóng Rổ 3x3 FPTU là sân chơi thể thao hấp dẫn thu hút những bạn trẻ yêu thích vận động, chiến thuật và tinh thần đồng đội. Với thể thức thi đấu 3x3 hiện đại, trận đấu diễn ra nhanh, căng thẳng và đầy kịch tính. Các đội bóng đến từ nhiều cơ sở FPTU sẽ tranh tài quyết liệt để giành chức vô địch. Bên cạnh đó, sự kiện còn có các hoạt động cổ động, tặng quà cho khán giả và phần biểu diễn sôi động giữa trận đấu, tạo nên bầu không khí sôi nổi và đầy hào hứng.' WHERE EventID = 6;
UPDATE Events SET Description = 'Cuộc Thi Tranh Biện Xã Hội 2025 mở ra diễn đàn lý tưởng cho những sinh viên yêu thích tranh luận, tư duy phản biện và quan tâm đến các vấn đề xã hội. Với các chủ đề nóng như giáo dục, môi trường, bình đẳng giới, đội thi sẽ đối đầu trực tiếp qua các vòng debate gay cấn. Sự kiện góp phần nâng cao khả năng lập luận, thuyết phục và trình bày logic cho người trẻ. Ban giám khảo là giảng viên và chuyên gia, đảm bảo tính chuyên môn và công tâm.' WHERE EventID = 7;
UPDATE Events SET Description = 'Giải Bóng Đá Sinh Viên FPTU 2025 quy tụ các đội bóng sinh viên tài năng đến từ các khoa và cơ sở khác nhau. Đây là dịp để sinh viên rèn luyện thể chất, xây dựng tinh thần đồng đội và giao lưu giữa các cộng đồng học tập. Các trận đấu diễn ra hấp dẫn với sự cổ vũ nhiệt tình từ khán giả. Sự kiện hứa hẹn tạo ra không khí thể thao sôi động, kết nối đam mê và lan tỏa năng lượng tích cực trong sinh viên.' WHERE EventID = 8;
UPDATE Events SET Description = 'Ngày Hội Âm Nhạc FPTU 2025 là lễ hội nghệ thuật mở rộng với sân khấu hoành tráng, hệ thống âm thanh – ánh sáng chuyên nghiệp. Sinh viên sẽ được đắm chìm trong các màn trình diễn nhạc trẻ, acoustic, rap, EDM đến từ các ban nhạc và nghệ sĩ trẻ FPTU. Ngoài ra còn có các khu vui chơi, gian hàng ẩm thực và khu check-in nghệ thuật, tạo nên một không gian văn hóa – giải trí đặc sắc. Đây là dịp để sinh viên thể hiện đam mê và thư giãn sau những giờ học căng thẳng.' WHERE EventID = 9;
UPDATE Events SET Description = 'Hackathon Đổi Mới AI là sự kiện công nghệ nổi bật dành cho sinh viên yêu thích lập trình và sáng tạo với trí tuệ nhân tạo. Trong vòng 24-48 giờ, các đội sẽ xây dựng sản phẩm AI giải quyết bài toán xã hội như chatbot giáo dục, nhận diện cảm xúc, phân tích dữ liệu. Cuộc thi khuyến khích tư duy đổi mới, kỹ năng teamwork và khả năng hiện thực hóa ý tưởng thành sản phẩm. Các dự án xuất sắc sẽ được mentor hỗ trợ phát triển tiếp và có cơ hội trình bày trước nhà đầu tư.' WHERE EventID = 10;
UPDATE Events SET Description = 'Ngày Hội Câu Lạc Bộ FPTU 2025 là dịp để hơn 50 CLB học thuật, kỹ năng, thể thao, nghệ thuật… giới thiệu hoạt động đến tân sinh viên. Không gian sự kiện tràn ngập sắc màu với các booth trải nghiệm, mini game, sân khấu biểu diễn và hoạt động giao lưu hấp dẫn. Sinh viên sẽ có cơ hội khám phá đam mê, kết nối bạn bè, và tìm kiếm môi trường phát triển bản thân trong thời gian học tại FPTU.' WHERE EventID = 11;
UPDATE Events SET Description = 'Cuộc Thi Sáng Tạo Nội Dung Số 2025 khuyến khích sinh viên thể hiện bản lĩnh truyền thông qua các hình thức như video ngắn, infographic, podcast, và mạng xã hội. Với chủ đề về lối sống sinh viên, giáo dục số, sức khỏe tinh thần, người tham gia sẽ thi tài sáng tạo nội dung hấp dẫn, lan tỏa giá trị tích cực. Cuộc thi kết hợp workshop từ chuyên gia truyền thông và giải thưởng giá trị.' WHERE EventID = 12;
UPDATE Events SET Description = 'Diễn Đàn Sinh Viên FPTU 2025 quy tụ sinh viên xuất sắc cùng lãnh đạo nhà trường để trao đổi thẳng thắn về các vấn đề học tập, đời sống, cơ hội nghề nghiệp. Diễn đàn là cầu nối giúp sinh viên đóng góp ý kiến cải tiến môi trường học đường, đồng thời được định hướng phát triển toàn diện. Đây là hoạt động mang tính chất đối thoại, thể hiện sự dân chủ và cầu thị của FPTU.' WHERE EventID = 13;
UPDATE Events SET Description = 'Ngày Hội Việc Làm FPTU 2025 kết nối sinh viên với hơn 100 doanh nghiệp trong và ngoài nước thuộc nhiều lĩnh vực như công nghệ, tài chính, marketing, giáo dục… Sinh viên có cơ hội phỏng vấn trực tiếp, cập nhật yêu cầu thị trường, và nhận tư vấn nghề nghiệp từ chuyên gia. Ngoài ra, các hội thảo kỹ năng ứng tuyển và viết CV cũng được tổ chức đồng hành.' WHERE EventID = 14;
UPDATE Events SET Description = 'Tuần Lễ Quốc Tế FPTU 2025 giới thiệu văn hóa các quốc gia thông qua chuỗi hoạt động giao lưu với sinh viên quốc tế, trình diễn trang phục truyền thống, ẩm thực đa quốc gia, và workshop ngôn ngữ. Sự kiện góp phần xây dựng môi trường quốc tế hóa, tôn vinh sự đa dạng và thúc đẩy hội nhập toàn cầu cho sinh viên FPTU.' WHERE EventID = 15;
UPDATE Events SET Description = 'Workshop Lập Kế Hoạch Tài Chính Cá Nhân cung cấp kiến thức thiết thực về quản lý chi tiêu, tiết kiệm, đầu tư, và lập kế hoạch tài chính cho sinh viên. Diễn giả là các chuyên gia tài chính, nhà đầu tư trẻ chia sẻ câu chuyện thực tế và kinh nghiệm ứng dụng. Đây là kỹ năng quan trọng giúp sinh viên tự chủ và chuẩn bị tốt cho cuộc sống sau khi ra trường.' WHERE EventID = 16;
UPDATE Events SET Description = 'Cuộc Thi Nhiếp Ảnh FPTU 2025 tìm kiếm những khoảnh khắc đẹp và ý nghĩa trong đời sống sinh viên qua ống kính sáng tạo. Chủ đề năm nay là "Thanh xuân FPTU", mở ra không gian cảm xúc qua hình ảnh về học tập, tình bạn, hoạt động xã hội. Các tác phẩm xuất sắc sẽ được triển lãm, in sách ảnh và trao giải.' WHERE EventID = 17;
UPDATE Events SET Description = 'Ngày Hội Đổi Mới Sáng Tạo FPTU là sân chơi kết nối ý tưởng khởi nghiệp, sáng chế công nghệ, cải tiến sản phẩm từ sinh viên toàn trường. Các đội sẽ trình bày dự án trước hội đồng chuyên gia, nhận tư vấn và có cơ hội gọi vốn hoặc tham gia ươm tạo. Sự kiện góp phần thúc đẩy tinh thần đổi mới và xây dựng văn hóa khởi nghiệp tại FPTU.' WHERE EventID = 18;
UPDATE Events SET Description = 'Chương Trình Tri Ân Ngày Nhà Giáo Việt Nam 20/11 là dịp sinh viên gửi lời cảm ơn đến thầy cô qua các tiết mục văn nghệ, viết thư tay, tặng quà, và các hoạt động gắn kết. Sự kiện mang không khí ấm áp, xúc động, thể hiện tinh thần “tôn sư trọng đạo” và sự trân trọng công lao của người làm giáo dục.' WHERE EventID = 19;
UPDATE Events SET Description = 'Hành Trình Khám Phá Bản Thân là chuỗi hoạt động trải nghiệm như trekking, dã ngoại, thử thách nhóm giúp sinh viên khám phá giá trị cá nhân, tăng cường kỹ năng mềm và xây dựng tinh thần đồng đội. Các hoạt động được thiết kế kết hợp giữa vui chơi, thử thách và chia sẻ giúp người tham gia hiểu rõ bản thân và định hướng tương lai.' WHERE EventID = 20;



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
(4, 'U004', 'REGISTERED');



CREATE TABLE TaskAssignment (
    TaskAssignmentID INT PRIMARY KEY AUTO_INCREMENT,
    EventID INT NOT NULL,
    DepartmentID INT NOT NULL,
    Description TEXT,
    TaskName VARCHAR(100),
    DueDate DATETIME,
    Status ENUM('PENDING', 'COMPLETED'),
    FOREIGN KEY (EventID) REFERENCES Events(EventID),
    FOREIGN KEY (DepartmentID) REFERENCES ClubDepartments(DepartmentID)
);
INSERT INTO TaskAssignment (EventID, DepartmentID, TaskName, Description, DueDate, Status) VALUES
(1, 1, 'Chuẩn bị sân bãi', 'Kiểm tra và chuẩn bị sân bãi cho giải đấu', '2023-12-10 12:00:00', 'PENDING'),
(1, 2, 'Truyền thông sự kiện', 'Đăng bài quảng cáo sự kiện trên các kênh truyền thông', '2023-12-05 12:00:00', 'PENDING'),
(2, 7, 'Chuẩn bị tài liệu', 'Chuẩn bị tài liệu và slide cho workshop', '2023-11-15 12:00:00', 'PENDING'),
(2, 8, 'Đăng ký người tham dự', 'Quản lý danh sách đăng ký tham dự workshop', '2023-11-18 12:00:00', 'PENDING'),
(3, 11, 'Chuẩn bị âm thanh', 'Kiểm tra và chuẩn bị hệ thống âm thanh cho đêm nhạc', '2023-11-20 12:00:00', 'PENDING'),
(3, 12, 'Trang trí sân khấu', 'Trang trí sân khấu cho đêm nhạc', '2023-11-22 12:00:00', 'PENDING'),
(4, 9, 'Chuẩn bị quà tặng', 'Chuẩn bị quà tặng cho các em nhỏ', '2023-12-05 12:00:00', 'PENDING'),
(4, 10, 'Lên kế hoạch di chuyển', 'Lên kế hoạch di chuyển và logistics cho chiến dịch', '2023-12-08 12:00:00', 'PENDING');




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

CREATE TABLE CreatedClubApplications (
    ApplicationID INT PRIMARY KEY AUTO_INCREMENT,
    UserID VARCHAR(10),
    ClubID INT,
    Email VARCHAR(100) NOT NULL,			
    Status ENUM('PENDING', 'APPROVED', 'REJECTED'),
    SubmitDate DATETIME,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID)
);
INSERT INTO CreatedClubApplications (UserID, ClubID, Email, Status, SubmitDate) VALUES
('U001', 4, 'a@gmail.com', 'PENDING', '2023-11-01 10:00:00'),
('U002', 6, 'b@gmail.com', 'PENDING', '2023-11-02 11:00:00'),
('U003', 8, 'c@gmail.com', 'PENDING', '2023-11-03 09:00:00'),
('U004', 7, 'd@gmail.com', 'PENDING', '2023-11-04 14:00:00');

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


CREATE TABLE PeriodicClubReport (
    ReportID INT PRIMARY KEY AUTO_INCREMENT,
    ClubID INT NOT NULL,
    SubmittedBy VARCHAR(10),
    Term VARCHAR(10),
    SubmissionDate DATETIME,
    Status ENUM('PENDING', 'APPROVED') DEFAULT 'PENDING',
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID),
    FOREIGN KEY (SubmittedBy) REFERENCES Users(UserID)
);

-- INSERT mẫu
INSERT INTO PeriodicClubReport (ClubID, SubmittedBy, Term, SubmissionDate, Status)
VALUES
(1, 'U001', 'SP25', NOW(), 'PENDING'),
(3, 'U003', 'SP25', NOW(), 'APPROVED'),
(5, 'U004', 'SP25', NOW(), 'APPROVED');

CREATE TABLE PeriodicReportMemberStats (
    ReportMemberID INT PRIMARY KEY AUTO_INCREMENT,
    ReportID INT,
    UserID VARCHAR(10),
    RoleNote VARCHAR(50),
    FOREIGN KEY (ReportID) REFERENCES PeriodicClubReport(ReportID),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- INSERT mẫu
INSERT INTO PeriodicReportMemberStats (ReportID, UserID, RoleNote)
VALUES
(1, 'U001', 'Chủ nhiệm'),
(1, 'U002', 'Thành viên'),
(2, 'U003', 'Chủ nhiệm'),
(2, 'U004', 'Thành viên'),
(3, 'U002', 'Thành viên');

CREATE TABLE PeriodicReportEvents (
    ReportEventID INT PRIMARY KEY AUTO_INCREMENT,
    ReportID INT,
    EventName VARCHAR(100),
    EventDate DATETIME,
    Description TEXT,
    EventType ENUM('Internal', 'Public'),
    ParticipantCount INT,
    ProofLink VARCHAR(100),
    FOREIGN KEY (ReportID) REFERENCES PeriodicClubReport(ReportID)
);

-- INSERT mẫu
INSERT INTO PeriodicReportEvents (ReportID, EventName, EventDate, Description, EventType, ParticipantCount, ProofLink)
VALUES
(1, 'Giao lưu thể thao tháng 3', '2023-03-15 08:00:00', 'Trận bóng giao hữu nội bộ', 'Internal', 20, 'https://imgur.com/sport.jpg'),
(1, 'Tập huấn CLB', '2023-04-05 10:00:00', 'Đào tạo thành viên mới', 'Internal', 15, 'https://imgur.com/training.jpg'),
(2, 'Hội thảo kỹ năng', '2023-03-25 14:00:00', 'Học cách thuyết trình hiệu quả', 'Public', 50, 'https://imgur.com/workshop.jpg'),
(3, 'Chiến dịch Xuân tình nguyện', '2023-04-10 07:00:00', 'Tặng quà cho trẻ em vùng cao', 'Public', 40, 'https://imgur.com/xuantinhnguyen.jpg');
