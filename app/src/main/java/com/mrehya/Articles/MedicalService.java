package com.mrehya.Articles;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mrehya.Helper.LocaleHelper;
import com.mrehya.MyTextView;
import com.mrehya.R;
import com.mrehya.Reserv.Reserve;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class MedicalService extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private List<Article> articles;
    private Button btnReserve;


    //new
    MyTextView mytextMainMenu,mytextArticles;
    LinearLayout LinearLayoutmedicalservice1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_service);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_article);
        btnReserve = (Button) findViewById(R.id.btnReserve);

        articles = new ArrayList<>();
        adapter = new ArticleAdapter(getApplicationContext(), articles);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareArticles();

        btnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedicalService.this,Reserve.class);
                startActivity(intent);
            }
        });

        //new
        Paper.init(this);
        mytextMainMenu=(MyTextView) findViewById(R.id.mytextMainMenu);
        mytextArticles=(MyTextView) findViewById(R.id.mytextArticles);
        LinearLayoutmedicalservice1=(LinearLayout) findViewById(R.id.LinearLayoutmedicalservice1);
        updateLanguage();
        updateView((String) Paper.book().read("language"));
    }

    private void prepareArticles() {
        int[] covers = new int[]{
                R.drawable.company1,
                R.drawable.company2,
                R.drawable.company3,
                R.drawable.company4,
                R.drawable.company5,
                R.drawable.company6,
                R.drawable.company7,
                R.drawable.company1,
                R.drawable.company2,
                R.drawable.company3,
                R.drawable.company4};

        Article a = new Article(1,covers[0],"مقاله شماره ۱","آرش محمدی");
        articles.add(a);

        a = new Article(1,covers[1],"مقاله شماره ۱","آرش محمدی");
        articles.add(a);

        a = new Article(1,covers[2],"مقاله شماره ۱","آرش محمدی");
        articles.add(a);

        a = new Article(1,covers[3],"مقاله شماره ۱","آرش محمدی");
        articles.add(a);

        a = new Article(1,covers[4],"مقاله شماره ۱","آرش محمدی");
        articles.add(a);

        a = new Article(1,covers[5],"مقاله شماره ۱","آرش محمدی");
        articles.add(a);



        adapter.notifyDataSetChanged();
    }
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

        //LANGUAGE FA
        if(language.equals("fa")){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                LinearLayoutmedicalservice1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                btnReserve.setGravity(Gravity.RIGHT|Gravity.CENTER);
            }
            else
            {
                btnReserve.setGravity(Gravity.RIGHT|Gravity.CENTER);
            }
            //makeResume.setGravity(Gravity.RIGHT|Gravity.CENTER);
        }
        //LANGUAGE ENGLISH
        else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                LinearLayoutmedicalservice1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                btnReserve.setGravity(Gravity.LEFT|Gravity.CENTER);
            }
            else
            {
                btnReserve.setGravity(Gravity.LEFT|Gravity.CENTER);
            }
            //makeResume.setGravity(Gravity.LEFT|Gravity.CENTER);
        }

        mytextMainMenu.setText(resources.getString(R.string.MainMenu));
        btnReserve.setText(resources.getString(R.string.Onlineclinicbookkeeping));
        mytextArticles.setText(resources.getString(R.string.articles));

    }
}
