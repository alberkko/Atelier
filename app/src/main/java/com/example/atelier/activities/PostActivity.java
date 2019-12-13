package com.example.atelier.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.atelier.R;
import com.example.atelier.adapters.RecyclerViewAdapterComment;
import com.example.atelier.models.Comments;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageTask;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        getSupportActionBar().hide();
        FirebaseApp.initializeApp(this);

        mButtonSend = findViewById(R.id.button_send);
        mEditTextFileName = findViewById(R.id.comment_field);

        mRecyclerView = findViewById(R.id.recycler_view_too);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mcUploads = new ArrayList<>();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Comments");
        mAdapter = new RecyclerViewAdapterComment(PostActivity.this, mcUploads);
        mRecyclerView.setAdapter(mAdapter);

         mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Comments upload = postSnapshot.getValue(Comments.class);
                    mcUploads.add(upload);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PostActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
    }


    private void uploadComment() {
        if (mEditTextFileName == null ) {
            Toast.makeText(this, "Type something", Toast.LENGTH_LONG).show();
        } else {

                    Comments upload;

                    //call the constructor from the Posts class in "models" package
                    upload = new Comments(mEditTextFileName.getText().toString().trim());

                    String uploadId = mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadId).setValue(upload);

                }
            }


}
