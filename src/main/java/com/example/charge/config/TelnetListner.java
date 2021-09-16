package com.example.charge.config;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class TelnetListner extends Thread {

    public ServerSocket serverSocket = null;
    public Socket socket = null;

    public TelnetListner(String name) {
        this.setName(name);
        try {
            serverSocket = new ServerSocket(12222);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        log.debug(this.getName() + " 실행");
        boolean isConnected = true;

        while (isConnected) {

            try {
                socket = serverSocket.accept();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                String strOut = "";

                while ((strOut = reader.readLine()) != null) {
                    System.out.println("클라이언트 메시지: " + strOut);
                }

                log.debug("2");
                reader.close();

            } catch (Exception e) {
                log.debug("error: " + e.getMessage());
//                log.debug("3");
//                try {
//                    socket.close();
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//                isConnected= false;
            }

        } //while문 끝
        log.debug("4");
    }
}
