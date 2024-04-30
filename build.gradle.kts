// 自动同意 Gradle Build Scan 的条款
extensions.findByName("buildScan")?.withGroovyBuilder {
    setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
    setProperty("termsOfServiceAgree", "yes")
}

subprojects {
    buildscript {
        repositories {
            mavenLocal()
            maven {
                setUrl("https://maven.aliyun.com/repository/public/")
            }
            maven {
                setUrl("https://mirrors.huaweicloud.com/repository/maven/")
            }
            mavenCentral()
        }
    }

    repositories {
        mavenLocal()
        maven {
            setUrl("https://maven.aliyun.com/repository/public/")
        }
        maven {
            setUrl("https://mirrors.huaweicloud.com/repository/maven/")
        }
        mavenCentral()
    }
}
