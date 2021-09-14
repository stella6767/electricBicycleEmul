package com.example.charge.config;

import com.example.charge.service.SocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class Emul {

    private final SocketService socketService;

    @PostConstruct
    public void emulStart() throws IOException {

        socketService.socketServer();
    }

}
