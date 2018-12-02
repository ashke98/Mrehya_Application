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
import com.mrehya.ListAdapterEducation;
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

public class ResumeEducationActivity extends AppCompatActivity {
    private String Language;
    private SessionManager session;
    private Resources resources;
    private Context context;
    private Resume resume;
    //ELEMENTS

    Button btn_saveresume,btn_back;

    TextView txttitle;

    ListView listViewEducation;

    ImageButton btnAddEducation;

    ArrayList<Education> listEducation;
    private ArrayList<Education> oldEducation;
    ListAdapterEducation listAdapterEducation;

    ArrayList<String> years, month;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_education);
        PolicyGuard.Allow();
        session = new SessionManager(getApplicationContext());
        Intent intent = getIntent();
        Language = intent.getStringExtra("Language");
        context = this.getApplicationContext();
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
        listViewEducation = (ListView) findViewById(R.id.listViewEducation);
        btnAddEducation = (ImageButton) findViewById(R.id.btnAddEducation);
        listViewEducation.setAdapter(listAdapterEducation);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_saveresume = (Button) findViewById(R.id.btn_saveresume);
        setEducationList();
    }
    public void setLists(){
        ids = new ArrayList<>();
        resume= new Resume(ResumeEducationActivity.this);
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
        btnAddEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAddEducation(listViewEducation);
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
        context = LocaleHelper.setLocale(getApplicationContext(), Language);
        resources = context.getResources();
        if(Language.equals("fa")){
            txttitle.setText("سوابق تحصیلی");
        }
        else{
            txttitle.setText("Educational records");
        }
        listViewEducation.setAdapter(listAdapterEducation);
        btn_back.setText(resources.getString(R.string.Back));
        btn_saveresume.setText(resources.getString(R.string.save));
    }
    public void setDefaults() throws JSONException {
        if(session.get_Resume().getAcademics()!=null){
            JSONArray array = new JSONArray(session.get_Resume().getAcademics().toString());
            oldEducation = new ArrayList<>();
            for (int i=0 ; i<array.length();i++)
            {
                JSONObject c= array.getJSONObject(i);
                Education edu = new Education("", c.getString("field"), c.getString("university"), c.getString("start_year"),
                        c.getString("last"));
                edu.setId(c.getInt("id"));
                listEducation.add(edu);
                oldEducation.add(edu);
                listAdapterEducation.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listViewEducation);
            }
        }
        else{
            JSONArray array = new JSONArray();
            oldEducation = new ArrayList<>();
            for (int i=0 ; i<array.length();i++)
            {
                JSONObject c= array.getJSONObject(i);
                Education edu = new Education("", c.getString("field"), c.getString("university"), c.getString("start_year"),
                        c.getString("last"));
                edu.setId(c.getInt("id"));
                listEducation.add(edu);
                oldEducation.add(edu);
                listAdapterEducation.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listViewEducation);
            }
        }
    }

    //APIS
    private void save() {
        if(Language.equals("fa")){
            Toast.makeText(this, "در حال ارتباط با سرور...", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Connecting to server...", Toast.LENGTH_SHORT).show();
        }
        ids.clear();
        String resumeid = session.getUserDetails().getResume();
        for(final Education item: listEducation){
            ids.add(item.getId());
        }
        for(final Education item:oldEducation){
            String req="";
            if(item.getId() == 0){
                if (ids.contains(item.getId()))
                {
                    req="https://api.mrehya.com/v1/user/academic";
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
                    for (Education item2: listEducation) {
                        if(item2.getId() == item.getId()){
                            item.setField(item2.getField());
                            item.setFrom(item2.getFrom());
                            item.setTo(item2.getTo());
                            item.setPlace(item2.getPlace());
                            break;
                        }
                    }
                }
                req = "https://api.mrehya.com/v1/user/academic?id="+item.getId();
            }
            saveApi(resumeid, req, item);
        }
    }
    private RequestBody requestbody(final Education item, String resumeid){
        if(item.getId()==0){
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("auth", session.getUserDetails().getToken())
                    .addFormDataPart("Academic[field]", item.getField())
                    .addFormDataPart("Academic[university]", item.getPlace())
                    .addFormDataPart("Academic[start_year]", item.getFrom())
                    .addFormDataPart("Academic[end_year]", item.getTo())
                    .addFormDataPart("Academic[resumeId]", resumeid)
                    .addFormDataPart("Academic[grade]", item.getGrade())
                    .addFormDataPart("Academic[still]", "0")
                    .addFormDataPart("Academic[description]", "")
                    .build();
            return requestBody;
        }
        else{
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("auth", session.getUserDetails().getToken())
                    .addFormDataPart("Academic[field]", item.getField())
                    .addFormDataPart("Academic[university]", item.getPlace())
                    .addFormDataPart("Academic[start_year]", item.getFrom())
                    .addFormDataPart("Academic[end_year]", item.getTo())
                    .addFormDataPart("Academic[resumeId]", resumeid)
                    .build();
            return requestBody;
        }
    }
    private void saveApi(String resumeid, String req, final Education item){
        ApiHelpers ah = new ApiHelpers(this);
        final String finalResumeid = resumeid;
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
                                    Log.e("Academic_Education", e.getMessage());
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
                                    Log.e("Academic_Education", DataString);
                                    if (response.isSuccessful()) {
                                        try {
                                            JSONObject json = new JSONObject(DataString);
                                            String success = json.getString("success");
                                            if (success.equalsIgnoreCase("true")) {
                                                if(Language.equals("fa")){
                                                    Toast.makeText(ResumeEducationActivity.this, "با موفقیت بروزرسانی شد", Toast.LENGTH_SHORT).show(); }
                                                else{
                                                    Toast.makeText(ResumeEducationActivity.this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                                                }
                                                resume.get_resume_api();
                                            }
                                        } catch (JSONException e) {
                                            Log.e("Academic_Education", "failed to Academic_Education");
                                        }
                                    }
                                    else {
                                        Log.e("Academic_Education", "failed to Academic_Education");
                                    }
                                }
                            });
                        }
                    });
        }
    }

    //methods
    private void setEducationList(){
        listEducation = new ArrayList<>();
        listAdapterEducation = new ListAdapterEducation(listEducation,getApplicationContext(),ResumeEducationActivity.this,listViewEducation);
    }
    public boolean existsacademic(String grade, String field, String Place, String from, String to){
        for (Education item: listEducation) {
            if(item.getGrade().equals(grade) && item.getFrom().equals(from) && item.getTo().equals(to) && item.getField().equals(field)
                    && item.getPlace().equals(Place))
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
    private void showDialogAddEducation(final ListView listView){
        final Dialog dialog = new Dialog(ResumeEducationActivity.this);
        dialog.setContentView(R.layout.list_dialog_education);
        final EditText txtEditField = dialog.findViewById(R.id.txtEditDialogEducationField);
        final EditText txtEditPlace = dialog.findViewById(R.id.txtEditDialogEducationPlace);
        final Spinner txtEditFrom = dialog.findViewById(R.id.spinnerFromEducation);
        final Spinner txtEditTo = dialog.findViewById(R.id.spinnerToEducation);
        final Spinner txtEditgrade = dialog.findViewById(R.id.spinnerGradeEducation);

        final TextView txtmajor = dialog.findViewById(R.id.txtmajor);
        final TextView txteducationplace = dialog.findViewById(R.id.txteducationplace);
        final TextView txtFrom = dialog.findViewById(R.id.txtFrom);
        final TextView txteducationgrade = dialog.findViewById(R.id.txteducationgrade);
        final TextView txtuntil = dialog.findViewById(R.id.txtuntil2);
        LinearLayout LinearLayoutresumedialogEdu = dialog.findViewById(R.id.LinearLayoutresumedialogEdu);
        LinearLayout LinearLayoutresumedialogEdu2 = dialog.findViewById(R.id.LinearLayoutresumedialogEdu2);
        LinearLayout LinearLayoutresumedialogEdu3 = dialog.findViewById(R.id.LinearLayoutresumedialogEdu3);

        TextView btnEditd = dialog.findViewById(R.id.btnEditd);

        if(Language.equals("fa"))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                LinearLayoutresumedialogEdu.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutresumedialogEdu2.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutresumedialogEdu3.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            btnEditd.setText("اضافه کردن");


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

            ArrayAdapter<String> gradeAdapter = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_rtl,
                    resources.getStringArray(R.array.certificate_arrays)
            );
            gradeAdapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
            txtEditgrade.setAdapter(gradeAdapter);
        }

        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                LinearLayoutresumedialogEdu.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutresumedialogEdu2.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutresumedialogEdu3.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            btnEditd.setText("Add");

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
            To.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
            txtEditTo.setAdapter(To);

            ArrayAdapter<String> gradeAdapter = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_ltr,
                    resources.getStringArray(R.array.certificate_arrays)
            );
            gradeAdapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
            txtEditgrade.setAdapter(gradeAdapter);
        }

        ImageButton btnEdit = dialog.findViewById(R.id.btnEditDialogEducation);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(existsacademic(txtEditgrade.getSelectedItem().toString(), txtEditField.getText().toString(),txtEditPlace.getText().toString(),txtEditFrom.getSelectedItem().toString(),txtEditTo.getSelectedItem().toString())))
                {
                    Education edu = new Education(txtEditgrade.getSelectedItem().toString(), txtEditField.getText().toString(),txtEditPlace.getText().toString(),txtEditFrom.getSelectedItem().toString(),txtEditTo.getSelectedItem().toString());
                    edu.setId(0);
                    listEducation.add(edu);
                    oldEducation.add(edu);
                    listAdapterEducation.notifyDataSetChanged();
                    justifyListViewHeightBasedOnChildren(listView);
                }
                dialog.dismiss();
            }
        });

        txtmajor.setText(resources.getString(R.string.major));
        txteducationplace.setText(resources.getString(R.string.educationplace));
        txteducationgrade.setText(resources.getString(R.string.Educationgrade));
        txtFrom.setText(resources.getString(R.string.From));
        txtuntil.setText(resources.getString(R.string.until));
        dialog.show();
        dialog.getWindow().setLayout((6 * width)/7, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    private String fa_toen_num(String number){
        return number.replace("۱","1").replace("۲","2")
                .replace("۳","3").replace("۴","4")
                .replace("۵","5").replace("۶","6")
                .replace("۷","7").replace("۸","8")
                .replace("۹","9").replace("۰","0");
    }
}
