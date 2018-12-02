package com.mrehya;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mrehya.Helper.PolicyGuard;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        PolicyGuard.Allow();


    }
}
