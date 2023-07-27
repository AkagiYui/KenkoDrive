plugins {
    java
    id("org.springframework.boot") version "3.1.2" // Spring Boot
    id("io.spring.dependency-management") version "1.1.2" // 依赖管理
}

group = "com.akagiyui"
version = "0.0.1"
java {
    sourceCompatibility = JavaVersion.VERSION_17
}

val hutoolVersion = "5.8.20"

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    maven {
        url = uri("https://maven.aliyun.com/repository/public/")
    }
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.yaml:snakeyaml:2.0")  // 覆盖 Spring Boot 默认的 SnakeYAML 版本，解决 CVE-2022-41854
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")  // ORM 框架
    implementation("org.springframework.boot:spring-boot-starter-data-redis")  // Redis 操作
    implementation("org.springframework.boot:spring-boot-starter-mail")  // 邮件发送
    implementation("org.springframework.boot:spring-boot-starter-security")  // 认证 & 授权
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")  // 模板引擎
    implementation("org.springframework.boot:spring-boot-starter-validation")  // 参数校验
    implementation("org.springframework.boot:spring-boot-starter-web")  // Web 开发
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")  // Thymeleaf Spring Security
    implementation("cn.hutool:hutool-core:${hutoolVersion}")  // Hutool 核心工具包
    implementation("cn.hutool:hutool-crypto:${hutoolVersion}")  // Hutool 加解密
    implementation("cn.hutool:hutool-jwt:${hutoolVersion}")  // Hutool JWT
    implementation("com.google.guava:guava:31.1-jre")  // Guava 工具包
    implementation("net.coobird:thumbnailator:0.4.20")  // 图片处理
    compileOnly("org.projectlombok:lombok")  // Lombok
    runtimeOnly("com.mysql:mysql-connector-j")  // MySQL 驱动
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
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
