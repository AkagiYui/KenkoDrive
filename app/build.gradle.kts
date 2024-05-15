@file:Suppress("VulnerableLibrariesLocal")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    jacoco // 代码覆盖率
    id("org.springframework.boot") version "3.2.4" // Spring Boot
    id("io.spring.dependency-management") version "1.1.4" // Spring Boot 相关依赖关系管理
    kotlin("jvm") version "1.9.23" // Kotlin 支持
    /**
     * Kotlin Spring 插件
     * https://kotlinlang.org/docs/all-open-plugin.html#spring-support
     *
     * 自动为 Spring Bean 添加 open 修饰符
     */
    kotlin("plugin.spring") version "1.9.23"
}

group = "com.akagiyui" // 项目组织
version = "0.0.1" // 项目版本
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

allOpen {
    // 为 JPA 实体类添加 open 修饰符
    annotation("jakarta.persistence.MappedSuperclass")
}

val jjwtVersion = "0.12.5"
val hutoolVersion = "5.8.25"
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib") // Kotlin 标准库
    implementation("org.jetbrains.kotlin:kotlin-reflect") // Kotlin 反射
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0") // Kotlin 协程
    implementation("org.yaml:snakeyaml:2.0")  // 覆盖 Spring Boot 默认的 SnakeYAML 版本，解决 CVE-2022-41854
    implementation("org.jetbrains:annotations:24.0.1") // JetBrain 的注解，如 @NonNull
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")  // ORM 框架
    implementation("org.springframework.boot:spring-boot-starter-data-redis")  // Redis 操作
    implementation("org.springframework.boot:spring-boot-starter-cache")  // 缓存
    implementation("org.springframework.boot:spring-boot-starter-mail")  // 邮件发送
    implementation("org.springframework.boot:spring-boot-starter-security")  // 认证 & 授权
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")  // 模板引擎
    implementation("org.springframework.boot:spring-boot-starter-validation")  // 参数校验
    implementation("org.springframework.boot:spring-boot-starter-websocket") {  // WebSocket 开发
        exclude("org.springframework.boot", "spring-boot-starter-tomcat")  // 排除内置 Tomcat
    }
    implementation("org.springframework.boot:spring-boot-starter-web") {  // Web 开发
        exclude("org.springframework.boot", "spring-boot-starter-tomcat")  // 排除内置 Tomcat
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow")  // Undertow
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")  // Thymeleaf Spring Security
    implementation("cn.hutool:hutool-core:$hutoolVersion")  // Hutool 核心工具包
    implementation("cn.hutool:hutool-crypto:$hutoolVersion")  // Hutool 加密工具包
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")  // JWT
    implementation("com.bucket4j:bucket4j_jdk17-core:8.12.0")  // 限流工具
    implementation("net.coobird:thumbnailator:0.4.20")  // 缩略图生成
    implementation("io.minio:minio:8.5.8")  // MinIO 客户端
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")  // Caffeine 内存缓存
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.+") // 对Kotlin类和数据类的序列化/反序列化的支持
    implementation("com.aliyun:alibabacloud-dysmsapi20170525:2.0.24") { // 阿里云短信服务
        // 排除 pull-parser，该库导致 logback 配置文件无法加载
        // 该库用于解析 XML，但是本项目不需要在阿里云短信服务中使用 XML
        exclude("pull-parser", "pull-parser")
    }
    implementation("com.github.oshi:oshi-core:6.6.0")  // 系统信息获取
    implementation(project(":easy-captcha")) // 验证码

    // scope: runtime
    runtimeOnly("com.mysql:mysql-connector-j")  // MySQL 驱动
    runtimeOnly("org.postgresql:postgresql")  // PostgreSQL 驱动
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")  // MariaDB 驱动
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")  // JWT
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")  // JWT

    // scope: test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.h2database:h2") // H2 数据库，用于测试
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Jacoco 配置
tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
    }
}

// gradle test 任务配置
tasks.withType<Test> {
    // 设置 Spring Boot 的测试配置文件
    systemProperty("spring.profiles.active", "test")
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf(
            "-Xjsr305=strict", // 严格检查 JSR-305 注解，如 @NonNull、@Nullable
        )
        jvmTarget = "21"
    }
}

tasks.processResources {
    // 替换配置文件中的占位符
    // IDEA 构建警告，但不影响构建：https://youtrack.jetbrains.com/issue/IDEA-296490/Unsupported-action-found-org.gradle.api.internal.file.copy.MatchingCopyAction
    filesMatching("application.yaml") {
        expand(mapOf("version" to version))
    }
}
