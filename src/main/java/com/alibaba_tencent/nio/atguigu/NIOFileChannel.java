package com.alibaba_tencent.nio.atguigu;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName: NIOFileChannel
 * @Desc:
 * @Author: ChenYong
 * @Date: 2019-12-20 10:52
 */
public class NIOFileChannel {

    public static void main(String[] args) throws Exception{
        copy1();
        copy2();
    }


    /**
     * @Desc: 文件拷贝1
     * @Param:
     * @Return:
     * @Author: ChenYong
     * @Date: 2019-12-20 11:23
     */
    public static void copy1() throws Exception{

        FileInputStream fileInputStream = new FileInputStream("1.txt");
        FileChannel inputStreamChannel = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream("3.txt");
        FileChannel outputStreamChannel = fileOutputStream.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(2);
        // 因为不确定buffer的大小是否能够一次容纳文件的所有内容，所以需要循环去读写
        while (true) {
            buffer.clear();
            /*
            注意：这里有一个clear操作，入学
            public final Buffer clear() {
                position = 0;
                limit = capacity;
                mark = -1;
                return this;
            }
            */
            int a = inputStreamChannel.read(buffer);
            if (a == -1) {
                break;
            }
            buffer.flip();
            outputStreamChannel.write(buffer);
            // buffer.flip();
        }
        fileInputStream.close();
        fileOutputStream.close();

    }


    /**
     * @Desc:文件拷贝2
     * @Param:
     * @Return:
     * @Author: ChenYong
     * @Date: 2019-12-20 11:24
     */
    public static void copy2() throws  Exception{

        FileInputStream fileInputStream = new FileInputStream("1.txt");
        FileChannel sourceCh = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream("2.txt");
        FileChannel destCh = fileOutputStream.getChannel();

        destCh.transferFrom(sourceCh, 0, sourceCh.size());

        sourceCh.close();
        destCh.close();
        fileInputStream.close();
        fileOutputStream.close();
    }
}
