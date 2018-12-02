package com.mrehya;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mrehya.Exams.ChooseExam;
import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.Reserv.Reserve;
import com.mrehya.Resume.ResumeMainActivity;
import com.mrehya.Shopping.Shop;

import io.paperdb.Paper;


public class MoreFragment extends Fragment {

    Button ResumeShow,btnMedicalServices,btnGoToExams,btnShop,btnExamResults,btnSettings,btnAboutUs;
    String Language="fa";
    LinearLayout LinearLayoutmore2,LinearLayoutmore3,LinearLayoutmore4
            ,LinearLayoutmore5,LinearLayoutmore6,LinearLayoutmore7,LinearLayoutmoreresume;
    private SessionManager session;
    MyTextView mytextMainMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PolicyGuard.Allow();
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        session = new SessionManager(view.getContext());
        ResumeShow = view.findViewById(R.id.Resumeshow);
        btnMedicalServices = view.findViewById(R.id.btnMedicalServices);
        btnShop = view.findViewById(R.id.btnShop);
        btnGoToExams = view.findViewById(R.id.btnGoToExams);
        mytextMainMenu = view.findViewById(R.id.mytextMainMenu);
        btnExamResults = view.findViewById(R.id.btnExamResults);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnAboutUs = view.findViewById(R.id.btnAboutUs);
        //layouts
        LinearLayoutmore2 = (LinearLayout) view.findViewById(R.id.LinearLayoutmore2);
        LinearLayoutmore3 = (LinearLayout) view.findViewById(R.id.LinearLayoutmore3);
        LinearLayoutmore4 = (LinearLayout) view.findViewById(R.id.LinearLayoutmore4);
        LinearLayoutmore5 = (LinearLayout) view.findViewById(R.id.LinearLayoutmore5);
        LinearLayoutmore6 = (LinearLayout) view.findViewById(R.id.LinearLayoutmore6);
        LinearLayoutmore7 = (LinearLayout) view.findViewById(R.id.LinearLayoutmore7);
        LinearLayoutmoreresume = (LinearLayout) view.findViewById(R.id.LinearLayoutmoreresume);
        //new
        updateLanguage();
        updateView((String) Paper.book().read("language"));

        ResumeShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ResumeMainActivity.class);
                startActivity(intent);
            }
        });

        btnMedicalServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Reserve.class);
                startActivity(intent);
            }
        });

        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Shop.class);
                startActivity(intent);
            }
        });

        btnGoToExams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ChooseExam.class);
                startActivity(intent);
            }
        });
        btnAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AboutUs.class);
                startActivity(intent);
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Settings.class);
                startActivity(intent);
            }
        });

        //LinearLayoutmoreresume
        if(session.isLoggedIn() && Language.equals("fa")){
            LinearLayoutmoreresume.setVisibility(View.VISIBLE);
            LinearLayoutmore5.setVisibility(View.VISIBLE);
        }else{
            LinearLayoutmoreresume.setVisibility(View.GONE);
            LinearLayoutmore5.setVisibility(View.GONE);
        }
        return view;
    }

    private String updateLanguage(){
        //Default language is fa
        Language = Paper.book().read("language");
        if(Language==null)
            Paper.book().write("language", "fa");
        return Language;
    }

    private void updateView(String language) {
        Context context = LocaleHelper.setLocale(getActivity(), language);
        Resources resources = context.getResources();

        //Linear Layouts
        if(language.equals("fa")){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                LinearLayoutmore2.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutmore3.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutmore4.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutmore5.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutmore6.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutmore7.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutmoreresume.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LinearLayoutmore2.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutmore3.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutmore4.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutmore5.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutmore6.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutmore7.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutmoreresume.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            ResumeShow.setGravity(Gravity.RIGHT|Gravity.CENTER);
            btnGoToExams.setGravity(Gravity.RIGHT|Gravity.CENTER);
            btnExamResults.setGravity(Gravity.RIGHT|Gravity.CENTER);
            btnShop.setGravity(Gravity.RIGHT|Gravity.CENTER);
            btnMedicalServices.setGravity(Gravity.RIGHT|Gravity.CENTER);
            btnSettings.setGravity(Gravity.RIGHT|Gravity.CENTER);
            btnAboutUs.setGravity(Gravity.RIGHT|Gravity.CENTER);
        }
        else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                LinearLayoutmore2.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutmore3.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutmore4.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutmore5.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutmore6.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutmore7.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutmoreresume.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LinearLayoutmore2.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutmore3.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutmore4.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutmore5.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutmore6.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutmore7.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutmoreresume.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            ResumeShow.setGravity(Gravity.LEFT|Gravity.CENTER);
            btnGoToExams.setGravity(Gravity.LEFT|Gravity.CENTER);
            btnExamResults.setGravity(Gravity.LEFT|Gravity.CENTER);
            btnShop.setGravity(Gravity.LEFT|Gravity.CENTER);
            btnMedicalServices.setGravity(Gravity.LEFT|Gravity.CENTER);
            btnSettings.setGravity(Gravity.LEFT|Gravity.CENTER);
            btnAboutUs.setGravity(Gravity.LEFT|Gravity.CENTER);
        }
        ResumeShow.setText(resources.getString(R.string.MakeResume));
        btnGoToExams.setText(resources.getString(R.string.GoToExams));
        btnMedicalServices.setText(resources.getString(R.string.MedicalServices));
        btnShop.setText(resources.getString(R.string.Shop));
        btnExamResults.setText(resources.getString(R.string.ExamResults));
        btnSettings.setText(resources.getString(R.string.Settings));
        btnAboutUs.setText(resources.getString(R.string.AboutUs));
        mytextMainMenu.setText(resources.getString(R.string.MainMenu));
    }


}
