package com.mrehya;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mrehya.Helper.PolicyGuard;
import com.mrehya.UserAccount.LoginOrSignup;
import com.zcw.togglebutton.ToggleButton;

public class Settings extends AppCompatActivity {
    ToggleButton toggleBtn;
    Button btnLogOut;
    MyTextView txtNotifTitle;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        PolicyGuard.Allow();
        toggleBtn = (ToggleButton) findViewById(R.id.notifToggle);
        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        txtNotifTitle =  (MyTextView)findViewById(R.id.txtNotifTitle);

        sessionManager = new SessionManager(getApplicationContext());
        if (sessionManager.isNotifOn()){
            toggleBtn.setToggleOn();
        }else{
            toggleBtn.setToggleOff();
        }

        toggleBtn.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                sessionManager.setNotifStatus(on, getApplicationContext());
            }
        });
        txtNotifTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBtn.toggle();
            }
        });

        if(sessionManager.isLoggedIn()){
            btnLogOut.setVisibility(View.VISIBLE);
        }
        else{
            btnLogOut.setVisibility(View.GONE);
        }

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager sessionManager = new SessionManager(getApplicationContext());
                sessionManager.setLogin(false);
                Intent intent = new Intent(Settings.this , LoginOrSignup.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
