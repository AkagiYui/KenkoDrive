plugins {
    java
    id("org.springframework.boot") version "3.1.8" // Spring Boot
    id("io.spring.dependency-management") version "1.1.4" // 依赖管理
    kotlin("jvm") version "1.9.20" // Kotlin 支持
}

group = "com.akagiyui"
version = "0.0.1"
java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

buildscript {
    repositories {
        mavenLocal()
        maven {
            url = uri("https://maven.aliyun.com/repository/public/")
        }
        mavenCentral()
    }
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://maven.aliyun.com/repository/public/")
    }
    mavenCentral()
}

val hutoolVersion = "5.8.25"
val snakeYAMLVersion = "2.0"
val jetbrainsAnnotationsVersion = "24.0.1"
val guavaVersion = "33.0.0-jre"
val thumbnailatorVersion = "0.4.20"
val minioVersion = "8.5.8"
val caffeineVersion = "3.1.8"
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib") // Kotlin 标准库
    implementation("org.yaml:snakeyaml:$snakeYAMLVersion")  // 覆盖 Spring Boot 默认的 SnakeYAML 版本，解决 CVE-2022-41854
    implementation("org.jetbrains:annotations:$jetbrainsAnnotationsVersion") // JetBrain 的注解，如 @NonNull
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")  // ORM 框架
    implementation("org.springframework.boot:spring-boot-starter-data-redis")  // Redis 操作
    implementation("org.springframework.boot:spring-boot-starter-cache")  // 缓存
    implementation("org.springframework.boot:spring-boot-starter-mail")  // 邮件发送
    implementation("org.springframework.boot:spring-boot-starter-security")  // 认证 & 授权
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")  // 模板引擎
    implementation("org.springframework.boot:spring-boot-starter-validation")  // 参数校验
    implementation("org.springframework.boot:spring-boot-starter-websocket")  {  // Web 开发
        exclude("org.springframework.boot", "spring-boot-starter-tomcat")  // 排除内置 Tomcat
    }
    implementation("org.springframework.boot:spring-boot-starter-web") {  // Web 开发
        exclude("org.springframework.boot", "spring-boot-starter-tomcat")  // 排除内置 Tomcat
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow")  // Undertow
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")  // Thymeleaf Spring Security
    implementation("cn.hutool:hutool-core:$hutoolVersion")  // Hutool 核心工具包
    implementation("cn.hutool:hutool-crypto:$hutoolVersion")  // Hutool 加解密
    implementation("cn.hutool:hutool-jwt:$hutoolVersion")  // Hutool JWT
    implementation("com.google.guava:guava:$guavaVersion")  // Guava 工具包
    implementation("net.coobird:thumbnailator:$thumbnailatorVersion")  // 缩略图生成
    implementation("io.minio:minio:$minioVersion")  // MinIO 客户端
    implementation("com.github.ben-manes.caffeine:caffeine:$caffeineVersion")  // Caffeine 内存缓存

    compileOnly("org.projectlombok:lombok")  // Lombok

    runtimeOnly("com.mysql:mysql-connector-j")  // MySQL 驱动

    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.h2database:h2") // H2 数据库，用于测试
}

tasks.withType<Test> {
    systemProperty("spring.profiles.active", "test")
    useJUnitPlatform()
}

tasks.processResources {
    // 替换配置文件中的占位符
    filesMatching("application.yaml") {
        expand(project.properties)
    }
}

// 自动同意 Gradle Build Scan 的条款
extensions.findByName("buildScan")?.withGroovyBuilder {
    setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
    setProperty("termsOfServiceAgree", "yes")
}

// Kotlin 代码生成版本
kotlin {
    jvmToolchain(17)
}
