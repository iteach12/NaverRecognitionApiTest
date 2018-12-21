package com.sihwan.iteach12.naverrecognitionapitest;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class Result2Activity extends AppCompatActivity implements Animator.AnimatorListener {


    // 파이어베이스 인증
    private FirebaseAuth auth;
    private DatabaseReference database;

    //아이디 저장용
    String userID;
    String sfName = "UserID";
    int userLevel;
    MyStatusDTO my = new MyStatusDTO();


    //TextView pointTextview;
    //TextView textview2;
    TextView mentTextView;


    ArrayList<String> wrongProblem = new ArrayList<>();
    ArrayList<String> present = new ArrayList<>();
    ArrayList<String> goodMent = new ArrayList<>();
    ArrayList<String> normalMent = new ArrayList<>();
    ArrayList<String> badMent = new ArrayList<>();
    ArrayList<LottieAnimationView> starViews = new ArrayList<>();
    int starCount;
    int animListenerCount;


    String presentChoice;
    String mentChoice;

    LottieAnimationView lottieTrophy;
    LottieAnimationView lottiePresent;
    LottieAnimationView lottieStar1;
    LottieAnimationView lottieStar2;
    LottieAnimationView lottieStar3;
    LottieAnimationView lottieStar4;
    LottieAnimationView lottieStar5;


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


        init();


        //점수 넘어온거
        Intent intent = getIntent();
        final int userPoint = intent.getExtras().getInt("userPoint");
        final int userResult = intent.getExtras().getInt("result");

        //점수 넘어온거


        //틀린문제넘어온것
        wrongProblem = intent.getStringArrayListExtra("wrong");
        //틀린문제 넘어온것

        //리스너 카운트 세팅
        animListenerCount = 0;

        //점수를 넣어서 몇번 돌리는지 체크하기
        checkCount(userResult);

        // pointTextview.setText(String.valueOf(userPoint));


        // 선물 넣기
        present.add("'ㄴ'훈장 (일반등급)");
        present.add("'ㄱ'훈장 (일반등급)");
        present.add("'ㄷ'훈장 (일반등급)");
        present.add("'ㄹ'훈장 (일반등급)");
        present.add("'ㅁ'훈장 (일반등급)");
        present.add("'ㅂ'훈장 (일반등급)");
        present.add("'ㅅ'훈장 (일반등급)");
        present.add("'ㅇ'훈장 (일반등급)");
        present.add("'ㅈ'훈장 (일반등급)");
        present.add("'ㅊ'훈장 (일반등급)");
        present.add("'ㅋ'훈장 (일반등급)");
        present.add("'ㅌ'훈장 (일반등급)");
        present.add("'ㅍ'훈장 (일반등급)");
        present.add("'ㅎ'훈장 (일반등급)");
        present.add("'ㄲ'훈장 (전설등급)");
        present.add("'ㄸ'훈장 (전설등급)");
        present.add("'ㅒ'훈장 (전설등급)");
        present.add("'ㅖ'훈장 (전설등급)");
        present.add("'ㅉ'훈장 (전설등급)");


        //잘했을 때 멘트
        goodMent.add("정말 잘했어요!!");
        goodMent.add("실력이 대단한데요?");
        goodMent.add("아주 잘했습니다!");
        goodMent.add("발음이 정확하네요!");
        goodMent.add("실력이 많이 늘었네요!");
        goodMent.add("열심히 하는 모습 보기 좋아요!");
        goodMent.add("훌륭합니다!");


        //보통일 때 멘트
        normalMent.add("실력이 많이 늘었어요!");
        normalMent.add("자주 틀리는 발음이 있네요!");
        normalMent.add("발음이 꽤 정확한데요?");
        normalMent.add("잘하고 있어요!");
        normalMent.add("조금 더 노력해 볼까요!");
        normalMent.add("힘내세요! 발음이 많이 좋아졌어요!");

        //나쁠 때 멘트
        badMent.add("연습을 하면 잘할 수 있어요!");
        badMent.add("포기하지 말고 힘내세요!");
        badMent.add("할 수 있어요!");
        badMent.add("조금 더 노력해 볼까요?");
        badMent.add("괜찮아요! 잘하고 있어요!");


        Collections.shuffle(present);
        Collections.shuffle(goodMent);
        Collections.shuffle(normalMent);
        Collections.shuffle(badMent);

        if(userPoint >=8){
            mentChoice = goodMent.get(0);
        }else if(userPoint <8 && userPoint>=3){
            mentChoice = normalMent.get(0);
        }else{
            mentChoice = badMent.get(0);
        }


        //점수에 따라서 멘트 선택
        mentTextView.setText(mentChoice);

        //선물 주기
        presentChoice = present.get(0);




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

        //페이지가 시작되면 일단 점수부터 추가하기
        //push가 붙으면 계속 추가하기임 같은 데이터라 할지라도 또 추가됨.
        //push를 빼면 덮어쓰기임 이전 데이터를 지우고 지금 데이터를 입력함.
        database.child("userProfile").child(userID).child("userLevel").setValue(userLevel+userPoint);



        //일단 트로피 먼저 시작
        lottieTrophy.playAnimation();

        //트로피 애니메이션 끝나면 star애니메이션 등장
        lottieTrophy.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                starViews.get(animListenerCount).playAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });


        lottiePresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Result2Activity.this);
                builder.setTitle("보상").
                        setMessage("당신의 선물은 "+ presentChoice +" 입니다").

                        setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {




                                Intent myIntent = new Intent (Result2Activity.this, MainActivity.class);
                                startActivity(myIntent);
                                finish();


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

                WrongWordDialog dialog = new WrongWordDialog(Result2Activity.this, wrongProblem);
                dialog.show();

            }
        });





    }

    private void init() {

        lottieTrophy = (LottieAnimationView)findViewById(R.id.lottieTrophy);
        lottiePresent = (LottieAnimationView)findViewById(R.id.lottiePresent);
        lottieStar1 = (LottieAnimationView)findViewById(R.id.lottieStar1);
        lottieStar2 = (LottieAnimationView)findViewById(R.id.lottieStar2);
        lottieStar3 = (LottieAnimationView)findViewById(R.id.lottieStar3);
        lottieStar4 = (LottieAnimationView)findViewById(R.id.lottieStar4);
        lottieStar5 = (LottieAnimationView)findViewById(R.id.lottieStar5);
        lottieStar1.addAnimatorListener(this);
        lottieStar2.addAnimatorListener(this);
        lottieStar3.addAnimatorListener(this);
        lottieStar4.addAnimatorListener(this);
        lottieStar5.addAnimatorListener(this);


        float starSpeed = 3.0f; //속도 두배
        lottieStar1.setSpeed(starSpeed);
        lottieStar2.setSpeed(starSpeed);
        lottieStar3.setSpeed(starSpeed);
        lottieStar4.setSpeed(starSpeed);
        lottieStar5.setSpeed(starSpeed);

        starViews.add(lottieStar1);
        starViews.add(lottieStar2);
        starViews.add(lottieStar3);
        starViews.add(lottieStar4);
        starViews.add(lottieStar5);


        //textview2 = (TextView)findViewById(R.id.secondTextView);
        //pointTextview = (TextView)findViewById(R.id.pointTextView);
        mentTextView = (TextView)findViewById(R.id.mentTv);

    }


    public void checkCount(int point){


        switch (point){

            case 5:
                starCount = 5;
                break;
            case 4:
                starCount = 4;
                break;
            case 3:
                starCount = 3;
                break;
            case 2:
                starCount = 2;
                break;
            case 1:
                starCount = 1;
                break;

        }


        Log.i("별 개수", String.valueOf(starCount));
//
//        if(point >=8){
//            starCount = 5;
//        }else if(point < 8 && point >= 6){
//            starCount = 4;
//        }else if(point < 6 && point >= 4){
//            starCount = 3;
//        }else if(point < 4 && point >= 2){
//            starCount = 2;
//        }else{
//            starCount = 1;
//        }

    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {

        Log.i("CheckAnimViewCount", String.valueOf(animListenerCount));
        Log.i("CheckStarCount", String.valueOf(starCount));


        if(starCount != 1){

            animListenerCount++;
            starViews.get(animListenerCount).playAnimation();
            starCount--;


        }else{
            lottiePresent.playAnimation();
        }

    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }

    @Override
    public void onBackPressed() {



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("정말 종료하시겠습니까?").
                setMessage("지금 종료 하면 진행상황이 모두 사라집니다.").
                setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setNegativeButton("계속하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
