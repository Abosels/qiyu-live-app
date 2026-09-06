# qiyu-live-app 本机 Docker Compose 部署实施说明

## 1. 目标与边界

本说明用于指导 Claude Code 将项目整理为适合个人练手的本机 Docker Compose 环境。目标是学习部署流程，不追求 Kubernetes、Redis Cluster、MySQL 主从切换或高可用。

已确认的部署边界：

- 继续复用当前已经运行的 Redis、RocketMQ、MySQL、SRS 容器及其中的数据。
- 新建 Nacos 和 MongoDB 容器。
- 新建一份 Compose 编排全部 Java 运行服务。
- 所有新 Java 容器、Nacos 和 MongoDB 均加入同一个 Docker 网络。
- 旧 Java 服务 Compose 在切换期间停止但不删除，避免端口冲突。
- 禁止删除或重建当前 Redis、RocketMQ、MySQL、SRS 容器。
- 禁止改动或打印支付宝私钥、数据库密码等真实敏感值。

本说明不要求将 `*-interface`、`qiyu-live-framework` 等纯依赖模块容器化。仅容器化存在 `@SpringBootApplication` 的可运行服务。

## 2. 当前已知环境

截图和项目文件显示当前基础设施大致如下。Claude Code 必须先用 `docker ps` 确认实际容器名、映射端口和状态，不能假设名称一定一致。

| 类型 | 当前容器或 Compose 项目 | 处理方式 |
| --- | --- | --- |
| Redis | `redis-qiyu` | 复用，接入共享网络并给出 `redis` 别名 |
| RocketMQ NameServer | `qiyu-rmq-namesrv` | 复用，接入共享网络并给出 `rocketmq-namesrv` 别名 |
| RocketMQ Broker | `qiyu-rmq-broker` | 复用，接入共享网络并给出 `rocketmq-broker` 别名 |
| MySQL | `mysql-master` / 现有 MySQL 容器 | 复用，接入共享网络并给出 `mysql` 别名 |
| SRS | `qiyu-live-srs` / 现有 SRS 容器 | 复用，接入共享网络并给出 `srs` 别名 |
| Nacos | 未统一容器化 | 新建 |
| MongoDB | 当前为 Windows 本机服务 | 新建容器，迁移现有数据 |
| 旧 Java Compose | 根目录 `docker-compose.yml` | 停止但不删除，后续由新 App Compose 替代 |

现有根 Compose 文件中仍大量使用 `host.docker.internal`。新部署文件不能继续复制这种配置，见 [docker-compose.yml](/D:/myCode/qiyu-live-app/docker-compose.yml)。

## 3. 目标网络结构

创建一个外部共享网络：`qiyu-live-network`。

```text
                         qiyu-live-network
  +---------------------------------------------------------------+
  | redis       mysql       rocketmq-namesrv       srs            |
  |   |           |                 |                |            |
  | nacos      mongodb              |                |            |
  |   |           |                  |                |            |
  | gateway -- api -- all providers / IM services ----------------|
  +---------------------------------------------------------------+
```

Docker 网络内的 `172.x.x.x` 容器地址是正常且可访问的。问题只会发生在宿主机 Java 服务通过 Nacos 获取到容器 `172.x` 地址后尝试调用 Dubbo。因此迁移后必须保证 Dubbo Provider、Dubbo Consumer 和 Nacos 都在同一 Docker 网络内；不要让宿主机启动的 Java 服务混入这套 Docker 环境。

## 4. Claude Code 的硬性操作约束

Claude Code 必须遵守以下顺序：

1. 只做读取和检查，输出实际容器清单与端口清单。
2. 在没有得到用户确认前，不执行 `docker rm`、`docker volume rm`、`docker compose down -v`、删除目录、清空数据库等操作。
3. 不修改现有根目录 `docker-compose.yml`，而是新建部署文件。
4. 不修改业务 Java 逻辑；仅新增 Dockerfile、Compose、环境变量样例和必要的 Docker 环境配置覆盖。
5. 所有新建代码与配置必须有简洁中文注释，文件编码 UTF-8。
6. 构建或启动失败时先输出完整错误和最小修复建议，不得猜测性大范围改依赖。
7. 每一步都要执行对应验证命令，验证不通过不得进入下一步。

