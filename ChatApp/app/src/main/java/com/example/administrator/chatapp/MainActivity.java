package com.example.administrator.chatapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    private EditText userID;
    private EditText userPW;
    private String id;
    private String pw;

    String TAG = "main";
    DatabaseReference userRef;
    FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userID = (EditText)findViewById(R.id.userID);
        userPW = (EditText)findViewById(R.id.userPW);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users");

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("uid", user.getUid());
                    editor.putString("email", user.getEmail());
                    editor.apply();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        Button registerbtn = (Button)findViewById(R.id.registerbtn);
        Button loginbtn = (Button)findViewById(R.id.loginbtn);
        Button cancelbtn = (Button)findViewById(R.id.cancelbtn);

        registerbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                id = userID.getText().toString();
                pw = userPW.getText().toString();
                if(id.isEmpty() || id.equals("") || pw.isEmpty() || pw.equals("")){
                    Toast.makeText(MainActivity.this, "ID와 PW를 올바르게 입력해 주세요", Toast.LENGTH_SHORT).show();
                }else {
                    registerUser(id, pw);
                }
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                id = userID.getText().toString();
                pw = userPW.getText().toString();
                if(id.isEmpty() || id.equals("") || pw.isEmpty() || pw.equals("")){
                    Toast.makeText(MainActivity.this, "ID와 PW를 올바르게 입력해 주세요", Toast.LENGTH_SHORT).show();
                }else {
                    loginUser(id, pw);
                }
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void registerUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if(task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "register sucsses", Toast.LENGTH_SHORT).show();
                        }
                        if(!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "register fail",
                                    Toast.LENGTH_SHORT).show();
                        }
                        if(user != null){
                            Hashtable<String, String> userprofile = new Hashtable<String, String>();
                            userprofile.put("email", user.getEmail());
                            userprofile.put("photo", "");
                            userprofile.put("key", user.getUid());
                            userRef.child(user.getUid()).setValue(userprofile);
                        }
                    }
                });
    }

    public void loginUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if(task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "sucsess", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                            startActivity(intent);
                        }

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, "Login fail",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
