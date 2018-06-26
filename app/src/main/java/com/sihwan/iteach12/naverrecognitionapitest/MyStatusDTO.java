package com.sihwan.iteach12.naverrecognitionapitest;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by iteach12 on 2018. 3. 1..
 */

public class MyStatusDTO {


    public String userName;

    public int userLevel;

    //내 아이템
    public ArrayList<Map<String, Integer>> userItem = new ArrayList<>();
    public ArrayList<Map<String, Integer>> wrongWord = new ArrayList<>();



}
