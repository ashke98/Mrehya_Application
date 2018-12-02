package com.mrehya.Helper;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.mrehya.Lang;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.ibm.icu.impl.locale.XCldrStub.FileUtilities.UTF8;

/**
 * Created by hgjhghgjh on 10/18/2018.
 */

public class VolleyHandler {
    private Context context;
    private String Language="fa", Header="Message";
    private View view;

    public VolleyHandler(Context context){
        this.context = context;
    }

    public VolleyHandler(Context context, String Language){
        this.context = context;
        this.Language = Language;
    }

    public VolleyHandler(Context context, String Language, View view){
        this.context = context;
        this.Language = Language;
        this.view = view;
    }

    public void AlertResponse(okhttp3.Response response, String Header){
        Alert alert = new Alert(view, context, Language);
        String json = null;
        String dataString = null;
        if(response!=null){
            try {
                dataString = new JSONObject(response.body().string().toString()).getString("data");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            switch (response.code()){
                case 400:
                    json = trimMessage(dataString, "message");
                    alert.show(Header, dataString);
                    break;
                case 403:
                    if(Language.equals("fa")){
                        json="ورودی ها اشتباه می باشد";
                    }
                    else{
                        json = "Wrong credentials";
                    }
                    alert.show(Header, json);
                    break;
                case 406:
                    if(dataString!=null){
                        Log.e("data", dataString);
                        if(dataString.contains("email")){
                            if(Language.equals("fa")){
                                json="با این ایمیل از قبل ثبت نام شده است";
                            }
                            else{
                                json = "This email is already taken!";
                            }
                        }
                        else{
                            json = dataString;
                        }
                    }
                    else{
                        json = dataString;
                    }
                    alert.show(Header, json);
                    break;
            }
        }

    }

    public void AlertVolley(VolleyError error, String Header){

        Alert alert = new Alert(view, context, Language);

        Log.e("Volley","here");

        String json = null;
        NetworkResponse response = error.networkResponse;
        if(response!=null && response.data!=null){
            switch (response.statusCode){
                case 400:
                    json = new String (response.data, UTF8);
                    json = trimMessage(json, "message");
                    alert.show(Header, json);
                    break;
                case 403:
                    if(Language.equals("fa")){
                        json="ورودی ها اشتباه می باشد";
                    }
                    else{
                        json = "Wrong credentials";
                    }
                    alert.show(Header, json);
                    break;
                case 406:
                    String txtResponse =  new String (response.data, UTF8);
                    JSONObject jObj=null;
                    try {
                         jObj = new JSONObject(txtResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(jObj!=null){
                        JSONObject data = null;
                        try {
                            data = jObj.getJSONObject("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("data", data.toString());
                        if(data!=null){
                            if(data.toString().contains("email")){
                                if(Language.equals("fa")){
                                    json="با این ایمیل از قبل ثبت نام شده است";
                                }
                                else{
                                    json = "This email is already taken!";
                                }
                            }
                            else{
                                json = new String (response.data, UTF8);
                            }
                        }
                        else{
                            json = new String (response.data, UTF8);
                        }
                    }
                    else{
                        json = new String (response.data, UTF8);
                    }
                    alert.show(Header, json);
                    break;
            }
        }
    }

    public void handleVolley(VolleyError error){
        String json = null;
        NetworkResponse response = error.networkResponse;
        if(response!=null && response.data!=null){
            switch (response.statusCode){
                case 400:
                    json = new String (response.data, UTF8);
                    json = trimMessage(json, "message");
                    if(json!=null) displayMessage(json);
                    break;
                case 406:
                    json = new String (response.data, UTF8);
                    json = trimMessage(json, "email");
                    if(json!=null) displayMessage(json);
                    break;
            }
        }
    }
    public String trimMessage(String json, String key){
        String trimmedString=null;

        try{
            JSONObject obj = new JSONObject(json);
            Log.e("JSON", obj.toString());
            if(obj.has("email")){
                Log.e("JSON", "has email");
            }
            trimmedString = obj.getString(key);
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return trimmedString;
    }

    public void displayMessage(String message){
        Log.e("JSON", message);
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL,1,1);
        toast.show();
    }

}
