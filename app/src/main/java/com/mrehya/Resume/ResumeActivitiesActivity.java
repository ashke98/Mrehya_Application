package com.mrehya.Resume;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
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
import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.ListAdapterJobcat;
import com.mrehya.ListAdapterProes;
import com.mrehya.ListAdapterProvince;
import com.mrehya.ListAdapterSkill;
import com.mrehya.ListAdapterTerms;
import com.mrehya.R;
import com.mrehya.SessionManager;
import com.mrehya.UserAccount.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;

public class ResumeActivitiesActivity extends AppCompatActivity {

    //GLOBALS
    private String Language;
    private SessionManager session;
    private Resources resources;
    //ELEMENTS
    private Button btn_back, btn_saveresume;
    private TextView txttitle;

    //Linearlayout
    LinearLayout
            LinearLayoutresumeTalents,
            LinearLayoutresumeProvinces,
            LinearLayoutresumeJobcats,
            LinearLayoutresumeContracts,
            LinearLayoutresumeSeniority
            ;

    TextView
            txtTalents,
            txtProvinces,
            txtJobcats,
            txtContracts,
            txtSeniority
            ;

    ListView
            listViewTalents,
            listViewProvinces,
            listViewJobcats,
            listViewContracts,
            listViewSeniority
            ;
    ArrayList<String>
            listTalents,
            listProvinces,
            listJobcats,
            listContracts,
            listSeniority
                    ;
    ListAdapterProes     listAdapterTalents;
    ListAdapterProvince  listAdapterProvinces;
    ListAdapterJobcat      listAdapterJobcats;
    ListAdapterTerms     listAdapterContracts;
    ListAdapterSkill     listAdapterSeniority;

    ImageButton
            btnAddTalents,
            btnAddProvinces,
            btnAddJobcats,
            btnAddContracts,
            btnAddSeniority
            ;

