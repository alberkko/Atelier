package com.example.atelier.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.atelier.R;
import com.example.atelier.adapters.RecyclerViewAdapterComment;
import com.example.atelier.models.Comments;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapterComment mAdapter;
    private DatabaseReference mDatabaseRef;
    private List<Comments> mcUploads;
    private FirebaseStorage mStorage;
    private ValueEventListener mDBListener;
    private StorageTask mUploadTask;
    private TextView mButtonSend;
    private EditText mEditTextFileName;
    private TextView mPostDescription;
    private ImageView mPostImage;
    private TextView mUsername;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;
    private String p_id;
    private String Comment_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        getSupportActionBar().hide();
        FirebaseApp.initializeApp(this);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        p_id = getIntent().getExtras().get("p_id").toString();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
       // mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mButtonSend = findViewById(R.id.button_send);
        mEditTextFileName = findViewById(R.id.comment_field);
        mRecyclerView = findViewById(R.id.recycler_view_too);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mcUploads = new ArrayList<>();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Comments");
        mAdapter = new RecyclerViewAdapterComment(PostActivity.this, mcUploads);
        mRecyclerView.setAdapter(mAdapter);
        mPostDescription = findViewById(R.id.post_desc);
        mPostImage = findViewById(R.id.thumbnail);
        mUsername = findViewById(R.id.username_aa2);
        mUsername.setText("Tier");

        //get comments from DB
         mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mcUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Comments upload = postSnapshot.getValue(Comments.class);
                    upload.setKey(postSnapshot.getKey());
                    mcUploads.add(upload);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PostActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        //GET POST IMAGE, DESCRIPTION FROM DATABASE
        DatabaseReference mainRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference buildingRef = mainRef.child("Posts").child(p_id);
        buildingRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String building_description = dataSnapshot.child("description").getValue(String.class);
                    String building_img = dataSnapshot.child("image_url").getValue(String.class);
                  //  Toast.makeText(PostActivity.this, "post:::" + building_img, Toast.LENGTH_SHORT).show();
                    mPostDescription.setText(building_description);
                    Picasso.get().load(building_img).into(mPostImage);

                    Log.e("rcc","::userid::"+building_description);
                    Log.e("rcc","::userid::"+building_img);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(PostActivity.this, "Comment is being sent", Toast.LENGTH_SHORT).show();
                } else {
                    uploadComment();
                }
            }
        });


        //show comments
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mcUploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Comments upload = postSnapshot.getValue(Comments.class);
                    upload.setKey(postSnapshot.getKey());

                    Comment_id = upload.getC_PostID();

                    if (Comment_id.equals(p_id)) {
                        mcUploads.add(upload);
                    } else {
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PostActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void uploadComment() {
        String sUsername = mEditTextFileName.getText().toString();
        if(mCurrentUser ==null) {
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_LONG).show();
        }
        else if (sUsername.matches("")) {
            Toast.makeText(this, "Type something", Toast.LENGTH_LONG).show();

        }
        else {
                    mcUploads.clear();
                    Comments upload;
                    //call the constructor from the Posts class in "models" package
                    upload = new Comments(mEditTextFileName.getText().toString().trim(), mAuth.getUid(),p_id);
                    String uploadId = mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadId).setValue(upload);
                    mEditTextFileName.setText("");
        }
    }
}
