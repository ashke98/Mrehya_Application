package com.mrehya.UserAccount;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.mrehya.ApiHelpers.ApiHelpers;
import com.mrehya.AppConfig;
import com.mrehya.AppController;
import com.mrehya.DashboardPackage.HttpsTrustManager;
import com.mrehya.Helper.Alert;
import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.Helper.VolleyHandler;
import com.mrehya.MainActivity;
import com.mrehya.R;
import com.mrehya.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Login extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {
    private AppCompatEditText txtEmail  , txtPassword;
    private RelativeLayout relativeLayout , activity_login;
    private TextInputLayout emailTextInputLayout , passwordTextInputLayout;
    private Button btnToRegister , btnLogin , btnToChangePassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton btnSignIn;
    private ProgressDialog mProgressDialog;

    private static final int RC_SIGN_IN = 007;
    //new
    private String Language;
    private Alert alert;
    private VolleyHandler vh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        PolicyGuard.Allow();
        setViews();
        btnSignIn.setOnClickListener(this);

        setTextViewAttr();
        setButtonOnClick();
        session = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        //pDialog.setCancelable(false);

        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        //new
        Paper.init(this);
        Language=updateLanguage();
        updateView(Language);
        View v = (View) findViewById(R.id.activity_signup);
        vh = new VolleyHandler(this, Language, v);
        alert = new Alert(v, this, Language);
    }
    private void setViews(){
        btnToChangePassword = (Button) findViewById(R.id.btnToChangePassword);
        emailTextInputLayout = (TextInputLayout) findViewById(R.id.emailTextInputLayout);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.passwordTextInputLayout);
        txtEmail = (AppCompatEditText) findViewById(R.id.txtEmail);
        txtPassword = (AppCompatEditText) findViewById(R.id.txtPassword);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_login);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnToRegister = (Button) findViewById(R.id.btnToRegister);
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in_google);
    }
    private void setTextViewAttr(){
        relativeLayout.setOnClickListener(null);
        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (txtEmail.getText().toString().isEmpty() && !hasFocus){
                    emailTextInputLayout.setErrorEnabled(false);
                }
            }
        });
        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txtEmail.getText().toString().isEmpty()) {
                    emailTextInputLayout.setErrorEnabled(false);
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
        passwordTextInputLayout.setCounterEnabled(true);
        passwordTextInputLayout.setCounterMaxLength(20);
    }
    private void setButtonOnClick(){
        btnToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this , Signup.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtPassword.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);


                if (txtEmail.getText().toString().isEmpty()){
                    emailTextInputLayout.setErrorEnabled(true);
                    if(Language.equals("fa"))
                        emailTextInputLayout.setError("لطفا نام کاربری را وارد نمایید.");
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
                if (!txtEmail.getText().toString().isEmpty() && !txtPassword.getText().toString().isEmpty()){
                    startDialog();
                    new LoginTask().execute(0);
                }
            }
        });
    }


    //Methods
    private void checkLogin(final String email , final String password){
        ApiHelpers ah = new ApiHelpers(this);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", email)
                .addFormDataPart("password", password)
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
                                    alert.show("خطا در ورود", "مشکلی در اتصال با سرور پیش آمده است");
                                } else {
                                    alert.show("Signing in Error", "Network Connection or Server failed!");
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
                                            get_credentials(data.getString("token"), data.getString("username"), password);
                                            Intent intent = new Intent(Login.this,
                                                    MainActivity.class);
                                            message = true;
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // Error in login. Get the error message
                                            if (response != null) {
                                                if (Language.equals("fa")) {
                                                    vh.AlertResponse(response, "خطا در ورود");
                                                } else {
                                                    vh.AlertResponse(response, "Signing Error");
                                                }
                                                message = true;
                                            } else {
                                                if (Language.equals("fa")) {
                                                    alert.show("خطا در ورود", "مشکلی در اتصال با سرور پیش آمده است");
                                                } else {
                                                    alert.show("Signing in Error", "Network Connection or Server failed!");
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
                                                vh.AlertResponse(response, "خطا در ورود");
                                            } else {
                                                vh.AlertResponse(response, "Signing Error");
                                            }
                                            message = true;
                                        } else {
                                            if (Language.equals("fa")) {
                                                alert.show("خطا در ورود", "مشکلی در اتصال با سرور پیش آمده است");
                                            } else {
                                                alert.show("Signing Error", "Network Connection or Server failed!");
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
    class LoginTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep(2000);
                // some long running task will run here. We are using sleep as a dummy to delay execution
                checkLogin(txtEmail.getText().toString(), txtPassword.getText().toString());
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            // do whatever needs to be done next
            //Toast.makeText(Login.this, "Connecting to server...", Toast.LENGTH_SHORT).show();
        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_sign_in_google:
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(this, this)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();
                OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
                if (opr.isDone()) {
                    // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                    // and the GoogleSignInResult will be available instantly.
                    GoogleSignInResult result = opr.get();
                    handleSignInResult(result);
                } else {
                    // If the user has not previously signed in on this device or the sign-in has expired,
                    // this asynchronous branch will attempt to sign in the user silently.  Cross-device
                    // single sign-on will occur in this branch.
                    showProgressDialog();
                    opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                        @Override
                        public void onResult(GoogleSignInResult googleSignInResult) {
                            hideProgressDialog();
                            handleSignInResult(googleSignInResult);
                        }
                    });
                }
                signIn();
                break;
        }
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(Login.this , LoginOrSignup.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
    }
    @Override
    public void onStart() {
        super.onStart();

    }

    //VIA GOOGLE API
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();


            String personName = acct.getDisplayName();
            String personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();


            txtEmail.setText(email);
            /*Glide.with(getApplicationContext()).load(personPhotoUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfilePic);*/

        } else {
            // Signed out, show unauthenticated UI.
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    //others
    private void startDialog(){
        pDialog.setCancelable(false);
        if(Language.equals("fa"))
            pDialog.setMessage("در حال ورود...");
        else
            pDialog.setMessage("Logging in...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        showDialog();
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("در حال ارتباط با سرور...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
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
            Loginparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            Loginparams.addRule(RelativeLayout.BELOW, R.id.passwordTextInputLayout);
            Loginparams.setMargins(0,25,32,0);

            Registerparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            Registerparams.addRule(RelativeLayout.BELOW, R.id.passwordTextInputLayout);
            Registerparams.setMargins(32,25,0,0);
        }
        else{
                Registerparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                Registerparams.addRule(RelativeLayout.BELOW, R.id.passwordTextInputLayout);
                Registerparams.setMargins(0,25,32,0);

                Loginparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                Loginparams.addRule(RelativeLayout.BELOW, R.id.passwordTextInputLayout);
                Loginparams.setMargins(32,25,0,0);
        }
        btnLogin.setLayoutParams(Loginparams);
        btnToRegister.setLayoutParams(Registerparams);
        emailTextInputLayout.setHint(resources.getString(R.string.EmailAddressTitle));
        passwordTextInputLayout.setHint(resources.getString(R.string.Password));
        btnToRegister.setText(resources.getString(R.string.Signup));
        btnToChangePassword.setText(resources.getString(R.string.ForgotPassword));
        btnLogin.setText(resources.getString(R.string.Login));
    }
}
