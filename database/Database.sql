-- Tạo database và chọn sử dụng
CREATE DATABASE ManagerClub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ManagerClub;

-- Bảng 1: Permissions
CREATE TABLE Permissions (
    PermissionID INT PRIMARY KEY,
    PermissionName VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL
);

-- Bảng 2: Users
CREATE TABLE Users (
    UserID INT PRIMARY KEY AUTO_INCREMENT,
    FullName VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    PermissionID INT NOT NULL,
    Status BOOLEAN DEFAULT 1,
    ResetToken VARCHAR(255),
    TokenExpiry DATETIME,
    FOREIGN KEY (PermissionID) REFERENCES Permissions(PermissionID)
);

-- Bảng 3: Roles
CREATE TABLE Roles (
    RoleID INT PRIMARY KEY,
    RoleName VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL
);

-- Bảng 4: Clubs
CREATE TABLE Clubs (
    ClubID INT PRIMARY KEY AUTO_INCREMENT,
    ClubName VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    Description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    EstablishedDate DATE,
    ContactPhone VARCHAR(100),
    ContactGmail VARCHAR(100),
    ContactURL VARCHAR(500)
);

-- Bảng 5: Departments
CREATE TABLE Departments (
    DepartmentID INT PRIMARY KEY,
    DepartmentName VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    Description VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    ClubID INT,
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID)
);

-- Bảng 6: UserClubs
CREATE TABLE UserClubs (
    UserClubID INT PRIMARY KEY AUTO_INCREMENT,
    UserID INT,
    ClubID INT,
    DepartmentID INT,
    RoleID INT,
    JoinDate DATETIME,
    IsActive BOOLEAN DEFAULT 1,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID),
    FOREIGN KEY (DepartmentID) REFERENCES Departments(DepartmentID),
    FOREIGN KEY (RoleID) REFERENCES Roles(RoleID)
);

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
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID)
);

-- Bảng 8: TaskAssignment
CREATE TABLE TaskAssignment (
    TaskAssignmentID INT PRIMARY KEY AUTO_INCREMENT,
    EventID INT,
    DepartmentID INT,
    Description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    FOREIGN KEY (EventID) REFERENCES Events(EventID),
    FOREIGN KEY (DepartmentID) REFERENCES Departments(DepartmentID)
);

-- Bảng 9: EventParticipants
CREATE TABLE EventParticipants (
    EventParticipantID INT PRIMARY KEY AUTO_INCREMENT,
    EventID INT,
    UserID INT,
    Status ENUM('Registered','Attended','Absent','Accepted','Rejected') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    FOREIGN KEY (EventID) REFERENCES Events(EventID),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- Bảng 10: Notifications
CREATE TABLE Notifications (
    NotificationID INT PRIMARY KEY AUTO_INCREMENT,
    Title VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    Content TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    ReceiverID INT,
    FOREIGN KEY (ReceiverID) REFERENCES Users(UserID)
);

-- Bảng 11: ClubApplications
CREATE TABLE ClubApplications (
    ApplicationID INT PRIMARY KEY AUTO_INCREMENT,
    UserID INT,
    ClubID INT,
    Reason TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    Status ENUM('Pending','Approved','Rejected') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    SubmitDate DATETIME,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID)
);
