package com.sihwan.iteach12.naverrecognitionapitest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iteach12 on 2018. 3. 1..
 */

public class MyStatusDTO {

    public int userLevel;
    public Map<String, Boolean> userProgress = new HashMap<>();
    public Map<String, Boolean> userWrongAnswer = new HashMap<>();
    public Map<String, Boolean> userItem = new HashMap<>();
}
