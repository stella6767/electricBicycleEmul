//package com.example.charge.config;
//
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.nio.channels.SocketChannel;
//
//
//@Slf4j
//public class DockingListner extends Thread {
//
//    @Autowired
//    private GlobalVar globalVar;
//
//    public ServerSocket serverSocket = null;
//    public Socket socket = null;
//
//    SocketChannel stationSchn = null;
//
//    public DockingListner(String name) {
//        this.setName(name);
//        try {
//            log.debug("ServerSocker create");
//            serverSocket = new ServerSocket(12222);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @SneakyThrows
//    public void run() {
//
//        log.debug(this.getName() + " 실행");
//        boolean isConnected = true;
//
//        while (isConnected) {
//
//            try {
//                socket = serverSocket.accept();
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                PrintWriter writer = new PrintWriter(socket.getOutputStream());
//                String strOut = "";
//
//                SocketChannel schn = globalVar.globalSocket.get("schn");
//
//                while ((strOut = reader.readLine()) != null) {
//                    System.out.println("클라이언트 메시지: " + strOut);
//                    writer.println(strOut);
//                    writer.flush();
//                }
//
//                log.debug("2");
//                reader.close();
//                writer.close();
//
//            } catch (Exception e) {
//                log.debug("error: " + e.getMessage());
//                socket.close();
//            }
//
//        } //while문 끝
//        log.debug("4");
//    }
//}
