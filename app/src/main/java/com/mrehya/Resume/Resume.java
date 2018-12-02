package com.mrehya.Resume;

import android.content.Context;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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
import com.mrehya.Exams.Exam;
import com.mrehya.Exams.Question;
import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.R;
import com.mrehya.SessionManager;
import com.mrehya.UserAccount.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by ashke98 on 7/30/2018.
 */

public class Resume {

    //GLOBALS
    private SessionManager session;
    private Context context;

    //ELEMENTS
    String
            id;
    String email;
    String job_title;
    String slug;
    String address;
    String job_status;
    String certificate;
    String province;
    String marital;
    String militarystatus;
    String about_me;
    String image;
    String fullname;
    String link;
    String sex;
    String levels;
    String salary;
    String categories;
    String benefits;
    String provinces;
    String contracts;
    String experiences;
    String academics;
    String languages;
    String mobile;
    String year_birth;
    String skills;
    boolean loaded;
    private ApiHelpers ah;

    private List<String>
            allProvinces,
            allJobcats,
            allcontracts,
            allSenorities,
            allBenefits;

    public Resume(Context context){
        this.context = context;
        session = new SessionManager(context);
        this.ah = new ApiHelpers(context);
    }
    public Resume(Context context, List<String> allProvinces){
        this.context = context;
        session = new SessionManager(context);
        this.allProvinces = allProvinces;
        this.ah = new ApiHelpers(context);
    }
    public Resume(Context context, List<String> allProvinces, List<String> allJobcats,
                  List<String> allcontracts,List<String> allSenorities){
        this.context = context;
        session = new SessionManager(context);
        this.allProvinces = allProvinces;
        this.allJobcats = allJobcats;
        this.allcontracts = allcontracts;
        this.allSenorities = allSenorities;
        this.ah = new ApiHelpers(context);
        context = LocaleHelper.setLocale(context, "fa");
        this.allBenefits = Arrays.asList(context.getResources().getStringArray(R.array.benefitArray));
    }



    public String getYear_birth() {
        return year_birth;
    }

    public void setYear_birth(String year_birth) {
        this.year_birth = year_birth;
    }


    public boolean getLoaded() {
        return loaded;
    }

