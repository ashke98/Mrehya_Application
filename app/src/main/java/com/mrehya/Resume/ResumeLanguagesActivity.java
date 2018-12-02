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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.mrehya.Lang;
import com.mrehya.ListAdapterLang;
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
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ResumeLanguagesActivity extends AppCompatActivity {
    private Context context;
    private String Language;
    private SessionManager session;
    private Resources resources;
    private Resume resume;
    //ELEMENTS
    private Button btn_saveresume,btn_back;
    private TextView txttitle;
    private ListView listViewLang;
    private ImageButton btnAddLang;
    private ArrayList<Lang> listLang, oldLang;
    private ListAdapterLang listAdapterLang;
    private ArrayList<Integer> ids;

    private List<String> levels, names;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_languages);
        PolicyGuard.Allow();
        session = new SessionManager(getApplicationContext());
        Intent intent = getIntent();
        Language = intent.getStringExtra("Language");

        context = LocaleHelper.setLocale(getApplicationContext(), Language);
        resources = context.getResources();
        findviews();
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
        listViewLang = (ListView) findViewById(R.id.listViewLang);
        btnAddLang = (ImageButton) findViewById(R.id.btnAddLang);
        listViewLang.setAdapter(listAdapterLang);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_saveresume = (Button) findViewById(R.id.btn_saveresume);
        resume= new Resume(ResumeLanguagesActivity.this);
        ids = new ArrayList<>();

        levels = Arrays.asList(resources.getStringArray(R.array.langLevels));
        names =   Arrays.asList(resources.getStringArray(R.array.langs));
        setLangList();
    }
    public void setOnclicks(){
        btnAddLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAddLang(listViewLang);
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
            txttitle.setText("زبان ها");
        }
        else{
            txttitle.setText("Languages");
        }
        listViewLang.setAdapter(listAdapterLang);
    }
    public void setDefaults() throws JSONException {
        if(session.get_Resume().getLanguages()!=null){
            JSONArray array = new JSONArray(session.get_Resume().getLanguages().toString());
            oldLang = new ArrayList<>();
            for (int i=0 ; i<array.length();i++)
            {
                JSONObject c= array.getJSONObject(i);
                Log.e("default", c.getString("language")+","+ c.getString("level"));
                Lang lang = new Lang((names.indexOf(c.getString("language").replace("ي","ی"))+1)+""
                        ,(levels.indexOf(c.getString("level").replace("ي","ی"))+1)+"" );
                lang.setId(c.getInt("id"));
                Boolean exist = false;
                for (Lang item:oldLang
                     ) {
                    if(item.getLevel().equals(lang.getLevel()) && item.getName().equals(lang.getName())){
                        exist = true;
                        break;
                    }
                }
                if(!exist){
                    listLang.add(lang);
                    oldLang.add(lang);
                }

                listAdapterLang.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listViewLang);
            }
        }
        else{
            JSONArray array = new JSONArray();
            oldLang = new ArrayList<>();
            for (int i=0 ; i<array.length();i++)
            {
                JSONObject c= array.getJSONObject(i);
                Log.e("default", c.getString("language")+","+ c.getString("level"));
                Lang lang = new Lang((names.indexOf(c.getString("language").replace("ي","ی"))+1)+""
                        ,(levels.indexOf(c.getString("level").replace("ي","ی"))+1)+"" );
                Boolean exist = false;
                for (Lang item:oldLang
                        ) {
                    if(item.getLevel().equals(lang.getLevel()) && item.getName().equals(lang.getName())){
                        exist = true;
                        break;
                    }
                }
                if(!exist){
                    listLang.add(lang);
                    oldLang.add(lang);
                }
                listAdapterLang.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listViewLang);
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
        for(final Lang item: listLang){
            ids.add(item.getId());
        }

        for (final Lang item : oldLang) {
            String req = "";
            if(item.getId() == 0){
                if (ids.contains(item.getId()))
                {
                    req="https://api.mrehya.com/v1/user/language";
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
                    for (Lang item2: listLang) {
                        if(item2.getId() == item.getId()){
                            item.setLevel(item2.getLevel());
                            item.setName(item2.getName());
                            break;
                        }
                    }
                }
                req = "https://api.mrehya.com/v1/user/language?id="+item.getId();
            }
            saveApi(resumeid, req, item);
        }
    }
    private RequestBody requestbody(final Lang item, String resumeid){
        Log.e("default", item.getName()+","+ item.getLevel());
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("auth", session.getUserDetails().getToken())
                    .addFormDataPart("LangResume[language]", item.getName())
                    .addFormDataPart("LangResume[level]", item.getLevel())
                    .addFormDataPart("LangResume[resumeId]", resumeid)
                    .build();
            return requestBody;
    }
    private void saveApi(String resumeid, String req, final Lang item){
        ApiHelpers ah = new ApiHelpers(context);
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
                                    Log.e("Languages", e.getMessage());
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
                                    Log.e("Languages", DataString);
                                    if (response.isSuccessful()) {
                                        try {
                                            JSONObject json = new JSONObject(DataString);
                                            String success = json.getString("success");
                                            if (success.equalsIgnoreCase("true")) {
                                                if(Language.equals("fa")){
                                                    Toast.makeText(ResumeLanguagesActivity.this, "با موفقیت بروزرسانی شد", Toast.LENGTH_SHORT).show(); }
                                                else{
                                                    Toast.makeText(ResumeLanguagesActivity.this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                                                }
                                                resume.get_resume_api();
                                            }
                                        } catch (JSONException e) {
                                            Log.e("Languages", "failed to Languages");
                                        }
                                    }
                                    else {
                                        Log.e("Languages", "failed to Languages");
                                    }
                                }
                            });
                        }
                    });
        }
    }

    //methods
    private void setLangList(){
        listLang = new ArrayList<>();
        listAdapterLang = new ListAdapterLang(listLang,getApplicationContext(),ResumeLanguagesActivity.this,listViewLang);
    }
    public boolean existslang(String name, String level){
        for (Lang item: listLang) {
            if(item.getName().equals(name) && item.getLevel().equals(level))
                return true;
        }
        return false;
    }
    private void showDialogAddLang(final ListView listView){
        final Dialog dialog = new Dialog(ResumeLanguagesActivity.this);
        dialog.setContentView(R.layout.list_dialog_lang);
        //final EditText txtEdit = dialog.findViewById(R.id.txtEditDialogLang);
        final Spinner spinnerLevel = dialog.findViewById(R.id.spinnerLangLevel);
        final Spinner spinnerLang = dialog.findViewById(R.id.spinnerLang);

        LinearLayout LinearLayout1 = dialog.findViewById(R.id.LinearLayoutmain);

        TextView btnEditd = dialog.findViewById(R.id.btnEditd);


        if(Language.equals("fa"))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                LinearLayout1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                spinnerLang.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                spinnerLang.setGravity(Gravity.RIGHT);
                spinnerLang.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            }
            btnEditd.setText("اضافه کردن");

            ArrayAdapter<String> Leveladapter = new ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.langLevels)
            );
            Leveladapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
            spinnerLevel.setAdapter(Leveladapter);

            ArrayAdapter<String> Langadapter = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_rtl,
                    resources.getStringArray(R.array.langs)
            );
            Langadapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
            spinnerLang.setAdapter(Langadapter);
        }

        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                LinearLayout1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                spinnerLang.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            btnEditd.setText("Add");

            ArrayAdapter<String> Leveladapter = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_ltr,
                    levels
            );
            Leveladapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
            spinnerLevel.setAdapter(Leveladapter);

            ArrayAdapter<String> Langadapter = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_ltr,
                    names
            );
            Langadapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
            spinnerLang.setAdapter(Langadapter);
        }

        ImageButton btnEdit = dialog.findViewById(R.id.btnEditDialogLang);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e( "add"
                        ,names.indexOf( spinnerLang.getSelectedItem().toString())+"," +levels.indexOf(spinnerLevel.getSelectedItem().toString()));
                if(!(existslang((names.indexOf( spinnerLang.getSelectedItem().toString()))+""
                        ,(levels.indexOf(spinnerLevel.getSelectedItem().toString())+1)+""))){

                   Lang lang = new Lang((names.indexOf( spinnerLang.getSelectedItem().toString())+1)+""
                            ,(levels.indexOf(spinnerLevel.getSelectedItem().toString())+1)+"");
                    lang.setId(0);
                    listLang.add(lang);
                    oldLang.add(lang);
                    listAdapterLang.notifyDataSetChanged();
                    justifyListViewHeightBasedOnChildren(listView);
                    dialog.dismiss();
                }
                else
                    Log.e("LANG", "Exists");
            }
        });
        dialog.show();
        dialog.getWindow().setLayout((6 * width)/7, ViewGroup.LayoutParams.WRAP_CONTENT);
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
}
