package com.mrehya.Resume;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Image;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mrehya.Education;
import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.Job;
import com.mrehya.Lang;
import com.mrehya.ListAdapterEducation;
import com.mrehya.ListAdapterJobcat;
import com.mrehya.ListAdapterJobs;
import com.mrehya.ListAdapterLang;
import com.mrehya.ListAdapterProes;
import com.mrehya.ListAdapterProvince;
import com.mrehya.ListAdapterSkill;
import com.mrehya.ListAdapterTerms;
import com.mrehya.R;
import com.mrehya.Resume.ResumeListAdapters.ListAdapterSimpleEducation;
import com.mrehya.Resume.ResumeListAdapters.ListAdapterSimpleJobcat;
import com.mrehya.Resume.ResumeListAdapters.ListAdapterSimpleJobs;
import com.mrehya.Resume.ResumeListAdapters.ListAdapterSimpleLang;
import com.mrehya.Resume.ResumeListAdapters.ListAdapterSimpleProes;
import com.mrehya.Resume.ResumeListAdapters.ListAdapterSimpleProvince;
import com.mrehya.Resume.ResumeListAdapters.ListAdapterSimpleSkill;
import com.mrehya.Resume.ResumeListAdapters.ListAdapterSimpleTerms;
import com.mrehya.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResumeViewActivity extends AppCompatActivity {
    private String Language="fa";
    private SessionManager session;
    private Resources resources;
    private Context context;

    //ELMENTS
    private LinearLayout
            LinearLayoutResumeView,
            LinearLayoutresumeJob,
            LinearLayoutPrivateInfoTitle, LinearLayoutPrivateInfo,
            LinearLayoutTalentsTitle,
            LinearLayoutJobExp,
            LinearLayoutEducation,
            LinearLayoutLanguages,
            LinearLayoutJobFavoritesTitle,
            LinearLayouttxtSelectedProvincesTitle,
            LinearLayoutJobCatTitle,
            LinearLayoutContractsTitle,
            LinearLayoutLevelsTitle,
            LinearLayoutSalaryTitle,
            LinearLayoutSalary,
            LinearLayoutBenefitsTitle,
            LinearLayoutBenefits,
            LinearLayoutAboutMeTitle;

    private TextView
            txtJobTitle,
            txtJobTitleTitle,
            txtjob_status,
            txtjob_statusTitle,
            txtLastDegree,
            txtLastDegreeTitle,
            txtPrivateInfoTitle,
            txtEmailAddress,
            txtEmailAddressTitle,
            txtPhone,
            txtPhoneTitle,
            txtProvinceResume,
            txtProvinceResumeTitle,
            txtMarriageResume,
            txtMarriageResumeTitle,
            txtGenderResume,
            txtGenderResumeTitle,
            txtBirthYearResume,
            txtBirthYearResumeTitle,
            txtDutyResume,
            txtDutyResumeTitle,
            txtAddressResume,
            txtAddressResumeTitle,
            txtTalentsTitle,
            txtJobExpTitle,
            txtEducationTitle,
            txtLanguages,
            txtJobFavoritesTitle,
            txtSelectedProvincesTitle,
            txtJobCatTitle,
            txtContractsTitle,
            txtLevelsTitle,
            txtSalaryTitle,
            txtSalary,
            txtBenefitsTitle,
            txtBenefits1,
            txtBenefits2,
            txtBenefits3,
            txtBenefits4,
            txtBenefits5,
            txtBenefits6,
            txtAboutMeTitle,
            txtAboutMe;

    private ImageView resumeProfilePic;

    private ListView
            listViewTalents,
            listViewJobExps,
            listViewEducation,
            listViewLanguages,
            listViewProvince,
            listViewJobcat,
            listViewContracts,
            listViewLevels;
    private ArrayList<String>
            listTalents,
            listProvinces,
            listJobcats,
            listContracts,
            listSeniority;
    private ListAdapterSimpleProes listAdapterTalents;
    private ListAdapterSimpleProvince listAdapterProvinces;
    private ListAdapterSimpleJobcat listAdapterJobcats;
    private ListAdapterSimpleTerms listAdapterContracts;
    private ListAdapterSimpleSkill listAdapterSeniority;

    private List<String> levels, names;

    private ArrayList<Lang> listLang;
    private ListAdapterSimpleLang listAdapterLang;

    private ArrayList<Job> listJobs;
    private ListAdapterSimpleJobs listAdapterJobs;

    private ArrayList<Education> listEducation;
    private ListAdapterSimpleEducation listAdapterEducation;

    private List<String>
            Marital_arrays,
            Gender_arrays ,
            Province,
            JobStatus_arrays;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PolicyGuard.Allow();
        setContentView(R.layout.activity_resume_view);
        session = new SessionManager(getApplicationContext());
        Intent intent = getIntent();
        Language = intent.getStringExtra("Language");
        Context context = LocaleHelper.setLocale(getApplicationContext(), Language);
        resources = context.getResources();

        findViews();
        updateView();
        try {
            setTexts();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void findViews(){
        resumeProfilePic = (ImageView) findViewById(R.id.resumeProfilePic);

        LinearLayoutResumeView                  = (LinearLayout) findViewById(R.id.LinearLayoutResumeView);
        LinearLayoutresumeJob                   = (LinearLayout) findViewById(R.id.LinearLayoutresumeJob);
        LinearLayoutPrivateInfoTitle            = (LinearLayout) findViewById(R.id.LinearLayoutPrivateInfoTitle);
        LinearLayoutPrivateInfo                 = (LinearLayout) findViewById(R.id.LinearLayoutPrivateInfo);
        LinearLayoutTalentsTitle                = (LinearLayout) findViewById(R.id.LinearLayoutTalentsTitle);
        LinearLayoutJobExp                      = (LinearLayout) findViewById(R.id.LinearLayoutJobExp);
        LinearLayoutEducation                   = (LinearLayout) findViewById(R.id.LinearLayoutEducation);
        LinearLayoutLanguages                   = (LinearLayout) findViewById(R.id.LinearLayoutLanguages);
        LinearLayoutJobFavoritesTitle           = (LinearLayout) findViewById(R.id.LinearLayoutJobFavoritesTitle);
        LinearLayouttxtSelectedProvincesTitle   = (LinearLayout) findViewById(R.id.LinearLayouttxtSelectedProvincesTitle);
        LinearLayoutJobCatTitle                 = (LinearLayout) findViewById(R.id.LinearLayoutJobCatTitle);
        LinearLayoutContractsTitle              = (LinearLayout) findViewById(R.id.LinearLayoutContractsTitle);
        LinearLayoutLevelsTitle                 = (LinearLayout) findViewById(R.id.LinearLayoutLevelsTitle);
        LinearLayoutSalaryTitle                 = (LinearLayout) findViewById(R.id.LinearLayoutSalaryTitle);
        LinearLayoutSalary                      = (LinearLayout) findViewById(R.id.LinearLayoutSalary);
        LinearLayoutBenefitsTitle               = (LinearLayout) findViewById(R.id.LinearLayoutBenefitsTitle);
        LinearLayoutBenefits                    = (LinearLayout) findViewById(R.id.LinearLayoutBenefits);
        LinearLayoutAboutMeTitle                = (LinearLayout) findViewById(R.id.LinearLayoutAboutMeTitle);

        txtJobTitle                         = (TextView) findViewById(R.id.txtJobTitle);
        txtJobTitleTitle                = (TextView) findViewById(R.id.txtJobTitleTitle);
        txtjob_status                           = (TextView) findViewById(R.id.txtjob_status);
        txtjob_statusTitle                               = (TextView) findViewById(R.id.txtjob_statusTitle);
        txtLastDegree                                = (TextView) findViewById(R.id.txtLastDegree);
        txtLastDegreeTitle                       = (TextView) findViewById(R.id.txtLastDegreeTitle);
        txtPrivateInfoTitle                                 = (TextView) findViewById(R.id.txtPrivateInfoTitle);
        txtEmailAddress                                  = (TextView) findViewById(R.id.txtEmailAddress);
        txtEmailAddressTitle                                    = (TextView) findViewById(R.id.txtEmailAddressTitle);
        txtPhone                                             = (TextView) findViewById(R.id.txtPhone);
        txtPhoneTitle                               = (TextView) findViewById(R.id.txtPhoneTitle);
        txtProvinceResume                               = (TextView) findViewById(R.id.txtProvinceResume);
        txtProvinceResumeTitle                               = (TextView) findViewById(R.id.txtProvinceResumeTitle);
        txtMarriageResume                                                = (TextView) findViewById(R.id.txtMarriageResume);
        txtMarriageResumeTitle                                          = (TextView) findViewById(R.id.txtMarriageResumeTitle);
        txtGenderResume                                         = (TextView) findViewById(R.id.txtGenderResume);
        txtGenderResumeTitle                                     = (TextView) findViewById(R.id.txtGenderResumeTitle);
        txtBirthYearResume                                       = (TextView) findViewById(R.id.txtBirthYearResume);
        txtBirthYearResumeTitle                              = (TextView) findViewById(R.id.txtBirthYearResumeTitle);
        txtDutyResume                                = (TextView) findViewById(R.id.txtDutyResume);
        txtDutyResumeTitle                                                   = (TextView) findViewById(R.id.txtDutyResumeTitle);
        txtAddressResume                                         = (TextView) findViewById(R.id.txtAddressResume);
        txtAddressResumeTitle                                    = (TextView) findViewById(R.id.txtAddressResumeTitle);
        txtTalentsTitle                                          = (TextView) findViewById(R.id.txtTalentsTitle);
        txtJobExpTitle                                           = (TextView) findViewById(R.id.txtJobExpTitle);
        txtEducationTitle                                            = (TextView) findViewById(R.id.txtEducationTitle);
        txtLanguages                                             = (TextView) findViewById(R.id.txtLanguages);
        txtJobFavoritesTitle                                             = (TextView) findViewById(R.id.txtJobFavoritesTitle);
        txtSelectedProvincesTitle                                = (TextView) findViewById(R.id.txtSelectedProvincesTitle);
        txtJobCatTitle                                      = (TextView) findViewById(R.id.txtJobCatTitle);
        txtContractsTitle                                   = (TextView) findViewById(R.id.txtContractsTitle);
        txtLevelsTitle                                               = (TextView) findViewById(R.id.txtLevelsTitle);
        txtSalaryTitle                                                   = (TextView) findViewById(R.id.txtSalaryTitle);
        txtSalary                                               = (TextView) findViewById(R.id.txtSalary);
        txtBenefitsTitle                                                 = (TextView) findViewById(R.id.txtBenefitsTitle);
        txtBenefits1                                         = (TextView) findViewById(R.id.txtBenefits1);
        txtBenefits2                                         = (TextView) findViewById(R.id.txtBenefits2);
        txtBenefits3                                         = (TextView) findViewById(R.id.txtBenefits3);
        txtBenefits4                                         = (TextView) findViewById(R.id.txtBenefits4);
        txtBenefits5                                         = (TextView) findViewById(R.id.txtBenefits5);
        txtBenefits6                                         = (TextView) findViewById(R.id.txtBenefits6);
        txtAboutMeTitle                                         = (TextView) findViewById(R.id.txtAboutMeTitle);
        txtAboutMe                                              = (TextView) findViewById(R.id.txtAboutMe);

        listViewTalents     = (ListView) findViewById(R.id.listViewTalents);
        listViewJobExps     = (ListView) findViewById(R.id.listViewJobExps);
        listViewEducation   = (ListView) findViewById(R.id.listViewEducation);
        listViewLanguages   = (ListView) findViewById(R.id.listViewLanguages);
        listViewProvince    = (ListView) findViewById(R.id.listViewProvince);
        listViewJobcat      = (ListView) findViewById(R.id.listViewJobcat);
        listViewContracts   = (ListView) findViewById(R.id.listViewContracts);
        listViewLevels      = (ListView) findViewById(R.id.listViewLevels);

        levels = Arrays.asList(resources.getStringArray(R.array.langLevels));
        names =   Arrays.asList(resources.getStringArray(R.array.langs));
        setLists_adapters();
    }
    public void setLists_adapters(){
        listTalents = new ArrayList<>();
        listAdapterTalents = new ListAdapterSimpleProes(listTalents,getApplicationContext(),ResumeViewActivity.this,listViewTalents);

        listProvinces = new ArrayList<>();
        listAdapterProvinces = new ListAdapterSimpleProvince(listProvinces,getApplicationContext(),ResumeViewActivity.this,listViewProvince);

        listJobcats = new ArrayList<>();
        listAdapterJobcats = new ListAdapterSimpleJobcat(listJobcats,getApplicationContext(),ResumeViewActivity.this,listViewJobcat);

        listContracts = new ArrayList<>();
        listAdapterContracts = new ListAdapterSimpleTerms(listContracts,getApplicationContext(),ResumeViewActivity.this,listViewContracts);

        listSeniority = new ArrayList<>();
        listAdapterSeniority = new ListAdapterSimpleSkill(listSeniority,getApplicationContext(),ResumeViewActivity.this,listViewLevels);

        listLang = new ArrayList<>();
        listAdapterLang = new ListAdapterSimpleLang(listLang,getApplicationContext(),ResumeViewActivity.this,listViewLanguages);

        listJobs = new ArrayList<>();
        listAdapterJobs = new ListAdapterSimpleJobs(listJobs,getApplicationContext(),ResumeViewActivity.this,listViewJobExps);

        listEducation = new ArrayList<>();
        listAdapterEducation = new ListAdapterSimpleEducation(listEducation,getApplicationContext(),ResumeViewActivity.this,listViewEducation);

        listViewTalents .setAdapter(listAdapterTalents);
        listViewProvince.setAdapter(listAdapterProvinces);
        listViewJobcat.setAdapter(listAdapterJobcats);
        listViewContracts.setAdapter(listAdapterContracts);
        listViewLevels.setAdapter(listAdapterSeniority);


        justifyListViewHeightBasedOnChildren(listViewTalents);
        justifyListViewHeightBasedOnChildren(listViewProvince);
        justifyListViewHeightBasedOnChildren(listViewJobcat);
        justifyListViewHeightBasedOnChildren(listViewContracts);
        justifyListViewHeightBasedOnChildren(listViewLevels);



        listViewEducation.setAdapter(listAdapterEducation);
        listViewJobExps.setAdapter(listAdapterJobs);
        listViewLanguages.setAdapter(listAdapterLang);
    }


    private void updateView(){
        if(Language.equals("fa")){
            //LINEAR
            LinearLayoutResumeView                .setGravity(Gravity.RIGHT);
            LinearLayoutresumeJob                 .setGravity(Gravity.RIGHT);
            LinearLayoutPrivateInfoTitle          .setGravity(Gravity.RIGHT);
            LinearLayoutPrivateInfo               .setGravity(Gravity.RIGHT);
            LinearLayoutTalentsTitle              .setGravity(Gravity.RIGHT);
            LinearLayoutJobExp                    .setGravity(Gravity.RIGHT);
            LinearLayoutEducation                 .setGravity(Gravity.RIGHT);
            LinearLayoutLanguages                 .setGravity(Gravity.RIGHT);
            LinearLayoutJobFavoritesTitle         .setGravity(Gravity.RIGHT);
            LinearLayouttxtSelectedProvincesTitle .setGravity(Gravity.RIGHT);
            LinearLayoutJobCatTitle               .setGravity(Gravity.RIGHT);
            LinearLayoutContractsTitle            .setGravity(Gravity.RIGHT);
            LinearLayoutLevelsTitle               .setGravity(Gravity.RIGHT);
            LinearLayoutSalaryTitle               .setGravity(Gravity.RIGHT);
            LinearLayoutSalary                    .setGravity(Gravity.RIGHT);
            LinearLayoutBenefitsTitle             .setGravity(Gravity.RIGHT);
            LinearLayoutBenefits                  .setGravity(Gravity.RIGHT);
            LinearLayoutAboutMeTitle              .setGravity(Gravity.RIGHT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                LinearLayoutResumeView               .setForegroundGravity(Gravity.RIGHT);
//                LinearLayoutresumeJob                .setForegroundGravity(Gravity.RIGHT);
//                LinearLayoutPrivateInfoTitle         .setForegroundGravity(Gravity.RIGHT);
//                LinearLayoutPrivateInfo              .setForegroundGravity(Gravity.RIGHT);
//                LinearLayoutTalentsTitle             .setForegroundGravity(Gravity.RIGHT);
//                LinearLayoutJobExp                   .setForegroundGravity(Gravity.RIGHT);
//                LinearLayoutEducation                .setForegroundGravity(Gravity.RIGHT);
//                LinearLayoutLanguages                .setForegroundGravity(Gravity.RIGHT);
//                LinearLayoutJobFavoritesTitle        .setForegroundGravity(Gravity.RIGHT);
//                LinearLayouttxtSelectedProvincesTitle.setForegroundGravity(Gravity.RIGHT);
//                LinearLayoutJobCatTitle              .setForegroundGravity(Gravity.RIGHT);
//                LinearLayoutContractsTitle           .setForegroundGravity(Gravity.RIGHT);
//                LinearLayoutLevelsTitle              .setForegroundGravity(Gravity.RIGHT);
//                LinearLayoutSalaryTitle              .setForegroundGravity(Gravity.RIGHT);
//                LinearLayoutSalary                   .setForegroundGravity(Gravity.RIGHT);
//                LinearLayoutBenefitsTitle            .setForegroundGravity(Gravity.RIGHT);
//                LinearLayoutBenefits                 .setForegroundGravity(Gravity.RIGHT);
//                LinearLayoutAboutMeTitle             .setForegroundGravity(Gravity.RIGHT);

                listViewTalents  .setForegroundGravity(Gravity.RIGHT);
                listViewJobExps  .setForegroundGravity(Gravity.RIGHT);
                listViewEducation.setForegroundGravity(Gravity.RIGHT);
                listViewLanguages.setForegroundGravity(Gravity.RIGHT);
                listViewProvince .setForegroundGravity(Gravity.RIGHT);
                listViewJobcat   .setForegroundGravity(Gravity.RIGHT);
                listViewContracts.setForegroundGravity(Gravity.RIGHT);
                listViewLevels   .setForegroundGravity(Gravity.RIGHT);

                listViewTalents  .setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                listViewJobExps  .setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                listViewEducation.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                listViewLanguages.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                listViewProvince .setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                listViewJobcat   .setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                listViewContracts.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                listViewLevels   .setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                listViewTalents  .setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
        else{
            //LINEAR
            LinearLayoutResumeView                .setGravity(Gravity.LEFT);
            LinearLayoutresumeJob                 .setGravity(Gravity.LEFT);
            LinearLayoutPrivateInfoTitle          .setGravity(Gravity.LEFT);
            LinearLayoutPrivateInfo               .setGravity(Gravity.LEFT);
            LinearLayoutTalentsTitle              .setGravity(Gravity.LEFT);
            LinearLayoutJobExp                    .setGravity(Gravity.LEFT);
            LinearLayoutEducation                 .setGravity(Gravity.LEFT);
            LinearLayoutLanguages                 .setGravity(Gravity.LEFT);
            LinearLayoutJobFavoritesTitle         .setGravity(Gravity.LEFT);
            LinearLayouttxtSelectedProvincesTitle .setGravity(Gravity.LEFT);
            LinearLayoutJobCatTitle               .setGravity(Gravity.LEFT);
            LinearLayoutContractsTitle            .setGravity(Gravity.LEFT);
            LinearLayoutLevelsTitle               .setGravity(Gravity.LEFT);
            LinearLayoutSalaryTitle               .setGravity(Gravity.LEFT);
            LinearLayoutSalary                    .setGravity(Gravity.LEFT);
            LinearLayoutBenefitsTitle             .setGravity(Gravity.LEFT);
            LinearLayoutBenefits                  .setGravity(Gravity.LEFT);
            LinearLayoutAboutMeTitle              .setGravity(Gravity.LEFT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                LinearLayoutResumeView               .setForegroundGravity(Gravity.LEFT);
                LinearLayoutresumeJob                .setForegroundGravity(Gravity.LEFT);
                LinearLayoutPrivateInfoTitle         .setForegroundGravity(Gravity.LEFT);
                LinearLayoutPrivateInfo              .setForegroundGravity(Gravity.LEFT);
                LinearLayoutTalentsTitle             .setForegroundGravity(Gravity.LEFT);
                LinearLayoutJobExp                   .setForegroundGravity(Gravity.LEFT);
                LinearLayoutEducation                .setForegroundGravity(Gravity.LEFT);
                LinearLayoutLanguages                .setForegroundGravity(Gravity.LEFT);
                LinearLayoutJobFavoritesTitle        .setForegroundGravity(Gravity.LEFT);
                LinearLayouttxtSelectedProvincesTitle.setForegroundGravity(Gravity.LEFT);
                LinearLayoutJobCatTitle              .setForegroundGravity(Gravity.LEFT);
                LinearLayoutContractsTitle           .setForegroundGravity(Gravity.LEFT);
                LinearLayoutLevelsTitle              .setForegroundGravity(Gravity.LEFT);
                LinearLayoutSalaryTitle              .setForegroundGravity(Gravity.LEFT);
                LinearLayoutSalary                   .setForegroundGravity(Gravity.LEFT);
                LinearLayoutBenefitsTitle            .setForegroundGravity(Gravity.LEFT);
                LinearLayoutBenefits                 .setForegroundGravity(Gravity.LEFT);
                LinearLayoutAboutMeTitle             .setForegroundGravity(Gravity.LEFT);

                listViewTalents  .setForegroundGravity(Gravity.LEFT);
                listViewJobExps  .setForegroundGravity(Gravity.LEFT);
                listViewEducation.setForegroundGravity(Gravity.LEFT);
                listViewLanguages.setForegroundGravity(Gravity.LEFT);
                listViewProvince .setForegroundGravity(Gravity.LEFT);
                listViewJobcat   .setForegroundGravity(Gravity.LEFT);
                listViewContracts.setForegroundGravity(Gravity.LEFT);
                listViewLevels   .setForegroundGravity(Gravity.LEFT);

                listViewTalents  .setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                listViewJobExps  .setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                listViewEducation.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                listViewLanguages.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                listViewProvince .setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                listViewJobcat   .setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                listViewContracts.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                listViewLevels   .setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                listViewTalents  .setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        }
    }
    private void setTexts() throws JSONException {


        txtJobTitleTitle          .setText(resources.getString(R.string.JobTitle2));


        txtjob_statusTitle        .setText(resources.getString(R.string.job_status));

        txtLastDegree             .setText(session.get_Resume().getCertificate());
        txtLastDegreeTitle        .setText(resources.getString(R.string.LastDegreeTitle));

        txtPrivateInfoTitle       .setText(resources.getString(R.string.PrivateInfo));

        txtEmailAddress           .setText(session.get_Resume().getEmail());
        txtEmailAddressTitle      .setText(resources.getString(R.string.EmailAddressTitle));

        txtPhone                  .setText(session.get_Resume().getMobile());
        txtPhoneTitle             .setText(resources.getString(R.string.PhoneTitle));

        txtProvinceResume         .setText(session.get_Resume().getProvince());
        txtProvinceResumeTitle    .setText(resources.getString(R.string.ProvinceResumeTitle));

        txtMarriageResume         .setText(session.get_Resume().getMarital());
        txtMarriageResumeTitle    .setText(resources.getString(R.string.MarriageResumeTitle));

        txtGenderResume           .setText(session.get_Resume().getSex());
        txtGenderResumeTitle      .setText(resources.getString(R.string.GenderResumeTitle));

        txtBirthYearResume        .setText(session.get_Resume().getYear_birth());
        txtBirthYearResumeTitle   .setText(resources.getString(R.string.BirthYearResumeTitle));

        txtDutyResume             .setText(session.get_Resume().getMilitarystatus());
        txtDutyResumeTitle        .setText(resources.getString(R.string.DutyResumeTitle));

        txtAddressResume          .setText(session.get_Resume().getAddress());
        txtAddressResumeTitle     .setText(resources.getString(R.string.AddressResumeTitle));

        txtTalentsTitle           .setText(resources.getString(R.string.Talents));
        txtJobExpTitle            .setText(resources.getString(R.string.JobExp));
        txtEducationTitle         .setText(resources.getString(R.string.GraduateExp));
        txtLanguages              .setText(resources.getString(R.string.Languages));
        txtJobFavoritesTitle      .setText(resources.getString(R.string.JobFavorites));
        txtSelectedProvincesTitle .setText(resources.getString(R.string.SelectedProvinces));
        txtJobCatTitle            .setText(resources.getString(R.string.JobCategory2));
        txtContractsTitle         .setText(resources.getString(R.string.AcceptedContract));
        txtLevelsTitle            .setText(resources.getString(R.string.ActivityLevel));

        txtSalaryTitle            .setText(resources.getString(R.string.Salary));
        if(session.get_Resume().getSalary() == null){
            txtSalary                 .setText("مشخص نشده");
        }
        else{
            if(session.get_Resume().getSalary().equals("0"))
                txtSalary                 .setText("مشخص نشده");
            else
                txtSalary                 .setText(session.get_Resume().getSalary());
        }
        txtBenefitsTitle          .setText(resources.getString(R.string.JobBenefits));

        txtAboutMeTitle           .setText(resources.getString(R.string.AboutMe2));
        txtAboutMe                .setText(session.get_Resume().getAbout_me());


        setActivities();
        setLangs();
        setJobExp();
        seteducation();
        setBenefits();
        setUserInfoes();
    }
    private void setUserInfoes() throws JSONException {
        Marital_arrays = Arrays.asList(resources.getStringArray(R.array.Marital_arrays));
        Gender_arrays = Arrays.asList(resources.getStringArray(R.array.Gender_arrays));
        Province = Arrays.asList(resources.getStringArray(R.array.Province));
        JobStatus_arrays= Arrays.asList(resources.getStringArray(R.array.job_status_arrays));

        txtJobTitle               .setText(session.get_Resume().getJob_title());
        txtjob_status             .setText(JobStatus_arrays.get(Integer.parseInt(session.get_Resume().getJob_status())-1));
        //txtProvinceResume         .setText(Province.get(Integer.parseInt(session.get_Resume().getProvince())-1));
    }
    private void setActivities() throws JSONException {

        List<String> skills = Arrays.asList(session.get_Resume().getSkills().split(","));
        for (int i=0 ; i<skills.size();i++)
        {
            listTalents.add(skills.get(i).replace("ي","ی"));
            listAdapterTalents.notifyDataSetChanged();
            justifyListViewHeightBasedOnChildren(listViewTalents);
        }

        List<String> provinces = Arrays.asList(session.get_Resume().getProvinces().split(","));
        for (int i=0 ; i<provinces.size();i++)
        {
            listProvinces.add(provinces.get(i).replace("ي","ی"));
            listAdapterProvinces.notifyDataSetChanged();
            justifyListViewHeightBasedOnChildren(listViewProvince);
        }

        List<String> contracts = Arrays.asList(session.get_Resume().getContracts().split(","));
        for (int i=0 ; i<contracts.size();i++)
        {
            listContracts.add(contracts.get(i).replace("ي","ی"));
            listAdapterContracts.notifyDataSetChanged();
            justifyListViewHeightBasedOnChildren(listViewContracts);
        }

        List<String> categories = Arrays.asList(session.get_Resume().getCategories().split(","));
        for (int i=0 ; i<categories.size();i++)
        {
            listJobcats.add(categories.get(i).replace("ي","ی"));
            listAdapterJobcats.notifyDataSetChanged();
            justifyListViewHeightBasedOnChildren(listViewJobcat);
        }

        List<String> sens = Arrays.asList(resources.getStringArray(R.array.Senority_arrays));
        List<String> seniorities = Arrays.asList(session.get_Resume().getLevels().split(","));
        for (int i=0 ; i<seniorities.size();i++)
        {
            String Fromseniorities = seniorities.get(i);
            String Fromsens = "";
            if(Integer.parseInt(Fromseniorities)>0)
                Fromsens = sens.get(Integer.parseInt(Fromseniorities)-1);
            if(Fromsens.length()>0)
            {
                listSeniority.add(Fromsens.replace("ي","ی"));
                listAdapterSeniority.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listViewLevels);
            }
        }
    }
    private void setLangs() throws JSONException {
        if(session.get_Resume().getLanguages()!=null){
            JSONArray array = new JSONArray(session.get_Resume().getLanguages().toString());
            for (int i=0 ; i<array.length();i++)
            {
                JSONObject c= array.getJSONObject(i);
                //Log.e("default", c.getString("language")+","+ c.getString("level"));
                Lang lang = new Lang((names.indexOf(c.getString("language").replace("ي","ی"))+1)+""
                        ,(levels.indexOf(c.getString("level").replace("ي","ی"))+1)+"" );
                lang.setId(c.getInt("id"));
                Boolean exist = false;
                for (Lang item:listLang
                        ) {
                    if(item.getLevel().equals(lang.getLevel()) && item.getName().equals(lang.getName())){
                        exist = true;
                        break;
                    }
                }
                if(!exist){
                    listLang.add(lang);
                }

                listAdapterLang.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listViewLanguages);
            }
        }
        else{
            JSONArray array = new JSONArray();
            for (int i=0 ; i<array.length();i++)
            {
                JSONObject c= array.getJSONObject(i);
                //Log.e("default", c.getString("language")+","+ c.getString("level"));
                Lang lang = new Lang((names.indexOf(c.getString("language").replace("ي","ی"))+1)+""
                        ,(levels.indexOf(c.getString("level").replace("ي","ی"))+1)+"" );
                lang.setId(c.getInt("id"));
                Boolean exist = false;
                for (Lang item:listLang
                        ) {
                    if(item.getLevel().equals(lang.getLevel()) && item.getName().equals(lang.getName())){
                        exist = true;
                        break;
                    }
                }
                if(!exist){
                    listLang.add(lang);
                }
                listAdapterLang.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listViewLanguages);
            }
        }
    }
    private void setJobExp() throws JSONException {
        if(session.get_Resume().getExperiences()!=null){
            JSONArray array = new JSONArray(session.get_Resume().getExperiences().toString());
            for (int i=0 ; i<array.length();i++)
            {
                JSONObject c= array.getJSONObject(i);
                Job job = new Job(c.getString("title"), c.getString("start_year"), c.getString("last"), "",
                        c.getString("company"), "", "1", "1");
                job.setId(c.getInt("id"));
                listJobs.add(job);
                listAdapterJobs.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listViewJobExps);
            }
        }
        else{
            JSONArray array = new JSONArray();
            for (int i=0 ; i<array.length();i++)
            {
                JSONObject c= array.getJSONObject(i);
                Job job = new Job(c.getString("title"), c.getString("start_year"), c.getString("last"), "",
                        c.getString("company"), "", "1", "1");
                job.setId(c.getInt("id"));
                listJobs.add(job);
                listAdapterJobs.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listViewJobExps);
            }
        }
    }
    private void seteducation() throws JSONException {
        if(session.get_Resume().getAcademics() != null){
            JSONArray array = new JSONArray(session.get_Resume().getAcademics().toString());
            for (int i=0 ; i<array.length();i++)
            {
                JSONObject c= array.getJSONObject(i);
                Education edu = new Education("", c.getString("field"), c.getString("university"), c.getString("start_year"),
                        c.getString("last"));
                edu.setId(c.getInt("id"));
                listEducation.add(edu);
                listAdapterEducation.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listViewEducation);
            }
        }
        else{
            JSONArray array = new JSONArray();
            for (int i=0 ; i<array.length();i++)
            {
                JSONObject c= array.getJSONObject(i);
                Education edu = new Education("", c.getString("field"), c.getString("university"), c.getString("start_year"),
                        c.getString("last"));
                edu.setId(c.getInt("id"));
                listEducation.add(edu);
                listAdapterEducation.notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listViewEducation);
            }
        }
    }
    private void setBenefits() throws JSONException {
        String s1 = session.get_Resume().getBenefits();
        String[] words=s1.split(",");
        int i =0;
        for (String item:words
                ) {
            if(i==0){
                txtBenefits1.setVisibility(View.VISIBLE);
                txtBenefits1.setText(item);
            }
            if(i==1){
                txtBenefits2.setVisibility(View.VISIBLE);
                txtBenefits2.setText(item);
            }
            if(i==2){
                txtBenefits3.setVisibility(View.VISIBLE);
                txtBenefits3.setText(item);
            }
            if(i==3){
                txtBenefits4.setVisibility(View.VISIBLE);
                txtBenefits4.setText(item);
            }
            if(i==4){
                txtBenefits5.setVisibility(View.VISIBLE);
                txtBenefits5.setText(item);
            }
            if(i==5) {
                txtBenefits6.setVisibility(View.VISIBLE);
                txtBenefits6.setText(item);
            }
            i++;
        }
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
