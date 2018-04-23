package com.netty5.controller;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

@Controller
public class NettyController {
	@ResponseBody
	@RequestMapping(value = "/netty", method = RequestMethod.POST)
	public Map<String, String> postMe(@RequestParam("name") String instr) {
		Map<String, String> map = new HashMap<String, String>();
		System.out.println("inStr:" + instr);
		try {
			ClientHandler c = new ClientHandler();
			String handler = sendMessage("127.0.0.1", 11111, instr, c);
			map.put("code", "0");
			map.put("msg", handler);
		} catch (Exception e) {
			map.put("code", "0");
			map.put("msg", e.getMessage());
			e.printStackTrace();
		}
		System.out.println("map:" + map.toString());
		return map;
	}

	public static String sendMessage(final String hostAddress, final int port, String request,
			final ClientHandler chand) {
		String message="";
		Bootstrap client = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup();
		ChannelFuture future = null;
		try {
			client.group(group);
			client.option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);
			client.option(ChannelOption.TCP_NODELAY, Boolean.TRUE);
			client.channel(NioSocketChannel.class);
			client.handler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast(new StringDecoder(Charset.defaultCharset()));
					pipeline.addLast(new StringEncoder(Charset.defaultCharset()));
					pipeline.addLast(chand);
				}
			});
			future = client.connect(hostAddress, port).sync();
			if (future.isSuccess()) {
				System.out.println("客户端连接成功，并向服务端发送消息:"+request);
				ChannelFuture future2 = future.channel().writeAndFlush(request).sync();
				System.out.println("客户端发送数据是否成功:" + future2.isSuccess());
			}
		} catch (Exception e) {
		}
		try {
			Thread.sleep(200);
			message=chand.getMessage();
			if (future != null) {
				//System.out.println("是否关闭成功:"+future.channel().closeFuture().sync().isSuccess());
			}
			group.shutdownGracefully();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return message;
	}
}
