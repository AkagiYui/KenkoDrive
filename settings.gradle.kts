rootProject.name = "kenko-drive" // 父项目名称

pluginManagement {
    repositories {
        maven {
            setUrl("https://maven.aliyun.com/repository/gradle-plugin/")
        }
        gradlePluginPortal()
    }
}

plugins {
    /**
     * https://github.com/gradle/foojay-toolchains
     * 通过 Foojay Toolchains 插件，Gradle 可以自动下载并使用 Foojay 提供的 JDK
     * 无需手动下载 JDK，也无需配置环境变量
     */
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
