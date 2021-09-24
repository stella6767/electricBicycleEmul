package com.example.charge.config;

import com.example.charge.service.DockingService;
import com.example.charge.service.ScheduledService;
import com.example.charge.service.SocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Component
public class Emul {

    private final SocketService socketService;
    private final DockingService dockingService;
    //private final GlobalVar globalVar;
    private final ScheduledService scheduledService;


    @PostConstruct
    public void emulStart() throws IOException, ExecutionException, InterruptedException {

        //socketService.socketClient();
        //CompletableFuture<SocketChannel> completableFuture = socketService.socketClient();
        //SocketChannel schn = completableFuture.get();//블록킹
        //https://brunch.co.kr/@springboot/401
        dockingService.dockingListen();
        scheduledService.scheuledUpdate();
        socketService.readSocketData(); //요거 순서를 바꿔주면 되네.
    }


//    @Scheduled(fixedDelay = 1000) //??????????????????
//    public void scheuledUpdate() throws JsonProcessingException {
//        socketService.scheuledUpdate();
//
//    }


}
