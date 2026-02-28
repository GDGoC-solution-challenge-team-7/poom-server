# 1) Build stage
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean bootJar -x test

# 2) Run stage
FROM amazoncorretto:17
WORKDIR /app

# 타임존
RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime

# builder 단계에서 생성된 jar만 복사
COPY --from=builder /app/build/libs/*.jar soom_app.jar

ENTRYPOINT ["java","-jar","/app/soom_app.jar"]
