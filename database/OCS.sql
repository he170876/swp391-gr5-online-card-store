-------------------------------------------------------------
-- 1. DROP DATABASE IF EXISTS
-------------------------------------------------------------
IF DB_ID('ocs') IS NOT NULL
BEGIN
    ALTER DATABASE ocs SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE ocs;
END
GO

-------------------------------------------------------------
-- 2. RECREATE DATABASE
-------------------------------------------------------------
CREATE DATABASE ocs;
GO

USE ocs;
GO

-------------------------------------------------------------
-- 3. DROP TABLE IF EXISTS
-------------------------------------------------------------
IF OBJECT_ID('WalletTransaction') IS NOT NULL DROP TABLE WalletTransaction;
IF OBJECT_ID('[Order]') IS NOT NULL DROP TABLE [Order];
IF OBJECT_ID('ProductLog') IS NOT NULL DROP TABLE ProductLog;
IF OBJECT_ID('CardInfo') IS NOT NULL DROP TABLE CardInfo;
IF OBJECT_ID('Product') IS NOT NULL DROP TABLE Product;
IF OBJECT_ID('Provider') IS NOT NULL DROP TABLE Provider;
IF OBJECT_ID('Category') IS NOT NULL DROP TABLE Category;
IF OBJECT_ID('UserOTP') IS NOT NULL DROP TABLE UserOTP;
IF OBJECT_ID('[User]') IS NOT NULL DROP TABLE [User];
IF OBJECT_ID('Role') IS NOT NULL DROP TABLE Role;
GO

-------------------------------------------------------------
-- 4. CREATE TABLES
-------------------------------------------------------------

CREATE TABLE Role (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL,
    description NVARCHAR(255)
);
GO

CREATE TABLE [User] (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    email NVARCHAR(100) NOT NULL UNIQUE,
    password_hash NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    phone NVARCHAR(20),
    address NVARCHAR(255),
    status NVARCHAR(20) NOT NULL CHECK (status IN (N'ACTIVE',N'LOCKED',N'INACTIVE')),
    wallet_balance DECIMAL(15,2) NOT NULL DEFAULT 0,
    role_id BIGINT NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES Role(id)
);
GO

CREATE TABLE UserOTP (
    user_id BIGINT PRIMARY KEY,  -- mỗi user chỉ có 1 record
    otp_code NVARCHAR(10) NOT NULL,
    otp_created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    send_count INT NOT NULL DEFAULT 0,
    last_send DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT fk_userotp_user FOREIGN KEY (user_id) REFERENCES [User](id)
);
GO

CREATE TABLE Category (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(255),
    status NVARCHAR(20) NOT NULL CHECK (status IN (N'ACTIVE',N'INACTIVE'))
);
GO

CREATE TABLE Provider (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    contact_info NVARCHAR(255),
    status NVARCHAR(20) NOT NULL CHECK (status IN (N'ACTIVE',N'INACTIVE'))
);
GO

CREATE TABLE Product (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    category_id BIGINT NOT NULL,
    provider_id BIGINT NOT NULL,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(255),
    image_url NVARCHAR(500),
    cost_price DECIMAL(15,2) NOT NULL,
    sell_price DECIMAL(15,2) NOT NULL,
    discount_percent DECIMAL(5,2) NOT NULL DEFAULT 0,
    quantity INT NOT NULL DEFAULT 0,
    status NVARCHAR(20) NOT NULL CHECK (status IN (N'ACTIVE',N'INACTIVE')),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES Category(id),
    CONSTRAINT fk_product_provider FOREIGN KEY (provider_id) REFERENCES Provider(id)
);
GO

CREATE TABLE ProductLog (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT,
    action NVARCHAR(50) NOT NULL,
    field_name NVARCHAR(100),
    old_value NVARCHAR(255),
    new_value NVARCHAR(255),
    note NVARCHAR(255),
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT fk_productlog_product FOREIGN KEY (product_id) REFERENCES Product(id),
    CONSTRAINT fk_productlog_user FOREIGN KEY (user_id) REFERENCES [User](id)
);
GO

CREATE TABLE CardInfo (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_id BIGINT NOT NULL,
    code NVARCHAR(255) NOT NULL,
    serial NVARCHAR(100),
    expiry_date DATE,
    status NVARCHAR(20) NOT NULL CHECK (status IN (N'AVAILABLE',N'SOLD',N'EXPIRED',N'INACTIVE')),
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT uq_cardinfo_code UNIQUE (code),
    CONSTRAINT fk_cardinfo_product FOREIGN KEY (product_id) REFERENCES Product(id)
);
GO

CREATE TABLE [Order] (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    cardinfo_id BIGINT NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    original_price DECIMAL(15,2) NOT NULL,
    discount_percent DECIMAL(5,2) NOT NULL DEFAULT 0,
    final_price DECIMAL(15,2) NOT NULL,
    status NVARCHAR(20) NOT NULL CHECK (status IN (N'PENDING',N'PAID',N'COMPLETED',N'CANCELED',N'REFUNDED')),
    receiver_email NVARCHAR(100) NOT NULL,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES [User](id),
    CONSTRAINT fk_order_cardinfo FOREIGN KEY (cardinfo_id) REFERENCES CardInfo(id),
    CONSTRAINT uq_order_cardinfo UNIQUE (cardinfo_id)
);
GO

CREATE TABLE WalletTransaction (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type NVARCHAR(20) NOT NULL CHECK (type IN (N'TOPUP',N'PURCHASE',N'REFUND')),
    amount DECIMAL(15,2) NOT NULL,
    balance DECIMAL(15,2) NOT NULL,
    status NVARCHAR(20) NOT NULL CHECK (status IN (N'PENDING',N'SUCCESS',N'FAILED')),
    reference_code NVARCHAR(100),
    qr_url NVARCHAR(255),
    bank_code NVARCHAR(50),
    payment_time DATETIME2,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES [User](id)
);
GO

-------------------------------------------------------------
-- 5. INSERT SAMPLE DATA
-------------------------------------------------------------
INSERT INTO Role (name, description) VALUES
(N'ADMIN', N'System administrator'),
(N'STAFF', N'Internal staff'),
(N'CUSTOMER', N'Regular customer');

INSERT INTO [User] (email, password_hash, full_name, phone, address, status, wallet_balance, role_id)
VALUES
(N'admin@ocs.com', 
 N'$2a$12$/43J6kpkYk.EFIcJtYGkBO2Vks88HQVkyBFb6L71S/3KXwe7nHR5.',
 N'Administrator', N'0900000001', N'System HQ', N'ACTIVE', 0, 1),

(N'staff@ocs.com',
 N'$2a$12$/43J6kpkYk.EFIcJtYGkBO2Vks88HQVkyBFb6L71S/3KXwe7nHR5.',
 N'System Staff', N'0900000002', N'Office 1', N'ACTIVE', 0, 2),

(N'customer@ocs.com',
 N'$2a$12$/43J6kpkYk.EFIcJtYGkBO2Vks88HQVkyBFb6L71S/3KXwe7nHR5.',
 N'Nguyễn Văn A', N'0900000003', N'Quận 1, TP.HCM', N'ACTIVE', 50000, 3);
GO
