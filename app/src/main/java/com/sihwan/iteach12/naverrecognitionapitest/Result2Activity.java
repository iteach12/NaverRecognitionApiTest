package com.sihwan.iteach12.naverrecognitionapitest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Result2Activity extends AppCompatActivity {



    private FirebaseAuth auth;
    private DatabaseReference database;



    //아이디 저장용
    String userID;
    String sfName = "UserID";
    int userLevel;
    MyStatusDTO my = new MyStatusDTO();

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



        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        SharedPreferences sf = getSharedPreferences(sfName, 0);

        userID = sf.getString("userID", "");



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



        //점수 넘어온거
        Intent intent = getIntent();
        final int userPoint = intent.getExtras().getInt("userPoint");
        //점수 넘어온거

        //점수 데이터베이스에서 불러온거


        database.child("userProfile").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                my = dataSnapshot.getValue(MyStatusDTO.class);
                userLevel = my.userLevel;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



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


                                //push가 붙으면 계속 추가하기임 같은 데이터라 할지라도 또 추가됨.
                                //push를 빼면 덮어쓰기임 이전 데이터를 지우고 지금 데이터를 입력함.
                                database.child("userProfile").child(userID).child("userLevel").setValue(userLevel+userPoint);

                                Intent myIntent = new Intent (Result2Activity.this, PracticeActivity.class);
                                startActivity(myIntent);


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
