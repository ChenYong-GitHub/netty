package com.alibaba_tencent.nio.base;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName: NIOFileChannel01
 * @Desc:  FileChannel写出到文件
 * @Author: ChenYong
 * @Date: 2019-12-19 20:23
 */
public class NIOFileChannelOutput {

    public static void main(String[] args) throws Exception {
        String str = "Hello baby";
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\file01.txt");
        FileChannel channel = fileOutputStream.getChannel();
        byte[] bytes = str.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length + 1);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        channel.write(byteBuffer);
        fileOutputStream.close();

    }
}
