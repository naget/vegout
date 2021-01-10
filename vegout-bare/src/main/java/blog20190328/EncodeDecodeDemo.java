package blog20190328;


import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * created by tianfeng on 2019/3/28
 */
@Slf4j
public class EncodeDecodeDemo {
    private static CharBuffer charBuffer;
    private static ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

    //编码
    public static void encode(Object o,String charset){
        byteBuffer=Charset.forName(charset).encode(o.toString());
    }

    //解码
    public static void decode(ByteBuffer byteBuffer,String charset){
        charBuffer = Charset.forName(charset).decode(byteBuffer);
    }

    public static void binaryPrintln(ByteBuffer byteBuffer){
        log.info("二进制：");
        for (int i=0;i<byteBuffer.capacity();i++){
            System.out.print(Integer.toBinaryString(byteBuffer.array()[i])+" ");
        }
        log.info("");
    }

    public static void hexPrintln(ByteBuffer byteBuffer){
        log.info("十六进制：");
        for (int i = 0;i<byteBuffer.capacity();i++){
            System.out.print(Integer.toHexString(byteBuffer.array()[i])+" ");
        }
        log.info("");
    }

    public static void Println(ByteBuffer byteBuffer){
        log.info("十进制：");
        for (int i=0;i<byteBuffer.capacity();i++){
            System.out.print(byteBuffer.array()[i]+" ");
        }
        log.info("");
    }

    public static void main(String[] args) {
        String charset = "UTF-8";
        String person = "i am tian 峰";
        log.info("\n字符串："+person);

        encode(person,charset);
        log.info("\n编码为字节数组：");
        binaryPrintln(byteBuffer);
        Println(byteBuffer);
        hexPrintln(byteBuffer);

        log.info("\n解码为字符：");
        decode(byteBuffer,charset);
        log.info("charBuffer:"+charBuffer);
    }

}

