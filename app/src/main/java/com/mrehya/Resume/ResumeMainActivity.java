package com.mrehya.Resume;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.mrehya.ApiHelpers.ApiHelpers;
import com.mrehya.AppConfig;
import com.mrehya.AppController;
import com.mrehya.DashboardPackage.HttpsTrustManager;
import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.UserAccount.Login;
import com.mrehya.MainActivity;
import com.mrehya.R;
import com.mrehya.SessionManager;
import com.mrehya.UserAccount.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ResumeMainActivity extends AppCompatActivity {
    //GLOBALS
    private String Language;
    private SessionManager session;
    private Resources resources;
    private ProgressDialog pDialog;
    private Context context;
    //ELEMENTS
    private Button btn_UserInfo, btn_UserActs, btn_UserAboutMe,
    btn_UserBenefits, btn_ShowResume, btn_gotologin, btn_UserLanguages,
            btn_UserDegrees, btn_UserJobexp, btn_Back;

    private TextView txtNotloggedin;

    private LinearLayout LinearLayoutLogged, LinearLayoutNotlogged, LinearLayoutspin_kit;
    private String[] urlArray = null;

    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_main);
        PolicyGuard.Allow();
        session = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        context = this.getApplicationContext();
        updateLanguage();
        findviews();
        setOnclicks();
        updateView();
        User u = new User(this);
        u.checkLogin(session.getUserDetails().getEmail(), session.getUserDetails().getPassword());
    }
    public void findviews(){

        progressbar = (ProgressBar)findViewById(R.id.spin_kit);
        FoldingCube fc = new FoldingCube();
        progressbar.setIndeterminateDrawable(fc);

        btn_UserInfo = (Button) findViewById(R.id.btn_UserInfo);
        btn_UserActs = (Button) findViewById(R.id.btn_UserActs);
        btn_UserAboutMe = (Button) findViewById(R.id.btn_UserAboutMe);
        btn_UserBenefits = (Button) findViewById(R.id.btn_UserBenefits);
        btn_ShowResume = (Button) findViewById(R.id.btn_ShowResume);
        btn_UserLanguages = (Button) findViewById(R.id.btn_UserLanguages);
        btn_UserDegrees = (Button) findViewById(R.id.btn_UserDegrees);
        btn_UserJobexp = (Button) findViewById(R.id.btn_UserJobexp);
        btn_gotologin = (Button) findViewById(R.id.btn_gotologin);
        btn_Back = (Button) findViewById(R.id.btn_Back);
        txtNotloggedin = (TextView) findViewById(R.id.txtNotloggedin);
        LinearLayoutLogged = (LinearLayout) findViewById(R.id.LinearLayoutLogged);
        LinearLayoutNotlogged = (LinearLayout) findViewById(R.id.LinearLayoutNotlogged);
        LinearLayoutspin_kit = (LinearLayout) findViewById(R.id.LinearLayoutspin_kit);
        JobCats_Api();
        if(session.isLoggedIn()){
            Showgif();
            //Log.e("ISLOGED IN FOR RESUME", "WE ARE IN");
            if(session.getUserDetails().getResume()==null || session.getUserDetails().getResume()=="0")
            {
                //Log.e("NO resmue", session.getUserDetails().getResume());
                make_resume(session, Language, this);
            }
            else
            {
                //Log.e("Has resmue", session.getUserDetails().getResume());
                 get_resume_api();
            }
        }
        else{
            LinearLayoutLogged.setVisibility(View.GONE);
            LinearLayoutNotlogged.setVisibility(View.VISIBLE);
        }
    }
    public void setOnclicks(){

        btn_UserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ResumeUserInfoActivity.class);
                intent.putExtra("Language", Language);
                startActivity(intent);
            }
        });

        btn_UserActs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ResumeActivitiesActivity.class);
                intent.putExtra("Language", Language);
                startActivity(intent);
            }
        });

        btn_gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), Login.class);
                intent.putExtra("Language", Language);
                startActivity(intent);
            }
        });

        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                intent.putExtra("frgToLoad", "0");
                startActivity(intent);
            }
        });

        btn_UserLanguages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ResumeLanguagesActivity.class);
                intent.putExtra("Language", Language);
                startActivity(intent);
            }
        });

        btn_UserJobexp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ResumeJobexpActivity.class);
                intent.putExtra("Language", Language);
                startActivity(intent);
            }
        });

        btn_UserDegrees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ResumeEducationActivity.class);
                intent.putExtra("Language", Language);
                startActivity(intent);
            }
        });

        btn_UserBenefits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ResumeBenefitsActivity.class);
                intent.putExtra("Language", Language);
                startActivity(intent);
            }
        });

        btn_ShowResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ResumeViewActivity.class);
                intent.putExtra("Language", Language);
                startActivity(intent);
            }
        });

        btn_UserAboutMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogEditAbout();
            }
        });
    }
    private String updateLanguage(){
        //Default language is fa
        Language = Paper.book().read("language");
        if(Language==null)
            Paper.book().write("language", "fa");
        return Language;
    }
    private void updateView() {
        Context context = LocaleHelper.setLocale(getApplicationContext(), Language);
        resources = context.getResources();
        updateView_4btn();
    }
    private void updateView_4btn(){
        if(Language.equals("fa")){

            //TEXTS
            btn_UserInfo.setText("اطلاعات کاربری من");
            btn_UserActs.setText("فعالیت ها و علاقه مندی\u200Cهای شغلی من");
            btn_UserAboutMe.setText("درباره من");
            btn_UserBenefits.setText("مزایای شغلی مورد نظرم");
            btn_ShowResume.setText("مشاهده رزومه");
            btn_gotologin.setText("ورود یا ثبت نام");
            btn_UserLanguages.setText("زبان ها");
            btn_UserDegrees.setText("سوابق تحصیلی");
            btn_UserJobexp.setText("سوابق شغلی");
            txtNotloggedin.setText("برای ایجاد رزومه باید لاگین کنید");
            btn_Back.setText("برگشت");
        }
        else{
            //TEXTS
            btn_UserInfo.setText("User Private Information");
            btn_UserActs.setText("User Activities");
            btn_UserAboutMe.setText("About Me");
            btn_UserBenefits.setText("Desired Job Benefits");
            btn_ShowResume.setText("View Total Resume");
            btn_gotologin.setText("Login/Regsiter");
            btn_UserLanguages.setText("Languages");
            btn_UserDegrees.setText("Educational records");
            btn_UserJobexp.setText("Work experiences");
            txtNotloggedin.setText("You need to login in order to build resume");
            btn_Back.setText("Back to list");
        }
    }
    private void save(String aboutme){
        Showgif();
        if(Language.equals("fa")){
            Toast.makeText(this, "در حال ارتباط با سرور...", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Connecting to server...", Toast.LENGTH_SHORT).show();
        }
        String result = session.set_Resume_Aboutme(aboutme);
        if(result.equals("ok")){
            if(urlArray==null)
            {
                urlArray = new String[1];
                urlArray[0]="";
            }
            Resume resume = new Resume(this,
                    Arrays.asList(resources.getStringArray(R.array.Province)),
                    Arrays.asList(urlArray),
                    Arrays.asList(resources.getStringArray(R.array.contract_arrays)),
                    Arrays.asList(resources.getStringArray(R.array.Senority_arrays)));
            resume.set_resume_api();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Dissmissgif();
            }
        }, 4000);
    }
    //methods
    private void showDialogEditAbout(){
        final Dialog dialog = new Dialog(ResumeMainActivity.this);
        dialog.setContentView(R.layout.list_dialog_about);
        final EditText txtEditDialogAbout = dialog.findViewById(R.id.txtEditDialogAbout);
        final Button btn_back, btn_saveresume;
        //txtEditDialogAbout.setText(oldAbout);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        TextView btnEditd = dialog.findViewById(R.id.btnEditd);
        btn_back = dialog.findViewById(R.id.btn_back);
        btn_saveresume = dialog.findViewById(R.id.btn_saveresume);
        if(Language.equals("fa"))
        {
            btn_back.setText("برگشت");
            btn_saveresume.setText("ذخیره");
            btnEditd.setText("ویرایش");
        }
        else{
            btn_back.setText("Back");
            btn_saveresume.setText("Save");
            btnEditd.setText("Edit");
        }
        try {
            txtEditDialogAbout.setText(session.get_Resume().getAbout_me());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btn_saveresume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(txtEditDialogAbout.getText().toString());
                //txtAboutMe.setText(txtEditDialogAbout.getText());
                dialog.dismiss();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //txtAboutMe.setText(txtEditDialogAbout.getText());
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout((13 * width)/14, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void Showgif(){
        LinearLayoutLogged.setVisibility(View.GONE);
        LinearLayoutNotlogged.setVisibility(View.GONE);
        LinearLayoutspin_kit.setVisibility(View.VISIBLE);
    }
    private void Dissmissgif(){
        LinearLayoutNotlogged.setVisibility(View.GONE);
        LinearLayoutspin_kit.setVisibility(View.GONE);
        LinearLayoutLogged.setVisibility(View.VISIBLE);
    }

    //APIS
    //LOAD RESUME
    //get resume from cloud
    public void get_resume_api(){
        ApiHelpers ah = new ApiHelpers(this);
        ah.getCall(AppConfig.URL_GETAPI_RESUME + session.getUserDetails().getResume(), false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                Log.e("Resume_Get", e.getMessage());
                                hideDialog();
                            }
                        });
                    }
                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                if (response.isSuccessful()) {
                                    try {
                                        if(response.code() == 200) {
                                            String DataString = response.body().string();
                                            JSONObject json = new JSONObject(DataString);
                                            JSONObject data = new JSONObject(DataString).getJSONObject("data");
                                            String success = json.getString("success");

                                            if (success.equalsIgnoreCase("true")) {

                                                session.setResumeId(data.getString("id"));

                                                //ADD SKILLS TO STRING WITH ,
                                                String skills = "";
                                                JSONArray Skilljsarray = new JSONArray(data.getString("skills"));
                                                for(int i=0;i<Skilljsarray.length();i++){
                                                    skills+=Skilljsarray.getString(i)+",";
                                                }

                                                session.set_Resume(
                                                        data.getString("id"),
                                                        data.getString("email"),
                                                        data.getString("job_title"),// c.getString("address"),
                                                        data.getString("job_status"), "0",
                                                        data.getString("province"),
                                                        data.getString("martial"),// c.getString("military_status"),
                                                        data.getString("about_me"),
                                                        data.getString("image"),
                                                        data.getString("fullname"),
                                                        data.getString("link"),
                                                        data.getString("sex"),
                                                        data.getString("levels"),
                                                        data.getString("salary"),
                                                        data.getString("categories"),
                                                        data.getString("benefits"),
                                                        data.getString("provinces"),
                                                        data.getString("contracts"), skills!=""? skills.substring(0, skills.length() - 1):null ,
                                                        data.getString("experiences"),
                                                        data.getString("academics"),
                                                        data.getString("languages"),
                                                        data.getString("mobile"),
                                                        data.getString("year_birth"),
                                                        data.getString("sex")
                                                );
                                                session.setLoaded(true);
                                            } else {
                                                Log.e("get_resume_api", "failed to get resume trycatch");
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    hideDialog();
                                }
                                else{
                                    //not success response
                                    Log.e("get_resume_api", "failed to get resume trycatch");
                                }
                                //after run
                                hideDialog();
                            }
                        });
                    }
                }
        );
    }
    //DAFAULTS RESUME
    private RequestBody requestbody() {
            String job_title = "";
            String aboutme = "";
            if(Language.equals("fa")){
                job_title =  "عنوان شغلی";
                aboutme =  "درباره من";
            }
            else{
                job_title =  "Job title";
                aboutme =  "About me";
            }
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("auth", session.getUserDetails().getToken())
                    //part1
                    .addFormDataPart("Resume[job_title]", job_title)
                    .addFormDataPart("Resume[job_status]", "1")
                    .addFormDataPart("Resume[year_birth]", "0000")
                    .addFormDataPart("Resume[provinceId]", "1")
                    .addFormDataPart("Resume[martial]", "1")
                    .addFormDataPart("Resume[sex]", "1")
                    .addFormDataPart("categories", "1")
                    //part2
                    .addFormDataPart("Resume[levels]", "1")
                    //part3
                    .addFormDataPart("Resume[slug]", myrandom())
                    .addFormDataPart("Resume[salary]", "1")
                    .addFormDataPart("Resume[about_me]", aboutme)
                    .build();
            return requestBody;
    }
    private void make_resume(final SessionManager session, final String Language, final Context context){
        ApiHelpers ah = new ApiHelpers(this);
        RequestBody requestBody = requestbody();

        if(requestBody != null){
            ah.PosttCall(AppConfig.URL_MAKE_RESUME, requestBody, false).enqueue(
                    new Callback() {
                        Handler mainHandler = new Handler(context.getMainLooper());
                        @Override
                        public void onFailure(Call call, final IOException e) {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("make_resume", e.getMessage());
                                    hideDialog();
                                }
                            });
                        }
                        @Override
                        public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                            mainHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    String DataString = null;
                                    try {
                                        DataString = response.body().string();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Log.e("make_resume", DataString);
                                    if (response.isSuccessful()) {
                                        try {
                                            JSONObject json = new JSONObject(DataString);
                                            String success = json.getString("success");
                                            if (success.equalsIgnoreCase("true")) {
                                                JSONObject data = new JSONObject(json.getString("data"));
                                                session.setResumeId(data.getInt("id")+"");

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Resume resume = new Resume(context);
                                                        resume.setLoaded(false);
                                                        resume.get_resume_api();
                                                        SessionManager sessionManager = new SessionManager(getApplicationContext());
                                                        hideDialog();
                                                    }
                                                }, 6000);
                                                hideDialog();
                                            }
                                        } catch (JSONException e) {
                                            Log.e("make_resume", "failed to make_resume");
                                            e.printStackTrace();
                                            hideDialog();
                                        }
                                        hideDialog();
                                    }
                                    else {
                                        Log.e("make_resume", "failed to make_resume");
                                        hideDialog();
                                    }
                                    hideDialog();
                                }
                            });
                        }
                    });
        }

    }
    private void JobCats_Api(){
        ApiHelpers ah = new ApiHelpers(this);
        ah.getCall(AppConfig.URL_JobCats+Language, false).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("JobCats", "error");
                    }
                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                String DataString = response.body().string();
                                JSONObject json = new JSONObject(DataString);
                                JSONArray data = json.getJSONArray("data");
                                String success = json.getString("success");
                                if(data.length()>0){
                                    urlArray = new String[data.length()];
                                    //txtEmptyHires.setVisibility(View.GONE);
                                    for (int i = 0; i < data.length(); i++) {
                                        urlArray[data.length()-i-1] = data.getJSONObject(i).getString("title");
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void startDialog(){
        pDialog.setCancelable(false);
//        if(Language.equals("fa"))
//            pDialog.setMessage("بارگزاری رزومه(نخستین مراجعه)");
//        else
//            pDialog.setMessage("Logging in...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        showDialog();
    }
    private void showDialog() {
        startDialog();
//        if (!pDialog.isShowing())
//            pDialog.show();
    }
    private void hideDialog() {
        Dissmissgif();
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    public static String myrandom(){
        Date date = new Date();
        return date.getYear()+""+date.getMonth()+""+date.getDay()+""+date.getMinutes()+""+date.getSeconds()+""+(int)(Math.random()*6);
    }
    /////CALL BACKS
    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        if(Language.equals("fa")){
            Toast.makeText(this, "برای خروج دکمه بازگشت را دو بار کلیک کنید", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        }


        mHandler.postDelayed(mRunnable, 2000);
    }


}
