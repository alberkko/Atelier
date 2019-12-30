package com.example.atelier.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.atelier.activities.EditPostActivity;
import com.example.atelier.activities.LoginActivity;
import com.example.atelier.activities.PostActivity;
import com.example.atelier.models.Comments;
import com.example.atelier.models.Favorites;
import com.example.atelier.models.Posts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

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

        if(uploadCurrent.getCategory() != null){
            holder.tag.setText("#" + uploadCurrent.getCategory());
        }else{
            holder.tag.setVisibility(View.GONE);
        }

        long time2 = uploadCurrent.getTs();
        long mills2 = time2 - System.currentTimeMillis();
        long hours2 = mills2/(1000 * 60 * 60);
        long mins2 = (mills2/(1000*60)) % 60;

        hours2 = Math.abs(hours2);

        if(hours2 >= 1){
            String diff = hours2 + " hours ago";
            holder.t_counter.setText(diff);
        }

        else {
            mins2 = Math.abs(mins2);
            String diff = mins2 + " minutes ago";
            holder.t_counter.setText(diff);
        }

        final String postId = uploadCurrent.getKey();

        holder.mDatabaseRef2 = holder.mDatabaseRef.child(postId);
        holder.mQueryCommentRef = holder.mCommentRef.orderByChild("c_PostID").equalTo(postId);
        holder.mQueryTimeRef = holder.mTimeRef;



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
                    holder.c_counter.setText(numm +" Comments");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.editpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v ) {
                Intent intent = new Intent(v.getContext(), PostActivity.class);
                intent.putExtra("p_id", postId);
                mContext.startActivity(intent);
            }
        });

        holder.commentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v ) {
                Intent intent = new Intent(v.getContext(), EditPostActivity.class);
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
        public DatabaseReference mDatabaseRef;
        public DatabaseReference mDatabaseRef2;
        public DatabaseReference mDatabaseRef3;
        public ValueEventListener mDBListener;
        public ValueEventListener mDBListener2;

        public DatabaseReference mCommentRef;
        public DatabaseReference mTimeRef;
        public Query mQueryCommentRef;
        public Query mQueryTimeRef;

        public List<Comments> mcUploads;
        public List<Posts> mpUploads;

        public int num;
        public TextView commentbtn;
        public TextView bookmarkbtn;
        public TextView editpen;
        public String adminemail;
        public TextView tag;

        public ImageViewHolderMain(@NonNull View itemView) {
            super(itemView);

            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();
         //   mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
            commentbtn = itemView.findViewById(R.id.comment_icon);
            tag = itemView.findViewById(R.id.tag_inpost);
            bookmarkbtn = itemView.findViewById(R.id.bookmark_icon);
            textViewName = itemView.findViewById(R.id.post_description);
            imageView = itemView.findViewById(R.id.thumbnail);
            editpen = itemView.findViewById(R.id.edit_icon);
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
            mTimeRef = FirebaseDatabase.getInstance().getReference().child("Posts");
            if(mCurrentUser != null) {
                adminemail = mCurrentUser.getEmail();
            }
            else {
                //nothing
            }

            if (mCurrentUser != null) {

                if(adminemail.equals("admin@email.com")){
                    commentbtn.setVisibility(View.VISIBLE);
                }
                else {
                    commentbtn.setVisibility(View.GONE);
                }
            }
            else if (mCurrentUser == null) {
                commentbtn.setVisibility(View.GONE);
            }

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
