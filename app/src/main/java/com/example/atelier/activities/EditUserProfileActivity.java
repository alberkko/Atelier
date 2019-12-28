package com.example.atelier.activities;


import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.atelier.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class EditUserProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

//    private EditText mFirstName;
    private EditText mName;
    private EditText mBio;
    private DatabaseReference mDatabaseUser_name;
//    private DatabaseReference mDatabaseUser_firstname;
    private DatabaseReference mDatabaseUser_bio;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
//    private DatabaseReference dbRef2;
    private DatabaseReference dbRef3;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private Button mSave;
    private String user_id;
    private TextView mChangeImage;
    private ImageView mProfilePhoto;
    private Uri mImageUri;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        getSupportActionBar().hide();

        mChangeImage = findViewById(R.id.changeProfile);
        mProfilePhoto = findViewById(R.id.photo200);
//        mFirstName = findViewById(R.id.username200);
        mBio = findViewById(R.id.bio200);
        mSave = findViewById(R.id.button_save);
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        mName = findViewById(R.id.edit_username);

        mStorageRef = FirebaseStorage.getInstance().getReference("Users");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(user_id).child("img");

        //load image into the ImageView
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String p_image = dataSnapshot.getValue(String.class);

                if (p_image != null && !p_image.isEmpty()) {
                    Picasso.get().load(p_image).into(mProfilePhoto);
                }
                else{
                  //  Picasso.get().load(R.drawable.ic_person_black_24dp).into(mProfilePhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mDatabaseUser_name = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("name");
        mDatabaseUser_name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mName.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        /*mDatabaseUser_firstname = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("firstname");
        mDatabaseUser_firstname.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mFirstName.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/

        mDatabaseUser_bio = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("bio");
        mDatabaseUser_bio.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mBio.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String setName = mName.getText().toString();
                dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("name");
                dbRef.setValue(setName);

                /*String setFirstName = mFirstName.getText().toString();
                dbRef2 = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("firstname");
                dbRef2.setValue(setFirstName);*/

                String setBio = mBio.getText().toString();
                dbRef3 = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("bio");
                dbRef3.setValue(setBio);

                uploadFile();

                Intent startIntent = new Intent(EditUserProfileActivity.this, UserProfileActivity.class);
                startActivity(startIntent);
                finish();
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

            Picasso.get().load(mImageUri).into(mProfilePhoto);
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
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //  mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(EditUserProfileActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();

                            String imag = downloadUrl.toString();
                            mDatabaseRef.setValue(imag);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditUserProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
        }
    }

    @Override
    public void onBackPressed() {
        finish();

    }
}
