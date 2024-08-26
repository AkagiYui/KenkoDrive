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
