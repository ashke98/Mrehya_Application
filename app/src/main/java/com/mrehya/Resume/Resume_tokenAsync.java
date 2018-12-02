package com.mrehya.Resume;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mrehya.AppConfig;
import com.mrehya.AppController;
import com.mrehya.DashboardPackage.HttpsTrustManager;
import com.mrehya.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ashke98 on 7/31/2018.
 */

public class Resume_tokenAsync {
    public static class ResumeAsyncTask extends AsyncTask<Integer, Void, String> {

//        private final Context context;
//        private final SessionManager session;
//        private final String Language;
//        private boolean hasresume=false;
        public ResumeAsyncTask(Context context, String Language) {
//            this.session = new SessionManager(context);
//            this.Language = Language;
//            this.context = context;
//            this.hasresume = session.getUserDetails().getResume()==null? false:true;
        }
        @Override
        protected String doInBackground(Integer... params) {
//            int seconds = params[0];
//            try { Thread.sleep(seconds*1000);
//                checkLogin(context, Language, session, hasresume);}
//            catch (InterruptedException e) { e.printStackTrace(); }

            return "OK";
        }
        @Override
        protected void onPostExecute(String result) {
        }
    }

    //APIS
//    private static void checkLogin(final Context context, final String Language,
//                                   final SessionManager session, final Boolean hasresume){
//        String tag_string_req = "req_login";
//        HttpsTrustManager.allowAllSSL();
//        StringRequest strReq = new StringRequest(Request.Method.POST,
//                AppConfig.URL_SIGNIN, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                //Log.d("TAG", "Login Response: " + response.toString());
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    String success = jObj.getString("success");
//                    if (success.equalsIgnoreCase("true")) {
//                        JSONObject c = jObj.getJSONObject("data");
//                        set_credentials(
//                                c.getString("token"),
//                                c.getString("username"),
//                                session.getUserDetails().getPassword(),
//                                session, Language, hasresume, context);
//                    } else {
//                        // Error in login. Get the error message
//                        if(Language.equals("fa")){
//                            Toast.makeText(context, "مشکلی در گرفتن اطلاعات کابری به وجود آمده است", Toast.LENGTH_SHORT).show();
//                        }
//                        else{
//                            Toast.makeText(context, "Failed to get user info...", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                } catch (JSONException e) {
//                    // JSON error
//                    Log.d("TAG", "مشکلی در اتصال با سرور پیش آمده است ,error 1 ");
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("TAG",    " مشکلی در اتصال با سرور پیش آمده است" + error.getMessage());
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("email", session.getUserDetails().getEmail());
//                params.put("password", session.getUserDetails().getPassword());
//                params.put("type", "1");
//                return params;
//            }
//            //basic auth
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String,String> headers = new HashMap<String, String>();
//                // add headers <key,value>
//                String credentials = AppConfig.AUTH_USERNAME+":"+AppConfig.AUTH_PASS;
//                String auth = "Basic "
//                        + Base64.encodeToString(credentials.getBytes(),
//                        Base64.URL_SAFE|Base64.NO_WRAP);
//                headers.put("Authorization", auth);
//                return headers;
//            }
//        };
//        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
//    }
//    private static void set_credentials(final String token, final String email, final String passwordd,
//                                        final SessionManager session, final String Language, final Boolean hasresume,
//                                        final Context context){
//        String tag_string_req = "req_login";
//        HttpsTrustManager.allowAllSSL();
//        StringRequest strReq = new StringRequest(Request.Method.POST,
//                AppConfig.URL_VIEW_Profile, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                //Log.d("TAG", "View Profile Response: " + response.toString());
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    String success = jObj.getString("success");
//                    if (success.equalsIgnoreCase("true")) {
//                        JSONObject c = jObj.getJSONObject("data");
//                        int id =0;
//                        String firstname=" ",lastname=" " , email=" ", phone= " ", token=" ",
//                                image =" ",mobile=" ",address=" ", zip=" ",password=" ", resume=" ";
//                        id = c.getInt("id");
//                        firstname = c.getString("first_name");
//                        lastname = c.getString("last_name");
//                        email = c.getString("email");
//                        token = c.getString("auth_key");
//                        phone = c.getString("tel_number");
//                        image = c.getString("avatar");
//                        mobile = c.getString("mobile");
//                        address = c.getString("address");
//                        zip = c.getString("postal_code");
//                        if(c.has("resumeId"))
//                            resume = c.getInt("resumeId")+"";
//                        else
//                            resume =null;
//                        password = passwordd;
//                        session.setLogin(true);
//                        session.setUserDetails(id,firstname,lastname,email,phone,token, image,mobile,address,zip,password
//                                ,resume);
//                        //Log.e("hasresume", hasresume.toString());
//                        if(!hasresume){
//                            make_resume(session, Language, context);
//                        }
//                    } else {
//                        //Log.d("TAG", "failed to get user");
//                    }
//                } catch (JSONException e) {
//                    // JSON error
//                    //Log.d("TAG", "get creds error 1 ");
//                }
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //Log.e("TAG",  "get creds error2 : " + error.getMessage());
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("auth", token);
//                return params;
//            }
//        };
//        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
//    }
//    private static void make_resume(final SessionManager session, final String Language, final Context context){
//        String tag_string_req = "req_make_resume";
//        HttpsTrustManager.allowAllSSL();
//        StringRequest strReq = new StringRequest(Request.Method.POST,
//                AppConfig.URL_MAKE_RESUME, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                //Log.e("make_resume", ": " + response);
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    String success = jObj.getString("success");
//                    if (success.equalsIgnoreCase("true")) {
//                        JSONObject c = jObj.getJSONObject("data");
//                        session.setResumeId(c.getInt("id")+"");
//                        Resume resume = new Resume(context);
//                        resume.get_resume_api();
//                    }
//                } catch (JSONException e) {
//                    //Log.d("TAG", "error 1, creating first resume");
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //Log.e("TAG",  "error 2,creating first resume " + " " + error.getMessage());
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("auth",session.getUserDetails().getToken() );
//                if(Language.equals("fa")){
//                    params.put("Resume[job_title]", "عنوان شغلی");
//                    params.put("Resume[about_me]","درباره من");
//                }
//                else{
//                    params.put("Resume[job_title]", "Job title");
//                    params.put("Resume[about_me]","About me");
//                }
//                params.put("Resume[job_status]","1");
//                params.put("Resume[provinceId] ","1");
//                params.put("Resume[year_birth]", "0000");
//                params.put("Resume[marital]", "1");
//                params.put("Resume[sex]","1");
//                params.put("Resume[levels]","1");
//                params.put("Resume[slug]",myrandom());
//                params.put("categories","1");
//                params.put("Resume[salary]","1");
//                return params;
//            }
//
//
//            //basic auth
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String,String> headers = new HashMap<String, String>();
//                // add headers <key,value>
//                String credentials = AppConfig.AUTH_USERNAME+":"+AppConfig.AUTH_PASS;
//                String auth = "Basic "
//                        + Base64.encodeToString(credentials.getBytes(),
//                        Base64.URL_SAFE|Base64.NO_WRAP);
//                headers.put("Authorization", auth);
//                return headers;
//            }
//
//        };
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
//    }
//
//    public static String myrandom(){
//        Date date = new Date();
//        return date.getYear()+""+date.getMonth()+""+date.getDay()+""+date.getMinutes()+""+date.getSeconds()+""+(int)(Math.random()*6);
//    }
}
