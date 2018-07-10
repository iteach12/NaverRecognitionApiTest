package com.sihwan.iteach12.naverrecognitionapitest;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.Window;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import java.util.ArrayList;

/**
 * Created by iteach12 on 2018. 7. 8..
 */

public class WrongWordDialog extends Dialog implements Animator.AnimatorListener{
    private static final int LAYOUT = R.layout.wrong_dialog;

    private Context context;

    private TextView wrongTextView;
    private String wrongResult = "";
    private LottieAnimationView crying;
    private ConstraintLayout mainLayout;

    private ArrayList<String> wrongList = new ArrayList<>();



    public WrongWordDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public WrongWordDialog(Context context, ArrayList<String> wrongList){
        super(context);
        this.context = context;
        this.wrongList = wrongList;




    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 삭제
        setContentView(LAYOUT);

        for(int j=0;j<wrongList.size();j++) {
            if (j == wrongList.size() - 1) {
                wrongResult += wrongList.get(j).toString();
            } else {
                wrongResult += wrongList.get(j).toString() + ", ";
            }
        }


        crying = (LottieAnimationView)findViewById(R.id.lottieimage);
        crying.playAnimation();
        wrongTextView = (TextView) findViewById(R.id.wrongTextview);
        wrongTextView.setText(wrongResult);



    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {

    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
