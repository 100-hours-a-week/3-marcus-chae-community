# Community Backend
Spring Boot 기반 커뮤니티 REST API 서버

## 개발 기간

2025.9.15 ~ 2025.12.07

## 기능

- 회원가입/로그인 (JWT)
- 게시글 작성/수정/삭제/조회
- 댓글 작성/삭제
- 게시글 목록 조회 (무한 스크롤)
- 조회수 및 댓글 수 집계

## 기술 스택

- Java 21, Spring Boot 3.5`
- MySQL 8.0
- JPA
- JWT
- Docker Compose
- AWS (EC2, ALB, ASG)
- GitHub Actions

## 프로젝트 구조

```
src/main/java/kr/adapterz/springboot/
├── auth/       # 인증/인가 (JWT)
├── user/       # 사용자 관리
├── post/       # 게시글
├── comment/    # 댓글
├── common/     # 공통 유틸리티
└── config/     # 설정
```
FE GitHub Repository: https://github.com/100-hours-a-week/3-marcus-chae-community-fe

각 도메인 패키지는 controller, service, repository, entity, dto, exception으로 구성

## 배포
- URL: https://community.marcuth.store
- API 문서: https://community.marcuth.store/api/v1/swagger-ui.html

## 데모
https://github.com/user-attachments/assets/4e9fb0c6-a17e-4984-bded-016a7e33efe0
