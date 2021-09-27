package com.example.charge.service;

import com.example.charge.config.GlobalVar;
import com.example.charge.dto.CMRespDto;
import com.example.charge.dto.Opcode;
import com.example.charge.dto.RespData;
import com.example.charge.utills.Common;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class SocketService {

    private final GlobalVar globalVar;


    JSONParser parser = new JSONParser();
    JSONObject obj;


    //@Async
    public SocketChannel socketClient() throws IOException { //여기서 어떻게 chargerId 별로 connect시키지..

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


    @Async
    public void readSocketData() throws IOException {

        //SocketChannel schn = globalVar.globalSocket.get("schn");

        SocketChannel schn = socketClient();
        globalVar.globalSocket.put("schn", schn);
        log.debug("socket 담김: " +  schn);

        CMRespDto initResp = new CMRespDto<>(Opcode.INIT, globalVar.chargerid);

        writeSocket(initResp); //최초 자기 chargeId 전송

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

 //                           globalVar.globalSocket.put("schn", schn);
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


    public void clsfyReq(String response) { //여기서 도킹여부, 대여, 반납 요청 다 분류

        log.debug("분류: " + response);

        try {
            obj = (JSONObject) parser.parse(response);
            String opcode = String.valueOf(obj.get("opcode"));
            Opcode opCode = Opcode.valueOf(opcode); //enum으로 변환
            log.info("Opcode: " + opCode);

            switch (opCode) {

                case RENTAL:
                    rentalResp(response);
                    break;

                case DOCKING:
                    dockingResp(response);
                    break;

                case RETURN:
                    returnResp(response);
                    break;

            }


        } catch (ParseException | JsonProcessingException e) {
            e.printStackTrace();
        }

    }


    public void dockingResp(String response) throws JsonProcessingException, ParseException {

        HashMap<String, CMRespDto> hashMap = globalVar.objectMapper.readValue(response, HashMap.class);
        log.info("도킹 여부:  " + hashMap.get("data"));
        String parseData = globalVar.objectMapper.writeValueAsString(hashMap.get("data"));
        log.info("도킹여부 파싱2: " + parseData);
        obj = (JSONObject) parser.parse(parseData);
        String result_message = String.valueOf(obj.get("result_message"));


        if(result_message.equals("도킹 해제 요청이 완료되었습니다.")){
            log.debug("충전기의 모빌리티 ID를 0으로 초기회");
            globalVar.mobilityId = 0;

        }else{
            log.debug("docking ok");
        }

    }


    public void returnResp(String response) throws ParseException, JsonProcessingException {

        HashMap<String, CMRespDto> hashMap = globalVar.objectMapper.readValue(response, HashMap.class);

        log.info("반납요청 파싱:  " + hashMap.get("data"));

        //String parseData = String.valueOf(hashMap.get("data"));

        String parseData = globalVar.objectMapper.writeValueAsString(hashMap.get("data"));
        log.info("반납요청 파싱2: " + parseData);

        obj = (JSONObject) parser.parse(parseData);
        String stationid = String.valueOf(obj.get("stationid"));
        String mobilityid = String.valueOf(obj.get("mobilityid"));
        String chargerid = String.valueOf(obj.get("chargerid"));


        RespData data = RespData.builder()
                .result_code(0)  //요것도 나중에 enum
                .result_message("반납 요청되었습니다.")
                //.stationid(Integer.parseInt(stationid))
                .chargerid(Integer.valueOf(globalVar.chargerid))
                //.mobilityid(globalVar.mobilityId)
                .build();


        log.info("반납요청 파싱3: " + data);

        CMRespDto parsingCmRespDto = new CMRespDto(Opcode.RETURN, data);

        log.info("충전기 Lock");

        writeSocket(parsingCmRespDto);

    }


    public void rentalResp(String response) throws ParseException, JsonProcessingException {


        HashMap<String, CMRespDto> hashMap = globalVar.objectMapper.readValue(response, HashMap.class);
        log.info("대여요청 파싱:  " + hashMap.get("data"));
        //String parseData = String.valueOf(hashMap.get("data"));
        String parseData = globalVar.objectMapper.writeValueAsString(hashMap.get("data"));
        log.info("대여요청 파싱2: " + parseData);

        obj = (JSONObject) parser.parse(parseData);
        String stationid = String.valueOf(obj.get("stationid"));
        String mobilityid = String.valueOf(obj.get("mobilityid"));
        String chargerid = String.valueOf(obj.get("chargerid"));


        RespData data = RespData.builder()
                .result_code(0)  //요것도 나중에 enum
                .result_message("대여 요청되었습니다.")
                .stationid(Integer.parseInt(stationid))
                .chargerid(Integer.parseInt(chargerid))
                .mobilityid(Integer.parseInt(mobilityid))
                .build();

        log.info("대여요청 파싱3: " + data);

        CMRespDto parsingCmRespDto = new CMRespDto(Opcode.RENTAL, data);

        log.info("충전기 UnLock");

        writeSocket(parsingCmRespDto);

    }

//    @Scheduled(fixedDelay = 1000) //????안 먹힘...
//    //@Async
//    public void scheuledUpdate() throws JsonProcessingException {
//
//        RespData data = RespData.builder()
//                .stationId(1)
//                .chargerId(Integer.parseInt(Opcode.INIT.getCode()))
//                .mobilityId(globalVar.mobilityId)
//                .slotno(1)
//                .battery(55)
//                .build();
//
//        CMRespDto cmRespDto = new CMRespDto(Opcode.UPDATE, data);
//
//        log.info("1분마다 station 서버로 자기 정보 전송 " + cmRespDto);
//
//        writeSocket(cmRespDto);
//    }

    public void writeSocket(CMRespDto cmRespDto) throws JsonProcessingException {

        log.debug("station에게 buffer로 응답합니다..");
        globalVar.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String response = globalVar.objectMapper.writeValueAsString(cmRespDto);
        log.debug(response);

        //globalVar.globalReqData.put(Opcode.INIT.getCode(), response); //도킹 시 가져갈 statoin, mobilityId, 기타 등등 정보들..

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




}
