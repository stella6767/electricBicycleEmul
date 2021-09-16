package com.example.charge.service;

import com.example.charge.config.GlobalVar;
import com.example.charge.utills.Common;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

@Slf4j
@RequiredArgsConstructor
@Service
public class SocketService {

    private final GlobalVar globalVar;

    JSONParser parser = new JSONParser();
    JSONObject obj;


    @Async
    public void test(){
        log.debug("이건 실행되나");
    }


    //@Async
    public SocketChannel socketClient() throws IOException {

        log.debug("socketClient create");
        SocketChannel schn = null;
        schn = SocketChannel.open();
        try {
            schn.connect(new InetSocketAddress(globalVar.ip, globalVar.socketPort));
            schn.configureBlocking(true);// Non-Blocking I/O
            log.debug("socketChannel connected to port 5053");

        } catch (Exception e2) {
            log.debug("connected refused!!!");
        }
        return schn; // 다른 대안 탐색중..

    }

    //@Async
    public void readSocketData() throws IOException {

        //SocketChannel schn = globalVar.globalSocket.get("schn");

        SocketChannel schn = socketClient();


        boolean isRunning = true; // 일단 추가, socketWork 중지할지 안 중지할지

        while (isRunning && schn.isConnected()) {

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

                            globalVar.globalSocket.put("schn", schn);
                            clsfyReq(result);
                            result = "";
                            bEtxEnd = false;
                            readBuf.clear();

                        }

                    } // #ETX# 단위로 루프
                } // byteCount > 0

                log.debug("소켓 닫기");
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
        String mobilityid = String.valueOf(obj.get("mobilityid"));

        String rentalResponse = "대여 요청되었습니다. chargerid: " + chargerid +" 의  mobilityid: " +mobilityid +" 가 unLock";
        writeSocket(rentalResponse);


    }



    public void writeSocket(String response){

        log.debug("station에게 buffer로 응답합니다..");
        log.debug(response);

        ByteBuffer writBuf = ByteBuffer.allocate(10240);

        SocketChannel schn = globalVar.globalSocket.get("schn");

        writBuf.flip();
        writBuf = Common.str_to_bb(response);
        try {
            schn.write(writBuf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        writBuf.clear();
    }


    public void returnResp(){



    }

}
