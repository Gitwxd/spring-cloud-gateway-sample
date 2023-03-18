package com.example.demogateway.client;

import java.net.Socket;

/**
 * @author wangxiaodong
 * Created on 2023-03-17
 */
public class Client {

    public static void main(String[] args) {
        try {
            System.out.println("=======客户端=======");
            //1.线连接上服务器的套接字 （套接字=ip：端口号）
            Socket socket = new Socket("127.0.0.1", 10001);

            //2. 开启一个线程对象 专们用来接收文件的线程
            //new ClientGetFile(socket).start();

            //3.开启发送文件的线程对象
            new ClientSendFile(socket).start();
            while (true) {
                ;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
