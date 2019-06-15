package tool.entertainment.proxy.runnable;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author : xiezhidong
 * @date :  2019/6/15  14:44
 */
public class ReadWriteRunnable implements Runnable {

    private boolean writeBack;
    /**
     * 读入流的数据的套接字。
     */
    private Socket readSocket;

    /**
     * 输出数据的套接字。
     */
    private Socket writeSocket;

    /**
     * 两个套接字参数分别用来读数据和写数据。这个方法仅仅保存套接字的引用，
     * 在运行线程的时候会用到。
     *
     * @param writeBack 是否回写线程
     * @param readSocket  读取数据的套接字。
     * @param writeSocket 输出数据的套接字。
     */
    public ReadWriteRunnable(boolean writeBack, Socket readSocket, Socket writeSocket) {
        this.writeBack = writeBack;
        this.readSocket = readSocket;
        this.writeSocket = writeSocket;
    }




    @Override
    public void run() {
        System.out.println("readSocket:" + readSocket.getInetAddress() + ":" + readSocket.getPort());
        System.out.println("writeSocket:" + writeSocket.getInetAddress() + ":" + writeSocket.getPort());

        int length = 1024;
        byte[] b = new byte[length];
        InputStream is = null;
        OutputStream os = null;
        try {
            is = readSocket.getInputStream();
            os = writeSocket.getOutputStream();
            while (!readSocket.isClosed() && !writeSocket.isClosed()) {
                int size = is.read(b);
                if (size > -1) {
                    os.write(b, 0, size);
                }
                if(size < length){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(writeBack){
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (null != os) {
                        os.flush();
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
