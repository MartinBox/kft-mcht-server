package com.mcoder.kft;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@Data
@Component
@Configuration
@ConfigurationProperties(prefix = "kft")
public class KftChannelConfiguration {
    private String clientIp;
    private String language;
    private String serviceVersion;
    private List<MerchantConfiguration> merchantInfoConf;
    private HttpConfiguration http;
    private SftpConfiguration sftp;
    private ErrorCodeConfiguration errorCode;
    private FilesConfiguration files;

    @Data
    public static class MerchantConfiguration {
        private String merchantId;
        private String type;
        private String path;
        private String password;
        private String keyPassword;
        private ProductNoConfiguration product;
    }

    @Data
    public static class HttpConfiguration {
        private String domain;
        private int port;
        private int connectionTimeoutSeconds;
        private int responseTimeoutSeconds;
    }

    @Data
    public static class SftpConfiguration {
        private String accountName;
        private String password;
        private String domain;
        private int port;
        private String dir;
    }

    @Data
    public static class ProductNoConfiguration {
        /**
         * 单笔付款
         */
        private String trade;
        /**
         * 付款查询
         */
        private String tradeQuery;
        /**
         * 查询资金余额
         */
        private String queryAvailableBalance;
        /**
         * 查询资金变动
         */
        private String queryBalanceChange;
        /**
         * 查询资金变动
         */
        private String sfptQuery;
    }

    @Data
    public static class ErrorCodeConfiguration {
        private List<String> fail;
        private List<String> process;
    }

    @Data
    public static class FilesConfiguration {
        private String balanceChangeFile;
        private String sftpFile;
    }
}
