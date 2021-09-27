package com.example.charge.service;

import com.example.charge.config.GlobalVar;
import com.example.charge.dto.CMRespDto;
import com.example.charge.dto.Opcode;
import com.example.charge.dto.RespData;
import com.example.charge.utills.Common;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@Service
public class DockingService {


    @Autowired
    private SocketService socketService; //무슨 이유에서진 RequiredArgsConstroturor가 안 먹힘..

    public ServerSocket serverSocket = null;
    public Socket socket = null;
    JSONParser parser = new JSONParser();
    JSONObject obj;

    private GlobalVar globalVar;


    @SneakyThrows
    public DockingService(GlobalVar globalVar) {
        log.debug("docking server socket create");
        this.serverSocket = new ServerSocket(Common.emulPort);
        this.globalVar = globalVar;
    }


    @SneakyThrows
    @Async
    public void dockingListen() { //SocketChannel stationSchn

        log.debug("docking listen");

        boolean isConnected = true;


        while (isConnected) {

            try {
                socket = serverSocket.accept();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                String strOut = "";


                while ((strOut = reader.readLine()) != null) {

                    strOut = strOut.trim();
                    System.out.println("클라이언트 메시지: " + strOut);
                    //System.out.println("glovar inject Test: " + globalVar.ip);

                    //docking 시에 json으로 docked, mobilityid 전송
                    //ClientReqDto clientReqDto = globalVar.objectMapper.readValue(strOut, ClientReqDto.class);
                    obj = (JSONObject) parser.parse(strOut);


                    Integer docked = Integer.parseInt(String.valueOf(obj.get("docked")));
                    globalVar.mobilityId = Integer.parseInt(String.valueOf(obj.get("mobilityid")));

                    //Integer docked = strOut.equals("1") ? 1 : 2;
                    //Integer mobilityId = docked == 1 ? 15 : 0;
                    //this.mobilityId = 1;

                    log.debug("docked: " + docked + ", mobilityId: " + globalVar.mobilityId); //

                    RespData respData = RespData.builder()
                            .stationid(1) //이미 알고있다고 가정
                            .chargerid(Integer.parseInt(Opcode.INIT.getCode()))
                            .docked(docked)
                            .slotno(1) //이것도 임의로
                            .mobilityid(globalVar.mobilityId)
                            .build();

                    CMRespDto cmRespDto = new CMRespDto<>(Opcode.DOCKING, respData);
                    //CMRespDto cmRespDto = new CMRespDto<>(Opcode.DOCKING, Opcode.INIT.getCode());
                    socketService.writeSocket(cmRespDto);

                    writer.println(strOut);
                    writer.flush();
                }

                log.debug("2");
                reader.close();
                writer.close();

            } catch (Exception e) {
                log.debug("error: " + e.getMessage());
                //충전기에 Data를 정상적으로 요청하지 않았습니다..
                socket.close();
            }

        } //while문 끝
        log.debug("4");


    }


}
