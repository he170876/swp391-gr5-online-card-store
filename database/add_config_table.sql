-- Script to add Config table for system configuration
-- Run this script to add the Config table to your database

USE ocs;
GO

-- Create Config table if not exists
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Config]') AND type in (N'U'))
BEGIN
    CREATE TABLE Config (
        [key] VARCHAR(100) PRIMARY KEY,
        value VARCHAR(500) NOT NULL,
        description VARCHAR(255)
    );
    
    -- Insert default values
    INSERT INTO Config ([key], value, description) VALUES
    ('systemName', 'OCS - Online Card Store', 'Tên hệ thống'),
    ('maintenanceMode', 'false', 'Chế độ bảo trì (true/false)'),
    ('currency', 'VND', 'Tiền tệ mặc định'),
    ('maxLoginAttempts', '5', 'Số lần đăng nhập tối đa'),
    ('emailSupport', 'support@ocs.com', 'Email hỗ trợ'),
    ('phoneSupport', '1900-xxxx', 'Số điện thoại hỗ trợ'),
    ('pageSize', '20', 'Số bản ghi mỗi trang');
    
    PRINT 'Config table created successfully with default values.';
END
ELSE
BEGIN
    PRINT 'Config table already exists.';
END
GO

