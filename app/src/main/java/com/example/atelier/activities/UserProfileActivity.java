package com.example.atelier.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atelier.R;
import com.example.atelier.adapters.RecyclerViewAdapter;
import com.example.atelier.models.Comments;
import com.example.atelier.models.Favorites;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private DatabaseReference mDatabaseRef;
    private List<Favorites> mUploads;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseStorage mStorage;
    private ValueEventListener mDBListener;
    private DatabaseReference mDatabaseCurrentUser;
    private Query mQueryCurrentUser;
    private ImageView mImgView;
    private int num;
    private TextView mBCount;
    private TextView mUsername;
    private ImageView mProfilePhoto;
    private TextView mBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().hide();

        mBio = findViewById(R.id.bio33);
        mProfilePhoto = findViewById(R.id.prof_photo);
        mRecyclerView = findViewById(R.id.recycler_view_profile);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mUploads = new ArrayList<>();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Favorites");
        mAdapter = new RecyclerViewAdapter(UserProfileActivity.this, mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(UserProfileActivity.this);
        mUsername = findViewById(R.id.username_aa);
        mBCount = findViewById(R.id.b_count);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        String CurrentUserId = mAuth.getCurrentUser().getUid();
        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Favorites");
        mQueryCurrentUser = mDatabaseCurrentUser.orderByChild("user_id").equalTo(CurrentUserId);

        Log.e("work2", "here:db:" + mDatabaseCurrentUser);
        Log.e("work2", "here:qq:" + mQueryCurrentUser);


        //DISPLAY NUMBER OF FAVORITED POSTS ON PROFILE
        mDBListener = mQueryCurrentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Favorites upload = postSnapshot.getValue(Favorites.class);
                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                    num = mUploads.size();
                    String numm = String.valueOf(num);
                    mBCount.setText(numm);

                    Log.e("work2", "here:qqMCMCMC:" + mUploads);
                    Log.e("whatisthisfor", "now here here::" + numm);
                }

                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference name = ref.child("Users").child(mCurrentUser.getUid());
        name.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String username = dataSnapshot.child("name").getValue(String.class);
                    mUsername.setText(username);
                    String bio = dataSnapshot.child("bio").getValue(String.class);
                    mBio.setText(bio);

                    String profile_photo = dataSnapshot.child("img").getValue(String.class);
                    if (profile_photo != null && !profile_photo.isEmpty()) {
                        Picasso.get().load(profile_photo).into(mProfilePhoto);
                    } else {
                        //do not retrieve photo
                        //Picasso.get().load(R.drawable.ic_person_black_24dp).into(mProfilePhoto);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mImgView = findViewById(R.id.editpenn);
        mImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EditUserProfileActivity.class));
            }
        });
    }


    @Override
    public void onItemClick(int position) {
        Favorites selectedItem = mUploads.get(position);
        String postId = selectedItem.getmPost_id();
        //Toast.makeText(UserProfileActivity.this, "::"+postId, Toast.LENGTH_SHORT).show();
        Intent mainIntent = new Intent(UserProfileActivity.this, PostActivity.class);
        mainIntent.putExtra("p_id", postId);
        startActivity(mainIntent);
    }

    @Override
    public void onDeleteClick(int position) {

        //DELETE ONLY DELETED IMAGES FROM STORAGE
        Favorites selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        mDatabaseRef.child(selectedKey).removeValue();
        Toast.makeText(UserProfileActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent startIntent = new Intent(UserProfileActivity.this, MainActivity.class);
        startActivity(startIntent);
        finish();
    }
}
