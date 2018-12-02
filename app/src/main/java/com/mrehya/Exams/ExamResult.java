package com.mrehya.Exams;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mrehya.ApiHelpers.ApiHelpers;
import com.mrehya.AppConfig;
import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.MainActivity;
import com.mrehya.R;
import com.mrehya.SessionManager;
import com.mrehya.UserAccount.Login;
import com.mrehya.UserAccount.checkLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.paperdb.Paper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ExamResult extends AppCompatActivity {

    private TextView tv_point, tv_answer, tv_message, tv_title,tv_points;
    private Button btn_Back;
    private ApiHelpers ah;

    private String Language="fa";
    private Context context;
    private int sum = 0;
    private Resources resources;
    private SessionManager session;
    private int examid;
    final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PolicyGuard.Allow();
        setContentView(R.layout.activity_exam_result);
        Paper.init(this);
        context = this.getApplicationContext();
        Language=updateLanguage();
        ah = new ApiHelpers(this);
        session = new SessionManager(getApplicationContext());
        if(!session.isLoggedIn()){
            Toast.makeText(this, "برای مشاهده نتیجه آزمون ها باید ورود کنید", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ExamResult.this,Login.class);
            startActivity(intent);
        }
        else{
            execute();
            if(Language.equals("fa")){
                Toast.makeText(this, "منتظر جواب سرور...", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Waiting for server response...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void findViews(){
        tv_point = (TextView) findViewById(R.id.tv_point);
        tv_answer = (TextView) findViewById(R.id.tv_answer);
        tv_message = (TextView) findViewById(R.id.tv_message);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_points = (TextView) findViewById(R.id.tv_points);
        btn_Back = (Button) findViewById(R.id.btn_Back);

        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExamResult.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void setViews(){
    }

    //APIS
    private void getExamCode(){
        ApiHelpers ah = new ApiHelpers(this);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("auth", session.getUserDetails().getToken())
                .build();

        ah.PosttCall(AppConfig.URL_Get_Exam_CODE+examid, requestBody, false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("getExamCode", e.getMessage());
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
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject response = new JSONObject(DataString);
                                        String success = response.getString("success");
                                        if (success.equalsIgnoreCase("true")) {
                                            JSONObject data = response.getJSONObject("data");
                                            //check if we got code ???
                                            if (data.has("code")) {
                                                get_show_Results(data.getString("code"));
                                            } else {
                                                get_show_Exam_notCompelted();
                                            }
                                        } else {
                                            get_show_Exam_notCompelted();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                });
    }
    private void get_show_Results(final String code){
        ApiHelpers ah = new ApiHelpers(this);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("auth", session.getUserDetails().getToken())
                .addFormDataPart("code", code)
                .addFormDataPart("lang", Language)
                .build();

        ah.PosttCall(AppConfig.URL_Get_Exam_RESULT+examid, requestBody, false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("get_show_Results", e.getMessage());
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
                                        String DataString = response.body().string();
                                        JSONObject response = new JSONObject(DataString);
                                        String success = response.getString("success");
                                        if (success.equalsIgnoreCase("true")) {
                                            JSONArray data = response.getJSONArray("data");
                                            //GET RESULTS AND SEND USER GOT THIS EXAM
                                            for(int i=0;i<data.length();i++) {
                                                JSONObject c = data.getJSONObject(i);
                                                JSONArray messages = c.getJSONArray("messages");
                                                JSONObject message = messages.getJSONObject(0);
                                                int min = message.getInt("pointMin");
                                                int max = message.getInt("pointMax");
                                                int point = sum;
                                                if(!c.isNull("point"))
                                                    point = c.getInt("point");
                                                //Log.e("points", min+","+point+","+max);
                                                if (point > min && point < max) {
                                                    //Log.e("RESULTs", "hi");
                                                    tv_title.setText(c.getString("title"));
                                                    tv_title.setVisibility(View.VISIBLE);

                                                    tv_point.setText("ارزیابی شما: " + point);
                                                    tv_point.setVisibility(View.VISIBLE);

                                                    tv_answer.setText("جواب تست: " + message.getString("resultShow"));
                                                    tv_answer.setVisibility(View.VISIBLE);

                                                    tv_message.setText(message.getString("message"));
                                                    tv_message.setVisibility(View.VISIBLE);

                                                    tv_points.setText(min +"-" + max);
                                                    tv_points.setVisibility(View.VISIBLE);

                                                    btn_Back.setVisibility(View.VISIBLE);
                                                    if (Language.equals("fa")) {
                                                        btn_Back.setText("برگشت");
                                                    } else {
                                                        btn_Back.setText("back");
                                                    }
                                                }
                                            }
                                        }
                                        else {
                                            Log.e("get_show_Results", "failed to get get_show_Results");
                                        };
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                });
    }
    class TestResultTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep(2000);
                getExamCode();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            // do whatever needs to be done next
            //Log.e("result", "Connecting to server..." );
        }
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
    private void get_show_Exam_notCompelted(){
        Log.e("Not compelted exam", "Not code found");
        tv_title.setText("نتیجه در حال بررسی است");
        tv_title.setVisibility(View.VISIBLE);

        tv_point.setText("ارزیابی شما: " + sum);
        tv_point.setVisibility(View.VISIBLE);

        tv_message.setText("منتطر اتمام آزمون شوید");
        tv_message.setVisibility(View.VISIBLE);

        btn_Back.setVisibility(View.VISIBLE);
        if (Language.equals("fa")) {
            btn_Back.setText("برگشت");
        } else {
            btn_Back.setText("back");
        }
    }

    private void execute(){
        new LoginTask().execute(0);
    }
    private class LoginTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                // some long running task will run here. We are using sleep as a dummy to delay execution
                checkLogin(session.getUserDetails().getEmail(), session.getUserDetails().getPassword());
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
    private void checkLogin(final String email , final String password){
        ApiHelpers ah = new ApiHelpers(context);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", email)
                .addFormDataPart("password", password)
                .addFormDataPart("type", "1")
                .build();
        ah.PosttCall(AppConfig.URL_SIGNIN, requestBody, false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Error in login. Get the error message
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
                                        } else {
                                            Log.e("Signing in", response.body().string());
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.e("Signing in", "error");
                                }
                            }
                        });
                    }
                });
    }
    private void get_credentials(final String token, final String email, final String passwordd) {
        ApiHelpers ah = new ApiHelpers(context);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("auth", token)
                .build();
        ah.PosttCall(AppConfig.URL_VIEW_Profile, requestBody, false).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("get_credntials", e.getMessage());
                    }
                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                        //Log.e("get_credntials", response.body().toString());
                        if (response.isSuccessful()) {
                            try {
                                //Log.e("get_credntials", response.body().toString());
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
                                    Log.e("get_credntials", "user logged");
                                    session.setUserDetails(id,firstname,lastname,email,phone,token, image,mobile,address,zip,password,resume);

                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            findViews();
                                            setViews();
                                            updateView();

                                            Intent intent = getIntent();
                                            examid = intent.getIntExtra("examId",0);
                                            if(examid!=0){
                                                new TestResultTask().execute(0);
                                            }

                                        }
                                    },2000);
                                } else {
                                    Log.d("TAG", response.body().toString());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            Log.d("get_credntials", "failed to get user");
                        }
                    }
                });
    }


    private String updateLanguage(){
        String language = Paper.book().read("language");
        if(language==null)
            Paper.book().write("language", "fa");
        return language;
    }
    private void updateView(){
        context = LocaleHelper.setLocale(this, Language);
        resources = context.getResources();
        btn_Back.setText(resources.getString(R.string.Back));
    }
}
