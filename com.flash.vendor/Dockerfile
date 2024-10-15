# 1. OpenJDK 베이스 이미지 사용
FROM openjdk:17-jdk

# 2. 작업 디렉토리 설정
WORKDIR /flash

# 3. JAR 파일을 컨테이너로 복사
COPY build/libs/vendor-0.0.1-SNAPSHOT.jar /flash/vendor.jar

# 4. 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/flash/vendor.jar"]