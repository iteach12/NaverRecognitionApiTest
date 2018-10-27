package com.sihwan.iteach12.naverrecognitionapitest;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Date;
import java.text.SimpleDateFormat;


/**
 * Refactoring by iteach on 2018-10-27 1711
 */

public class DatabaseManageActivity extends AppCompatActivity {



    //유저 아이디
    private String userId;


    //파이어베이스 인증
    private FirebaseAuth auth;
    //파이어베이스 데이터베이스 레퍼런스
    private DatabaseReference database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_manage);


        //파이어베이스 init
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        //유저 아이디는 이메일을 구해서 스트링으로.
        userId = auth.getCurrentUser().getEmail().toString();


        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "Problem.db", null, 1);

        // 테이블에 있는 모든 데이터 출력
        final TextView result = (TextView) findViewById(R.id.result);

        final TextView create_who = (TextView) findViewById(R.id.create_who);
        create_who.setText(userId);
        final EditText etItem = (EditText) findViewById(R.id.item);
        final EditText etPrice = (EditText) findViewById(R.id.price);
        final EditText etPoint = (EditText)findViewById(R.id.point);
        final EditText etChild = (EditText)findViewById(R.id.child1);



        // DB에 데이터 추가
        // 미사용
        Button insert = (Button) findViewById(R.id.insert);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String create_who = userId;
                String item = etItem.getText().toString();
                int price = Integer.parseInt(etPrice.getText().toString());

                dbHelper.insert(create_who, item, price);
                result.setText(dbHelper.getResult());
            }
        });

        // DB에 있는 데이터 수정
        // 미사용
        Button update = (Button) findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = etItem.getText().toString();
                int price = Integer.parseInt(etPrice.getText().toString());

                dbHelper.update(item, price);
                result.setText(dbHelper.getResult());
            }
        });


        // DB에 있는 데이터 삭제
        // 미사용
        Button delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = etItem.getText().toString();

                dbHelper.delete(item);
                result.setText(dbHelper.getResult());
            }
        });


        // DB에 있는 데이터 조회 (원래는)
        // 파이어 베이스 문제 입력하기 (지금은)
        Button select = (Button) findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 기존코드
                result.setText(dbHelper.getResult());

                AlertDialog.Builder builder = new AlertDialog.Builder(DatabaseManageActivity.this);
                builder.setTitle("문제뽑은거");
                builder.setMessage(dbHelper.getResult());
                builder.setPositiveButton("확인", null);
                builder.create().show();
                */

                //
                MyProblemDTO myProblemDTO = new MyProblemDTO();
                myProblemDTO.problemText = etItem.getText().toString();
                myProblemDTO.problemLevel = Integer.parseInt(etPrice.getText().toString());
                myProblemDTO.problemPoint = Integer.parseInt(etPoint.getText().toString());
                myProblemDTO.problemSolve = false;
                myProblemDTO.problemCorrectAnswer = false;

                //push는 기존의 것을 그대로 둔 채 끼워 넣는 것???
                database.child("problem").child(etChild.getText().toString()).push().setValue(myProblemDTO);

            }
        });
    }

}
