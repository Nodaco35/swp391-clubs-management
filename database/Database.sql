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

-- Bảng 3: Roles
CREATE TABLE Roles (
    RoleID INT PRIMARY KEY AUTO_INCREMENT,
    RoleName VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL
);

-- Bảng 4: Clubs
CREATE TABLE Clubs (
    ClubID INT PRIMARY KEY AUTO_INCREMENT,
    ClubImg VARCHAR(255) NULL,
    IsRecruiting BOOLEAN DEFAULT 1,
    ClubName VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    Description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    EstablishedDate DATE,
    ContactPhone VARCHAR(20) NULL,
    ContactGmail VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE,
    ContactURL VARCHAR(150) NULL,
    ClubStatus BOOLEAN DEFAULT 1
);

-- Bảng 5: ClubDepartments
CREATE TABLE ClubDepartments (
    DepartmentID INT PRIMARY KEY AUTO_INCREMENT,
    DepartmentName VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    DepartmentStatus BOOLEAN DEFAULT 1,
    Description VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
    ClubID INT,
    FOREIGN KEY (ClubID) REFERENCES Clubs(ClubID) ON DELETE CASCADE
);

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

-- Bảng 9: EventParticipants
CREATE TABLE EventParticipants (
    EventParticipantID INT PRIMARY KEY AUTO_INCREMENT,
    EventID INT,
    UserID VARCHAR(10),
    Status ENUM('REGISTERED', 'ATTENDED', 'ABSENT'),
    FOREIGN KEY (EventID) REFERENCES Events(EventID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);

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

-- Bảng 13: ApplicationResponses
CREATE TABLE ApplicationResponses (
    ResponseID INT PRIMARY KEY AUTO_INCREMENT,
    ApplicationID INT NOT NULL,
    TemplateID INT NOT NULL,
    FieldValue TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
    FOREIGN KEY (ApplicationID) REFERENCES ClubApplications(ApplicationID) ON DELETE CASCADE,
    FOREIGN KEY (TemplateID) REFERENCES ApplicationFormTemplates(TemplateID) ON DELETE CASCADE
);
