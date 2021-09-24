package com.example.charge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RespData {

    private Integer result_code;
    private String result_message;
    private Integer stationId;
    private Integer chargerId;
    private Integer mobilityId;
    private Integer slotno;
    private Integer docked;
    private Integer battery;


}
