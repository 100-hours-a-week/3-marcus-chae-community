# Docker Compose 사용법

## 환경별 실행 방법

이 프로젝트는 환경별로 **독립적인 compose 파일**을 사용합니다 (오버라이드 방식 아님).

### 로컬 개발 (기본)
```bash
docker compose up
```
- 별도 옵션 없이 실행하면 `compose.yaml`이 사용됩니다
- **로컬 환경 전용** 설정입니다

### 개발 서버
```bash
docker compose -f compose.dev.yaml up -d
```
- `-f` 옵션으로 **별도의 독립적인 파일**을 지정합니다
- `compose.yaml`을 오버라이드하는 것이 아닙니다