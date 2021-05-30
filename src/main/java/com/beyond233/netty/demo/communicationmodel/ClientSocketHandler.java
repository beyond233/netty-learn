package com.beyond233.netty.demo.communicationmodel;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

/**
 * description: 负责客户端连接上服务端后的处理 : 读取客户端发来的数据并给出响应
 *
 * @author beyond233
 * @since 2021/5/30 11:06
 */
@Slf4j
public class ClientSocketHandler implements Runnable {
    private Socket socket;

    public ClientSocketHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            // 1.获取这个客户端socket的输入输出流
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream());
            String currentTime = null;
            String body = null;

            while (true) {
                // 2.读取客户端发来的数据
                body = in.readLine();
                if (body == null) {
                    break;
                }
                log.info("服务器接收到消息: " + body);
                // 3.向客户端响应数据
                currentTime = "query time order".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "bad order";
                out.println(currentTime);
                out.flush();
                log.info("向客户端发送消息: " + currentTime);
            }

        } catch (IOException e) {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (out != null) {
                out.close();
            }

            try {
                if (this.socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            this.socket = null;
        }
    }
}
