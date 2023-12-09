package com.example.videoplayer.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.developer.filepicker.model.DialogConfigs;
import com.example.videoplayer.R;
import com.example.videoplayer.adapter.Archos_MediaFoldersAdapter;
import com.example.videoplayer.model.Archos_MediaFiles;

import java.util.ArrayList;

public class Archos_VideoFragment extends Fragment {
    private static final String TAG = Archos_VideoFragment.class.getSimpleName();
    RecyclerView mRecyclerView;
    Archos_MediaFoldersAdapter mediaFoldersAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Archos_MediaFiles> mediaFiles = new ArrayList<>();
    private ArrayList<String> allFolderList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.archos_fragment_videos, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mRecyclerView = (RecyclerView) view.findViewById(R.id.folders_recycler_view_Video);
        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_folder_Video);
        showFolders();
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Archos_VideoFragment.this.showFolders();
                Archos_VideoFragment.this.swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void showFolders() {
        this.mediaFiles = fetchMedia();
        Archos_MediaFoldersAdapter archos_MediaFoldersAdapter = new Archos_MediaFoldersAdapter(this.mediaFiles, this.allFolderList, getActivity(), "video");
        this.mediaFoldersAdapter = archos_MediaFoldersAdapter;
        this.mRecyclerView.setAdapter(archos_MediaFoldersAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        this.mediaFoldersAdapter.notifyDataSetChanged();
    }

    private ArrayList<Archos_MediaFiles> fetchMedia() {
        ArrayList<Archos_MediaFiles> mediaFilesArrayList = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        try {
            Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    String displayName = cursor.getString(cursor.getColumnIndexOrThrow("_display_name"));
                    String size = cursor.getString(cursor.getColumnIndexOrThrow("_size"));
                    String duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                    String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow("date_added"));
                    Archos_MediaFiles mediaFiles = new Archos_MediaFiles(id, title, displayName, size, duration, path, dateAdded);
                    int index = path.lastIndexOf(DialogConfigs.DIRECTORY_SEPERATOR);
                    String subString = path.substring(0, index);
                    if (!this.allFolderList.contains(subString)) {
                        this.allFolderList.add(subString);
                    }
                    mediaFilesArrayList.add(mediaFiles);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG + " ###", "Error retrieving the video files " + e);
        }
        return mediaFilesArrayList;
    }
}
