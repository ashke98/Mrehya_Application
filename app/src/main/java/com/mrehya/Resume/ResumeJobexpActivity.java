package com.mrehya.Resume;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.mrehya.Education;
import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.Job;
import com.mrehya.ListAdapterJobs;
import com.mrehya.R;
import com.mrehya.Reserv.Persian_Date_Methods;
import com.mrehya.SessionManager;
import com.mrehya.UserAccount.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ResumeJobexpActivity extends AppCompatActivity {
    private String Language;
    private SessionManager session;
    private Resources resources;
    private Context context;
    private Resume resume;
    //ELEMENTS
    private Button btn_saveresume,btn_back;
    private TextView txttitle;
    private ListView listViewJobs;
    private ImageButton btnAddJobs;
    private ArrayList<Job> listJobs, oldJobs;
    private ListAdapterJobs listAdapterJobs;
    private ArrayList<String> years, month;
    private ArrayList<Integer> ids;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_jobexp);
        PolicyGuard.Allow();
        session = new SessionManager(getApplicationContext());
        Intent intent = getIntent();
        context = this.getApplicationContext();
        Language = intent.getStringExtra("Language");
        findviews();
        setLists();
        setOnclicks();
        updateView();
        try {
            setDefaults();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void findviews(){
        txttitle = (TextView) findViewById(R.id.txttitle);
        listViewJobs = (ListView) findViewById(R.id.listViewJobs);
        btnAddJobs = (ImageButton) findViewById(R.id.btnAddJobs);
        listViewJobs.setAdapter(listAdapterJobs);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_saveresume = (Button) findViewById(R.id.btn_saveresume);
        setJobsList();
    }
    private void setLists(){
        ids = new ArrayList<>();
        resume= new Resume(ResumeJobexpActivity.this);
        Persian_Date_Methods pd = new Persian_Date_Methods(Language);
        if(Language.equals("fa")){
            years = pd.get_persian_years();
            month = pd.get_persian_month();
        }
        else{
            years = pd.get_gregorian_years();
            month = pd.get_gregorian_months();
        }
    }
    public void setOnclicks(){
        btnAddJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAddJobs(listViewJobs);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ResumeMainActivity.class);
                startActivity(intent);
            }
        });

        btn_saveresume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }
    public void updateView(){
        Context context = LocaleHelper.setLocale(getApplicationContext(), Language);
        resources = context.getResources();
        if(Language.equals("fa")){
            txttitle.setText("سوابق شغلی");
        }
        else{
            txttitle.setText("Job Experiences");
        }
        listViewJobs.setAdapter(listAdapterJobs);
        btn_back.setText(resources.getString(R.string.Back));
        btn_saveresume.setText(resources.getString(R.string.save));
    }
    public void setDefaults() throws JSONException {
        if(session.get_Resume().getExperiences()!=null){
            JSONArray array = new JSONArray(session.get_Resume().getExperiences().toString());
            oldJobs = new ArrayList<>();
            for (int i=0 ; i<array.length();i++)
            {
                JSONObject c= array.getJSONObject(i);
                Job job = new Job(c.getString("title"), c.getString("start_year"), c.getString("last"), "",
                        c.getString("company"), "", "1", "1");
                job.setId(c.getInt("id"));
                listJobs.add(job);
                oldJobs.add(job);
                listAdapterJobs.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listViewJobs);
            }
        }
        else{
            JSONArray array = new JSONArray();
            oldJobs = new ArrayList<>();
            for (int i=0 ; i<array.length();i++)
            {
                JSONObject c= array.getJSONObject(i);
                Job job = new Job(c.getString("title"), c.getString("start_year"), c.getString("last"), "",
                        c.getString("company"), "", "1", "1");
                job.setId(c.getInt("id"));
                listJobs.add(job);
                oldJobs.add(job);
                listAdapterJobs.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listViewJobs);
            }
        }
    }

    //APIS
    private void save(){
        if(Language.equals("fa")){
            Toast.makeText(this, "در حال ارتباط با سرور...", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Connecting to server...", Toast.LENGTH_SHORT).show();
        }
        ids.clear();
        String resumeid = session.getUserDetails().getResume();
        for(final Job item: listJobs){
            ids.add(item.getId());
        }
        for(final Job item:oldJobs){
            String req="";
            if(item.getId() == 0){
                if (ids.contains(item.getId()))
                {
                    req="https://api.mrehya.com/v1/user/experience";
                }
                else{
                    continue;
                }
            }
            else if(item.getId() != 0){
                if (!ids.contains(item.getId()))
                {
                    resumeid="-1";
                }
                else{
                    for (Job item2: listJobs) {
                        if(item2.getId() == item.getId()){
                            item.setCompany(item2.getCompany());
                            item.setFrom(item2.getFrom());
                            item.setTo(item2.getTo());
                            item.setJobtitle(item2.getJobtitle());
                            break;
                        }
                    }
                }
                req = "https://api.mrehya.com/v1/user/experience?id="+item.getId();
            }
            saveApi(resumeid, req, item);
        }
    }
    private RequestBody requestbody(final Job item, String resumeid){
        if(item.getId()==0){
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("auth", session.getUserDetails().getToken())
                    .addFormDataPart("Experience[job_title]", item.getJobtitle())
                    .addFormDataPart("Experience[company]", item.getCompany())
                    .addFormDataPart("Experience[start_year]", fa_toen_num(item.getFrom()))
                    .addFormDataPart("Experience[end_year]", fa_toen_num(item.getTo()))
                    .addFormDataPart("Experience[resumeId]", resumeid)
                    .addFormDataPart("Experience[end_month]", item.getTomonth())
                    .addFormDataPart("Experience[start_month]", item.getFrommonth())
                    .addFormDataPart("Experience[working]", item.getstillworking())
                    .addFormDataPart("Experience[description]",  item.getRole())
                    .build();
            return requestBody;
        }
        else{
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("auth", session.getUserDetails().getToken())
                    .addFormDataPart("Experience[job_title]", item.getJobtitle())
                    .addFormDataPart("Experience[company]", item.getCompany())
                    .addFormDataPart("Experience[start_year]", fa_toen_num(item.getFrom()))
                    .addFormDataPart("Experience[end_year]", fa_toen_num(item.getTo()))
                    .addFormDataPart("Experience[resumeId]", resumeid)
                    .build();
            return requestBody;
        }
    }
    private void saveApi(String resumeid, String req, final Job item){
        ApiHelpers ah = new ApiHelpers(this);
        RequestBody requestBody = requestbody(item, resumeid);
        if(requestBody != null){
            ah.PosttCall(req, requestBody, false).enqueue(
                    new Callback() {
                        Handler mainHandler = new Handler(context.getMainLooper());
                        @Override
                        public void onFailure(Call call, final IOException e) {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("Job_Jobcat", e.getMessage());
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
                                    Log.e("Job_Jobcat", DataString);
                                    if (response.isSuccessful()) {
                                        try {
                                            JSONObject json = new JSONObject(DataString);
                                            String success = json.getString("success");
                                            if (success.equalsIgnoreCase("true")) {
                                                if(Language.equals("fa")){
                                                    Toast.makeText(ResumeJobexpActivity.this, "با موفقیت بروزرسانی شد", Toast.LENGTH_SHORT).show(); }
                                                else{
                                                    Toast.makeText(ResumeJobexpActivity.this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                                                }
                                                resume.get_resume_api();
                                            }
                                        } catch (JSONException e) {
                                            Log.e("Job_Jobcat", "failed to Job_Jobcat");
                                        }
                                    }
                                    else {
                                        Log.e("Job_Jobcat", "failed to Job_Jobcat");
                                    }
                                }
                            });
                        }
                    });
        }
    }


    //methods
    private void setJobsList(){
        listJobs = new ArrayList<>();
        listAdapterJobs = new ListAdapterJobs(listJobs,getApplicationContext(),ResumeJobexpActivity.this,listViewJobs);
    }
    private void showDialogAddJobs(final ListView listView){
        final Dialog dialog = new Dialog(ResumeJobexpActivity.this);
        dialog.setContentView(R.layout.list_dialog_jobs);
        final EditText txtEditJobDesc = dialog.findViewById(R.id.txtEditDialogJobDesc);
        final EditText txtEditCompany = dialog.findViewById(R.id.txtEditDialogJobCompany);
        final Spinner txtEditFrom = dialog.findViewById(R.id.spinnerFromJob);
        final Spinner txtEditTo = dialog.findViewById(R.id.spinnerToJob);

        final Spinner txtEditFrommonth = dialog.findViewById(R.id.spinnerFromJobmonth);
        final Spinner txtEditTomonth = dialog.findViewById(R.id.spinnerToJobmonth);

        final EditText txtEditJobtitle = dialog.findViewById(R.id.txtEditDialogJobtitle);
        final CheckBox chkStillWorking = (CheckBox)  dialog.findViewById(R.id.chkStillWorking);


        final LinearLayout LinearLayoutresumedialogJob = dialog.findViewById(R.id.LinearLayoutresumedialogJob);
        final LinearLayout LinearLayoutresumedialogJob2 = dialog.findViewById(R.id.LinearLayoutresumedialogJob2);
        final LinearLayout LinearLayoutresumedialogJob3 = dialog.findViewById(R.id.LinearLayoutresumedialogJob3);

        final TextView btnEditd = dialog.findViewById(R.id.btnEditd);
        final TextView txtjobtitle= dialog.findViewById(R.id.txtjobtitle);
        final TextView txtdesc = dialog.findViewById(R.id.txtdesc);
        final TextView txtcompany = dialog.findViewById(R.id.txtcompany);
        final TextView txtFrom = dialog.findViewById(R.id.txtFrom);
        final TextView txtuntil = dialog.findViewById(R.id.txtuntil);
        Context context = LocaleHelper.setLocale(getApplicationContext(), Language);
        resources = context.getResources();
        if(Language.equals("fa"))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                LinearLayoutresumedialogJob.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutresumedialogJob2.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutresumedialogJob3.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            btnEditd.setText("اضافه کردن");
            chkStillWorking.setText("من هنوز مشغول کار هستم");
            ArrayAdapter<String> From = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_rtl,
                    years
            );
            From.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
            txtEditFrom.setAdapter(From);

            ArrayAdapter<String> To = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_rtl,
                    years
            );
            To.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
            txtEditTo.setAdapter(To);


            ArrayAdapter<String> Frommonth = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_rtl,
                    month
            );
            From.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
            txtEditFrommonth.setAdapter(Frommonth);

            ArrayAdapter<String> Tomonth = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_rtl,
                    month
            );
            To.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
            txtEditTomonth.setAdapter(Tomonth);

        }

        else
        {
            ArrayAdapter<String> From = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_ltr,
                    years
            );
            From.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
            txtEditFrom.setAdapter(From);

            ArrayAdapter<String> To = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_ltr,
                    years
            );


            ArrayAdapter<String> Frommonth = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_ltr,
                    month
            );
            From.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
            txtEditFrommonth.setAdapter(Frommonth);

            ArrayAdapter<String> Tomonth = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_ltr,
                    month
            );
            To.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
            txtEditTomonth.setAdapter(Tomonth);

            To.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
            txtEditTo.setAdapter(To);
            chkStillWorking.setText("Still Working");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                LinearLayoutresumedialogJob.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutresumedialogJob2.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutresumedialogJob3.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            btnEditd.setText("Add");
        }
        txtjobtitle.setText(resources.getString(R.string.JobTitle2));
        txtdesc.setText(resources.getString(R.string.desc));
        txtcompany.setText(resources.getString(R.string.Company));
        txtFrom.setText(resources.getString(R.string.From));
        txtuntil.setText(resources.getString(R.string.until));



        ImageButton btnEdit = dialog.findViewById(R.id.btnEditDialogJob);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(existsexperience(txtEditFrom.getSelectedItem().toString(),txtEditTo.getSelectedItem().toString(),txtEditJobDesc.getText().toString(),txtEditCompany.getText().toString())))
                {
                    Job job = new Job(txtEditJobtitle.getText().toString(),txtEditFrom.getSelectedItem().toString(),txtEditTo.getSelectedItem().toString(),
                            txtEditJobDesc.getText().toString(),txtEditCompany.getText().toString(),
                            chkStillWorking.isSelected()?"1":"0", (month.indexOf(txtEditFrommonth.getSelectedItem().toString())+1)+""
                            , (month.indexOf(txtEditTomonth.getSelectedItem().toString())+1)+"" );
                    job.setId(0);
                    listJobs.add(job);
                    oldJobs.add(job);
                    listAdapterJobs.notifyDataSetChanged();
                    justifyListViewHeightBasedOnChildren(listView);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout((6 * width)/7, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    public boolean existsexperience(String from, String to, String role, String name){
        for (Job item: listJobs) {
            if(item.getFrom().equals(from) && item.getTo().equals(to) && item.getRole().equals(role)
                    && item.getCompany().equals(name))
                return true;
        }
        return false;
    }
    public static void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }
    private String fa_toen_num(String number){
        return number.replace("۱","1").replace("۲","2")
                .replace("۳","3").replace("۴","4")
                .replace("۵","5").replace("۶","6")
                .replace("۷","7").replace("۸","8")
                .replace("۹","9").replace("۰","0");
    }
}
