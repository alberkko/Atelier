package com.example.atelier.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atelier.R;
import com.example.atelier.adapters.RecyclerViewAdapterMain;
import com.example.atelier.models.Comments;
import com.example.atelier.models.Posts;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapterMain.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapterMain mAdapter;
    private DatabaseReference mDatabaseRef;
    private List<Posts> mUploads;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private FirebaseStorage mStorage;
    private ValueEventListener mDBListener;
    private TextView mlogout;
    private ImageView mthreedots;
    private TextView mcomment_btn;
    private TextView mbookmark_btn;
    private Spinner spinner;
    private String choic;
    private Query mDbQuerry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUploads = new ArrayList<>();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Posts");
        mAdapter = new RecyclerViewAdapterMain(MainActivity.this, mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListenerMain(MainActivity.this);
        mlogout = findViewById(R.id.logout);
        mthreedots = findViewById(R.id.threedots);
        spinner = findViewById(R.id.spinnerr);

        mcomment_btn = findViewById(R.id.comment_icon);
        mbookmark_btn = findViewById(R.id.bookmark_icon);

        getSupportActionBar().hide();


        List<String> category = new ArrayList<>();
        category.add("Show all");
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
                if(!choice.equals("Show all")) {
                    choic = choice;
                  //  Toast.makeText(MainActivity.this,"u bo kategoria" + choice ,Toast.LENGTH_LONG).show();

                    //GET DATA FROM FIREBASE
                    mDbQuerry = mDatabaseRef.orderByChild("category").equalTo(choic);

                    mDbQuerry.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mUploads.clear();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                Posts upload = postSnapshot.getValue(Posts.class);
                                upload.setKey(postSnapshot.getKey());
                                mUploads.add(upload);
                            }
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                else {

                    //GET DATA FROM FIREBASE
                   // mDbQuerry = mDatabaseRef.orderByChild("category").equalTo(choic);

                    mDatabaseRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mUploads.clear();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                Posts upload = postSnapshot.getValue(Posts.class);
                                upload.setKey(postSnapshot.getKey());
                                mUploads.add(upload);
                            }
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        //GET NAME TO PUT ON THE SLIDE MENU
        if (mCurrentUser != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            DatabaseReference name = ref.child("Users").child(mCurrentUser.getUid()).child("name");
            name.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.getValue(String.class);
                    NavigationView navigationView = (NavigationView) findViewById(R.id.navmenuffs);
                    Menu menu = navigationView.getMenu();
                    MenuItem nav_profile = menu.findItem(R.id.nav_profile);
                    nav_profile.setTitle(username);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        } else {
            NavigationView navigationView = (NavigationView) findViewById(R.id.navmenuffs);
            Menu menu = navigationView.getMenu();
            MenuItem nav_profile = menu.findItem(R.id.nav_profile);
            nav_profile.setTitle("GUEST");
        }




        //DISPLAY ON LOGIN ONLY

        if (mCurrentUser != null) {         //LOGGED IN
            mlogout.setText("Logout");
        } else {
            mlogout.setText("Login");       //LOGGED OUT
        }

        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                sendToStart();
            }
        });


        //OPEN AND CLOSE MENU
        mthreedots.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                DrawerLayout navDrawer = findViewById(R.id.drawer_layout);
                // If the navigation drawer is not open then open it, if its already open then close it.
                if (!navDrawer.isDrawerOpen(Gravity.END)) navDrawer.openDrawer(Gravity.END);
                else navDrawer.closeDrawer(Gravity.START);
            }
        });




//        mthreedots.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent startIntent = new Intent(MainActivity.this, AdminUploadActivity.class);
//                startActivity(startIntent);
//                finish();
//            }
//        });


}


//Profile on Menu Slider
    public void profile(MenuItem item) {
        if (mCurrentUser != null) {
            Intent startIntent = new Intent(MainActivity.this, UserProfileActivity.class);
            startActivity(startIntent);
        } else {
            Intent startIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(startIntent);
            finish();
        }
    }

        //Add Post
    public void add(MenuItem item) {
        if(mCurrentUser != null) {
            Intent intent = new Intent(this, AdminUploadActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(MainActivity.this, "Only admin is supposed to upload ???",Toast.LENGTH_SHORT).show();
        }
    }


        //Criteria - on Menu Slide
//    public void criteria(MenuItem item) {
//
//    }


        //On RecyclerView Item Click
        @Override
        public void onItemClick ( int position){
            Posts selectedItem = mUploads.get(position);
            String postId = selectedItem.getKey();
            Intent mainIntent = new Intent(MainActivity.this, PostActivity.class);
            mainIntent.putExtra("p_id", postId);
            startActivity(mainIntent);
        }

        //Stop listening when activity has ended.
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mDatabaseRef.removeEventListener(mDBListener);
//    }

    private void openUploadActivity() {
        Intent intent = new Intent(this, AdminUploadActivity.class);
        startActivity(intent);
    }

//
//
//    private void openChoseMapActivity() {
//        Intent intent = new Intent(this, ChoseonMapActivity.class);
//        startActivity(intent);
//
//    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(startIntent);
        finish();
    }
}

