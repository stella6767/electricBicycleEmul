package com.example.charge.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class GlobalVar {

    @Value("${ip}")
    public String ip;

    @Value("${socketPort}")
    public Integer socketPort;

    @Value("${cmd.port}")
    public String port;

    @Value("${cmd.chargerid}")
    public String chargerid;

    public Integer mobilityId = 0; //mobilityId는 charger랑 1대1 관계

    public ObjectMapper objectMapper = new ObjectMapper();
    //public ConcurrentHashMap<String, String> globalReqData = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, SocketChannel> globalSocket = new ConcurrentHashMap<>();
}
