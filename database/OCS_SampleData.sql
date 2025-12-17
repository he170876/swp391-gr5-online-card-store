-------------------------------------------------------------
-- OCS - Sample Data Insert Script
-- Run this after OCS.sql to populate sample data
-------------------------------------------------------------

USE ocs;
GO

-------------------------------------------------------------
-- 1. CATEGORIES
-------------------------------------------------------------
INSERT INTO Category (name, description, status) VALUES
(N'Thẻ Game', N'Thẻ nạp game online các loại', 'ACTIVE'),
(N'Thẻ Điện Thoại', N'Thẻ nạp tiền điện thoại di động', 'ACTIVE'),
(N'Thẻ Quà Tặng', N'Thẻ quà tặng các thương hiệu', 'ACTIVE'),
(N'Thẻ Dịch Vụ', N'Thẻ các dịch vụ trực tuyến', 'ACTIVE'),
(N'Thẻ Ví Điện Tử', N'Thẻ nạp ví điện tử', 'INACTIVE');
GO

-------------------------------------------------------------
-- 2. PROVIDERS
-------------------------------------------------------------
INSERT INTO Provider (name, contact_info, status) VALUES
(N'Garena Vietnam', N'Email: support@garena.vn | Phone: 1900 1234', 'ACTIVE'),
(N'VNG Corporation', N'Email: support@vng.com.vn | Phone: 1900 5678', 'ACTIVE'),
(N'Viettel Telecom', N'Email: cskh@viettel.com.vn | Phone: 198', 'ACTIVE'),
(N'Vinaphone', N'Email: support@vinaphone.com.vn | Phone: 18001091', 'ACTIVE'),
(N'Mobifone', N'Email: cskh@mobifone.vn | Phone: 9090', 'ACTIVE'),
(N'Google Play', N'support.google.com/googleplay', 'ACTIVE'),
(N'Apple iTunes', N'support.apple.com', 'ACTIVE'),
(N'Steam', N'support.steampowered.com', 'ACTIVE'),
(N'MoMo', N'Email: support@momo.vn | Phone: 1900 545441', 'INACTIVE');
GO

-------------------------------------------------------------
-- 3. PRODUCTS
-------------------------------------------------------------

-- Thẻ Game - Garena (category_id = 1, provider_id = 1)
INSERT INTO Product (category_id, provider_id, name, description, image_url, cost_price, sell_price, discount_percent, quantity, status) VALUES
(1, 1, N'Thẻ Garena 20.000đ', N'Thẻ nạp Garena mệnh giá 20.000 VNĐ', 'https://example.com/garena-20k.jpg', 18000, 20000, 0, 100, 'ACTIVE'),
(1, 1, N'Thẻ Garena 50.000đ', N'Thẻ nạp Garena mệnh giá 50.000 VNĐ', 'https://example.com/garena-50k.jpg', 45000, 50000, 0, 80, 'ACTIVE'),
(1, 1, N'Thẻ Garena 100.000đ', N'Thẻ nạp Garena mệnh giá 100.000 VNĐ', 'https://example.com/garena-100k.jpg', 90000, 100000, 5, 60, 'ACTIVE'),
(1, 1, N'Thẻ Garena 200.000đ', N'Thẻ nạp Garena mệnh giá 200.000 VNĐ', 'https://example.com/garena-200k.jpg', 180000, 200000, 5, 40, 'ACTIVE'),
(1, 1, N'Thẻ Garena 500.000đ', N'Thẻ nạp Garena mệnh giá 500.000 VNĐ', 'https://example.com/garena-500k.jpg', 450000, 500000, 10, 20, 'ACTIVE');
GO

