package com.fetch.ducmanh.socialnetwork.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fetch.ducmanh.socialnetwork.R;
import com.fetch.ducmanh.socialnetwork.model.Post;

import java.util.List;

public class MyFotoAdapter extends RecyclerView.Adapter<MyFotoAdapter.ViewHoder>{

    private Context mContext;
    private List<Post> mPosts;



    public MyFotoAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }



    @NonNull
    @Override
    public ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fotos_item,parent,false);
        return new MyFotoAdapter.ViewHoder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHoder holder, int position) {

        Post post = mPosts.get(position);

        Glide.with(mContext).load(post.getPostimage()).into(holder.post_image);
    }



    @Override
    public int getItemCount() {
        return mPosts.size();
    }



    public class ViewHoder extends RecyclerView.ViewHolder{
        public ImageView post_image;
        public ViewHoder(View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);
        }
    }
}
