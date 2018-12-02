package com.mrehya.UserAccount;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mrehya.Helper.LocaleHelper;
import com.mrehya.MainActivity;
import com.mrehya.R;
import com.mrehya.SessionManager;

import io.paperdb.Paper;

public class LoginOrSignup extends AppCompatActivity {
    private Button btnGoToLogin , btnGoToSignup, enterAsGuest;
    private SessionManager session;
    private LinearLayout LinearLayoutloginorsignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_signup);
        btnGoToLogin = (Button) findViewById(R.id.btnGoToLogin);
        enterAsGuest = (Button) findViewById(R.id.enterAsGuest);
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginOrSignup.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOrSignup.this , Login.class);
                startActivity(intent);
                finish();
            }
        });
        btnGoToSignup = (Button) findViewById(R.id.btnGoToSignup);
        btnGoToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOrSignup.this , Signup.class);
                startActivity(intent);
                finish();
            }
        });
        enterAsGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOrSignup.this , MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        //new
        LinearLayoutloginorsignup= (LinearLayout) findViewById(R.id.LinearLayoutloginorsignup);
        Paper.init(this);
        updateView(updateLanguage());
    }

    //Methods
    private String updateLanguage(){
        //Default language is fa
        String language = Paper.book().read("language");
        if(language==null)
            Paper.book().write("language", "fa");
        return language;
    }
    private void updateView(String language) {
        Context context = LocaleHelper.setLocale(this, language);
        Resources resources = context.getResources();

        if(language.equals("fa")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                LinearLayoutloginorsignup.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                LinearLayoutloginorsignup.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        }
        btnGoToLogin.setText(resources.getString(R.string.Login));
        btnGoToSignup.setText(resources.getString(R.string.Signup));
        enterAsGuest.setText(resources.getString(R.string.enterAsGuest));
    }
}