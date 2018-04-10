package com.sihwan.iteach12.naverrecognitionapitest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class Result2Activity extends AppCompatActivity {


    TextView textview1;
    TextView pointTextview;
    TextView textview2;

    Button resultBtn;

    ArrayList<String> present = new ArrayList<>();
    String a;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textview1 = (TextView)findViewById(R.id.firstTextView);
        textview2 = (TextView)findViewById(R.id.secondTextView);
        pointTextview = (TextView)findViewById(R.id.pointTextView);

        present.add("최신형컴퓨터");
        present.add("예쁜어항");
        present.add("최신형스마트폰");
        present.add("예쁜시계");
        present.add("건담");
        present.add("프라모델");
        present.add("큰 물총");
        present.add("신발");
        present.add("과자");
        present.add("당근");
        present.add("배");

        Collections.shuffle(present);
        a = present.get(0);




        Intent intent = getIntent();
        int userPoint = intent.getExtras().getInt("userPoint");
        pointTextview.setText(String.valueOf(userPoint)+"점");
        resultBtn = (Button)findViewById(R.id.button2);
        resultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Result2Activity.this);
                builder.setTitle("보상").
                        setMessage("당신의 선물은 "+a+" 입니다").

                        setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {



                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });







        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });





    }

}
