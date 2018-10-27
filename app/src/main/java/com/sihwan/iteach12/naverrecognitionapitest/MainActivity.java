package com.sihwan.iteach12.naverrecognitionapitest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ValueEventListener{


    //perrmission
    private static final int RECORD_REQUEST_CODE = 101;
    private static String TAG = "Permission_Record";


    //메인 메뉴 아이템 개수
    final int MAIN_ITEM_SIZE = 4;

    //기본 생성
    Toolbar toolbar;
    FloatingActionButton fab;
    DrawerLayout drawer;
    NavigationView navigationView;

    //리사이클러 뷰 + 매니저
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    //드로어블 관련 뷰
    TextView header_userID;
    TextView header_userLevel;

    //파이어베이스
    private FirebaseAuth auth;
    private DatabaseReference database;

    //아이디 저장용 SharedPreferrence
    String userID;
    String sfName = "UserID";

    int userLevel;

    //내 정보 저장용. 얘를 옮겨다닐거야. 액티비티 별로
    MyStatusDTO myStatusDTO1;


    //다이얼로그
    AlertDialog.Builder builder;
    AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied");
            makeRequest();
        }


        //기본 초기화 과정
        initView();

        //파이어베이스 사용자 관련해서 사용하기 위해 만들어주는것.
        auth = FirebaseAuth.getInstance();

        //파이어베이스 데이터베이스
        database = FirebaseDatabase.getInstance().getReference();

        //저장된 아이디 불러오기. 없으면 기본값은 공란
        SharedPreferences sf = getSharedPreferences(sfName, 0);

        userID = sf.getString("userID", "");

        //사용자 정보 업로드 하기 이름 레벨 개인 정보 관련
        myStatusDTO1 = new MyStatusDTO();

        database.child("userProfile").child(userID).addValueEventListener(this);


    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//
//
//            }
//        });


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle;
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        header_userID = (TextView)headerView.findViewById(R.id.header_userLevel);
        header_userLevel = (TextView)headerView.findViewById(R.id.header_userID);



        navigationView.setNavigationItemSelectedListener(this);


        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

//
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(layoutManager);
//
//
//
//        List<Item> items = new ArrayList<>();
//        Item[] item = new Item[MAIN_ITEM_SIZE];
//        item[0] = new Item("오늘의 문제", "쉽고 즐겁게 발음을 연습해 보세요", "moum", 1);
//        item[1] = new Item("기본 모음 연습", "나의 순위를 확인하세요.", "moum", 2);
//        item[2] = new Item("기본 자음 연습", "나의 학습 상황을 점검해 보세요.", "moum", 3);
//        item[3] = new Item("내 정보", "친구들과 함께 오락을 즐기면서 연습해 보세요.",  "moum", 4);
//
//        for (int i = 0; i < MAIN_ITEM_SIZE; i++) {
//            items.add(item[i]);
//        }
//
//        recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(), items, R.layout.activity_main));
//        recyclerView.addItemDecoration(new RecyclerViewDecoration(40));





    }

    //런타임 퍼미션 요청결과
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch(requestCode){
            case RECORD_REQUEST_CODE:{
                if(grantResults.length==0 || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Log.i(TAG, "Permission has been denied by user");

                }else{

                    Log.i(TAG, "Permission has been granted by user");

                }
            }
        }
    }

    //런타임 퍼미션 요청하기
    protected void makeRequest(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                RECORD_REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify presentChoice parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("개인정보처리방침").
                    setMessage("개인정보처리방침을 확인하시려면 아래의 접속버튼을 눌러 주시기 바랍니다").

                    setPositiveButton("접속", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            String url = "https://goo.gl/W2fhkF";
                            Intent internetIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(internetIntent);



                        }
                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            AlertDialog dialog = builder.create();

            dialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.today) {

            //오늘의 문제

        }else if(id == R.id.prac_moum){

            Intent myIntent = new Intent(MainActivity.this, PracticeActivity.class);
            startActivity(myIntent);


        } else if (id == R.id.prac_jaum) {

            Intent myIntent = new Intent(MainActivity.this, JaumActivity.class);
            startActivity(myIntent);

        } else if (id == R.id.easy_word) {

            Intent myIntent = new Intent(MainActivity.this, EasyWordActivity.class);
            startActivity(myIntent);

        } else if (id == R.id.middle_word) {



        } else if (id == R.id.no_mean_word) {



        } else if (id == R.id.sokdam) {



        } else if (id == R.id.prac_myProblem) {



        } else if (id == R.id.game) {



        } else if (id == R.id.problem_input) {
            Intent myIntent = new Intent(MainActivity.this, DatabaseManageActivity.class);
            startActivity(myIntent);

        } else if (id == R.id.nav_share) {

//            auth.signOut();
//            LoginManager.getInstance().logOut();
//            finish();
//            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(myIntent);
            //굳이 로그아웃이 필요할까?????

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        myStatusDTO1 = dataSnapshot.getValue(MyStatusDTO.class);
        if(myStatusDTO1 != null){


//            builder = new AlertDialog.Builder(MainActivity.this);
//            builder.setTitle("불러온 내용").
//                    setMessage(myStatusDTO1.userLevel+"\n"+myStatusDTO1.userName+"\n").
//
//
//
//                    setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//
//
//                        }
//                    });
//
//            dialog = builder.create();
//            dialog.show();


            header_userID.setText("이름 : "+myStatusDTO1.userName);
            header_userLevel.setText("점수 : "+myStatusDTO1.userLevel);




        }else{
            Toast.makeText(getApplicationContext(), "사용자 데이터 초기화 성공.", Toast.LENGTH_SHORT).show();

            Map<String, Integer> mMap = new HashMap();
            mMap.put("heal", 30);
            mMap.put("attack", 200);
            final MyStatusDTO myStatusDTO = new MyStatusDTO();
            myStatusDTO.userLevel = 20;
            myStatusDTO.userName = userID;
            myStatusDTO.userItem.add(mMap);


            //push가 붙으면 계속 추가하기임 같은 데이터라 할지라도 또 추가됨.
            //push를 빼면 덮어쓰기임 이전 데이터를 지우고 지금 데이터를 입력함.
            //내 프로필은 메인화면 시작하면서 새로 덮어쓰기??? nono 최초에 한 번만 하는거임
            database.child("userProfile").child(userID).setValue(myStatusDTO);




        }



    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    protected void onPause() {
        super.onPause();

//        dialog.dismiss();
//        SharedPreferences sf = getSharedPreferences(sfName, 0);
//        SharedPreferences.Editor editor = sf.edit();
//
//        String userid_shared = userID;
//        int userLevel_shared = userLevel;
//
//
//        editor.putString("userID", userid_shared);
//        editor.putInt("userLevel", userLevel_shared);
//
//        editor.commit();



    }

    @Override
    protected void onStop() {
        super.onStop();

//        dialog.dismiss();

    }
}