-- Thẻ Game - VNG (category_id = 1, provider_id = 2)
INSERT INTO Product (category_id, provider_id, name, description, image_url, cost_price, sell_price, discount_percent, quantity, status) VALUES
(1, 2, N'VCoin 20.000đ', N'Thẻ VCoin mệnh giá 20.000 VNĐ', 'https://example.com/vcoin-20k.jpg', 18000, 20000, 0, 100, 'ACTIVE'),
(1, 2, N'VCoin 50.000đ', N'Thẻ VCoin mệnh giá 50.000 VNĐ', 'https://example.com/vcoin-50k.jpg', 45000, 50000, 0, 80, 'ACTIVE'),
(1, 2, N'VCoin 100.000đ', N'Thẻ VCoin mệnh giá 100.000 VNĐ', 'https://example.com/vcoin-100k.jpg', 90000, 100000, 3, 50, 'ACTIVE'),
(1, 2, N'VCoin 200.000đ', N'Thẻ VCoin mệnh giá 200.000 VNĐ', 'https://example.com/vcoin-200k.jpg', 180000, 200000, 5, 30, 'ACTIVE');
GO

-- Thẻ Điện Thoại - Viettel (category_id = 2, provider_id = 3)
INSERT INTO Product (category_id, provider_id, name, description, image_url, cost_price, sell_price, discount_percent, quantity, status) VALUES
(2, 3, N'Thẻ Viettel 10.000đ', N'Thẻ nạp Viettel mệnh giá 10.000 VNĐ', 'https://example.com/viettel-10k.jpg', 9500, 10000, 0, 200, 'ACTIVE'),
(2, 3, N'Thẻ Viettel 20.000đ', N'Thẻ nạp Viettel mệnh giá 20.000 VNĐ', 'https://example.com/viettel-20k.jpg', 19000, 20000, 0, 150, 'ACTIVE'),
(2, 3, N'Thẻ Viettel 50.000đ', N'Thẻ nạp Viettel mệnh giá 50.000 VNĐ', 'https://example.com/viettel-50k.jpg', 47500, 50000, 2, 100, 'ACTIVE'),
(2, 3, N'Thẻ Viettel 100.000đ', N'Thẻ nạp Viettel mệnh giá 100.000 VNĐ', 'https://example.com/viettel-100k.jpg', 95000, 100000, 3, 80, 'ACTIVE'),
(2, 3, N'Thẻ Viettel 200.000đ', N'Thẻ nạp Viettel mệnh giá 200.000 VNĐ', 'https://example.com/viettel-200k.jpg', 190000, 200000, 5, 50, 'ACTIVE'),
(2, 3, N'Thẻ Viettel 500.000đ', N'Thẻ nạp Viettel mệnh giá 500.000 VNĐ', 'https://example.com/viettel-500k.jpg', 475000, 500000, 5, 30, 'ACTIVE');
GO

-- Thẻ Điện Thoại - Vinaphone (category_id = 2, provider_id = 4)
INSERT INTO Product (category_id, provider_id, name, description, image_url, cost_price, sell_price, discount_percent, quantity, status) VALUES
(2, 4, N'Thẻ Vinaphone 20.000đ', N'Thẻ nạp Vinaphone mệnh giá 20.000 VNĐ', 'https://example.com/vina-20k.jpg', 19000, 20000, 0, 120, 'ACTIVE'),
(2, 4, N'Thẻ Vinaphone 50.000đ', N'Thẻ nạp Vinaphone mệnh giá 50.000 VNĐ', 'https://example.com/vina-50k.jpg', 47500, 50000, 0, 100, 'ACTIVE'),
(2, 4, N'Thẻ Vinaphone 100.000đ', N'Thẻ nạp Vinaphone mệnh giá 100.000 VNĐ', 'https://example.com/vina-100k.jpg', 95000, 100000, 2, 70, 'ACTIVE'),
(2, 4, N'Thẻ Vinaphone 200.000đ', N'Thẻ nạp Vinaphone mệnh giá 200.000 VNĐ', 'https://example.com/vina-200k.jpg', 190000, 200000, 3, 40, 'ACTIVE');
GO

