package com.example.asus.movieapp1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus.movieapp1.R;
import com.example.asus.movieapp1.model.Review;

import java.util.List;

/**
 * Created by asus on 14-Dec-17.
 */

public class ReviewAdapter  extends  RecyclerView.Adapter<ReviewAdapter.MyViewHolder>{

    private Context mContext;
    private List<Review> reviewList;

    public ReviewAdapter (Context mContext, List<Review> reviewList ){

        this.mContext=mContext;
        this.reviewList=reviewList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.review_card,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        viewHolder.author.setText(reviewList.get(i).getAuthor());
        viewHolder.content.setText(reviewList.get(i).getContent());

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView author,content;

        public MyViewHolder(View view) {
            super(view);
            author= view.findViewById(R.id.author);
            content= view.findViewById(R.id.content);


        }
    }
}
