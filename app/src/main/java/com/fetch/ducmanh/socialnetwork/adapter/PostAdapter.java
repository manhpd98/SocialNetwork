package com.fetch.ducmanh.socialnetwork.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fetch.ducmanh.socialnetwork.CommentsActivity;
import com.fetch.ducmanh.socialnetwork.FollowersActivity;
import com.fetch.ducmanh.socialnetwork.R;
import com.fetch.ducmanh.socialnetwork.fragment.PostDetailFragment;
import com.fetch.ducmanh.socialnetwork.fragment.ProfileFragment;
import com.fetch.ducmanh.socialnetwork.model.Post;
import com.fetch.ducmanh.socialnetwork.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHoder>{

    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }



    @NonNull
    @Override
    public ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);
        return new PostAdapter.ViewHoder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHoder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPost.get(position);

        Glide.with(mContext).load(post.getPostimage())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.post_image);

        if (post.getDescription().equals("")){
            holder.description.setVisibility(View.GONE);
        }else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }

        publisherInfo(holder.image_profile,holder.username,holder.publisher,post.getPublisher());

        isLiked(post.getPostid(),holder.like);
        nrLike(holder.likes,post.getPostid());
        getComment(post.getPostid(),holder.comments);
        isSaved(post.getPostid(),holder.save);

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        holder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postid",post.getPostid());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PostDetailFragment()).commit();
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).removeValue();
                }
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostid())
                            .child(firebaseUser.getUid())
                            .setValue(true);
                    addNotifications(post.getPublisher(),post.getPostid());
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostid())
                            .child(firebaseUser.getUid())
                            .removeValue();
                }
            }
        });


        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("publisherid",post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("publisherid",post.getPublisher());
                mContext.startActivity(intent);
            }
        });


        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id",post.getPostid());
                intent.putExtra("title","likes");
                mContext.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return mPost.size();
    }



    public class ViewHoder extends RecyclerView.ViewHolder{

        public ImageView image_profile,post_image,like,comment,save;
        private TextView username,likes,description,publisher,comments;


        public ViewHoder(View itemView) {
            super(itemView);


            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);

            username = itemView.findViewById(R.id.username);
            likes = itemView.findViewById(R.id.likes);
            description = itemView.findViewById(R.id.description);
            publisher = itemView.findViewById(R.id.publisher);
            comments = itemView.findViewById(R.id.comments);
        }
    }

    private void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, String userid){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);

                username.setText(user.getUsername());
                publisher.setText(user.getUsername());

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void isLiked(String postid, final ImageView imageView){

        final FirebaseUser  firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                }else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nrLike(final TextView likes, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+" lượt thích");
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getComment(String postid, final TextView comments){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText("Có " +dataSnapshot.getChildrenCount() + " bình luận");
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isSaved(final String postid, final ImageView imageView){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_save_black);
                    imageView.setTag("saved");
                }else {
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("save");
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void addNotifications(String userid,String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","thích bài viết của bạn");
        hashMap.put("postid",postid);
        hashMap.put("ispost",true);

        reference.push().setValue(hashMap);
    }
}