## 5. 实施待办表

| 状态 | 任务 | Claude Code 的动作 | 验收 |
| --- | --- | --- | --- |
| ⬜ | 基础设施盘点 | `docker ps`、`docker network ls`、`docker inspect` 核验名称与端口 | 输出真实名称，不修改容器 |
| ⬜ | 创建共享网络 | 创建 `qiyu-live-network` | `docker network inspect qiyu-live-network` 成功 |
| ⬜ | 接入旧基础设施 | 将 Redis、MySQL、RocketMQ、SRS 接入共享网络并设置别名 | 网络 Inspect 可看到对应别名 |
| ⬜ | 导出本机 MongoDB | 使用 `mongodump` 导出到仓库外目录 | 导出目录包含 BSON 数据 |
| ⬜ | 新建 Infra Compose | 新建 Nacos、MongoDB 的 Compose 文件与命名卷 | 两个容器健康运行 |
| ⬜ | 导入 MongoDB | 通过 `mongorestore` 导入旧数据 | 容器内查询到原集合和数据量 |
| ⬜ | 新建 Docker 环境变量样例 | 新建 `.env.docker.example`，真实 `.env.docker` 不提交 | `docker compose config` 成功 |
| ⬜ | 补服务 Dockerfile | 为缺失运行服务新建 Dockerfile | 每个镜像可构建 |
| ⬜ | 新建 App Compose | 编排全部 Java 服务，统一接入共享网络 | Provider 全部在 Nacos 注册 |
| ⬜ | 配置 Nacos | 上传 Docker 环境配置，地址全部使用服务别名 | Nacos 配置可加载 |
| ⬜ | 联调验证 | Gateway、Dubbo、IM、送礼、支付回调 | 见第 11 节 |

## 6. 第一步：盘点并接入旧基础设施

### 6.1 仅检查命令

在 PowerShell 执行：

```powershell
docker ps --format "table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}"
docker network ls
```

先记录 Redis、MySQL、RocketMQ NameServer、RocketMQ Broker、SRS 的真实容器名。若任何一个容器不存在或未运行，停止后续部署，先恢复该基础设施。

### 6.2 创建共享网络

```powershell
docker network create qiyu-live-network
docker network inspect qiyu-live-network
```

如果网络已存在，不视为错误，直接 Inspect 确认即可。

### 6.3 将旧容器接入共享网络

以下命令里的容器名必须替换为第 6.1 步真实名称。执行前让用户确认，执行后逐个 Inspect 验证。

```powershell
docker network connect --alias redis qiyu-live-network redis-qiyu
docker network connect --alias mysql qiyu-live-network mysql-master
docker network connect --alias rocketmq-namesrv qiyu-live-network qiyu-rmq-namesrv
docker network connect --alias rocketmq-broker qiyu-live-network qiyu-rmq-broker
docker network connect --alias srs qiyu-live-network qiyu-live-srs
docker network inspect qiyu-live-network
```

如果提示容器已连接到网络，继续 Inspect 确认别名；不要使用 `docker network disconnect -f` 强制处理。

MySQL 主从环境以实际承载业务库的容器为准。新 App 容器只通过 `mysql:3306` 访问业务主库；若业务当前实际在 Windows 本机 MySQL 而非 `mysql-master`，必须先停止并向用户说明，不能盲目将 `mysql-master` 当作业务库。

## 7. MongoDB 数据迁移

### 7.1 导出 Windows MongoDB

本机 MongoDB 仍保留，迁移前不得停止或卸载。将备份输出到仓库外目录，避免 BSON 数据被 Git 跟踪。

```powershell
New-Item -ItemType Directory -Force "$env:USERPROFILE\qiyu-live-backup"
mongodump --uri "mongodb://127.0.0.1:27017" --out "$env:USERPROFILE\qiyu-live-backup\mongodb-2026-09-03"
```

