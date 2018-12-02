package com.mrehya;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Helper.PolicyGuard;

import io.paperdb.Paper;

public class NuroFragment extends Fragment {

    MyTextView mytextWhatIsNuro,mytextMainMenu,mytextNuroExplain1,mytextNuroExplain2,mytextNuroExplain3,mytextNuroExplain4;
    LinearLayout LinearLayoutnuro1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PolicyGuard.Allow();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nuro, container, false);

        mytextWhatIsNuro   = view.findViewById(R.id.mytextWhatIsNuro);
        mytextMainMenu     = view.findViewById(R.id.mytextMainMenu);
        mytextMainMenu     = view.findViewById(R.id.mytextMainMenu);
        mytextNuroExplain1 = view.findViewById(R.id.mytextNuroExplain1);
        mytextNuroExplain2 = view.findViewById(R.id.mytextNuroExplain2);
        mytextNuroExplain3 = view.findViewById(R.id.mytextNuroExplain3);
        mytextNuroExplain4 = view.findViewById(R.id.mytextNuroExplain4);
        //layouts
        LinearLayoutnuro1 =  view.findViewById(R.id.LinearLayoutnuro1);


        //new
        updateLanguage();
        updateView((String) Paper.book().read("language"));

        // Inflate the layout for this fragment
        return view;
    }


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

        //Linear Layouts
        if(language.equals("fa")){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                LinearLayoutnuro1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LinearLayoutnuro1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            //makeResume.setGravity(Gravity.RIGHT|Gravity.CENTER);
        }
        else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                LinearLayoutnuro1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LinearLayoutnuro1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            //makeResume.setGravity(Gravity.LEFT|Gravity.CENTER);
        }

        mytextWhatIsNuro.setText(resources.getString(R.string.WhatIsNuro));
        mytextMainMenu.setText(resources.getString(R.string.MainMenu));
        mytextNuroExplain1.setText(resources.getString(R.string.NuroExplain1));
        mytextNuroExplain2.setText(resources.getString(R.string.NuroExplain2));
        mytextNuroExplain3.setText(resources.getString(R.string.NuroExplain3));
        mytextNuroExplain4.setText(resources.getString(R.string.NuroExplain4));
    }

}
