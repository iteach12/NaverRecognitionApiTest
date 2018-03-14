package com.sihwan.iteach12.naverrecognitionapitest;

import android.content.ContentValues;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import android.widget.Button;
import android.widget.TextView;

import com.naver.speech.clientapi.SpeechRecognitionResult;
import com.sihwan.iteach12.naverrecognitionapitest.utils.AudioWriterPCM;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class MyDataActivity extends AppCompatActivity {

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





    private static final String TAG = BasicSysnActivity.class.getSimpleName();
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
    String url;
    //contentValues관련 변수들
    String user_name = "iteach12";
    String user_text;
    String user_voice = "mijin";
    int user_speed = 0;
    //음성합성 관련

    //플로팅 액션 버튼
    FloatingActionButton TTS_btn;
    FloatingActionButton STT_btn;


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
                for(String result : results) {
                    strBuf.append(result);
                    strBuf.append("\n");
                }
                mResult = strBuf.toString();
//              txtResult.setText(mResult);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        STT_btn = findViewById(R.id.fab);
        STT_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "말해보세요.", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

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






            }
        });


        TTS_btn = (FloatingActionButton) findViewById(R.id.fab2);
        TTS_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                url = "http://lovepusa.cafe24.com/naverapi.php";

                // AsyncTask를 통해 HttpURLConnection 수행.

                user_text=mResult;


                SynsAsyncTask networkTask = new SynsAsyncTask(url, setContentsValues(user_name, user_text, user_voice, user_speed));
                networkTask.execute();
                Log.i("Result", setContentsValues(user_name, user_text, user_voice, user_speed).toString());



                Snackbar.make(view, "문제읽기", Snackbar.LENGTH_SHORT)
                        .setAction("재생", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                MediaPlayer mediaPlayer = new MediaPlayer();

                                try {
                                    mediaPlayer.setDataSource("http://lovepusa.cafe24.com/iteach12.mp3");
                                    mediaPlayer.prepare();
                                    mediaPlayer.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }).show();




            }
        });

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

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
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            // 여기있는 숫자를 바꾸면... 원하는 대로 페이지 수가 바뀐다.
            return 10;
        }
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


    public ContentValues setContentsValues(String name, String my_text, String voice, int speed){

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("text", my_text);
        contentValues.put("voice", voice);
        contentValues.put("speed", speed);
        return contentValues;
    }
    //////////////////음성 합성 관련 /////////////////////


}
