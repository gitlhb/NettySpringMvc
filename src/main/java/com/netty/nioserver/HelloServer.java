package com.netty.nioserver;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

@Component("helloServer")
public class HelloServer {
	public final Integer PORT = 11111;
	@Resource
	HelloHandler helloHandler;

	@PostConstruct
	public void start() {
		ExecutorService pool = Executors.newCachedThreadPool();
		pool.execute(new Runnable() {
			public void run() {
				
				ServerBootstrap server = new ServerBootstrap();
				EventLoopGroup parentGroup = new NioEventLoopGroup();
				EventLoopGroup childGroup = new NioEventLoopGroup();
				try {
					server.group(parentGroup, childGroup);
					server.option(ChannelOption.SO_BACKLOG, 2048).childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
							.childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
					server.channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<Channel>() {

						@Override
						protected void initChannel(Channel channel) throws Exception {
							ChannelPipeline pipeline = channel.pipeline();
							pipeline.addLast(new StringDecoder(Charset.defaultCharset()));
							pipeline.addLast(new StringEncoder(Charset.defaultCharset()));
							pipeline.addLast(new IdleStateHandler(60,60, 120));
							pipeline.addLast(helloHandler);
						}
					});
					ChannelFuture future = server.bind(new InetSocketAddress(PORT)).sync();
					if (future.isSuccess()) {
						System.out.println("Srv start up success...");
						future.channel().closeFuture().sync();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					parentGroup.shutdownGracefully();
					childGroup.shutdownGracefully();
					System.out.println("Srv close is success...");
				}
			}
		});

		
	};

}
