package com.mcoder.kft;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@Data
@Component
@Configuration
@ConfigurationProperties(prefix = "login")
public class LoginConfiguration {
    /**
     * 是否校验邮箱验证码
     */
    private boolean emailCheck;
    /**
     * 是否校验登录密码
     */
    private boolean pwdCheck;
}
