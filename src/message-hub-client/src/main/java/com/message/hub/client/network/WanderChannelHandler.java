package com.message.hub.client.network;

import com.message.hub.client.processer.CommandProcesser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by shi on 6/29/2016.
 */
public class WanderChannelHandler extends SimpleChannelInboundHandler<String> {
    private static ExecutorService executorService = null;
    private static Logger logger = LoggerFactory.getLogger(WanderChannelHandler.class);

    /**
     * 接受到的消息处理
     *
     * @param channelHandlerContext
     * @param s
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(WanderClient.getWorkPool());
        }
        executorService.submit(() -> {
            try {
                CommandProcesser.instance().process(s);
            } catch (Exception e) {
                logger.error("", e);
            }
        });
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                WanderClient.connect();
            }
        }, 2, TimeUnit.SECONDS);
        ctx.close();
    }

}
