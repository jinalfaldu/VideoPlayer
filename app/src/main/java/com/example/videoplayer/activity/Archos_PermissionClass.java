package com.example.videoplayer.activity;

import android.app.Activity;
import android.os.Build;
import androidx.core.content.ContextCompat;

public class Archos_PermissionClass {
    public static boolean checkPermissionAccess(Activity activity, int REQUEST_WRITE_PERMISSION) {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(activity, "android.permission.READ_MEDIA_IMAGES") == 0 && ContextCompat.checkSelfPermission(activity, "android.permission.READ_MEDIA_VIDEO") == 0 && ContextCompat.checkSelfPermission(activity, "android.permission.READ_MEDIA_AUDIO") == 0) {
                return true;
            }
            try {
                requestPermission(activity, REQUEST_WRITE_PERMISSION);
            } catch (Exception e) {
            }
            return false;
        } else if (ContextCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            return true;
        } else {
            try {
                requestPermission(activity, REQUEST_WRITE_PERMISSION);
            } catch (Exception e2) {
            }
            return false;
        }
    }

    public static boolean checkPermissionAccess1(Activity activity, int REQUEST_WRITE_PERMISSION) {
        return Build.VERSION.SDK_INT >= 33 ? ContextCompat.checkSelfPermission(activity, "android.permission.READ_MEDIA_IMAGES") == 0 && ContextCompat.checkSelfPermission(activity, "android.permission.READ_MEDIA_VIDEO") == 0 && ContextCompat.checkSelfPermission(activity, "android.permission.READ_MEDIA_AUDIO") == 0 : ContextCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE") == 0;
    }

    public static void requestPermission(Activity activity, int REQUEST_WRITE_PERMISSION) {
        try {
            if (Build.VERSION.SDK_INT >= 33) {
                try {
                    activity.requestPermissions(new String[]{"android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO", "android.permission.READ_MEDIA_AUDIO"}, REQUEST_WRITE_PERMISSION);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            try {
                activity.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, REQUEST_WRITE_PERMISSION);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return;
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }
}
