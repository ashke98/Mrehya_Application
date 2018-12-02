package com.mrehya;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.mrehya.Dashboards.DashboardFragment;
import com.mrehya.Dashboards.DashboardFragment2;
import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.Hire.HireFragment;
import com.mrehya.Nofification.AlarmReceiver;
import com.mrehya.UserAccount.Login;
import com.mrehya.UserAccount.ProfileFragment;

import io.paperdb.Paper;

import static android.view.View.LAYOUT_DIRECTION_LTR;

public class MainActivity extends AppCompatActivity {
    private  Context context;
    private String Language="fa";

    //new
    TextView mytext3;

    BottomNavigationViewEx bnve;

    //new
    MenuItem navigation_hire;
    MenuItem navigation_nuro;
    MenuItem navigation_dashboard;
    MenuItem navigation_profile;
    MenuItem navigation_more;

    //new
    int openingFragment=0;

    private SessionManager session;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "fa"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PolicyGuard.Allow();
        context = getApplicationContext();

        session = new SessionManager(getApplicationContext());
        Intent intent = getIntent();
        Language = intent.getStringExtra("Language");

        //alarmScheduler();

        //new
        bnve = (BottomNavigationViewEx) findViewById(R.id.bnve);
        Typeface tf = Typeface.createFromAsset(getAssets(), "IRANSans.ttf");
        bnve.setTypeface(tf);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        bnve.enableShiftingMode(false);
        bnve.enableItemShiftingMode(false);
        FrameLayout bnveFrame = (FrameLayout) findViewById(R.id.bnveFrame);
        ImageView dashIcon = (ImageView) findViewById(R.id.dashIcon);
        setBnveSize(bnve,bnveFrame,dashIcon);
        bnve.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fab.bringToFront();

        //DEFAULT FRAGMENT TO OPEN
        if(getIntent().hasExtra("frgToLoad")){
            if(openingFragment==0){
                bnve.setCurrentItem(4);
                Fragment fragment = new MoreFragment();
                loadFragment(fragment);
            }
        }
        else{
            Fragment fragment = new DashboardFragment();
            loadFragment(fragment);
            bnve.setCurrentItem(2);
        }


        //new
        //translating...
        mytext3 = (MyTextView) findViewById(R.id.mytext3);
        navigation_hire = bnve.getMenu().findItem(R.id.navigation_hire);
        navigation_nuro = bnve.getMenu().findItem(R.id.navigation_nuro);
        navigation_dashboard = bnve.getMenu().findItem(R.id.navigation_dashboard);
        navigation_profile = bnve.getMenu().findItem(R.id.navigation_profile);
        navigation_more = bnve.getMenu().findItem(R.id.navigation_more);
        //init paper
        updatelanguage(context);
        updateView((String) Paper.book().read("language"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            bnve.setLayoutDirection(LAYOUT_DIRECTION_LTR);
        }
    }

    private void updatelanguage(Context context){
        Paper.init(context);
        //Default language is fa
        String language = Paper.book().read("language");
        if(language==null)
            Paper.book().write("language", "fa");
    }

    private void updateView(String language) {
        Context context = LocaleHelper.setLocale(this, language);
        Resources resources = context.getResources();


        if(language.equals("fa")){
            mytext3.setText("پیشخوان");
            navigation_hire.setTitle("استخدام");
            navigation_nuro.setTitle("نورو مارکتینگ");
            navigation_dashboard.setTitle("");
            navigation_profile.setTitle("پروفایل");
            navigation_more.setTitle("بیشتر");
        }
        else{
            mytext3.setText("Dashboard");
            mytext3.setTextSize(11);
            navigation_hire.setTitle("Hire");
            navigation_nuro.setTitle("Neuro Marketing");
            navigation_dashboard.setTitle("");
            navigation_profile.setTitle("Profile");
            navigation_more.setTitle("More");
        }
    }

    private void alarmScheduler(){
        //Alarm scheduler
        this.context = getApplicationContext();
        Intent alarm = new Intent(this.context, AlarmReceiver.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(this.context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
        if(alarmRunning == false) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            //in ALARM REPEATING : (WAKEUP TIME, STARTTIME, INTERVAL, PENDING INTENT)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 18400 * 1000, 18400 * 1000, pendingIntent);
        }
    }
    private void setBnveSize(BottomNavigationViewEx bnve,FrameLayout bnveFrame,ImageView dashIcon){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels;
        float dpWidth = displayMetrics.widthPixels ;
        double mBnveHeight = dpHeight *0.10 ;
        double mBnveIconHeight = mBnveHeight/4.3;

        bnve.setIconSize((int) mBnveIconHeight, (int) mBnveIconHeight);
        bnve.setItemHeight((int) mBnveHeight);

        final RelativeLayout.LayoutParams layoutparams = (RelativeLayout.LayoutParams) bnveFrame.getLayoutParams();
        double bnveFrameSize = mBnveHeight *1.3 ;
        layoutparams.width = (int) bnveFrameSize;
        layoutparams.height = (int) bnveFrameSize;
        bnveFrame.setLayoutParams(layoutparams);

        final RelativeLayout.LayoutParams layoutparams2 = (RelativeLayout.LayoutParams) dashIcon.getLayoutParams();
        double dashIconSize = bnveFrameSize *0.5 ;
        layoutparams2.width = (int) dashIconSize;
        layoutparams2.height = (int) dashIconSize;
        dashIcon.setLayoutParams(layoutparams2);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_hire:
                    fragment = new HireFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_nuro:
                    fragment = new NuroFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_dashboard:
                    fragment = new DashboardFragment2();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_profile:
                    if(session.isLoggedIn()){
                        fragment = new ProfileFragment();
                        loadFragment(fragment);
                        return true;
                    }
                    else{
                        if(Language!=null){
                            if(Language.equals("fa"))
                                Toast.makeText(context, "شما باید لاگین کنید", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(context, "You must login", Toast.LENGTH_SHORT).show();
                        }

                        Intent intent = new Intent(MainActivity.this, Login.class);
                        startActivity(intent);
                    }
                case R.id.navigation_more:
                    fragment = new MoreFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void open_thatFragment(){
            Fragment fragment;
            switch (openingFragment) {
                case 0:
                    fragment = new HireFragment();
                    loadFragment(fragment);
                case 1:
                    fragment = new NuroFragment();
                    loadFragment(fragment);
                case 2:
                    fragment = new DashboardFragment2();
                    loadFragment(fragment);
                case 3:
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                case 4:
                    fragment = new MoreFragment();
                    loadFragment(fragment);
        }
    }
}

