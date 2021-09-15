package com.example.charge.config;

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

    @PostConstruct
    public void emulStart() throws IOException, ExecutionException, InterruptedException {

        //socketService.socketServer();
        CompletableFuture<SocketChannel> completableFuture = socketService.socketClient();
        SocketChannel socketClient = completableFuture.get(); //일단은 그냥 blocking 시켜서 보내자
        socketService.readSocketData2(socketClient);
    }


}
