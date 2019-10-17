package com.mcoder.kft.dto;

import lombok.Data;

/**
 * @author liuheng
 * @Description: TODO
 * @date 2019/1/10 17:13
 */
@Data
public class DetailResultDto extends KftBaseResultDto {
    private String originalProductNo;
    private String batchNo;
    private String orderNo;
    private String status;
    private String dishonorStatus;
    private String bankDishonorTime;
    private String settlementStatus;
    private String estimateSettlementDate;
    private String settlementTime;
    private String nettingDate;
    private String feeSettlementTime;
    private String feeSettlementStatus;
}
