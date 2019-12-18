package com.alibaba_tencent.bio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @ClassName: NioServerWork
 * @Desc:
 * @Author: ChenYong
 * @Date: 2019-12-18 15:49
 */
public class NioServerWork implements Runnable {

    // 多路复用器 Selector会对注册在其上面的channel进行；轮询，当某个channel发生读写操作时，
    // 就会处于相应的就绪状态，通过SelectionKey的值急性IO 操作
    private Selector selector;
    private ServerSocketChannel channel;
    private volatile boolean stop;

    public NioServerWork(int port) {
        try {
            selector = Selector.open(); // 打开多路复用器
            channel = ServerSocketChannel.open();
            channel.configureBlocking(false); // 配置通道为非阻塞的状态
            channel.socket().bind(new InetSocketAddress(port), 1024); // 通道socket绑定地址和端口
            channel.register(selector, SelectionKey.OP_ACCEPT); // 将通道channel在多路复用器selector上注册为接收操作
            System.out.println("NIO 服务启动，端口：" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        System.out.println("NIO 服务 run()");
        while (!stop) {
            try {
                selector.select(1000); // 设置最大阻塞时间为1s
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        handleinput(key); // 根据SelectionKey的值进行相应的读写操作
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * @param key
     * @throws IOException 根据SelectionKey的值进行相应的读写操作
     */
    public void handleinput(SelectionKey key) throws IOException {
        if (key.isValid()) { // 判断所传的SelectionKey值是否可用
            if (key.isAcceptable()) { // 在构造函数中注册的key值为OP_ACCEPT,在判断是否为接收操作
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel(); // 获取key值所对应的channel
                SocketChannel accept = ssc.accept();
                accept.configureBlocking(false); // 设置为接收非阻塞通道
                accept.register(selector, SelectionKey.OP_ACCEPT);
            }
            if (key.isReadable()) { // 判断所传的SelectionKey值是否为OP_READ,通过上面的注册后，经过轮询后就会是此操作
                SocketChannel sc = (SocketChannel) key.channel(); // 获取key对应的channel
                ByteBuffer readBuf = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuf); // 从channel中读取byte数据并存放readbuf
                if (readBytes > 0) {
                    readBuf.flip(); //检测时候为完整的内容,若不是则返回完整的
                    byte[] bytes = new byte[readBuf.remaining()];
                    readBuf.get(bytes);
                    String string = new String(bytes, "UTF-8"); // 把读取的数据转换成string
                    System.out.println("服务器接受到命令 :" + string);
                    //"查询时间"就是读取的命令，此字符串要与客户端发送的一致，才能获取当前时间，否则就是bad order
                    String currenttime = "查询时间".equalsIgnoreCase(string) ? new java.util.Date(System.currentTimeMillis()).toString() : "bad order";
                } else if (readBytes < 0) {
                    key.cancel();
                    sc.close();
                } else {

                }
            }
        }
    }

    /**
     * @param sc
     * @param currenttime
     * @throws IOException 服务器的业务操作，将当前时间写到通道内
     */
    private void dowrite(SocketChannel sc, String currenttime) throws IOException {
        System.out.println("服务器 dowrite  currenttime" + currenttime);
        if (currenttime != null && currenttime.trim().length() > 0) {
            byte[] bytes = currenttime.getBytes();//将当前时间序列化
            ByteBuffer writebuf = ByteBuffer.allocate(bytes.length);
            writebuf.put(bytes);//将序列化的内容写入分配的内存
            writebuf.flip();
            sc.write(writebuf);    //将此内容写入通道
        }
    }

}
