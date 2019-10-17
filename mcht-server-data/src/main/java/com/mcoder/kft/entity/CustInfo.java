package com.mcoder.kft.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustInfo implements Serializable {
    private static final long serialVersionUID = -3125399803897191174L;
    private int id;
    private String custBankNo;
    private String custBankAccountIssuerNo;
    private String custBankAccountNo;
    private String custName;
    private String custBankAcctType;
    private String custAccountCreditOrDebit;
    private String custCardCvv2;
    private String custCertificationType;
    private String custCardValidDate;
    private String custID;
    private String custPhone;
    private String custEmail;
    private String insertTime;
    private String updateTime;
    private String merchantTreatyNo;
    private String note;
    private String startDate;
    private String endDate;
    private String paymentItem;
    /**
     * 协议状态 0 - 待复核  1 - 生效  2 - 失败  3 - 冻结 6 - 解约
     */
    private String treatyStatus;
    /**
     * 协议备案编号
     */
    private String treatyNo;
    /**
     * 协议开通描述
     */
    private String treatyResultDesc;
    /**
     * 协议开通订单号
     */
    private String treatyOrderNo;
}