-- Thẻ Điện Thoại - Mobifone (category_id = 2, provider_id = 5)
INSERT INTO Product (category_id, provider_id, name, description, image_url, cost_price, sell_price, discount_percent, quantity, status) VALUES
(2, 5, N'Thẻ Mobifone 20.000đ', N'Thẻ nạp Mobifone mệnh giá 20.000 VNĐ', 'https://example.com/mobi-20k.jpg', 19000, 20000, 0, 100, 'ACTIVE'),
(2, 5, N'Thẻ Mobifone 50.000đ', N'Thẻ nạp Mobifone mệnh giá 50.000 VNĐ', 'https://example.com/mobi-50k.jpg', 47500, 50000, 0, 80, 'ACTIVE'),
(2, 5, N'Thẻ Mobifone 100.000đ', N'Thẻ nạp Mobifone mệnh giá 100.000 VNĐ', 'https://example.com/mobi-100k.jpg', 95000, 100000, 2, 60, 'ACTIVE'),
(2, 5, N'Thẻ Mobifone 200.000đ', N'Thẻ nạp Mobifone mệnh giá 200.000 VNĐ', 'https://example.com/mobi-200k.jpg', 190000, 200000, 3, 35, 'ACTIVE');
GO

-- Thẻ Quà Tặng - Google Play (category_id = 3, provider_id = 6)
INSERT INTO Product (category_id, provider_id, name, description, image_url, cost_price, sell_price, discount_percent, quantity, status) VALUES
(3, 6, N'Google Play 100.000đ', N'Thẻ Google Play mệnh giá 100.000 VNĐ', 'https://example.com/ggplay-100k.jpg', 95000, 100000, 0, 50, 'ACTIVE'),
(3, 6, N'Google Play 200.000đ', N'Thẻ Google Play mệnh giá 200.000 VNĐ', 'https://example.com/ggplay-200k.jpg', 190000, 200000, 0, 40, 'ACTIVE'),
(3, 6, N'Google Play 500.000đ', N'Thẻ Google Play mệnh giá 500.000 VNĐ', 'https://example.com/ggplay-500k.jpg', 475000, 500000, 2, 25, 'ACTIVE');
GO

-- Thẻ Quà Tặng - Apple iTunes (category_id = 3, provider_id = 7)
INSERT INTO Product (category_id, provider_id, name, description, image_url, cost_price, sell_price, discount_percent, quantity, status) VALUES
(3, 7, N'iTunes 100.000đ', N'Thẻ iTunes mệnh giá 100.000 VNĐ', 'https://example.com/itunes-100k.jpg', 96000, 100000, 0, 45, 'ACTIVE'),
(3, 7, N'iTunes 200.000đ', N'Thẻ iTunes mệnh giá 200.000 VNĐ', 'https://example.com/itunes-200k.jpg', 192000, 200000, 0, 35, 'ACTIVE'),
(3, 7, N'iTunes 500.000đ', N'Thẻ iTunes mệnh giá 500.000 VNĐ', 'https://example.com/itunes-500k.jpg', 480000, 500000, 3, 20, 'ACTIVE');
GO

-- Thẻ Dịch Vụ - Steam (category_id = 4, provider_id = 8)
INSERT INTO Product (category_id, provider_id, name, description, image_url, cost_price, sell_price, discount_percent, quantity, status) VALUES
(4, 8, N'Steam Wallet 100.000đ', N'Thẻ Steam Wallet mệnh giá 100.000 VNĐ', 'https://example.com/steam-100k.jpg', 95000, 100000, 0, 60, 'ACTIVE'),
(4, 8, N'Steam Wallet 200.000đ', N'Thẻ Steam Wallet mệnh giá 200.000 VNĐ', 'https://example.com/steam-200k.jpg', 190000, 200000, 0, 45, 'ACTIVE'),
(4, 8, N'Steam Wallet 500.000đ', N'Thẻ Steam Wallet mệnh giá 500.000 VNĐ', 'https://example.com/steam-500k.jpg', 475000, 500000, 5, 25, 'ACTIVE'),
(4, 8, N'Steam Wallet 1.000.000đ', N'Thẻ Steam Wallet mệnh giá 1.000.000 VNĐ', 'https://example.com/steam-1m.jpg', 950000, 1000000, 8, 15, 'ACTIVE');
GO

