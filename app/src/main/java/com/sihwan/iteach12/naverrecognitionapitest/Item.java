package com.sihwan.iteach12.naverrecognitionapitest;

/**
 * Created by iteach12 on 2018. 4. card_back3..
 */

public class Item {

    String title;
    String subTitle;


    String getTitle() {
        return this.title;
    }
    String getSubTitle(){ return this.subTitle; }

    Item(String title, String subTitle) {

        this.title = title;
        this.subTitle = subTitle;
    }
}
