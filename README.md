# KenkoDrive 我的云盘

计划作为毕业设计，立项于 2023年5月31日。项目整体结构清晰，职责明确，注释全面，开箱即用。

GitHub仓库：[github.com/AkagiYui/KenkoDrive](https://github.com/AkagiYui/KenkoDrive)

GitLink中国大陆仓库：[gitlink.org.cn/AkagiYui/KenkoDrive](https://gitlink.org.cn/AkagiYui/KenkoDrive)

在线演示地址：[drive.akagiyui.com](https://drive.akagiyui.com)

API 文档：https://apifox.com/apidoc/project-2811497

前端仓库：[KenkoDriveVue](https://github.com/AkagiYui/KenkoDriveVue) [中国大陆镜像仓库](https://gitlink.org.cn/AkagiYui/KenkoDriveVue)

## 用户功能一览

- [x] 用户注册
- [x] 用户名登录/邮箱登录
- [ ] 重置密码
- [x] 用户信息获取/修改
- [x] 用户头像获取/修改
- [ ] 用户文件上传/删除/下载
- [ ] 文件分享/分享文件下载
- [ ] 文件夹
- [ ] 文件夹分享
- [ ] 游客广场
- [ ] 管理员用户管理
- [ ] 管理员文件管理
- [ ] 文件搜索
- [ ] 照片地理位置统计
- [ ] 用户登录地理位置统计
- [ ] 文件类型统计
- [ ] 流量统计
- [ ] 在线解压
- [ ] 批量打包下载
- [ ] 敏感内容审查
- [ ] 缩略图生成
- [ ] 系统告警通知

## 技术功能一览

- [x] [请求频率限制（注解 + 令牌桶）](src/main/java/com/akagiyui/common/limiter/LimitAspect.java)
- [ ] 请求频率限制（Redis + IP地址限流）
- [x] [异步任务](src/main/java/com/akagiyui/drive/service/MailService.java)
- [x] [邮件发送](src/main/java/com/akagiyui/drive/service/MailService.java)
- [x] [参数校验](src/main/java/com/akagiyui/drive/model/request/AddUserRequest.java)
- [x] [权限校验](src/main/java/com/akagiyui/drive/model/Permission.java)
- [x] [统一 JSON 格式返回](src/main/java/com/akagiyui/common/ResponseResult.java)
- [ ] 短信发送
- [ ] 日志记录
- [ ] 速度限制
- [ ] 流量限制
- [x] [邮箱验证码（Redis）](src/main/java/com/akagiyui/drive/service/impl/MailServiceImpl.java)
- [ ] 图片验证码
- [x] [断点续传](src/main/java/com/akagiyui/drive/controller/FileController.java)
- [x] [分片上传](src/main/java/com/akagiyui/drive/service/UploadService.java)
- [x] [分片下载](src/main/java/com/akagiyui/drive/controller/FileController.java)
- [ ] 文件秒传
- [x] 相同文件合并（在上传时会检测）
- [x] [Gotify 消息推送](src/main/kotlin/com/akagiyui/common/notifier/GotifyPusher.kt)
- [x] [定时任务](src/main/java/com/akagiyui/drive/task/RemoveUnusedFileTask.java)

## 技术栈

- [x] [Gradle 包管理](build.gradle.kts)
- [x] [Spring Boot 3.1](src/main/java/com/akagiyui/drive/KenkoDriveApplication.java)
- [x] [Spring Security（跨域与认证授权）](src/main/java/com/akagiyui/drive/config/SecurityConfig.java)
- [x] MySQL 数据库
- [x] [Spring Cache 缓存](src/main/java/com/akagiyui/drive/config/CacheConfig.java)
- [x] [Caffeine 本地缓存](src/main/java/com/akagiyui/drive/config/CacheConfig.java)
- [x] [Redis 缓存](src/main/java/com/akagiyui/drive/component/RedisCache.java)
- [ ] [多级缓存](https://github.com/pig-mesh/multilevel-cache-spring-boot-starter)
- [x] [JWT 鉴权](src/main/java/com/akagiyui/drive/component/JwtUtils.java)
- [x] [Docker 容器化部署](docker-compose.yaml)
- [x] [Drone CI/CD 自动化部署](.drone.yml)
- [x] [JPA ORM 框架](src/main/java/com/akagiyui/drive/repository)
- [x] AOP 切面编程：[频率控制](src/main/java/com/akagiyui/common/limiter/LimitAspect.java)、[权限校验](src/main/java/com/akagiyui/drive/component/permission/PermissionAspect.java)、[请求日志](src/main/java/com/akagiyui/drive/component/RequestLogAspect.java)
- [x] [ApiFox 在线 API 文档](#kenkodrive-我的云盘)
- [ ] Minio 对象存储
- [ ] 阿里云 OSS 对象存储
- [ ] 搜索引擎
- [x] 事务管理

## RoadMap

|   需求    | 状态  |    完成时间    |
|:-------:|:---:|:----------:|
| 前端自动部署  | 已完成 | 2023年6月1日  |
| 后端自动部署  | 已完成 | 2023年6月26日 |
| 用户注册/登录 | 已完成 | 2023年7月25日 |
| 用户权限校验  | 已完成 | 2023年8月15日 |
| 断点续传下载  | 已完成 | 2023年8月19日 |

## 原理解析

### 文件分片上传

1. 前端 计算整个文件哈希值，询问后端是否存在该文件
2. 后端 根据 MD5 值查询数据库，如果存在该文件，则返回文件信息，否则返回空
3. 前端 根据后端返回的文件信息，判断是否需要上传
4. 前端 告诉后端，需要上传的文件信息（文件名、文件大小、分片数量）
5. 后端 写入redis（文件大小、分片数量、每个分片的【哈希值、是否完成上传】）
6. 后端 在文件系统创建临时文件夹，用于存放分片文件
7. 前端 将文件分片，伴随【文件哈希值、分片索引、分片哈希值】，与【分片二进制内容】进行上传
8. 后端 根据请求ID，将分片哈希值写入redis，计算分片哈希值
9. 后端 如果分片哈希值与前端传来的分片哈希值不一致，则丢弃并返回错误信息
10. 后端 如果分片哈希值与前端传来的分片哈希值一致，则将分片文件写入临时文件夹，返回成功信息
11. 后端 如果所有分片上传完成，则添加异步任务，将临时文件夹中的分片文件合并为一个文件，写入文件系统，删除临时文件夹


## 总结与展望

在编写与完善该项目的过程中，我学习到了许多知识与软件设计的思想，也遇到了许多困难。

区别于一般的“CURD式”管理系统，该项目的重点在于文件的管理，
比如文件的上传下载、文件的分享、文件的搜索、文件的分片上传下载等等。

目前，该项目有几个仍需完善的地方：

1. 权限管理不够灵活，未来可能需要采用ABAC模型。
2. 该项目为单体架构，在扩展性上有所欠缺，未来可能需要采用微服务架构。
3. 当前使用基于数据库的搜索方案，可以引入搜索引擎，提高搜索效率。
4. 目前仅把用户上传文件存储在本地，可以引入对象存储，提高文件的可用性。
5. 该系统当前没有使用图片验证码，行为验证等验证机制。
6. 可以使用消息队列，避免高并发时的请求阻塞与系统重启时的任务丢失。

## 鸣谢

- [Drone官方文档](https://docs.drone.io/)
- [Spring官方文档: CORS](https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html)
- [柏码知识库](https://itbaima.net/document)
- [Spring Boot JPA 打印 SQL 语句及参数](https://www.zhangbj.com/p/1411.html)
- [Spring 框架缓存故障自动切换](https://kyon.life/post/dynamic-switch-cache-in-spring/)
- [Auto-accepting terms of service with Gradle build scans](https://www.yellowduck.be/posts/auto-accepting-terms-of-service-with-gradle-build-scans/)
- [GitHub: Improve CVE-2023-34035 detection](https://github.com/spring-projects/spring-security/issues/13568)
- [Stack Overflow: How to intercept a RequestRejectedException in Spring?](https://stackoverflow.com/a/75338927/19990931)
- [Stack Overflow: Map enum in JPA with fixed values?](https://stackoverflow.com/questions/2751733/map-enum-in-jpa-with-fixed-values)
- [Stack Overflow: Are many-to-many relationships possible with enums in JPA or Hibernate?](https://stackoverflow.com/questions/39870914/are-many-to-many-relationships-possible-with-enums-in-jpa-or-hibernate)
- [Stack Overflow: Proper way of streaming using ResponseEntity and making sure the InputStream gets closed](https://stackoverflow.com/questions/51845228/proper-way-of-streaming-using-responseentity-and-making-sure-the-inputstream-get)
- [Medium: Partial Data Retrieval in Spring Boot REST API](https://medium.com/@bubu.tripathy/partial-data-retrieval-in-spring-boot-rest-api-b62b7a0cae34)
- [腾讯云开发者社区: 将构建配置从 Groovy 迁移到 KTS](https://cloud.tencent.com/developer/article/1839887?from=15425)
- [博客园: docker-compose重新启动单个容器](https://www.cnblogs.com/yakniu/p/16982310.html)
- [博客园: SpringBoot应用程序使用Gradle配置脚本中的版本号](https://www.cnblogs.com/xupeixuan/p/15695652.html)
- [博客园: Java下载文件，中文名乱码（attachment;filename=中文文件名）](https://www.cnblogs.com/tomcatandjerry/p/11541871.html)
- [掘金: SpringBoot实现固定、动态定时任务 | 三种实现方式](https://juejin.cn/post/7013234573823705102)
- [脚本之家: springboot切换使用undertow容器的方式](https://www.jb51.net/article/254623.htm)
- [知乎: SpringBoot开始定时任务的三种方式](https://zhuanlan.zhihu.com/p/622930121)
- [知乎: ObjectMapper，别再像个二货一样一直new了！](https://zhuanlan.zhihu.com/p/498705670)
- [哔哩哔哩: 【java工程师必知】SpringBoot Validation入参校验国际化](https://www.bilibili.com/video/av742302746/)
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
- [简书: Spring Boot - 数据校验](https://www.jianshu.com/p/e69a1f187482)
- [简书: java 修改HttpServletRequest的参数或请求头](https://www.jianshu.com/p/a8c9d45775ea)

## 活跃数据

![Alt](https://repobeats.axiom.co/api/embed/0ed4941f9e91671fd7d675d4ee71c21c1c497a85.svg "Repobeats analytics image")
