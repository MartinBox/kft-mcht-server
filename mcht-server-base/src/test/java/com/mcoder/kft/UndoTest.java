package com.mcoder.kft;

import com.mcoder.kft.cst.Constants;
import com.mcoder.kft.net.Https;
import com.mcoder.kft.security.RsaHelper;
import com.mcoder.kft.security.RsaUtils;
import com.mcoder.kft.utils.CollectionUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
public class UndoTest extends AppTest {
    // 快捷代扣(步骤1)

    @Test
    public void http() throws Exception {

        RsaHelper rsaHelper = new RsaHelper("PKCS12",
                "d:/work/pfx.pfx", "123456".toCharArray(), null,
                "123456".toCharArray());

        Map<String, String> params = new HashMap<>();
        params.put("service", "query_available_balance");
        params.put("version", "1.0.0-IEST");
        params.put("merchantId", merchantId);
        params.put("productNo", "2GCB0AAA");
        params.put("callerIp", "10.36.160.37");
        params.put("language", "zh_CN");
        String signStr = CollectionUtils.coverMap2String(params, true, null);
        params.put("signatureInfo", RsaUtils.sign(signStr.getBytes(Constants.CHARSET_UTF_8_NAME), rsaHelper.getX509Certificate(), rsaHelper.getPrivateKey()));
        params.put("signatureAlgorithm", "RSA");
        String result = Https.anInstance(TimeUnit.SECONDS, 10)
//                .ssl12Ubidirectional(new FileInputStream(new File("d:/work/pfx.pfx")), "PKCS12", "123456", "123456")
                .trustAnyServer()
                .url("https://218.17.35.123:6443/gateway/nonbatch")
                .formBody(params)
                .post()
                .execute();
        System.out.println(result);
    }
}
