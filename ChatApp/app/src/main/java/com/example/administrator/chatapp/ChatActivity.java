package com.example.administrator.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView username;
    private EditText sendtext;
    private Button sendbtn;
    private String email;
    FirebaseDatabase database;
    List<Chating> mChating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
        }

        Intent in = getIntent();
        final String ChatId = in.getStringExtra("friendUid");

        username = (TextView)findViewById(R.id.username);
        sendtext = (EditText)findViewById(R.id.sendtext);
        sendbtn = (Button)findViewById(R.id.sendbtn);

        username.setText(email);
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String st = sendtext.getText().toString();
                if(st.equals("") || st.isEmpty()){
                    Toast.makeText(ChatActivity.this, "대화 내용을 입력해 주세요!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ChatActivity.this, st, Toast.LENGTH_SHORT).show();

                    //대화 내용을 보낸 시간 순으로 DB에 저장
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());

                    // 데이터 베이스에 시간 순으로 대화내용을 저장
                    DatabaseReference chatRef = database.getReference("users").child(ChatId).child("chat").child(formattedDate);

                    Hashtable<String, String> userchat
                            = new Hashtable<String, String>();
                    userchat.put("email", email);
                    userchat.put("text", st);
                    chatRef.setValue(userchat);
                    sendtext.setText("");
                }
            }
        });

        Button logoutbtn = (Button)findViewById(R.id.logoutbtn);
        logoutbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mChating = new ArrayList<>();
        mAdapter = new MyAdapter(mChating, email);
        mRecyclerView.setAdapter(mAdapter);

        DatabaseReference myRef = database.getReference("users").child(ChatId).child("chat");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chating chating = dataSnapshot.getValue(Chating.class);
                mChating.add(chating);
                mRecyclerView.scrollToPosition(mChating.size() - 1);
                mAdapter.notifyItemInserted(mChating.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}