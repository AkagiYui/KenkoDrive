# KenkoDrive 我的云盘

[![GitHub License](https://img.shields.io/github/license/AkagiYui/KenkoDrive)](https://github.com/AkagiYui/KenkoDrive?tab=readme-ov-file#MIT-1-ov-file)
![GitHub top language](https://img.shields.io/github/languages/top/AkagiYui/KenkoDrive)
[![GitHub commit activity](https://img.shields.io/github/commit-activity/t/AkagiYui/KenkoDrive)](https://github.com/AkagiYui/KenkoDrive/commits/)
[![GitHub last commit](https://img.shields.io/github/last-commit/AkagiYui/KenkoDrive)](https://github.com/AkagiYui/KenkoDrive/commits/)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/AkagiYui/KenkoDrive)
[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/AkagiYui/KenkoDrive/test.yml)](https://github.com/AkagiYui/KenkoDrive/actions/workflows/test.yml)
[![GitHub Repo stars](https://img.shields.io/github/stars/AkagiYui/KenkoDrive)](https://github.com/AkagiYui/KenkoDrive/stargazers)

一个基于 `SpringBoot 3.2`、`Spring Security`、`Gradle 8.7` 和 `JPA` 的 Web 云盘应用单体后端。
项目整体结构清晰，职责明确，注释全面，开箱即用。

GitHub仓库：[github.com/AkagiYui/KenkoDrive](https://github.com/AkagiYui/KenkoDrive)

GitLink中国大陆仓库：[gitlink.org.cn/AkagiYui/KenkoDrive](https://gitlink.org.cn/AkagiYui/KenkoDrive)

在线演示地址：[drive.akagiyui.com](https://drive.akagiyui.com)

API 文档：https://apifox.com/apidoc/project-2811497

前端仓库：[KenkoDriveVue](https://github.com/AkagiYui/KenkoDriveVue) [中国大陆镜像仓库](https://gitlink.org.cn/AkagiYui/KenkoDriveVue)

## 用户功能一览

- [x] 用户注册
- [x] 用户名登录/邮箱登录
- [x] 重置密码
- [x] 用户信息获取/修改
- [x] 用户头像获取/修改
- [ ] 用户文件上传/删除/下载
- [ ] 文件分享/分享文件下载
- [x] 文件夹
- [ ] 文件夹分享
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

## 技术功能一览

- [x] [请求频率限制（注解 + 令牌桶）](src/main/java/com/akagiyui/drive/component/limiter/LimitAspect.java)
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
- [ ] OAuth2.0
- [ ] 对接支付宝

## 技术栈

- [x] [Gradle 包管理](build.gradle.kts)
- [x] [Spring Boot 3.2](src/main/kotlin/com/akagiyui/drive/KenkoDriveApplication.kt)
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
- [x] AOP 切面编程：[频率控制](src/main/java/com/akagiyui/drive/component/limiter/LimitAspect.java)、[权限校验](src/main/java/com/akagiyui/drive/component/permission/PermissionAspect.java)、[请求日志](src/main/java/com/akagiyui/drive/component/RequestLogAspect.java)
- [x] [ApiFox 在线 API 文档](#kenkodrive-我的云盘)
- [ ] Minio 对象存储
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

![Alt](https://repobeats.axiom.co/api/embed/0ed4941f9e91671fd7d675d4ee71c21c1c497a85.svg "Repobeats analytics image")

## 鸣谢

- [Drone官方文档](https://docs.drone.io/)
- [Spring官方文档: CORS](https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html)
- [Spring官方文档: Building web applications with Spring Boot and Kotlin](https://spring.io/guides/tutorials/spring-boot-kotlin)
- [Kotlin官方文档：Lombok compiler plugin](https://kotlinlang.org/docs/lombok.html)
- [柏码知识库](https://itbaima.net/document)
- [在 Kotlin 中使用 SLF4J](https://flapypan.top/kotlin-slf4j)
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
- [掘金: SpringData JPA条件查询、排序、分页查询](https://juejin.cn/post/6985573675764285477)
- [脚本之家: springboot切换使用undertow容器的方式](https://www.jb51.net/article/254623.htm)
- [知乎: SpringBoot开始定时任务的三种方式](https://zhuanlan.zhihu.com/p/622930121)
- [知乎: ObjectMapper，别再像个二货一样一直new了！](https://zhuanlan.zhihu.com/p/498705670)
- [哔哩哔哩: 【java工程师必知】SpringBoot Validation入参校验国际化](https://www.bilibili.com/video/av742302746/)
- [哔哩哔哩: 嘿嘿，我发现了百度网盘秒传的秘密！](https://www.bilibili.com/video/av1751974636)
- [哔哩哔哩: 【IT老齐508】二十分钟快速上手Gradle](https://www.bilibili.com/video/av1602972088)
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
- [简书: Spring Boot - 数据校验](https://www.jianshu.com/p/e69a1f187482)
- [简书: java 修改HttpServletRequest的参数或请求头](https://www.jianshu.com/p/a8c9d45775ea)

## 更新日志

这里是汉化了的commit日志，并且仅包含业务相关的修改，
不包括一些代码格式化、注释修改等无关紧要的commit。
如需查看完整的commit日志，请查看[GitHub提交](https://github.com/AkagiYui/KenkoDrive/commits)。

### 2024年4月

- 添加了 Kotlin 对 Java 类中 Lombok 注解的支持

- 新增了`日志数据库记录器`，将日志记录到数据库中

- 添加了 Spring 的 Kotlin 插件，使其自动为 Spring Bean 添加 open 关键字
- 更新了 Docker 镜像构建脚本、 Drone 流水线脚本
- 新增了 slf4j 日志对象获取委托类
- 修复了默认语言中`TEST`词条的错误
- 添加了 GitHub Actions 流水线脚本，使用 Gradle 运行测试，并上传测试报告
- 迁移了部分测试样例到 Kotlin
- 迁移了部分工具类到 Kotlin
- 在用户信息中添加了`权限`字段

- 在公告信息中包含发布者的用户名
- 优化了 gradle 构建脚本，并添加了必要的注释
- 升级到 Java 21 、Gradle 8.7
- 升级到 SpringBoot 3.2.4

- 修复了 `UserController` 中的方法参数校验无效的问题
- 修改了公告相应类，使时刻
- 新增了`条件分页获取公告`接口、`设置公告状态`接口、`删除公告`接口、`修改公告`接口

- 删除了 `MetaInfoController` 与 `ServerController` ，并将其功能整合到 `SystemController` 中
- 修改了默认头像
- 在可用时使用构造函数注入
- 新增了更多的权限
- 新增了获取所有配置项的服务

- 在 gradle 测试中自动使用 test 配置文件
- 修复了`i18n`测试样例中的错误

- 统一返回时间戳而不是时间字符串
- 新增了`初始化数据库`任务，在首次启动时自动添加默认角色与管理员用户
- 在测试中使用 H2 内存数据库，不必依赖外部数据库
- 在 gradle 脚本中添加了插件的阿里云镜像源
- 修改了`用户`实体的表名为`user_info`，避免关键字冲突

- 新增了`查询文件夹路径`接口

### 2024年3月

- 新增了`获取某一用户的角色`接口
- 修改了`用户`实体类，使邮箱字段可空
- 删除了新增角色请求中对邮件的非空校验
- 支持了在更新角色信息中修改密码

- 在自定义权限校验注解检查无权限时返回403状态码

- 新增了`分配角色`接口、`移除角色`接口

- 新增了`获取某一角色的用户`接口
- 新增了 Gitea 代码仓库的 CI/CD 流水线配置

- 新增了`更新角色信息`接口
- 关闭了 GitHub Actions 流水线的 Qodana 代码检查

- 删除了`Permission`枚举类中冗余的字段
- 新增了`分页获取角色`接口、`新增角色`接口、`删除角色`接口、`重置密码`接口
- 删除了不必要的返回值

- 添加了`.editorconfig`文件，用于辅助统一代码风格

### 2024年2月

- 截断请求日志中的过长的参数与返回值

- 为构建脚本添加阿里云镜像源

- 升级到 SpringBoot 3.1.8
- 升级到 Kotlin 1.9.20

### 2023年9月

- 新增了`分片上传`接口

- 新增了`删除冗余文件`任务
- 新增了请求日志切面

- 新增了`判断文件是否已存在`接口
- 发送邮箱验证码前检查是否开放注册
- 新增了`文件上传大小限制`配置项
- 在角色实体类中添加了`是否为默认角色`字段

### 2023年8月

- 在响应类中不使用包装类
- 修改了用户实体类，使其用户名与邮箱不可重复
- 修改了`获取首页公告`接口，使其只有在登录时才有权限
- 新增了`创建文件夹`接口、`获取文件夹列表`接口
- 新增了 用户-文件关联 实体类，并添加了对应的测试样例
- 新增了通过哈希值获取文件信息的服务
- 新增了`文件分片大小`配置项
- 新增了`角色实体操作`测试样例

- 修改了下载接口的 URL ，使其默认使用断点续传
- 优化了响应类的代码结构，在构造函数中设置字段值
- 新增了键值对配置实体类
- 新增了对配置的增删改查服务
- 新增了`获取是否开放注册`接口、`设置是否开放注册`接口
- 新增了`获取文件夹内容`接口

- 使用 SQL 语句进行下载次数记录

- 为所有接口添加了权限校验

- 在业务异常中返回200状态码
- 新增了`获取全部角色`接口
- 修复了用户信息缓存未清除的问题
- 在用户信息响应中添加注册时间字段
- 新增了`设置用户状态`接口
- 新增了`新增公告`接口、`获取公告`接口、`获取首页公告`接口

- 添加了异步任务执行器配置
- 添加了异步任务异常日志输出
- 新增了`断点续传下载`接口
- 新增了 redis 在线判断检测，如果redis不可用，则使用无操作缓存

- 将`tomcat`容器替换为`undertow`容器
- 添加了 undertow 配置类
- 启用了 HTTP2 支持
- 删除了 hikari 连接池配置
- 删除了 `BaseEntity` 中的继承策略

- 在进行JWT解析前检查token长度
- 添加了国际化支持(Accept-Language)
- 添加了Gotify消息推送支持
- 新增了`获取当前用户角色`接口、`获取当前用户权限`接口
- ~~添加了`MetaInfoController`~~（已弃用）

- 优化了文件目录结构
- 支持了使用邮箱登录
- 添加了权限检查注解

- 添加了 Spring Cache 缓存依赖与配置
- 对`用户信息操作`与`文件信息操作`进行缓存操作

- 添加了对 **Kotlin** 的支持
- 将`ServerController`重构为 Kotlin 实现

### 2023年7月

- 使用logger输出位置而不是print
- 修改gradle本体下载源为腾讯云
- 添加了Qodana代码检查配置
- 添加了开源协议
- 添加了 JetBrains 注解依赖

- 新增了异步线程池配置类
- 新增了`上传头像`接口、`获取头像`接口，并且对头像图片进行压缩
- 新增了`文件处理`工具类

- 添加了 Spring Security 异常的捕获
- 新增了`获取当前用户信息`接口
- 新增了`获取用户token`接口，使用JWT作为token
- 新增了`修改当前用户信息`接口
- 添加了`注册`接口白名单

- 修复了 CI/CD 流水线脚本中的错误
- 新增了对 docker 容器的健康检查
- 升级到 Gradle 8.2.1
- 将gradle脚本从groovy迁移到kotlin

- 修复了冗余的接口数据包装
- 添加了更多异常的捕获
- 新增了`文件上传`接口、`文件下载`接口、`获取文件信息`接口、`删除文件`接口
- 新增了限流注解
- 新增了redis操作类
- 新增了redis配置类
- 新增了`邮件验证码获取`接口

- 升级到 SpringBoot 3.1.2

- 新增了`服务器版本`接口
- 修改了`分页获取用户`接口，使其拥有默认参数
- 修改了gradle脚本，使其在构建时自动替换配置文件中的版本号占位符

- 修复了接口中返回`String`类型发生异常的问题
- 新增了`删除用户`接口
- 修改了`用户`实体类，使一些字段不可为空
- 修改了`用户信息`类，新增了`用户ID`字段
- 修改了hikari连接池配置，避免了一些连接错误

### 2023年6月

- 添加了更多异常的捕获
- 新增了 Spring Security 配置类
- 新增了`分页获取用户`接口
- 新增了`用户信息`类，避免返回不必要的信息
- 新增了 docker compose 配置文件
- 删除了`获取所有用户`接口

- 新增了 Drone CI/CD 流水线脚本
- 新增了 Docker 镜像构建配置脚本

### 2023年5月

- 创建空SpringBoot项目
- 修改gradle本体下载源为华为云
- 添加了redis、hutool依赖
- 添加了必要的配置项
- 新增了 JPA 配置类
- 新增了雪花算法生成器类
- 新增了用户实体类、用户仓库类、用户服务接口、用户服务实现类、用户控制器类，以建立初步的项目结构
- 新增了应用启动事件类
- 新增了业务状态码枚举类
- 新增了返回结果包装类
- 新增了全局异常处理类
- 新增了`新增用户`接口、`获取所有用户`接口
