package com.sihwan.iteach12.naverrecognitionapitest;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by iteach12 on 2018. 2. 23..
 */

public class MyStatus {

    String userId;
    int userLevel;

    ArrayList<Integer> userProgress;
    ArrayList<Integer> userItem;
    ArrayList<Integer> userWrongAnswer;



    public MyStatus(String id){
            this.userId = id;

    }

    public String getUserId() {
        return userId;
    }

    public int getUserLevel() {
        return userLevel;
    }


    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public ArrayList<Integer> getUserItem() {
        return userItem;
    }

    public void setUserItem(ArrayList<Integer> userItem) {
        this.userItem = userItem;
    }

    public ArrayList<Integer> getUserWrongAnswer() {
        return userWrongAnswer;
    }

    public void setUserWrongAnswer(ArrayList<Integer> userWrongAnswer) {
        this.userWrongAnswer = userWrongAnswer;
    }

    public ArrayList<Integer> getUserProgress() {
        return userProgress;
    }

    public void setUserProgress(ArrayList<Integer> userProgress) {
        this.userProgress = userProgress;
    }
}
