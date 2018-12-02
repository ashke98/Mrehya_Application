package com.mrehya.UserAccount;

import android.content.Context;
import android.content.Intent;
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
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 06/03/2018.
 */

public class User {
    private SessionManager session;

    int id,password_change;
    String firstname,lastname;
    String password;
    String image;
    String phone;
    String mobile;
    String address;
    String token;
    String email;
    String resume;
    String exams = "";

    public String getExams() {
        return exams;
    }

    public void setExams(String exams) {
        this.exams = exams;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    String zip;

    public User(Context context){
        session = new SessionManager(context);
    }

    public User(int id, String firstname,String lastname, String password, String image) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.image = image;
        //this.token = token;
    }

    public User(int id, String firstname,String lastname, String email) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public User(int id, int password_change, String firstname,String lastname, String password, String image) {

        this.id = id;
        this.password_change = password_change;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.image = image;
        //this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPassword_change() {
        return password_change;
    }

    public void setPassword_change(int password_change) {
        this.password_change = password_change;
    }


    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getResume() {
        return resume;
    }


    public void checkLogin (final String email , final String password){
        PolicyGuard.Allow();
        String tag_string_req = "req_login";
        HttpsTrustManager.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SIGNIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String success = jObj.getString("success");

                    // Check for error node in json
                    if (success.equalsIgnoreCase("true")) {
                        // user successfully logged in
                        JSONObject c = jObj.getJSONObject("data");
                        get_credentials(c.getString("token"), c.getString("username"), password);
                    } else {
                        Log.d("USER UPDATE LOGIN", "NOT TRUE SUCCESS");
                    }
                } catch (JSONException e) {
                    // JSON error
                    Log.d("TAG", "error 1 ");
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG",  "Error 2: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                params.put("type", "1");
                return params;
            }
            //basic auth
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<String, String>();
                // add headers <key,value>
                String credentials = AppConfig.AUTH_USERNAME+":"+AppConfig.AUTH_PASS;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.URL_SAFE|Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void get_credentials(final String token, final String email, final String passwordd){
        PolicyGuard.Allow();
        String tag_string_req = "req_login";
        Log.e("TOKEN" , token);
        HttpsTrustManager.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_VIEW_Profile, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d("TAG", "View Profile Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String success = jObj.getString("success");
                    // Check for error node in json
                    if (success.equalsIgnoreCase("true")) {
                        JSONObject c = jObj.getJSONObject("data");
                        int id =0;
                        String firstname=" ",lastname=" " , email=" ", phone= " ", token=" ",
                                image =" ",mobile=" ",address=" ", zip=" ",password=" ",resume=" ";
                        id = c.getInt("id");
                        firstname = c.getString("first_name");
                        lastname = c.getString("last_name");
                        email = c.getString("email");
                        token = c.getString("auth_key");
                        phone = c.getString("tel_number");
                        image = c.getString("avatar");
                        mobile = c.getString("mobile");
                        address = c.getString("address");
                        zip = c.getString("postal_code");
                        if(c.has("resumeId"))
                            resume =c.getInt("resumeId")+"";
                        else
                            resume =null;
                        password = passwordd;
                        session.setLogin(true);
                        session.setUserDetails(id,firstname,lastname,email,phone,token, image,mobile,address,zip,password,resume);

                        Log.d("token", token);
                    } else {
                        Log.d("TAG", "failed to get user");
                    }
                } catch (JSONException e) {
                    // JSON error
                    Log.d("TAG", "error 1 SETTING CREDENTIALS IN USER" + e.getMessage());
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG",  "error2  SETTING CREDENTIALS IN USER: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("auth", token);

                return params;
            }


            //basic auth
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<String, String>();
                // add headers <key,value>
                String credentials = AppConfig.AUTH_USERNAME+":"+AppConfig.AUTH_PASS;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.URL_SAFE|Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
