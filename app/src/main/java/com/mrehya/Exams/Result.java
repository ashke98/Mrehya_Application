package com.mrehya.Exams;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrehya.ApiHelpers.ApiHelpers;
import com.mrehya.AppConfig;
import com.mrehya.Helper.MyDividerItemDecoration;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.Helper.VolleyApplication;
import com.mrehya.MainActivity;
import com.mrehya.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.mrehya.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Result extends AppCompatActivity {
    private ShimmerFrameLayout mShimmerViewContainer;

    private static final String TAG = Result.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<ResultModel> cartList;
    private ResultListAdapter mAdapter;
    private Button btn_Back;
    private Context context;
    private String Language="fa";
    private int sum=-123456,ExamId;
    private Resources resources;
    private ProgressDialog pDialog;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        PolicyGuard.Allow();
        context = this;
        Paper.init(this);
        Language=updateLanguage();
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        session = new SessionManager(getApplicationContext());
        recyclerView = findViewById(R.id.recycler_view);
        cartList = new ArrayList<>();
        mAdapter = new ResultListAdapter(this, cartList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        btn_Back = (Button) findViewById(R.id.btn_Back);
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Result.this,MainActivity.class);
                startActivity(intent);
            }
        });
        if (Language.equals("fa")) {
            btn_Back.setText("برگشت");
        } else {
            btn_Back.setText("back");
        }

        //get examcode
        Intent intent = getIntent();
        ExamId = intent.getIntExtra("examId",0);
        if(intent.hasExtra("sum"))
            sum = intent.getIntExtra("sum",-123456);
        // making http call and fetching menu json
        if(Language.equals("fa"))
            Toast.makeText(context, "منتظر بمانید(بررسی جواب های شما)...", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Please Wait...", Toast.LENGTH_SHORT).show();

        if(sum != -123456){
            getExamCode();
        }else{
            execute();
        }

    }


    //APIS
    private void getExamCode(){
        //Log.e("getExamCode + sum", getCode()+"_"+sum);
        if(sum == -123456)
        {
            if(getCode()!=null){
                get_show_Results(getCode());
            }
            else{
                get_show_Results("failed");
            }
        }
        else {
            ApiHelpers ah = new ApiHelpers(this);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("auth", session.getUserDetails().getToken())
                    .build();

            ah.PosttCall(AppConfig.URL_Get_Exam_CODE+ExamId, requestBody, false).enqueue(
                    new Callback() {
                        Handler mainHandler = new Handler(context.getMainLooper());
                        @Override
                        public void onFailure(Call call, final IOException e) {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //Log.e("getExamCode", e.getMessage());
                                    get_show_Results("failed");
                                    //get_show_Exam_notCompelted();
                                    //hideaDialog();
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
                                    //Log.e("Get Exam Code", DataString);
                                    if (response.isSuccessful()) {
                                        try {
                                            JSONObject response = new JSONObject(DataString);
                                            String success = response.getString("success");
                                            if (success.equalsIgnoreCase("true")) {
                                                JSONObject data = response.getJSONObject("data");
                                                //check if we got code ???
                                                if (data.has("code")) {
                                                    get_show_Results(data.getString("code"));
                                                    updateExams(data.getString("code"));
                                                } else {
                                                    get_show_Results("failed");
                                                }
                                            } else {
                                                get_show_Results("failed");
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            get_show_Results("failed");
                                        }
                                    }
                                    else{
                                        get_show_Results("failed");
                                    }
                                }
                            });
                        }
                    });
        }
    }
    private void get_show_Results(final String code){
        final JSONArray obj = new JSONArray();
        if(code.equals("failed")){
            fetchResults(null);
        }
        else{
            //Log.e("Code", code);
            ApiHelpers ah = new ApiHelpers(this);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("auth", session.getUserDetails().getToken())
                    .addFormDataPart("code", code)
                    .addFormDataPart("lang", Language)
                    .build();

            ah.PosttCall(AppConfig.URL_Get_Exam_RESULT+ExamId, requestBody, false).enqueue(
                    new Callback() {
                        Handler mainHandler = new Handler(context.getMainLooper());
                        @Override
                        public void onFailure(Call call, final IOException e) {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    fetchResults(null);
                                    //Log.e("get_show_Results", e.getMessage());
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
                                    if(DataString==null)
                                        fetchResults(null);
                                    //Log.e("get_show_Results", DataString);
                                    if (response.isSuccessful()) {
                                        //Log.e("response.isSuccessful()", true+"");
                                        try {
                                            JSONObject response = new JSONObject(DataString);
                                            JSONArray dataArray = response.getJSONArray("data");
                                            if (dataArray.length()>0) {
                                                //Log.e("dataArray.length()", dataArray.length()+"");
                                                ////
                                                for(int i=0; i<dataArray.length();i++){
                                                    JSONObject data = (JSONObject) dataArray.get(i);

                                                    String title = data.getString("title");

                                                    String point = "";
                                                    if(!data.getString("point").equals("null"))
                                                        point = data.getInt("point")+"";
                                                    else if(sum != -123456){
                                                        point = sum+"";
                                                    }

                                                    JSONArray messages = data.getJSONArray("messages");
                                                    for(int j=0;j<messages.length();j++) {
                                                        JSONObject message = messages.getJSONObject(j);
                                                        int pointMin = message.getInt("pointMin");
                                                        int pointMax = message.getInt("pointMax");
                                                        String point2 = pointMin + "-" + pointMax;

                                                        point = point + "  " + point2;

                                                        String messagearea = message.getString("message");
                                                        String resultShow = message.getString("resultShow");
                                                        String isAnswer = "جواب شما";

                                                        String compare = "امتیاز شما در بازه زیر دیده نشده است ";

                                                        if (messagearea.replace("ي", "ی").equals(compare.replace("ي", "ی"))) {
//                                                            if (sum != -123456) {
//                                                                if (!(sum >= pointMin && sum <= pointMax))
//                                                                    isAnswer = "no";
//                                                            } else {
//                                                                isAnswer = "no";
//                                                            }
                                                            isAnswer = "no";
                                                        }
                                                        if (!(isAnswer.equals("no"))) {
                                                            messagearea = "امتیاز شما در بازه زیر دیده شده است";
                                                        }
                                                        JSONObject list = new JSONObject();
                                                        try {
                                                            list.put("title", title);
                                                            list.put("point", point);
                                                            list.put("message", messagearea);
                                                            list.put("resultshow", resultShow);
                                                            list.put("isanswer", isAnswer);
                                                            list.put("id", i);
                                                            obj.put(list);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                            fetchResults(null);
                                                        }
                                                    }
                                                }
                                                fetchResults(obj);
                                            }
                                            else {
                                                //Log.e("get_show_Results", "failed to get get_show_Results" + ":"+dataArray.length());
                                                fetchResults(null);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            fetchResults(null);
                                        }
                                    }
                                    else {
                                        //Log.e("get_show_Results", "failed to get get_show_Results");
                                        fetchResults(null);
                                    }
                                }
                            });
                        }
                    });
        }
    }
    private void fetchResults(final JSONArray obj) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
        if(obj == null || obj.length()<=0){
            JSONArray obj = new JSONArray();
            JSONObject list = new JSONObject();
            try {
                list.put("title","آزمون شما به اتمام نرسیده است");
                list.put("point","0-0");
                list.put("message","آزمون را از نو شروع کنید یا منتظر جواب سرور باشید...");
                list.put("resultshow","Exam is not ended or Wait for server response...");
                list.put("isanswer","no");
                list.put("id",1);
                obj.put(list);

                List<ResultModel> recipes = new Gson().fromJson(obj.toString(), new TypeToken<List<ResultModel>>() {
                }.getType());

                // adding recipes to cart list
                cartList.clear();
                cartList.addAll(recipes);
                // refreshing recycler view
                mAdapter.notifyDataSetChanged();

                // stop animating Shimmer and hide the layout
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            List<ResultModel> recipes = new Gson().fromJson(obj.toString(), new TypeToken<List<ResultModel>>() {
            }.getType());

            // adding recipes to cart list
            cartList.clear();
            cartList.addAll(recipes);
            // refreshing recycler view
            mAdapter.notifyDataSetChanged();

            // stop animating Shimmer and hide the layout
            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);
        }
            }
        });
    }


    //update user token
    private void execute(){
        new Result.LoginTask().execute(0);
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
                                    getExamCode();
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

    //
    public void updateExams(String code){
        String newExam_Code = "";
        Log.e("exam_code", session.getUserDetails().getExams());
        Boolean exists = false;
        if(session.getUserDetails().getExams().length()>0){
            for (String exam_code: session.getUserDetails().getExams().split(",")) {
                Log.e("exam_code", exam_code);
                String examId = exam_code.split(":")[0];
                String codeId = exam_code.split(":")[1];
                if(examId.equals(ExamId)){
                    codeId = code;
                    exists=true;
                }
                newExam_Code += examId + ":" + codeId+",";
            }
        }

        if(!exists)
            newExam_Code += ExamId + ":" + code+",";
        Log.e("updateExams exams", newExam_Code.substring(0, newExam_Code.length()-1));
        session.setExams(newExam_Code.substring(0, newExam_Code.length()-1));
    }
    public String getCode(){
        String code = null;
        Boolean exists = false;
        if(session.getUserDetails().getExams().contains(",")) {
            for (String item : session.getUserDetails().getExams().split(",")) {
                if (item.split(":")[0].equals(ExamId+"")) {
                    code = item.split(":")[1];
                    break;
                }
            }
        }
        else{
            if(session.getUserDetails().getExams().length()>0)code=session.getUserDetails().getExams().split(":")[1];
        }
        return code;
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }

    //methods
    private String updateLanguage(){
        String language = Paper.book().read("language");
        if(language==null)
            Paper.book().write("language", "fa");
        return language;
    }
}
