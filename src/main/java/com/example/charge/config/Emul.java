package com.example.charge.config;

import com.example.charge.service.DockingService;
import com.example.charge.service.SocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class Emul {

    private final SocketService socketService;
    private final DockingService dockingService;
    //private final GlobalVar globalVar;

    @PostConstruct
    public void emulStart() throws IOException, ExecutionException, InterruptedException {
        CompletableFuture<SocketChannel> completableFuture = socketService.socketClient();
        SocketChannel schn = completableFuture.get();//블록킹
        socketService.readSocketData(schn);
        dockingService.dockingListen();
    }





}
