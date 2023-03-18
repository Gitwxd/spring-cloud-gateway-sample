package com.example.demogateway.client;

import java.io.*;
import java.net.Socket;

//作为客户端发送文件的线程类
public class ClientSendFile extends Thread {
    private final Socket socket;

    public ClientSendFile(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //1.创建要发送的文件对象
            File file = new File("/Users/wangxiaodong/Code/os/commons-lang.zip");

            //2.通过套接字传文件给服务器,由服务器转发给别的客户
            sendFileToServer(socket, file);
            System.out.println(file.getName() + "发送完毕！！！");
            //while (true);
        } catch (Exception e) {
            System.out.println("您已离线!!");
            e.printStackTrace();
        }
    }

    //定义一个静态方法 作为专门发送文件
    private static void sendFileToServer(Socket socket, File file) throws Exception {
        //1.将文件对象输入到内存中来
        DataInputStream InputToRAM = new DataInputStream(new FileInputStream(file));

        //2.准备发送管道 发送到网络 给服务器接收
        DataOutputStream outputToNet = new DataOutputStream(socket.getOutputStream());

        //3.发送文件名、文件大小 给服务器
        outputToNet.writeUTF(file.getName());   //发送文件名给 服务器   file.getName(); 得到要发送的文件名
        outputToNet.flush();                    //刷新流
        outputToNet.writeLong(file.length());  // 发送文件大小给 服务器   file.length();得到要发送的文件大小 单位字节（1K=1024Byte）
        outputToNet.flush();

        // 4.发送文件内容 给服务器
        int length;
        byte[] buffer = new byte[1024];
        while ((length = InputToRAM.read(buffer)) != -1) {  //dis.read(buffer)  从磁盘中读取内容，存到buffer数组中（数组在内存中）
            outputToNet.write(buffer, 0, length);
            outputToNet.flush();
        }
    }
}