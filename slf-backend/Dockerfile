FROM openjdk:17-jdk-slim

# 挂载临时目录，部分 SpringBoot 上传文件需要
VOLUME /tmp
COPY *.jar app.jar

# 设置时区
ENV TZ=Asia/Shanghai

# 暴露端口
EXPOSE 8080

# 启动
ENTRYPOINT ["java","-jar","/app.jar"]
