package com.example.charge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class ChargeApplication {

    public static void main(String[] args) {

//        Integer emulPort = Integer.valueOf(args[0]);
//        log.info("전달받은 인자: " + emulPort.toString());

//        Common.emulPort = emulPort;
//
//        log.info("전달받은 client emul port 인자: " + Common.emulPort);

        SpringApplication.run(ChargeApplication.class, args);

        //Thread telnetListner =new Thread(new DockingListner("DockingListner"),"DockingListner");
        //System.out.println(telnetListner.getName());
        //telnetListner.start();
    }

}
