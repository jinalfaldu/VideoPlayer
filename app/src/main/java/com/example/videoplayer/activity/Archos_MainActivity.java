package com.example.videoplayer.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.videoplayer.R;
import com.example.videoplayer.fragment.Archos_MusicFragment;
import com.example.videoplayer.fragment.Archos_VideoFragment;
import com.google.android.material.navigation.NavigationView;

public class Archos_MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    ImageView iv_music_player;
    ImageView iv_video_player;
    ImageView menu_btn;
    TextView tv_music_player;
    TextView tv_video_player;
    TextView txt_header;

    public void unselect() {
        this.iv_video_player.setImageResource(R.drawable.ic_video_un);
        this.iv_music_player.setImageResource(R.drawable.ic_music_un);
        this.tv_video_player.setTextColor(getResources().getColor(R.color.white1));
        this.tv_music_player.setTextColor(getResources().getColor(R.color.white1));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(0);
        setContentView(R.layout.archoss_activity_main);
//        try {
//            Archos_NativeAds.nativeAdsNeno(this, findViewById(R.id.rl_native_lay_nenos));
//        } catch (Exception e) {
//        }
        this.iv_video_player = (ImageView) findViewById(R.id.iv_video_player);
        this.iv_music_player = (ImageView) findViewById(R.id.iv_music_player);
        this.tv_video_player = (TextView) findViewById(R.id.tv_video_player);
        this.tv_music_player = (TextView) findViewById(R.id.tv_music_player);
        this.iv_video_player.setImageResource(R.drawable.ic_video_sel);
        this.tv_video_player.setTextColor(getResources().getColor(R.color.blue));
        Archos_VideoFragment archos_videoFragment = new Archos_VideoFragment();
        fragmentTrsaction(archos_videoFragment);
        findViewById(R.id.video_player).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Archos_MainActivity.this.unselect();
                    Archos_MainActivity.this.iv_video_player.setImageResource(R.drawable.ic_video_sel);
                    Archos_MainActivity.this.tv_video_player.setTextColor(Archos_MainActivity.this.getResources().getColor(R.color.blue));
                    Archos_VideoFragment archos_videoFragment2 = new Archos_VideoFragment();
                    Archos_MainActivity.this.fragmentTrsaction(archos_videoFragment2);
                } catch (Resources.NotFoundException e2) {
                    throw new RuntimeException(e2);
                }
            }
        });
        findViewById(R.id.music_player).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Archos_MainActivity.this.unselect();
                    Archos_MainActivity.this.iv_music_player.setImageResource(R.drawable.ic_music_sel);
                    Archos_MainActivity.this.tv_music_player.setTextColor(Archos_MainActivity.this.getResources().getColor(R.color.blue));
                    Archos_MusicFragment archos_musicFragment = new Archos_MusicFragment();
                    Archos_MainActivity.this.fragmentTrsaction(archos_musicFragment);
                } catch (Resources.NotFoundException e2) {
                    throw new RuntimeException(e2);
                }
            }
        });

        this.menu_btn = (ImageView) findViewById(R.id.menu_btn);
        this.txt_header = (TextView) findViewById(R.id.txt_header);
        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, this.drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        this.menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Archos_MainActivity.this.drawer.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setItemIconTintList(null);
    }

    public void fragmentTrsaction(Fragment fragment) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_Video_player) {
            try {
                Archos_VideoFragment archos_videoFragment = new Archos_VideoFragment();
                fragmentTrsaction(archos_videoFragment);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (id == R.id.nav_music_player) {
            try {
                Archos_MusicFragment archos_musicFragment = new Archos_MusicFragment();
                fragmentTrsaction(archos_musicFragment);
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        } else if (id == R.id.nav_share) {
//            if (isNetworkAvailable()) {
//                try {
//                    Archos_App_About.share(this);
//                } catch (Exception e5) {
//                }
//            } else {
//                Toast.makeText(this, "Connect Internet Please...", 0).show();
//            }
        } else if (id == R.id.nav_rate) {
//            if (isNetworkAvailable()) {
//                Archos_App_About.rate(this);
//            } else {
//                Toast.makeText(this, "Connect Internet Please...", 0).show();
//            }
        } else if (id == R.id.nav_privacy) {
//            if (isNetworkAvailable()) {
//                Archos_App_About.policy(this);
//            } else {
//                Toast.makeText(this, "Connect Internet Please...", 0).show();
//            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
