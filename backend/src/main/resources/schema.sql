-- 1. 사용자 테이블
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER') DEFAULT 'USER',
    coupang_sub_id VARCHAR(100),
    coupang_api_key VARCHAR(255),
    coupang_secret_key VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. 키워드 테이블
CREATE TABLE keywords (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    keyword VARCHAR(255) NOT NULL,
    score VARCHAR(255),
    is_valid BOOLEAN DEFAULT FALSE,
    user_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 3. 상품 테이블
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    keyword_id BIGINT,
    user_id BIGINT,
    product_name VARCHAR(255),
    price DECIMAL(10, 2),
    description TEXT,
    product_url TEXT,
    partner_url TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (keyword_id) REFERENCES keywords(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 4. 상품 이미지 테이블
CREATE TABLE product_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT,
    image_url TEXT,
    is_main BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 5. AI 리뷰 초안 테이블
CREATE TABLE draft_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    keyword_id BIGINT,
    product_id BIGINT,
    user_id BIGINT,
    title VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (keyword_id) REFERENCES keywords(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 6. 리뷰 목차(섹션) 테이블
CREATE TABLE review_sections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    draft_review_id BIGINT,
    section_order INT,
    heading VARCHAR(255),
    content TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (draft_review_id) REFERENCES draft_reviews(id)
);
