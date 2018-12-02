package com.mrehya.ApiHelpers;

import android.app.ProgressDialog;
import android.content.Context;

import io.paperdb.Paper;

/**
 * Created by hgjhghgjh on 10/22/2018.
 */

public class LoadlingDialog {
    private static ProgressDialog pDialog;
    private static String Language="fa";

    public static void startDialog(Context context, String Message){
        pDialog = new ProgressDialog(context);
        Paper.init(context);
        Language = (String) Paper.book().read("language");
        showDialog(Message);
    }

    public static void startDialog(Context context, String Message, Boolean ShowLoading){
        pDialog = new ProgressDialog(context);
        Paper.init(context);
        Language = (String) Paper.book().read("language");
        if(ShowLoading)
            showDialog(Message);
    }

    public static void startDialog(Context context){
        pDialog = new ProgressDialog(context);
        Paper.init(context);
        Language = (String) Paper.book().read("language");
        showDialog();
    }

    public static void startDialog(Context context, Boolean ShowLoading){
        if(ShowLoading){
            pDialog = new ProgressDialog(context);
            Paper.init(context);
            Language = (String) Paper.book().read("language");
            showDialog();
        }
    }

    private static void showDialog(String Message) {
        if(pDialog!=null) {
            pDialog.setCancelable(true);
            pDialog.setMessage(Message);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
    }

    private static void showDialog() {
        if(pDialog!=null)
        {
            pDialog.setCancelable(true);
            if(Language.equals("fa"))
                pDialog.setMessage("در حال بارگزاری...");
            else
                pDialog.setMessage("Loading...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
    }


    public static void hideDialog() {
        if(pDialog!=null)
            if (pDialog.isShowing())
                pDialog.dismiss();
    }

    public static void hideDialog(Boolean ShowLoading) {
        if(ShowLoading &&  pDialog!=null)
            if (pDialog.isShowing())
                pDialog.dismiss();
    }
}
