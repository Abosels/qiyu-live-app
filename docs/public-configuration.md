# 公开仓库配置说明

## 原则

仓库只保存可运行的配置模板，不保存账号、密码、支付私钥、内网穿透域名或本机绝对路径。真实值只应存在于本机 `.env.docker`、Nacos 或部署平台的密钥管理中。

## Docker 环境变量

复制根目录 `.env.docker.example` 为 `.env.docker` 后填写下列值：

| 变量 | 用途 |
| --- | --- |
| `QIYU_MYSQL_PASSWORD` | MySQL 业务库密码 |
| `MONGODB_ROOT_USERNAME` / `MONGODB_ROOT_PASSWORD` | MongoDB 容器初始化账号 |
| `NACOS_USERNAME` / `NACOS_PASSWORD` | Nacos 登录凭据 |
| `QIYU_ALIPAY_APP_ID` | 支付宝沙箱应用 ID |
| `QIYU_ALIPAY_APP_PRIVATE_KEY` | 应用私钥，仅本机或密钥管理保存 |
| `QIYU_ALIPAY_PUBLIC_KEY` | 支付宝公钥 |
| `QIYU_ALIPAY_NOTIFY_URL` | 支付宝可访问的 HTTPS 异步通知地址 |
| `QIYU_ALIPAY_RETURN_URL` | 浏览器同步跳转展示地址 |

## Nacos

`docs/nacos-configs` 下的文件对应各服务的 Data ID 模板。上传前请使用真实环境变量或直接在 Nacos 中填写真实配置；不要把填好真实值的 YAML 写回仓库。

## 发布前检查

在创建 GitHub 远程前执行：

```powershell
git status
rg -l "BEGIN (RSA )?PRIVATE KEY|MII[A-Za-z0-9+/=]{40,}" .
rg -l "root1|root123456|your-real-domain" .
```

上述命令应不返回任何待发布的文件。若支付宝私钥曾出现在任何已推送提交中，应立刻在支付宝沙箱重新生成密钥，而不是只删除文件内容。
