package com.example.atelier.adapters;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.atelier.R;
import com.example.atelier.activities.LoginActivity;
import com.example.atelier.activities.PostActivity;
import com.example.atelier.models.Comments;
import com.example.atelier.models.Favorites;
import com.example.atelier.models.Posts;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static java.text.DateFormat.getDateTimeInstance;


public class RecyclerViewAdapterMain extends RecyclerView.Adapter<RecyclerViewAdapterMain.ImageViewHolderMain> {

    private Context mContext;
    private List<Posts> mUploads;
    private OnItemClickListener mListener;

    public RecyclerViewAdapterMain(Context context, List<Posts> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public RecyclerViewAdapterMain.ImageViewHolderMain onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.post_single_item, parent, false);
        return new RecyclerViewAdapterMain.ImageViewHolderMain(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapterMain.ImageViewHolderMain holder, final int position) {
        Posts uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getDescription());
        Picasso.get().load(mUploads.get(position).getImage_url()).fit().centerCrop().into(holder.imageView);


        final String postId = uploadCurrent.getKey();

        holder.mDatabaseRef2 = holder.mDatabaseRef.child(postId);
        holder.mQueryCommentRef = holder.mCommentRef.orderByChild("c_PostID").equalTo(postId);



//        Log.e("work", "here:POSTIDIDIDIDID:" + postId);
//        Log.e("work", "here:Query:" + holder.mCommentRef);
//        Log.e("work", "here:Query:" + holder.mQueryCommentRef);



//        holder.mDatabaseRef3.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

//                    Posts ap = postSnapshot.getValue(Posts.class);
//                    String pathstatus = ap.getPath();
//                    final String idk = ap.getDescription();
//                    final String key = ap.getKey();

                //    holder.mDatabaseRef3.child("timestamp").push().setValue(pathstatus);

//                    Log.e("ppd","::"+idk);

                   // String pp = (String) postSnapshot.child("path").getValue();
                  //  Log.e("www",":: " + holder.storageRef);
                  //  holder.storageRef = FirebaseStorage.getInstance().getReference().child("Posts").child(pathstatus);
//                    holder.storageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
//                        @Override
//                        public void onSuccess(StorageMetadata storageMetadata) {
//
//                            Calendar calendar = Calendar.getInstance();
//                            calendar.setTimeInMillis(storageMetadata.getCreationTimeMillis());
//
//                            int mYear = calendar.get(Calendar.YEAR);
//                            int mMonth = calendar.get(Calendar.MONTH);
//                            int cMonth = mMonth+1;
//                            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
//                            int hour = calendar.get(Calendar.HOUR_OF_DAY);
//                            int min = calendar.get(Calendar.MINUTE);
//
//                           // Log.e("metadata",""+calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH+1)+"/"+calendar.get(Calendar.YEAR)+" :: "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
//
//                            Log.e("ppd","::"+hour+":"+min+"  "+mDay+"/"+cMonth+"::"+idk);
//
//                            holder.mDatabaseRef3.child(key).push().setValue(holder.storageRef);
//
//                            holder.t_counter.setText(hour+":"+min+"  "+mDay+"/"+cMonth);
//                        }
//
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            // Uh-oh, an error occurred!
//                        }
////                    });
//                }
//            }

//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });



        holder.mDBListener2 = holder.mQueryTimeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot psn : dataSnapshot.getChildren()){
                    Posts t_upload = psn.getValue(Posts.class);



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        holder.mDBListener = holder.mQueryCommentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                holder.mcUploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Comments c_upload = postSnapshot.getValue(Comments.class);
                    c_upload.setKey(postSnapshot.getKey());
                    holder.mcUploads.add(c_upload);
                    holder.num = holder.mcUploads.size();
                    String numm = String.valueOf(holder.num);

                    holder.c_counter.setText(numm+" Comments");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



        holder.commentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v ) {
                Intent intent = new Intent(v.getContext(), PostActivity.class);
                intent.putExtra("p_id", postId);
                mContext.startActivity(intent);
            }
        });

        holder.bookmarkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v ) {

                if(holder.mCurrentUser != null) {
                    Favorites upload;
                    upload = new Favorites(mUploads.get(position).getImage_url(), holder.mCurrentUser.getUid(), mUploads.get(position).getKey());
                    String uploadId = holder.mDatabaseRef.push().getKey();
                    holder.mDatabaseRef.child(uploadId).setValue(upload);

                    //change bookmark icon so it looks like it can't be clicked anymore.
                    holder.bookmarkbtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bookmark_transp, 0, 0, 0);
                }
                else{
                    Toast.makeText(mContext,"you are not loggeeeeeed in",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(intent);

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolderMain extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView textViewName;
        public TextView c_counter;
        public TextView t_counter;
        public ImageView imageView;
        LinearLayout view_container;
        public FirebaseAuth mAuth;
        public FirebaseUser mCurrentUser;
    //    public DatabaseReference mDatabaseUser;
        public DatabaseReference mDatabaseRef;
        public DatabaseReference mDatabaseRef2;
        public DatabaseReference mDatabaseRef3;
        public StorageReference storageRef;

        public DatabaseReference ref;
        public DatabaseReference mostafa;

        public ValueEventListener mDBListener;
        public ValueEventListener mDBListener2;

        public DatabaseReference mCommentRef;
        public Query mQueryCommentRef;
        public Query mQueryTimeRef;

        public List<Comments> mcUploads;
        public List<Posts> mpUploads;

        public int num;
        public TextView commentbtn;
        public TextView bookmarkbtn;

        public ImageViewHolderMain(@NonNull View itemView) {
            super(itemView);

            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();
         //   mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
            commentbtn = itemView.findViewById(R.id.comment_icon);
            bookmarkbtn = itemView.findViewById(R.id.bookmark_icon);
            textViewName = itemView.findViewById(R.id.post_description);
            imageView = itemView.findViewById(R.id.thumbnail);
            view_container = itemView.findViewById(R.id.container);
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Favorites");
            mDatabaseRef3 = FirebaseDatabase.getInstance().getReference("Posts");
            c_counter = itemView.findViewById(R.id.textView4);
            t_counter = itemView.findViewById(R.id.textView3);
            mcUploads = new ArrayList<>();
            mpUploads = new ArrayList<>();

          //  StorageReference forestRef = storageRef.child("Posts");


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

            mCommentRef = FirebaseDatabase.getInstance().getReference().child("Comments");


            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }
    }

    //on Click Interface
    public interface OnItemClickListener {
        void onItemClick(int position);

    }

    public void setOnItemClickListenerMain(OnItemClickListener listener) {
        mListener = listener;
    }

}
