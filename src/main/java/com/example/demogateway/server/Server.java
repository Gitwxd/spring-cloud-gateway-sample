package com.example.demogateway.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangxiaodong
 * Created on 2023-03-17
 */
public class Server {

    /*创建一个Socket的list集合 用来装套接字  Socket=（IP地址：端口号）   */
    public static final List<Socket> onLineSockets = new ArrayList<>();  //当客户端连接上服务器的时候，就将客户端的套接字存入集合中

    public static void main(String[] args) {
        try {
            System.out.println("===服务端启动成功===");
            // 1、注册端口
            ServerSocket serverSocket = new ServerSocket(10001);
            // a.定义一个死循环由主线程负责不断的接收客户端的Socket管道连接。
            while (true) {
                // 2、每接收到一个客户端的Socket管道，交给一个独立的子线程负责读取消息
                Socket socket = serverSocket.accept();
                System.out.println(socket.getRemoteSocketAddress() + "上线了！");
                onLineSockets.add(socket);// 把当前客户端管道Socket加入到在线集合中去
                // 3、开始创建独立线程处理这个连接上来的客户
                new ServerFileThread(socket).start();
            }
        } catch (Exception e) {
            System.out.println("您已离线!");
            e.printStackTrace();
        }
    }
}
