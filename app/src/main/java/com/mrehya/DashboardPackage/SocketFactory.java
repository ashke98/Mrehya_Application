package com.mrehya.DashboardPackage;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.HurlStack;
        import com.android.volley.toolbox.StringRequest;
        import com.android.volley.toolbox.Volley;
        import com.mrehya.R;

        import java.io.FileNotFoundException;
        import java.io.IOException;
        import java.io.InputStream;
        import java.security.KeyManagementException;
        import java.security.KeyStore;
        import java.security.KeyStoreException;
        import java.security.NoSuchAlgorithmException;
        import java.security.SecureRandom;
        import java.security.cert.Certificate;
        import java.security.cert.CertificateException;
        import java.security.cert.CertificateFactory;
        import java.security.cert.X509Certificate;
        import javax.net.ssl.HostnameVerifier;
        import javax.net.ssl.HttpsURLConnection;
        import javax.net.ssl.SSLContext;
        import javax.net.ssl.SSLSession;
        import javax.net.ssl.SSLSocketFactory;
        import javax.net.ssl.TrustManager;
        import javax.net.ssl.TrustManagerFactory;
        import android.content.Context;

public class SocketFactory {
    public SSLSocketFactory getSocketFactory(Context context2) {

        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = context2.getResources().openRawResource(R.raw.server);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                Log.e("CERT", "ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }


            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);


            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);


            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {

                    Log.e("CipherUsed", session.getCipherSuite());
                    return hostname.compareTo("mrehya.com")==0; //The Hostname of your server
                }
            };


            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
            SSLContext context = null;
            context = SSLContext.getInstance("TLS");

            context.init(null, tmf.getTrustManagers(), null);
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

            SSLSocketFactory sf = context.getSocketFactory();


            return sf;

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return  null;
    }

}
