package com.mrehya.Hire;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mrehya.ApiHelpers.ApiHelpers;
import com.mrehya.AppConfig;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.R;
import com.mrehya.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import okhttp3.Call;
import okhttp3.Callback;

public class ShowHireStatus extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HireAdapter adapter;
    private List<HireCompanies> companyList;
    private SessionManager session;
    private TextView txtEmptyCompaniesReqs;
    private String Language="fa";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PolicyGuard.Allow();
        setContentView(R.layout.activity_show_hire_status);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        session = new SessionManager(getApplicationContext());
        txtEmptyCompaniesReqs = (TextView) findViewById(R.id.txtEmptyCompaniesReqs);

        companyList = new ArrayList<>();
        adapter = new HireAdapter(getApplicationContext(), companyList, session);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        //new
        Paper.init(this);
        Language = updateLanguage();
        Hire_Api(Language);
    }

    //APIS
    private void Hire_Api(final String Language){
        Toast.makeText(getApplicationContext(), "در حال بارگزاری لیست شرکت ها...", Toast.LENGTH_SHORT).show();
        ApiHelpers ah = new ApiHelpers(this);
        ah.getCall(AppConfig.URL_Hires, false).enqueue(

                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Hires: ", e.getMessage());
                        if(Language.equals("fa")){
                            txtEmptyCompaniesReqs.setText("لیست شرکت\u200Cها خالی است");
                            Toast.makeText(getApplicationContext(), "مشکلی در اتصال با سرور پیش آمده است", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            txtEmptyCompaniesReqs.setText("No Comapanies Loaded!");
                            Toast.makeText(getApplicationContext(), "Network Connection or Server failed!", Toast.LENGTH_SHORT).show();
                        }
                        txtEmptyCompaniesReqs.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject data = new JSONObject(response.body().string()).getJSONObject("data");
                                        if(data.length()>0){
                                            txtEmptyCompaniesReqs.setVisibility(View.GONE);
                                            for (int i = 0; i < data.length()-1; i++) {
                                                JSONObject c = data.getJSONObject(i+"");
                                                //Log.d("TAG", c.toString());
                                                HireCompanies a = new HireCompanies(c.getInt("id"),c.getString("image"),c.getString("brand_name"),c.getString("brand_name"),c.getString("job_title"),c.getString("province")
                                                        ,c.getString("job_description"));
                                                companyList.add(a);
                                            }

                                            adapter.notifyDataSetChanged();
                                        }
                                        else{
                                            if(Language.equals("fa"))
                                                txtEmptyCompaniesReqs.setText("لیست شرکت\u200Cها خالی است");
                                            else
                                                txtEmptyCompaniesReqs.setText("No Comapanies Loaded!");
                                            txtEmptyCompaniesReqs.setVisibility(View.VISIBLE);
                                        }
                                        Log.d("TAG", "No Object recieved!");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
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

}
