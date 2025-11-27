# Docker Compose 사용법

## 환경별 실행 방법

이 프로젝트는 환경별로 **독립적인 compose 파일**을 사용합니다 (오버라이드 방식 아님).

### 로컬 개발
```bash
docker compose up
```

### 개발 서버
```bash
docker compose -f compose.dev.yaml up -d
```

## 파일 설명

- `compose.yaml`: 로컬 개발 환경 (MySQL 컨테이너 포함)
- `compose.dev.yaml`: 개발 서버 환경 (RDS 사용, Parameter Store에서 설정 로드)