package com.alibaba_tencent.bio.server;

/**
 * @ClassName: NioServer
 * @Desc:
 * @Author: ChenYong
 * @Date: 2019-12-18 15:48
 */
public class NioServer {

    public static void main(String[] args) {
        int port = 8080;
        //创建服务器线程
        NioServerWork nioServerWork = new NioServerWork(port);
        new Thread(nioServerWork, "server").start();
    }

}