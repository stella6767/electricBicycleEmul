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

    @Async
    public void socketClient() throws IOException {

        SocketChannel schn = null;

        schn = SocketChannel.open();
        try {
            schn.connect(new InetSocketAddress(globalVar.ip, 5051));
            schn.configureBlocking(true);// Non-Blocking I/O
            log.info("socketChannel connected to port 5051");
            globalVar.globalSocket.put("schnClient", schn);
            readSocketData(schn);

        } catch (Exception e2) {
            log.debug("connected refused!!!");
        }

    }


    public void readSocketData(SocketChannel schn) throws IOException {
        ByteBuffer readBuf = ByteBuffer.allocate(10240);

        String response = "";

        if (schn.isConnected()) {
            log.info("Socket channel이 정상적으로 연결되었고 data를 읽습니다.");

            int bytesRead = schn.read(readBuf); // read into buffer. 일단은 버퍼 초과 신경쓰지 않고
            while (bytesRead != -1) {// 만약 소켓채널을 통해 buffer에 데이터를 받아왔으면

                readBuf.flip(); // make buffer ready for read

                while (readBuf.hasRemaining()) {
                    //System.out.print((char) readBuf.get()); // read 1 byte at a time
                    response = response + String.valueOf(((char) readBuf.get()));
                }

                readBuf.clear(); //make buffer ready for writing
                log.info("response: " + response);
                try {
                    clsfyReq(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                bytesRead = schn.read(readBuf); //여기서 읽을 때까지 블락킹이 걸리므로 while문 못 빠져나가는 건 당연하다.

            }
        }

    }



    public void clsfyReq(String response)   {

        log.info("분류" + response);



        try {
            obj = (JSONObject) parser.parse(response);
            String opCode = String.valueOf(obj.get("opcode"));

            switch (opCode){

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


        log.info("대여 요청되었습니다: " + chargerid);

    }


}
