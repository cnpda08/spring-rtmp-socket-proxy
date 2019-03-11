package com.sc.socket.rtmp.proxy.server;

import com.sc.socket.rtmp.proxy.service.RedisService;
import com.sc.socket.rtmp.proxy.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Simon Chu
 * *************************
 * Name：RtmpSocketServer
 * Package Name：
 * Project Name：
 * Date: 2019/3/8
 * Time: 22:44
 * Description: No Description
 * *************************
 */
@Component
public class RtmpSocketServer {

    private final Logger log = LoggerFactory.getLogger(RtmpSocketServer.class);

    private static final int MAX_CONNECTION = 1000;
    private static boolean DEBUG_MODE = false;
    private static Vector clients = new Vector();
    private static String connectType = "UNIX";

    public void startServer(int localPort, int remotePort, String remoteHost)
            throws IOException {
        ServerSocket server = new ServerSocket(localPort);
        log.info("**********************************************************************");
        log.info("RTMP Socket Proxy Server Start Done, Standby The Client Connecting......");
        log.info("Locel Server Port:" + localPort);
        log.info("Remote Server Port:" + remotePort);
        log.info("Remote Server Host:" + remoteHost);
        log.info("**********************************************************************");
        try {
            while (true) {
                Socket incoming = server.accept();
                try {
                    log.info("**********************************************************************");
                    log.info("客户端【" + incoming.getInetAddress().getHostAddress() + ":" + incoming.getPort() + "】请求连接");
                    log.info("当前已建立连接数：" + clients.size());
                    if (clients.size() >= MAX_CONNECTION) {
                        log.info("连接超过最大连接数，取消连接请求");
                        incoming.close();
                    } else {
                        log.info("正在连接重定向【" + remoteHost + ":" + remotePort + "】");
                        Socket outputing = new Socket(remoteHost, remotePort);
                        log.info("重定向成功,当前转发模式【" + connectType + "】");
                        if (connectType.equalsIgnoreCase("UNIX")) {
                            clients.addElement(new SocketMap(outputing, incoming, "SERVER"));
                            new SocketMap(incoming, outputing, "CLIENT");
                        }
                    }
                    log.info("**********************************************************************");
                } catch (IOException e) {
                    log.error("建立服务失败.", e);
                    try {
                        incoming.close();
                    } catch (IOException err) {
                        log.error("发生错误，关闭连接", err);
                    }
                }
            }
        } finally {
            server.close();
        }
    }

    class SocketMap extends Thread {

        private byte[] dataBuf = new byte[4096];
        private int dataLen;
        private Socket socket, target;
        private InputStream thisIn, targetIn;
        private OutputStream thisOut, targetOut;
        private String serverSymbol, clientSymbol;
        private boolean authLock;

        SocketMap(Socket source, Socket dest, String symbol) throws IOException {
            serverSymbol = symbol;
            socket = source;
            target = dest;
            thisIn = socket.getInputStream();
            thisOut = socket.getOutputStream();
            targetIn = target.getInputStream();
            targetOut = target.getOutputStream();
            authLock = false;
            if (symbol.equalsIgnoreCase("SERVER")) {
                clientSymbol = socket.getLocalAddress().getHostAddress() + ":" + socket.getPort();
            } else {
                clientSymbol = socket.getInetAddress().getHostAddress();
            }

            start();
        }

        public void run() {
            RedisService redisService = SpringContextUtil.getBeanByClass(RedisService.class);
            log.debug("**********************************************************************");
            log.debug(serverSymbol + "->【" + clientSymbol + "】开始一个新的线程服务");
            log.debug("**********************************************************************");
            try {
                while (true) {
                    dataLen = thisIn.read(dataBuf);
                    if (dataLen > 0) {
                        if (DEBUG_MODE) {
                            log.debug(serverSymbol + "缓冲区长度:" + dataLen);
                        }
                        targetOut.write(dataBuf, 0, dataLen);
                        targetOut.flush();
                    } else {
                        break;
                    }
                    try {
                        this.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("**********************************************************************");
                log.debug(serverSymbol + "->【" + clientSymbol + "】关闭连接");
                log.debug("**********************************************************************");
            } catch (IOException e) {
            } finally {
                try {
                    socket.close();
                    if (connectType.equalsIgnoreCase("UNIX") && serverSymbol.equalsIgnoreCase("SERVER")) {
                        clients.remove(this);
                    }
                } catch (IOException e) {
                    log.error("发生错误，关闭连接", e);
                }
            }
        }

    }

}
