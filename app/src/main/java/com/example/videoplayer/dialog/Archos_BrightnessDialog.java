package com.example.videoplayer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.videoplayer.R;

public class Archos_BrightnessDialog extends AppCompatDialogFragment {
    private TextView brightnessNumber;
    private ImageView cross;
    private ActivityResultLauncher<Intent> requestBrightnessPermissionLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
        }
    });
    private SeekBar seekBar;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.archos_brt_dialog_item, (ViewGroup) null);
        builder.setView(view);
        this.cross = (ImageView) view.findViewById(R.id.brt_close);
        this.brightnessNumber = (TextView) view.findViewById(R.id.brt_number);
        this.seekBar = (SeekBar) view.findViewById(R.id.brt_seekbar);
        int brightness = Settings.System.getInt(getContext().getContentResolver(), "screen_brightness", 0);
        this.brightnessNumber.setText(brightness + "");
        this.seekBar.setProgress(brightness);
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Context context = Archos_BrightnessDialog.this.getContext().getApplicationContext();
                boolean canWrite = Settings.System.canWrite(context);
                if (!canWrite) {
                    Toast.makeText(context, "Enable write settings for brightness control", Toast.LENGTH_SHORT).show();
                    Intent brightnessIntent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS");
                    brightnessIntent.setData(Uri.parse("package:" + context.getPackageName()));
                    Archos_BrightnessDialog.this.requestBrightnessPermissionLauncher.launch(brightnessIntent);
                    return;
                }
                int sBrightness = (i * 255) / 255;
                Archos_BrightnessDialog.this.brightnessNumber.setText(sBrightness + "");
                Settings.System.putInt(context.getContentResolver(), "screen_brightness_mode", 0);
                Settings.System.putInt(context.getContentResolver(), "screen_brightness", sBrightness);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        this.cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                Archos_BrightnessDialog.this.dismiss();
            }
        });
        return builder.create();
    }
}
