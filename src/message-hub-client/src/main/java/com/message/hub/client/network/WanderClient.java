package com.message.hub.client.network;


import com.message.hub.client.ClientManger;
import com.message.hub.common.JsonConverter;
import com.message.hub.server.contract.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public class WanderClient {
    private static Logger logger = LoggerFactory.getLogger(WanderClient.class);
    private static Bootstrap bootstrap = null;
    private static Channel channel = null;
    private static String host;
    private static int port;
    private static ChannelFutureListener channelFutureListener;
    private static int workPool = 10;

    public static void start(String shost, int sport, int workPoolNum) {
        host = shost;
        port = sport;
        workPool = workPoolNum;
        try {
            run();
        } catch (Exception ex) {
            logger.error("", ex);
            start(host, port, workPoolNum);
        }

    }

    public static void run() {


        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
                pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
                pipeline.addLast("handler", new WanderChannelHandler());
                pipeline.addLast("ping", new IdleStateHandler(25, 15, 10, TimeUnit.SECONDS));
            }
        });
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

        channelFutureListener = f -> {
            if (f.isSuccess()) {
                logger.info("重新连接成功");
                ClientManger.instance().reSub();
            } else {
                logger.info("重新连接失败，3秒后重新连接");
                //  3秒后重新连接
                f.channel().eventLoop().schedule(() -> connect(), 3, TimeUnit.SECONDS);
            }
        };
        connect();
    }

    static void connect() {
        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port);
            channelFuture.addListener(channelFutureListener);
            channel = channelFuture.channel();
            logger.info(String.format("连接服务器: %s:%d！", host, port));
        } catch (Exception e) {
            logger.error("connect fail ", e);
        }
    }


    public static void send(String msg) {
        if (channel == null) {
            logger.info("链接尚未建立");
        } else {
            channel.writeAndFlush(msg);
        }
    }

    public static void send(BaseCommandContract model) {
        String json = JsonConverter.toJson(model);
//        System.out.println(json);
        send(json);
    }

    static int getWorkPool() {
        return workPool;
    }
}
