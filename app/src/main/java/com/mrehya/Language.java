package com.mrehya;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.style.FoldingCube;
import com.google.android.gms.security.ProviderInstaller;
import com.mrehya.DashboardPackage.DashboardLists;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.Helper.TLSLowApi;

import java.util.Locale;


import co.ronash.pushe.Pushe;
import io.paperdb.Paper;

public class Language extends AppCompatActivity {
    private Button btnPersian, btnEnglish, btn_tryagain;
    private ImageButton imgBtnPersian, imgBtnEnglish;
    private Context context;
    private LinearLayout DisConnected, Connected, Wellcome;
    private ProgressBar progressbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PolicyGuard.Allow();
        setContentView(R.layout.activity_language);
        TLSLowApi tls = new TLSLowApi(this);
        progressbar = (ProgressBar)findViewById(R.id.spin_kit);
        FoldingCube fc = new FoldingCube();
        progressbar.setIndeterminateDrawable(fc);

        DisConnected = (LinearLayout) findViewById(R.id.DisConnected);
        Connected = (LinearLayout) findViewById(R.id.Connected);
        Wellcome = (LinearLayout) findViewById(R.id.Wellcome);

        btnPersian = (Button) findViewById(R.id.btnPersian);
        imgBtnPersian = (ImageButton) findViewById(R.id.imgBtnPersian);
        btn_tryagain = (Button) findViewById(R.id.btn_tryagain);

        if(!Hasnet()){
            Connected.setVisibility(View.GONE);
            DisConnected.setVisibility(View.VISIBLE);
        }
        else{
            Connected.setVisibility(View.VISIBLE);
            DisConnected.setVisibility(View.GONE);

        DashboardLists dl = new DashboardLists(this, true, progressbar, Connected, Wellcome);
        //INITIATE PUSHE NOTIFICATION
            try {
                Pushe.initialize(this,true);
            }
            catch (Exception e){

            }



        //new
        Paper.init(this);

        btnPersian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().write("language", "fa");
                Intent intent = new Intent(Language.this,MainActivity.class);
                intent.putExtra("Language", "fa");
                startActivity(intent);
                finish();
            }
        });

        btnEnglish = (Button) findViewById(R.id.btnEnglish);
        imgBtnEnglish = (ImageButton) findViewById(R.id.imgBtnEnglish);
        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //select lang
                Paper.book().write("language", "en");

                Intent intent = new Intent(Language.this,MainActivity.class);
                intent.putExtra("Language", "en");
                startActivity(intent);
                finish();
            }
        });

        //new
        //set default direction
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            updateResources(this, "en");
        else
            updateResourcesLegacy(this, "en");
        //checkView();
        }
        btn_tryagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());
            }
        });

    }


    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String lang) {
        Locale locale = new Locale(lang);
        locale.setDefault(locale);

        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        return context.createConfigurationContext(config);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String lang) {
        Locale locale = new Locale(lang);
        locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            configuration.setLayoutDirection(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }



    private boolean Hasnet(){
        ConnectivityManager conmgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = conmgr.getActiveNetworkInfo();

        if(netinfo == null){
            return false;
        }
        else{
            return true;
        }
    }



}
