package com.example.videoplayer.dialog;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videoplayer.R;
import com.example.videoplayer.adapter.Archos_AudioFilesAdapter;
import com.example.videoplayer.model.Archos_MediaFiles;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class Archos_Audio_PlaylistDialog extends BottomSheetDialogFragment {
    private static final String TAG = Archos_Audio_PlaylistDialog.class.getSimpleName();
    ArrayList<Archos_MediaFiles> arrayList;
    BottomSheetDialog bottomSheetDialog;
    TextView folder;
    Archos_AudioFilesAdapter mediaFilesAdapter;
    RecyclerView recyclerView;

    public Archos_Audio_PlaylistDialog(ArrayList<Archos_MediaFiles> arrayList, Archos_AudioFilesAdapter mediaFilesAdapter) {
        this.arrayList = new ArrayList<>();
        this.arrayList = arrayList;
        this.mediaFilesAdapter = mediaFilesAdapter;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.archos_playlist_bottomsheet_layout, (ViewGroup) null);
        this.bottomSheetDialog.setContentView(view);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.playlist_rv);
        this.folder = (TextView) view.findViewById(R.id.playlistName);
        SharedPreferences preferences = getActivity().getSharedPreferences("My Pref", 0);
        String mediaType = preferences.getString("mediaType", "DEFAULT_MEDIA_TYPE");
        String folderName = preferences.getString("playlistFolderName", "DEFAULT_FOLDER_NAME");
        StringBuilder sb = new StringBuilder();
        String str = TAG;
        Log.i(sb.append(str).append(" ###").toString(), "folderName: " + folderName + " mediaType: " + mediaType);
        this.folder.setText(folderName);
        if (mediaType.equals("audio")) {
            this.arrayList = fetchAudio(folderName);
        } else if (mediaType.equals("video")) {
            this.arrayList = fetchVideo(folderName);
        } else {
            Log.e(str + "###", "error fetching mediaType!");
        }
        this.mediaFilesAdapter = new Archos_AudioFilesAdapter(this.arrayList, getActivity(), mediaType, 1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setAdapter(this.mediaFilesAdapter);
        this.mediaFilesAdapter.notifyDataSetChanged();
        return this.bottomSheetDialog;
    }

    private ArrayList<Archos_MediaFiles> fetchVideo(String folderName) {
        ArrayList<Archos_MediaFiles> mediaFiles = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] selectionArg = {"%" + folderName + "%"};
        Cursor cursor = getContext().getContentResolver().query(uri, null, "_data like?", selectionArg, null);
        if (cursor != null && cursor.moveToNext()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow("_display_name"));
                String size = cursor.getString(cursor.getColumnIndexOrThrow("_size"));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"));
                String path = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow("date_added"));
                Archos_MediaFiles mMediaFiles = new Archos_MediaFiles(id, title, displayName, size, duration, path, dateAdded);
                mediaFiles.add(mMediaFiles);
            } while (cursor.moveToNext());
            return mediaFiles;
        }
        return mediaFiles;
    }

    private ArrayList<Archos_MediaFiles> fetchAudio(String folderName) {
        ArrayList<Archos_MediaFiles> mediaFiles = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] selectionArg = {"%" + folderName + "%"};
        Cursor cursor = getContext().getContentResolver().query(uri, null, "_data like?", selectionArg, null);
        if (cursor != null && cursor.moveToNext()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow("_display_name"));
                String size = cursor.getString(cursor.getColumnIndexOrThrow("_size"));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"));
                String path = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow("date_added"));
                Archos_MediaFiles mMediaFiles = new Archos_MediaFiles(id, title, displayName, size, duration, path, dateAdded);
                mediaFiles.add(mMediaFiles);
            } while (cursor.moveToNext());
            return mediaFiles;
        }
        return mediaFiles;
    }
}
