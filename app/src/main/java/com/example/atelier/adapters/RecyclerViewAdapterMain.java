package com.example.atelier.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.atelier.R;
import com.example.atelier.activities.PostActivity;
import com.example.atelier.models.Favorites;
import com.example.atelier.models.Posts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

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

        final String postId = uploadCurrent.getKey();
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
                Favorites upload;
                upload = new Favorites(mUploads.get(position).getImage_url(), holder.mCurrentUser.getUid() );
                String uploadId = holder.mDatabaseRef.push().getKey();
                holder.mDatabaseRef.child(uploadId).setValue(upload);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolderMain extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView textViewName;
        public ImageView imageView;
        LinearLayout view_container;
        public FirebaseAuth mAuth;
        public FirebaseUser mCurrentUser;
        public DatabaseReference mDatabaseUser;
        public DatabaseReference mDatabaseRef;

        public TextView commentbtn;
        public TextView bookmarkbtn;

        public ImageViewHolderMain(@NonNull View itemView) {
            super(itemView);

            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();
            mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
            commentbtn = itemView.findViewById(R.id.comment_icon);
            bookmarkbtn = itemView.findViewById(R.id.bookmark_icon);
            textViewName = itemView.findViewById(R.id.post_description);
            imageView = itemView.findViewById(R.id.thumbnail);
            view_container = itemView.findViewById(R.id.container);
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Favorites");
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
