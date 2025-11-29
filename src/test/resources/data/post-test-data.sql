-- 테스트용 사용자 (user는 H2 예약어이므로 백틱 사용)
INSERT INTO `user` (id, email, password, nickname, created_at, updated_at)
VALUES (1, 'test@example.com', '$2a$10$dummyHashedPasswordForTestUser1234567890', 'testuser', NOW(), NOW());

-- 테스트용 게시글
INSERT INTO post (id, title, content, author_id, created_at, updated_at)
VALUES (1, '테스트 게시글', '테스트 내용입니다.', 1, NOW(), NOW());