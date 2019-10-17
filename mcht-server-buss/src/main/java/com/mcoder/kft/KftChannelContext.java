package com.mcoder.kft;

import com.mcoder.kft.security.RsaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@Configuration
public class KftChannelContext {
    @Autowired
    private List<RsaHelper> rsaHelpers;

    public RsaHelper findStrategy(String merchantId) {
        return rsaHelpers.stream()
                .filter(rsaHelper -> rsaHelper.getMerchantId().equals(merchantId))
                .findFirst()
                .get();
    }

}
