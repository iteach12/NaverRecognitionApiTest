package com.sihwan.iteach12.naverrecognitionapitest;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.github.vivchar.viewpagerindicator.ViewPagerIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.naver.speech.clientapi.SpeechRecognitionResult;
import com.sihwan.iteach12.naverrecognitionapitest.utils.AudioWriterPCM;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class MyDataActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener, ViewPager.OnPageChangeListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private ViewPagerIndicator viewPagerIndicator;

    private static FirebaseAuth auth;

    private DatabaseReference database;

    Query myProblemQuery;


    private static ArrayList<MyProblemDTO> myProblemDTOS;
    private static int currentProblemIndex;

    int currentPoint;
    int currentProgress;








    private static final String TAG = MyDataActivity.class.getSimpleName();
    private static final String CLIENT_ID = "MTaabvfKipKDh2clp5Xl";
    // 1. "내 애플리케이션"에서 Client ID를 확인해서 이곳에 적어주세요.
    // 2. build.gradle (Module:app)에서 패키지명을 실제 개발자센터 애플리케이션 설정의 '안드로이드 앱 패키지 이름'으로 바꿔 주세요

    private MyDataActivity.RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private String mResult;
    private AudioWriterPCM writer;
    //음성합성 관련
    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    static String url = "http://lovepusa.cafe24.com/naverapi.php";
    //contentValues관련 변수들
    String user_name = "iteach12";
    static String user_text;
    String user_voice = "mijin";
    int user_speed = 0;
    //음성합성 관련

    //플로팅 액션 버튼
    FloatingActionButton TTS_btn;
    FloatingActionButton STT_btn;



    //데이터베이스 불러오자.



    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:
                // Now an user can speak.
//                txtResult.setText("Connected");
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;

            case R.id.partialResult:
                // Extract obj property typed with String.
                mResult = (String) (msg.obj);


//                txtResult.setText(mResult);
                break;

            case R.id.finalResult:
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
                    }
                }
                mResult = strBuf.toString();

                checkTheAnswer(finalAnswer);




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
                Toast.makeText(getApplicationContext(), "다 풀었습니다", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(MyDataActivity.this);
                builder.setMessage("모든 문제를 풀었습니다").
                        setTitle("완료");
                builder.setPositiveButton("결과보기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent = new Intent(MyDataActivity.this, Result2Activity.class);

                        intent.putExtra("userPoint", currentPoint);




                        startActivity(intent);


                    }
                }).setNegativeButton("다시하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "문제를 다시 풀어보세요.", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();




            }else{

            }
        }



        if(answer){
            Toast.makeText(getApplicationContext(), "Good", Toast.LENGTH_SHORT).show();

            //이 문제를 풀었으면 점수를 다시 주지 않도록 설정
            if(!myProblemDTOS.get(currentProblemIndex).problemSolve){

                //문제 풀었다 표시
                myProblemDTOS.get(currentProblemIndex).problemSolve = true;





                //정답 맞았다 표시
                myProblemDTOS.get(currentProblemIndex).problemCorrectAnswer = true;

                //점수넣기
                currentPoint += myProblemDTOS.get(currentProblemIndex).problemPoint;

                //진행상황넣기



                Log.i("CheckAnswer", ""+currentProblemIndex);


                //이제 뷰 갱신해주기
                //뷰의 변화를 감지해서 다시 그려줌 ㅋㅋㅋㅋㅋ 대박
                //어뎁터에서 getitemposition을 넣어줌
                //notifydatasetchanged도 넣어줌
                mSectionsPagerAdapter.notifyDataSetChanged();


            }


        }else{

                //문제 풀었다 표시
                myProblemDTOS.get(currentProblemIndex).problemSolve = true;


                //정답 틀렸다 표시
                myProblemDTOS.get(currentProblemIndex).problemCorrectAnswer = false;

                Log.i("CheckAnswer", ""+currentProblemIndex);


                //이제 뷰 갱신해주기
                //뷰의 변화를 감지해서 다시 그려줌 ㅋㅋㅋㅋㅋ 대박
                //어뎁터에서 getitemposition을 넣어줌
                //notifydatasetchanged도 넣어줌
                mSectionsPagerAdapter.notifyDataSetChanged();

        }





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


        //문제 데이터베이스 작성과정


        myProblemDTOS= new ArrayList<>();


        //문제 뽑아내기.
        myProblemQuery = database.child("problemTest").
                orderByChild("problemText");

        //addChileEventListener은 데이터베이스에 추가가될 때 사용 일단 지금은 쓸일이 없지? 아닌가 새로운 아이템 나올때?
        //아니면 새로운 랭킹이 생겼을 때? 아니면 새로운 아이템 있을 때?
        myProblemQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //이게 핵심 ㅋㅋ 문제 가져오는 방법 implements해놨음
        myProblemQuery.addValueEventListener(this);




        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        STT_btn = findViewById(R.id.fab);
        STT_btn.setOnClickListener(this);
        TTS_btn = (FloatingActionButton) findViewById(R.id.fab2);
        TTS_btn.setOnClickListener(this);

        handler = new RecognitionHandler(MyDataActivity.this);
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
        myProblemQuery = database.child("problemTest").
                orderByChild("problemText");
