rootProject.name = "kenko-drive" // 父项目名称

pluginManagement {
    repositories {
        maven {
            setUrl("https://maven.aliyun.com/repository/gradle-plugin/")
        }
        gradlePluginPortal()
    }
}
include(
    "app", // 主程序
    "easy-captcha", // 验证码
)
