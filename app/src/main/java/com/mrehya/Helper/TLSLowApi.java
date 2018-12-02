package com.mrehya.Helper;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by hgjhghgjh on 10/21/2018.
 */
//https://gist.github.com/Kane-Shih/2a556fd24604ed9e80d69581c4f912a3

public class TLSLowApi {
    private Context context;
    public TLSLowApi(Context context){
        this.context = context;
    }
    private void checkTls() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {
                ProviderInstaller.installIfNeeded(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void ssl() throws NoSuchAlgorithmException, KeyManagementException {
        String[] prts = new String[1];
        prts[0] = "TLSv1";
        //prts[1] = "TLSv2";
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null,null,null);
        Log.e("GOT U", "SSLS");
        sslContext.createSSLEngine().setEnabledProtocols(prts);
    }

    public SSLContext getsslContext() {
        if(context!=null){
            try {
                ProviderInstaller.installIfNeeded(context);
                SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, null, null);
                SSLEngine engine = sslContext.createSSLEngine();
                engine.setEnabledProtocols(new String[]{"TLSv1.1", "TLSv1.2"});
                return sslContext;
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public X509TrustManager gettrustManager(){
        TrustManagerFactory trustManagerFactory = null;
        try {
            trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            trustManagerFactory.init((KeyStore) null);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }
        X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
        return trustManager;
    }
}
