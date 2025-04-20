-- 테스트용 더미 데이터 삽입
INSERT INTO users (username, email, password_hash, role, coupang_sld, coupang_api_key, coupang_secret_key, created_at)
VALUES 
('testuser1', 'testuser1@example.com', 'hashed_password_1', 'USER', 'subld1', '1102e410-2bc1-4448-82c2-9e43c6b13b22', '6049fee70afa30f7c523930d29e5cd01e4c84961', NOW()),
('testuser2', 'testuser2@example.com', 'hashed_password_2', 'USER', 'subld2', '1102e410-2bc1-4448-82c2-9e43c6b13b22', '6049fee70afa30f7c523930d29e5cd01e4c84961', NOW()),
('adminuser', 'admin@example.com', 'hashed_password_3', 'ADMIN', 'subld3', '1102e410-2bc1-4448-82c2-9e43c6b13b22', '6049fee70afa30f7c523930d29e5cd01e4c84961', NOW());