// https://gitee.com/ele-admin/EasyCaptcha

plugins {
    `java-library`
}

dependencies {
    implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

group = "com.github.whvcse"
version = "1.6.2"
description = "Java web graphics verification code, support gif verification code."

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
