package com.example.videoplayer.activity;

import android.app.PictureInPictureParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkRequest;

import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;
import com.example.videoplayer.R;
import com.example.videoplayer.adapter.Archos_MediaFilesAdapter;
import com.example.videoplayer.adapter.Archos_PlaybackIconsAdapter;
import com.example.videoplayer.dialog.Archos_BrightnessDialog;
import com.example.videoplayer.dialog.Archos_PlaylistDialog;
import com.example.videoplayer.dialog.Archos_VolumeDialog;
import com.example.videoplayer.model.Archos_IconModel;
import com.example.videoplayer.model.Archos_MediaFiles;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.ArrayList;

@SuppressWarnings("all")
public class Archos_VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = Archos_VideoPlayerActivity.class.getSimpleName();
    ConcatenatingMediaSource concatenatingMediaSource;
    private ControlsMode controlsMode;
    View decorView;
    DialogProperties dialogProperties;
    FrameLayout eqContainer;
    FilePickerDialog filePickerDialog;
    boolean isCrossChecked;
    ImageView lock;
    ArrayList<Archos_MediaFiles> mVideoFilesArrayList;
    Archos_MediaFilesAdapter mediaFilesAdapter;
    ImageView menu_more;
    ImageView nextButton;
    View nightMode;
    PlaybackParameters parameters;
    PictureInPictureParams.Builder pictureInPicture;
    Archos_PlaybackIconsAdapter playbackIconsAdapter;
    SimpleExoPlayer player;
    PlayerView playerView;
    int position;
    ImageView previousButton;
    RecyclerView recyclerViewIcons;
    RelativeLayout root;
    ImageView scaling;
    float speed;
    Uri subtitleUri;
    TextView title;
    ImageView unlock;
    ImageView videoBack;
    ImageView videoList;
    String videoTitle;
    WindowInsetsControllerCompat windowInsetsController;
    private ArrayList<Archos_IconModel> iconModelArrayList = new ArrayList<>();
    boolean expand = false;
    boolean dark = false;
    boolean mute = false;
    private ActivityResultLauncher<Intent> audioEffect = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
        }
    });
    private final ActivityResultLauncher<Intent> fileChooserIntentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        static final boolean $assertionsDisabled = false;

        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getData() == null) {
                throw new AssertionError();
            }
            Archos_VideoPlayerActivity.this.subtitleUri = result.getData().getData();
            Log.i(Archos_VideoPlayerActivity.TAG + "###", "result.getData().getData() for file chooser: " + Archos_VideoPlayerActivity.this.subtitleUri);
        }
    });
    ActivityResultLauncher<String> fileChooserIntentLauncher1 = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri uri) {
            Log.i(Archos_VideoPlayerActivity.TAG + " ###", "Uri returned for the subtitle file:\n" + uri.toString());
            Archos_VideoPlayerActivity.this.subtitleUri = uri;
        }
    });
    View.OnClickListener firstListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Archos_VideoPlayerActivity.this.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            Archos_VideoPlayerActivity.this.player.setVideoScalingMode(1);
            Archos_VideoPlayerActivity.this.scaling.setImageResource(R.drawable.fullscreen);
            Toast.makeText(Archos_VideoPlayerActivity.this, "Full Screen", Toast.LENGTH_SHORT).show();
            Archos_VideoPlayerActivity.this.scaling.setOnClickListener(Archos_VideoPlayerActivity.this.secondListener);
        }
    };
    View.OnClickListener secondListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Archos_VideoPlayerActivity.this.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            Archos_VideoPlayerActivity.this.player.setVideoScalingMode(1);
            Archos_VideoPlayerActivity.this.scaling.setImageResource(R.drawable.zoom);
            Toast.makeText(Archos_VideoPlayerActivity.this, "Zoom", Toast.LENGTH_SHORT).show();
            Archos_VideoPlayerActivity.this.scaling.setOnClickListener(Archos_VideoPlayerActivity.this.thirdListener);
        }
    };
    View.OnClickListener thirdListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Archos_VideoPlayerActivity.this.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            Archos_VideoPlayerActivity.this.player.setVideoScalingMode(1);
            Archos_VideoPlayerActivity.this.scaling.setImageResource(R.drawable.fit);
            Toast.makeText(Archos_VideoPlayerActivity.this, "Fit", Toast.LENGTH_SHORT).show();
            Archos_VideoPlayerActivity.this.scaling.setOnClickListener(Archos_VideoPlayerActivity.this.firstListener);
        }
    };

    private enum ControlsMode {
        LOCK,
        FULLSCREEN
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        setFullScreen();
        setContentView(R.layout.archos_activity_video_player);
        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
            Log.e(TAG + " ###", "in VideoPlayerActivity: " + e);
        }
        this.playerView = (PlayerView) findViewById(R.id.exoplayer_view);
        this.position = getIntent().getIntExtra("position", 1);
        this.videoTitle = getIntent().getStringExtra("media_title");
        this.mVideoFilesArrayList = getIntent().getExtras().getParcelableArrayList("mediaArrayList");
        screenOrientation();
        this.nextButton = (ImageView) findViewById(R.id.exo_next);
        this.previousButton = (ImageView) findViewById(R.id.exo_prev);
        this.nextButton.setOnClickListener(this);
        this.previousButton.setOnClickListener(this);
        TextView textView = (TextView) findViewById(R.id.video_title);
        this.title = textView;
        textView.setText(this.videoTitle);
        this.videoBack = (ImageView) findViewById(R.id.video_back);
        this.lock = (ImageView) findViewById(R.id.lock);
        this.unlock = (ImageView) findViewById(R.id.unlock);
        this.scaling = (ImageView) findViewById(R.id.exo_scaling);
        this.root = (RelativeLayout) findViewById(R.id.root_layout);
        this.nightMode = findViewById(R.id.night_mode);
        ImageView imageView = (ImageView) findViewById(R.id.video_list);
        this.videoList = imageView;
        imageView.setOnClickListener(this);
        this.recyclerViewIcons = (RecyclerView) findViewById(R.id.recyclerView_icon);
        this.eqContainer = (FrameLayout) findViewById(R.id.equalizer);
        ImageView imageView2 = (ImageView) findViewById(R.id.video_more);
        this.menu_more = imageView2;
        imageView2.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= 26) {
            this.pictureInPicture = new PictureInPictureParams.Builder();
        }
        this.iconModelArrayList.add(new Archos_IconModel(R.drawable.ic_right, ""));
        this.iconModelArrayList.add(new Archos_IconModel(R.drawable.ic_nightmode, "Night Mode"));
        this.iconModelArrayList.add(new Archos_IconModel(R.drawable.ic_pip_mode, "Popup"));
        this.iconModelArrayList.add(new Archos_IconModel(R.drawable.ic_rotate_screen, "Rotate"));
        this.videoBack.setOnClickListener(this);
        this.lock.setOnClickListener(this);
        this.unlock.setOnClickListener(this);
        this.scaling.setOnClickListener(this.firstListener);
        this.dialogProperties = new DialogProperties();
        FilePickerDialog filePickerDialog = new FilePickerDialog(this);
        this.filePickerDialog = filePickerDialog;
        filePickerDialog.setTitle("Select a Subtitle File");
        this.playbackIconsAdapter = new Archos_PlaybackIconsAdapter(this.iconModelArrayList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, true);
        this.recyclerViewIcons.setLayoutManager(layoutManager);
        this.recyclerViewIcons.setAdapter(this.playbackIconsAdapter);
        this.playbackIconsAdapter.notifyDataSetChanged();
        this.playbackIconsAdapter.setOnItemClickListener(new Archos_PlaybackIconsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position == 0) {
                    if (Archos_VideoPlayerActivity.this.expand) {
                        Archos_VideoPlayerActivity.this.iconModelArrayList.clear();
                        Archos_VideoPlayerActivity.this.iconModelArrayList.add(new Archos_IconModel(R.drawable.ic_right, ""));
                        Archos_VideoPlayerActivity.this.iconModelArrayList.add(new Archos_IconModel(R.drawable.ic_nightmode, "Night Mode"));
                        Archos_VideoPlayerActivity.this.iconModelArrayList.add(new Archos_IconModel(R.drawable.ic_pip_mode, "Popup"));
                        Archos_VideoPlayerActivity.this.iconModelArrayList.add(new Archos_IconModel(R.drawable.ic_rotate_screen, "Rotate"));
                        Archos_VideoPlayerActivity.this.playbackIconsAdapter.notifyDataSetChanged();
                        Archos_VideoPlayerActivity.this.expand = false;
                        return;
                    }
                    if (Archos_VideoPlayerActivity.this.iconModelArrayList.size() == 4) {
                        Archos_VideoPlayerActivity.this.iconModelArrayList.add(new Archos_IconModel(R.drawable.ic_volumeoff, "Mute"));
                        Archos_VideoPlayerActivity.this.iconModelArrayList.add(new Archos_IconModel(R.drawable.ic_volume, "Volume"));
                        Archos_VideoPlayerActivity.this.iconModelArrayList.add(new Archos_IconModel(R.drawable.ic_brightness, "Brightness"));
                        Archos_VideoPlayerActivity.this.iconModelArrayList.add(new Archos_IconModel(R.drawable.ic_fast_forward, "Speed"));
                    }
                    Archos_VideoPlayerActivity.this.iconModelArrayList.set(position, new Archos_IconModel(R.drawable.ic_left, ""));
                    Archos_VideoPlayerActivity.this.playbackIconsAdapter.notifyDataSetChanged();
                    Archos_VideoPlayerActivity.this.expand = true;
                } else if (position == 1) {
                    if (Archos_VideoPlayerActivity.this.dark) {
                        Archos_VideoPlayerActivity.this.nightMode.setVisibility(View.GONE);
                        Archos_VideoPlayerActivity.this.iconModelArrayList.set(position, new Archos_IconModel(R.drawable.ic_nightmode, "Night"));
                        Archos_VideoPlayerActivity.this.playbackIconsAdapter.notifyDataSetChanged();
                        Archos_VideoPlayerActivity.this.dark = false;
                        return;
                    }
                    Archos_VideoPlayerActivity.this.nightMode.setVisibility(View.VISIBLE);
                    Archos_VideoPlayerActivity.this.iconModelArrayList.set(position, new Archos_IconModel(R.drawable.ic_nightmode, "Day"));
                    Archos_VideoPlayerActivity.this.playbackIconsAdapter.notifyDataSetChanged();
                    Archos_VideoPlayerActivity.this.dark = true;
                } else if (position == 2) {
                    if (Build.VERSION.SDK_INT >= 26) {
                        Rational aspectRatio = new Rational(16, 9);
                        Archos_VideoPlayerActivity.this.pictureInPicture.setAspectRatio(aspectRatio);
                        Archos_VideoPlayerActivity archos_VideoPlayerActivity = Archos_VideoPlayerActivity.this;
                        archos_VideoPlayerActivity.enterPictureInPictureMode(archos_VideoPlayerActivity.pictureInPicture.build());
                        return;
                    }
                    Toast.makeText(Archos_VideoPlayerActivity.this.getApplicationContext(), "Picture-in-Picture is not supported!", Toast.LENGTH_SHORT).show();
                    Log.wtf(Archos_VideoPlayerActivity.TAG + " ###", "Picture in Picture not supported: true");
                } else if (position == 3) {
                    if (Archos_VideoPlayerActivity.this.getResources().getConfiguration().orientation != 1) {
                        if (Archos_VideoPlayerActivity.this.getResources().getConfiguration().orientation == 2) {
                            Archos_VideoPlayerActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            Archos_VideoPlayerActivity.this.playbackIconsAdapter.notifyDataSetChanged();
                            return;
                        }
                        return;
                    }
                    Archos_VideoPlayerActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    Archos_VideoPlayerActivity.this.playbackIconsAdapter.notifyDataSetChanged();
                } else if (position == 4) {
                    if (Archos_VideoPlayerActivity.this.mute) {
                        Archos_VideoPlayerActivity.this.player.setVolume(1.0f);
                        Archos_VideoPlayerActivity.this.iconModelArrayList.set(position, new Archos_IconModel(R.drawable.ic_volumeoff, "Mute"));
                        Archos_VideoPlayerActivity.this.playbackIconsAdapter.notifyDataSetChanged();
                        Archos_VideoPlayerActivity.this.mute = false;
                        return;
                    }
                    Archos_VideoPlayerActivity.this.player.setVolume(0.0f);
                    Archos_VideoPlayerActivity.this.iconModelArrayList.set(position, new Archos_IconModel(R.drawable.ic_volume, "Unmute"));
                    Archos_VideoPlayerActivity.this.playbackIconsAdapter.notifyDataSetChanged();
                    Archos_VideoPlayerActivity.this.mute = true;
                } else if (position == 5) {
                    Archos_VolumeDialog volumeDialog = new Archos_VolumeDialog();
                    volumeDialog.show(Archos_VideoPlayerActivity.this.getSupportFragmentManager(), "dialog");
                    Archos_VideoPlayerActivity.this.playbackIconsAdapter.notifyDataSetChanged();
                } else if (position == 6) {
                    Archos_BrightnessDialog brightnessDialog = new Archos_BrightnessDialog();
                    brightnessDialog.show(Archos_VideoPlayerActivity.this.getSupportFragmentManager(), "dialog");
                    Archos_VideoPlayerActivity.this.playbackIconsAdapter.notifyDataSetChanged();
                } else if (position == 7) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Archos_VideoPlayerActivity.this);
                    alertDialog.setTitle("Select Playback Speed").setPositiveButton("Ok", (DialogInterface.OnClickListener) null);
                    String[] items = {"0.5x", "1x Normal Speed", "1.25x", "1.5x", "2x"};
                    final int[] checkedItem = {-1};
                    alertDialog.setSingleChoiceItems(items, checkedItem[0], new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            switch (which) {
                                case 0:
                                    Archos_VideoPlayerActivity.this.speed = 0.5f;
                                    checkedItem[0] = which;
                                    Archos_VideoPlayerActivity.this.parameters = new PlaybackParameters(Archos_VideoPlayerActivity.this.speed);
                                    Archos_VideoPlayerActivity.this.player.setPlaybackParameters(Archos_VideoPlayerActivity.this.parameters);
                                    return;
                                case 1:
                                    Archos_VideoPlayerActivity.this.speed = 1.0f;
                                    checkedItem[0] = which;
                                    Archos_VideoPlayerActivity.this.parameters = new PlaybackParameters(Archos_VideoPlayerActivity.this.speed);
                                    Archos_VideoPlayerActivity.this.player.setPlaybackParameters(Archos_VideoPlayerActivity.this.parameters);
                                    return;
                                case 2:
                                    Archos_VideoPlayerActivity.this.speed = 1.25f;
                                    checkedItem[0] = which;
                                    Archos_VideoPlayerActivity.this.parameters = new PlaybackParameters(Archos_VideoPlayerActivity.this.speed);
                                    Archos_VideoPlayerActivity.this.player.setPlaybackParameters(Archos_VideoPlayerActivity.this.parameters);
                                    return;
                                case 3:
                                    Archos_VideoPlayerActivity.this.speed = 1.5f;
                                    checkedItem[0] = which;
                                    Archos_VideoPlayerActivity.this.parameters = new PlaybackParameters(Archos_VideoPlayerActivity.this.speed);
                                    Archos_VideoPlayerActivity.this.player.setPlaybackParameters(Archos_VideoPlayerActivity.this.parameters);
                                    return;
                                case 4:
                                    Archos_VideoPlayerActivity.this.speed = 2.0f;
                                    checkedItem[0] = which;
                                    Archos_VideoPlayerActivity.this.parameters = new PlaybackParameters(Archos_VideoPlayerActivity.this.speed);
                                    Archos_VideoPlayerActivity.this.player.setPlaybackParameters(Archos_VideoPlayerActivity.this.parameters);
                                    return;
                                default:
                                    return;
                            }
                        }
                    });
                    AlertDialog alert = alertDialog.create();
                    alert.show();
                }
            }
        });
        playVideo();
    }

    private void playVideo() {
        String path = this.mVideoFilesArrayList.get(this.position).getPath();
        Uri uri = Uri.parse(path);
        SimpleExoPlayer.Builder builder = new SimpleExoPlayer.Builder(this);
        builder.setSeekForwardIncrementMs(WorkRequest.MIN_BACKOFF_MILLIS);
        builder.setSeekBackIncrementMs(WorkRequest.MIN_BACKOFF_MILLIS);
        this.player = builder.build();
        DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(this);
        this.concatenatingMediaSource = new ConcatenatingMediaSource(new MediaSource[0]);
        for (int i = 0; i < this.mVideoFilesArrayList.size(); i++) {
            new File(String.valueOf(this.mVideoFilesArrayList.get(i)));
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(String.valueOf(uri))));
            this.concatenatingMediaSource.addMediaSource(mediaSource);
        }
        this.playerView.setPlayer(this.player);
        this.playerView.setKeepScreenOn(true);
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build();
        this.player.setAudioAttributes(audioAttributes, true);
        this.player.setPlaybackParameters(this.parameters);
        this.player.setMediaSource(this.concatenatingMediaSource);
        this.player.prepare();
        this.player.play();
        this.player.seekTo(this.position, C.TIME_UNSET);
        playError();

    }

    private void screenOrientation() {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            String path = this.mVideoFilesArrayList.get(this.position).getPath();
            Uri uri = Uri.parse(path);
            retriever.setDataSource(this, uri);
            Bitmap bitmap = retriever.getFrameAtTime();
            int videoWidth = bitmap.getWidth();
            int videoHeight = bitmap.getHeight();
            if (videoWidth > videoHeight) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } catch (Exception e) {
            Log.e("Media Meta Data Retriever:", "Error changing orientation!");
        }
    }

    private void playError() {
        this.player.addListener(new Player.Listener() { // from class: com.imusicplayer.fullhdvideoplayer.Archos_MediaPlayerClasses.Archos_VideoPlayerActivity.5
            @Override // com.google.android.exoplayer2.Player.Listener
            public void onPlayerError(PlaybackException error) {
                Toast.makeText(Archos_VideoPlayerActivity.this, "Video Playing Error", Toast.LENGTH_SHORT).show();
            }
        });
        this.player.setPlayWhenReady(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (this.player.isPlaying()) {
            this.player.stop();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
            this.player.getPlaybackState();
            if (!isInPictureInPictureMode()) {
                this.player.setPlayWhenReady(false);
                this.player.getPlaybackState();
                return;
            }
            this.player.setPlayWhenReady(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.player.setPlayWhenReady(true);
        this.player.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.player.setPlayWhenReady(true);
        this.player.getPlaybackState();
    }

    private void setFullScreen() {
        WindowInsetsControllerCompat windowInsetsControllerCompat = this.windowInsetsController;
        if (windowInsetsControllerCompat == null) {
            return;
        }
        windowInsetsControllerCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        this.windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.exo_next) {
            try {
                this.player.stop();
                int i = this.position + 1;
                this.position = i;
                String title = this.mVideoFilesArrayList.get(i).getTitle();
                this.videoTitle = title;
                this.title.setText(title);
                playVideo();
            } catch (Exception e) {
                Toast.makeText(this, "No next Video", Toast.LENGTH_SHORT).show();
                this.position--;
            }
        } else if (view.getId() == R.id.exo_pause) {
            this.player.pause();
        } else if (view.getId() == R.id.exo_play) {
            this.player.play();
        } else if (view.getId() == R.id.exo_prev) {
            try {
                this.player.stop();
                int i2 = this.position - 1;
                this.position = i2;
                String title2 = this.mVideoFilesArrayList.get(i2).getTitle();
                this.videoTitle = title2;
                this.title.setText(title2);
                playVideo();
            } catch (Exception e2) {
                Toast.makeText(this, "No previous Video", Toast.LENGTH_SHORT).show();
                this.position++;
            }
        } else if (view.getId() == R.id.lock) {
            this.controlsMode = ControlsMode.FULLSCREEN;
            this.root.setVisibility(View.VISIBLE);
            this.lock.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "UNLOCKED", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.unlock) {
            this.controlsMode = ControlsMode.LOCK;
            this.root.setVisibility(View.INVISIBLE);
            this.lock.setVisibility(View.VISIBLE);
            Toast.makeText(this, "LOCKED", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.video_back) {
            SimpleExoPlayer simpleExoPlayer = this.player;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.release();
            }
            finish();
        } else if (view.getId() == R.id.video_list) {
            Archos_PlaylistDialog playlistDialog = new Archos_PlaylistDialog(this.mVideoFilesArrayList, this.mediaFilesAdapter);
            playlistDialog.show(getSupportFragmentManager(), playlistDialog.getTag());
        } else if (view.getId() == R.id.video_more) {
            PopupMenu popupMenu = new PopupMenu(this, this.menu_more);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.actions_video, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    int id = menuItem.getItemId();

                    if (id == R.id.share_file) {
                        Intent shareIntent = new Intent("android.intent.action.SEND");
                        String filepath = Archos_VideoPlayerActivity.this.mVideoFilesArrayList.get(Archos_VideoPlayerActivity.this.position).getPath();
                        Uri uri = Uri.parse(filepath);
                        shareIntent.setType("*/*");
                        shareIntent.putExtra("android.intent.extra.STREAM", uri);
                        Archos_VideoPlayerActivity.this.startActivity(Intent.createChooser(shareIntent, "Share File using"));
                    }
                    return false;
                }
            });
            popupMenu.show();
        } else {
            return;
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        this.isCrossChecked = isInPictureInPictureMode;
        if (isInPictureInPictureMode) {
            this.playerView.hideController();
        } else {
            this.playerView.showController();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.isCrossChecked) {
            this.player.release();
            finish();
        }
    }
}
