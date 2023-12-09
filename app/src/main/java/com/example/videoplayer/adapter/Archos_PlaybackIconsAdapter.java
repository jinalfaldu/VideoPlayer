package com.example.videoplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.videoplayer.R;
import com.example.videoplayer.model.Archos_IconModel;

import java.util.ArrayList;

public class Archos_PlaybackIconsAdapter extends RecyclerView.Adapter<Archos_PlaybackIconsAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Archos_IconModel> iconModelArrayList;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public Archos_PlaybackIconsAdapter(ArrayList<Archos_IconModel> iconModelArrayList, Context context) {
        this.iconModelArrayList = iconModelArrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.archos_icons_layout, parent, false);
        return new ViewHolder(view, this.mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.iconImage.setImageResource(this.iconModelArrayList.get(position).getImageView());
        holder.iconName.setText(this.iconModelArrayList.get(position).getIconTitle());
    }

    @Override
    public int getItemCount() {
        return this.iconModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView iconName;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position;
                    if (listener != null && (position = ViewHolder.this.getAbsoluteAdapterPosition()) != -1) {
                        listener.onItemClick(position);
                    }
                }
            });
            this.iconName = (TextView) itemView.findViewById(R.id.icon_Title);
            this.iconImage = (ImageView) itemView.findViewById(R.id.playback_icon);
        }
    }
}
