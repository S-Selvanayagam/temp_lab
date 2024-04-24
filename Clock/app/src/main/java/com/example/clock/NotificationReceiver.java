package com.example.clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class NotificationReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    // implement onReceive() method
    public void onReceive(Context context, Intent intent) {

        // we will use vibrator first
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(4000);

//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("New event!")
//                .setMessage("Notification!")
//                .setNeutralButton("OK", null);
//
//        AlertDialog dialog = builder.create();
//        dialog.show();

        Toast.makeText(context, "Notification!", Toast.LENGTH_LONG).show();
    }
}