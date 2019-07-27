package blog20190328;


import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * created by tianfeng on 2019/3/28
 */
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
        System.out.println("二进制：");
        for (int i=0;i<byteBuffer.capacity();i++){
            System.out.print(Integer.toBinaryString(byteBuffer.array()[i])+" ");
        }
        System.out.println();
    }

    public static void hexPrintln(ByteBuffer byteBuffer){
        System.out.println("十六进制：");
        for (int i = 0;i<byteBuffer.capacity();i++){
            System.out.print(Integer.toHexString(byteBuffer.array()[i])+" ");
        }
        System.out.println();
    }

    public static void Println(ByteBuffer byteBuffer){
        System.out.println("十进制：");
        for (int i=0;i<byteBuffer.capacity();i++){
            System.out.print(byteBuffer.array()[i]+" ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        String charset = "UTF-8";
        String person = "i am tian 峰";
        System.out.println("\n字符串："+person);

        encode(person,charset);
        System.out.println("\n编码为字节数组：");
        binaryPrintln(byteBuffer);
        Println(byteBuffer);
        hexPrintln(byteBuffer);

        System.out.println("\n解码为字符：");
        decode(byteBuffer,charset);
        System.out.println("charBuffer:"+charBuffer);
    }

}

