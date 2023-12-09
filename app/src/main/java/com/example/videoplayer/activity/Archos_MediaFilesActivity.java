package com.example.videoplayer.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.videoplayer.R;
import com.example.videoplayer.adapter.Archos_MediaFilesAdapter;
import com.example.videoplayer.model.Archos_MediaFiles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class Archos_MediaFilesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public static final String FOLDER_NAME_KEY = "playlistFolderName";
    public static final String MEDIA_TYPE_AUDIO = "audio";
    public static final String MEDIA_TYPE_KEY = "mediaType";
    public static final String MEDIA_TYPE_VIDEO = "video";
    public static final String MY_PREF = "My Pref";
    static Archos_MediaFilesAdapter mediaFilesAdapter;
    ImageView back;
    String folder_name;
    private ArrayList<Archos_MediaFiles> mediaFilesArrayList = new ArrayList<>();
    String mediaType;
    RecyclerView recyclerView;
    String sortOrder;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView toolbar_;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.archos_activity_media_files);
//        try {
//            Archos_NativeAds.nativeAdsSmall(this, findViewById(R.id.rl_native_lay_small));
//        } catch (Exception e) {
//        }
        this.folder_name = getIntent().getStringExtra("folderName");
        this.mediaType = getIntent().getStringExtra("mediaType");
        this.toolbar_ = (TextView) findViewById(R.id.toolbar_);
        ImageView imageView = (ImageView) findViewById(R.id.back);
        this.back = imageView;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Archos_MediaFilesActivity.this.onBackPressed();
            }
        });
        this.toolbar_.setText(this.folder_name);
        this.recyclerView = (RecyclerView) findViewById(R.id.media_rv);
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_media);
        showMediaFiles();
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Archos_MediaFilesActivity.this.showMediaFiles();
                Archos_MediaFilesActivity.this.swipeRefreshLayout.setRefreshing(false);
            }
        });
        SharedPreferences.Editor editor = getSharedPreferences("My Pref", 0).edit();
        editor.putString("playlistFolderName", this.folder_name);
        editor.putString("mediaType", this.mediaType);
        editor.apply();

    }

    public void showMediaFiles() {
        if (this.mediaType.equals("video")) {
            this.mediaFilesArrayList = fetchVideo(this.folder_name);
        } else if (this.mediaType.equals("audio")) {
            this.mediaFilesArrayList = fetchAudio(this.folder_name);
        }
        Archos_MediaFilesAdapter archos_MediaFilesAdapter = new Archos_MediaFilesAdapter(this.mediaFilesArrayList, this, this.mediaType, 0);
        mediaFilesAdapter = archos_MediaFilesAdapter;
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(mediaFilesAdapter);
        mediaFilesAdapter.notifyDataSetChanged();
    }

    private ArrayList<Archos_MediaFiles> fetchVideo(String folderName) {
        SharedPreferences preferences = getSharedPreferences("My Pref", 0);
        String sort_value = preferences.getString("sort", "abcd");
        ArrayList<Archos_MediaFiles> mediaFiles = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        if (sort_value.equals("sortName")) {
            this.sortOrder = "_display_name ASC";
        } else if (sort_value.equals("sortSize")) {
            this.sortOrder = "_size DESC";
        } else if (sort_value.equals("sortDate")) {
            this.sortOrder = "date_added DESC";
        } else {
            this.sortOrder = "duration DESC";
        }
        String[] selectionArg = {"%" + folderName + "%"};
        Cursor cursor = getContentResolver().query(uri, null, "_data like?", selectionArg, this.sortOrder);
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
        SharedPreferences preferences = getSharedPreferences("My Pref", 0);
        String sort_value = preferences.getString("sort", "abcd");
        ArrayList<Archos_MediaFiles> mediaFiles = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        if (sort_value.equals("sortName")) {
            this.sortOrder = "_display_name ASC";
        } else if (sort_value.equals("sortSize")) {
            this.sortOrder = "_size DESC";
        } else if (sort_value.equals("sortDate")) {
            this.sortOrder = "date_added DESC";
        } else {
            this.sortOrder = "duration DESC";
        }
        String[] selectionArg = {"%" + folderName + "%"};
        Cursor cursor = getContentResolver().query(uri, null, "_data like?", selectionArg, this.sortOrder);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.media_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_media);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences preferences = getSharedPreferences("My Pref", 0);
        final SharedPreferences.Editor editor = preferences.edit();
        int id = item.getItemId();

        if (id == 16908332) {
            finish();
        } else if (id == R.id.refresh_files) {
            finish();
            startActivity(getIntent());
        } else if (id == R.id.sort_by) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Sort By");
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    editor.apply();
                    Archos_MediaFilesActivity.this.finish();
                    Archos_MediaFilesActivity archos_MediaFilesActivity = Archos_MediaFilesActivity.this;
                    archos_MediaFilesActivity.startActivity(archos_MediaFilesActivity.getIntent());
                    dialogInterface.dismiss();
                }
            });
            String[] items = {"Name (A to Z)", "Size (Big to Small)", "Date (New to Old)", "Length (Long to Short)"};
            alertDialog.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0:
                            editor.putString("sort", "sortName");
                            return;
                        case 1:
                            editor.putString("sort", "sortSize");
                            return;
                        case 2:
                            editor.putString("sort", "sortDate");
                            return;
                        case 3:
                            editor.putString("sort", "sortLength");
                            return;
                        default:
                            return;
                    }
                }
            });
            alertDialog.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String inputs = newText.toLowerCase();
        ArrayList<Archos_MediaFiles> mediaFiles = new ArrayList<>();
        Iterator<Archos_MediaFiles> it = this.mediaFilesArrayList.iterator();
        while (it.hasNext()) {
            Archos_MediaFiles media = it.next();
            if (media.getTitle().toLowerCase(Locale.ROOT).contains(inputs)) {
                mediaFiles.add(media);
            }
        }
        mediaFilesAdapter.updateMediaFiles(mediaFiles);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
