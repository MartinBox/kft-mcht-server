package com.mcoder.kft.vo;

import lombok.*;

import java.util.List;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultVO<T> {
    private String retCode;
    private String retDesc;
    private String availableBalance;
    private String file;
    private String url;
    private Long total;
    private List<T> rows;
    private String treatyStatus;

    public ResultVO success() {
        this.retCode = "0000";
        return this;
    }

    public ResultVO fail() {
        this.retCode = "0001";
        return this;
    }
}
