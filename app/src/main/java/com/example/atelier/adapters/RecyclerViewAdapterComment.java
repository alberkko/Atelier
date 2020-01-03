package com.example.atelier.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atelier.R;
import com.example.atelier.activities.LoginActivity;
import com.example.atelier.models.Comments;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapterComment extends RecyclerView.Adapter<RecyclerViewAdapterComment.ImageViewHolderComment> {

    private Context mContext;
    private List<Comments> mcUploads;

    public RecyclerViewAdapterComment(Context context, List<Comments> uploads) {
        mContext = context;
        mcUploads = uploads;
    }

    @Override
    public ImageViewHolderComment onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.comment_single_item, parent, false);
        return new ImageViewHolderComment(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolderComment holder, int position) {
        Comments uploadCurrent = mcUploads.get(position);
        holder.commentText.setText(uploadCurrent.getComment());
//        holder.commentName.setText(uploadCurrent.getC_UserID());
//
//        get image from profile of user
//

        //  Log.e("rcc","::position::"+mcUploads.get(position));
        //   Log.e("rcc","::comment::"+uploadCurrent.getComment());
        //   Log.e("rcc","::profile photo::"+uploadCurrent.getProfile_p_url());
        //   Log.e("rcc","::userid::"+uploadCurrent.getC_UserID());


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference name = ref.child("Users").child(uploadCurrent.getC_UserID());
        name.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String username = dataSnapshot.child("name").getValue(String.class);
                holder.commentName.setText(username);

                String userphoto = dataSnapshot.child("img").getValue(String.class);
                Picasso.get().load(userphoto).fit().centerCrop().into(holder.commentPhoto);
                //    Log.e("rcc", "::userid::" + username);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        DatabaseReference profile_photo = ref.child("Users").child(uploadCurrent.getC_UserID()).child("PROFILEPHOTO");
//        name.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String username = dataSnapshot.getValue(String.class);
//                holder.commentName.setText(username);
//
//                Log.e("rcc","::userid::"+username);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mcUploads.size();
    }

    public class ImageViewHolderComment extends RecyclerView.ViewHolder {

        public TextView commentText;
        public ImageView commentPhoto;
        public TextView commentName;
        public FirebaseAuth mAuth;
        public FirebaseUser mCurrentUser;

        public ImageViewHolderComment(@NonNull View itemView) {
            super(itemView);

            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();

            commentText = itemView.findViewById(R.id.comment_content);
            commentPhoto = itemView.findViewById(R.id.comment_photo);
            commentName = itemView.findViewById(R.id.comment_username);

        }

    }
}