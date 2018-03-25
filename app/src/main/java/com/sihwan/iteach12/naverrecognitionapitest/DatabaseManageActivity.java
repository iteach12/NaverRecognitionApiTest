package com.sihwan.iteach12.naverrecognitionapitest;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class DatabaseManageActivity extends AppCompatActivity {

    private String userId;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_manage);


        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getEmail().toString();


        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "Problem.db", null, 1);

// 테이블에 있는 모든 데이터 출력
        final TextView result = (TextView) findViewById(R.id.result);

        final TextView create_who = (TextView) findViewById(R.id.create_who);
        create_who.setText(userId);
        final EditText etItem = (EditText) findViewById(R.id.item);
        final EditText etPrice = (EditText) findViewById(R.id.price);



// DB에 데이터 추가
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
        Button delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = etItem.getText().toString();

                dbHelper.delete(item);
                result.setText(dbHelper.getResult());
            }
        });

// DB에 있는 데이터 조회
        Button select = (Button) findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText(dbHelper.getResult());

                AlertDialog.Builder builder = new AlertDialog.Builder(DatabaseManageActivity.this);
                builder.setTitle("문제뽑은거");
                builder.setMessage(dbHelper.getResult());
                builder.setPositiveButton("확인", null);
                builder.create().show();
            }
        });
    }

}
