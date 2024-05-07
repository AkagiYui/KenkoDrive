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

> [!CAUTION]
> 该项目仍处于初期开发阶段，数据库结构将会频繁变动，不建议在生产环境中使用，请注意备份数据。

|      相关       |                                                                          链接                                                                          |
|:-------------:|:----------------------------------------------------------------------------------------------------------------------------------------------------:|
|   GitHub仓库    |                                       [github.com/AkagiYui/KenkoDrive](https://github.com/AkagiYui/KenkoDrive)                                       |
| GitLink中国大陆仓库 |                                   [gitlink.org.cn/AkagiYui/KenkoDrive](https://gitlink.org.cn/AkagiYui/KenkoDrive)                                   |
|    在线演示地址     | [![Website](https://img.shields.io/website?url=https%3A%2F%2Fdrive.akagiyui.com%2F&style=flat-square)drive.akagiyui.com](https://drive.akagiyui.com) |
|    API 文档     |                                                [kenkodrive.apifox.cn](https://kenkodrive.apifox.cn/)                                                 |
|     前端仓库      |                [KenkoDriveVue](https://github.com/AkagiYui/KenkoDriveVue) / [中国大陆镜像仓库](https://gitlink.org.cn/AkagiYui/KenkoDriveVue)                |

## 业务功能一览

- [x] 用户注册
- [x] 用户名/邮箱登录
- [x] 个人信息设置、头像上传、密码重置
- [ ] 文件(夹)上传/下载/删除
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
- [ ] 缩略图生成
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
- [x] [参数校验](app/src/main/kotlin/com/akagiyui/drive/model/request/AddUserRequest.kt)
- [x] [权限校验](app/src/main/kotlin/com/akagiyui/drive/model/Permission.kt)
- [x] [统一 JSON 格式返回](app/src/main/kotlin/com/akagiyui/common/ResponseResult.kt)
- [ ] 短信发送
- [ ] 日志记录
- [x] [速度限制](app/src/main/kotlin/com/akagiyui/drive/controller/FileController.kt)
- [ ] 流量限制
- [x] 验证码(CAPTCHA): [图片验证码](app/src/main/kotlin/com/akagiyui/drive/service/CaptchaService.kt)、
  极验行为验证码
- [x] 一次性密码(OTP): [邮箱验证码](app/src/main/kotlin/com/akagiyui/drive/service/impl/MailServiceImpl.kt)、
  阿里云短信验证码
- [x] [断点续传](app/src/main/kotlin/com/akagiyui/drive/controller/FileController.kt)
- [x] [分片上传](app/src/main/kotlin/com/akagiyui/drive/service/UploadService.kt)
- [x] [分片下载](app/src/main/kotlin/com/akagiyui/drive/controller/FileController.kt)
- [ ] 文件秒传
- [x] [相同文件合并(上传时检测)](app/src/main/kotlin/com/akagiyui/drive/service/impl/UploadServiceImpl.kt)
- [x] [Gotify 消息推送](app/src/main/kotlin/com/akagiyui/common/notifier/GotifyPusher.kt)
- [x] [定时任务](app/src/main/kotlin/com/akagiyui/drive/task/CronTasks.kt)
- [ ] OAuth2.0
- [ ] 对接支付宝

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

## RoadMap

|        需求        | 状态  |    完成时间    |
|:----------------:|:---:|:----------:|
|      前端自动部署      | 已完成 | 2023年6月1日  |
|      后端自动部署      | 已完成 | 2023年6月26日 |
|     用户注册/登录      | 已完成 | 2023年7月25日 |
|      用户权限校验      | 已完成 | 2023年8月15日 |
|      断点续传下载      | 已完成 | 2023年8月19日 |
|    升级到Java21     | 已完成 | 2024年4月14日 |
| 升级到SpringBoot3.2 | 已完成 | 2024年4月14日 |

## 活跃数据

![活跃数据](https://repobeats.axiom.co/api/embed/0ed4941f9e91671fd7d675d4ee71c21c1c497a85.svg "Repobeats analytics image")

## 部署运行

后端暴露端口默认为 `6677` 。

### 使用 Docker Compose 部署

注意：该方法会使用 Dockerfile 进行容器的本地构建。

```shell
git clone https://github.com/AkagiYui/KenkoDrive
cd KenkoDrive
docker compose -p kenko-drive -f docker-compose.yaml up -d
```

### 从源码运行

你需要拥有 JDK 21 环境，并且安装有 MySQL 8，Redis 7 与 Minio 。
默认将启动 `prod` 配置，你可以修改 `.env.yaml` 或其他配置文件中修改数据库地址等信息。

```shell
git clone https://github.com/AkagiYui/KenkoDrive
cd KenkoDrive
./gradlew bootRun
```

## 鸣谢

- [Drone官方文档](https://docs.drone.io/)
- [Spring官方文档: CORS](https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html)
- [Spring官方文档: Building web applications with Spring Boot and Kotlin](https://spring.io/guides/tutorials/spring-boot-kotlin)
- [Kotlin官方文档: Lombok compiler plugin](https://kotlinlang.org/docs/lombok.html)
- [GitHub官方文档: 创建 Redis 服务容器](https://docs.github.com/zh/actions/using-containerized-services/creating-redis-service-containers)
- [柏码知识库](https://itbaima.net/document)
- [在 Kotlin 中使用 SLF4J](https://flapypan.top/notes/kotlin#%E5%9C%A8-kotlin-%E4%B8%AD%E4%BD%BF%E7%94%A8-slf4j)
- [Spring Boot JPA 打印 SQL 语句及参数](https://www.zhangbj.com/p/1411.html)
- [Spring 框架缓存故障自动切换](https://kyon.life/post/dynamic-switch-cache-in-spring/)
- [Auto-accepting terms of service with Gradle build scans](https://www.yellowduck.be/posts/auto-accepting-terms-of-service-with-gradle-build-scans/)
- [【DB系列】JPA之update使用姿势](https://spring.hhui.top/spring-blog/2019/06/23/190623-SpringBoot%E7%B3%BB%E5%88%97%E6%95%99%E7%A8%8BJPA%E4%B9%8Bupdate%E4%BD%BF%E7%94%A8%E5%A7%BF%E5%8A%BF/)
- [Contract，开发者和 Kotlin 编译器之间的契约](https://droidyue.com/blog/2019/08/25/kotlin-contract-between-developers-and-the-compiler/)
- [GitHub: Improve CVE-2023-34035 detection](https://github.com/spring-projects/spring-security/issues/13568)
- [Stack Overflow: How to intercept a RequestRejectedException in Spring?](https://stackoverflow.com/a/75338927/19990931)
- [Stack Overflow: Map enum in JPA with fixed values?](https://stackoverflow.com/questions/2751733/map-enum-in-jpa-with-fixed-values)
- [Stack Overflow: Are many-to-many relationships possible with enums in JPA or Hibernate?](https://stackoverflow.com/questions/39870914/are-many-to-many-relationships-possible-with-enums-in-jpa-or-hibernate)
- [Stack Overflow: Proper way of streaming using ResponseEntity and making sure the InputStream gets closed](https://stackoverflow.com/questions/51845228/proper-way-of-streaming-using-responseentity-and-making-sure-the-inputstream-get)
- [Medium: Partial Data Retrieval in Spring Boot REST API](https://medium.com/@bubu.tripathy/partial-data-retrieval-in-spring-boot-rest-api-b62b7a0cae34)
- [Baeldung: Rate Limiting a Spring API Using Bucket4j](https://www.baeldung.com/spring-bucket4j)
- [腾讯云开发者社区: 将构建配置从 Groovy 迁移到 KTS](https://cloud.tencent.com/developer/article/1839887?from=15425)
- [博客园: docker-compose重新启动单个容器](https://www.cnblogs.com/yakniu/p/16982310.html)
- [博客园: SpringBoot应用程序使用Gradle配置脚本中的版本号](https://www.cnblogs.com/xupeixuan/p/15695652.html)
- [博客园: Java下载文件，中文名乱码（attachment;filename=中文文件名）](https://www.cnblogs.com/tomcatandjerry/p/11541871.html)
- [博客园: SpringBoot中logback.xml使用application.yml中属性](https://www.cnblogs.com/jianliang-Wu/p/8945343.html)
- [掘金: SpringBoot实现固定、动态定时任务 | 三种实现方式](https://juejin.cn/post/7013234573823705102)
- [掘金: SpringData JPA条件查询、排序、分页查询](https://juejin.cn/post/6985573675764285477)
- [慕课手记: Spring AOP中AspectJ切入点表达式的巧妙利用](https://www.imooc.com/article/details/id/297283)
- [脚本之家: springboot切换使用undertow容器的方式](https://www.jb51.net/article/254623.htm)
- [知乎: SpringBoot开始定时任务的三种方式](https://zhuanlan.zhihu.com/p/622930121)
- [知乎: ObjectMapper，别再像个二货一样一直new了！](https://zhuanlan.zhihu.com/p/498705670)
- [知乎: 基于 HTTP Range 实现文件分片并发下载！](https://zhuanlan.zhihu.com/p/620113538)
- [哔哩哔哩: 【java工程师必知】SpringBoot Validation入参校验国际化](https://www.bilibili.com/video/av742302746/)
- [哔哩哔哩: 嘿嘿，我发现了百度网盘秒传的秘密！](https://www.bilibili.com/video/av1751974636)
- [哔哩哔哩: 【IT老齐508】二十分钟快速上手Gradle](https://www.bilibili.com/video/av1602972088)
- [哔哩哔哩: 【IT老齐509】巧用Docker Container网络模式让应用更易维护](https://www.bilibili.com/video/av1302980032)
- [CSDN: 有关HikariPool-1 – Failed to validate connection com.mysql.cj.jdbc.ConnectionImp 错误的产生原因与解决方法](https://blog.csdn.net/qq_45886144/article/details/128984915)
- [CSDN: 数据库连接池选型 Druid vs HikariCP性能对比](https://blog.csdn.net/weixin_39098944/article/details/109228618)
- [CSDN: SpringBoot 使用 beforeBodyWrite 实现统一的接口返回类型](https://blog.csdn.net/qq_37170583/article/details/107470337)
- [CSDN: Jpa设置默认值约束](https://blog.csdn.net/github_38336924/article/details/107153217)
- [CSDN: gradle通过def定义变量指定依赖版本](https://blog.csdn.net/qq_36666651/article/details/80718761)
- [CSDN: 踩坑：springboot邮箱发送邮件，JavaMailSender自动注入失败的问题](https://blog.csdn.net/A15517340610/article/details/103764245)
- [CSDN: 解决JPA的枚举局限性](https://blog.csdn.net/listeningsea/article/details/122149580)
- [CSDN: springboot 配置 Validator 校验框架国际化 支持快速返回](https://blog.csdn.net/weixin_40461281/article/details/121597834)
- [CSDN: spring常见错误【数据库】idleTimeout is close to or more than maxLifetime, disabling it.](https://blog.csdn.net/qq_26462567/article/details/123982879)
- [CSDN: spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during v](https://blog.csdn.net/jj89929665/article/details/111387865)
- [CSDN: 大文件上传下载实现思路，分片、断点续传代码实现，以及webUpload组件](https://blog.csdn.net/weixin_52210557/article/details/124097574)
- [CSDN: kotlin 中的open关键字](https://blog.csdn.net/weixin_42600398/article/details/114486754)
- [CSDN: 在SpringBoot中使用AOP获取HttpServletRequest、HttpSession 内容](https://blog.csdn.net/fishinhouse/article/details/79896971)
- [CSDN: 【spring boot - JPA--H2报错】H2 error: “Syntax error in SQL statement ... expected identifier“](https://blog.csdn.net/wondersfan/article/details/126631804)
- [CSDN: 前端 ts 使用枚举爆红 元素隐式具有 “any“ 类型，因为索引表达式的类型不为 “number“,根据名字获取数字，根据数字获取字符](https://blog.csdn.net/weixin_45973327/article/details/131985139)
- [CSDN: 【进阶】logback之 AsyncAppender 的原理、源码及避坑建议](https://blog.csdn.net/BASK2312/article/details/128504636)
- [CSDN: JPA不用@Repository--笔记](https://blog.csdn.net/zero_cctv/article/details/109322211)
- [CSDN: java发送邮件](https://blog.csdn.net/lv_zj/article/details/134037631)
- [CSDN: springboot集成websocket报错Are you running in a Servlet container that supports JSR-356?](https://blog.csdn.net/gz_jax/article/details/120327900)
- [CSDN: @Scheduled注解简介](https://blog.csdn.net/weixin_52255395/article/details/126952819)
- [简书: Spring Boot - 数据校验](https://www.jianshu.com/p/e69a1f187482)
- [简书: java 修改HttpServletRequest的参数或请求头](https://www.jianshu.com/p/a8c9d45775ea)

## 更新日志

这里是汉化了的commit日志，并且仅包含业务相关的修改，
不包括一些代码格式化、注释修改等无关紧要的commit。
如需查看完整的commit日志，请查看[GitHub提交](https://github.com/AkagiYui/KenkoDrive/commits)。

### 2024年4月

- 修复了 `UserController` 中的方法参数校验无效的问题
- 修复了 `i18n` 测试样例中的错误
- 修复了默认语言中 `TEST` 词条的错误
- 优化了 gradle 构建脚本，并添加了必要的注释
- **升级到 Java 21 、Gradle 8.7**
- **升级到 SpringBoot 3.2.4**
- **迁移了整个项目至 Kotlin**
- 将项目结构调整为多模块结构
- 在构造函数上添加 `@Autowired` 注解而不是在参数上添加
- 更新了 Docker 镜像构建脚本、 Drone 流水线脚本
- 删除了 `MetaInfoController` 与 `ServerController` ，并将其功能整合到 `SystemController` 中
- 删除了无效的 HTTP2 配置
- 将部分配置项作为系统设置存储于数据库中
- 修改了默认头像
- 在用户信息中添加了`权限`字段
- 在公告信息中包含发布者的用户名
- 在可用时使用构造函数注入
- 统一返回时间戳而不是时间字符串
- 在测试中使用 H2 内存数据库，不必依赖外部数据库
- 在 gradle 测试中自动使用 test 配置文件
- 在 gradle 脚本中添加了插件的阿里云镜像源
- 修改了`用户`实体的表名为`user_info`，避免关键字冲突
- 引入了第三方验证码生成库
- 添加了 Kotlin 对 Java 类中 Lombok 注解的支持
- 添加了 Spring 的 Kotlin 插件，使其自动为 Spring Bean 添加 open 关键字
- 添加了 GitHub Actions 流水线脚本，使用 Gradle 运行测试，并上传测试报告
- 添加了下载速度限制
- 新增了`日志数据库记录器`，将日志记录到数据库中
- 新增了 slf4j 日志对象获取委托类
- 新增了`条件分页获取公告`接口、`设置公告状态`接口、`删除公告`接口、`修改公告`接口
- 新增了更多的权限
- 新增了获取所有配置项的服务
- 新增了`初始化数据库`任务，在首次启动时自动添加默认角色与管理员用户
- 新增了`查询文件夹路径`接口
- 新增了`创建验证码`接口、`检查验证码`接口

### 2024年3月

- 修改了`用户`实体类，使邮箱字段可空
- 删除了新增角色请求中对邮件的非空校验
- 删除了`Permission`枚举类中冗余的字段
- 删除了不必要的返回值
- 支持了在更新角色信息中修改密码
- 在自定义权限校验注解检查无权限时返回403状态码
- 关闭了 GitHub Actions 流水线的 Qodana 代码检查
- 添加了`.editorconfig`文件，用于辅助统一代码风格
- 新增了`获取某一用户的角色`接口
- 新增了`分配角色`接口、`移除角色`接口
- 新增了`获取某一角色的用户`接口
- 新增了 Gitea 代码仓库的 CI/CD 流水线配置
- 新增了`更新角色信息`接口
- 新增了`分页获取角色`接口、`新增角色`接口、`删除角色`接口、`重置密码`接口

### 2024年2月

- 截断请求日志中的过长的参数与返回值
- 为构建脚本添加阿里云镜像源
- 升级到 SpringBoot 3.1.8
- 升级到 Kotlin 1.9.20

### 2023年9月

- 发送邮箱验证码前检查是否开放注册
- 在角色实体类中添加了`是否为默认角色`字段
- 新增了`分片上传`接口
- 新增了`删除冗余文件`任务
- 新增了请求日志切面
- 新增了`判断文件是否已存在`接口
- 新增了`文件上传大小限制`配置项

### 2023年8月

- 修复了用户信息缓存未清除的问题
- 优化了文件目录结构
- 优化了响应类的代码结构，在构造函数中设置字段值
- 修改了用户实体类，使其用户名与邮箱不可重复
- 删除了 hikari 连接池配置
- 删除了 `BaseEntity` 中的继承策略
- 修改了`获取首页公告`接口，使其只有在登录时才有权限
- 修改了下载接口的 URL ，使其默认使用断点续传
- 使用 SQL 语句进行下载次数记录
- 为所有接口添加了权限校验
- 在响应类中不使用包装类
- 在业务异常中返回200状态码
- 在用户信息响应中添加注册时间字段
- 将`tomcat`容器替换为`undertow`容器
- 启用了 HTTP2 支持
- 在进行JWT解析前检查token长度
- 支持了使用邮箱登录
- 对`用户信息操作`与`文件信息操作`进行缓存操作
- 将 `ServerController` 重构为 Kotlin 实现
- ~~添加了`MetaInfoController`~~（已弃用）
- 添加了 Spring Cache 缓存依赖与配置
- 添加了异步任务执行器配置
- 添加了异步任务异常日志输出
- 添加了 undertow 配置类
- 添加了国际化支持(Accept-Language)
- 添加了Gotify消息推送支持
- 添加了权限检查注解
- 添加了对 **Kotlin** 的支持
- 新增了`创建文件夹`接口、`获取文件夹列表`接口
- 新增了 用户-文件关联 实体类，并添加了对应的测试样例
- 新增了通过哈希值获取文件信息的服务
- 新增了`文件分片大小`配置项
- 新增了`角色实体操作`测试样例
- 新增了键值对配置实体类
- 新增了对配置的增删改查服务
- 新增了`获取是否开放注册`接口、`设置是否开放注册`接口
- 新增了`获取文件夹内容`接口
- 新增了`获取全部角色`接口
- 新增了`设置用户状态`接口
- 新增了`新增公告`接口、`获取公告`接口、`获取首页公告`接口
- 新增了`断点续传下载`接口
- 新增了 redis 在线判断检测，如果redis不可用，则使用无操作缓存
- 新增了`获取当前用户角色`接口、`获取当前用户权限`接口

### 2023年7月

- 修复了 CI/CD 流水线脚本中的错误
- 修复了冗余的接口数据包装
- 修复了接口中返回`String`类型发生异常的问题
- 升级到 Gradle 8.2.1
- 升级到 SpringBoot 3.1.2
- 将 gradle 脚本从 groovy 迁移到 kotlin
- 使用 logger 输出位置而不是print
- 修改 gradle 本体下载源为腾讯云
- 添加了 Qodana 代码检查配置
- 修改了`分页获取用户`接口，使其拥有默认参数
- 修改了 gradle 脚本，使其在构建时自动替换配置文件中的版本号占位符
- 修改了`用户`实体类，使一些字段不可为空
- 修改了`用户信息`类，新增了`用户ID`字段
- 修改了 hikari 连接池配置，避免了一些连接错误
- 添加了开源协议
- 添加了 JetBrains 注解依赖
- 添加了 Spring Security 异常的捕获
- 添加了`注册`接口白名单
- 添加了更多异常的捕获
- 新增了异步线程池配置类
- 新增了`上传头像`接口、`获取头像`接口，并且对头像图片进行压缩
- 新增了`文件处理`工具类
- 新增了`获取当前用户信息`接口
- 新增了`获取用户token`接口，使用JWT作为token
- 新增了`修改当前用户信息`接口
- 新增了对 Docker 容器的健康检查
- 新增了`文件上传`接口、`文件下载`接口、`获取文件信息`接口、`删除文件`接口
- 新增了限流注解
- 新增了 redis 操作类
- 新增了 redis 配置类
- 新增了`邮件验证码获取`接口
- 新增了`服务器版本`接口
- 新增了`删除用户`接口

### 2023年6月

- 删除了`获取所有用户`接口
- 添加了更多异常的捕获
- 新增了 Spring Security 配置类
- 新增了`分页获取用户`接口
- 新增了`用户信息`类，避免返回不必要的信息
- 新增了 Docker Compose 配置文件
- 新增了 Drone CI/CD 流水线脚本
- 新增了 Docker 镜像构建配置脚本

### 2023年5月

- 创建初始SpringBoot项目
- 修改gradle本体下载源为华为云
- 添加了 redis 、 hutool 依赖
- 添加了必要的配置项
- 新增了 JPA 配置类
- 新增了雪花算法生成器类
- 新增了用户实体类、用户仓库类、用户服务接口、用户服务实现类、用户控制器类，以建立初步的项目结构
- 新增了应用启动事件类
- 新增了业务状态码枚举类
- 新增了返回结果包装类
- 新增了全局异常处理类
- 新增了`新增用户`接口、`获取所有用户`接口
