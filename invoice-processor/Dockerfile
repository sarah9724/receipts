FROM openjdk:17-jdk-slim

WORKDIR /app

# 复制项目jar包
COPY target/*.jar app.jar

# 创建文件存储目录
RUN mkdir -p /app/uploads /app/processed /app/temp /app/duplicate /app/reports

# 设置时区
ENV TZ=Asia/Shanghai

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"] 