-------------------------------------------------------------
-- 4. CARD INFO (Sample cards for products)
-------------------------------------------------------------

-- Cards for Garena 20k (product_id = 1)
INSERT INTO CardInfo (product_id, code, serial, expiry_date, status) VALUES
(1, 'GAR20K-001-ABCD-EFGH', 'SN20K001', '2026-12-31', 'AVAILABLE'),
(1, 'GAR20K-002-IJKL-MNOP', 'SN20K002', '2026-12-31', 'AVAILABLE'),
(1, 'GAR20K-003-QRST-UVWX', 'SN20K003', '2026-12-31', 'AVAILABLE'),
(1, 'GAR20K-004-YZAB-CDEF', 'SN20K004', '2026-12-31', 'SOLD'),
(1, 'GAR20K-005-GHIJ-KLMN', 'SN20K005', '2026-12-31', 'AVAILABLE');
GO

-- Cards for Garena 50k (product_id = 2)
INSERT INTO CardInfo (product_id, code, serial, expiry_date, status) VALUES
(2, 'GAR50K-001-OPQR-STUV', 'SN50K001', '2026-12-31', 'AVAILABLE'),
(2, 'GAR50K-002-WXYZ-1234', 'SN50K002', '2026-12-31', 'AVAILABLE'),
(2, 'GAR50K-003-5678-9ABC', 'SN50K003', '2026-12-31', 'AVAILABLE');
GO

-- Cards for Garena 100k (product_id = 3)
INSERT INTO CardInfo (product_id, code, serial, expiry_date, status) VALUES
(3, 'GAR100K-001-DEFG-HIJK', 'SN100K001', '2026-12-31', 'AVAILABLE'),
(3, 'GAR100K-002-LMNO-PQRS', 'SN100K002', '2026-12-31', 'AVAILABLE');
GO

-- Cards for Viettel 50k (product_id = 12)
INSERT INTO CardInfo (product_id, code, serial, expiry_date, status) VALUES
(12, 'VTL50K-001-TUVW-XYZ1', 'SNVTL001', '2026-06-30', 'AVAILABLE'),
(12, 'VTL50K-002-2345-6789', 'SNVTL002', '2026-06-30', 'AVAILABLE'),
(12, 'VTL50K-003-ABCD-EFGH', 'SNVTL003', '2026-06-30', 'AVAILABLE'),
(12, 'VTL50K-004-IJKL-MNOP', 'SNVTL004', '2026-06-30', 'SOLD');
GO

-- Cards for Steam 500k (product_id = 31)
INSERT INTO CardInfo (product_id, code, serial, expiry_date, status) VALUES
(31, 'STM500K-001-QRST-UVWX', 'SNSTM001', '2027-12-31', 'AVAILABLE'),
(31, 'STM500K-002-YZAB-CDEF', 'SNSTM002', '2027-12-31', 'AVAILABLE');
GO

-------------------------------------------------------------
-- 5. ADDITIONAL CUSTOMERS
-------------------------------------------------------------
INSERT INTO [User] (email, password_hash, full_name, phone, address, status, wallet_balance, role_id) VALUES
('nguyenvana@gmail.com', '$2a$12$/43J6kpkYk.EFIcJtYGkBO2Vks88HQVkyBFb6L71S/3KXwe7nHR5.', N'Nguyễn Văn A', '0901234567', N'123 Nguyễn Huệ, Quận 1, TP.HCM', 'ACTIVE', 150000, 3),
('tranthib@gmail.com', '$2a$12$/43J6kpkYk.EFIcJtYGkBO2Vks88HQVkyBFb6L71S/3KXwe7nHR5.', N'Trần Thị B', '0912345678', N'456 Lê Lợi, Quận 3, TP.HCM', 'ACTIVE', 500000, 3),
('lequangc@gmail.com', '$2a$12$/43J6kpkYk.EFIcJtYGkBO2Vks88HQVkyBFb6L71S/3KXwe7nHR5.', N'Lê Quang C', '0923456789', N'789 Trần Hưng Đạo, Quận 5, TP.HCM', 'ACTIVE', 75000, 3),
('phamthid@gmail.com', '$2a$12$/43J6kpkYk.EFIcJtYGkBO2Vks88HQVkyBFb6L71S/3KXwe7nHR5.', N'Phạm Thị D', '0934567890', N'101 Võ Văn Tần, Quận 10, TP.HCM', 'LOCKED', 0, 3),
('hoangvane@gmail.com', '$2a$12$/43J6kpkYk.EFIcJtYGkBO2Vks88HQVkyBFb6L71S/3KXwe7nHR5.', N'Hoàng Văn E', '0945678901', N'202 Điện Biên Phủ, Bình Thạnh, TP.HCM', 'ACTIVE', 1000000, 3);
GO

