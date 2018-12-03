package com.fetch.ducmanh.socialnetwork.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fetch.ducmanh.socialnetwork.EditProfileActivity;
import com.fetch.ducmanh.socialnetwork.FollowersActivity;
import com.fetch.ducmanh.socialnetwork.OptionsActivity;
import com.fetch.ducmanh.socialnetwork.R;
import com.fetch.ducmanh.socialnetwork.adapter.MyFotoAdapter;
import com.fetch.ducmanh.socialnetwork.model.Post;
import com.fetch.ducmanh.socialnetwork.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    ImageView image_profile,options;
    TextView posts,following,followers,fullname,bio,username;
    Button edit_profile;

    FirebaseUser firebaseUser;

    String profileid;

    ImageButton my_fotos,saved_fotos;
    //save
    private List<String> mySaves;
    RecyclerView recyclerView_saves;
    MyFotoAdapter myFotoAdapter_saves;
    List<Post> postList_saves;


    //
    RecyclerView recyclerView;
    MyFotoAdapter myFotoAdapter;
    List<Post> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = preferences.getString("profileid","none");

        initviews(view);
        userInfo();
        getFollowers();
        getNrPosts();
        myFotos();
        mysaves();


        if (profileid.equals(firebaseUser.getUid())){
            edit_profile.setText("Sửa thông tin của bạn");
        }else {
            checkFollow();
            saved_fotos.setVisibility(View.GONE);
        }

        return view;
    }


    private void initviews(View view) {

        image_profile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        posts = view.findViewById(R.id.posts);
        following = view.findViewById(R.id.following);
        followers = view.findViewById(R.id.followers);
        username = view.findViewById(R.id.username);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        my_fotos = view.findViewById(R.id.my_fotos);
        saved_fotos = view.findViewById(R.id.saved_fotos);
        edit_profile = view.findViewById(R.id.edit_profile);
        ///
        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager  = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(layoutManager);
        postList = new ArrayList<>();
        myFotoAdapter = new MyFotoAdapter(getContext(),postList);
        recyclerView.setAdapter(myFotoAdapter);

        //save
        recyclerView_saves=view.findViewById(R.id.recycler_view_save);
        recyclerView_saves.setHasFixedSize(true);
        LinearLayoutManager layoutManager_saves  = new GridLayoutManager(getContext(),3);
        recyclerView_saves.setLayoutManager(layoutManager_saves);
        postList_saves = new ArrayList<>();
        myFotoAdapter_saves = new MyFotoAdapter(getContext(),postList_saves);
        recyclerView_saves.setAdapter(myFotoAdapter_saves);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);

        edit_profile.setOnClickListener(this);
        my_fotos.setOnClickListener(this);
        saved_fotos.setOnClickListener(this);
        followers.setOnClickListener(this);
        following.setOnClickListener(this);
        options.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_profile:
                String btn = edit_profile.getText().toString();

                if (btn.equals("Sửa thông tin của bạn")){
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }else if (btn.equals("Theo dõi")){
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid())
                            .child("Đang theo dõi")
                            .child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(profileid)
                            .child("Người theo dõi")
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotifications();
                }else if(btn.equals("Đang theo dõi")){
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid())
                            .child("Đang theo dõi")
                            .child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(profileid)
                            .child("Người theo dõi")
                            .child(firebaseUser.getUid()).removeValue();
                }
                break;
            case R.id.my_fotos:
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE);
                break;
            case R.id.saved_fotos:
                recyclerView.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE);
                break;
            case R.id.followers:
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileid);
                intent.putExtra("title","Người theo dõi");
                startActivity(intent);
                break;
            case R.id.following:
                Intent intentFollowing = new Intent(getContext(), FollowersActivity.class);
                intentFollowing.putExtra("id",profileid);
                intentFollowing.putExtra("title","Đang theo dõi");
                startActivity(intentFollowing);
                break;
            case R.id.options:
                Intent intentOptions = new Intent(getContext(), OptionsActivity.class);
                startActivity(intentOptions);
                break;
        }
    }


    private void addNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","bắt đầu theo dõi bạn");
        hashMap.put("postid","");
        hashMap.put("ispost",false);

        reference.push().setValue(hashMap);
    }

    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null){
                    return;
                }

                User user = dataSnapshot.getValue(User.class);

                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                bio.setText(user.getBio());
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(firebaseUser.getUid()).child("Đang theo dõi");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileid).exists()){
                    edit_profile.setText(R.string.dang_theo_doi);
                }else {
                    edit_profile.setText(R.string.theodoi);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("Người theo dõi");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("Đang theo dõi");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getNrPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        i++;
                    }

                }

                posts.setText(""+i);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void myFotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }

                Collections.reverse(postList);
                myFotoAdapter.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void mysaves(){
        mySaves = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mySaves.add(snapshot.getKey());
                }

                readSaves();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void readSaves() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList_saves.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    for (String id : mySaves){
                        if (post.getPostid().equals(id)){
                            postList_saves.add(post);
                        }
                    }
                }

                myFotoAdapter_saves.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
