package org.qiyu.live.im.core.server.starter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.qiyu.live.im.core.server.handler.ws.WsImMsgEncoder;
import org.qiyu.live.im.core.server.handler.ws.WsSharkHandler;
import org.qiyu.live.im.core.server.handler.ws.WslmServerCoreHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 独立监听浏览器 WebSocket，避免与现有 TCP 二进制协议抢占同一个端口和 pipeline。
 */
@Slf4j
@Configuration
public class NettyWsServerStarter implements InitializingBean {

    @Value("${qiyu.im.ws.port}")
    private int port;
    @Resource
    private WsImMsgEncoder wsImMsgEncoder;
    @Resource
    private WsSharkHandler wsSharkHandler;
    @Resource
    private WslmServerCoreHandler wslmServerCoreHandler;

    /** 在独立线程启动 WS 服务，避免阻塞 Spring 容器启动。 */
    private void startApplication() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        channel.pipeline().addLast(new HttpServerCodec());
                        channel.pipeline().addLast(new HttpObjectAggregator(65_536));
                        // 登录完成后缓存的 ctx 从 WsSharkHandler 写出，编码器必须位于其之前。
                        channel.pipeline().addLast(wsImMsgEncoder);
                        channel.pipeline().addLast(wsSharkHandler);
                        channel.pipeline().addLast(wslmServerCoreHandler);
                    }
                });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }));
        bootstrap.bind(port).sync().channel().closeFuture().sync();
    }

    @Override
    public void afterPropertiesSet() {
        Thread wsServerThread = new Thread(() -> {
            try {
                startApplication();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("WebSocket IM 服务启动被中断", e);
            }
        }, "qiyu-live-im-ws-server");
        wsServerThread.start();
        log.info("WebSocket IM 服务准备监听端口 {}", port);
    }
}
