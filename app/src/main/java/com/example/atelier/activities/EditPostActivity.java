package com.example.atelier.activities;


import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.atelier.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EditPostActivity extends AppCompatActivity {
    private TextView mButtonUpload;
    private EditText mEditTextFileName;
    private ImageView mImageView;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;
    private static final int PICK_IMAGE_REQUEST = 1;
    private DatabaseReference mDatabaseUser_name;
    private DatabaseReference mDatabaseUser_tag;
    private DatabaseReference mDatabaseUser_category;
    private DatabaseReference mDatabaseUser_img;
    private String p_id;
    private Spinner spinner;
    private String tag;
    private String db_tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        getSupportActionBar().hide();

        FirebaseApp.initializeApp(this);
        mButtonUpload = findViewById(R.id.upload_btn_00);
        mEditTextFileName = findViewById(R.id.name_editText2_00);
        mImageView = findViewById(R.id.imageView_00);
        mStorageRef = FirebaseStorage.getInstance().getReference("Posts");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Posts/Posts");
        spinner = findViewById(R.id.spinnerUpload);

        p_id = getIntent().getExtras().get("p_id").toString();
        mDatabaseUser_tag = FirebaseDatabase.getInstance().getReference().child("Posts").child(p_id).child("category");
        mDatabaseUser_tag.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                db_tag = dataSnapshot.getValue(String.class);

                //SPINNER
                List<String> category = new ArrayList<>();

                if (db_tag != null) {
                    category.add(db_tag);
                    category.add("Show all");
                    category.add("News");
                    category.add("Announcements");
                    category.add("Clothing pieces");

                    if (db_tag.equals("News")) {
                        category.remove(2);
                    } else if (db_tag.equals("Announcements")) {
                        category.remove(3);
                    } else if (db_tag.equals("Clothing pieces")) {
                        category.remove(4);
                    } else if (db_tag.equals("Show all")) {
                        category.remove(1);
                    }
                } else {
                    db_tag = "Show all";
                    category.add(db_tag);
                    category.add("News");
                    category.add("Announcements");
                    category.add("Clothing pieces");
                }

                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(EditPostActivity.this, android.R.layout.simple_spinner_item, category);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(categoryAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String choice = spinner.getSelectedItem().toString();
                        if (!choice.equals(db_tag)) {
                            tag = choice;
                            //Toast.makeText(EditPostActivity.this, "u bo kategoria " + choice, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        mDatabaseUser_name = FirebaseDatabase.getInstance().getReference().child("Posts").child(p_id).child("description");
        mDatabaseUser_img = FirebaseDatabase.getInstance().getReference().child("Posts").child(p_id).child("image_url");


        //GET ALL TOGETHER
        mDatabaseUser_name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEditTextFileName.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mDatabaseUser_img.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                Picasso.get().load(url).into(mImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(EditPostActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {

                    String setName = mEditTextFileName.getText().toString();
                    mDatabaseUser_name.setValue(setName);

                    mDatabaseUser_tag.setValue(tag);

                    uploadFile();
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(mImageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(EditPostActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();

                            String imgString = downloadUrl.toString();
                            mDatabaseUser_img.setValue(imgString);

                            Intent startIntent = new Intent(EditPostActivity.this, MainActivity.class);
                            startActivity(startIntent);
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Intent startIntent = new Intent(EditPostActivity.this, MainActivity.class);
            startActivity(startIntent);
            finish();
        }
    }

}
