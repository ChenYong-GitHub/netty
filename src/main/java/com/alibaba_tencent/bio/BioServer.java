package com.alibaba_tencent.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName: BioServer
 * @Desc: BIO服务端
 * @Author: ChenYong
 * @Date: 2019-12-19 14:44
 */
public class BioServer {

    public static void main(String[] args) throws IOException {

        // 1、创建一个线程池
        // 2、如果有客户连接，就创建一个线程与之通讯
        ExecutorService threadPool = Executors.newCachedThreadPool();

        // 创建ServerSocket
        ServerSocket serverSocket = new ServerSocket(6666);

        System.out.println("服务启动了");
        System.out.println("当前线程1：" + Thread.currentThread().getName());
        while (true) {

            // 监听，等待客户连接
            System.out.println("当前线程2：" + Thread.currentThread().getName());
            final Socket socket = serverSocket.accept();
            System.out.println("有一个客户端连接进来了");

            threadPool.execute(
                () -> handler(socket)
            );
        }


    }


    public static void handler(Socket socket) {
        byte[] bytes = new byte[1024];
        // 通过socket 获取输入流
        try {
            InputStream in = socket.getInputStream();
            // 循环读取数据
            while (true) {
                System.out.println("read阻塞在此 -----");
                System.out.println("handler当前线程：" + Thread.currentThread().getName());
                /*
                 * 别忘了，read是将流中的数据读到bytes数组中，如果是输入流的write()方法，则是往流中写数据
                 */
                int read = in.read(bytes);
                System.out.println("read执行完毕");
                if (read != -1) {
                    System.out.println(new String(bytes, 0, read));
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("关闭和client的连接");
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
