package com.easing.commons.android.http;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import lombok.SneakyThrows;

public class SSL {

    @SneakyThrows
    public static SSLSocketFactory sslSocketFactory() {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, getTrustManager(), new SecureRandom());
        return sslContext.getSocketFactory();
    }

    public static X509TrustManager trustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String message) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String message) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        };
    }

    private static TrustManager[] getTrustManager() {
        return new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String message) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String message) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        }};
    }

    public static HostnameVerifier hostnameVerifier() {
        return (message, hostnameVerifier) -> true;
    }

}














