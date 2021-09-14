package com.example.charge.service;

import com.example.charge.config.GlobalVar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

@Slf4j
@EnableAsync
@RequiredArgsConstructor
@Service
public class SocketService {

    private final GlobalVar globalVar;

    public SocketChannel schn = null;
    StringBuffer sb = new StringBuffer();
    Charset charset = Charset.forName("UTF-8");

    @Async
    public void socketServer() {

        try {

            ServerSocketChannel serverSocketChannel = null; // ServerSocketChannel은 하나

            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(5051)); // socket().

            boolean bLoop = true;

            log.info("socketStart");

            while (bLoop) {

                try {
                    log.info("socket이 연결이 될 때까지 블록킹");
                    SocketChannel schn = null;

                    schn = serverSocketChannel.accept(); // 이 부분에서 연결이 될때까지 블로킹
                    schn.configureBlocking(true); // 블록킹 방식

                    log.info("socket connected 5051 port");
                    //globalVar.globalSocket.put("schnServer", schn);
                    readSocketData(schn);
                } catch (Exception e) {
                    //logger.debug("AsynchronousCloseException 터짐");
                    //socketChannel.close();

                    e.printStackTrace();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }


    public void readSocketData(SocketChannel schn) throws IOException {

        byte[] readByteArr;
        ByteBuffer readBuf = ByteBuffer.allocate(3);
        String response = "";
        //int bytesRead = 0;

        sb.delete(0, sb.length()); // 초기화

        if (schn.isConnected()) {
            log.info("Socket channel이 정상적으로 연결되었고 data를 읽습니다.");

            int bytesRead = schn.read(readBuf); // read into buffer. 일단은 버퍼 초과 신경쓰지 않고
            while (bytesRead != -1) {// 만약 소켓채널을 통해 buffer에 데이터를 받아왔으면

                readBuf.flip(); // make buffer ready for read
                // 10240로 정의한 buffer의 크기를 실제 데이터의 크기로 flip() 함

                while (readBuf.hasRemaining()) {
                    System.out.print((char) readBuf.get()); // read 1 byte at a time
                    //response = response + String.valueOf(((char) readBuf.get()));
                }

                //log.info("읽기 끝 " + bytesRead);
                // logger.debug("hl7Response data1: "+hl7Response);
                readBuf.clear(); //make buffer ready for writing
                bytesRead = schn.read(readBuf);

            }

            log.info("response: " + response);


//
//
//            try {
//                bytesRead = schn.read(readBuf); // read into buffer. 일단은 버퍼 초과 신경쓰지 않고
//                log.info("bytesRead1: " + bytesRead);
//            } catch (Exception e) {
//                schn.close();
//            }
//
//
//            while (bytesRead > 0) {// 만약 소켓채널을 통해 buffer에 데이터를 받아왔으면
//                readBuf.flip(); // make buffer ready for read
//                readByteArr = new byte[readBuf.remaining()];
//
//                log.info(String.valueOf(readBuf.remaining()));
//                log.info(String.valueOf(readBuf.capacity()));
//                log.info(String.valueOf(readBuf.position()));
//                readBuf.get(readByteArr);
//
//                response = response + new String(readByteArr, Charset.forName("UTF-8"));
//
//                try {
//                    bytesRead = schn.read(readBuf);
//                    readBuf.clear();
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//                }
//            }
//            log.info("response: " + response);
//
//        } else if (!schn.isConnected()) {
//            log.info("Socket channel이 연결이 끊어졌습니다.");
//        }

        }

    }




}
