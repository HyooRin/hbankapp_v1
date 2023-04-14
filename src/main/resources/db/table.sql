CREATE TABLE user_tb(
	id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(30) NOT NULL,
    fullname VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- user 계좌 정보 테이블 
CREATE TABLE account_tb(
	id INT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(20) NOT NULL,
    balance BIGINT NOT NULL COMMENT "계좌잔액",
    user_id INT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- 입금 내역 저장 
-- 출금 내역 저장
-- 사용자 간 이체 내역 저장 

-- history 테이블 
CREATE TABLE history_tb(
	id INT AUTO_INCREMENT PRIMARY KEY COMMENT "거래내역 ID",
    amount BIGINT NOT NULL COMMENT "거래 금액",
    w_account_id int COMMENT "출금 계좌 id",
    d_account_id INT COMMENT "입금 계좌 id",
    w_balance BIGINT COMMENT "출금 요청된 계좌의 잔액",
    d_balance BIGINT COMMENT "입금 요청된 계좌의 잔액",
    created_at TIMESTAMP NOT NULL DEFAULT now()
);