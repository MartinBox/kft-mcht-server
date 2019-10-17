package com.mcoder.kft.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author liuheng
 * @Description: TODO
 * @date 2019/1/10 17:18
 */
@Data
public class KftBaseResultDto implements Serializable {
    private String status;
    /**
     * 失败详情
     */
    private String failureDetails;
    private String bankReturnTime;
    private String errorCode;
}
