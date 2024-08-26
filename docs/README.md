# 项目开发规范

## Controller层

Controller层方法直接返回业务数据，
包装将由`ResponseBodyAdvice`统一处理。

## 二维码登录

- 临时token：用于索引本次登录的任务
- 确认权限token：保证只有扫描者才能进行确认操作

```mermaid
sequenceDiagram
    participant A as 待登录前端
    participant S as 后端
    participant B as 已登录前端
    A ->> S: 请求临时token(浏览器信息,IP地址)
    S ->> S: 记录{临时token:(IP地址,IP属地,浏览器信息)}
    S -->> A: 临时token
    A ->> A: 使用二维码展示临时token
    A ->> S: 轮询临时token状态(临时token)
    S -->> A: 临时token状态(未扫描) "枚举{未扫描,未确认,已确认,已取消,无效}"
    B ->> A: 扫描二维码
    A -->> B: 临时token
    B ->> S: 获取待登录设备信息(临时token,确认权限token)
    S ->> S: 修改临时token状态为"未确认",记录扫描者的账号ID
    S -->> A: 临时token状态(未确认,扫描者账号信息)
    S -->> B: 待登录端信息(IP属地,浏览器信息)
    B ->> S: 确认登录(临时token,确认权限token)
    S ->> S: 修改临时token状态为"已确认",生成新AccessKey
    S -->> A: 临时token状态(已确认,新AccessKey)
    S ->> S: 销毁临时token相关信息
```

## 命名规范

### 方法名必须表明意图

- ❌`find()`
- ✅`findUser()`

### Service中不要使用`find`

- ❌`userService.findUser()`
- ✅`userService.getUser()`
