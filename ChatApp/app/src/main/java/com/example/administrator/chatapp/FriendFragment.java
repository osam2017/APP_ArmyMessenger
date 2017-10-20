package com.example.administrator.chatapp;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment {

    String TAG = getClass().getSimpleName();
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;

    List<Friend> mFriend;
    FriendAdapter mfriendadapter;
    FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_friend, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.friendList);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mFriend = new ArrayList<>();
        mfriendadapter = new FriendAdapter(mFriend, getActivity());
        mRecyclerView.setAdapter(mfriendadapter);

        database = FirebaseDatabase.getInstance();
        DatabaseReference friendRef = database.getReference("users");

        // 데이터 베이스로 부터 데이터를 읽음
        friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
                Log.d(TAG, "Value is: " + value);

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    String value1 = dataSnapshot1.getValue().toString();
                    Log.d(TAG, "Value is: " + value1);
                    Friend friend = dataSnapshot1.getValue(Friend.class);
                    mFriend.add(friend);
                    mfriendadapter.notifyItemInserted(mFriend.size()-1);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        return v;
    }
}
