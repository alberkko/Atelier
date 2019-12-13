package com.example.atelier.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atelier.R;
import com.example.atelier.models.Comments;
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
    public void onBindViewHolder(ImageViewHolderComment holder, int position) {
        Comments uploadCurrent = mcUploads.get(position);
        holder.commentText.setText(uploadCurrent.getComment());
        Picasso.get().load(uploadCurrent.getProfile_p_url()).fit().centerCrop().into(holder.commentPhoto);

        Log.e("rcc","::"+mcUploads.get(position));
        Log.e("rcc","::"+uploadCurrent.getComment());
        Log.e("rcc","::"+uploadCurrent.getProfile_p_url());

    }

    @Override
    public int getItemCount() {
        return mcUploads.size();
    }

    public class ImageViewHolderComment extends RecyclerView.ViewHolder {

        public TextView commentText;
        public ImageView commentPhoto;

        public ImageViewHolderComment(@NonNull View itemView) {
            super(itemView);

            commentText = itemView.findViewById(R.id.comment_content);
            commentPhoto = itemView.findViewById(R.id.comment_photo);

        }

    }
}