package com.mcoder.kft.security;

import com.mcoder.kft.cst.Constants;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2018/11/14 16:18
 */
public class RsaUtils {

    private static final Logger logger = LoggerFactory.getLogger(RsaUtils.class);


    /**
     * RSA签名
     *
     * @param content
     * @param privateKey
     * @return
     */
    public static String sign(byte[] content, X509Certificate x509Certificate, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
            signature.initSign(privateKey);
            signature.update(content);
            return Base64.encodeBase64String(signature.sign());
        } catch (Exception e) {
            logger.error("RsaUtils sign throw exception", e);
        }
        return null;
    }


    /**
     * RSA验签
     *
     * @param content    待签名数据
     * @param sign       签名值
     * @param publicKey  公钥
     * @param charset    编码格式
     * @param algorithms 算法
     * @return
     */
    public static boolean verify(String content, String sign, String publicKey, String charset, String algorithms) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decodeBase64(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            return verify(pubKey, algorithms, null, content.getBytes(Constants.CHARSET_UTF_8_NAME), sign.getBytes(Constants.CHARSET_UTF_8_NAME));
        } catch (Exception e) {
            logger.error("RsaUtils verify throw exception", e);
        }
        return false;
    }

    /**
     * 公钥验签
     *
     * @param publicKey 公钥
     * @param algorithm 填充模式
     * @param provider  验签资源对象
     * @param signData  验签目标对象
     * @param srcData
     * @return true 验签通过  false 验签不通过
     * @throws Exception
     */
    public static boolean verify(PublicKey publicKey, String algorithm, String provider, byte[] signData, byte[] srcData) throws Exception {
        Signature st = StringUtils.isBlank(provider) ? Signature.getInstance(algorithm) : Signature.getInstance(algorithm, provider);
        st.initVerify(publicKey);
        st.update(srcData);
        return st.verify(signData);
    }

    public static void main(String[] args) {


    }
}
