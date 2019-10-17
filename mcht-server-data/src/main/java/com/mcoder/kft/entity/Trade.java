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
public class Trade implements Serializable {
    private static final long serialVersionUID = -3160423788012237328L;
    private int id;
    private String merchantId;
    private String orderNo;
    private String tradeName;
    private String merchantBankAccountNo;
    private String merchantBindPhoneNo;
    private String tradeTime;
    private String amount;
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
    private String messages;
    private String custEmail;
    private String emailMessages;
    private String custProtocolNo;
    private String remark;
    private String status;
    private String bankReturnTime;
    private String errorCode;
    private String errorDesc;
    private String insertTime;
    private String updateTime;

    /* 页面查询属性 */
    private String tradeDate;
    private String checkedDate;
    private String startDate;
    private String endDate;
}
