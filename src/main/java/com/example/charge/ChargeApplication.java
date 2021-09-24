package com.example.charge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ChargeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChargeApplication.class, args);

        //Thread telnetListner =new Thread(new DockingListner("DockingListner"),"DockingListner");
        //System.out.println(telnetListner.getName());
        //telnetListner.start();

    }

}