若本机 MongoDB 有用户名、密码、非默认端口或认证库，Claude Code 必须先询问用户并调整 URI，不得把凭据写进文档或 Git。

### 7.2 新 MongoDB 容器要求

新建 `docker-compose.infra.yml`，要求如下：

- 使用固定版本，例如 `mongo:7.0`，禁止 `latest`。
- 服务名必须是 `mongodb`。
- 加入外部网络 `qiyu-live-network`。
- 使用命名卷，例如 `qiyu-live-mongodb-data:/data/db`。
- 默认不映射 `27017` 到宿主机；仅在需要本机 Navicat/IDEA 调试时，通过环境变量可选暴露端口。
- 设置 root 用户和密码，但真实值放在 `.env.docker`。
- 增加基础健康检查。

启动命令：

```powershell
docker compose --env-file .env.docker -f docker-compose.infra.yml up -d mongodb
docker compose --env-file .env.docker -f docker-compose.infra.yml ps
```

### 7.3 导入数据

容器名称以 `docker compose ps` 结果为准：

```powershell
docker cp "$env:USERPROFILE\qiyu-live-backup\mongodb-2026-09-03" <mongodb-container>:/backup
docker exec <mongodb-container> mongorestore --drop /backup
```

导入后用 `mongosh` 检查 `qiyu-live-user` 库中的集合和记录数量。确认无误后才允许让 `user-provider` 改连 `mongodb`。

## 8. 新基础设施 Compose

Claude Code 新建以下文件：

```text
docker-compose.infra.yml
.env.docker.example
.gitignore 仅追加 .env.docker 规则（若尚不存在）
```

`docker-compose.infra.yml` 仅包含：

- `nacos`：单机模式，固定镜像版本，使用命名卷保存 Nacos 数据，服务别名为 `nacos`。
- `mongodb`：按第 7 节要求配置。
- `networks.qiyu-live-network.external: true`：不得让 Compose 自动创建不同名称的隔离网络。

Nacos 对宿主机只暴露管理与客户端需要的端口。默认需要至少 `8848`；若所选 Nacos 版本需要 gRPC 端口，按官方镜像要求一并暴露。容器间始终使用 `nacos:8848`。

`.env.docker.example` 只放键名和安全的示例值，例如：

```dotenv
NACOS_IMAGE=nacos/nacos-server:v2.5.0
MONGODB_IMAGE=mongo:7.0
NACOS_USERNAME=nacos
NACOS_PASSWORD=replace-me
MONGODB_ROOT_USERNAME=root
MONGODB_ROOT_PASSWORD=replace-me
```

真实 `.env.docker` 必须由用户在本机创建，不能提交 Git。

## 9. 新 App Compose 和 Dockerfile

### 9.1 运行服务范围

新建 `docker-compose.app.yml`，需编排下列运行服务：

| 服务 | 当前 Dockerfile 状态 | 操作 |
| --- | --- | --- |
| `qiyu-live-gateway` | 已有 | 复用构建模式 |
| `qiyu-live-api` | 已有 | 复用构建模式 |
| `qiyu-live-user-provider` | 已有 | 复用构建模式 |
| `qiyu-live-account-provider` | 已有 | 复用构建模式 |
| `qiyu-live-msg-provider` | 已有 | 复用构建模式 |
| `qiyu-live-id-generate-provider` | 已有 | 复用构建模式 |
| `qiyu-live-bank-api` | 缺失 | 新建 `docker/Dockerfile` |
| `qiyu-live-bank-provider` | 缺失 | 新建 `docker/Dockerfile` |
| `qiyu-live-gift-provider` | 缺失 | 新建 `docker/Dockerfile` |
| `qiyu-live-living-provider` | 缺失 | 新建 `docker/Dockerfile` |
| `qiyu-live-im-core-server` | 缺失 | 新建 `docker/Dockerfile` |
| `qiyu-live-im-provider` | 缺失 | 新建 `docker/Dockerfile` |
| `qiyu-live-im-router-provider` | 缺失 | 新建 `docker/Dockerfile` |

