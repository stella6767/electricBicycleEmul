package com.example.charge.service;

import com.example.charge.config.GlobalVar;
import com.example.charge.dto.CMRespDto;
import com.example.charge.dto.Opcode;
import com.example.charge.dto.RespData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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


    @SneakyThrows
    public DockingService(GlobalVar globalVar) {
        log.debug("docking server socket create");
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


                while ((strOut = reader.readLine()) != null) {
                    System.out.println("클라이언트 메시지: " + strOut);
                    writer.println(strOut);
                    writer.flush();

                    //여기가 문제네.. docking 시에 아래와 같은 정보를 다 보내야겠다.
                    Integer docked = strOut.equals("1") ? 1 : 2;
                    //Integer mobilityId = docked == 1 ? 15 : 0;

                    log.debug("docked: " + docked ); //+ ", mobilityId: " + mobilityId

                    RespData respData = RespData.builder()
                            .stationId(1) //이미 알고있다고 가정
                            .chargerId(Integer.parseInt(Opcode.INIT.getCode()))
                            .docked(docked)
                            .slotno(1) //이것도 임의로
                            .mobilityId(15)
                            .build();

                    CMRespDto cmRespDto = new CMRespDto<>(Opcode.DOCKING, respData);
                    //CMRespDto cmRespDto = new CMRespDto<>(Opcode.DOCKING, Opcode.INIT.getCode());
                    socketService.writeSocket(cmRespDto);
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



}
