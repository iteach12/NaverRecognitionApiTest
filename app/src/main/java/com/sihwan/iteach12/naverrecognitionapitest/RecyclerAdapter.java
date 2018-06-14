package com.sihwan.iteach12.naverrecognitionapitest;

/**
 * Created by iteach12 on 2018. 4. card_back3..
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;




import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    List<Item> items;
    int item_layout;

    public RecyclerAdapter(Context context, List<Item> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_main1, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Item item = items.get(position);
//        Drawable drawable = ContextCompat.getDrawable(context, item.getImage());
//        Glide.with(context).load(item.getImage()).into(holder.image);
//        holder.image.setImageDrawable(drawable);
        holder.title.setText(item.getTitle());
        holder.subTitle.setText(item.getSubTitle());
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //여기서 문제풀이로 이동할거야.
                Intent myIntent = new Intent(context, MyDataActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myIntent.putExtra("problem_choice1", item.getChild1());
                myIntent.putExtra("difficult", item.getDifficult());
                context.startActivity(myIntent);


            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView image;
        TextView title;
        TextView subTitle;

//        Button btn1;
//        Button btn2;

        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
//            image = (ImageView) itemView.findViewById(R.id.img_main_card_1);
            title = (TextView) itemView.findViewById(R.id.tv_card_main_1_title);
            subTitle = (TextView) itemView.findViewById(R.id.tv_card_main1_subtitle);
//            btn1 = (Button)itemView.findViewById(R.id.btn_card_main1_action1);
//            btn2 = (Button)itemView.findViewById(R.id.btn_card_main1_action2);
            cardview = (CardView) itemView.findViewById(R.id.card_main_1_1);





        }
    }
}