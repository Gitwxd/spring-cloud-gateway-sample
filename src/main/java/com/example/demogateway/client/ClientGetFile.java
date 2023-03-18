package com.example.demogateway.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.Socket;

/**
 * @author wangxiaodong <wangxiaodong05@kuaishou.com>
 * Created on 2023-03-17
 */
public class ClientGetFile extends Thread {
    private final Socket socket;

    public ClientGetFile(Socket socket) {
        this.socket = socket;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void run() {
        try {
            //1.定义 输入流 从网络介质中获取数据存入内存中
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            //2. 定义流将文件数据从内存中写入磁盘
            String FileName = dis.readUTF();
            String FilePath = "/Users/wangxiaodong/Desktop/receive" + FileName;

            System.out.println("正在保存:" + FileName);
            DataOutputStream outputToDisk = new DataOutputStream(new FileOutputStream(FilePath));
            byte[] buffer = new byte[63326];
            long FileLength = dis.readLong();
            int length;
            int OKLength = 0;

            while ((length = dis.read(buffer)) != -1) {
                OKLength += length;
                outputToDisk.write(buffer, 0, length);
                outputToDisk.flush();
                if (FileLength == OKLength) {
                    break;
                }
            }
            System.out.println("接收" + FileName + "成功!");
            System.out.println("路径为:" + FilePath);
            outputToDisk.close();       //关闭 内存->磁盘 的io资源
        } catch (Exception e) {
            System.out.println("您已离线!!!");
            e.printStackTrace();
        }

    }
}
