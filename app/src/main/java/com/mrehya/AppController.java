package com.mrehya;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController{

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;

    public static final AppController mInstance = new AppController();

    private AppController(){

    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mInstance = this;
//    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag, Context context) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue(context).add(req);
    }
    public <T> void addToRequestQueue(Request<T> req, String tag) {

    }

    public <T> void addToRequestQueue(Request<T> req, Context context) {
        req.setTag(TAG);
        getRequestQueue(context).add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}