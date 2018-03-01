package com.sihwan.iteach12.naverrecognitionapitest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iteach12 on 2018. 3. 1..
 */

public class ImageDTO {

    public String imageUrl;
    public String title;
    public String imageName;
    public String description;
    public String uid;
    public String userId;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();
}
