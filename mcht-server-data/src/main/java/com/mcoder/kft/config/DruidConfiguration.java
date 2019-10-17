package com.mcoder.kft.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@Getter
@Setter
@Component
@Configuration
@ConfigurationProperties(prefix = "druid")
public class DruidConfiguration {
    private String name;
    private String username;
    private String password;
    private String url;
    private int initialSize;
    private int minIdle = 20;
    private int maxActive = 50;
    private int maxWait;
    private int timeBetweenEvictionRunsMillis = 60000;
    private int minEvictableIdleTimeMillis = 1800000;
    private String validationQuery;
    private boolean testWhileIdle = true;
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;
    private boolean poolPreparedStatements = false;
    private int maxPoolPreparedStatementPerConnectionSize = 0;
    private boolean removeAbandoned;
    private int removeAbandonedTimeoutMillis = 300000;
    private boolean logAbandoned;
}
