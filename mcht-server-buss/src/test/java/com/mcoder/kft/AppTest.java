package com.mcoder.kft;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Enumeration;

/**
 * Unit test for simple TradeService.
 */
public class AppTest 
{
    public static void readPfx(String certPath, String password)throws Exception {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        FileInputStream fis = new FileInputStream(certPath);
        ks.load(fis, password.toCharArray());
        System.out.println("证书类型___:"+ ks.getType());

        Enumeration enumas = ks.aliases();
        String aliases = null;
        if (enumas.hasMoreElements()){
            aliases = (String) enumas.nextElement();
            System.out.println("证书别名Alias___:"+aliases);
        }
        Certificate cert = ks.getCertificate(aliases);
        PublicKey pubkey = cert.getPublicKey();
        System.out.println("公钥___:"+pubkey);

        System.out.println("is key entry=" + ks.isKeyEntry(aliases));
        PrivateKey prikey = (PrivateKey) ks.getKey(aliases,password.toCharArray());
        System.out.println("私钥___:"+prikey);
    }

    public static void main(String[] args) {
        try {
            readPfx("D:\\Program Files\\Java\\projects\\musclecoder\\kft-mcht-server\\doc\\prod\\certs\\2019031902924240\\gaoxinyuan-pri.pfx", "000000");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
