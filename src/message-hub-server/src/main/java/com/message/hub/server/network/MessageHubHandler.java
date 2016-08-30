package com.message.hub.server.network;

import com.message.hub.common.JsonConverter;
import com.message.hub.server.command.CommandProcesser;
import com.message.hub.server.contract.BaseCommandContract;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shi on 6/27/2016.
 */
@Service
@ChannelHandler.Sharable
public class MessageHubHandler extends SimpleChannelInboundHandler<String> {

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static Logger logger = LoggerFactory.getLogger(MessageHubServer.class);

    @Autowired
    CommandProcesser commandProcesser;

    /**
     * 接受到的消息处理
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (msg == null || msg.isEmpty()) {
            return;
        }
        executorService.submit(() -> {
            try {
                BaseCommandContract cmd = JsonConverter.fromJson(msg, BaseCommandContract.class);
                if (cmd != null) {
                    cmd = commandProcesser.process(cmd,ctx.channel());
                    if (cmd != null)
                        ctx.channel().writeAndFlush(JsonConverter.toJson(cmd));
                }
            } catch (Exception ex) {
                logger.error("", ex);
            }
        });
    }

    /**
     * 发生异常？
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Unexpected exception from downstream." , cause);
        commandProcesser.close(ctx.channel());
        ctx.close();

    }
}