-- Additional staff
INSERT INTO [User] (email, password_hash, full_name, phone, address, status, wallet_balance, role_id) VALUES
('staff2@ocs.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Nhân Viên 2', '0900000004', N'Office 2', 'ACTIVE', 0, 2),
('staff3@ocs.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Nhân Viên 3', '0900000005', N'Office 3', 'INACTIVE', 0, 2);
GO

-------------------------------------------------------------
-- 6. SAMPLE ORDERS
-------------------------------------------------------------
-- Customer 4 (nguyenvana) buys Garena 20k card (cardinfo_id = 4 which is SOLD)
INSERT INTO [Order] (user_id, cardinfo_id, original_price, discount_percent, final_price, status, receiver_email) VALUES
(4, 4, 20000, 0, 20000, 'COMPLETED', 'nguyenvana@gmail.com');
GO

-- Customer 5 (tranthib) buys Viettel 50k card (cardinfo_id = 14 which is SOLD)  
INSERT INTO [Order] (user_id, cardinfo_id, original_price, discount_percent, final_price, status, receiver_email) VALUES
(5, 14, 50000, 2, 49000, 'COMPLETED', 'tranthib@gmail.com');
GO

-------------------------------------------------------------
-- 7. SAMPLE WALLET TRANSACTIONS
-------------------------------------------------------------
-- Topup for nguyenvana
INSERT INTO WalletTransaction (user_id, type, amount, balance, status, reference_code, created_at) VALUES
(4, 'TOPUP', 200000, 200000, 'SUCCESS', 'TXN20241201001', '2024-12-01 10:30:00'),
(4, 'PURCHASE', -20000, 180000, 'SUCCESS', 'ORD20241201001', '2024-12-01 11:00:00'),
(4, 'TOPUP', 50000, 230000, 'SUCCESS', 'TXN20241205001', '2024-12-05 14:20:00');
GO

-- Topup for tranthib
INSERT INTO WalletTransaction (user_id, type, amount, balance, status, reference_code, created_at) VALUES
(5, 'TOPUP', 500000, 500000, 'SUCCESS', 'TXN20241202001', '2024-12-02 09:15:00'),
(5, 'PURCHASE', -49000, 451000, 'SUCCESS', 'ORD20241202001', '2024-12-02 09:30:00'),
(5, 'TOPUP', 100000, 551000, 'SUCCESS', 'TXN20241210001', '2024-12-10 16:45:00');
GO

-------------------------------------------------------------
-- 8. SAMPLE PRODUCT LOGS
-------------------------------------------------------------
INSERT INTO ProductLog (product_id, user_id, action, field_name, old_value, new_value, note) VALUES
(1, 2, 'CREATE', NULL, NULL, NULL, N'Tạo sản phẩm mới'),
(1, 2, 'UPDATE', 'sell_price', '19000', '20000', N'Cập nhật giá bán'),
(3, 2, 'UPDATE', 'discount_percent', '0', '5', N'Thêm khuyến mãi 5%'),
(5, 2, 'UPDATE', 'discount_percent', '5', '10', N'Tăng khuyến mãi lên 10%');
GO

PRINT N'Sample data inserted successfully!';
GO