//        currentPoint=0;
//        currentProgress =0;


    }

    @Override
    protected void onRestart(){
        super.onRestart();
        mResult = "";
        myProblemQuery = database.child("problemTest").
                orderByChild("problemText");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

                url = "http://lovepusa.cafe24.com/naverapi.php";



                String mp3_name = auth.getCurrentUser().getEmail().split("@")[0];
                String current_text = myProblemDTOS.get(currentProblemIndex).problemText;

//                AsyncTask를 통해 HttpURLConnection 수행.
                SynsAsyncTask networkTask = new SynsAsyncTask(url, setContentsValues(mp3_name, current_text, user_voice, user_speed));
                networkTask.execute();
                Log.i("Result", setContentsValues(mp3_name, current_text, user_voice, user_speed).toString());



                Snackbar.make(view, "발음듣기", Snackbar.LENGTH_SHORT)
                        .setAction("시작", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                MediaPlayer mediaPlayer = new MediaPlayer();

                                try {

                                    String mp3_name = auth.getCurrentUser().getEmail().split("@")[0];
                                    mediaPlayer.setDataSource("http://lovepusa.cafe24.com/"+mp3_name+".mp3");
                                    mediaPlayer.prepare();
                                    mediaPlayer.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }).show();





                break;


        }


    }




    //ㅋㅋ 문제 뽑아오기. 랜덤 넣기 가능함.
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {

            MyProblemDTO myProblemDTO = postSnapShot.getValue(MyProblemDTO.class);

            //이번 문제들을 리스트에 저장해 둠 여기서 문제당 점수 텍스트 등등 뽑아쓰자


            //Random levelRandom = new Random();
            //levelRandom.nextInt()

            if (myProblemDTO.problemLevel < 2){
                myProblemDTOS.add(myProblemDTO);

            }

        }

        //데이터베이스를 불러온 다음에 화면을 띄워줄라고....
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.



        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        viewPagerIndicator = (ViewPagerIndicator)findViewById(R.id.view_pager_indicator);
        viewPagerIndicator.setupWithViewPager(mViewPager);
        viewPagerIndicator.addOnPageChangeListener(this);


        currentProblemIndex = 0;
        currentPoint=0;
        currentProgress =0;

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


    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.i("ViewPagerChange", "onPageStateChanged"+state);
    }
    //뷰페이저 온 페이지 체인지 리스너




    /**
     * A placeholder fragment containing a simple view.
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
         * Returns a new instance of this fragment for the given section
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


//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            //리스트에서 문제 번호를 뽑아와 그 다음 그 문제번호에 들어있는 문제를 화면에 띄워준다.



            user_text = myProblemDTOS.get(getArguments().getInt(ARG_SECTION_NUMBER)).problemText;

            Log.i("onCreateViewIndex", ""+(getArguments().getInt(ARG_SECTION_NUMBER)));


            textView.setText(user_text);

            //현제 문제 모음에서 지금 문제의 해결 여부를 가져옴.
            if(myProblemDTOS.get(getArguments().getInt(ARG_SECTION_NUMBER)).problemSolve){

                solve = true;
                answer = myProblemDTOS.get(getArguments().getInt(ARG_SECTION_NUMBER)).problemCorrectAnswer;

                if(answer) {
                    rootView.setBackgroundColor(Color.GREEN);

                }else{

                    rootView.setBackgroundColor(Color.RED);
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
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {



        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).



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
        private final WeakReference<MyDataActivity> mActivity;

        RecognitionHandler(MyDataActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MyDataActivity activity = mActivity.get();
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
    //////////////////음성 합성 관련 /////////////////////


}
