# KenkoDrive 我的云盘

计划作为毕业设计，立项于 2023年5月31日。项目整体结构清晰，职责明确，注释全面，开箱即用。

在线演示地址：[drive.akagiyui.com](https://drive.akagiyui.com)

API 文档：https://apifox.com/apidoc/project-2811497

前端仓库：[KenkoDriveVue](https://github.com/AkagiYui/KenkoDriveVue)

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
- [ ] 违规检查
- [ ] 缩略图生成
- [ ] 系统告警通知

## 技术功能一览

- [x] [请求频率限制（注解 + 令牌桶）](src/main/java/com/akagiyui/drive/component/limiter/LimitAspect.java)
- [ ] 请求频率限制（Redis + IP地址限流）
- [x] [异步任务](src/main/java/com/akagiyui/drive/service/MailService.java)
- [x] [邮件发送](src/main/java/com/akagiyui/drive/service/MailService.java)
- [x] [参数校验](src/main/java/com/akagiyui/drive/model/request/AddUserRequest.java)
- [x] [统一 JSON 格式返回](src/main/java/com/akagiyui/drive/model/ResponseResult.java)
- [ ] 短信发送
- [ ] 日志记录
- [ ] 速度限制
- [ ] 流量限制
- [x] [邮箱验证码（Redis）](src/main/java/com/akagiyui/drive/service/impl/MailServiceImpl.java)
- [ ] 图片验证码
- [ ] 断点续传
- [ ] 分片上传
- [ ] 分片下载
- [ ] 文件秒传
- [ ] 相同文件合并

## 技术栈

- [x] [Gradle 包管理](build.gradle.kts)
- [x] [Spring Boot](src/main/java/com/akagiyui/drive/KenkoDriveApplication.java)
- [x] [Spring Security（跨域与认证授权）](src/main/java/com/akagiyui/drive/config/SecurityConfig.java)
- [x] MySQL 数据库
- [x] [Spring Cache 缓存](src/main/java/com/akagiyui/drive/config/CacheConfig.java)
- [x] [Caffeine 本地缓存](src/main/java/com/akagiyui/drive/config/CacheConfig.java)
- [x] [Redis 缓存](src/main/java/com/akagiyui/drive/component/RedisCache.java)
- [x] [JWT 鉴权](src/main/java/com/akagiyui/drive/component/JwtUtils.java)
- [x] [Docker 容器化部署](docker-compose.yaml)
- [x] [Drone CI/CD 自动化部署](.drone.yml)
- [x] [JPA ORM 框架](src/main/java/com/akagiyui/drive/repository)
- [x] [AOP 切面编程](src/main/java/com/akagiyui/drive/component/limiter/LimitAspect.java)
- [x] [ApiFox 在线 API 文档](#kenkodrive-我的云盘)
- [ ] Minio 对象存储
- [ ] 事务管理

## RoadMap

|   需求    | 状态  |    完成时间    |
|:-------:|:---:|:----------:|
| 前端自动部署  | 已完成 | 2023年6月1日  |
| 后端自动部署  | 已完成 | 2023年6月26日 |
| 用户注册/登录 | 已完成 | 2023年7月25日 |

## 鸣谢

- [Drone官方文档](https://docs.drone.io/)
- [柏码知识库](https://itbaima.net/document)

- [Spring Boot JPA 打印 SQL 语句及参数](https://www.zhangbj.com/p/1411.html)
- [Auto-accepting terms of service with Gradle build scans](https://www.yellowduck.be/posts/auto-accepting-terms-of-service-with-gradle-build-scans/)
- [Stack Overflow: How to intercept a RequestRejectedException in Spring?](https://stackoverflow.com/a/75338927/19990931)
- [腾讯云开发者社区: 将构建配置从 Groovy 迁移到 KTS](https://cloud.tencent.com/developer/article/1839887?from=15425)
- [博客园: docker-compose重新启动单个容器](https://www.cnblogs.com/yakniu/p/16982310.html)
- [博客园: SpringBoot应用程序使用Gradle配置脚本中的版本号](https://www.cnblogs.com/xupeixuan/p/15695652.html)
- [CSDN: 有关HikariPool-1 – Failed to validate connection com.mysql.cj.jdbc.ConnectionImp 错误的产生原因与解决方法](https://blog.csdn.net/qq_45886144/article/details/128984915)
- [CSDN: 数据库连接池选型 Druid vs HikariCP性能对比](https://blog.csdn.net/weixin_39098944/article/details/109228618)
- [CSDN: SpringBoot 使用 beforeBodyWrite 实现统一的接口返回类型](https://blog.csdn.net/qq_37170583/article/details/107470337)
- [CSDN: Jpa设置默认值约束](https://blog.csdn.net/github_38336924/article/details/107153217)
- [CSDN: gradle通过def定义变量指定依赖版本](https://blog.csdn.net/qq_36666651/article/details/80718761)
- [CSDN: 踩坑：springboot邮箱发送邮件，JavaMailSender自动注入失败的问题](https://blog.csdn.net/A15517340610/article/details/103764245)
- [简书: java 修改HttpServletRequest的参数或请求头](https://www.jianshu.com/p/a8c9d45775ea)
