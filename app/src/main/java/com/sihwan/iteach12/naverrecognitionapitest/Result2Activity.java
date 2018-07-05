package com.sihwan.iteach12.naverrecognitionapitest;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

public class Result2Activity extends AppCompatActivity implements Animator.AnimatorListener {



    private FirebaseAuth auth;
    private DatabaseReference database;



    //아이디 저장용
    String userID;
    String sfName = "UserID";
    int userLevel;
    MyStatusDTO my = new MyStatusDTO();


//    TextView pointTextview;
//    TextView textview2;
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

        float starSpeed = 2.0f; //속도 두배
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



//        textview2 = (TextView)findViewById(R.id.secondTextView);
//        pointTextview = (TextView)findViewById(R.id.pointTextView);
        mentTextView = (TextView)findViewById(R.id.mentTv);


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

//        pointTextview.setText(String.valueOf(userPoint));


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


        //잘했을 때 멘트
        goodMent.add("정말 잘했어요!!");
        goodMent.add("실력이 대단한데요?");
        goodMent.add("아주 잘했습니다!");
        goodMent.add("발음이 정확하네요!");
        goodMent.add("실력이 많이 늘었네요!");
        goodMent.add("열심히 하는 모습 보기 좋아요!");
        goodMent.add("훌륭합니다!");


        //보통일 때 멘트
        normalMent.add("잘하네요! 많이 늘었어요!");
        normalMent.add("자주 틀리는 발음이 있네요!");
        normalMent.add("발음이 좋은데요?");
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


                                //push가 붙으면 계속 추가하기임 같은 데이터라 할지라도 또 추가됨.
                                //push를 빼면 덮어쓰기임 이전 데이터를 지우고 지금 데이터를 입력함.
                                database.child("userProfile").child(userID).child("userLevel").setValue(userLevel+userPoint);

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

                String wrongResult = "";
                for(int j=0;j<wrongProblem.size();j++){
                    if(j==wrongProblem.size()-1){
                        wrongResult+=wrongProblem.get(j).toString();
                    }else{
                        wrongResult+=wrongProblem.get(j).toString() + ", ";
                    }


                }


                Snackbar.make(view, "틀린문제 : "+wrongResult, Snackbar.LENGTH_LONG)
                        .setAction("확인", null).show();
            }
        });





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
}
