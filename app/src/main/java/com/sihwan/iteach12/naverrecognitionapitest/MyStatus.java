package com.sihwan.iteach12.naverrecognitionapitest;

import java.util.ArrayList;

/**
 * Created by iteach12 on 2018. 2. 23..
 */

public class MyStatus {

    String userId;
    String userPassword;
    int userLevel;
    ArrayList<Integer> userItem;
    ArrayList<Integer> userWrongAnswer;



    public MyStatus(String id, String passWord){
            this.userId = id;
            this.userPassword = passWord;
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
}
