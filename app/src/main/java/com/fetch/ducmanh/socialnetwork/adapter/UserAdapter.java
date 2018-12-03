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
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fetch.ducmanh.socialnetwork.MainActivity;
import com.fetch.ducmanh.socialnetwork.R;
import com.fetch.ducmanh.socialnetwork.fragment.ProfileFragment;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHoder> {

    private Context mContext;
    private List<User> mUsers;

    private FirebaseUser firebaseUser;

    private boolean isFragment;

    public UserAdapter(Context mContext, List<User> mUsers,boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }



    @NonNull
    @Override
    public ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHoder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHoder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);

        holder.btn_follow.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getFullname());

        Glide.with(mContext).load(user.getImageurl()).into(holder.image_profile);

        isFollowing(user.getId(),holder.btn_follow);
        if (user.getId().equals(firebaseUser.getUid())){
            holder.btn_follow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFragment){
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("profileid",user.getId());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,new ProfileFragment()).commit();
                }else {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherid",user.getId());
                    mContext.startActivity(intent);
                }

            }
        });
        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btn_follow.getText().toString().equals("Theo dõi")){

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid())
                            .child("Đang theo dõi")
                            .child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getId())
                            .child("Người theo dõi")
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotifications(user.getId());
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid())
                            .child("Đang theo dõi")
                            .child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getId())
                            .child("Người theo dõi")
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return mUsers.size();
    }



    public class ViewHoder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView fullname;
        public CircleImageView image_profile;
        public Button btn_follow;
        public ViewHoder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_follow = itemView.findViewById(R.id.btn_follow);
        }
    }

    private void isFollowing(final String userid, final Button button){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("Đang theo dõi");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()){
                    button.setText(R.string.dang_theo_doi);
                }else {
                    button.setText(R.string.theodoi);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void addNotifications(String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","Bắt đầu theo dõi bạn ");
        hashMap.put("postid","");
        hashMap.put("ispost",false);
        reference.push().setValue(hashMap);
    }
}