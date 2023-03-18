package com.example.demogateway.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author wangxiaodong <wangxiaodong05@kuaishou.com>
 * Created on 2023-03-17
 */
public class ServerFileThread extends Thread {

    private final Socket socket;                      //用来存套接字

    public ServerFileThread(Socket socket) {   //构造方法，用来接收  与服务器连接的管道对管道进行读操作
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //1.得到客户的通信管道
            DataInputStream InputToRAM = new DataInputStream(socket.getInputStream());

            //2.准备输出流  一个输出到服务器的本地磁盘下，一个输出到网络介质上，让其他客户接收
            DataOutputStream outputToDisk;
            DataOutputStream outputToNet = new DataOutputStream(socket.getOutputStream());

            //3. 接收和转发文件    （接收是接收到服务器的磁盘下，转发是转发到 网络介质上给别的客户）
            while (true) {
                // 获取文件名字和文件长度
                String FileName = InputToRAM.readUTF();
                String FilePaht = "/Users/wangxiaodong/Desktop/serverRece/" + FileName;
                System.out.println("正在接收:" + FileName);
                outputToDisk = new DataOutputStream(new FileOutputStream(FilePaht));
                long FileLength = InputToRAM.readLong();

                // 发送文件名字和文件长度给所有客户端
                for (Socket onLineSocket : Server.onLineSockets) { //onLineSockets存的是当前连接的客户
                    if (onLineSocket != socket) {       // 发送给其它客户端
                        outputToNet.writeUTF(FileName);
                        outputToNet.flush();
                        outputToNet.writeLong(FileLength);
                        outputToNet.flush();
                    }
                }

                //真正传送文件数据
                int length;
                long OKLength = 0;
                byte[] buffer = new byte[8192];
                while ((length = InputToRAM.read(buffer)) != -1) {
                    OKLength += length;        //记录已经传输的文件大小
                    //存到服务器的磁盘下
                    outputToDisk.write(buffer, 0, length);
                    outputToDisk.flush();
                    //转发数据到每个用户
                    for (Socket onLineSocket : Server.onLineSockets) {
                        if (onLineSocket != socket) {  // 发送给其它客户端,
                            outputToNet.write(buffer, 0, length);
                            outputToNet.flush();
                        }
                    }
                    if (OKLength == FileLength) {  // 强制退出
                        break;
                    }
                }
                System.out.println(FileName + "转发完毕!");
                System.out.println(FileName + "保存到服务器的路径为:" + FilePaht);
                outputToDisk.close();           //关闭 内存->磁盘 的io资源
            }
        } catch (IOException e) {
            Server.onLineSockets.remove(socket);
            System.out.println(socket.getRemoteSocketAddress() + "已下线~");     //该用户已经断开连接
            System.out.println("当前在线人数:" + Server.onLineSockets.size());
            // e.printStackTrace();
        }
    }

}