    public void setLoaded(boolean Loaded) {
        this.loaded = Loaded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        email = email;
    }

    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        slug = slug;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        address = address;
    }

    public String getJob_status() {
        return job_status;
    }

    public void setJob_status(String job_status) {
        this.job_status = job_status;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getMarital() {
        return marital;
    }

    public void setMarital(String marital) {
        this.marital = marital;
    }

    public String getMilitarystatus() {
        return militarystatus;
    }

    public void setMilitarystatus(String militarystatus) {
        this.militarystatus = militarystatus;
    }

    public String getAbout_me() {
        return about_me;
    }

    public void setAbout_me(String about_me) {
        this.about_me = about_me;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLevels() {
        return levels;
    }

    public void setLevels(String levels) {
        this.levels = levels;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getProvinces() {
        return provinces;
    }

    public void setProvinces(String provinces) {
        this.provinces = provinces;
    }

    public String getContracts() {
        return contracts;
    }

    public void setContracts(String contracts) {
        this.contracts = contracts;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getExperiences() {
        return experiences;
    }

    public void setExperiences(String experiences) {
        this.experiences = experiences;
    }

    public String getAcademics() {
        return academics;
    }

    public void setAcademics(String academics) {
        this.academics = academics;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Resume(String email, String job_title, String slug, String address, String job_status, String certificate,
                  String province, String marital, String militarystatus, String image, String sex,  String mobile, String year_birth) {
        this.email = email;
        this.job_title = job_title;
        this.slug = slug;
        this.address = address;
        this.job_status = job_status;
        this.certificate = certificate;
        this.province = province;
        this.marital = marital;
        this.militarystatus = militarystatus;
        this.image = image;
        this.sex = sex;
        this.mobile = mobile;
        this.year_birth = year_birth;
    }

    public Resume(String id, String email, String job_title, String slug, String address, String job_status,
                  String certificate, String province, String marital, String militarystatus,
                  String about_me, String image, String fullname, String link, String sex,
                  String levels, String salary, String categories, String benefits, String provinces,
                  String contracts, String skills, String experiences, String academics,
                  String languages, String mobile, String year_birth) {
        this.id = id;
        this.email = email;
        this.job_title = job_title;
        this.slug = slug;
        this.address = address;
        this.job_status = job_status;
        this.certificate = certificate;
        this.province = province;
        this.marital = marital;
        this.militarystatus = militarystatus;
        this.about_me = about_me;
        this.image = image;
        this.fullname = fullname;
        this.link = link;
        this.sex = sex;
        this.levels = levels;
        this.salary = salary;
        this.categories = categories;
        this.benefits = benefits;
        this.provinces = provinces;
        this.contracts = contracts;
        this.skills = skills;
        this.experiences = experiences;
        this.academics = academics;
        this.languages = languages;
        this.mobile = mobile;
        this.year_birth = year_birth;
    }

    //get resume from cloud
    public void get_resume_api(){
        PolicyGuard.Allow();
        ah.getCall(AppConfig.URL_GETAPI_RESUME + session.getUserDetails().getResume(), false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                Log.e("Resume_Get", e.getMessage());
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
                                        if(response.code() == 200) {
                                            String DataString = response.body().string();
                                            JSONObject json = new JSONObject(DataString);
                                            JSONObject data = new JSONObject(DataString).getJSONObject("data");
                                            String success = json.getString("success");

                                            if (success.equalsIgnoreCase("true")) {

                                                session.setResumeId(data.getString("id"));

                                                //ADD SKILLS TO STRING WITH ,
                                                String skills = "";
                                                JSONArray Skilljsarray = new JSONArray(data.getString("skills"));
                                                for(int i=0;i<Skilljsarray.length();i++){
                                                    skills+=Skilljsarray.getString(i)+",";
                                                }

                                                session.set_Resume(
                                                        data.getString("id"),
                                                        data.getString("email"),
                                                        data.getString("job_title"),// c.getString("address"),
                                                        data.getString("job_status"), "0",
                                                        data.getString("province"),
                                                        data.getString("martial"),// c.getString("military_status"),
                                                        data.getString("about_me"),
                                                        data.getString("image"),
                                                        data.getString("fullname"),
                                                        data.getString("link"),
                                                        data.getString("sex"),
                                                        data.getString("levels"),
                                                        data.getString("salary"),
                                                        data.getString("categories"),
                                                        data.getString("benefits"),
                                                        data.getString("provinces"),
                                                        data.getString("contracts"), skills!=""? skills.substring(0, skills.length() - 1):null ,
                                                        data.getString("experiences"),
                                                        data.getString("academics"),
                                                        data.getString("languages"),
                                                        data.getString("mobile"),
                                                        data.getString("year_birth"),
                                                        data.getString("sex")
                                                );
                                                session.setLoaded(true);
                                            } else {
                                                Log.e("get_resume_api", "failed to get resume trycatch");
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else{
                                    //not success response
                                    Log.e("get_resume_api", "failed to get resume trycatch");
                                }
                                //after run
                            }
                        });
                    }
                }
        );
    }

    //set resume in cloud
    private  RequestBody requestbody() {
        try {
            //part1
            String province = "";
            if(IsNumeric(session.get_Resume().getProvince()))
                province= session.get_Resume().getProvince();
            else {
                province = (allProvinces.indexOf(session.get_Resume().getProvince().replace("ي","ی"))+1)+"";
            }

            String martial = "";
            if(IsNumeric(session.get_Resume().getMarital()))
            {
                martial = session.get_Resume().getMarital();
            }
            else {
                martial = session.get_Resume().getMarital().equals("مجرد")?"2":"1";
            }
            String sex = "";
            if(IsNumeric(session.get_Resume().getSex()))
                sex = session.get_Resume().getSex();
            else {
                sex = session.get_Resume().getSex().equals("مرد")?"1":"2";
            }

            //part2
            //SKILLS
            String skills="";
            if(session.get_Resume().getSkills()==null)
            {
                session.get_Resume().setSkills("مهارتی اضافه نشده است");
                skills = "مهارتی اضافه نشده است";
            }
            else{
                skills = session.get_Resume().getSkills();
            }
            //PROVINCES
            String provinces = "";
            if(session.get_Resume().getProvinces() == null)
                province = "";
            else{
                if(IsNumeric(session.get_Resume().getProvinces().split(",")[0]))
                {
                    provinces=session.get_Resume().getProvinces();
                }
                else {
                    String items = "";
                    for (String item : session.get_Resume().getProvinces().split(",")) {
                        items= items + (allProvinces.indexOf(item.replace("ي","ی"))+1)+",";
                    }
                    //Log.e("pros", items);
                    if(items.length()>0){
                        items = items.substring(0,items.length()-1);
                        provinces = items;
                    }
                }
                //params.put("Resume[provinces][]", session.get_Resume().getProvinces());  ??????
            }
            //JOB CATS
            String jobcats = "";
            if(session.get_Resume().getCategories() == null)
                jobcats = "";
            else{
                if(IsNumeric(session.get_Resume().getCategories().split(",")[0]))
                    jobcats = session.get_Resume().getCategories();
                else {
                    String items = "";
                    for (String item : session.get_Resume().getCategories().split(",")) {
                        items= items + (allJobcats.indexOf(item.replace("ي","ی"))+1)+",";
                    }
                    //Log.e("categories","HERE : " +  items);
                    if(items.length()>0){
                        items = items.substring(0,items.length()-1);
                        jobcats = items;
                    }
                }
            }
            //CONTRACTS
            String contracts = "";
            if(session.get_Resume().getContracts()==null)
            {
                contracts = "";
            }
            else{
                if(IsNumeric(session.get_Resume().getContracts().split(",")[0]))
                    contracts = session.get_Resume().getContracts();
                else {
                    String items = "";
                    for (String item : session.get_Resume().getContracts().split(",")) {
                        items= items + (allcontracts.indexOf(item.replace("ي","ی"))+1)+",";
                    }
                    //Log.e("categories","HERE : " +  items);
                    if(items.length()>0){
                        items = items.substring(0,items.length()-1);
                        contracts = items;
                    }
                }
            }
            //LEVELS
            String levels = "";
            if (session.get_Resume().getLevels()==null || session.get_Resume().getLevels().length()==0)
                levels = "0";
            else{
                if(IsNumeric(session.get_Resume().getLevels().split(",")[0]))
                    levels = session.get_Resume().getLevels();
                else {
                    String items = "";
                    for (String item : session.get_Resume().getLevels().split(",")) {
                        items= items + (allSenorities.indexOf(item.replace("ي","ی"))+1)+",";
                    }
                    //Log.e("categories","HERE : " +  items);
                    if(items.length()>0){
                        items = items.substring(0,items.length()-1);
                        levels = items;
                    }
                }
            }

            //part3
            //BENEFITS
            //Log.e("Saving bnfit", session.get_Resume().getBenefits());
            String benefits = "";
            if(session.get_Resume().getBenefits()!=null){
                if(!session.get_Resume().getBenefits().equals("null")){
                    if(session.get_Resume().getBenefits().split(",").length>0)
                    {
                        if(IsNumeric(session.get_Resume().getBenefits().split(",")[0])){
                            //Log.e("if 1 benefits","HERE : " +  session.get_Resume().getBenefits());
                            benefits = session.get_Resume().getBenefits();
                        }
                        else {
                            String items = "";
                            for (String item : session.get_Resume().getBenefits().split(",")) {

                                int index=(allBenefits.indexOf(item.replace("ي","ی"))+1);
                                items= items +index +",";
                                //Log.e("if 2 benefits","item1: " +  item +" item 2: " +allBenefits.get(index-1));
                            }
                            //Log.e("if 2 benefits","HERE : " +  items);
                            if(items.length()>0){
                                items = items.substring(0,items.length()-1);
                                benefits = items;
                            }
                        }
                    }
                }
            }
            String salary = "";
            if(session.get_Resume().getSalary()!=null)
                salary=session.get_Resume().getSalary();
            String aboutme = "";
            if(session.get_Resume().getAbout_me()!=null)
                aboutme=session.get_Resume().getAbout_me();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("auth", session.getUserDetails().getToken())
                    //part1
                    .addFormDataPart("Resume[job_title]", session.get_Resume().getJob_title())
                    .addFormDataPart("Resume[job_status]", session.get_Resume().getJob_status())
                    .addFormDataPart("Resume[year_birth]", session.get_Resume().getYear_birth())
                    .addFormDataPart("Resume[provinceId]", province)
                    .addFormDataPart("Resume[martial]", martial)
                    .addFormDataPart("Resume[sex]", sex)
                    //part2
                    .addFormDataPart("Resume[skills]", skills)
                    .addFormDataPart("Resume[provinces][]", provinces)
                    .addFormDataPart("categories", jobcats)
                    .addFormDataPart("Resume[contracts][]", contracts)
                    .addFormDataPart("Resume[levels]", levels)
                    //part3
                    .addFormDataPart("Resume[benefits][]", benefits)
                    .addFormDataPart("Resume[salary]", salary)
                    .addFormDataPart("Resume[about_me]", aboutme)
                    .build();
            return requestBody;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void set_resume_api(){
        PolicyGuard.Allow();
        final String nowLanguage = Paper.book().read("language");
        RequestBody requestBody = requestbody();
        if(requestBody != null){
            ah.PosttCall(AppConfig.URL_UPDATE_RESUME, requestBody, false).enqueue(
                    new Callback() {
                        Handler mainHandler = new Handler(context.getMainLooper());
                        @Override
                        public void onFailure(Call call, final IOException e) {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("set_resume_api", e.getMessage());
                                    if(nowLanguage.equals("fa")){
                                        Toast.makeText(context, "مشکلی در بروزرسانی رزومه تان بوجود آمده است!", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(context, "Problem on updating Resume!", Toast.LENGTH_SHORT).show();
                                    }
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
                                    Log.e("get_show_Results", DataString);
                                    if (response.isSuccessful()) {
                                        Log.e("response.isSuccessful()", true+"");
                                        try {
                                            JSONObject json = new JSONObject(DataString);
                                            String success = json.getString("success");
                                            if (success.equalsIgnoreCase("true")) {
                                                JSONArray jsonarray = new JSONArray(json.getString("data"));
                                                //Log.e("DATA OF RESUME UPDATE", jsonarray.toString());
                                                if(nowLanguage.equals("fa")){
                                                    Toast.makeText(context, "با موفقیت بروزرسانی شد", Toast.LENGTH_SHORT).show(); }
                                                else{
                                                    Toast.makeText(context, "Updated successfully!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        } catch (JSONException e) {
                                            Log.e("set_resume_api", "failed to set_resume_api");
                                            e.printStackTrace();
                                            if(nowLanguage.equals("fa")){
                                                Toast.makeText(context, "مشکلی در بروزرسانی رزومه تان بوجود آمده است!", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(context, "Problem on updating Resume!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                    else {
                                        Log.e("set_resume_api", "failed to set_resume_api");
                                        if(nowLanguage.equals("fa")){
                                            Toast.makeText(context, "مشکلی در بروزرسانی رزومه تان بوجود آمده است!", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(context, "Problem on updating Resume!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }
                    });
        }
    }

    private Boolean IsNumeric(String string) {
            boolean numeric = true;
            try {
                Double num = Double.parseDouble(string);
            } catch (NumberFormatException e) {
                numeric = false;
            }
            return numeric;
        }
}
