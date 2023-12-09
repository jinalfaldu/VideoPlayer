package com.example.videoplayer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.videoplayer.R;

public class Archos_VolumeDialog extends AppCompatDialogFragment {
    AudioManager audioManager;
    private ImageView cross;
    private SeekBar seekBar;
    private TextView volume_num;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.archos_vol_dialog_item, (ViewGroup) null);
        builder.setView(view);
        getActivity().setVolumeControlStream(3);
        this.cross = (ImageView) view.findViewById(R.id.volume_close);
        this.volume_num = (TextView) view.findViewById(R.id.vol_number);
        this.seekBar = (SeekBar) view.findViewById(R.id.vol_seekbar);
        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        this.audioManager = audioManager;
        this.seekBar.setMax(audioManager.getStreamMaxVolume(3));
        this.seekBar.setProgress(this.audioManager.getStreamVolume(3));
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                Archos_VolumeDialog.this.audioManager.setStreamVolume(3, progress, 0);
                int mediaVol = Archos_VolumeDialog.this.audioManager.getStreamVolume(3);
                int maxVol = Archos_VolumeDialog.this.audioManager.getStreamMaxVolume(3);
                double volPer = Math.ceil((mediaVol / maxVol) * 100.0d);
                Archos_VolumeDialog.this.volume_num.setText("" + volPer);
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
                Archos_VolumeDialog.this.dismiss();
            }
        });
        return builder.create();
    }
}