    HashMap<String, Integer> JobcatsMap = new HashMap<String, Integer>();
    List<String> Seniority_arrays;
    String[] urlArray = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_activities);
        PolicyGuard.Allow();
        session = new SessionManager(getApplicationContext());
        Intent intent = getIntent();
        Language = intent.getStringExtra("Language");
        findviews();
        setOnclicks();
        updateView();
        try {
            setDefaults();
        } catch (JSONException e) {
            Log.e("FAILED"," TO SET DEFAULT OF RESUME INFO USERS: " + e.getMessage() );
            e.printStackTrace();
        }
        User u = new User(this);
        u.checkLogin(session.getUserDetails().getEmail(), session.getUserDetails().getPassword());
        JobCats_Api();
    }
    public void findviews(){
        LinearLayoutresumeTalents   = (LinearLayout) findViewById(R.id.LinearLayoutresumeTalents);
        LinearLayoutresumeProvinces = (LinearLayout) findViewById(R.id.LinearLayoutresumeProvinces);
        LinearLayoutresumeJobcats   = (LinearLayout) findViewById(R.id.LinearLayoutresumeJobcats);
        LinearLayoutresumeContracts = (LinearLayout) findViewById(R.id.LinearLayoutresumeContracts);
        LinearLayoutresumeSeniority = (LinearLayout) findViewById(R.id.LinearLayoutresumeSeniority);

        txtTalents      = (TextView) findViewById(R.id.txtTalents);
        txtProvinces    = (TextView) findViewById(R.id.txtProvinces);
        txtJobcats      = (TextView) findViewById(R.id.txtJobcats);
        txtContracts    = (TextView) findViewById(R.id.txtContracts);
        txtSeniority    = (TextView) findViewById(R.id.txtSeniority);

        listViewTalents     = (ListView) findViewById(R.id.listViewTalents);
        listViewProvinces   = (ListView) findViewById(R.id.listViewProvinces);
        listViewJobcats     = (ListView) findViewById(R.id.listViewJobcats);
        listViewContracts   = (ListView) findViewById(R.id.listViewContracts);
        listViewSeniority   = (ListView) findViewById(R.id.listViewSeniority);

        btnAddTalents       = (ImageButton) findViewById(R.id.btnAddTalents);
        btnAddProvinces     = (ImageButton) findViewById(R.id.btnAddProvinces);
        btnAddJobcats       = (ImageButton) findViewById(R.id.btnAddJobcats);
        btnAddContracts     = (ImageButton) findViewById(R.id.btnAddContracts);
        btnAddSeniority     = (ImageButton) findViewById(R.id.btnAddSeniority);

        btn_back            = (Button) findViewById(R.id.btn_back);
        btn_saveresume      = (Button) findViewById(R.id.btn_saveresume);
        txttitle            = (TextView) findViewById(R.id.txttitle);

        setLists_adapters();
    }
    public void setLists_adapters(){
        listTalents = new ArrayList<>();
        listAdapterTalents = new ListAdapterProes(listTalents,getApplicationContext(),ResumeActivitiesActivity.this,listViewTalents);

        listProvinces = new ArrayList<>();
        listAdapterProvinces = new ListAdapterProvince(listProvinces,getApplicationContext(),ResumeActivitiesActivity.this,listViewProvinces);

        listJobcats = new ArrayList<>();
        listAdapterJobcats = new ListAdapterJobcat(listJobcats,getApplicationContext(),ResumeActivitiesActivity.this,listViewJobcats);

        listContracts = new ArrayList<>();
        listAdapterContracts = new ListAdapterTerms(listContracts,getApplicationContext(),ResumeActivitiesActivity.this,listViewContracts);

        listSeniority = new ArrayList<>();
        listAdapterSeniority = new ListAdapterSkill(listSeniority,getApplicationContext(),ResumeActivitiesActivity.this,listViewSeniority);


        listViewTalents .setAdapter(listAdapterTalents);
        listViewProvinces.setAdapter(listAdapterProvinces);
        listViewJobcats.setAdapter(listAdapterJobcats);
        listViewContracts.setAdapter(listAdapterContracts);
        listViewSeniority.setAdapter(listAdapterSeniority);


        justifyListViewHeightBasedOnChildren(listViewTalents);
        justifyListViewHeightBasedOnChildren(listViewProvinces);
        justifyListViewHeightBasedOnChildren(listViewJobcats);
        justifyListViewHeightBasedOnChildren(listViewContracts);
        justifyListViewHeightBasedOnChildren(listViewSeniority);
    }
    public void setOnclicks(){
        btnAddTalents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAddTalents(listViewTalents);
            }
        });
        btnAddJobcats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAddJobcats(listViewJobcats);
            }
        });
        btnAddSeniority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAddSeniority(listViewSeniority);
            }
        });

        btnAddProvinces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAddProvinces(listViewProvinces);
            }
        });

        btnAddContracts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAddContracts(listViewContracts);
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
        Seniority_arrays= Arrays.asList(resources.getStringArray(R.array.Senority_arrays));

        if(Language.equals("fa")){
            txttitle.setText("فعالیت و علاقه مندی\u200Cهای شغلی");
        }
        else{
            txttitle.setText("Activities  and Job Interests");
        }
        txtTalents  .setText(resources.getString(R.string.Talents));
        txtProvinces.setText(resources.getString(R.string.SelectedProvinces));
        txtJobcats  .setText(resources.getString(R.string.JobCategory));
        txtContracts.setText(resources.getString(R.string.AcceptedContract));
        txtSeniority.setText(resources.getString(R.string.ActivityLevel));
        btn_back.setText(resources.getString(R.string.Back));
        btn_saveresume.setText(resources.getString(R.string.save));
    }
    public void setDefaults() throws JSONException {

        if(session.get_Resume().getSkills()!=null)
        {
            List<String> skills = Arrays.asList(session.get_Resume().getSkills().split(","));
            for (int i=0 ; i<skills.size();i++)
            {
                listTalents.add(skills.get(i).replace("ي","ی"));
                listAdapterTalents.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listViewTalents);
            }
        }
        if(session.get_Resume().getProvinces()!=null) {
            List<String> provinces = Arrays.asList(session.get_Resume().getProvinces().split(","));
            for (int i = 0; i < provinces.size(); i++) {
                listProvinces.add(provinces.get(i).replace("ي", "ی"));
                listAdapterProvinces.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listViewProvinces);
            }
        }
        if(session.get_Resume().getContracts()!=null) {
            List<String> contracts = Arrays.asList(session.get_Resume().getContracts().split(","));
            for (int i = 0; i < contracts.size(); i++) {
                listContracts.add(contracts.get(i).replace("ي", "ی"));
                listAdapterContracts.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listViewContracts);
            }
        }
        if(session.get_Resume().getCategories()!=null) {
            List<String> categories = Arrays.asList(session.get_Resume().getCategories().split(","));
            for (int i = 0; i < categories.size(); i++) {
                listJobcats.add(categories.get(i).replace("ي", "ی"));
                listAdapterJobcats.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listViewJobcats);
            }
        }
        if(session.get_Resume().getLevels()!=null) {
            List<String> sens = Arrays.asList(resources.getStringArray(R.array.Senority_arrays));
            List<String> seniorities = Arrays.asList(session.get_Resume().getLevels().split(","));
            for (int i = 0; i < seniorities.size(); i++) {
                listSeniority.add(sens.get(Integer.parseInt(seniorities.get(i)) - 1).replace("ي", "ی"));
                listAdapterSeniority.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listViewSeniority);
            }
        }
    }
    public void save(){
        if(Language.equals("fa")){
            Toast.makeText(this, "در حال ارتباط با سرور...", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Connecting to server...", Toast.LENGTH_SHORT).show();
        }
        String skills="";
        for(String item: listTalents){
            skills+=item+",";
        }

        if(skills!=null && skills.length()>2)
            skills=skills.substring(0,skills.length()-1);

        String pros = SplitListProvince();
        //Log.e("Pros first", pros);
        String result = session.set_Resume_Acts(
                        skills,
                        SplitListProvince(),
                        SplitTerms(),
                        SplitJobcats(),
                        SplitSenority());

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
    }

    //methods
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

    //dialogs
    private void showDialogAddTalents(final ListView listView){
        final Dialog dialog = new Dialog(ResumeActivitiesActivity.this);
        dialog.setContentView(R.layout.list_dialog_proes);
        final EditText txtEdit = dialog.findViewById(R.id.txtEditDialogPro);
        ImageButton btnEdit = dialog.findViewById(R.id.btnEditDialogPro);
        TextView btnEditd = dialog.findViewById(R.id.btnEditd);
        if(Language.equals("fa"))
            btnEditd.setText("ویرایش");
        else
            btnEditd.setText("Edit");
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listTalents.add(txtEdit.getText().toString());
                listAdapterTalents.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listView);
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout((6 * width)/7, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    private void showDialogAddJobcats(final ListView listView){
        final Dialog dialog = new Dialog(ResumeActivitiesActivity.this);
        dialog.setContentView(R.layout.list_dialog_jobcat);
        //final TextView txtJobcat = dialog.findViewById(R.id.txtEditDialogJobcat);
        final ImageButton txtedit = dialog.findViewById(R.id.btnEditDialogJobcat);
        final Spinner spinnerJobcats = dialog.findViewById(R.id.spinnerJobcats);
        final LinearLayout LinearLayout1 = dialog.findViewById(R.id.LinearLayout1);

        TextView btnEditd = dialog.findViewById(R.id.btnEditd);
        if(Language.equals("fa"))
            btnEditd.setText("ویرایش");
        else
            btnEditd.setText("Edit");

        if(Language.equals("fa"))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                LinearLayout1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            btnEditd.setText("ویرایش");
        }

        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                LinearLayout1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            btnEditd.setText("Edit");
        }

        if(JobcatsMap.size()<=0){
            JobCats_Api();
            String[] list = new String[1];
            list[0] =  "Empty";
            ArrayAdapter<String> Jobcatadapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    list
            );
            if(Language.equals("fa")) {
                Jobcatadapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
            }
            else{
                Jobcatadapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
            }
            spinnerJobcats.setAdapter(Jobcatadapter);

        }
        else
        {
            String[] urlArray = new String[JobcatsMap.size()];
            JobcatsMap.keySet().toArray(urlArray);
            ArrayAdapter<String> Jobcatadapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    urlArray
            );
            if(Language.equals("fa")) {
                Jobcatadapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
            }
            else{
                Jobcatadapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
            }
            spinnerJobcats.setAdapter(Jobcatadapter);
        }



        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;


        txtedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(listJobcats.isEmpty())) {
                    if (!(listJobcats.contains(spinnerJobcats.getSelectedItem().toString()))){
                        listJobcats.add(spinnerJobcats.getSelectedItem().toString());
                        Log.d("TAG", "spinners item"+spinnerJobcats.getSelectedItem());
                    }
                }
                else
                    listJobcats.add(spinnerJobcats.getSelectedItem().toString());
                Log.d("TAG", "spinners item"+spinnerJobcats.getSelectedItem());


                listAdapterJobcats.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listView);
                dialog.dismiss();
            }
        });
        //txtedit.setVisibility(View.GONE);
        dialog.show();
        dialog.getWindow().setLayout((6 * width)/7, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    private void showDialogAddProvinces(final ListView listView){
        final Dialog dialog = new Dialog(ResumeActivitiesActivity.this);
        dialog.setContentView(R.layout.list_dialog_provice);
        final Spinner spinnerProvince = dialog.findViewById(R.id.spinnerProvince);
        final ImageButton txtedit = dialog.findViewById(R.id.btnEditDialogProvince);

        TextView btnEditd = dialog.findViewById(R.id.btnEditd);


        Context context = LocaleHelper.setLocale(getApplicationContext(), Language);
        Resources resources = context.getResources();
        if(Language.equals("fa"))
        {
            btnEditd.setText("ویرایش");
        }

        else
        {
            btnEditd.setText("Edit");
        }

        ArrayAdapter<String> Province_array = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.Province)
        );
        if(Language.equals("fa")) {
            Province_array.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
        }
        else{
            Province_array.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
        }
        spinnerProvince.setAdapter(Province_array);



        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;


        txtedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String province = spinnerProvince.getSelectedItem().toString();
                String province = (spinnerProvince.getSelectedItemId()+1)+"";
                listProvinces.add(spinnerProvince.getSelectedItem().toString());
                listAdapterProvinces.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listView);
                dialog.dismiss();


            }
        });

        dialog.show();
        dialog.getWindow().setLayout((6 * width)/7, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    private void showDialogAddContracts(final ListView listView){
        final Dialog dialog = new Dialog(ResumeActivitiesActivity.this);
        dialog.setContentView(R.layout.list_dialog_terms);
        final Spinner spinnerTerms = dialog.findViewById(R.id.spinnerTerms);
        final ImageButton txtedit = dialog.findViewById(R.id.btnEditDialogTerms);

        TextView btnEditd = dialog.findViewById(R.id.btnEditd);

        Context context = LocaleHelper.setLocale(getApplicationContext(), Language);
        Resources resources = context.getResources();
        if(Language.equals("fa"))
        {
            btnEditd.setText("ویرایش");
        }

        else
        {
            btnEditd.setText("Edit");
        }

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;


        ArrayAdapter<String> contract_array = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.contract_arrays)
        );
        if(Language.equals("fa")) {
            contract_array.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
        }
        else{
            contract_array.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
        }
        spinnerTerms.setAdapter(contract_array);

        txtedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean exists=false;

                String Terms = spinnerTerms.getSelectedItem().toString();

                for (int i=0 ;i<listContracts.size();i++){
                    if (Terms.equalsIgnoreCase(listContracts.get(i))){
                        exists=true;
                        break;
                    }
                }
                if(!exists) {
                    listContracts.add(Terms);
                    listAdapterContracts.notifyDataSetChanged();
                    justifyListViewHeightBasedOnChildren(listView);
                }
                dialog.dismiss();

            }
        });

        dialog.show();
        dialog.getWindow().setLayout((6 * width)/7, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    private void showDialogAddSeniority(final ListView listView){
        final Dialog dialog = new Dialog(ResumeActivitiesActivity.this);
        dialog.setContentView(R.layout.list_dialog_skill);
        final Spinner spinnerskill = dialog.findViewById(R.id.spinnerskill);
        //final EditText txtEdit = dialog.findViewById(R.id.txtEditDialogskill);

//        TextView btnEditd = dialog.findViewById(R.id.btnEditd);
//        if(Language.equals("fa"))
//            btnEditd.setText("ویرایش");
//        else
//            btnEditd.setText("Edit");

        ImageButton btnEdit = dialog.findViewById(R.id.btnEditDialogSkill);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;


        Context context = LocaleHelper.setLocale(getApplicationContext(), Language);
        Resources resources = context.getResources();
//        if(Language.equals("fa"))
//        {
//            btnEditd.setText("ویرایش");
//        }
//
//        else
//        {
//            btnEditd.setText("Edit");
//        }

        ArrayAdapter<String> skill_array = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                Seniority_arrays
        );
        ArrayAdapter<String> contract_array = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.contract_arrays)
        );
        if(Language.equals("fa")) {
            contract_array.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
        }
        else{
            contract_array.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
        }
        spinnerskill.setAdapter(skill_array);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(listSeniority.isEmpty()))
                    listSeniority.clear();
                listSeniority.add(spinnerskill.getSelectedItem().toString());
                listAdapterSeniority.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listView);
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout((6 * width)/7, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    //apis
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
                                    for (int i = 0; i < data.length(); i++) {
                                        JobcatsMap.put(data.getJSONObject(i).getString("title")
                                                , data.getJSONObject(i).getInt("id"));
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

    public String  SplitTerms() {
        String splitetTerm="";
        String last="";
        for (String itme: listContracts) {
            splitetTerm+=(listContracts.indexOf(itme)+1)+",";
        }

        if ((splitetTerm.length()>0)) {
            last = splitetTerm.substring(0, splitetTerm.length() - 1);
        }
        return last;
    }
    public String  SplitListProvince() {
        String splitetTerm="";
        String last="";
        for (String item: listProvinces
                ) {
            splitetTerm+=item+",";
        }

        if ((splitetTerm.length()>0)) {
            last = splitetTerm.substring(0, splitetTerm.length() - 1);
        }

        return last;
    }
    public String  SplitJobcats() {
        String splitetTerm="";
        String last="";

        for (String item: listJobcats) {
            splitetTerm+=(item)+",";
        }

        if ((splitetTerm.length()>0)) {
            last = splitetTerm.substring(0, splitetTerm.length() - 1);
        }
        return last;
    }
    public String  SplitSenority() {
        if (listSeniority.size()!=0)
            return Seniority_arrays.indexOf(listSeniority.get(0))+1+"";
        else return "0";
    }
}
