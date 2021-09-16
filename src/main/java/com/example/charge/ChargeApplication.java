package com.example.charge;

import com.example.charge.config.TelnetListner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChargeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChargeApplication.class, args);

        Thread telnetListner =new Thread(new TelnetListner("TelnetListner"),"TelnetListner");
        //System.out.println(telnetListner.getName());
        telnetListner.start();

    }

}
