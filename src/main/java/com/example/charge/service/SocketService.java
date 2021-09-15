package com.example.charge.service;

import com.example.charge.config.GlobalVar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

@Slf4j
@EnableAsync
@RequiredArgsConstructor
@Service
public class SocketService {

    private final GlobalVar globalVar;

    //    public SocketChannel schn = null;
//    StringBuffer sb = new StringBuffer();
//    Charset charset = Charset.forName("UTF-8");
    JSONParser parser = new JSONParser();
    JSONObject obj;
    SocketChannel schn = null;

    @Async
    public CompletableFuture<SocketChannel> socketClient() throws IOException {

        schn = SocketChannel.open();
        try {
            schn.connect(new InetSocketAddress(globalVar.ip, globalVar.socketPort));
            schn.configureBlocking(true);// Non-Blocking I/O
            log.debug("socketChannel connected to port 5051");
        } catch (Exception e2) {
            log.debug("connected refused!!!");
        }

        return CompletableFuture.completedFuture(schn);
    }


    @Async //비동기로 처리해야지만
    public void readSocketData2(SocketChannel schn) throws IOException {


        log.debug("들어옴? ");
        byte[] readByteArr;

        String response = "";

        boolean bConnect = true;
        String hl7Response = "";
        ByteBuffer readBuf = ByteBuffer.allocate(10240);

        if (schn.isConnected()) {
            log.debug("Socket channel이 정상적으로 연결되었습니다.");

            log.debug("1111");
            int bytesRead = schn.read(readBuf); // read into buffer. 일단은 버퍼 초과 신경쓰지 않고
            while (bytesRead != -1) {// 만약 소켓채널을 통해 buffer에 데이터를

                readBuf.flip(); // make buffer ready for read
                // 10240로 정의한 buffer의 크기를 실제 데이터의 크기로 flip() 함

                while (readBuf.hasRemaining()) {
                    // System.out.print((char) readBuf.get()); // read 1 byte at a time
                    hl7Response = hl7Response + String.valueOf(((char) readBuf.get()));
                }

//                    log.debug("??????????????: " + hl7Response);
//                    log.debug("limit까지의 값: " + readBuf.remaining());
//                    log.debug("capacity: " + readBuf.capacity());
//                    log.debug("position: " + readBuf.position());
                readBuf.clear(); //make buffer ready for writing

//                    readBuf.compact();
//                    log.debug("limit까지의 값 2: " + readBuf.remaining());
//                    log.debug("capacity 2: " + readBuf.capacity());
//                    log.debug("position 2: " + readBuf.position());
//
//                    log.debug("limit까지의 값 3: " + readBuf.remaining());
//                    log.debug("capacity 3: " + readBuf.capacity());
//                    log.debug("position 3: " + readBuf.position());
//                    if(readBuf.position() == 0){
//                        break;
//                    }

                bytesRead = schn.read(readBuf);

            }
            log.debug("-------------- 응답 hl7Response ----------------");
            log.debug(hl7Response);


        } else if (!schn.isConnected()) {
            log.debug("Socket channel이 연결이 끊어졌습니다.");
        }
    }


    public void readSocketData(SocketChannel schn) throws IOException {
        ByteBuffer readBuf = ByteBuffer.allocate(10240);

        String response = "";

        if (schn.isConnected()) {
            log.debug("Socket channel이 정상적으로 연결되었고 data를 읽습니다.");

            int bytesRead = schn.read(readBuf); // read into buffer. 일단은 버퍼 초과 신경쓰지 않고
            while (bytesRead != -1) {// 만약 소켓채널을 통해 buffer에 데이터를 받아왔으면

                readBuf.flip(); // make buffer ready for read
                log.debug("limit까지의 값: " + readBuf.remaining());
                log.debug("capacity: " + readBuf.capacity());
                log.debug("position: " + readBuf.position());


                while (readBuf.hasRemaining()) {
                    //System.out.print((char) readBuf.get()); // read 1 byte at a time
                    response = response + String.valueOf(((char) readBuf.get()));
                }

                readBuf.clear(); //make buffer ready for writing
                log.debug("response: " + response);
                try {
                    clsfyReq(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                bytesRead = schn.read(readBuf); //여기서 읽을 때까지 블락킹이 걸리므로 while문 못 빠져나가는 건 당연하다.

            }
        }

    }


    private void socketWork(SocketChannel schn) {

        boolean isRunning = true; // 일단 추가, socketWork 중지할지 안 중지할지

        while (isRunning) {

            try {
                long lThId = Thread.currentThread().getId();
                int byteCount = 0;
                byte[] readByteArr;

                // ByteBuffer readBuf = ByteBuffer.allocate(10); //버퍼 메모리 공간확보
                ByteBuffer readBuf = ByteBuffer.allocate(10240);

                log.debug("첫번째  while문");

                // 무한 루프
                String result = ""; // 요기서 초기화

                while (byteCount >= 0) {

                    try {

                        byteCount = schn.read(readBuf); // 소켓채널에서 한번에 초과되는 버퍼사이즈의 데이터가 들어오면..

                        log.debug("[gwEmulThread #100] TID[" + "] byteCount :  " + byteCount);
                        // logger.debug("isRunning why: " + isRunning);
                    } catch (Exception e) {
                        // e.printStackTrace();
                        log.debug("갑자기 클라이언트 소켓이 닫혔을 시");
                        schn.close();
                        isRunning = false;
                        break;
                    }

                    int i = 0;

                    // 버퍼에 값이 있다면 계속 버퍼에서 값을 읽어 result 를 완성한다.
                    while (byteCount > 0) {

                        readBuf.flip(); // 입력된 데이터를 읽기 위해 read-mode로 바꿈, positon이 데이터의 시작인 0으로 이동

                        readByteArr = new byte[readBuf.remaining()]; // 현재 위치에서 limit까지 읽어드릴 수 있는 데이터의 개수를 리턴

                        // 일단 확인
                        // logger.debug("limit까지의 값: " + readBuf.remaining());
                        // logger.debug("capacity: " + readBuf.capacity());
                        // logger.debug("position: " + readBuf.position());

                        readBuf.get(readByteArr); // 데이터 읽기

                        result = result + new String(readByteArr, Charset.forName("UTF-8"));

                        log.debug("[gwEmulThread #200] TID[ " + lThId + "] socketRead Start[" + result
                                + "], byteCount[" + byteCount + "], i[" + i + "]");
                        i++;

                        try {
                            byteCount = schn.read(readBuf);
                            log.debug("[gwEmulThread #210] TID[" + result + "] byteCount :  " + byteCount);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // break;
                        }

                        boolean bEtxEnd = true; // 아래 while문을 실행할지 안할지

                        // #ETX# 단위로 루프
                        while (!result.equals("") && bEtxEnd) {

                            clsfyReq(result);
                        }

                    } // #ETX# 단위로 루프
                } // byteCount > 0

                schn.close(); // 소켓 닫기

            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }


    public void clsfyReq(String response) {

        log.debug("분류" + response);


        try {
            obj = (JSONObject) parser.parse(response);
            String opCode = String.valueOf(obj.get("opcode"));

            switch (opCode) {

                case "rental":
                    rentalResp(response);
                    break;

                case "return":
                    break;

            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    public void rentalResp(String response) throws ParseException {

        obj = (JSONObject) parser.parse(response);
        String chargerid = String.valueOf(obj.get("chargerid"));


        log.debug("대여 요청되었습니다. UnLock: " + chargerid);

    }


}
