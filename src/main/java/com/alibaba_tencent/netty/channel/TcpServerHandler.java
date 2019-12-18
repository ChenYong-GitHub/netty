package com.alibaba_tencent.netty.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @ClassName: TcpServerHandler
 * @Desc:
 * @Author: ChenYong
 * @Date: 2019-12-18 14:54
 */
public class TcpServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // super.channelActive(ctx);
        System.out.println("channel active >>>>>>");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // super.channelRead(ctx, msg);
        System.out.println("server receive message:" + msg);
        ctx.channel().writeAndFlush("accept message" + msg);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // super.exceptionCaught(ctx, cause);
        System.out.println("get server exception :" + cause.getMessage());
    }
}
