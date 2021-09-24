package com.example.charge.dto;

import lombok.Getter;

@Getter
public enum Opcode {

    DOCKING("docking"), RENTAL("rental"), RETURN("return"), INIT("1");

    String code;

    Opcode(String code) {
        this.code = code;
    }
}