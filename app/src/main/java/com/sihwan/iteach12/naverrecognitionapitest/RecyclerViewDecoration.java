package com.sihwan.iteach12.naverrecognitionapitest;


import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.view.View;

/**
 * Created by iteach12 on 2018. 4. 20..
 */

public class RecyclerViewDecoration extends RecyclerView.ItemDecoration {
    private final int divHeight;

    public RecyclerViewDecoration(int divHeight){
        this.divHeight = divHeight;

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = divHeight;
    }
}
