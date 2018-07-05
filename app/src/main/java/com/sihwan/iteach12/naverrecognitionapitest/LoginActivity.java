package com.sihwan.iteach12.naverrecognitionapitest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.text.SimpleDateFormat;
import java.util.Random;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = "MyTag";

    //파이어베이스 추가 변수
    private FirebaseAuth mAuth;

    //아이디 저장용
    String userID;
    String userPassword;

    String sfName = "UserID";
    Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        LottieAnimationView lottie = (LottieAnimationView) findViewById(R.id.lottieIntro);
        lottie.playAnimation();


        //아이디 저장하기

        SharedPreferences sf = getSharedPreferences(sfName, 0);

        userID = sf.getString("userID", "");
        userPassword = sf.getString("userPassword", "");



        if (userID == null || "".equals(userID)) {
            userID = getRandomString(10);
            userPassword = getRandomString(6);

        }


        loginButton = (Button) findViewById(R.id.login_button_v2);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        //파이어베이스 구글 로그인 인증
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mAuth = FirebaseAuth.getInstance();

//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(myIntent);
//                    finish();
//
//
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//                // ...
//            }
//        };
        //파이어베이스 구글 로그인 인증


//        mLoginFormView = findViewById(R.id.login_form);
//        mProgressView = findViewById(R.id.login_progress);


    }

    private static String getRandomString(int length)

    {
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();

        String chars[] = "presentChoice,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",");

        for (int i = 0; i < length; i++) {
            buffer.append(chars[random.nextInt(chars.length)]);
        }

        long now = System.currentTimeMillis();
        SimpleDateFormat date = new SimpleDateFormat("SSS");

        buffer.append(date.format(now));
        return buffer.toString();
    }


    private void attemptLogin() {
//        if (mAuthTask != null) {
//
//            return;
//        }
//
//        // Reset errors.
//        userIdEt.setError(null);
//        userPasswordEt.setError(null);
//
//        // Store values at the time of the login attempt.
        String userInputId = userID;
        String email = userInputId + "@chodingcoding.com";

        String password = userPassword;


        createUser(email, password);

    }
//
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//    private void showProgress(final boolean show) {
//        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//        // for very easy animations. If available, use these APIs to fade-in
//        // the progress spinner.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });
//
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
//        } else {
//            // The ViewPropertyAnimator APIs are not available, so simply show
//            // and hide the relevant UI components.
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//        }
//    }


    //email 직접 로그인 관련 만든 코드임
    private void createUser(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

//                            showProgress(false);
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);


                        } else {
                            // If sign in fails, display presentChoice message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            //updateUI(null);

                            loginUser(email, password);
                        }

                        // ...
                    }
                });

    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Name, email address, and profile photo Url
                                String name = user.getDisplayName();
                                String email = user.getEmail();
                                Uri photoUrl = user.getPhotoUrl();

                                // The user's ID, unique to the Firebase project. Do NOT use this value to
                                // authenticate with your backend server, if you have one. Use
                                // FirebaseUser.getToken() instead.
                                String uid = user.getUid();

                                Intent myintent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(myintent);
                                finish();

                            }
                            //updateUI(user);
                        } else {
                            // If sign in fails, display presentChoice message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);

//                            showProgress(false);


                        }


                    }
                });


    }


    @Override
    protected void onStart() {
        super.onStart();

//        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }


        SharedPreferences sf = getSharedPreferences(sfName, 0);
        SharedPreferences.Editor editor = sf.edit();

        String userid_shared = userID;
        String userPassword_shared = userPassword;


        editor.putString("userID", userid_shared);
        editor.putString("userPassword", userPassword_shared);

        editor.commit();


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

