package com.mrehya.Exams;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mrehya.ApiHelpers.ApiHelpers;
import com.mrehya.AppConfig;
import com.mrehya.AppController;
import com.mrehya.DashboardPackage.HttpsTrustManager;
import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.MainActivity;
import com.mrehya.Shopping.Product;
import com.mrehya.UserAccount.Login;
import com.mrehya.MyTextView;
import com.mrehya.R;
import com.mrehya.SessionManager;
import com.mrehya.UserAccount.checkLogin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import okhttp3.Call;
import okhttp3.Callback;

public class ChooseExam extends AppCompatActivity {

    private String Language="fa";
    private Resources resources;
    private Context context;
    private SessionManager session;

    //ELEMENTS
    ArrayList<Exam> listExams;
    ListAdapterExam listAdapterExams;
    RecyclerView listViewExams;

    MyTextView mytextExams;
    private MyTextView txtEmptyExams;
    private ProgressDialog pDialog;

    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PolicyGuard.Allow();
        setContentView(R.layout.activity_choose_exam);
        context = this;
        pDialog = new ProgressDialog(this);
        session = new SessionManager(getApplicationContext());
        if(!session.isLoggedIn()){
            Toast.makeText(context, "برای مشاهده آزمون ها باید ورود کنید", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChooseExam.this,Login.class);
            startActivity(intent);
        }
        else {
            findViews();
            updatelanguage();
            updateView();
            setViews();
            Exam_Api(Language);
            checkLogin cl = new checkLogin(this, session);
            cl.execute();
        }
    }
    public void findViews(){
        mytextExams = (MyTextView) findViewById (R.id.mytextExams);
        txtEmptyExams = (MyTextView) findViewById (R.id.txtEmptyExams);
        listViewExams = (RecyclerView) findViewById(R.id.listViewExams);
        btnBack = (Button) findViewById(R.id.btnBack);
    }
    public void setViews(){
        listExams = new ArrayList<>();
        listAdapterExams = new ListAdapterExam(listExams,getApplicationContext(),ChooseExam.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listViewExams.setLayoutManager(mLayoutManager);
        listViewExams.setItemAnimator(new DefaultItemAnimator());
        listViewExams.setAdapter(listAdapterExams);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                intent.putExtra("frgToLoad", "0");
                startActivity(intent);
            }
        });
    }

    //methods
    private void Exam_Api(final String Language){
        ApiHelpers ah = new ApiHelpers(context);
        ah.getCall(AppConfig.URL_DashExam+"1", false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("TAG", "error 1 " + e.getMessage());
                                //e.printStackTrace();
                                Show_Hide_Exams(false);
                                if(Language.equals("fa")){
                                    txtEmptyExams.setText("لیست آزمون\u200Cها خالی است");
                                    Toast.makeText(getApplicationContext(), "مشکلی در اتصال به سرور پیش آمده است", Toast.LENGTH_SHORT).show();
                                }

                                else{
                                    txtEmptyExams.setText("Empty Hire Advertisements!");
                                    Toast.makeText(getApplicationContext(), "Network Connection or Server failed!", Toast.LENGTH_SHORT).show();
                                }
                                txtEmptyExams.setVisibility(View.VISIBLE);

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
                                        String dataString = response.body().string();
                                        JSONObject Jobject = new JSONObject(dataString);
                                        JSONObject data = Jobject.getJSONObject("data");
                                        if(data.length()>0){
                                            txtEmptyExams.setVisibility(View.GONE);
                                            Show_Hide_Exams(true);
                                            set_Content(data);
                                            listAdapterExams.notifyDataSetChanged();
                                        }
                                        else{
                                            Show_Hide_Exams(false);
                                            if(Language.equals("fa"))
                                                txtEmptyExams.setText("لیست آزمون\u200Cها خالی است!\"");
                                            else
                                                txtEmptyExams.setText("Empty Hire Advertisements!");
                                            txtEmptyExams.setVisibility(View.VISIBLE);
                                        }
                                        hideDialog();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        //e.printStackTrace();
                                        Show_Hide_Exams(false);
                                        if(Language.equals("fa")){
                                            txtEmptyExams.setText("لیست آزمون\u200Cها خالی است");
                                            Toast.makeText(getApplicationContext(), "مشکلی در اتصال به سرور پیش آمده است", Toast.LENGTH_SHORT).show();
                                        }

                                        else{
                                            txtEmptyExams.setText("Empty Hire Advertisements!");
                                            Toast.makeText(getApplicationContext(), "Network Connection or Server failed!", Toast.LENGTH_SHORT).show();
                                        }
                                        txtEmptyExams.setVisibility(View.VISIBLE);

                                        hideDialog();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.d("TAG", "error 1 " + e.getMessage());
                                        //e.printStackTrace();
                                        Show_Hide_Exams(false);
                                        if(Language.equals("fa")){
                                            txtEmptyExams.setText("لیست آزمون\u200Cها خالی است");
                                            Toast.makeText(getApplicationContext(), "مشکلی در اتصال به سرور پیش آمده است", Toast.LENGTH_SHORT).show();
                                        }

                                        else{
                                            txtEmptyExams.setText("Empty Hire Advertisements!");
                                            Toast.makeText(getApplicationContext(), "Network Connection or Server failed!", Toast.LENGTH_SHORT).show();
                                        }
                                        txtEmptyExams.setVisibility(View.VISIBLE);

                                        hideDialog();
                                    }
                                }
                                else{
                                    Show_Hide_Exams(false);
                                    if(Language.equals("fa")){
                                        txtEmptyExams.setText("لیست آزمون\u200Cها خالی است");
                                        Toast.makeText(getApplicationContext(), "مشکلی در اتصال به سرور پیش آمده است", Toast.LENGTH_SHORT).show();
                                    }

                                    else{
                                        txtEmptyExams.setText("Empty Hire Advertisements!");
                                        Toast.makeText(getApplicationContext(), "Network Connection or Server failed!", Toast.LENGTH_SHORT).show();
                                    }
                                    txtEmptyExams.setVisibility(View.VISIBLE);

                                    hideDialog();
                                }
                                hideDialog();
                            }
                        });
                    }
                }
        );
    }
    public void set_Content(JSONObject data) throws JSONException {
        List<String> Allowed_Exams = Arrays.asList( "54", "53", "49","45","30","2","1","55","56");
        for (int i = 0; i < data.length()-1; i++) {
            JSONObject c = data.getJSONObject(i + "");
            //Log.d("Fragment part String: ", c.getJSONObject("image").getString("thumb"));
            if(Allowed_Exams.contains(c.getInt("id") +""))
            {
                if (Language.equals("fa")) {
                    if(c.getString("price").equals(" 0 تومان") ||  c.getString("paymentType").equals("1") || c.getString("paymentType").equals("رایگان"))
                    {
                        listExams.add(new Exam(c.getInt("id"),c.getString("title"),
                                c.getString("content"), "رایگان",c.getJSONObject("image").getString("thumb")
                                , c.getString("paymentType")));
                    }
                    else{
                        listExams.add(new Exam(c.getInt("id"),c.getString("title"),
                                c.getString("content"), c.getString("price"),c.getJSONObject("image").getString("thumb")
                                , c.getString("paymentType")));
                    }

                }else {
                    listExams.add(new Exam(c.getInt("id"),c.getString("title_en"),
                            c.getString("content_en"), c.getString("price"),c.getJSONObject("image").getString("thumb")
                            , c.getString("paymentType")));
                }
            }
        }
    }
    public void Show_Hide_Exams(boolean show){
        if(show){
            listViewExams.setVisibility(View.VISIBLE);

        }
        else{
            listViewExams.setVisibility(View.GONE);
        }
    }
    private String updatelanguage(){
        Paper.init(context);
        //Default language is fa
        String language = Paper.book().read("language");
        if(language==null)
            Paper.book().write("language", "fa");

        return language;
    }
    private void updateView() {
        context = LocaleHelper.setLocale(this, Language);
        resources = context.getResources();
        mytextExams.setText(resources.getText(R.string.Exams));
    }

    private void startDialog(){
        pDialog.setCancelable(true);
        if(Language.equals("fa"))
            pDialog.setMessage("در حال بارگزاری...");
        else
            pDialog.setMessage("Loading Companies...");
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
            if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
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