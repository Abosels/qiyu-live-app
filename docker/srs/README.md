# SRS 本地推拉流

启动：

```powershell
cd D:\myCode\qiyu-live-app\docker\srs
docker compose up -d
```

OBS 配置：

```text
服务：自定义
服务器：rtmp://localhost:1935/live
串流密钥：test001
```

浏览器 HLS 播放地址：

```text
http://localhost:8080/live/test001.m3u8
```

停止：

```powershell
docker compose down
```
