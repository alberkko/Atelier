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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.atelier.R;
import com.example.atelier.models.Posts;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AdminUploadActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
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
    private Spinner spinner;
    private String tag;
    private StorageReference mStorageRef2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_upload);

        //hide toolbar
        getSupportActionBar().hide();
        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mStorageRef = FirebaseStorage.getInstance().getReference("Posts");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Posts");
        mButtonUpload = findViewById(R.id.upload_btn);
        mEditTextFileName = findViewById(R.id.name_editText2);
        mImageView = findViewById(R.id.imageView);
        spinner = findViewById(R.id.spinerUpload_1);

        List<String> category = new ArrayList<>();
        category.add("Criteria of categorization");
        category.add("News");
        category.add("Announcements");
        category.add("Clothing pieces");

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, category);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(categoryAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = spinner.getSelectedItem().toString();
                if (!choice.equals("Criteria of categorization")) {
                    tag = choice;
                    Toast.makeText(AdminUploadActivity.this, "u bo kategoria " + choice, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                    Toast.makeText(AdminUploadActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                    openImagesActivity();
                    finish();
                    mButtonUpload.setText("Uploading");
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


    //get image URI
    //images are saved on Firebase Storage as images can't be directly uploaded to the database
    //URI/url is saved on Firebase Database, referencing the image on Firebase Storage
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //function to upload image to Firebase storage and send URL to Database
    private void uploadFile() {
        if (mImageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_LONG).show();
        } else {

            final String path = "Posts/" + System.currentTimeMillis() + "." + getFileExtension(mImageUri);
            StorageReference fileReference = mStorageRef.child(path);

            mUploadTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(AdminUploadActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

                    //get image url from storage
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    Uri downloadUrl = urlTask.getResult();

                    Posts upload;

                    //call the constructor from the Posts class in "models" package
                    upload = new Posts(path, downloadUrl.toString(), mEditTextFileName.getText().toString().trim(), mAuth.getUid(), tag);
                    final String uploadId = mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadId).setValue(upload);

                    mStorageRef2 = FirebaseStorage.getInstance().getReference().child("Posts").child(path);

                    mStorageRef2.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                        @Override
                        public void onSuccess(StorageMetadata storageMetadata) {

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(storageMetadata.getCreationTimeMillis());

                            long mills = storageMetadata.getCreationTimeMillis();


                            Log.e("storage time", ":: " + storageMetadata.getCreationTimeMillis());
                            Log.e("current time", ":: " + System.currentTimeMillis());

                            //   long hours = mills/(1000 * 60 * 60);
                            //   long mins = (mills/(1000*60)) % 60;
                            //   String diff = hours + ":" + mins;

                            int mYear = calendar.get(Calendar.YEAR);
                            int mMonth = calendar.get(Calendar.MONTH);
                            int cMonth = mMonth + 1;
                            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                            int hour = calendar.get(Calendar.HOUR_OF_DAY);
                            int min = calendar.get(Calendar.MINUTE);

                            //UPLOAD time
                            String ts = hour + ":" + min + ", " + mDay + "/" + cMonth + "/" + mYear;
                            mDatabaseRef.child(uploadId).child("ts").setValue(storageMetadata.getCreationTimeMillis());


//                            Posts upload2;
//                            upload2 = new Posts(ts);
//                            final String uploadId = mDatabaseRef.push().getKey();
//                            mDatabaseRef.child(uploadId).setValue(upload2);


                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdminUploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        }
                    });

        }
    }


    private void openImagesActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}


