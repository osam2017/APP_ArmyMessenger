package com.example.administrator.chatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;

public class ProfileFragment extends Fragment {

    ImageView userImage;
    private StorageReference mStorageRef;
    Bitmap bmp;
    String userUid;
    String userEmail;
    String TAG = getClass().getSimpleName();

    String age;
    String name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        userUid = sharedPreferences.getString("uid", "");
        userEmail = sharedPreferences.getString("email", "");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference proRef = database.getReference();
        proRef.child("users").child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 데이터베이스에 저장되어 있는 사진을 불러옴
                String value = dataSnapshot.getValue().toString();
                String userPhoto = dataSnapshot.child("photo").getValue().toString();

                if(TextUtils.isEmpty(userPhoto)){

                }else{
                    Picasso.with(getActivity()).load(userPhoto).fit().centerInside().into(userImage, new Callback.EmptyCallback() {
                        @Override public void onSuccess() {
                            Log.d(TAG, "SUCESS");
                        }
                    });
                }
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // 갤러리에 들어갈 수 있도록 허가권을 부여하는 코드
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

        //이미지가 눌러지면 발생하는 이벤트
        userImage =(ImageView)v.findViewById(R.id.userImage);
        userImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
            }
        });

        //Store 버튼이 눌러지면 발생하는 이벤트
        Button storeBtn = (Button)v.findViewById(R.id.proStore);
        storeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                infoStore();
            }
        });

        //Logout 버튼이 눌러지면 발생하는 이벤트
        Button logoutbtn = (Button)v.findViewById(R.id.proLogout);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
            }
        });

        return v;
    }

    //프로필 이미지를 firebase에 업로드하는 코드
    public void imageUpload(){
        StorageReference riversRef = mStorageRef.child("users").child(userUid+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = riversRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                String photoUrl = String.valueOf(downloadUrl);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference userRef = database.getReference("users");

                Hashtable<String, String> userprofile = new Hashtable<String, String>();
                userprofile.put("email", userEmail);
                userprofile.put("key", userUid);
                userprofile.put("photo", photoUrl);


                userRef.child(userUid).setValue(userprofile);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String s = dataSnapshot.getValue().toString();
                        if(dataSnapshot != null){
                            Toast.makeText(getActivity(), "업로드 완료", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public void infoStore(){

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri img = data.getData();
        try {
            bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), img);
                userImage.setImageBitmap(bmp);
                imageUpload();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
    }
}
