package com.sihwan.iteach12.naverrecognitionapitest;

/**
 * Created by iteach12 on 2018. 4. card_back3..
 */

public class MainMenuItem {

    public String title;
    public String subTitle;
    public String child;
    public int difficult;


    String getTitle() {
        return this.title;
    }
    String getSubTitle(){ return this.subTitle; }
    String getChild1(){ return this.child; }
    int getDifficult(){ return this.difficult; }


    MainMenuItem(String title, String subTitle, String child, int difficult) {

        this.title = title;
        this.subTitle = subTitle;
        this.child = child;
        this.difficult = difficult;

    }
}
