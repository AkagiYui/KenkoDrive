plugins {
    java // Gradle 内置的 Java 插件，提供 Java 项目的构建支持
    id("org.springframework.boot") version "3.1.8" // Spring Boot
    id("io.spring.dependency-management") version "1.1.4" // Spring Boot 相关依赖关系管理
    kotlin("jvm") version "1.9.20" // Kotlin 支持
    id("io.sentry.jvm.gradle") version "4.3.0" // Sentry
}

group = "com.akagiyui" // 项目组织
version = "0.0.1" // 项目版本
java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    /**
     * 让 compileOnly 配置继承自 annotationProcessor 配置的所有依赖
     * 添加到 compileOnly 中的任何库将被视为注解处理器
     * 但仅在编译时有效，不会被打包到 JAR 文件中
     */
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

buildscript {
    repositories {
        mavenLocal()
        maven {
            setUrl("https://maven.aliyun.com/repository/public/")
        }
        mavenCentral()
    }
}

repositories {
    mavenLocal()
    maven {
        setUrl("https://maven.aliyun.com/repository/public/")
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

    // scope: provided
    compileOnly("org.projectlombok:lombok")  // Lombok

    // scope: runtime
    runtimeOnly("com.mysql:mysql-connector-j")  // MySQL 驱动

    // 注解处理器
    annotationProcessor("org.projectlombok:lombok")

    // scope: test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.h2database:h2") // H2 数据库，用于测试
}

// gradle test 任务配置
tasks.withType<Test> {
    // 设置 Spring Boot 的测试配置文件
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
