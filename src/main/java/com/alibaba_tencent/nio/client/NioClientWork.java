package com.alibaba_tencent.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @ClassName: NioClientWork
 * @Desc:
 * @Author: ChenYong
 * @Date: 2019-12-18 16:10
 */
public class NioClientWork implements Runnable {


    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;


    /**
     * @param string
     * @param port   构造函数
     */
    public NioClientWork(String string, int port) {
        this.host = string == null ? "127.0.0.1" : string;
        this.port = port;
        try {
            selector = Selector.open();//打开多路复用器
            socketChannel = SocketChannel.open();//打开 socketChannel
            socketChannel.configureBlocking(false);
            System.out.println("NIO 客户端启动 端口： " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            doConnect();//客户端线程需要连接服务器
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        while (!stop) {

            try {
                selector.select(1000);//最大阻塞时间1s
                //获取多路复用器的事件值SelectionKey，并存放在迭代器中
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                SelectionKey key = null;
                while (iterator.hasNext()) {
                    key = iterator.next();
                    iterator.remove();
                    try {
                        handleinput(key);//获取多路复用器的事件值SelectionKey，并存放在迭代器中
                    } catch (Exception e) {
                        if (key == null) {
                            key.cancel();
                            if (socketChannel == null)
                                socketChannel.close();
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        if (selector != null) {
            try {
                selector.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @throws IOException 线程连接服务器，并注册操作
     */
    private void doConnect() throws IOException {
        if (socketChannel.connect(new InetSocketAddress(host, port))) {//检测通道是否连接到服务器
            System.out.println("NIO 客户端 idoConnect OP_READ ");
            socketChannel.register(selector, SelectionKey.OP_READ);//如果已经连接到了服务器，就把通道在selector注册为OP_READ
            dowrite(socketChannel);
        } else {
            System.out.println("NIO 客户端 doConnect OP_CONNECT ");
            socketChannel.register(selector, SelectionKey.OP_CONNECT);//如果客户端未连接到服务器，则将通道注册为OP_CONNECT操作
        }
    }

    /**
     * @param key
     * @throws IOException 根据SelectionKey的值进行相应的读写操作
     */
    private void handleinput(SelectionKey key) throws IOException {
        //System.out.println("NIO 客户端 handleinput ");
        if (key.isValid()) {//判断所传的SelectionKey值是否可用
            //System.out.println("NIO 客户端 isValid() ");
            SocketChannel sc = (SocketChannel) key.channel();
            if (key.isConnectable()) {//一开始的时候，客户端需要连接服务器操作，所以检测是否为连接状态
                System.out.println("NIO 客户端 isConnectable ");
                if (sc.finishConnect()) {//是否完成连接
                    System.out.println("NIO 客户端 finishConnect ");
                    dowrite(sc);//向通道内发送数据，就是“查询时间” 的命令,读写通道与通道注册事件类型无关，注册事件只是当有事件来了，就会去处理相应事件
                    sc.register(selector, SelectionKey.OP_READ);//如果完成了连接，就把通道注册为 OP_READ操作,用于接收服务器出过来的数据
                } else {
                    System.out.println("NIO 客户端 not finishConnect ");
                    System.exit(1);
                }
            }
            if (key.isReadable()) {//根据上面注册的selector的通道读事件，进行操作
                System.out.println("NIO 客户端 isReadable() ");
                ByteBuffer readbuf = ByteBuffer.allocate(1024);
                int readbytes = sc.read(readbuf);//获取通道从服务器发过来的数据，并反序列化
                if (readbytes > 0) {
                    readbuf.flip();
                    byte[] bytes = new byte[readbuf.remaining()];
                    readbuf.get(bytes);
                    String string = new String(bytes, "UTF-8");
                    System.out.println("时间是: " + string);
                    this.stop = true;    //操作完毕后，关闭所有的操作
                } else if (readbytes < 0) {
                    key.cancel();
                    sc.close();

                } else {
                }
            }
        }
    }

    private void dowrite(SocketChannel sc) throws IOException {
        byte[] req = "查询时间".getBytes();
        ByteBuffer writebuf = ByteBuffer.allocate(req.length);
        writebuf.put(req);
        writebuf.flip();
        sc.write(writebuf);
        if (!writebuf.hasRemaining()) {
            System.out.println("向服务器发送命令成功 ");
        }
    }
}
