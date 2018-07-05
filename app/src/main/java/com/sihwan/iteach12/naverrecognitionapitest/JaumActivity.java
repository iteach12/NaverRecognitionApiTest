package com.sihwan.iteach12.naverrecognitionapitest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class JaumActivity extends AppCompatActivity implements ValueEventListener {

    //리사이클러 뷰 + 매니저
    final int ITEM_SIZE = 7;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    //파이어베이스
    private FirebaseAuth auth;
    private DatabaseReference database;

    //아이디 저장용 SharedPreferrence
    String userID;
    String sfName = "UserID";
    int userLevel;
    MyStatusDTO myStatusDTO1;

    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jaum);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        SharedPreferences sf = getSharedPreferences(sfName, 0);
        userID = sf.getString("userID", "");


        //파이어베이스 사용자 관련해서 사용하기 위해 만들어주는것.
        auth = FirebaseAuth.getInstance();

        //파이어베이스 데이터베이스
        database = FirebaseDatabase.getInstance().getReference();

        database.child("userProfile").child(userID).addValueEventListener(this);



        //점수 넘어온거
        //사용자 정보 업로드 하기 이름 레벨 개인 정보 관련



        recyclerView = findViewById(R.id.recycleView_jaum);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);



        List<Item> items = new ArrayList<>();
        Item[] item = new Item[ITEM_SIZE];
        item[0] = new Item("ㄱ, ㄲ, ㅋ", "자음연습 1단계                                    ", "jaum", 1);
        item[1] = new Item("ㄴ, ㄷ, ㄸ, ㅌ", "자음연습 2단계                                    ", "jaum", 2);
        item[2] = new Item("ㅁ, ㅂ, ㅃ, ㅍ", "자음연습 3단계                                    ", "jaum", 3);
        item[3] = new Item("ㅅ, ㅈ, ㅊ, ㅉ, ㅆ", "자음연습 4단계                                    ", "jaum", 4);
        item[4] = new Item("ㅇ, ㅎ, ㄹ", "자음연습 5단계                                    ", "jaum", 5);
        item[5] = new Item("발음연습", "연습문제를 풀어서 실력을 늘려봅시다.                                    ", "jaum", 6);
        item[6] = new Item("달인의 도전!", "도전 문제를 맞춰서 달인 칭호를 획득하세요.", "jaum", 100);




        for (int i = 0; i < ITEM_SIZE; i++) {
            items.add(item[i]);
        }

        recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(), items, R.layout.activity_jaum));
        recyclerView.addItemDecoration(new RecyclerViewDecoration(40));




        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        myStatusDTO1 = dataSnapshot.getValue(MyStatusDTO.class);
        userLevel = myStatusDTO1.userLevel;

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }


    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
