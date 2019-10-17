package com.mcoder.kft.net;

import okhttp3.TlsVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * @author 1194688733@qq.com
 * @Description: https client
 * @date 2018/7/19 17:11
 */
public class Https extends Http {

    private static final Logger logger = LoggerFactory.getLogger(Https.class);

    private InputStream keyStoreInput;

    private InputStream trustKeyStoreInput;

    public Https() {
        super();
    }

    public Https(TimeUnit timeUnit, int timeout) {
        super(timeUnit, timeout);
    }

    public static Https anInstance() {
        return new Https();
    }

    public static Https anInstance(TimeUnit timeUnit, int timeout) {
        return new Https(timeUnit, timeout);
    }

    public Https trustAnyServer() throws Exception {
        return ssl12(false, null, null, null, true, null, null);
    }


    public Https ssl12Ubidirectional(InputStream keyStoreInput, String keyStoreType, String storePwd, String keyPwd) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        this.keyStoreInput = keyStoreInput;
        this.trustKeyStoreInput = trustKeyStoreInput;
        return ssl12(false, keyStoreType, storePwd, keyPwd, true, keyStoreType, null);
    }


    public Https ssl12Bidirectional(byte[] keyStore, String keyStoreType, String storePwd, String keyPwd, byte[] trustKeyStore, String trustKeyStoreType, String trustStorePwd) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        keyStoreInput = new ByteArrayInputStream(keyStore);
        trustKeyStoreInput = new ByteArrayInputStream(trustKeyStore);
        return ssl12(false, keyStoreType, storePwd, keyPwd, true, trustKeyStoreType, null);
    }

    private Https ssl12(boolean isBothAuth, String keyStoreType, String storePwd, String keyPwd, boolean trustAnyServer, String trustKeyStoreType, String trustStorePwd) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        SSLContext ctx = SSLContext.getInstance(TlsVersion.TLS_1_2.javaName());

        KeyManagerFactory kmf = null;
        if (isBothAuth) {
            kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            KeyStore ks = KeyStore.getInstance(keyStoreType);
            ks.load(keyStoreInput, storePwd.toCharArray());
            kmf.init(ks, keyPwd.toCharArray());
        }

        TrustManager[] tm;
        if (trustAnyServer) {
            tm = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
        } else {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            KeyStore tks = KeyStore.getInstance(trustKeyStoreType);
            tks.load(trustKeyStoreInput, trustStorePwd.toCharArray());
            tmf.init(tks);
            tm = tmf.getTrustManagers();
        }
        ctx.init(kmf != null ? kmf.getKeyManagers() : null, tm, null);
        HttpClientHolder.builder().sslSocketFactory(ctx.getSocketFactory(), new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }).hostnameVerifier((s, sslSession) -> trustAnyServer);
        return this;
    }


    @Override
    public String execute() throws IOException {
        try {
            return super.execute();
        } finally {
            try {
                if (keyStoreInput != null) {
                    keyStoreInput.close();
                }
                if (trustKeyStoreInput != null) {
                    trustKeyStoreInput.close();
                }
            } catch (Exception e) {
                logger.error("close keystore input stream failure after response", e);
            }

        }
    }
}
