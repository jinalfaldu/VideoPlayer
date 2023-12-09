package com.example.videoplayer.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developer.filepicker.model.DialogConfigs;
import com.example.videoplayer.util.Archos_DeleteFile;
import com.example.videoplayer.R;
import com.example.videoplayer.activity.Archos_AudioPlayerActivity;
import com.example.videoplayer.activity.Archos_VideoPlayerActivity;
import com.example.videoplayer.model.Archos_MediaFiles;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;

public class Archos_AudioFilesAdapter extends RecyclerView.Adapter<Archos_AudioFilesAdapter.ViewHolder> {
    private static final int REQUEST_PERM_DELETE = 101;
    BottomSheetDialog bottomSheetDialog;
    private Activity context;
    private boolean isImage;
    private ArrayList<Archos_MediaFiles> mediaList;
    private final String mediaType;
    private int viewType;
    private final String TAG = Archos_AudioFilesAdapter.class.getSimpleName();
    boolean rename = false;
    double milliSeconds = 0.0d;

    public Archos_AudioFilesAdapter(ArrayList<Archos_MediaFiles> mediaList, Activity context, String type, int viewType) {
        this.mediaList = mediaList;
        this.context = context;
        this.mediaType = type;
        this.viewType = viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.archos_audio_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mediaName.setText(this.mediaList.get(position).getDisplayName());
        String size = this.mediaList.get(position).getSize();
        holder.mediaSize.setText(Formatter.formatFileSize(this.context, Long.parseLong(size)));
        if (this.mediaType.equals("video")) {
            Glide.with(this.context).load(new File(this.mediaList.get(position).getPath())).into(holder.thumbnail);
            Log.println(Log.ASSERT, "music", this.mediaList.get(position).getPath() + "");
        } else if (this.mediaType.equals("audio")) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 150);
            layoutParams.setMarginStart(8);
            layoutParams.addRule(15, -1);
            holder.thumbnail_card.setLayoutParams(layoutParams);
        }
        if (this.viewType == 0) {
            holder.menu_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Archos_AudioFilesAdapter.this.showPopup1(view, holder);
                }
            });
        } else {
            holder.menu_more.setVisibility(View.GONE);
            holder.mediaName.setTextColor(ViewCompat.MEASURED_STATE_MASK);
            holder.mediaSize.setTextColor(ViewCompat.MEASURED_STATE_MASK);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Archos_AudioFilesAdapter.this.mediaType.equals("video")) {
                    if (Archos_AudioFilesAdapter.this.mediaType.equals("audio")) {
                        Intent intent = new Intent(Archos_AudioFilesAdapter.this.context, Archos_AudioPlayerActivity.class);
                        intent.putExtra("position", position);
                        intent.putExtra("media_title", ((Archos_MediaFiles) Archos_AudioFilesAdapter.this.mediaList.get(position)).getDisplayName());
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("mediaArrayList", Archos_AudioFilesAdapter.this.mediaList);
                        intent.putExtras(bundle);
                        Archos_AudioFilesAdapter.this.context.startActivity(intent);

                        if (Archos_AudioFilesAdapter.this.viewType == 1) {
                            Archos_AudioFilesAdapter.this.context.finish();
                            return;
                        }
                        return;
                    }
                    return;
                }
                if (Archos_AudioPlayerActivity.playerNotificationManager != null) {
                    Archos_AudioPlayerActivity.playerNotificationManager.setPlayer(null);
                }

                Intent intent = new Intent(Archos_AudioFilesAdapter.this.context, Archos_VideoPlayerActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("media_title", ((Archos_MediaFiles) Archos_AudioFilesAdapter.this.mediaList.get(position)).getDisplayName());
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("mediaArrayList", Archos_AudioFilesAdapter.this.mediaList);
                intent.putExtras(bundle);
                Archos_AudioFilesAdapter.this.context.startActivity(intent);

                if (Archos_AudioFilesAdapter.this.viewType == 1) {
                    Archos_AudioFilesAdapter.this.context.finish();
                }
            }
        });
    }

    public void delelePhoto1(File file) {
        String extension = file.getName().substring(file.getName().lastIndexOf("."));
        boolean z = !extension.equals(".mp4");
        this.isImage = z;
        Uri uri = Archos_DeleteFile.getUriFromPath(this.context, file, z);
        Archos_DeleteFile.deleteMedia(this.context, uri, 101, new Archos_DeleteFile.getQResult() {
            @Override
            public void OnResult() {
                Archos_AudioFilesAdapter.this.mediaList = new ArrayList();
                Archos_AudioFilesAdapter.this.notifyDataSetChanged();
                try {
                    Toast.makeText(Archos_AudioFilesAdapter.this.context, "The file has been deleted successfully!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showPopup1(View v, final ViewHolder holder) {
        PopupMenu popup = new PopupMenu(this.context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.option_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.bs_play) {
                    holder.itemView.performClick();
                } else if (menuItem.getItemId() == R.id.bs_share) {
                    Uri mUri = Uri.parse(((Archos_MediaFiles) Archos_AudioFilesAdapter.this.mediaList.get(holder.getAdapterPosition())).getPath());
                    try {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.SEND");
                        intent.putExtra("android.intent.extra.SUBJECT", "Here are some files.");
                        intent.setType("video/*");
                        File fileed = new File(String.valueOf(mUri));
                        Uri uri = FileProvider.getUriForFile(Archos_AudioFilesAdapter.this.context, "com.imusicplayer.fullhdvideoplayer.provider", fileed);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra("android.intent.extra.STREAM", uri);
                        Log.println(Log.ASSERT, "#1_uri", uri + "--");
                        Archos_AudioFilesAdapter.this.context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else if (menuItem.getItemId() == R.id.bs_delete) {

                    File file = new File(((Archos_MediaFiles) Archos_AudioFilesAdapter.this.mediaList.get(holder.getAdapterPosition())).getPath());
                    Archos_AudioFilesAdapter.this.delelePhoto1(file);
                    Archos_AudioFilesAdapter.this.notifyDataSetChanged();

                    return true;
                } else if (menuItem.getItemId() == R.id.bs_properties) {
                    AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(Archos_AudioFilesAdapter.this.context);
                    alertDialog2.setTitle(Html.fromHtml("<font color='#000000'>Properties</font>"));
                    String name = "File: " + ((Archos_MediaFiles) Archos_AudioFilesAdapter.this.mediaList.get(holder.getAdapterPosition())).getDisplayName();
                    String path = "Path: " + ((Archos_MediaFiles) Archos_AudioFilesAdapter.this.mediaList.get(holder.getAdapterPosition())).getPath();
                    int indexOfPath = path.lastIndexOf(DialogConfigs.DIRECTORY_SEPERATOR);
                    String path2 = "Path: " + path.substring(0, indexOfPath);
                    String size = "Size: " + Formatter.formatFileSize(Archos_AudioFilesAdapter.this.context, Long.parseLong(((Archos_MediaFiles) Archos_AudioFilesAdapter.this.mediaList.get(holder.getAdapterPosition())).getSize()));
                    StringBuilder append = new StringBuilder().append("Length: ");
                    Archos_AudioFilesAdapter archos_AudioFilesAdapter = Archos_AudioFilesAdapter.this;
                    String length = append.append(archos_AudioFilesAdapter.timeConversion((long) archos_AudioFilesAdapter.milliSeconds)).toString();
                    String name_With_Format = ((Archos_MediaFiles) Archos_AudioFilesAdapter.this.mediaList.get(holder.getAdapterPosition())).getDisplayName();
                    int index = name_With_Format.lastIndexOf(".");
                    String format = "Format: " + name_With_Format.substring(index + 1);
                    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(((Archos_MediaFiles) Archos_AudioFilesAdapter.this.mediaList.get(holder.getAdapterPosition())).getPath());
                    String height = mediaMetadataRetriever.extractMetadata(19);
                    String width = mediaMetadataRetriever.extractMetadata(18);
                    String resolution = "Resolution: " + width + "x" + height;
                    alertDialog2.setMessage(name + "\n\n" + path2 + "\n\n" + size + "\n\n" + length + "\n\n" + format + "\n\n" + resolution);
                    alertDialog2.setPositiveButton(Html.fromHtml("<font color='#000000'>OK</font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog2.show();
                    return true;
                }
                return false;
            }
        });
        popup.show();
    }

    @Override
    public int getItemCount() {
        return this.mediaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mediaName;
        TextView mediaSize;
        ImageView menu_more;
        ImageView thumbnail;
        RelativeLayout thumbnail_card;

        public ViewHolder(View itemView) {
            super(itemView);
            this.thumbnail_card = (RelativeLayout) itemView.findViewById(R.id.thumbnail_card);
            this.thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            this.menu_more = (ImageView) itemView.findViewById(R.id.media_menu_more);
            this.mediaName = (TextView) itemView.findViewById(R.id.media_name);
            this.mediaSize = (TextView) itemView.findViewById(R.id.media_size);
        }
    }

    public String timeConversion(long value) {
        int duration = (int) value;
        int hrs = duration / 3600000;
        int mns = (duration / 60000) % 60000;
        int scs = (duration % 60000) / 1000;
        if (hrs > 0) {
            String mediaTime = String.format("%02d:%02d:%02d", Integer.valueOf(hrs), Integer.valueOf(mns), Integer.valueOf(scs));
            return mediaTime;
        }
        String mediaTime2 = String.format("%02d:%02d", Integer.valueOf(mns), Integer.valueOf(scs));
        return mediaTime2;
    }

    public void updateMediaFiles(ArrayList<Archos_MediaFiles> files) {
        ArrayList<Archos_MediaFiles> arrayList = new ArrayList<>();
        this.mediaList = arrayList;
        arrayList.addAll(files);
        notifyDataSetChanged();
    }
}
