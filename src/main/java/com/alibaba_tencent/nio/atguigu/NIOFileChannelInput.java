package com.alibaba_tencent.nio.atguigu;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName: NIOFileChannelOutput
 * @Desc: FileChannel读文件
 * @Author: ChenYong
 * @Date: 2019-12-19 20:43
 */
public class NIOFileChannelInput {

    public static void main(String[] args) throws Exception{
        File file = new File("d:\\file01.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel channel = fileInputStream.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate((int)file.length());
        // 将通道中的数据读入到Buffer中
        channel.read(byteBuffer);

        // 将byteBuffer中的 字节数据转成字符串
        System.out.println(new String(byteBuffer.array()));
        fileInputStream.close();
    }
}
