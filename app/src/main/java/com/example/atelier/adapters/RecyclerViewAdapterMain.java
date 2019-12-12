package com.example.atelier.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atelier.R;
import com.example.atelier.models.Posts;
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
    public void onBindViewHolder(RecyclerViewAdapterMain.ImageViewHolderMain holder, int position) {
        Posts uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getDescription());
        Picasso.get().load(mUploads.get(position).getImage_url()).fit().centerCrop().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolderMain extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView textViewName;
        public ImageView imageView;
        LinearLayout view_container;

        public ImageViewHolderMain(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.post_description);
            imageView = itemView.findViewById(R.id.thumbnail);
            view_container = itemView.findViewById(R.id.container);
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
