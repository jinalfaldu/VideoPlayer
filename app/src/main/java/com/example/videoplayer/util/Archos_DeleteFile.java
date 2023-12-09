package com.example.videoplayer.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import java.io.File;
import java.util.ArrayList;

@SuppressWarnings("all")
public class Archos_DeleteFile {

    public interface getQResult {
        void OnResult();
    }

    public static Uri getUriFromPath(Context context, File file, boolean isImage) {
        Uri uri;
        if (isImage) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }
        String filePath = file.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"_id"}, "_data=? ", new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            cursor.close();
            return Uri.withAppendedPath(uri, "" + id);
        } else if (file.exists()) {
            ContentValues values = new ContentValues();
            values.put("_data", filePath);
            return context.getContentResolver().insert(uri, values);
        } else {
            return null;
        }
    }

    public static void deleteMedia(final Activity context, final Uri uri, int request, final getQResult getQResult2) {
        PendingIntent pendingIntent;
        final ContentResolver contentResolver = context.getContentResolver();
        PendingIntent pendingIntent2 = null;
        try {
            try {
                if (Build.VERSION.SDK_INT <= 29) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure to delete this file");
                    builder.setTitle("Delete File");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            Archos_DeleteFile.lambda$deleteMedia$0(contentResolver, uri, getQResult2, context, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    ArrayList<Uri> collection = new ArrayList<>();
                    collection.add(uri);
                    PendingIntent pendingIntent3 = MediaStore.createDeleteRequest(contentResolver, collection);
                    try {
                        try {
                            context.startIntentSenderForResult(pendingIntent3.getIntentSender(), request, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        pendingIntent2 = pendingIntent3;
                    } catch (SecurityException e2) {
                       e2.printStackTrace();
                        if (Build.VERSION.SDK_INT < 30) {
                            if (Build.VERSION.SDK_INT >= 29 && (e2 instanceof RecoverableSecurityException)) {
                                RecoverableSecurityException exception = (RecoverableSecurityException) e2;
                                pendingIntent = exception.getUserAction().getActionIntent();
                            } else {
                                pendingIntent = pendingIntent2;
                            }
                        } else {
                            ArrayList<Uri> collection2 = new ArrayList<>();
                            collection2.add(uri);
                            pendingIntent = MediaStore.createDeleteRequest(contentResolver, collection2);
                        }
                        if (pendingIntent != null) {
                            try {
                                context.startIntentSenderForResult(pendingIntent.getIntentSender(), request, null, 0, 0, 0);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            } catch (SecurityException e4) {
                e4.printStackTrace();
            }
        } catch (SecurityException e5) {
            e5.printStackTrace();
        }
    }

    public static void lambda$deleteMedia$0(ContentResolver contentResolver, Uri uri, getQResult getQResult2, Activity context, DialogInterface dialog, int which) {
        dialog.cancel();
        contentResolver.delete(uri, null, null);
        getQResult2.OnResult();
        Toast.makeText(context, "The file has been deleted successfully!", Toast.LENGTH_SHORT).show();
    }
}
