package com.mcoder.kft;

import com.mcoder.kft.security.RsaHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@Slf4j
@Configuration
public class KftChannelService {
    private static Map<String, KftChannelConfiguration.ProductNoConfiguration> productMap = new HashMap<>(5);

    @Bean
    public List<RsaHelper> list(KftChannelConfiguration kftChannelConfiguration) {
        List<RsaHelper> beans = new ArrayList<>();
        List<KftChannelConfiguration.MerchantConfiguration> configurations = kftChannelConfiguration.getMerchantInfoConf();
        configurations.forEach(merchantConfiguration -> {
            try {
                RsaHelper rsaHelper = new RsaHelper(merchantConfiguration.getType(),
                        merchantConfiguration.getPath(), merchantConfiguration.getKeyPassword().toCharArray(), null,
                        merchantConfiguration.getPassword().toCharArray());
                rsaHelper.setMerchantId(merchantConfiguration.getMerchantId());
                beans.add(rsaHelper);

                productMap.put(merchantConfiguration.getMerchantId(), merchantConfiguration.getProduct());
            } catch (Exception e) {
                log.error("KftChannelService create rsaHelper failed,keyStore path:{}", merchantConfiguration.getPath(), e);
            }
        });
        return beans;
    }


    public static KftChannelConfiguration.ProductNoConfiguration getProductMap(String key) {
        return productMap.get(key);
    }
}
