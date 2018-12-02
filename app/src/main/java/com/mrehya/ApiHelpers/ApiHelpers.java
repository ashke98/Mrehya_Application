package com.mrehya.ApiHelpers;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.mrehya.Helper.TLSLowApi;
import com.mrehya.Helper.Tls12SocketFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;

import static com.mrehya.ApiHelpers.LoadlingDialog.hideDialog;
import static com.mrehya.ApiHelpers.LoadlingDialog.startDialog;

/**
 * Created by hgjhghgjh on 10/22/2018.
 */

public class ApiHelpers {

    private Context context;

    public ApiHelpers(){

    }

    public ApiHelpers(Context context){
        this.context = context;
    }

    public Call getCall(String urlString, Boolean ShowLoading){
        Log.e("CALLING_API", urlString);
        OkHttpClient client = getNewHttpClient();
        Request.Builder requestBuilder = new Request.Builder();
        URL url = null;
        try {
            url = new URL(urlString);
            startDialog(context, ShowLoading);
            Request request = requestBuilder.url(url).build();
            return client.newCall(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject GetResponse(String urlString, Boolean ShowLoading){
        Log.e("CALLINGAPI", urlString);
        OkHttpClient client = getNewHttpClient();
        Request.Builder requestBuilder = new Request.Builder();

        URL url = null;
        try {
            url = new URL(urlString);
            startDialog(context, ShowLoading);
            Request request = requestBuilder.url(url).build();
            Call call = client.newCall(request);
            Response response = call.execute();
            String jsonData = response.body().string();
            JSONObject Jobject = new JSONObject(jsonData);
            Log.e("response", Jobject.has("data") + "");
            hideDialog();
            return Jobject.getJSONObject("data");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("response IS ", "FAILED");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("response IS ", "FAILED");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        hideDialog(ShowLoading);
        return null;
    }


    public JSONArray GetResponseArray(String urlString, Boolean ShowLoading){
        Log.e("CALLINGAPI", urlString);
        OkHttpClient client = getNewHttpClient();
        Request.Builder requestBuilder = new Request.Builder();

        URL url = null;
        try {
            url = new URL(urlString);
            startDialog(context, ShowLoading);
            Request request = requestBuilder.url(url).build();
            Call call = client.newCall(request);
            Response response = call.execute();
            String jsonData = response.body().string();
            JSONObject Jobject = new JSONObject(jsonData);
            Log.e("response", Jobject.has("data") + "");
            hideDialog();
            return Jobject.getJSONArray("data");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("response IS ", "FAILED");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("response IS ", "FAILED");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        hideDialog(ShowLoading);
        return null;
    }

    public Call PosttCall(String urlString, RequestBody requestBody, Boolean ShowLoading){
        Log.e("CALLING_API", urlString);
        OkHttpClient client = getNewHttpClient();

        Request.Builder requestBuilder = new Request.Builder();
        URL url = null;
        try {
            url = new URL(urlString);
            startDialog(context, ShowLoading);
            Request request = requestBuilder.url(url).post(requestBody).build();
            return client.newCall(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Call PosttCallHighTimeout(String urlString, RequestBody requestBody, Boolean ShowLoading){
        Log.e("CALLING_API", urlString);
        OkHttpClient client = getNewHttpClientHighTimeout();

        Request.Builder requestBuilder = new Request.Builder();
        URL url = null;
        try {
            url = new URL(urlString);
            startDialog(context, ShowLoading);
            Request request = requestBuilder.url(url).post(requestBody).build();
            return client.newCall(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Call PosttCall(String urlString, RequestBody requestBody, int readtimeout, int writetimeout, Boolean ShowLoading){
        Log.e("CALLING_API", urlString);
        OkHttpClient client = getNewHttpClient();

        Request.Builder requestBuilder = new Request.Builder();
        URL url = null;
        try {
            url = new URL(urlString);
            startDialog(context, ShowLoading);
            Request request = requestBuilder.url(url).post(requestBody).build();
            return client.newCall(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void PostRespomse(String urlString, RequestBody requestBody, int timeout, Boolean ShowLoading){
        Log.e("CALLINGAPI", urlString);
        OkHttpClient client = getNewHttpClient();
        Request.Builder requestBuilder = new Request.Builder();

        URL url = null;
        try {
            url = new URL(urlString);
            startDialog(context, ShowLoading);
            Request request = requestBuilder.url(url).post(requestBody).build();
            Response response = client.newCall(request).execute();
            //String jsonData = response.body().string();
            //JSONObject Jobject = new JSONObject(jsonData);
            //Log.e("response", Jobject.has("data") + "");
            hideDialog();
            //return Jobject.getJSONObject("data");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("response IS ", "FAILED");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("response IS ", "FAILED");
        }
        hideDialog(ShowLoading);
        //return null;
    }

    private OkHttpClient getNewHttpClientHighTimeout() {
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .cache(null)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS);
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            return enableTls12OnPreLollipop(client, context).build();
        }
        else
            return client.build();
    }

    private OkHttpClient getNewHttpClient() {
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .cache(null)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS);
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            return enableTls12OnPreLollipop(client, context).build();
        }
        else
            return client.build();
    }


    public static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client, Context contextt) {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            try {
                TLSLowApi tls = new TLSLowApi(contextt);
                SSLContext sc = tls.getsslContext();
                sc.init(null, null, null);
                client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()), tls.gettrustManager());

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .cipherSuites(
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
                        )
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                client.connectionSpecs(specs);
            } catch (Exception exc) {
                Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
            }
        }

        return client;
    }




}
