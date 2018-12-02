package com.mrehya.DashboardPackage;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.mrehya.ApiHelpers.ApiHelpers;
import com.mrehya.AppConfig;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by hgjhghgjh on 8/9/2018.
 */

public class DashboardLists {
    public static JSONObject Companiesdata = new JSONObject();
    public static JSONObject Testsdata = new JSONObject();
    public static JSONObject Productsdata = new JSONObject();
    private Boolean show;
    private ProgressBar progressbar;
    private LinearLayout  Connected, Wellcome;
    private Context context;

    public DashboardLists(Context context, Boolean show, ProgressBar progressbar, LinearLayout Connected, LinearLayout Wellcome){
        if(Companiesdata.length()==0 || Testsdata.length()==0 || Productsdata.length()==0)
        {
            this.progressbar = progressbar;
            this.Connected = Connected;
            this.Wellcome = Wellcome;
            this.show = show;
            this.context=context;

            Connected.setVisibility(View.GONE);
            this.progressbar.setVisibility(View.VISIBLE);
            Wellcome.setVisibility(View.VISIBLE);
            new ApiTask().execute(0);
        }
        else{
            checkView();
        }
    }
    public DashboardLists(Context context, Boolean show, ProgressBar progressbar){
        if(Companiesdata.length()==0 || Testsdata.length()==0 || Productsdata.length()==0)
        {
            this.progressbar = progressbar;
            this.show = show;
            this.context=context;
            new ApiTask().execute(0);
        }
    }
    public DashboardLists(Context context, Boolean show){
        if(Companiesdata.length()==0 || Testsdata.length()==0 || Productsdata.length()==0)
        {
            this.context=context;
            //this.sf = new SocketFactory();
            //this.rq = Volley.newRequestQueue(this.context, new HurlStack(null, sf.getSocketFactory(this.context)));
            this.show = show;
            new ApiTask().execute(0);
        }
    }
    public DashboardLists(Context context, String Listname){
        this.show = false;
        this.context=context;
//        if(Listname.equals("Products") || Productsdata.length()==0)
//        {
//            Product_Api();
//            //     Product_Api2(context);
//        }

    }
    private void checkView(){
        if(progressbar!=null){
            progressbar.setVisibility(View.GONE);
        }
        if(Wellcome!=null){
            Wellcome.setVisibility(View.GONE);
        }
        if(Connected!=null) {
            Connected.setVisibility(View.VISIBLE);
        }
    }


    //APIS
    class ApiTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep(500);
                // some long running task will run here. We are using sleep as a dummy to delay execution
                if(Testsdata.length()==0)
                    Exam_Api();

                if(Companiesdata.length()==0)
                    prepareCompanies();

                if(Productsdata.length()==0)
                    Product_Api();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }

    private void Exam_Api(){
        ApiHelpers ah = new ApiHelpers(context);
        ah.getCall(AppConfig.URL_DashExam+"1", false).enqueue(

                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                checkView();
                            }
                        });
                    }
                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {

                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject Jobject = new JSONObject(response.body().string());
                                        Testsdata = Jobject.getJSONObject("data");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                checkView();
                            }
                        });
                    }
                }
        );
    }
    private void prepareCompanies(){
        ApiHelpers ah = new ApiHelpers(context);
        ah.getCall(AppConfig.URL_Hires+"fa", false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                checkView();
                            }
                        });
                    }
                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject Jobject = new JSONObject(response.body().string());
                                        Companiesdata = Jobject.getJSONObject("data");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                checkView();
                            }
                        });
                    }
                }
        );
    }
    private void Product_Api() {
        ApiHelpers ah = new ApiHelpers(context);
        ah.getCall(AppConfig.URL_Products, false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                checkView();
                            }
                        });
                    }
                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject Jobject = new JSONObject(response.body().string());
                                        Productsdata = Jobject.getJSONObject("data");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                checkView();
                            }
                        });
                    }
                }
        );
    }
    private void Product_Api2(Context context){
        ApiHelpers ah = new ApiHelpers(context);
        JSONObject response = ah.GetResponse(AppConfig.URL_Products, false);
        if(response!=null)
            Productsdata = response;
    }

    //methods
    public void setProductsdata(JSONObject data){
        Productsdata = data;
    }
    public int getCompanyListCount(){
        return Companiesdata.length();
    }
    public int getTestsdataCount(){
        return Testsdata.length();
    }
}
