package com.example.videoplayer.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.WorkRequest;

import com.example.videoplayer.dialog.Archos_Audio_PlaylistDialog;
import com.example.videoplayer.R;
import com.example.videoplayer.adapter.Archos_AudioFilesAdapter;
import com.example.videoplayer.model.Archos_MediaFiles;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSource;

import java.io.File;
import java.util.ArrayList;

@SuppressWarnings("all")
public class Archos_AudioPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String CHANNEL_ID = "channel_id";
    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = Archos_AudioPlayerActivity.class.getSimpleName();
    static String audioTitle;
    static ArrayList<Archos_MediaFiles> mAudioFilesArrayList;
    public static SimpleExoPlayer player;
    public static PlayerNotificationManager playerNotificationManager;
    static int position;
    ImageView audioBack;
    ImageView audioList;
    ConcatenatingMediaSource concatenatingMediaSource;
    String listTitle;
    LinearLayout ll_ads;
    Archos_AudioFilesAdapter mediaFilesAdapter;
    private ProgressiveMediaSource mediaSource;
    ImageView menu_more;
    ImageView nextButton;
    PlaybackParameters parameters;
    PlayerNotificationManager.Builder playerNotificationManagerBuilder;
    PlayerView playerView;
    TextView playlistTitle;
    ImageView previousButton;
    RelativeLayout root;
    TextView title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.archos_exo_simple_player_view);
