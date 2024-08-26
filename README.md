# KenkoDrive 我的云盘

[![GitHub License](https://img.shields.io/github/license/AkagiYui/KenkoDrive?style=flat-square)](https://github.com/AkagiYui/KenkoDrive?tab=readme-ov-file#MIT-1-ov-file)
![GitHub top language](https://img.shields.io/github/languages/top/AkagiYui/KenkoDrive?style=flat-square)
[![GitHub commit activity](https://img.shields.io/github/commit-activity/t/AkagiYui/KenkoDrive?style=flat-square)](https://github.com/AkagiYui/KenkoDrive/commits/)
[![GitHub last commit](https://img.shields.io/github/last-commit/AkagiYui/KenkoDrive?style=flat-square)](https://github.com/AkagiYui/KenkoDrive/commits/)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/AkagiYui/KenkoDrive?style=flat-square)
[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/AkagiYui/KenkoDrive/test.yml?style=flat-square)](https://github.com/AkagiYui/KenkoDrive/actions/workflows/test.yml)
[![GitHub Repo stars](https://img.shields.io/github/stars/AkagiYui/KenkoDrive?style=flat-square)](https://github.com/AkagiYui/KenkoDrive/stargazers)
[![Codecov](https://img.shields.io/codecov/c/github/AkagiYui/KenkoDrive?style=flat-square)](https://codecov.io/gh/AkagiYui/KenkoDrive)

一个基于 `SpringBoot 3.2`、`Spring Security`、`Gradle 8.7` 和 `JPA` ，使用 `Kotlin` 编写的 Web 云盘应用单体后端。
项目整体结构清晰，职责明确，注释全面，开箱即用。

> [!IMPORTANT]
> 该项目仅为个人学习项目，不具备商业使用价值，仅供学习交流。
> 如果你需要一款类似的软件，不妨看看[alist](https://github.com/alist-org/alist)。

> [!CAUTION]
> 该项目仍处于初期开发阶段，数据库结构将会频繁变动，不建议在生产环境中使用，请注意备份数据。

|      相关       |                                                                          链接                                                                          |
|:-------------:|:----------------------------------------------------------------------------------------------------------------------------------------------------:|
|   GitHub仓库    |                                       [github.com/AkagiYui/KenkoDrive](https://github.com/AkagiYui/KenkoDrive)                                       |
| GitLink中国大陆仓库 |                                   [gitlink.org.cn/AkagiYui/KenkoDrive](https://gitlink.org.cn/AkagiYui/KenkoDrive)                                   |
|    在线演示地址     | [![Website](https://img.shields.io/website?url=https%3A%2F%2Fdrive.akagiyui.com%2F&style=flat-square)drive.akagiyui.com](https://drive.akagiyui.com) |
|    API 文档     |                                                [kenkodrive.apifox.cn](https://kenkodrive.apifox.cn/)                                                 |
|     前端仓库      |                [KenkoDriveVue](https://github.com/AkagiYui/KenkoDriveVue) / [中国大陆镜像仓库](https://gitlink.org.cn/AkagiYui/KenkoDriveVue)                |

## 预览图

![登录页面](docs/login.png "登录页面")
![概览页面](docs/overview.png "概览页面")

## 业务功能一览

- [x] 用户(邮箱)注册；(短信/用户名/邮箱)登录
- [x] 个人信息设置、头像上传、密码重置
- [x] 文件(夹)上传/下载/删除
- [ ] 文件分享
- [ ] 游客广场
- [x] 管理员用户管理
- [ ] 管理员文件管理
- [ ] 文件搜索
- [ ] 文件内容检索
- [ ] 照片地理位置统计
- [ ] 用户登录地理位置统计
- [ ] 文件类型统计
- [ ] 流量统计
- [ ] 在线解压
- [ ] 批量打包下载
- [ ] 敏感内容审查
- [ ] (图片、视频、音频封面)缩略图生成
- [ ] 系统告警通知
- [ ] 第三方登录
- [ ] 增值服务
- [ ] 回收站
- [ ] 文件收藏
- [ ] 大文件清理
- [ ] 重复文件清理
- [ ] 文档在线编辑
- [ ] 图片内容识别
- [ ] 离线下载
- [ ] 文件同步
- [ ] 文件版本管理
- [ ] 文档协作

### 文件快递柜

- [ ] 匿名分享：无需注册，直接分享
- [ ] 支持多种类型：文本，图片，文件
- [ ] 随机取件码生成
- [ ] 自定义次数、有效期

## 相关技术一览

- [x] [请求频率限制(注解 + 令牌桶)](app/src/main/kotlin/com/akagiyui/drive/component/limiter/FrequencyLimitAspect.kt)
- [x] [下载速度限制](app/src/main/kotlin/com/akagiyui/common/BucketManager.kt)
- [x] [异步任务](app/src/main/kotlin/com/akagiyui/drive/service/MailService.kt)
- [x] [邮件发送](app/src/main/kotlin/com/akagiyui/drive/service/MailService.kt)
- [x] [参数校验](app/src/main/kotlin/com/akagiyui/drive/model/request/user/AddUserRequest.kt)
- [x] [权限校验](app/src/main/kotlin/com/akagiyui/drive/model/Permission.kt)
- [x] [统一 JSON 格式返回](app/src/main/kotlin/com/akagiyui/common/ResponseResult.kt)
- [ ] [日志记录](app/src/main/kotlin/com/akagiyui/drive/component/DatabaseLogAppender.kt)
- [x] [速度限制、流量限制](app/src/main/kotlin/com/akagiyui/drive/controller/FileController.kt)
- [x] 验证码(CAPTCHA): [图片验证码](app/src/main/kotlin/com/akagiyui/drive/service/CaptchaService.kt)、
  [极验行为验证码](app/src/main/kotlin/com/akagiyui/drive/component/captcha/GeetestCaptchaV4Aspect.kt)
- [x] 一次性密码(OTP): [邮箱验证码](app/src/main/kotlin/com/akagiyui/drive/service/impl/MailServiceImpl.kt)、
  [阿里云短信验证码](app/src/main/kotlin/com/akagiyui/drive/service/impl/SmsServiceImpl.kt)
- [x] [断点续传](app/src/main/kotlin/com/akagiyui/drive/controller/FileController.kt)
- [ ] [分片上传](app/src/main/kotlin/com/akagiyui/drive/service/UploadService.kt)
- [x] [分片下载](app/src/main/kotlin/com/akagiyui/drive/controller/FileController.kt)
- [ ] 文件秒传
- [x] [相同文件合并(上传时检测)](app/src/main/kotlin/com/akagiyui/drive/service/impl/UploadServiceImpl.kt)
- [x] [Gotify 消息推送](app/src/main/kotlin/com/akagiyui/common/notifier/GotifyPusher.kt)
- [x] [定时任务](app/src/main/kotlin/com/akagiyui/drive/task/CronTasks.kt)
- [ ] OAuth2.0
- [ ] 对接支付宝
- [ ] Gravatar / Cravatar 头像

## 技术栈

[![Kotlin](https://img.shields.io/badge/Kotlin-7f52ff?logo=kotlin&logoColor=white&style=flat-square)](https://kotlinlang.org/)
[![Gradle](https://img.shields.io/badge/Gradle-blue?logo=gradle&logoColor=white&style=flat-square)](https://gradle.com/)
[![SpringBoot](https://img.shields.io/badge/SpringBoot-6cb52d?logo=springboot&logoColor=white&style=flat-square)](https://spring.io/projects/spring-boot)
[![SpringSecurity](https://img.shields.io/badge/SpringSecurity-6cb52d?logo=springsecurity&logoColor=white&style=flat-square)](https://spring.io/projects/spring-boot)
[![JPA](https://img.shields.io/badge/JPA-6cb52d?logo=spring&logoColor=white&style=flat-square)](https://spring.io/projects/spring-data-jpa)
[![MySQL](https://img.shields.io/badge/MySQL-4479a1?logo=mysql&logoColor=white&style=flat-square)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-ff4438?logo=redis&logoColor=white&style=flat-square)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-2496ed?logo=docker&logoColor=white&style=flat-square)](https://www.docker.com/)
[![Drone](https://img.shields.io/badge/Drone-212121?logo=drone&logoColor=white&style=flat-square)](https://www.drone.io/)
[![Minio](https://img.shields.io/badge/Minio-c72e49?logo=minio&logoColor=white&style=flat-square)](https://min.io/)

- [x] [Gradle 包管理](build.gradle.kts)
- [x] [Spring Boot 3.2](app/src/main/kotlin/com/akagiyui/drive/KenkoDriveApplication.kt)
- [x] [Spring Security（跨域与认证授权）](app/src/main/kotlin/com/akagiyui/drive/config/SecurityConfig.kt)
- [x] MySQL 数据库
- [x] [Spring Cache 缓存](app/src/main/kotlin/com/akagiyui/drive/config/CacheConfig.kt)
- [x] [Caffeine 本地缓存](app/src/main/kotlin/com/akagiyui/drive/config/CacheConfig.kt)
- [x] [Redis 缓存](app/src/main/kotlin/com/akagiyui/drive/component/RedisCache.kt)
- [ ] [多级缓存](https://github.com/pig-mesh/multilevel-cache-spring-boot-starter)
- [x] [JWT 鉴权](app/src/main/kotlin/com/akagiyui/common/token/TokenTemplate.kt)
- [x] [Docker 容器化部署](docker-compose.yaml)
- [x] [Drone CI/CD 自动化部署](.drone.yml)
- [x] [JPA ORM 框架](app/src/main/kotlin/com/akagiyui/drive/repository)
- [x] AOP 切面：[频率控制](app/src/main/kotlin/com/akagiyui/drive/component/limiter/FrequencyLimitAspect.kt)、
  [权限校验](app/src/main/kotlin/com/akagiyui/drive/component/permission/PermissionCheckAspect.kt)、
  [请求日志](app/src/main/kotlin/com/akagiyui/drive/component/RequestLogAspect.kt)
- [x] [ApiFox 在线 API 文档](#kenkodrive-我的云盘)
- [x] [Minio 对象存储](app/src/main/kotlin/com/akagiyui/drive/config/MinioConfig.kt)
- [ ] 阿里云 OSS 对象存储
- [ ] WebDAV
- [ ] 搜索引擎
- [x] 事务管理
- [x] [WebSocket](app/src/main/kotlin/com/akagiyui/drive/controller/persist/MemoryWebSocketHandler.kt)
- [x] [SSE(Server-Sent Events)](app/src/main/kotlin/com/akagiyui/drive/controller/persist/MemorySseController.kt)

## 活跃数据

![活跃数据](https://repobeats.axiom.co/api/embed/0ed4941f9e91671fd7d675d4ee71c21c1c497a85.svg "Repobeats analytics image")

## 其它文档

- [部署运行](docs/deploy.md)
- [更新日志](docs/changelog.md)
- [RoadMap](docs/roadmap.md)
- [项目开发规范](docs/README.md)
- [致谢名单](docs/thanks.md)

## 鸣谢

特别感谢JetBrains为开源项目提供支持。

<a href="https://jb.gg/OpenSourceSupport">
  <img src="https://user-images.githubusercontent.com/8643542/160519107-199319dc-e1cf-4079-94b7-01b6b8d23aa6.png" align="left" height="100" width="100"  alt="JetBrains">
</a>
