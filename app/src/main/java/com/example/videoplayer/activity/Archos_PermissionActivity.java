package com.example.videoplayer.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.videoplayer.R;

public class Archos_PermissionActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_PERMISSION = 786;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.archos_activity_permission);
        ImageView btn_allow_permsion = (ImageView) findViewById(R.id.btn_allow_permsion);
        btn_allow_permsion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Archos_PermissionClass.checkPermissionAccess(Archos_PermissionActivity.this, REQUEST_WRITE_PERMISSION)) {
                    openFilePicker();
                } else {
                    Archos_PermissionClass.requestPermission(Archos_PermissionActivity.this, REQUEST_WRITE_PERMISSION);
                }
            }
        });
        if (Archos_PermissionClass.checkPermissionAccess1(this, REQUEST_WRITE_PERMISSION)) {
            startActivity(new Intent(getApplicationContext(), Archos_MainActivity.class));
        }
    }

    private void openFilePicker() {
        if (Archos_PermissionClass.checkPermissionAccess(this, REQUEST_WRITE_PERMISSION)) {
            startActivity(new Intent(this, Archos_MainActivity.class));
            return;
        }
        Archos_PermissionClass.requestPermission(this, REQUEST_WRITE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= 33) {
            if (requestCode == REQUEST_WRITE_PERMISSION) {
                try {
                    if (grantResults[0] == 0 && grantResults[1] == 0 && grantResults[2] == 0) {
                        openFilePicker();
                        System.out.println("openFilePicker ===> ");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            if (grantResults[0] != -1 && grantResults[1] != -1 && grantResults[2] != -1) {
                Toast.makeText(getApplicationContext(), "Please Allow Storage Permission.", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(), "Please Allow Storage Permission.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            return;
        }
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            try {
                if (grantResults[0] == 0) {
                    openFilePicker();
                    return;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                return;
            }
        }
        if (grantResults[0] == -1) {
            Toast.makeText(getApplicationContext(), "Please Allow Storage Permission.", Toast.LENGTH_SHORT).show();
            try {
                Intent intent2 = new Intent();
                intent2.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                Uri uri2 = Uri.fromParts("package", getPackageName(), null);
                intent2.setData(uri2);
                startActivity(intent2);
            } catch (Exception e3) {
                e3.printStackTrace();
            }
            return;
        }
        Toast.makeText(getApplicationContext(), "Please Allow Storage Permission.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
