package com.alibaba_tencent.nio.client;

/**
 * @ClassName: NioClient
 * @Desc:
 * @Author: ChenYong
 * @Date: 2019-12-18 16:10
 */
public class NioClient {
    public static void main(String[] args) {

        int port = 8080;
        if(args !=null && args.length > 0){
            try {
                //port = Integer.valueOf(args[0]);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        //创建客户端线程
        new Thread(new NioClientWork("127.0.0.1",port),"client").start();

    }

}
