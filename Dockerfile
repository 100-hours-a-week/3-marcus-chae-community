# Stage 1: Build
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /build

# Gradle wrapper와 설정 파일 복사 (의존성 캐싱용)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 의존성 다운로드 (이 레이어는 build.gradle 변경 시에만 재빌드)
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사
COPY src src

# 빌드 (테스트는 GitHub Actions에서 이미 했으므로 스킵)
RUN ./gradlew build -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

# JVM 메모리 설정 환경 변수 (기본값: 컨테이너 메모리의 70%)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MinRAMPercentage=70.0 -XX:MaxRAMPercentage=70.0 -XX:InitialRAMPercentage=70.0"

WORKDIR /app
COPY --from=builder /build/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080

SHELL ["/bin/bash", "-c"]
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar