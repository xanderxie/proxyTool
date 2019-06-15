package tool.entertainment.proxy;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import tool.entertainment.proxy.runnable.ReadWriteRunnable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author : xiezhidong
 * @date :  2019/6/15  14:43
 */
public class Transform {
    /**
     * 当前服务器ServerSocket的最大连接数
     */
    private static final int MAX_CONNECTION_NUM = 50;

    private static boolean exit = false;

    private static ExecutorService executorService;


    public static void listen(String sourceIp, int sourcePort, String targetIp, int targetPort) {
        initThreadPool();
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(sourcePort, MAX_CONNECTION_NUM, InetAddress.getByName(sourceIp));

            while (!exit) {
                // 用户连接到当前服务器的socket
                Socket s = ss.accept();

                // 当前服务器连接到目的地服务器的socket。
                Socket client = new Socket(targetIp, targetPort);
                // 读取用户发来的流，然后转发到目的地服务器。
                executorService.execute(new ReadWriteRunnable(false, s, client));
///                new Thread(new ReadWriteRunnable(s, client)).start();

                // 读取目的地服务器的发过来的流，然后转发给用户。
                executorService.execute(new ReadWriteRunnable(true, client, s));
///                new Thread(new ReadWriteRunnable(client, s)).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != ss) {
                    ss.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void exit() {
        exit = true;
    }

    private static void initThreadPool() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("proxy-pool-%d").build();
        executorService = new ThreadPoolExecutor(2,
                4,
                200,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(100),
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }
}
