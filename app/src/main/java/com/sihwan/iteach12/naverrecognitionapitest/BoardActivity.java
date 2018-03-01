package com.sihwan.iteach12.naverrecognitionapitest;


import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;



import java.util.ArrayList;
import java.util.List;

public class BoardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ImageDTO> imageDTOs = new ArrayList<>();
    private List<String> uidLists = new ArrayList<>();
    private FirebaseDatabase database;
    private BoardRecyclerViewAdapter boardRecyclerViewAdapter;
    private FirebaseAuth auth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerView = (RecyclerView)findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        boardRecyclerViewAdapter = new BoardRecyclerViewAdapter();
        recyclerView.setAdapter(boardRecyclerViewAdapter);
        database.getReference().child("images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imageDTOs.clear();
                uidLists.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ImageDTO imageDTO = snapshot.getValue(ImageDTO.class);
                    String uidKey = snapshot.getKey();
                    imageDTOs.add(imageDTO);
                    uidLists.add(uidKey);



                }
                boardRecyclerViewAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    class BoardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{





        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_board,parent,false);


            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            ((CustomViewHolder)holder).textView1.setText(imageDTOs.get(position).title);
            ((CustomViewHolder)holder).textView2.setText(imageDTOs.get(position).description);

            Glide.with(holder.itemView.getContext()).load(imageDTOs.get(position).imageUrl).into(((CustomViewHolder)holder).imageView);

            ((CustomViewHolder)holder).starButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onStarClicked(database.getReference().child("images").child(uidLists.get(position)));
                }
            });

            if(imageDTOs.get(position).stars.containsKey(auth.getCurrentUser().getUid())){
                ((CustomViewHolder)holder).starButton.setImageResource(R.drawable.ic_favorite_black_24dp);

            }else {
                ((CustomViewHolder)holder).starButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }


        }

        @Override
        public int getItemCount() {
            return imageDTOs.size();
        }

        private void onStarClicked(DatabaseReference postRef){

            postRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    ImageDTO imageDTO = mutableData.getValue(ImageDTO.class);
                    if (imageDTO == null) {
                        mutableData.setValue(imageDTO);
                    }
                    if(imageDTO.stars.containsKey(auth.getCurrentUser().getUid())) {

                        imageDTO.starCount = imageDTO.starCount - 1;
                        imageDTO.stars.remove(auth.getCurrentUser().getUid());

                    } else {
                        imageDTO.starCount = imageDTO.starCount + 1;
                        imageDTO.stars.put(auth.getCurrentUser().getUid(), true);
                    }

                    mutableData.setValue(imageDTO);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                    System.out.println("Transaction completed");
                }
            });
        }




    }

    private class CustomViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView1;
        TextView textView2;
        ImageView starButton;


        public CustomViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView)itemView.findViewById(R.id.item_imageView);
            textView1 = (TextView)itemView.findViewById(R.id.item_textView1);
            textView2 = (TextView)itemView.findViewById(R.id.item_textView2);
            starButton = (ImageView)itemView.findViewById(R.id.item_starButton_imageView);


        }
    }




}
