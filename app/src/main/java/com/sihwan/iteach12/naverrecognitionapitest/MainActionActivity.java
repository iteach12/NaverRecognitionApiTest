package com.sihwan.iteach12.naverrecognitionapitest;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;


import com.airbnb.lottie.LottieAnimationView;
import com.github.vivchar.viewpagerindicator.ViewPagerIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.naver.speech.clientapi.SpeechRecognitionResult;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.sihwan.iteach12.naverrecognitionapitest.utils.AudioWriterPCM;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActionActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener, ViewPager.OnPageChangeListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use presentChoice
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to presentChoice
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private CustomViewPager mViewPager;

    private ViewPagerIndicator viewPagerIndicator;



    private static FirebaseAuth auth;

    private DatabaseReference database;


    Query myProblemQuery;


    //문제 관련 정보 인텐트로 넘어온 것
    String problem_choice1;
    int problem_diffi;


    private static ArrayList<MyProblemDTO> myProblemDTOS;
    private static ArrayList<MyProblemDTO> randomDTOS;
    private static ArrayList<String> wrongProblem;
    private static String wrong_text;
    private static ArrayList<String> problem_key_list;
    private static int currentProblemIndex;

    //아이디 저장용
    String userID;
    String sfName = "UserID";


    //현재 점수
    int currentPoint;

    //현재 푼 문제 개수 (문제 총 개수와 같아지면 결과 페이지로 가기)
    int currentProgress;

    //현재 에너지(승급전, 달인문제)
    int currentEnergy;

    //현재 풀 문제의 개수
    final private int problem_count = 5;

    //사운드 관련
    private MediaPlayer correctSound; //정답일 때
    private MediaPlayer wrongSound; //오답일 때


    //shinebutton 에너지 관련 (이거 굳이 안써도 되는데, lottie로 통일시키기?)
    ShineButton shineButton1;
    ShineButton shineButton2;
    ShineButton shineButton3;


    //현재 상태
    TextView nowStatus;



    private static final String TAG = MainActionActivity.class.getSimpleName();
    //    private static final String CLIENT_ID = "MTaabvfKipKDh2clp5Xl";
    //    v1 기존 아이디입니다


    //  v2 새로운 아이디 입니다
    private static final String CLIENT_ID = "q8pyzxbayz";


    private MainActionActivity.RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private static String mResult;
    private AudioWriterPCM writer;

    //음성합성 관련
    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    static String url = "http://lovepusa.cafe24.com/naverapi_v2.php";

    //contentValues관련 변수들
    String user_name = "iteach12";
    static String user_text;
    String user_voice = "mijin";
    int user_speed = 0;
    //음성합성 관련



    //플로팅 액션 버튼
    FloatingActionButton TTS_btn;
    FloatingActionButton STT_btn;


    //로티에 애니메이션 뷰
    LottieAnimationView lottieCorrect;
    LottieAnimationView lottieRecoding;
    LottieAnimationView fireworks1;


    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:
                // Now an user can speak.
//                txtResult.setText("Connected");
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                STT_btn.setEnabled(true);
                STT_btn.setClickable(true);

                break;

            case R.id.audioRecording:

                //녹음시작되면 애니메이션 시작
                if(!lottieRecoding.isAnimating()){
                    lottieRecoding.setVisibility(View.VISIBLE);
                    lottieRecoding.playAnimation();
                    mViewPager.setPagingEnabled(false);

                }
                writer.write((short[]) msg.obj);
                STT_btn.setEnabled(false);
                TTS_btn.setEnabled(false);
                STT_btn.setClickable(false);
                TTS_btn.setClickable(false);
                break;

            case R.id.partialResult:
                // Extract obj property typed with String.
                mResult = (String) (msg.obj);
                break;

            case R.id.finalResult:

                //최종결과 나오면 애니메이션끝내기
                mViewPager.setPagingEnabled(true);
                lottieRecoding.setVisibility(View.INVISIBLE);
                lottieRecoding.pauseAnimation();

                // Extract obj property typed with String array.
                // The first element is recognition result for speech.
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();
                Boolean finalAnswer=false;

                for(String result : results) {
                    strBuf.append(result);
                    strBuf.append("\n");
                    if(result.contains(myProblemDTOS.get(mViewPager.getCurrentItem()).problemText)){
                        Log.i("Answer", "correct");

                        //정답일 때
                        user_text=result;
                        finalAnswer=true;
                    }else{
                        user_text=result;
                        wrong_text=myProblemDTOS.get(mViewPager.getCurrentItem()).problemText;
                        Log.i("Answer", "wrooooong");
                    }
                }
                mResult = strBuf.toString();
                checkTheAnswer(finalAnswer);
                STT_btn.setEnabled(true);
                TTS_btn.setEnabled(true);
                STT_btn.setClickable(true);
                TTS_btn.setClickable(true);
                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }

                mResult = "Error code : " + msg.obj.toString();
