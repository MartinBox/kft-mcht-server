package com.mcoder.kft.cst;

/**
 * @author 1194688733@qq.com
 * @Description: 公共常量类
 * @date 2018/5/29 14:21
 */
public interface Constants {
    /**
     * 空字符串
     */
    String EMPTY_STR = "";
    /**
     * 跟踪号key
     */
    String TRANSACTION_ID = "transactionId";
    /**
     * 字符集
     */
    String CHARSET_UTF_8_NAME = "UTF-8";

    String RET_CODE_FAIL = "0001";

    String KFT_URL_FORMAT = "https://%s:%s/gateway/nonbatch";
    String KFT_URL_FILE_FORMAT = "https://%s:%s/gateway/file";


    /**
     * @author 1194688733@qq.com
     * @Description: 分隔符
     * @date 2018/7/24 14:45
     */
    interface Separate {
        String MIDDLE_LINE = "-";
        String SEMICOLON_STR = ";";
        String SLASH_STR = "/";
        String UNDER_LINE_STR = "_";
        String EQUAL = "=";
        String BIT_AND = "&";
    }

    interface CardType {
        /**
         * 借记卡
         */
        String DEBIT = "1";
        /**
         * 贷记卡
         */
        String CREDIT = "2";
    }

    interface TreatyStatus {
        /**
         * 未发起
         */
        String INIT = "-1";
        /**
         * 待复核
         */
        String STAT_0 = "0";
        /**
         * 生效
         */
        String STAT_1 = "1";
        /**
         * 失败
         */
        String STAT_2 = "2";
        /**
         * 冻结
         */
        String STAT_3 = "3";
        /**
         * 解约
         */
        String STAT_6 = "6";
    }
}
