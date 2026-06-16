# =========================
# 1️⃣ Build stage
# =========================
FROM crpi-v2fmzydhnzmlpzjc.cn-shanghai.personal.cr.aliyuncs.com/machenkai/maven:3.8.8-eclipse-temurin-8 AS builder

WORKDIR /app

# -------------------------------
# 0️ 写 Maven settings.xml
# -------------------------------
RUN set -eux; \
    mkdir -p /root/.m2; \
    printf '%s\n' \
    '<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0"' \
    '          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"' \
    '          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd">' \
    '  <mirrors>' \
    '    <mirror>' \
    '      <id>aliyun-central</id>' \
    '      <mirrorOf>central</mirrorOf>' \
    '      <name>Aliyun Central Mirror</name>' \
    '      <url>https://maven.aliyun.com/repository/central</url>' \
    '    </mirror>' \
    '  </mirrors>' \
    '  <profiles>' \
    '    <profile>' \
    '      <id>fallback-central</id>' \
    '      <repositories>' \
    '        <repository>' \
    '          <id>central</id>' \
    '          <url>https://repo1.maven.org/maven2/</url>' \
    '          <releases><enabled>true</enabled></releases>' \
    '          <snapshots><enabled>false</enabled></snapshots>' \
    '        </repository>' \
    '      </repositories>' \
    '      <pluginRepositories>' \
    '        <pluginRepository>' \
    '          <id>central</id>' \
    '          <url>https://repo1.maven.org/maven2/</url>' \
    '          <releases><enabled>true</enabled></releases>' \
    '          <snapshots><enabled>false</enabled></snapshots>' \
    '        </pluginRepository>' \
    '      </pluginRepositories>' \
    '    </profile>' \
    '  </profiles>' \
    '  <activeProfiles>' \
    '    <activeProfile>fallback-central</activeProfile>' \
    '  </activeProfiles>' \
    '</settings>' \
    > /root/.m2/settings.xml

# -------------------------------
# 1️ 先复制根 pom
# -------------------------------
COPY pom.xml /app/pom.xml

# -------------------------------
# 2️ 拷贝 gaussdb 驱动
# -------------------------------
COPY thirdparty/postgresql-gaussdb.jar /tmp/gaussdb/postgresql-gaussdb.jar

# -------------------------------
# 3️ 安装 gaussdb 到容器内 Maven 本地仓库
# -------------------------------
RUN set -eux; \
    mkdir -p /tmp/gaussdb; \
    printf '%s\n' \
      '<project xmlns="http://maven.apache.org/POM/4.0.0"' \
      '         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"' \
      '         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">' \
      '  <modelVersion>4.0.0</modelVersion>' \
      '  <groupId>dummy</groupId>' \
      '  <artifactId>dummy</artifactId>' \
      '  <version>1.0</version>' \
      '</project>' \
      > /tmp/gaussdb/empty-pom.xml; \
    printf '%s\n' \
      '<project xmlns="http://maven.apache.org/POM/4.0.0"' \
      '         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"' \
      '         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">' \
      '  <modelVersion>4.0.0</modelVersion>' \
      '  <groupId>org.postgresql</groupId>' \
      '  <artifactId>postgresql</artifactId>' \
      '  <version>gaussdb</version>' \
      '  <packaging>jar</packaging>' \
      '</project>' \
      > /tmp/gaussdb/postgresql-gaussdb.pom; \
    mvn -q -B -s /root/.m2/settings.xml -f /tmp/gaussdb/empty-pom.xml install:install-file \
      -Dfile=/tmp/gaussdb/postgresql-gaussdb.jar \
      -DpomFile=/tmp/gaussdb/postgresql-gaussdb.pom \
      -DgroupId=org.postgresql \
      -DartifactId=postgresql \
      -Dversion=gaussdb \
      -Dpackaging=jar \
      -DgeneratePom=false

# -------------------------------
# 4️ 复制全部源码
# -------------------------------
COPY . /app

# -------------------------------
# 5️ 只构建 datax-web 及其依赖模块
# -------------------------------
RUN mvn -s /root/.m2/settings.xml -U -B \
    -Dmaven.wagon.http.retryHandler.count=5 \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=60 \
    clean package \
    -DskipTests \
    -pl datax-web -am

# =========================
# 2️ Runtime stage
# =========================
FROM crpi-v2fmzydhnzmlpzjc.cn-shanghai.personal.cr.aliyuncs.com/machenkai/eclipse-temurin:8-jre

WORKDIR /opt/datax-web

ENV TZ=Asia/Shanghai
ENV JAVA_OPTS=""
ENV JASYPT_PASSWORD=""
ENV LOG_PATH=logs

COPY --from=builder /app/datax-web/target/datax-web-1.0-SNAPSHOT.jar /opt/datax-web/app.jar

LABEL org.opencontainers.image.title="datax-web" \
      org.opencontainers.image.description="DataX web application image" \
      org.opencontainers.image.version="1.0.0" \
      org.opencontainers.image.authors="mack"

EXPOSE 28080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djasypt.encryptor.password=$JASYPT_PASSWORD -jar /opt/datax-web/app.jar"]
