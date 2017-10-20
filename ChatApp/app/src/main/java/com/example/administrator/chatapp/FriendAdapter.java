package com.example.administrator.chatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    List<Friend> mFriend;
    String userEmail;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // RecyclerView에 들어가는 객체들
        public TextView tvUserEmail;
        public ImageView imgUser;
        public Button btnChat;

        public ViewHolder(View itemView) {
            super(itemView);
            tvUserEmail = (TextView)itemView.findViewById(R.id.tvUserEmail);
            imgUser=(ImageView)itemView.findViewById(R.id.imgUser);
            btnChat=(Button)itemView.findViewById(R.id.btnChat);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FriendAdapter(List<Friend> mFriend, Context context) {
        this.mFriend = mFriend;
        this.context = context;
    }

    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friend_list, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvUserEmail.setText(mFriend.get(position).getEmail());

        //유저의 사진이 있으면 있는 사진으로 설정, 없으면 대체 사진으로 설정
        String userPhoto = mFriend.get(position).getPhoto();
        if(TextUtils.isEmpty(userPhoto)){
            Picasso.with(context).load(R.mipmap.ic_noimage).fit().centerInside().into(holder.imgUser);
        }else{
            Picasso.with(context).load(userPhoto).fit().centerInside().into(holder.imgUser);
        }

        holder.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FriendID = mFriend.get(position).getKey();
                Intent in = new Intent(context, ChatActivity.class);
                in.putExtra("friendUid", FriendID);
                context.startActivity(in);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFriend.size();
    }
}