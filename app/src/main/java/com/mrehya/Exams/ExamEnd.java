package com.mrehya.Exams;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mrehya.Helper.PolicyGuard;
import com.mrehya.R;

public class ExamEnd extends AppCompatActivity {
    Button btnEndExam ;
    TextView txtPoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PolicyGuard.Allow();
        setContentView(R.layout.activity_exam_end);
        btnEndExam = (Button) findViewById(R.id.btnEndExam);
        txtPoint   = (TextView) findViewById(R.id.txtPoint);
        btnEndExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExamEnd.this,ChooseExam.class);
                startActivity(intent);
                finish();
            }
        });
        Intent intent = getIntent();
        int pts = intent.getIntExtra("point",0);
        txtPoint.setText("امتیاز شما = "+ pts);
    }
}
