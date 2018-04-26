package com.netty5.controller;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.CountDownLatch;

/**
 * 处理客户端发送和接收消息
 */
public class ClientHandler extends SimpleChannelInboundHandler<String> {
	/**
	 * 获取服务端返回的消息
	 */
	protected String message=null;
	/**
	 *获取等待服务器端返回的共享锁  可以理解为同步作用
	 */
	private CountDownLatch lathc;

	public CountDownLatch getLathc() {
		return lathc;
	}

	public ClientHandler(CountDownLatch lathc)
	{
		this.lathc=lathc;
	}
	public void resetLatch(CountDownLatch initLathc){
		this.lathc = initLathc;
	}
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
		System.out.println("recive message from srv:"+msg);
		setMessage(msg);
		//递减锁存器的计数，如果计数到达零，则释放所有等待的线程。
		lathc.countDown();

		ctx.close();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        System.out.println("exception:exceptionCaught");
        ctx.close();
    }
	
}
