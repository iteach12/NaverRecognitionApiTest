package com.sihwan.iteach12.naverrecognitionapitest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iteach12 on 2018. 3. 1..
 */

public class MyStatusDTO {

    public int userLevel;
    public Map<Integer, Boolean> userProgress = new HashMap<>();
    public Map<Integer, Boolean> userWrongAnswer = new HashMap<>();
    public Map<Integer, Boolean> userItem = new HashMap<>();
}
