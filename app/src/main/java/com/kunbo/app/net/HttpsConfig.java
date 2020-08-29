package com.kunbo.app.net;

import android.content.Context;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class HttpsConfig {
    /**
     * @return
     */
    public static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory sSocketFactory = null;
        try {
            // Install the all-trusting trust manager  //指定的信任管理器  { 忽略证书 }
            TrustManager[] trustManagers = {new AppTrustManager()};
            // 创建SSLContext对象    SSL/TLS
            SSLContext sslContext = SSLContext.getInstance("TLS");
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            sslContext.init(null, trustManagers, new SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            //用所有信任的管理器创建SSL套接字工厂        // 从上述SSLContext对象中得到SSLSocketFactory对象
            sSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
        }
        return sSocketFactory;
    }

    /**
     * 忽略Hostname 的验证
     * （仅仅用于测试阶段，不建议用于发布后的产品中。）
     */
    public static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }

    /**
     * 忽略信任证书
     */
    public static class AppTrustManager implements X509TrustManager {
        // 检查客户端证书
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        // 检查服务器端证书
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        // 返回受信任的X509证书数组
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }


    /*—————————————————————————添加信任证书———————————————————————————————*/

    /**
     * @param CertificatesName 证书
     * @param KeyStorePassword 证书密码
     * @return
     */
    public static X509TrustManager getAppTrustManagerForAssets(Context context, String CertificatesName, String KeyStorePassword) {
        X509TrustManager trustManager = null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            InputStream inputStream = context.getAssets().open(CertificatesName);
            // Any password will work.
            char[] password = KeyStorePassword.toCharArray();
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            //    //直接添加
            //    keyStore.load(inputStream, password);
            keyStore.load(null, password);
            Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(inputStream);
            if (certificates.isEmpty()) {
                throw new IllegalArgumentException("expected non-empty set of trusted certificates");
            }
            int index = 0;
            for (Certificate certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificate);
            }
            // Use it to build an X509 trust manager.    密钥校验
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            } else {
                trustManager = (X509TrustManager) trustManagers[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trustManager;
    }

    public static X509TrustManager getAppTrustManagerForRaw(Context context, int[] certificates) {
        X509TrustManager trustManager = null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            // Put the certificates a key store.
            keyStore.load(null, null);
            for (int i = 0; i < certificates.length; i++) {
                InputStream certificate = context.getResources().openRawResource(certificates[i]);
                keyStore.setCertificateEntry(String.valueOf(i), certificateFactory.generateCertificate(certificate));
                if (certificate != null) {
                    certificate.close();
                }
            }
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            } else {
                trustManager = (X509TrustManager) trustManagers[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trustManager;
    }
}
