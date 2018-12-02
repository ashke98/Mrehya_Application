package com.mrehya.Exams;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.mrehya.ApiHelpers.ApiHelpers;
import com.mrehya.AppConfig;
import com.mrehya.AppController;
import com.mrehya.DashboardPackage.HttpsTrustManager;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.paperdb.Paper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Test extends AppCompatActivity {
    private String Language="fa";
    private Context context;
    private Resources resources;
    private ProgressDialog pDialog;
    private ProgressDialog aDialog;
    private SessionManager session;
    private boolean showed = false;
    //ELEMENTS 20181024161639120
    private LinearLayout LinearLayoutRadios, LinearLayouttextViews, ExamNotReadyLayout, ExamLayout, ExamBuyLayout;
    private ArrayList<Exam> listExams;
    private TextView txtQuestion, txtNumber, tv_point, tv_answer, tv_message, tv_title,tv_points;
    private Button btnNext,btnBack, btn_Back, btn_Back2, btnBack3, btn_Buy;
    private CircularProgressBar progressBar;
    private RadioGroup radioGroup;
    private int q_counter = 1;
    private int sum = 0;
    private Boolean ExamLoaded = false;
    private Boolean Free = true;
    //LISTS VARS
    private Exam exam;
    private int counter;
    private ArrayList<Integer> UserAnswers;
    private HashMap<String, Integer> Question_Points;
    private HashMap<String, String> Question_Answers;

    private ApiHelpers ah;

    private int examid;
    final Handler handler = new Handler();

    //LOADING FOR EVERY ANSWER
    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PolicyGuard.Allow();
        setContentView(R.layout.activity_test);
        context = this;
        Paper.init(this);
        Language=updateLanguage();
        pDialog = new ProgressDialog(this);
        aDialog = new ProgressDialog(this);
        session = new SessionManager(getApplicationContext());
        ah = new ApiHelpers(this);
        if(!session.isLoggedIn()){
            Toast.makeText(context, "برای مشاهده آزمون ها باید ورود کنید", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Test.this,Login.class);
            startActivity(intent);
        }
        else{
            findViews();
            setViews();
            // getting exam from examslist by id
            Intent intent = getIntent();
            examid = intent.getIntExtra("examId",0);
            if(Language.equals("fa")){
                Toast.makeText(context, "منتظر بارگزاری آزمون باشید", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context, "Wait for test to be loaded!", Toast.LENGTH_SHORT).show();
            }
            startDialog();
            new Test.LoginTask().execute(0);
            updateView();
        }
    }
    private void findViews(){
        txtQuestion =(TextView) findViewById(R.id.txtQuestions);
        btnBack =(Button) findViewById(R.id.btnBack);
        btnNext =(Button) findViewById(R.id.btnNext);
        btn_Back =(Button) findViewById(R.id.btn_Back);
        btn_Back2 =(Button) findViewById(R.id.btn_Back2);
        btnBack3 =(Button) findViewById(R.id.btnBack3);
        btn_Buy =(Button) findViewById(R.id.btn_Buy);
        txtNumber = (TextView) findViewById(R.id.txtNumber);
        tv_point = (TextView) findViewById(R.id.tv_point);
        tv_message = (TextView) findViewById(R.id.tv_message);
        tv_answer= (TextView) findViewById(R.id.tv_answer);
        tv_title= (TextView) findViewById(R.id.tv_title);
        tv_points= (TextView) findViewById(R.id.tv_points);
        progressBar = (CircularProgressBar) findViewById(R.id.progressBar);
        LinearLayouttextViews = (LinearLayout) findViewById(R.id.LinearLayouttextViews);
        LinearLayoutRadios = (LinearLayout) findViewById(R.id.LinearLayoutRadios);
        ExamNotReadyLayout = (LinearLayout) findViewById(R.id.ExamNotReadyLayout);
        ExamLayout = (LinearLayout) findViewById(R.id.ExamLayout);
        ExamBuyLayout = (LinearLayout) findViewById(R.id.ExamBuyLayout);
    }
    private void setViews(){
        progressbar = (ProgressBar)findViewById(R.id.spin_kit);
        ThreeBounce doubleBounce = new ThreeBounce();
        progressbar.setIndeterminateDrawable(doubleBounce);

        counter=0;
        btnBack.setVisibility(View.GONE);
        UserAnswers=new ArrayList<>();
        Question_Points = new HashMap<>();
        Question_Answers = new HashMap<>();
        if(Language.equals("fa")){
            txtNumber.setText("۱");
        }
        else{
            txtNumber.setText("1");
        }
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Test.this, ChooseExam.class);
                startActivity(intent);
            }
        });
        btnBack3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Test.this, ChooseExam.class);
                startActivity(intent);
            }
        });
        btn_Buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });
        setOnclicks();
    }

    //---------------------------------------APIs--------------------------------------------------//

    //-----------------------------LOGIN AND START EXAM-------------------------------------//
    private class LoginTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                // some long running task will run here. We are using sleep as a dummy to delay execution
                checkLogin(session.getUserDetails().getEmail(), session.getUserDetails().getPassword());
                Thread.sleep(2000);
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
    }//TRUE
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
    }//TRUE
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
                                                Exam_api_with_Questions(examid);
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
    }//TRUE



    //-----------------------------CREATE EXAM AND SET FIRST QUESTION-----------------------//
    private void Exam_api_with_Questions(final int ExamId){
        startDialog();
        ApiHelpers ah = new ApiHelpers(context);
        ah.getCall(AppConfig.URL_Get_Exam+ExamId, false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                 //DIDNT LOAD EXAM
                                ExamLoaded = false;
                                Show_Hide_Exam();
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
                                        if(response.code() == 200){
                                            String DataString  = response.body().string();
                                            JSONObject data = new JSONObject(DataString).getJSONObject("data");

                                            if(data.length()>0){
                                                //Check Questions count number to see if exam is just ready!
                                                JSONArray Questions = data.getJSONArray("questions");
                                                if(Questions.length()<=0){
                                                    ExamLoaded = false;
                                                }
                                                else{
                                                    ExamLoaded = true;
                                                    //Log.e("exam", data.getString("price") +""+data.getString("paymentType"));
                                                    if(data.getString("paymentType")!=null){
                                                        exam = new Exam(
                                                                ExamId,data.getString("quiezTime"),data.getString("title"),data.getString("content"),
                                                                data.getString("price"),data.getString("paymentType")
                                                        );
                                                        if (Language.equals("fa")) {
                                                            if(exam.getPrice().equals(" 0 تومان") || exam.getType().equals("1") || exam.getType().equals("رایگان")
                                                                    || exam.getPrice().equals("رایگان"))
                                                            {
                                                                Free=true;
                                                            }
                                                            else{
                                                                Free= false;
                                                            }

                                                        }else {
                                                            if(exam.getPrice().equals(" 0 تومان") || exam.getType().equals("1") || exam.getType().equals("رایگان")
                                                                    || exam.getType().equals("Free") || exam.getPrice().equals("رایگان"))
                                                            {
                                                                Free=true;
                                                            }
                                                            else{
                                                                Free= false;
                                                            }
                                                        }
                                                    }
                                                    else{
                                                        exam = new Exam(
                                                                ExamId,data.getString("quiezTime"),data.getString("title"),data.getString("content"),
                                                                data.getString("price"),null
                                                        );
                                                        //|| exam.getType().equals("1") || exam.getType().equals("رایگان")
                                                        if (Language.equals("fa")) {
                                                            if(exam.getPrice().equals(" 0 تومان")
                                                                    || exam.getPrice().equals("رایگان"))
                                                            {
                                                                Free=true;
                                                            }
                                                            else{
                                                                Free= false;
                                                            }

                                                        }else {
                                                            if(exam.getPrice().equals(" 0 تومان") || exam.getType().equals("1") || exam.getType().equals("رایگان")
                                                                    || exam.getType().equals("Free") || exam.getPrice().equals("رایگان"))
                                                            {
                                                                Free=true;
                                                            }
                                                            else{
                                                                Free= false;
                                                            }
                                                        }
                                                    }


                                                    ExamLayout.setVisibility(View.VISIBLE);
                                                    for(int i=0;i<Questions.length();i++){
                                                        JSONObject q = Questions.getJSONObject(i);
                                                        JSONArray Answers = q.getJSONArray("answers");
                                                        Question newquestion = new Question(q.getString("question"),q.getInt("id"), data.getString("quiezTime"));
                                                        //Log.e("QUEEZE TIME", newquestion.getTime());
                                                        for(int j=0;j<Answers.length();j++) {
                                                            JSONObject a = Answers.getJSONObject(j);
                                                            newquestion.setAnswer(new Question.answer(a.getInt("id"), a.getInt("questionId")
                                                                    , a.getInt("point"),  a.getString("answer"), a.getString("answer_en")));
                                                        }
                                                        exam.add_Q(newquestion);
                                                    }
                                                    UserAnswers.add(-1000);
                                                    setQuestion();
                                                }
                                            }
                                        }
                                        else{
                                            ExamLoaded = false;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else{
                                    ExamLoaded = false;
                                }
                                Show_Hide_Exam();
                                hideDialog();
                            }
                        });
                    }
                }
        );
    } //TRUE



    //-----------------------------SEND ANSWER AND SAVE TO USERANSWERED.add(answerId);------//
    private void sendAnswer(final String questionId, final String answerId){
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("auth", session.getUserDetails().getToken())
                .addFormDataPart("question", questionId)
                .addFormDataPart("answer", answerId)
                .build();

        ah.PosttCallHighTimeout(AppConfig.URL_SEND_ANSWER, requestBody, false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                Log.e("sending answer", e.getMessage());
                                hideProgress();
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
                                //Log.d("sending answer", DataString);
                                if (response.isSuccessful()) {
                                    try {
                                        q_counter++;
                                        String success = new JSONObject(DataString).getString("success");
                                        if (success.equalsIgnoreCase("true")) {
                                            //userAnswered.add(answerId);
                                            Question_Answers.put(questionId, answerId);

                                        } else {
                                            Log.e("sendAnswer", "failed to get sendAnswer");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.e("sendAnswer", "failed to get sendAnswer");
                                }
                                hideProgress();
                            }
                        });
                    }
                });
    }



    //-----------------------------SET NEW QUESTION WITH COUNTER---------------------------//
    private void Show_Hide_Exam(){
        if(ExamLoaded)
        {
            if(Free){
                ExamNotReadyLayout.setVisibility(View.GONE);
                ExamBuyLayout.setVisibility(View.GONE);
                ExamLayout.setVisibility(View.VISIBLE);
            }
            else{
                ExamLayout.setVisibility(View.GONE);
                ExamNotReadyLayout.setVisibility(View.GONE);
                ExamBuyLayout.setVisibility(View.VISIBLE);
            }

        }
        else{
            ExamLayout.setVisibility(View.GONE);
            ExamBuyLayout.setVisibility(View.GONE);
            ExamNotReadyLayout.setVisibility(View.VISIBLE);
        }
    }//TRUE
    private void setQuestion(){
        Question question = exam.getQuestion(counter);
        if(question!=null)
        {
            txtQuestion.setText(question.getQuestion());
            setRadiobtns(question.getAnswers(),question.getId());
        }
    };//TRUE
    private void setRadiobtns(ArrayList<Question.answer> answers, final int questionId){
        LinearLayoutRadios.removeAllViews();
        radioGroup = new RadioGroup(this);
        for(int i=0;i<answers.size();i++){
            final Question.answer ans = answers.get(i);
            final String ansid = ans.getId()+"";
            final RadioButton radiobtn = new RadioButton(this);
            radiobtn.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 3));
            radiobtn.setPadding(10,10,10,10);
            radiobtn.setText(ans.getText());
            radiobtn.setId(ans.getId());
            radiobtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    radiobtn.setChecked(true);
                    showProgress();
                    UserAnswers.set(counter, ans.getPoint());
                    //UserAns.put(questionId+"", ansid);

                    Question_Points.put(questionId+"", ans.getPoint());
                    sendAnswer(questionId+"", ansid);
                }
            });
            if(ans.getPoint() == UserAnswers.get(counter)){
                radiobtn.setChecked(true);
            }
            radioGroup.addView(radiobtn);
        }
        LinearLayoutRadios.addView(radioGroup);
    }
    private void setOnclicks(){
        final Handler handler = new Handler();

        //NEXT
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        if(UserAnswers.get(counter) == -1000){
                            Toast.makeText(context, "گزینه ای برای این سوال انتخاب نشده است", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            counter++;
                            progressBar.setProgress(((float) counter/exam.getqCount())*100);
                            if((counter)>=(exam.getqCount())){
                                //setPoints();
                                save();
                            }
                            else{
                                if(Language.equals("fa")){
                                    txtNumber.setText(topersian(counter+1+""));
                                }
                                else{
                                    txtNumber.setText(counter+1+"");
                                }
                                btnBack.setVisibility(View.VISIBLE);
                                if(counter == UserAnswers.size()){
                                    UserAnswers.add(-1000);
                                }
                                setQuestion();
                            }
                        }
            }
        });
        //PREVIOUS
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter--;
                progressBar.setProgress(((float) counter/exam.getqCount())*100);
                if(Language.equals("fa")){
                    txtNumber.setText(topersian(counter+1+""));
                }
                else{
                    txtNumber.setText(counter+1);
                }
                if(counter==0){
                    btnBack.setVisibility(View.GONE);
                }
                setQuestion();
            }
        });

        btn_Back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Test.this,ChooseExam.class);
                startActivity(intent);
            }
        });
    }



    //-----------------------------WHEN EXAM ENDS---------------------------//
    private void save(){
        btnNext.setVisibility(View.GONE);
        btnBack.setVisibility(View.GONE);
        txtQuestion.setVisibility(View.GONE);
        txtNumber.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        LinearLayoutRadios.removeAllViews();
        sum=0;
        for (int point:
                Question_Points.values()) {
            sum+=point;
        }
        Log.e( "Sum point exam", sum+"_"+Question_Answers.size()+"_"+Question_Points.size());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //USER ANSWERED IS EQUALL TO ALL ANSWERS
                if(Question_Answers.size() >= Question_Points.size()){
                    new TestResultTask().execute(0);
                }
                else{
                    Set<String> QuestionAnswerdWithApiReturn = Question_Points.keySet();
                    for (String questionId: Question_Answers.keySet()) {
                        if(!QuestionAnswerdWithApiReturn.contains(questionId)){
                            sendAnswer(questionId, Question_Answers.get(questionId));
                        }
                    }
                    sum=0;
                    for (int point:
                            Question_Points.values()) {
                        sum+=point;
                    }
                    new TestResultTask().execute(0);
                }
            }
        },1000);

        Toast.makeText(context, "آزمون به پایان رسید", Toast.LENGTH_SHORT).show();
    } //TRUE
    class TestResultTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep(1000);
                Intent intent = new Intent(Test.this,Result.class);
                intent.putExtra("examId", examid);
                intent.putExtra("sum", sum);
                startActivity(intent);
                Thread.sleep(1000);
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
            startaDialog();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }//TRUE


    //methods
    private String updateLanguage(){
        String language = Paper.book().read("language");
        if(language==null)
            Paper.book().write("language", "fa");
        return language;
    }
    private void updateView() {
        context = LocaleHelper.setLocale(this, Language);
        resources = context.getResources();
        btnNext.setText(resources.getString(R.string.Next));
        btnBack.setText(resources.getString(R.string.Back));
    }

    private void startDialog(){
        pDialog.setCancelable(true);
        if(Language.equals("fa"))
            pDialog.setMessage("در حال بارگزاری...");
        else
            pDialog.setMessage("Loading...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        showDialog();
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hideDialog(){
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    private void startaDialog(){
        aDialog.setCancelable(true);
        if(Language.equals("fa"))
            aDialog.setMessage("استخراج جواب آزمون...");
        else
            aDialog.setMessage("Loading Result...");
        aDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        showaDialog();
    }
    private void showaDialog() {
        if (!aDialog.isShowing())
            aDialog.show();
    }
    private void hideaDialog() {
        if (aDialog.isShowing())
            aDialog.dismiss();
    }

    private void showProgress(){
        progressbar.setVisibility(View.VISIBLE);
        DisableViews();
    }
    private void hideProgress(){
        progressbar.setVisibility(View.GONE);
        EnableViews();
    }

    private void DisableViews(){
        btnNext.setEnabled(false);
        btnBack.setEnabled(false);
        btnNext.setClickable(false);
        btnBack.setClickable(false);
        LinearLayoutRadios.setEnabled(false);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(false);
        }
    }
    private void EnableViews(){
        btnNext.setEnabled(true);
        btnBack.setEnabled(true);
        btnNext.setClickable(true);
        btnBack.setClickable(true);
        LinearLayoutRadios.setEnabled(true);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(true);
        }
    }

    private String topersian(String input){
        return input.replace("1","١").replace("2","٢").replace("3","٣")
                .replace("4","۴").replace("5","۵").replace("6","۶")
                .replace("7","۷").replace("8","۸").replace("9","۹")
                .replace("0","۰");
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
    protected void onDestroy() {
        super.onDestroy();
        hideDialog();
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
