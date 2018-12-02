package com.mrehya.Hire;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.mrehya.DashboardPackage.DashboardLists;
import com.mrehya.DashboardPackage.HttpsTrustManager;
import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.Hire.HireAdapter;
import com.mrehya.Hire.HireCompanies;
import com.mrehya.MyTextView;
import com.mrehya.R;
import com.mrehya.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class HireFragment extends Fragment {

    private SessionManager session;
    private RecyclerView recyclerView;
    private HireAdapter adapter;
    private List<HireCompanies> companyList;

    //new
    private String Language;
    MyTextView mytextHirePerson;

    //filters
    private Button btnNavigation,btnJobCats,btnIntouch;
    boolean hasnav=false, hasjobcat=false, hastouch=false;
    boolean[] CheckedNavigations,CheckedIntouchs,CheckedJobcats;
    ArrayList<Integer> UserNavigations =  new ArrayList<>();
    ArrayList<Integer> UserIntouchs =  new ArrayList<>();
    ArrayList<Integer> UserJobcats  =  new ArrayList<>();

    AlertDialog.Builder mbuilderNav,mbuilderjobCat,mbuilderIntouchs;
    private ProgressDialog pDialog;
    private TextView txtEmptyHires;
    View view;
    Context context;

    private String jobcats="", contracts="", provinces="", status ="0", hireStatus="0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PolicyGuard.Allow();
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_hire, container, false);
        context =view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        session = new SessionManager(this.getContext());
        pDialog = new ProgressDialog(context);

        //Filters
        mbuilderNav = new AlertDialog.Builder(getActivity());
        mbuilderjobCat = new AlertDialog.Builder(getActivity());
        mbuilderIntouchs = new AlertDialog.Builder(getActivity());

        btnNavigation = (Button)view.findViewById(R.id.btnNavigation);
        //btnNavigation.setVisibility(view.GONE);
        btnNavigation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(hasjobcat){
                    AlertDialog navdialog = mbuilderNav.create();
                    navdialog.show();
                }
                else{
                    if(Language.equals("fa"))
                    {
                        if(!(hasnav))
                            Toast.makeText(context, "در حال بارگزاری لیست استان\u200Cها...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(!(hasnav))
                            Toast.makeText(context, "Loading Provinces...", Toast.LENGTH_SHORT).show();
                    }
                    Province_Api(Language);
                }
            }
        });
        btnJobCats = view.findViewById(R.id.btnJobCats);
        //btnJobCats.setVisibility(view.GONE);
        btnJobCats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hasjobcat){
                    AlertDialog jobcatdialog = mbuilderjobCat.create();
                    jobcatdialog.show();
                }
                else{
                    if(Language.equals("fa")){
                        if (!(hasjobcat))
                            Toast.makeText(context, "در حال بارگزاری لیست دسته\u200Cبندی\u200Cهای شغلی...", Toast.LENGTH_SHORT).show();
                    }
                     else
                    {
                        if(!(hasjobcat))
                            Toast.makeText(context, "Loading JobCategories...", Toast.LENGTH_SHORT).show();
                    }
                    Jobcat_Api(Language);
                }

            }
        });
        btnIntouch = view.findViewById(R.id.btnIntouch);
        //btnIntouch.setVisibility(view.GONE);
        btnIntouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hastouch){
                    AlertDialog Intouchsdialog = mbuilderIntouchs.create();
                    Intouchsdialog.show();
                }
                else{
                    if(Language.equals("fa"))
                    {
                        if(!(hastouch))
                            Toast.makeText(context, "در حال بارگزاری لیست قراردادها...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(!(hastouch))
                            Toast.makeText(context, "Loading Corporations...", Toast.LENGTH_SHORT).show();
                    }
                    Intouch_Api(Language);
                }
            }
        });
        //new
        Paper.init(getActivity());
        mytextHirePerson = view.findViewById(R.id.mytextHirePerson);
        txtEmptyHires = view.findViewById(R.id.txtEmptyHires);
        Language = updateLanguage();
        //new

        reloadlist();
        fastList();
        new LoadAllTask().execute(0);
        updateView(Language);
        return view;
    }

    //APIS
    private void fastList(){
        if(jobcats.equals("") && contracts.equals("") && provinces.equals("") && DashboardLists.Companiesdata.length()>0){
            JSONObject data = DashboardLists.Companiesdata;
            try {
                if(data.getInt("totalPage")==0){
                    if(Language.equals("fa"))
                        txtEmptyHires.setText("تبلیغی یافت نشد");
                    else
                        txtEmptyHires.setText("Empty Hire Advertisements!");
                    txtEmptyHires.setVisibility(View.VISIBLE);
                }
                else{

                    if(data.length()>0){
                        txtEmptyHires.setVisibility(View.GONE);
                        for(int i = 0; i < data.length()-1; i++) {
                            JSONObject c = data.getJSONObject(i+"");
                            //Log.d("TAG", c.toString());
                            HireCompanies a = new HireCompanies(c.getInt("id"),c.getString("image"),c.getString("job_title"),c.getString("brand_name"),c.getString("job_title"),c.getString("province")
                                    ,c.getString("job_description"));
                            companyList.add(a);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    else{
                        if(Language.equals("fa"))
                            txtEmptyHires.setText("لیست تبلیغ\u200Cها خالی است!\"");
                        else
                            txtEmptyHires.setText("Empty Hire Advertisements!");
                        txtEmptyHires.setVisibility(View.VISIBLE);
                    }
                }
                hideDialog();
            } catch (JSONException e) {
                hideDialog();
                e.printStackTrace();
            }
            hireStatus="1";
            hideDialog();
        }
        else{
            new LoadAllTask().execute(0);
        }
    }
    class LoadAllTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep(1000);
                if(!(jobcats.equals("") && contracts.equals("") && provinces.equals("") && DashboardLists.Companiesdata.length()>0)) {
                    Hire_Api(Language);
                }
                if(status.equals("0")){
                    Intouch_Api(Language);
                    Province_Api(Language);
                    Jobcat_Api(Language);
                    status="1";
                }
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            // do whatever needs to be done next
        }
        @Override
        protected void onPreExecute() {
            if(Language.equals("fa")){
                Toast.makeText(context, "در حال بارگزاری...", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
    private void Hire_Api(final String Language){
        ApiHelpers ah = new ApiHelpers(context);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("provinces", provinces)
                .addFormDataPart("corporations", contracts)
                .addFormDataPart("categories", jobcats)
                .build();
        ah.PosttCall(AppConfig.URL_Hires+Language, requestBody, false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("Hire_Api: ", e.getMessage());
                                if (Language.equals("fa"))

                                {
                                    txtEmptyHires.setText("لیست تبلیغ\u200Cها خالی است");
                                    Toast.makeText(context, "مشکلی در اتصال با سرور پیش آمده است", Toast.LENGTH_SHORT).show();
                                } else

                                {
                                    txtEmptyHires.setText("Empty Hire Advertisements!");
                                    Toast.makeText(context, "Network Connection or Server failed!", Toast.LENGTH_SHORT).show();
                                }
                                txtEmptyHires.setVisibility(View.VISIBLE);
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
                                        JSONObject data = new JSONObject(response.body().string()).getJSONObject("data");
                                        if (data.getInt("totalPage") == 0) {
                                            if (Language.equals("fa"))
                                                txtEmptyHires.setText("تبلیغی یافت نشد");
                                            else
                                                txtEmptyHires.setText("Empty Hire Advertisements!");
                                            txtEmptyHires.setVisibility(View.VISIBLE);
                                        } else {
                                            if (data.length() > 0) {
                                                txtEmptyHires.setVisibility(View.GONE);
                                                for (int i = 0; i < data.length() - 1; i++) {
                                                    JSONObject c = data.getJSONObject(i + "");
                                                    HireCompanies a = new HireCompanies(c.getInt("id"), c.getString("image"), c.getString("job_title"), c.getString("brand_name"), c.getString("job_title"), c.getString("province")
                                                            , c.getString("job_description"));
                                                    companyList.add(a);
                                                }
                                                adapter.notifyDataSetChanged();
                                            } else {
                                                if (Language.equals("fa"))
                                                    txtEmptyHires.setText("لیست تبلیغ\u200Cها خالی است!");
                                                else
                                                    txtEmptyHires.setText("Empty Hire Advertisements!");
                                                txtEmptyHires.setVisibility(View.VISIBLE);
                                            }
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
                });
    }
    private void Province_Api(final String Language){
        ApiHelpers ah = new ApiHelpers(context);
        ah.getCall(AppConfig.URL_Provinces, false).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Navigations: ", e.getMessage());
                        hasnav=false;
                        String[][] ListNavigations = new String[0][0];
                        setFilters(Language, "province", ListNavigations);
                    }
                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                JSONArray resarray = new JSONObject(response.body().string()).getJSONArray("data");
                                if(resarray.length()>0){
                                    String[][] ListNavigations = new String[resarray.length()][resarray.length()];
                                    hasnav=true;
                                    if(Language.equals("fa")) {
                                        for (int i = 0; i < resarray.length(); i++) {
                                            try {
                                                ListNavigations[0][i] = resarray.getJSONObject(i).getString("name");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                ListNavigations[1][i] = resarray.getJSONObject(i).getString("id");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }else{
                                        for (int i = 0; i < resarray.length(); i++) {
                                            try {
                                                ListNavigations[0][i] = resarray.getJSONObject(i).getString("name_en");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                ListNavigations[1][i] = resarray.getJSONObject(i).getString("id");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    CheckedNavigations = new boolean[ListNavigations.length];
                                    setFilters(Language, "province", ListNavigations);
                                }
                                else{
                                    hasnav=false;
                                    String[][] ListNavigations = new String[0][0];
                                    setFilters(Language, "province", ListNavigations);
                                    Log.d("Navigations", "error");
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
    private void Jobcat_Api(final String Language) {
        ApiHelpers ah = new ApiHelpers(context);
        ah.getCall(AppConfig.URL_JobCats+Language, false).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        hasjobcat=false;
                        String[][] ListNavigations = new String[0][0];
                        setFilters(Language, "jobcats", ListNavigations);
                        Log.d("JobCats", "error");
                    }
                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                JSONArray resarray = new JSONObject(response.body().string()).getJSONArray("data");
                                if(resarray.length()>0){
                                    hasjobcat=true;
                                    String[][] ListNavigations = new String[resarray.length()][resarray.length()];
                                    for (int i = 0; i < resarray.length(); i++) {
                                        try {
                                            ListNavigations[0][i] = resarray.getJSONObject(i).getString("title");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            ListNavigations[1][i] = resarray.getJSONObject(i).getString("id");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    CheckedJobcats = new boolean[ListNavigations.length];
                                    setFilters(Language, "jobcats", ListNavigations);
                                    //Log.d("JobCats", resarray.get(0).toString());
                                }
                                else {
                                    hasjobcat=false;
                                    String[][] ListNavigations = new String[0][0];
                                    setFilters(Language, "jobcats", ListNavigations);
                                    Log.d("JobCats", "error");
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
    private void Intouch_Api(final String Language) {
        ApiHelpers ah = new ApiHelpers(context);
        ah.getCall(AppConfig.URL_Corporations+Language, false).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        hastouch=false;
                        String[][] ListNavigations = new String[0][0];
                        setFilters(Language, "intouch", ListNavigations);
                    }
                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                JSONArray resarray = new JSONObject(response.body().string()).getJSONArray("data");
                                Log.d("Corporations", resarray.toString());
                                if(resarray.length()>0){
                                    hastouch=true;
                                    String[][] ListNavigations = new String[resarray.length()][resarray.length()];
                                    if(Language.equals("fa")){
                                        for (int i = 0; i < resarray.length(); i++) {
                                            try {
                                                ListNavigations[0][i] = resarray.getJSONObject(i).getString("name");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                ListNavigations[1][i] = resarray.getJSONObject(i).getString("code");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    else {
                                        for (int i = 0; i < resarray.length(); i++) {
                                            try {
                                                ListNavigations[0][i] = resarray.getJSONObject(i).getString("name_other");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                ListNavigations[1][i] = resarray.getJSONObject(i).getString("code");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    CheckedIntouchs = new boolean[ListNavigations.length];
                                    setFilters(Language, "intouch", ListNavigations);
                                    //Log.d("Corporations", resarray.get(0).toString());
                                }
                                else{
                                    hastouch=false;
                                    String[][] ListNavigations = new String[0][0];
                                    setFilters(Language, "intouch", ListNavigations);
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
    private void reloadlist(){
        companyList = new ArrayList<>();

        adapter = new HireAdapter(getContext(), companyList, session);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        startDialog();
    }

    //Hire Api , transactions
    private void setFilters(String Language, String filtername, final String[][] listfilter){

        if(Language.equals("fa")) {

            if (filtername.equals("province")) {
                //NAVIGATION

                        if(listfilter.length>0)
                        {
                        mbuilderNav.setTitle("لیست استان\u200Cها");
                        mbuilderNav.setMultiChoiceItems(listfilter[0], CheckedNavigations, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                if (b) {
                                    if (!UserNavigations.contains(i)) {
                                        UserNavigations.add(i);
                                    }
                                } else if (UserNavigations.contains(i)) {
                                    UserNavigations.remove(UserNavigations.indexOf(i));
                                }
                            }
                        });
                        mbuilderNav.setCancelable(false);
                        mbuilderNav.setPositiveButton("اعمال کن", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                provinces="";
                                String item = "";
                                for (int p = 0; p < UserNavigations.size(); p++) {
                                    item = item + listfilter[1][UserNavigations.get(p)];
                                    if (p != UserNavigations.size() - 1)
                                        item = item + ",";
                                }
                                //Log.d("FILTERS OF CITY", item);
                                provinces = item;
                                reloadlist();
                                fastList();
                            }
                        });
                        mbuilderNav.setNegativeButton("بستن", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        mbuilderNav.setNeutralButton("پاک کردن همه", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                for (int p = 0; p < CheckedNavigations.length; p++) {
                                    CheckedNavigations[p] = false;
                                    UserNavigations.clear();
                                }
                                provinces="";
                                reloadlist();
                                fastList();
                            }
                        });
                    }
            } else if (filtername.equals("jobcats")) {
                //JobCats

                        if (listfilter.length > 0) {
                            mbuilderjobCat.setTitle("لیست دسته\u200Cبندی\u200Cهای شغلی");
                            mbuilderjobCat.setMultiChoiceItems(listfilter[0], CheckedJobcats, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                    if (b) {
                                        if (!UserJobcats.contains(i)) {
                                            UserJobcats.add(i);
                                        }
                                    } else if (UserJobcats.contains(i)) {
                                        UserJobcats.remove(UserJobcats.indexOf(i));
                                    }
                                }
                            });
                            mbuilderjobCat.setCancelable(false);
                            mbuilderjobCat.setPositiveButton("اعمال کن", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    jobcats="";
                                    String item = "";
                                    for (int p = 0; p < UserJobcats.size(); p++) {
                                        item = item + listfilter[1][UserJobcats.get(p)];
                                        if (p != UserJobcats.size() - 1)
                                            item = item + ",";
                                    }
                                    Log.d("FILTERS OF JobCats", "onClick: " + item);
                                    jobcats=item;
                                    reloadlist();
                                    fastList();
                                }
                            });
                            mbuilderjobCat.setNegativeButton("بستن", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                            mbuilderjobCat.setNeutralButton("پاک کردن همه", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    for (int p = 0; p < CheckedJobcats.length; p++) {
                                        CheckedJobcats[p] = false;
                                        UserJobcats.clear();
                                    }
                                    jobcats="";
                                    reloadlist();
                                    fastList();
                                }
                            });
                        }
            }
            else if(filtername.equals("intouch")) {

                //Intouch
                        if(listfilter.length>0) {
                            mbuilderIntouchs.setTitle("لیست قراردادها");
                            mbuilderIntouchs.setMultiChoiceItems(listfilter[0], CheckedIntouchs, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                    if (b) {
                                        if (!UserIntouchs.contains(i)) {
                                            UserIntouchs.add(i);
                                        }
                                    } else if (UserIntouchs.contains(i)) {
                                        UserIntouchs.remove(UserIntouchs.indexOf(i));
                                    }
                                }
                            });
                            mbuilderIntouchs.setCancelable(false);
                            mbuilderIntouchs.setPositiveButton("اعمال کن", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    contracts="";
                                    String item = "";
                                    for (int p = 0; p < UserIntouchs.size(); p++) {
                                        item = item + listfilter[1][UserIntouchs.get(p)];
                                        if (p != UserIntouchs.size() - 1)
                                            item = item + ",";
                                    }
                                    Log.d("FILTERS OF JobCats", "onClick: " + item);
                                    contracts=item;
                                    reloadlist();
                                    fastList();
                                }
                            });
                            mbuilderIntouchs.setNegativeButton("بستن", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                            mbuilderIntouchs.setNeutralButton("پاک کردن همه", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    for (int p = 0; p < CheckedIntouchs.length; p++) {
                                        CheckedIntouchs[p] = false;
                                        UserIntouchs.clear();
                                    }
                                    contracts="";
                                    reloadlist();
                                    fastList();
                                }
                            });
                        }
            }
        }
        else{
            //ENGLISH
            if (filtername.equals("province")) {
                //NAVIGATIOM
                        if(listfilter.length>0) {
                            mbuilderNav.setTitle("List of provinces");
                            mbuilderNav.setMultiChoiceItems(listfilter[0], CheckedNavigations, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                    if (b) {
                                        if (!UserNavigations.contains(i)) {
                                            UserNavigations.add(i);
                                        }
                                    } else if (UserNavigations.contains(i)) {
                                        UserNavigations.remove(UserNavigations.indexOf(i));
                                    }
                                }
                            });
                            mbuilderNav.setCancelable(false);
                            mbuilderNav.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String item = "";
                                    provinces="";
                                    for (int p = 0; p < UserNavigations.size(); p++) {
                                        item = item + listfilter[1][UserNavigations.get(p)];
                                        if (p != UserNavigations.size() - 1)
                                            item = item + ",";
                                    }
                                    Log.d("FILTERS OF CITY", "onClick: " + item);
                                    provinces=item;
                                    reloadlist();
                                    fastList();
                                }
                            });
                            mbuilderNav.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                            mbuilderNav.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    for (int p = 0; p < CheckedNavigations.length; p++) {
                                        CheckedNavigations[p] = false;
                                        UserNavigations.clear();
                                    }
                                    provinces="";
                                    reloadlist();
                                    fastList();
                                }
                            });
                        }
            }
            else if (filtername.equals("jobcats")) {
                //JobCats
                        if(listfilter.length>0) {
                            mbuilderjobCat.setTitle("Job Categories");
                            mbuilderjobCat.setMultiChoiceItems(listfilter[0], CheckedJobcats, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                    if (b) {
                                        if (!UserJobcats.contains(i)) {
                                            UserJobcats.add(i);
                                        }
                                    } else if (UserJobcats.contains(i)) {
                                        UserJobcats.remove(UserJobcats.indexOf(i));
                                    }
                                }
                            });
                            mbuilderjobCat.setCancelable(false);
                            mbuilderjobCat.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    jobcats="";
                                    String item = "";
                                    for (int p = 0; p < UserJobcats.size(); p++) {
                                        item = item + listfilter[1][UserJobcats.get(p)];
                                        if (p != UserJobcats.size() - 1)
                                            item = item + ",";
                                    }
                                    Log.d("FILTERS OF JobCats", "onClick: " + item);
                                    jobcats=item;
                                    reloadlist();
                                    fastList();
                                }
                            });
                            mbuilderjobCat.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                            mbuilderjobCat.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    for (int p = 0; p < CheckedJobcats.length; p++) {
                                        CheckedJobcats[p] = false;
                                        UserJobcats.clear();
                                    }
                                    jobcats="";
                                    reloadlist();
                                    fastList();
                                }
                            });
                        }
            }
            else if (filtername.equals("intouch")) {
                //Intouch
                        if(listfilter.length>0) {
                            mbuilderIntouchs.setTitle("Corporations");
                            mbuilderIntouchs.setMultiChoiceItems(listfilter[0], CheckedIntouchs, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                    if (b) {
                                        if (!UserIntouchs.contains(i)) {
                                            UserIntouchs.add(i);
                                        }
                                    } else if (UserIntouchs.contains(i)) {
                                        UserIntouchs.remove(UserIntouchs.indexOf(i));
                                    }
                                }
                            });
                            mbuilderIntouchs.setCancelable(false);
                            mbuilderIntouchs.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    contracts="";
                                    String item = "";
                                    for (int p = 0; p < UserIntouchs.size(); p++) {
                                        item = item + listfilter[1][UserIntouchs.get(p)];
                                        if (p != UserIntouchs.size() - 1)
                                            item = item + ",";
                                    }
                                    Log.d("FILTERS OF Intouchs", "onClick: " + item);
                                    contracts=item;
                                    reloadlist();
                                    fastList();
                                }
                            });
                            mbuilderIntouchs.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                            mbuilderIntouchs.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    for (int p = 0; p < CheckedIntouchs.length; p++) {
                                        CheckedIntouchs[p] = false;
                                        UserIntouchs.clear();
                                    }
                                    contracts="";
                                    reloadlist();
                                    fastList();
                                }
                            });
                        }
            }
        }
    }
    //Progress Dialog
    private void startDialog(){
        pDialog.setCancelable(true);
        if(Language.equals("fa"))
            pDialog.setMessage("گرفتن لیست آگهی\u200Cهای استخدام...");
        else
            pDialog.setMessage("Loading Hiring Advertisements...");
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
    //Translation
    private String updateLanguage(){
        //Default language is fa
        String language = Paper.book().read("language");
        if(language==null)
            Paper.book().write("language", "fa");
        return language;
    }
    private void updateView(String language) {
        Context context = LocaleHelper.setLocale(getActivity(), language);
        Resources resources = context.getResources();
        mytextHirePerson.setText(resources.getString(R.string.HirePerson));

        //condition to check language
        if(Language.equals("fa")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                recyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                recyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        }
        btnNavigation.setText(resources.getString(R.string.Province));
        btnJobCats.setText(resources.getString(R.string.JobCategory2));
        btnIntouch.setText(resources.getString(R.string.Cooperation));
    }

}
