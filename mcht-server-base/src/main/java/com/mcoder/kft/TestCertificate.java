package com.mcoder.kft;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
public class TestCertificate {
    public static KeyStore loadKeyStore(String keyStorePath, char[] password, String keystoreType) throws Exception {
        KeyStore ks = KeyStore.getInstance(keystoreType == null ? KeyStore.getDefaultType() : keystoreType);
        File keystore = new File(keyStorePath);
        if (keystore == null || (keystore.exists() && keystore.isDirectory())) {
            throw new IllegalArgumentException("keystore[" + keyStorePath + "]必须是一个已经存在的文件,不能为空,且不能是一个文件夹");
        }
        InputStream is = null;
        try {
            is = new FileInputStream(keystore);
            ks.load(is, password);
            return ks;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    public static Certificate getCertificate(String certificatePath) throws Exception {
        File certificate = new File(certificatePath);
        if (certificate == null || (certificate.exists() && certificate.isDirectory())) {
            throw new IllegalArgumentException("certificatePath[" + certificatePath + "]必须是一个已经存在的文件,不能为空,且不能是一个文件夹");
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(certificate);
            // 实例化证书工厂
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate cert = cf.generateCertificate(inputStream);
            return cert;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    public static void main(String[] args) throws Exception {

        String privateKeyPath = "d:/work/2018102500097289_test.pfx";
        String publicKeyPath ="d:/work/2018102500097289_test.cer";
        String password = "123456";
        String keyStorePassword = password;
        String privateKeyPassword = password;
        /*String privateKeyPath = "D:\\储信证书\\pfx.pfx";
        String publicKeyPath = "D:\\储信证书\\pfx.cer";
        String password = "123456";
        String keyStorePassword = password;
        String privateKeyPassword = password;*/
        String charset = "UTF-8";
        String alias = null;
        String data = "数据数据数据数据数据数据";
        KeyStore ks = loadKeyStore(privateKeyPath, keyStorePassword.toCharArray(), "PKCS12");
        if (alias == null) {
            List<String> aliases = Collections.list(ks.aliases());
            if (aliases.size() == 1) {
                alias = aliases.get(0);
            } else {
                throw new IllegalArgumentException(
                        "[Assertion failed] - this String argument[alias] must have text; it must not be null, empty, or blank");
            }
        }
        X509Certificate privateCertificate = (X509Certificate) ks.getCertificate(alias);
        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, privateKeyPassword.toCharArray());
        Signature signature = Signature.getInstance(privateCertificate.getSigAlgName());
        signature.initSign(privateKey);
        signature.update(data.getBytes(charset));
        byte[] signInfoData = signature.sign();
        //String signInfo = org.apache.commons.codec.binary.Base64.encodeBase64String(signInfoData);
        String signInfo = Base64.encodeBase64String(signInfoData);
        System.out.println("加签成功：\n" + signInfo);
        // 公钥验签
        //signInfoData = org.apache.commons.codec.binary.Base64.decodeBase64(signInfo);
        signInfoData = Base64.decodeBase64(signInfo);
        X509Certificate publicCertificate = (X509Certificate) getCertificate(publicKeyPath);
        signature = Signature.getInstance(publicCertificate.getSigAlgName());
        signature.initVerify(publicCertificate);
        signature.update(data.getBytes(charset));
        boolean success = signature.verify(signInfoData);
        System.out.println(success ? "验签成功" : "验签失败");
    }

}