Dockerfile 应与项目现有 Dockerfile 风格保持一致：从模块 `target` 中复制已经构建出的可执行 JAR。不要在 Dockerfile 内执行 Maven 构建，避免重复下载依赖和本地仓库差异。

### 9.2 统一环境变量规则

所有 Java 服务加入 `qiyu-live-network`，并根据实际模块使用下列环境变量覆盖配置：

```dotenv
QIYU_NACOS_ADDR=nacos:8848
QIYU_NACOS_HOST=nacos
SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR=nacos:8848
SPRING_CLOUD_NACOS_CONFIG_SERVER_ADDR=nacos:8848
DUBBO_REGISTRY_ADDRESS=nacos://nacos:8848?namespace=public

SPRING_DATA_REDIS_HOST=redis
SPRING_DATA_REDIS_PORT=6379

QIYU_MYSQL_MASTER_HOST=mysql
QIYU_MYSQL_MASTER_PORT=3306
QIYU_MYSQL_SLAVE_HOST=mysql
QIYU_MYSQL_SLAVE_PORT=3306

QIYU_RMQ_PRODUCER_NAMESERVER=rocketmq-namesrv:9876
QIYU_RMQ_CONSUMER_NAMESERVER=rocketmq-namesrv:9876

SPRING_DATA_MONGODB_URI=mongodb://<username>:<password>@mongodb:27017/qiyu-live-user?authSource=admin
```

实际变量必须以各模块的 `bootstrap.yml`、`application.yml` 和 Nacos 配置读取方式为准。若已有配置没有对应环境变量入口，可新增 Docker profile 或最小环境变量覆盖；不要把 `host.docker.internal` 写回 Docker 配置。

Dubbo Provider 与 Consumer 都运行在同一网络时，不要设置宿主机 IP。Nacos 注册的容器 IP 可被同网络容器调用。除 HTTP、WebSocket、SRS 和支付宝回调所需端口外，不需要把所有 Dubbo 端口映射到宿主机。

### 9.3 推荐宿主机端口暴露

仅暴露练习与外部访问必须的端口，并在发现端口冲突时先报告：

| 服务 | 建议暴露端口 | 用途 |
| --- | --- | --- |
| Nacos | `8848` | 管理台与配置导入 |
| Gateway | `80` 或 `8080` | 前端 API 统一入口 |
| API | `8081` | 调试，可选 |
| Bank API | `8093` | 支付宝沙箱回调与调试 |
| IM Core TCP | `18080` | IM TCP |
| IM Core WebSocket | `18081` | 前端 WebSocket |
| SRS RTMP | `1935` | OBS 推流，复用旧容器映射 |
| SRS HTTP/HLS | `8080` | 播放，复用旧容器映射 |

## 10. Nacos 配置迁移

新 Nacos 是独立实例，旧 Nacos 中的数据不会自动出现。

导入前必须：

1. 从旧 Nacos 导出全部 Data ID 配置。
2. 删除导出文件内的真实私钥、密码后再将样例提交到仓库；真实值由用户在新 Nacos 管理台填写。
3. 将生产或 Docker 环境的地址统一替换为 Docker 服务别名。
4. 导入新 Nacos 后，逐项检查 Data ID、Group、Namespace 和应用名匹配。

重点核验：

- `qiyu-live-bank-api.yml`：支付宝密钥仅在新 Nacos 中填写，`notify-url` 为当前花生壳 HTTPS 地址。
- `qiyu-live-user-provider.yml`：MongoDB、Redis、MySQL、RocketMQ 地址。
- `qiyu-live-im-core-server.yml`：Redis、Nacos、TCP/WS 端口。
- `qiyu-live-id-generate-provider.yml`：MySQL、Nacos、Dubbo。
- `qiyu-live-im-provider.yml` 与 `qiyu-live-im-router-provider.yml`：Nacos、Dubbo。

## 11. 启动与验收顺序

### 11.1 构建

先在项目根目录构建。若构建失败，先修复编译问题，禁止跳过失败模块后强行起容器。

