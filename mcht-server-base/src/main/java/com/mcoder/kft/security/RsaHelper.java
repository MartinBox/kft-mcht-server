package com.mcoder.kft.security;

import com.mcoder.kft.cst.Constants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;
import java.util.List;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2018/11/14 16:13
 */
public class RsaHelper {
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;
    @Getter
    private final X509Certificate x509Certificate;
    @Getter
    private final PrivateKey privateKey;
    private final KeyStore keyStore;

    @Getter
    @Setter
    private String merchantId;

    /**
     * 生成RSA密钥对(默认密钥长度为1024)
     *
     * @return
     */
    public static KeyPair generateRSAKeyPair() {
        return generateRSAKeyPair(1024);
    }

    public RsaHelper(String kyeStoreType, String keyStorePath, char[] keyStorePassword, String alias, char[] keyPassword) throws Exception {
        this.keyStore = loadKeyStore(keyStorePath, keyStorePassword, kyeStoreType);
        this.x509Certificate = (X509Certificate) getCertificate(keyStore, alias);
        this.privateKey = getPrivateKeyByKeyStore(keyStore, alias, keyPassword);
    }

    public static Certificate getCertificate(KeyStore keyStore, String alias) throws Exception {
        if (alias == null) {
            List<String> aliases = Collections.list(keyStore.aliases());
            if (aliases.size() != 1) {
                throw new IllegalArgumentException("[Assertion failed] - this String argument[alias] must have text; it must not be null, empty, or blank");
            }

            alias = (String) aliases.get(0);
        }

        return keyStore.getCertificate(alias);
    }

    public static PrivateKey getPrivateKeyByKeyStore(KeyStore keyStore, String alias, char[] keyPassword) throws Exception {
        if (alias == null) {
            List<String> aliases = Collections.list(keyStore.aliases());
            if (aliases.size() != 1) {
                throw new IllegalArgumentException("[Assertion failed] - this String argument[alias] must have text; it must not be null, empty, or blank");
            }

            alias = (String) aliases.get(0);
        }

        PrivateKey key = (PrivateKey) keyStore.getKey(alias, keyPassword);
        return key;
    }

    public static KeyStore loadKeyStore(String keyStorePath, char[] password, String keystoreType) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keystoreType == null ? KeyStore.getDefaultType() : keystoreType);
        File keystore = new File(keyStorePath);
        if (keystore == null || keystore.exists() && keystore.isDirectory()) {
            throw new IllegalArgumentException("keystore[" + keyStorePath + "]必须是一个已经存在的文件,不能为空,且不能是一个文件夹");
        }
        FileInputStream is = null;
        try {
            is = new FileInputStream(keystore);
            keyStore.load(is, password);
        } finally {
            if (is != null) {
                is.close();
            }

        }

        return keyStore;
    }

    /**
     * 生成RSA密钥对
     *
     * @param keyLength 密钥长度，范围：512～2048
     * @return
     */
    public static KeyPair generateRSAKeyPair(int keyLength) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(keyLength);
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }


    public static String encode64PublicKeyString(PublicKey key) throws Exception {
        byte[] keyBytes = key.getEncoded();
        String s = (new BASE64Encoder()).encode(keyBytes);
        return s;
    }

    public static String encode64PrivateKeyString(PrivateKey key) throws Exception {
        byte[] keyBytes = key.getEncoded();
        String s = (new BASE64Encoder()).encode(keyBytes);
        return s;
    }

    /**
     * 得到公钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PublicKey decodePublicKeyFromBase64Str(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 得到私钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey decodePrivateKeyFromBase64Str(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 用公钥加密
     *
     * @param data
     * @param pubKey
     * @return
     */
    public static byte[] encryptData(byte[] data, PublicKey pubKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);

            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                // 写入内存缓冲区
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            // 获取内存缓冲中的数据，转换成数组
            byte[] decryptedData = out.toByteArray();
            out.close();
            return decryptedData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 私钥解密
     *
     * @param encryptedData
     * @param priKey
     * @return
     */
    public static byte[] decryptData(byte[] encryptedData, PrivateKey priKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            int inputLen = encryptedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                // 写入内存缓冲区
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            // 获取内存缓冲中的数据，转换成数组
            byte[] decryptedData = out.toByteArray();
            out.close();
            return decryptedData;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 公钥加密
     *
     * @param plainText 要加密的明文数据
     * @param pubKey    公钥
     * @return
     */
    public static String encryptDataFromStr(String plainText, PublicKey pubKey) throws UnsupportedEncodingException {
        byte[] dataByteArray = plainText.getBytes(Constants.CHARSET_UTF_8_NAME);
        byte[] encryptedDataByteArray = RsaHelper.encryptData(dataByteArray, pubKey);
        return Base64.encodeBase64String(encryptedDataByteArray);

    }

    /**
     * 根据指定私钥对数据进行签名(默认签名算法为"SHA1withRSA")
     *
     * @param data   要签名的数据
     * @param priKey 私钥
     * @return
     */
    public static byte[] signData(byte[] data, PrivateKey priKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        return signData(data, priKey, "SHA1withRSA");
    }

    /**
     * 根据指定私钥和算法对数据进行签名
     *
     * @param data      要签名的数据
     * @param priKey    私钥
     * @param algorithm 签名算法
     * @return
     */
    public static byte[] signData(byte[] data, PrivateKey priKey, String algorithm) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(priKey);
        signature.update(data);
        return signature.sign();
    }

    /**
     * 用指定的公钥进行签名验证(默认签名算法为"SHA1withRSA")
     *
     * @param data   数据
     * @param sign   签名结果
     * @param pubKey 公钥
     * @return
     */
    public static boolean verifySign(byte[] data, byte[] sign, PublicKey pubKey) {
        return verifySign(data, sign, pubKey, "SHA1withRSA");
    }

    /**
     * @param data      数据
     * @param sign      签名结果
     * @param pubKey    公钥
     * @param algorithm 签名算法
     * @return
     */
    public static boolean verifySign(byte[] data, byte[] sign,
                                     PublicKey pubKey, String algorithm) {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(pubKey);
            signature.update(data);
            return signature.verify(sign);
        } catch (Exception ex) {
            return false;
        }
    }


    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    public static RSAPublicKey loadPublicKeyByStr(String publicKeyStr)
            throws Exception {
        try {
            byte[] buffer = Base64.decodeBase64(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    /**
     * 从字符串加密私钥
     *
     * @param privateKeyStr
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.decodeBase64(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }

}
