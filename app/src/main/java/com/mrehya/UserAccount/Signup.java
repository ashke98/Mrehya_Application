package com.mrehya.UserAccount;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mrehya.ApiHelpers.ApiHelpers;
import com.mrehya.AppConfig;
import com.mrehya.AppController;
import com.mrehya.DashboardPackage.HttpsTrustManager;
import com.mrehya.Helper.Alert;
import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.Helper.VolleyHandler;
import com.mrehya.Lang;
import com.mrehya.MainActivity;
import com.mrehya.R;
import com.mrehya.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.ibm.icu.impl.locale.XCldrStub.FileUtilities.UTF8;

public class Signup extends AppCompatActivity {
    AppCompatEditText  txtPassword , txtPasswordAgain,txtFirstname,txtLastname,txtMobile,txtEmail,txtPhone;
    RelativeLayout relativeLayout ;
    TextInputLayout firstnameTextInputLayout,lastnameTextInputLayout,emailTextInputLayout , passwordTextInputLayout , passwordTextInputLayoutAgain;
    Button btnToLogin , btnRegister;
    private ProgressDialog pDialog;
    private SessionManager session;
    private String USERNAME="auth_key";
    private String PASSWORD="";
    private VolleyHandler vh;
    private Alert alert;
    //new
    String Language;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        PolicyGuard.Allow();
        setViews();
        setTextViewsAtrr();
        setButtonsOnClick();
        session = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        //new
        Paper.init(this);
        Language=updateLanguage();
        updateView(Language);
        View v = (View) findViewById(R.id.activity_signup);
        vh = new VolleyHandler(this, Language, v);
        alert = new Alert(v, this, Language);
    }
    private void setViews(){
        lastnameTextInputLayout = (TextInputLayout) findViewById(R.id.lastnameTextInputLayout);
        firstnameTextInputLayout = (TextInputLayout) findViewById(R.id.firstnameTextInputLayout);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.passwordTextInputLayout);
        emailTextInputLayout = (TextInputLayout) findViewById(R.id.emailTextInputLayout);
        passwordTextInputLayoutAgain = (TextInputLayout) findViewById(R.id.passwordTextInputLayoutAgain);
        txtPassword = (AppCompatEditText) findViewById(R.id.txtPassword);
        txtPasswordAgain = (AppCompatEditText) findViewById(R.id.txtPasswordAgain);
        txtFirstname = (AppCompatEditText) findViewById(R.id.txtFirstname);
        txtLastname = (AppCompatEditText) findViewById(R.id.txtLastname);
        txtMobile = (AppCompatEditText) findViewById(R.id.txtMobile);
        txtEmail = (AppCompatEditText) findViewById(R.id.txtEmailLogin);
        txtPhone = (AppCompatEditText) findViewById(R.id.txtPhone);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_signup);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnToLogin = (Button) findViewById(R.id.btnToLogin);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtPasswordAgain.getWindowToken(), 0);
    }
    private void setTextViewsAtrr(){
        relativeLayout.setOnClickListener(null);

        passwordTextInputLayout.setCounterEnabled(true);
        passwordTextInputLayout.setCounterMaxLength(20);
        passwordTextInputLayoutAgain.setCounterEnabled(true);
        passwordTextInputLayoutAgain.setCounterMaxLength(20);
        txtFirstname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (txtFirstname.getText().toString().isEmpty() && !hasFocus){
                    firstnameTextInputLayout.setErrorEnabled(false);
                }
            }
        });
        txtFirstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txtFirstname.getText().toString().isEmpty()) {
                    firstnameTextInputLayout.setErrorEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (txtPassword.getText().toString().isEmpty() && !hasFocus){
                    passwordTextInputLayout.setErrorEnabled(false);
                }
            }
        });
        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txtPassword.getText().toString().isEmpty()) {
                    passwordTextInputLayout.setErrorEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        txtPasswordAgain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equalsIgnoreCase(txtPassword.getText().toString())){
                    passwordTextInputLayoutAgain.setErrorEnabled(false);
                    passwordTextInputLayoutAgain.setHintTextAppearance(R.style.FlatButton);
                }else{
                    passwordTextInputLayoutAgain.setErrorEnabled(true);
                    passwordTextInputLayoutAgain.setError(" ");
                    passwordTextInputLayoutAgain.setHintTextAppearance(R.style.CharacterOverflow);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txtPasswordAgain.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && txtPasswordAgain.getText().toString().isEmpty()){
                    passwordTextInputLayoutAgain.setErrorEnabled(false);
                }
            }
        });
    }
    private void setButtonsOnClick(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtPasswordAgain.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                if (txtFirstname.getText().toString().isEmpty()){
                    firstnameTextInputLayout.setErrorEnabled(true);
                    if(Language.equals("fa"))
                        firstnameTextInputLayout.setError("لطفا نام را وارد نمایید.");
                    else
                        firstnameTextInputLayout.setError("Please fill Username field.");
                }
                if (txtLastname.getText().toString().isEmpty()){
                    lastnameTextInputLayout.setErrorEnabled(true);
                    if(Language.equals("fa"))
                        lastnameTextInputLayout.setError("لطفا نام خانوادگی را وارد نمایید.");
                    else
                        lastnameTextInputLayout.setError("Please fill Username field.");
                }if (txtLastname.getText().toString().isEmpty()){
                    emailTextInputLayout.setErrorEnabled(true);
                    if(Language.equals("fa"))
                        emailTextInputLayout.setError("لطفا ایمیل را وارد نمایید.");
                    else
                        emailTextInputLayout.setError("Please fill Username field.");
                }
                if (txtPassword.getText().toString().isEmpty()){
                    passwordTextInputLayout.setErrorEnabled(true);
                    if(Language.equals("fa"))
                        passwordTextInputLayout.setError("لطفا رمز ورود را وارد نمایید.");
                    else
                        passwordTextInputLayout.setError("Please fill Password field.");

                }
                if (txtPasswordAgain.getText().toString().isEmpty()){
                    passwordTextInputLayoutAgain.setErrorEnabled(true);
                    if(Language.equals("fa"))
                        passwordTextInputLayoutAgain.setError("لطفا رمز ورود را دوباره وارد نمایید.");
                    else
                        passwordTextInputLayoutAgain.setError("Please fill Confirm Password field.");
                }
                if (!txtPassword.getText().toString().isEmpty() && !txtPasswordAgain.getText().toString().isEmpty() &&
                        !txtPasswordAgain.getText().toString().equalsIgnoreCase(txtPassword.getText().toString())){
                    passwordTextInputLayoutAgain.setErrorEnabled(true);
                    if(Language.equals("fa"))
                        passwordTextInputLayoutAgain.setError("رمزها یکسان نیستند.");
                    else
                        passwordTextInputLayoutAgain.setError("Passwords do not match!");

                }
                if (!txtLastname.getText().toString().isEmpty() && !txtEmail.getText().toString().isEmpty() && !txtFirstname.getText().toString().isEmpty() && !txtPassword.getText().toString().isEmpty() &&
                        txtPasswordAgain.getText().toString().equalsIgnoreCase(txtPassword.getText().toString())){
//                    Log.e("txtEmail",txtEmail.getText().toString());
//                    Log.e("txtFirstname",txtFirstname.getText().toString());
//                    Log.e("txtLastname",txtLastname.getText().toString());
//                    Log.e("txtPassword",txtPassword.getText().toString());
//                    Log.e("txtMobile",txtMobile.getText().toString());
//                    Log.e("txtPhone",txtPhone.getText().toString());


                    signUp(txtEmail.getText().toString(), txtFirstname.getText().toString()
                            , txtLastname.getText().toString(), txtPassword.getText().toString()
                            , txtMobile.getText().toString(), txtPhone.getText().toString());
                }
            }
        });
        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this , Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //Methods
    private void signUp(final String email, final String firstname, final String lastname, final String password, final String mobile, final String phone){
        ApiHelpers ah = new ApiHelpers(this);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("first_name", firstname)
                .addFormDataPart("last_name", lastname)
                .addFormDataPart("mobile", mobile)
                .addFormDataPart("email", email)
                .addFormDataPart("password", password)
                .addFormDataPart("tel_number", phone)
                .build();
        showDialog();
        final Context context = this.getApplicationContext();
        ah.PosttCall(AppConfig.URL_SIGNUP, requestBody, false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                hideDialog();
                                if (Language.equals("fa")) {
                                    alert.show("خطا در ثبت نام", "مشکلی در اتصال با سرور پیش آمده است");
                                } else {
                                    alert.show("Signup Error", "Network Connection or Server failed!");
                                }
                                Log.e("Signing in", e.getMessage());
                            }
                        });
                        hideDialog();
                    }
                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                Boolean message = false;
                                if (response.isSuccessful()) {
                                    LogUser(email,password);
                                } else {
                                    if(Language.equals("fa")){
                                        vh.AlertResponse(response, "خطا در ثبت نام");
                                    }

                                    else{
                                        vh.AlertResponse(response, "Signup Error");
                                    }
                                }
                                hideDialog();
                            }
                        });
                        hideDialog();
                    }
                });
    }
    private void LogUser(final String Email, final String Password) {
        final int[] responsed = {-1};
        ApiHelpers ah = new ApiHelpers(this);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", Email)
                .addFormDataPart("password", Password)
                .addFormDataPart("type", "1")
                .build();

        final Context context = this.getApplicationContext();
        ah.PosttCall(AppConfig.URL_SIGNIN, requestBody, false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                hideDialog();
                                // Error in login. Get the error message
                                if (Language.equals("fa")) {
                                    alert.show("خطا در ثبت نام", "مشکلی در اتصال با سرور پیش آمده است");
                                } else {
                                    alert.show("Signup Error", "Network Connection or Server failed!");
                                }
                                Log.e("Signing in", e.getMessage());
                            }
                        });
                    }
                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                Boolean message = false;
                                if (response.isSuccessful()) {
                                    try {
                                        if (response.code() == 200) {
                                            JSONObject data = new JSONObject(response.body().string()).getJSONObject("data");
                                            get_credentials(data.getString("token"), data.getString("username"), Password);

                                            Intent intent = new Intent(Signup.this,
                                                    MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                            hideDialog();
                                        } else {
                                            // Error in login. Get the error message
                                            if (response != null) {
                                                if (Language.equals("fa")) {
                                                    vh.AlertResponse(response, "خطا در ثبت نام");
                                                } else {
                                                    vh.AlertResponse(response, "Signup Error");
                                                }
                                                message = true;
                                            } else {
                                                if (Language.equals("fa")) {
                                                    alert.show("خطا در ثبت نام", "مشکلی در اتصال با سرور پیش آمده است");
                                                } else {
                                                    alert.show("Signup Error", "Network Connection or Server failed!");
                                                }
                                            }
                                        }
                                        hideDialog();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    hideDialog();
                                    if (!message) {
                                        if (response != null) {
                                            if (Language.equals("fa")) {
                                                vh.AlertResponse(response, "خطا در ثبت نام");
                                            } else {
                                                vh.AlertResponse(response, "Signup Error");
                                            }
                                            message = true;
                                        } else {
                                            if (Language.equals("fa")) {
                                                alert.show("خطا در ثبت نام", "مشکلی در اتصال با سرور پیش آمده است");
                                            } else {
                                                alert.show("Signup Error", "Network Connection or Server failed!");
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                });

    }
    private void get_credentials(final String token, final String email, final String passwordd){
        ApiHelpers ah = new ApiHelpers(this);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("auth", token)
                .build();
        ah.PosttCall(AppConfig.URL_VIEW_Profile, requestBody, false).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        hideDialog();
                        Log.e("get_credntials", e.getMessage());
                    }
                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                        Log.e("get_credntials", response.body().toString());
                        if (response.isSuccessful()) {
                            try {
                                Log.e("get_credntials", response.body().toString());
                                if (response.code()==200) {
                                    JSONObject data = new JSONObject(response.body().string()).getJSONObject("data");
                                    int id =0;
                                    String firstname=" ",lastname=" " , email=" ", phone= " ", token=" ",
                                            image =" ",mobile=" ",address=" ", zip=" ",password=" ",resume=" ";

                                    id =        data.getInt("id");
                                    firstname = data.getString("first_name");
                                    lastname =  data.getString("last_name");
                                    email =     data.getString("email");
                                    token =     data.getString("auth_key");
                                    phone =     data.getString("tel_number");
                                    image =     data.getString("avatar");
                                    mobile =    data.getString("mobile");
                                    address =   data.getString("address");
                                    zip =       data.getString("postal_code");
                                    if(data.has("resumeId"))
                                        resume = data.getInt("resumeId")+"";
                                    else
                                        resume =null;
                                    password = passwordd;
                                    session.setLogin(true);
                                    Log.e("TAG", "user logged");
                                    session.setUserDetails(id,firstname,lastname,email,phone,token, image,mobile,address,zip,password,resume);
                                } else {
                                    Log.d("TAG", "failed to get user");
                                }
                                hideDialog();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                hideDialog();
                            } catch (IOException e) {
                                e.printStackTrace();
                                hideDialog();
                            }
                        }
                    }
                });
    }


    private String updateLanguage(){
        //Default language is fa
        String language = Paper.book().read("language");
        if(language==null)
            Paper.book().write("language", "fa");
        return language;
    }
    private void updateView(String language) {
        Context context = LocaleHelper.setLocale(this, language);
        Resources resources = context.getResources();


        RelativeLayout.LayoutParams Loginparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams Registerparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        if(language.equals("fa")){
            Registerparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            Registerparams.addRule(RelativeLayout.BELOW, R.id.passwordTextInputLayoutAgain);
            Registerparams.setMargins(0,16,32,0);

            Loginparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            Loginparams.addRule(RelativeLayout.BELOW, R.id.passwordTextInputLayoutAgain);
            Loginparams.setMargins(32,16,0,0);
        }
        else{
            Loginparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            Loginparams.addRule(RelativeLayout.BELOW, R.id.passwordTextInputLayoutAgain);
            Loginparams.setMargins(0,16,32,0);

            Registerparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            Registerparams.addRule(RelativeLayout.BELOW, R.id.passwordTextInputLayoutAgain);
            Registerparams.setMargins(32,16,0,0);

        }
        btnToLogin.setLayoutParams(Loginparams);
        btnRegister.setLayoutParams(Registerparams);
        firstnameTextInputLayout.setHint(resources.getString(R.string.first_name));
        lastnameTextInputLayout.setHint(resources.getString(R.string.last_name));
        passwordTextInputLayout.setHint(resources.getString(R.string.Password));
        passwordTextInputLayoutAgain.setHint(resources.getString(R.string.PasswordAgain));
        btnToLogin.setText(resources.getString(R.string.LoginPage));
        btnRegister.setText(resources.getString(R.string.Submit));

    }

    //Dialogs
    private void showDialog() {
        if (!pDialog.isShowing())
        {
            pDialog.setMessage("در حال ثبت نام");
            pDialog.show();
        }

    }
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Signup.this , LoginOrSignup.class);
        startActivity(intent);
        finish();
    }
}