package com.example.videoplayer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.developer.filepicker.model.DialogConfigs;
import com.example.videoplayer.R;
import com.example.videoplayer.activity.Archos_MediaFilesActivity;
import com.example.videoplayer.model.Archos_MediaFiles;

import java.util.ArrayList;
import java.util.Iterator;

public class Archos_MediaFoldersAdapter extends RecyclerView.Adapter<Archos_MediaFoldersAdapter.ViewHolder> {
    private static final String TAG = Archos_MediaFoldersAdapter.class.getSimpleName();
    private ArrayList<String> folderPath;
    private Activity mContext;
    private ArrayList<Archos_MediaFiles> mediaFiles;
    private String mediaType;

    public Archos_MediaFoldersAdapter(ArrayList<Archos_MediaFiles> mediaFiles, ArrayList<String> folderPath, Activity mContext, String mediaType) {
        this.mediaFiles = mediaFiles;
        this.folderPath = folderPath;
        this.mContext = mContext;
        this.mediaType = mediaType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.archos_folder_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int indexPath = this.folderPath.get(position).lastIndexOf(DialogConfigs.DIRECTORY_SEPERATOR);
        String nameOfFolder = this.folderPath.get(position).substring(indexPath + 1);
        holder.folderName.setText(nameOfFolder);
        holder.folderPath.setText(this.folderPath.get(position));
        int number_of_Files = noOfFiles(this.folderPath.get(position));
        holder.noOfFiles.setText(number_of_Files + " Files");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Archos_MediaFoldersAdapter.this.mContext, Archos_MediaFilesActivity.class);
                intent.putExtra("folderName", nameOfFolder);
                intent.putExtra("mediaType", Archos_MediaFoldersAdapter.this.mediaType);
                Archos_MediaFoldersAdapter.this.mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.folderPath.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView folderName;
        TextView folderPath;
        TextView noOfFiles;

        public ViewHolder(View itemView) {
            super(itemView);
            this.folderName = (TextView) itemView.findViewById(R.id.folderName);
            this.folderPath = (TextView) itemView.findViewById(R.id.folderPath);
            this.noOfFiles = (TextView) itemView.findViewById(R.id.noOfFiles);
        }
    }

    int noOfFiles(String folder_name) {
        int files_number = 0;
        Iterator<Archos_MediaFiles> it = this.mediaFiles.iterator();
        while (it.hasNext()) {
            Archos_MediaFiles mediaFiles = it.next();
            if (mediaFiles.getPath().substring(0, mediaFiles.getPath().lastIndexOf(DialogConfigs.DIRECTORY_SEPERATOR)).trim().equals(folder_name)) {
                files_number++;
            }
        }
        return files_number;
    }
}
