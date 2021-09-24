package com.example.charge.service;

import com.example.charge.config.GlobalVar;
import com.example.charge.dto.CMRespDto;
import com.example.charge.dto.Opcode;
import com.example.charge.dto.RespData;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduledService {


    private final GlobalVar globalVar;
    private final SocketService socketService;

    //@Scheduled(fixedDelay = 1000) //????안 먹힘...
    @Async
    public void scheuledUpdate() throws JsonProcessingException {

        while (true){

            RespData data = RespData.builder()
                    .stationId(1)
                    .chargerId(Integer.parseInt(Opcode.INIT.getCode()))
                    .mobilityId(globalVar.mobilityId)
                    .slotno(1)
                    .battery(55)
                    .build();

            CMRespDto cmRespDto = new CMRespDto(Opcode.UPDATE, data);

            log.info("1분마다 station 서버로 자기 정보 전송 " + cmRespDto);

            socketService.writeSocket(cmRespDto);

            try {
                Thread.sleep(1000 * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}
