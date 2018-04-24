package com.netty.nioserver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.socket.SocketChannel;

@Component("helloHandler")
@Scope("prototype")
@Sharable
public class HelloHandler extends SimpleChannelInboundHandler<String> {
    protected volatile static List<SocketChannel> list = new ArrayList<SocketChannel>();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                ChannelFuture future = ctx.channel().writeAndFlush("空闲时间过长...");
                future.addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        System.out.println("即将踢你下线：" + future.channel());
                        if (future.channel().isOpen() && future.channel().isActive())
                            future.channel().close();
                    }
                });
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("新来一个连接:" + ctx.channel());
        super.channelActive(ctx);
        SocketChannel channel = (SocketChannel) ctx.channel();
        if (channel.isActive()) {
            list.add(channel);
        } else {
            channel.close();
            list.remove(list.indexOf(channel));
        }
        System.out.println("在线客户端数:" + list.size());
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String message) throws Exception {
        System.out.println("接收到的消息:" + message);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ChannelFuture writeAndFlush = ctx.channel().writeAndFlush(message + "->" + simpleDateFormat.format(new Date())).sync();
        System.out.println("发送消息的结果:" + writeAndFlush.isSuccess());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        list.remove(list.indexOf(channel));
        System.out.println("移除的客户端是" + channel.toString());
    }

}
