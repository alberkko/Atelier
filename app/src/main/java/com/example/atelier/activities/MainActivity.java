package com.example.atelier.activities;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atelier.R;
import com.example.atelier.adapters.RecyclerViewAdapterMain;
import com.example.atelier.models.Posts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  mAuth = FirebaseAuth.getInstance();
        //     mCurrentUser = mAuth.getCurrentUser();
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
        // mlogout = findViewById(R.id.logout);
        mthreedots = findViewById(R.id.threedots);

        mcomment_btn = findViewById(R.id.comment_icon);
        mbookmark_btn = findViewById(R.id.bookmark_icon);

        getSupportActionBar().hide();

        //GET NAME TO PUT ON THE SLIDE MENU
//        if (mCurrentUser != null) {
//            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
//            DatabaseReference name = ref.child("Users").child(mCurrentUser.getUid()).child("name");
//            name.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    String username = dataSnapshot.getValue(String.class);
//                    NavigationView navigationView = (NavigationView) findViewById(R.id.navmenuffs);
//                    Menu menu = navigationView.getMenu();
//                    MenuItem nav_profile = menu.findItem(R.id.nav_profile);
//                    nav_profile.setTitle(username);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                }
//            });
//
//        } else {
//            NavigationView navigationView = (NavigationView) findViewById(R.id.navmenuffs);
//            Menu menu = navigationView.getMenu();
//            MenuItem nav_profile = menu.findItem(R.id.nav_profile);
//            nav_profile.setTitle("GUEST");
//        }

        //GET DATA FROM FIREBASE
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Posts upload = postSnapshot.getValue(Posts.class);
                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                    Toast.makeText(MainActivity.this, "data gotten, i guess", Toast.LENGTH_SHORT).show();
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        //DISPLAY ON LOGIN ONLY

//        if (mCurrentUser != null) {         //LOGGED IN
//            mlogout.setText("Logout");
//        } else {
//            mlogout.setText("Login");       //LOGGED OUT
//        }

//        mlogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                sendToStart();
//            }
//        });
//

        //OPEN AND CLOSE MENU
//        mthreedots.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DrawerLayout navDrawer = findViewById(R.id.drawer_layout);
//                // If the navigation drawer is not open then open it, if its already open then close it.
//                if (!navDrawer.isDrawerOpen(Gravity.END)) navDrawer.openDrawer(Gravity.END);
//                else navDrawer.closeDrawer(Gravity.START);
//            }
//        });
//


    mthreedots.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

           Intent startIntent = new Intent(MainActivity.this, PostActivity.class);
           startActivity(startIntent);

        }
    });

}


//Profile on Menu Slider
//    public void profile(MenuItem item) {
//        if (mCurrentUser != null) {
//            Intent startIntent = new Intent(MainActivity.this, UserProfileActivity.class);
//            startActivity(startIntent);
//        } else {
//            Intent startIntent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(startIntent);
//            finish();
//        }
//    }

        //Add Building - on Menu Slider
//    public void add(MenuItem item) {
//        if (mCurrentUser != null) {
//            new AlertDialog.Builder(MainActivity.this)
//                    .setTitle("How do you want to insert the location?")
//                    .setPositiveButton("Get location from GPS", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                                buildAlertMessageNoGps();
//                            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                                getLocation();
//
//                                Log.d("up", "lat : " +  lattitude);
//                                Log.d("up", "long : " +  longitude);
//                            }
//                            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
//                            Bundle bundle = new Bundle();
//                            bundle.putDouble("latpos", lattitude);
//                            bundle.putDouble("longpos", longitude);
//                            intent.putExtras(bundle);
//                            startActivity(intent);
//                        }
//                    })
//                    .setNegativeButton("Chose on Map", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            openChoseMapActivity();
//                            Log.d("up", "ChoseMapActivity() successfully opened");
//                        }
//                    })
//                    .setIcon(android.R.drawable.ic_menu_mylocation)
//                    .show();
//        } else
//            Toast.makeText(MainActivity.this, "You have to be Signed In to upload", Toast.LENGTH_SHORT).show();
//    }

        //Map - on Menu Slide
//    public void map(MenuItem item) {
//        openMapActivity();
//    }

        //Criteria - on Menu Slide
//    public void criteria(MenuItem item) {
//        Intent startIntent = new Intent(MainActivity.this, CriteriaActivity.class);
//        startActivity(startIntent);
//    }


        //On RecyclerView Item Click
        @Override
        public void onItemClick ( int position){
           // Posts selectedItem = mUploads.get(position);
           // final String uid = selectedItem.getUserID();
           // String buildingId = selectedItem.getKey();
           // Intent mainIntent = new Intent(MainActivity.this, ProfileActivity2.class);
           // mainIntent.putExtra("b_id", buildingId);
           // mainIntent.putExtra("b_userId", uid);
           // startActivity(mainIntent);
        }

        //Stop listening when activity has ended.
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mDatabaseRef.removeEventListener(mDBListener);
//    }
//
//
//    private void openMapActivity() {
//        Intent intent = new Intent(this, MapsActivity.class);
//        startActivity(intent);
//    }
//
//
//    private void openChoseMapActivity() {
//        Intent intent = new Intent(this, ChoseonMapActivity.class);
//        startActivity(intent);
//
//    }
//
//    private void sendToStart() {
//        Intent startIntent = new Intent(MainActivity.this, LoginActivity.class);
//        startActivity(startIntent);
//        finish();
//    }
}

