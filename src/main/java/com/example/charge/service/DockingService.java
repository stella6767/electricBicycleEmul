package com.example.charge.service;

import com.example.charge.config.GlobalVar;
import com.example.charge.utills.Common;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

@Slf4j
@Service
public class DockingService {

    private GlobalVar globalVar;
    public ServerSocket serverSocket = null;
    public Socket socket = null;

    @SneakyThrows
    public DockingService(GlobalVar globalVar) {
        log.debug("docking server socket create");
        this.globalVar = globalVar;
        this.serverSocket = new ServerSocket(12222);
    }


    @SneakyThrows
    @Async
    public void dockingListen()  { //SocketChannel stationSchn

        log.debug("docking listen");
        boolean isConnected = true;

        while (isConnected) {

            try {
                socket = serverSocket.accept();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                String strOut = "";

                //SocketChannel schn = globalVar.globalSocket.get("schn");

                while ((strOut = reader.readLine()) != null) {
                    System.out.println("클라이언트 메시지: " + strOut);
                    writer.println(strOut);
                    writer.flush();

                    //writeDockingOrNot(stationSchn, strOut);
                }

                log.debug("2");
                reader.close();
                writer.close();

            } catch (Exception e) {
                log.debug("error: " + e.getMessage());
                socket.close();
            }

        } //while문 끝
        log.debug("4");


    }


    //docking 여부 staion에게 답장
    public void writeDockingOrNot(SocketChannel schn, String response){

        log.debug("station에게 buffer로 응답합니다..");
        log.debug(response);
        ByteBuffer writBuf = ByteBuffer.allocate(10240);

        writBuf.flip();
        writBuf = Common.str_to_bb(response);
        try {
            schn.write(writBuf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        writBuf.clear();
    }


}
