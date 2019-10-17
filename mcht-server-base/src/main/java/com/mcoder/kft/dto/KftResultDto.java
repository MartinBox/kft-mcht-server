package com.mcoder.kft.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@Data
public class KftResultDto extends KftBaseResultDto implements Serializable {
    private static final long serialVersionUID = 5187955073529381834L;
    private String availableBalance;
    private String fileAbsolutePath;
    private String totalCount;
    private String totalAmount;
    private String fileName;
    private List<DetailResultDto> details;
    /**
     * 协议备案编号
     */
    private String treatyNo;
}