//        try {
//            Archos_NativeAds.nativeAdsNeno(this, findViewById(R.id.rl_native_lay_nenos));
//        } catch (Exception e) {
//        }
        PlayerView playerView = (PlayerView) findViewById(R.id.exoplayer_view_audio);
        this.playerView = playerView;
        playerView.setControllerShowTimeoutMs(0);
        this.playerView.setControllerHideOnTouch(false);
        this.playerView.showController();
        this.root = (RelativeLayout) findViewById(R.id.root_layout_audio);
        this.audioBack = (ImageView) findViewById(R.id.audio_back_audio);
        this.audioList = (ImageView) findViewById(R.id.audio_list);
        this.nextButton = (ImageView) findViewById(R.id.exo_next);
        this.previousButton = (ImageView) findViewById(R.id.exo_prev);
        this.playlistTitle = (TextView) findViewById(R.id.playlistTitle);
        this.title = (TextView) findViewById(R.id.audiofile_title);
        this.menu_more = (ImageView) findViewById(R.id.audio_more);
        position = getIntent().getIntExtra("position", 1);
        audioTitle = getIntent().getStringExtra("media_title");
        mAudioFilesArrayList = getIntent().getExtras().getParcelableArrayList("mediaArrayList");
        this.title.setText(audioTitle);
        SharedPreferences preferences = getSharedPreferences("My Pref", 0);
        String string = preferences.getString("playlistFolderName", "DEFAULT_FOLDER_NAME");
        this.listTitle = string;
        this.playlistTitle.setText(string);
        this.audioList.setOnClickListener(this);
        this.nextButton.setOnClickListener(this);
        this.previousButton.setOnClickListener(this);
        this.audioBack.setOnClickListener(this);
        this.menu_more.setOnClickListener(this);
        createNotificationChannel();
        PlayerNotificationManager.Builder builder = new PlayerNotificationManager.Builder(this, 1, CHANNEL_ID);
        this.playerNotificationManagerBuilder = builder;
        builder.setMediaDescriptionAdapter(new DescriptionAdapter());
        this.playerNotificationManagerBuilder.setNotificationListener(new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                PlayerNotificationManager.NotificationListener.super.onNotificationCancelled(notificationId, dismissedByUser);
                if (dismissedByUser) {
                    Archos_AudioPlayerActivity.player.stop();
                }
            }

            @Override
            public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                PlayerNotificationManager.NotificationListener.super.onNotificationPosted(notificationId, notification, ongoing);
            }
        });
        playerNotificationManager = this.playerNotificationManagerBuilder.build();
        playAudio();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setFullScreen() {
        requestWindowFeature(1);
    }

    private void playAudio() {
        String path = mAudioFilesArrayList.get(position).getPath();
        Uri uri = Uri.parse(path);
        SimpleExoPlayer.Builder builder = new SimpleExoPlayer.Builder(this);
        builder.setSeekForwardIncrementMs(WorkRequest.MIN_BACKOFF_MILLIS);
        builder.setSeekBackIncrementMs(WorkRequest.MIN_BACKOFF_MILLIS);
        player = builder.build();
        DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(this);
        this.concatenatingMediaSource = new ConcatenatingMediaSource(new MediaSource[0]);
        for (int i = 0; i < mAudioFilesArrayList.size(); i++) {
            new File(String.valueOf(mAudioFilesArrayList.get(i)));
            ProgressiveMediaSource createMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(String.valueOf(uri))));
            this.mediaSource = createMediaSource;
            this.concatenatingMediaSource.addMediaSource(createMediaSource);
        }
        this.playerView.setKeepScreenOn(true);
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build();
        player.setAudioAttributes(audioAttributes, true);
        player.setPlaybackParameters(this.parameters);
        player.setMediaSource(this.concatenatingMediaSource);
        player.prepare();
        player.setPlayWhenReady(true);
        this.playerView.setPlayer(player);
        playerNotificationManager.setPlayer(player);
        player.setPlayWhenReady(true);
        player.play();
        player.seekTo(position, C.TIME_UNSET);
        playError();
    }

    private void playError() {
        player.addListener(new Player.Listener() { // from class: com.imusicplayer.fullhdvideoplayer.Archos_MediaPlayerClasses.Archos_AudioPlayerActivity.2
            @Override // com.google.android.exoplayer2.Player.Listener
            public void onPlayerError(PlaybackException error) {
                Toast.makeText(Archos_AudioPlayerActivity.this, "Audio Playing Error", Toast.LENGTH_SHORT).show();
            }
        });
        player.setPlayWhenReady(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG + " ###", "onDestroy called");
        playerNotificationManager.setPlayer(null);
        SimpleExoPlayer simpleExoPlayer = player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            player.release();
            player = null;
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.audio_back_audio) {
            onBackPressed();
        } else if (view.getId() == R.id.audio_list) {
            Archos_Audio_PlaylistDialog playlistDialog = new Archos_Audio_PlaylistDialog(mAudioFilesArrayList, this.mediaFilesAdapter);
            playlistDialog.show(getSupportFragmentManager(), playlistDialog.getTag());
        } else if (view.getId() == R.id.audio_more) {
            PopupMenu popupMenu = new PopupMenu(this, this.menu_more);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.actions_video, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    int id = menuItem.getItemId();

                    if (id == R.id.share_file) {
                        Intent shareIntent = new Intent("android.intent.action.SEND");
                        String filepath = Archos_AudioPlayerActivity.mAudioFilesArrayList.get(Archos_AudioPlayerActivity.position).getPath();
                        Uri uri = Uri.parse(filepath);
                        shareIntent.setType("*/*");
                        shareIntent.putExtra("android.intent.extra.STREAM", uri);
                        Archos_AudioPlayerActivity.this.startActivity(Intent.createChooser(shareIntent, "Share File using"));
                    }
                    return false;
                }
            });
            popupMenu.show();
        } else if (view.getId() == R.id.exo_next) {
            try {
                player.stop();
                int i = position + 1;
                position = i;
                String title = mAudioFilesArrayList.get(i).getTitle();
                audioTitle = title;
                this.title.setText(title);
                playAudio();
            } catch (Exception e) {
                Toast.makeText(this, "No next audio file", Toast.LENGTH_SHORT).show();
                position--;
            }
        } else if (view.getId() == R.id.exo_pause) {

        } else if (view.getId() == R.id.exo_pause) {

        } else if (view.getId() == R.id.exo_prev) {
            try {
                player.stop();
                int i2 = position - 1;
                position = i2;
                String title2 = mAudioFilesArrayList.get(i2).getTitle();
                audioTitle = title2;
                this.title.setText(title2);
                playAudio();
            } catch (Exception e2) {
                Toast.makeText(this, "No previous Video", Toast.LENGTH_SHORT).show();
                position++;
            }
        }
    }

    private class DescriptionAdapter implements PlayerNotificationManager.MediaDescriptionAdapter {
        private DescriptionAdapter() {
        }

        @Override
        public CharSequence getCurrentContentTitle(Player player) {
            return Archos_AudioPlayerActivity.audioTitle;
        }

        @Override
        public PendingIntent createCurrentContentIntent(Player player) {
            Intent notificationIntent = new Intent(Archos_AudioPlayerActivity.this.getApplicationContext(), Archos_AudioPlayerActivity.class);
            return PendingIntent.getActivity(Archos_AudioPlayerActivity.this.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        }

        @Override
        public CharSequence getCurrentContentText(Player player) {
            return "Description";
        }

        @Override
        public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
            return null;
        }
    }
}