```powershell
mvn -s .mvn/settings.xml -DskipTests package
```

### 11.2 启动基础设施

```powershell
docker compose --env-file .env.docker -f docker-compose.infra.yml up -d
docker compose --env-file .env.docker -f docker-compose.infra.yml ps
docker network inspect qiyu-live-network
```

确认 Nacos 与 MongoDB 健康后，完成 MongoDB 导入和 Nacos 配置导入。

### 11.3 停止旧 Java App Compose

执行前必须再次确认只停 Java 服务 Compose，不会停 Redis、RocketMQ、MySQL、SRS：

```powershell
docker compose -f docker-compose.yml stop
```

不要执行 `down -v`。

### 11.4 启动新 App Compose

先启动 Provider 和 IM，再启动 API、Gateway。Compose 可以分 profile 或分多次 `up` 实现，但必须确保服务依赖关系清晰。

```powershell
docker compose --env-file .env.docker -f docker-compose.app.yml up -d --build
docker compose --env-file .env.docker -f docker-compose.app.yml ps
docker compose --env-file .env.docker -f docker-compose.app.yml logs --tail 100 qiyu-live-api
```

### 11.5 最小业务验收

| 场景 | 验收标准 |
| --- | --- |
| Nacos | 全部 Provider、API、Gateway、IM 服务成功注册 |
| Dubbo | API 调用 User、Living、Gift、Account、Bank 等 Provider 不出现不可达 IP |
| Redis/RocketMQ | 送礼、库存、异步消息没有连接错误 |
| MongoDB | 用户标签等历史数据可查询 |
| SRS | OBS 可以推流，浏览器可以通过 HLS/HTTP-FLV 播放 |
| IM | 前端可连接 `ws://<host>:18081`，登录、单播、广播正常 |
| 送礼 | 扣币一次、礼物消息广播一次、重复 MQ 不重复扣币 |
| 支付宝沙箱 | 创建订单一次；异步回调一次到账；重复回调后余额只增加一次 |

## 12. 当前非部署任务

以下事项不阻塞 Compose 基础部署，但不能宣称项目完全完成：

- 检查 `qiyu-live-gift-provider/src/java` 下重复的 `GiftProviderApplication` 是否为遗留源码；未经确认不得删除。
- PK 到期结算、`PK_FINISH` 广播、Redis 同步补偿、等待池脏数据清理、前端 PK 状态同步仍需逐项运行验证。
- 红包雨需验证配置、发送、领取、资金入账、消息广播和幂等。
- 支付宝沙箱私钥必须轮换，并从仓库文档中移除。

## 13. 可直接交给 Claude Code 的提示词

```text
请严格按照 docs/2026-09-03-docker-compose-local-deployment-claude-code-guide.md 实施 qiyu-live-app 的本机 Docker 部署。

目标：复用现有 Redis、RocketMQ、MySQL、SRS；新建 Nacos、MongoDB 和全部 Java 服务的 Compose 编排；所有容器加入 qiyu-live-network；容器内禁止使用 host.docker.internal。

约束：
1. 先只读检查 docker ps、网络、端口和当前 Compose，不得直接修改或删除任何旧容器。
2. 不得执行 docker rm、docker volume rm、docker compose down -v、删除源码或清空数据库。
3. 新建 docker-compose.infra.yml、docker-compose.app.yml、.env.docker.example；真实 .env.docker 不提交 Git。
4. MongoDB 必须先导出 Windows 本机数据，再导入容器，迁移验证通过后才允许 user-provider 使用容器 MongoDB。
5. 为 bank-api、bank-provider、gift-provider、living-provider、im-core-server、im-provider、im-router-provider 补 Dockerfile；不要删除旧 Dockerfile 或旧 Compose。
6. 对每项配置修改，先确认对应模块实际读取的环境变量，不得凭空假设。
7. 每一步完成后执行验证并报告结果。遇到错误停止，给出最小修复建议，等待确认。
8. 所有新建代码和配置用 UTF-8，并加简洁中文注释。
```