//                txtResult.setText(mResult);
//                btnStart.setText(R.string.str_start);
                STT_btn.setEnabled(true);
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
//                btnStart.setText(R.string.str_start);
                STT_btn.setEnabled(true);
                break;
        }
    }



    private void checkTheAnswer(Boolean answer) {


        //이 문제 이미 풀은거면 안올림. 문제 안풀은거면 하나 올림
        if(!myProblemDTOS.get(currentProblemIndex).problemSolve){
            currentProgress++;

            if(currentProgress>=myProblemDTOS.size()){


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActionActivity.this);
                builder.setMessage("모든 문제를 풀었습니다").
                        setTitle("완료");
                builder.setPositiveButton("결과보기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent = new Intent(MainActionActivity.this, Result2Activity.class);
                        intent.putExtra("userPoint", currentPoint);
                        intent.putExtra("result", resultJudge(currentPoint));
                        intent.putStringArrayListExtra("wrong", wrongProblem);

                        Log.i("result_log", "현재 결과값 :"+String.valueOf(resultJudge(currentPoint)));
                        Log.i("result_log", "현재 점수 :"+String.valueOf(currentPoint));

                        startActivity(intent);
                        //끄기
                        naverRecognizer.getSpeechRecognizer().release();
                        finish();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

            }else{

            }
        }
        if(answer){

            // 정답 애니메이션 시작
            lottieCorrect.playAnimation();
            // 정답 사운드 시작
            if(correctSound != null){
                correctIsPlaying();
            }else{
                correctSound = MediaPlayer.create(this, R.raw.correct);
                correctIsPlaying();
            }

            //이 문제를 풀었으면 점수를 다시 주지 않도록 설정
            if(!myProblemDTOS.get(currentProblemIndex).problemSolve){

                //문제 풀었다 표시
                myProblemDTOS.get(currentProblemIndex).problemSolve = true;

                //정답 맞았다 표시
                myProblemDTOS.get(currentProblemIndex).problemCorrectAnswer = true;

                //정답 맞춘 경우를 uid키값으로 저장해두기
                ///그래야 나중에 맞춘 문제를 알아낼 수 있으니깐??
                problem_key_list.get(currentProblemIndex);

                //점수넣기 & 서버 업로드하기
                currentPoint += myProblemDTOS.get(currentProblemIndex).problemPoint;

                //이렇게 데이터베이스에 하위 차일드를 지정해서 필요한 데이터만 올려줄 수도 있고만 기래.

                //진행상황넣기
                Log.i("CheckAnswer", ""+currentProblemIndex);


                //이제 뷰 갱신해주기
                //뷰의 변화를 감지해서 다시 그려줌 ㅋㅋㅋㅋㅋ 대박
                //어뎁터에서 getitemposition을 넣어줌
                //notifydatasetchanged도 넣어줌
                mSectionsPagerAdapter.notifyDataSetChanged();


            }


        }else{

                //오답 사운드 시작
                if(wrongSound != null){
                    wrongIsPlaying();
                }else{
                    wrongSound = MediaPlayer.create(this, R.raw.wrong);
                    wrongIsPlaying();
                }



                //문제 풀었다 표시
                myProblemDTOS.get(currentProblemIndex).problemSolve = true;


                //정답 틀렸다 표시
                myProblemDTOS.get(currentProblemIndex).problemCorrectAnswer = false;

                //에너지 깎기
                if(problem_diffi == 100){


                    currentEnergy-=1;

                    if(currentEnergy == 2){
                        shineButton3.setChecked(false,true);
                    }else if(currentEnergy ==1){
                        shineButton2.setChecked(false,true);
                    }else if(currentEnergy == 0){
                        shineButton1.setChecked(false,true);


                        AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActionActivity.this);
                        builder2.setMessage("실패").
                                setTitle("에너지가 바닥났습니다.");
                        builder2.setPositiveButton("결과보기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent intent = new Intent(MainActionActivity.this, Result2Activity.class);

                                intent.putExtra("userPoint", currentPoint);
                                intent.putExtra("result", resultJudge(currentPoint));

                                startActivity(intent);

                                //끄기
                                naverRecognizer.getSpeechRecognizer().release();

                                finish();


                            }
                        });

                        AlertDialog dialog2 = builder2.create();
                        dialog2.setCancelable(false);
                        dialog2.setCanceledOnTouchOutside(false);
                        dialog2.show();
                    }



                }


                //틀린 문제 확인하기
                wrongProblem.add(wrong_text);


                //틀린문제는 내가 따로 저장해 둬야 함.
                database.child("userProfile").child(userID).child("wrongProblem").push().setValue(problem_key_list.get(currentProblemIndex));


                Log.i("CheckAnswer", ""+currentProblemIndex);


                //이제 뷰 갱신해주기
                //뷰의 변화를 감지해서 다시 그려줌 ㅋㅋㅋㅋㅋ 대박
                //어뎁터에서 getitemposition을 넣어줌
                //notifydatasetchanged도 넣어줌
                mSectionsPagerAdapter.notifyDataSetChanged();

        }

    }


    //사운드 관련 메소드
    private void correctIsPlaying(){

        if(!correctSound.isPlaying()){
            correctSound.start();
        }else{
            correctSound.stop();
            correctSound.release();
            correctSound = null;
        }


    }

    private void wrongIsPlaying(){

        if(!wrongSound.isPlaying()){
            wrongSound.start();
        }else{
            wrongSound.stop();
            wrongSound.release();
            wrongSound = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(correctSound != null){
            correctSound.release();
            correctSound = null;

        }

        if(wrongSound != null){
            wrongSound.release();
            wrongSound = null;

        }
    }

    protected int resultJudge(int point){

        int result;

        int result_percent = ((point*100)/myProblemDTOS.size());

        if(result_percent >= 85){
            result = 5;
        }else if(result_percent >= 75 && result_percent < 85){
            result = 4;
        }else if(result_percent >= 65 && result_percent < 75){
            result = 3;
        }else if(result_percent >= 55 && result_percent < 65){
            result = 2;
        }else if(result_percent >= 45 && result_percent < 55){
            result = 1;
        }else{
            result = 1;
        }

        return result;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_data);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //firebase인증용
        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance().getReference();
        user_name = auth.getCurrentUser().getEmail().split("@")[0];


        SharedPreferences sf = getSharedPreferences(sfName, 0);

        userID = sf.getString("userID", "");


        //인텐트 넘어온것. 문제 뽑아올것 정하기 child로 구분됨.
        Intent intent = getIntent();

        //문제의 종류 차일드로 저장해 놨음.
        problem_choice1 = intent.getExtras().getString("problem_choice1");

        //난이도
        problem_diffi = intent.getExtras().getInt("difficult");



        //문제 데이터베이스 작성과정


        myProblemDTOS= new ArrayList<>();
        randomDTOS = new ArrayList<>();
        problem_key_list = new ArrayList<>();
        wrongProblem = new ArrayList<>();


        //문제 뽑아내기.
        myProblemQuery = database.child("problem").child(problem_choice1);
        myProblemQuery.addListenerForSingleValueEvent(this);

        //addChileEventListener은 데이터베이스에 추가가될 때 사용 일단 지금은 쓸일이 없지? 아닌가 새로운 아이템 나올때?
        //아니면 새로운 랭킹이 생겼을 때? 아니면 새로운 아이템 있을 때?
        //이게 핵심 ㅋㅋ 문제 가져오는 방법 implements해놨음

        // Create the adapter that will return presentChoice fragment for each of the three
        // primary sections of the activity.

        STT_btn = findViewById(R.id.fab);
        STT_btn.setOnClickListener(this);
        TTS_btn = (FloatingActionButton) findViewById(R.id.fab2);
        TTS_btn.setOnClickListener(this);


        //현재 상태 나타낼 텍스트뷰(녹음중인지 뭔지)

        lottieRecoding = (LottieAnimationView)findViewById(R.id.recoding_lottie);
        lottieRecoding.setVisibility(View.INVISIBLE);

        //끝났을 때 폭죽놀이 추가







        //내 에너지.
        shineButton1 = (ShineButton)findViewById(R.id.po_image1);
        shineButton2 = (ShineButton)findViewById(R.id.po_image2);
        shineButton3 = (ShineButton)findViewById(R.id.po_image3);
        shineButton1.setChecked(true, true);
        shineButton2.setChecked(true, true);
        shineButton3.setChecked(true, true);

        shineButton1.setVisibility(View.INVISIBLE);
        shineButton2.setVisibility(View.INVISIBLE);
        shineButton3.setVisibility(View.INVISIBLE);

        lottieCorrect = (LottieAnimationView) findViewById(R.id.lottieCorrect);
        lottieCorrect.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                lottieCorrect.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                lottieCorrect.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        lottieCorrect.setVisibility(View.INVISIBLE);



        handler = new RecognitionHandler(MainActionActivity.this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);


    }


    @Override
    protected void onStart() {
        super.onStart();
        // NOTE : initialize() must be called on start time.
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mResult = "";

//        currentPoint=0;
//        currentProgress =0;


    }

    @Override
    protected void onRestart(){
        super.onRestart();
        mResult = "";
//        myProblemQuery = database.child("problem").child("basic_1").
//                orderByChild("problemText");
//        currentPoint=0;
//        currentProgress =0;


    }

    @Override
    protected void onStop() {
        super.onStop();
        // NOTE : release() must be called on stop time.
        naverRecognizer.getSpeechRecognizer().release();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_data, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.fab:
                //음성인식하기 버튼 누르면
                if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    // Start button is pushed when SpeechRecognizer's state is inactive.
                    // Run SpeechRecongizer by calling recognize().
                    mResult = "";

                    naverRecognizer.recognize();
                } else {
                    Log.d(TAG, "stop and wait Final Result");
                    STT_btn.setEnabled(false);
                    naverRecognizer.getSpeechRecognizer().stop();
                }

                break;
            case R.id.fab2:


                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });

                try {

                    String mp3_name = auth.getCurrentUser().getEmail().split("@")[0];
                    mediaPlayer.setDataSource("http://lovepusa.cafe24.com/"+mp3_name+".mp3");
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }




                break;


        }


    }




    //ㅋㅋ 문제 뽑아오기. 랜덤 넣기 가능함.
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        myProblemDTOS.clear();
        problem_key_list.clear();
        for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {

            MyProblemDTO myProblemDTO = postSnapShot.getValue(MyProblemDTO.class);
            String problemKey = postSnapShot.getKey();


            //이번 문제들을 리스트에 저장해 둠 여기서 문제당 점수 텍스트 등등 뽑아쓰자


            if(problem_diffi!=100){
                if(myProblemDTO.problemLevel == problem_diffi){
                    myProblemDTOS.add(myProblemDTO);
                    randomDTOS.add(myProblemDTO);
                    problem_key_list.add(problemKey);
                }
            }else{
                myProblemDTOS.add(myProblemDTO);
                randomDTOS.add(myProblemDTO);
                problem_key_list.add(problemKey);

            }


        }

        // 여기 문제 개수는 바꿔주자. 10개는 너무 많아.
        // 아니면 승급전이나 달인 문제 때는 10개 그 외에는 5개 정도?? 아니면 난이도에 따라서 문제 개수 바꾸기
        if(myProblemDTOS.size()>10){

            //문제가 개수가 10개를 넘으면 myProblemDTOS를 클리어 시킨다. 싹 지워주고 랜덤으로 10개를 넣어줄거야
            myProblemDTOS.clear();
            Random r = new Random();
            for (int i=0;i<10;i++){
                int bound = r.nextInt(randomDTOS.size()-1);
                myProblemDTOS.add(randomDTOS.get(bound));
                randomDTOS.remove(bound);
            }
        }


        Collections.shuffle(myProblemDTOS);


        //데이터베이스를 불러온 다음에 화면을 띄워줄라고....
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.

        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);



        viewPagerIndicator = (ViewPagerIndicator)findViewById(R.id.view_pager_indicator);
        viewPagerIndicator.setupWithViewPager(mViewPager);
        viewPagerIndicator.addOnPageChangeListener(this);


        if(problem_diffi == 100){

            currentEnergy = 3;

            shineButton1.setVisibility(View.VISIBLE);
            shineButton2.setVisibility(View.VISIBLE);
            shineButton3.setVisibility(View.VISIBLE);

        }


        currentProblemIndex = 0;
        currentPoint=0;
        currentProgress =0;

        //최초의 mp3 만들어두기
        makeMp3Sysn();

        //push test git 윈도우와 연동중

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }



    //뷰페이저 온 페이지 체인지 리스너
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        Log.i("ViewPagerChange", "onPageScrolled"+position);


    }

    @Override
    public void onPageSelected(int position) {

        currentProblemIndex = position;
        Log.i("position", ""+position);


        // 음성 인식 시작
        // 만약 이 문제를 풀었던 문제라면? 음성인식 시작하지 않음.
        if(!myProblemDTOS.get(currentProblemIndex).problemSolve){

            if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
                // Start button is pushed when SpeechRecognizer's state is inactive.
                // Run SpeechRecongizer by calling recognize().
                mResult = "";

                naverRecognizer.recognize();
            }

        }

        //화면 바뀌면 알맞게 mp3만들기
        makeMp3Sysn();


    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.i("ViewPagerChange", "onPageStateChanged"+state);

    }


    //뷰페이저 온 페이지 체인지 리스너






    /**
     * A placeholder fragment containing presentChoice simple view.
     */
    public static class PlaceholderFragment extends Fragment{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public Boolean solve = false;
        public Boolean answer = false;



        public PlaceholderFragment() {
        }

        /**
         * Returns presentChoice new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);


            return fragment;
        }




        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.fragment_my_data, container, false);

            TextView textView = (TextView) rootView.findViewById(R.id.section_label);

            user_text = myProblemDTOS.get(getArguments().getInt(ARG_SECTION_NUMBER)).problemText;

            Log.i("onCreateViewIndex", ""+(getArguments().getInt(ARG_SECTION_NUMBER)));


            textView.setText(user_text);
            textView.setTextColor(Color.BLACK);

            //메인 핸들러에서 (음성인식 쪽) 프래그먼트의 뷰를 직접 건드리지 못하더라??
            //static이 아닌 값을 못가져와서 mResult를 static으로 만들어 줬다. 문제가 생길까??
//            my_pronon.setText(mResult);

            //현제 문제 모음에서 지금 문제의 해결 여부를 가져옴.
            if(myProblemDTOS.get(getArguments().getInt(ARG_SECTION_NUMBER)).problemSolve){

                solve = true;
                answer = myProblemDTOS.get(getArguments().getInt(ARG_SECTION_NUMBER)).problemCorrectAnswer;

                if(answer) {
                    textView.setTextColor(Color.WHITE);


                }else{

                    textView.setTextColor(Color.DKGRAY);
                }
            }

            return rootView;
        }


        @Override
        public void onDestroyView() {
            super.onDestroyView();

            Log.d("onDestroyView", "onDestroyView(First)"+currentProblemIndex);

        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns presentChoice fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {



        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return presentChoice PlaceholderFragment (defined as presentChoice static inner class below).



            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            // 여기있는 숫자를 바꾸면... 원하는 대로 페이지 수가 바뀐다.
            return myProblemDTOS.size();
        }

        //뷰 다시 그려줄 때 필요한 거임 ㅋㅋㅋㅋ
        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        //뷰 다시 그려줄 때 필요한 거임 ㅋㅋㅋㅋ






    }



    static class RecognitionHandler extends Handler {
        private final WeakReference<MainActionActivity> mActivity;

        RecognitionHandler(MainActionActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActionActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }


    public static ContentValues setContentsValues(String name, String my_text, String voice, int speed){

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("text", my_text);
        contentValues.put("voice", voice);
        contentValues.put("speed", speed);
        return contentValues;
    }

    private void makeMp3Sysn(){



        String mp3_name = auth.getCurrentUser().getEmail().split("@")[0];
        String current_text = myProblemDTOS.get(currentProblemIndex).problemText;

        // AsyncTask를 통해 HttpURLConnection 수행.
        SynsAsyncTask networkTask = new SynsAsyncTask(url, setContentsValues(mp3_name, current_text, user_voice, user_speed));
        networkTask.execute();
        Log.i("Result", setContentsValues(mp3_name, current_text, user_voice, user_speed).toString());


    }
    //////////////////음성 합성 관련 /////////////////////





}
