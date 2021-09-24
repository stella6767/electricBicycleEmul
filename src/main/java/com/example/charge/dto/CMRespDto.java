package com.example.charge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class CMRespDto<T> {

    private Opcode opcode; //추가
    private T data;

}
