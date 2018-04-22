package com.sihwan.iteach12.naverrecognitionapitest;

/**
 * Created by iteach12 on 2018. 4. card_back3..
 */

public class Item {
    int image;
    String title;
    String subTitle;

    int getImage() {
        return this.image;
    }
    String getTitle() {
        return this.title;
    }
    String getSubTitle(){ return this.subTitle; }

    Item(int image, String title, String subTitle) {
        this.image = image;
        this.title = title;
        this.subTitle = subTitle;
    }
}
