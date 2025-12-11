-------------------------------------------------------------
-- 1. DROP DATABASE IF EXISTS (đảm bảo xóa sạch DB)
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
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255)
);
GO

CREATE TABLE [User] (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE','LOCKED','INACTIVE')),
    wallet_balance DECIMAL(15,2) NOT NULL DEFAULT 0,
    role_id BIGINT NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES Role(id)
);
GO

CREATE TABLE UserOTP (
    user_id BIGINT PRIMARY KEY,  -- mỗi user chỉ có 1 record
    otp_code VARCHAR(10) NOT NULL,
    otp_created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    send_count INT NOT NULL DEFAULT 0,
    last_send DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT fk_userotp_user FOREIGN KEY (user_id) REFERENCES [User](id)
);
GO

CREATE TABLE Category (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE','INACTIVE'))
);
GO

CREATE TABLE Provider (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_info VARCHAR(255),
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE','INACTIVE'))
);
GO

CREATE TABLE Product (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    category_id BIGINT NOT NULL,
    provider_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    cost_price DECIMAL(15,2) NOT NULL,
    sell_price DECIMAL(15,2) NOT NULL,
    discount_percent DECIMAL(5,2) NOT NULL DEFAULT 0,
    quantity INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE','INACTIVE')),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES Category(id),
    CONSTRAINT fk_product_provider FOREIGN KEY (provider_id) REFERENCES Provider(id)
);
GO

CREATE TABLE ProductLog (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT,
    action VARCHAR(50) NOT NULL,
    field_name VARCHAR(100),
    old_value VARCHAR(255),
    new_value VARCHAR(255),
    note VARCHAR(255),
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT fk_productlog_product FOREIGN KEY (product_id) REFERENCES Product(id),
    CONSTRAINT fk_productlog_user FOREIGN KEY (user_id) REFERENCES [User](id)
);
GO

CREATE TABLE CardInfo (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_id BIGINT NOT NULL,
    code VARCHAR(255) NOT NULL,
    serial VARCHAR(100),
    expiry_date DATE,
    status VARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE','SOLD','EXPIRED','INACTIVE')),
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
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING','PAID','COMPLETED','CANCELED','REFUNDED')),
    receiver_email VARCHAR(100) NOT NULL,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES [User](id),
    CONSTRAINT fk_order_cardinfo FOREIGN KEY (cardinfo_id) REFERENCES CardInfo(id),
    CONSTRAINT uq_order_cardinfo UNIQUE (cardinfo_id)
);
GO

CREATE TABLE WalletTransaction (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('TOPUP','PURCHASE','REFUND')),
    amount DECIMAL(15,2) NOT NULL,
    balance DECIMAL(15,2) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING','SUCCESS','FAILED')),
    reference_code VARCHAR(100),
    qr_url VARCHAR(255),
    bank_code VARCHAR(50),
    payment_time DATETIME2,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES [User](id)
);
GO

INSERT INTO Role (name, description) VALUES
('ADMIN', 'System administrator'),
('STAFF', 'Internal staff'),
('CUSTOMER', 'Regular customer');

INSERT INTO [User] (email, password_hash, full_name, phone, address, status, wallet_balance, role_id)
VALUES
('admin@ocs.com', 
 '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',
 'Administrator', '0900000001', 'System HQ', 'ACTIVE', 0, 1),

('staff@ocs.com',
 '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',
 'System Staff', '0900000002', 'Office 1', 'ACTIVE', 0, 2),

('customer@ocs.com',
 '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',
 'Sample Customer', '0900000003', 'District 1', 'ACTIVE', 50000, 3);
