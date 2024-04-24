package com.example.clock;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    TimePicker alarmTimePicker;
    DatePicker alarmDatePicker;
    Spinner spinner;
    PendingIntent pendingIntent;
    PendingIntent pendingIntent2;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmDatePicker = (DatePicker) findViewById(R.id.datePicker);
        spinner = (Spinner) findViewById(R.id.spinner1);

        String[] items = new String[]{"Alarm", "Notification"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    // OnToggleClicked() method is implemented the time functionality
    public void OnToggleClicked(View view) {
        long time;
        if (((ToggleButton) view).isChecked() && spinner.getSelectedItem().toString().equals("Alarm")) {
            Toast.makeText(MainActivity.this, "ALARM ON", Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, alarmDatePicker.getDayOfMonth());
            calendar.set(Calendar.MONTH, alarmDatePicker.getMonth());
            calendar.set(Calendar.YEAR, alarmDatePicker.getYear());

            // calendar is called to get current time in hour and minute

            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());

            Context context = view.getContext();
            File path = context.getExternalFilesDir(null);
            File file = new File(path, "events.txt");
            Log.i("info", file.toString());
            String toshow = alarmDatePicker.getDayOfMonth()+"/"+alarmDatePicker.getMonth()+"/"+alarmDatePicker.getYear()+'\n';
            toshow += alarmTimePicker.getHour()+':'+alarmTimePicker.getMinute()+'\n';
            toshow += spinner.getSelectedItem().toString()+'\n';
            try {
                FileOutputStream stream = new FileOutputStream(file, true);
                try {
                    stream.write(toshow.getBytes());
                } finally {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            // using intent i have class AlarmReceiver class which inherits
            // BroadcastReceiver
            Intent intent = new Intent(this, AlarmReceiver.class);

            // we call broadcast using pendingIntent
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
            if (System.currentTimeMillis() > time) {
                // setting time as AM and PM
                if (Calendar.AM_PM == 0)
                    time = time + (1000 * 60 * 60 * 12);
                else
                    time = time + (1000 * 60 * 60 * 24);
            }
            // Alarm rings continuously until toggle button is turned off
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent);
            // alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (time * 1000), pendingIntent);
        }
        if (!((ToggleButton) view).isChecked() && spinner.getSelectedItem().toString().equals("Alarm")){
            alarmManager.cancel(pendingIntent);
            Toast.makeText(MainActivity.this, "ALARM OFF", Toast.LENGTH_SHORT).show();
        }
        if (((ToggleButton) view).isChecked() && spinner.getSelectedItem().toString().equals("Notification")) {
            Toast.makeText(MainActivity.this, "NOTIFICATION ON", Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, alarmDatePicker.getDayOfMonth());
            calendar.set(Calendar.MONTH, alarmDatePicker.getMonth());
            calendar.set(Calendar.YEAR, alarmDatePicker.getYear());

            // calendar is called to get current time in hour and minute

            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());

            // using intent i have class AlarmReceiver class which inherits
            // BroadcastReceiver
            Intent intent = new Intent(this, NotificationReceiver.class);

            // we call broadcast using pendingIntent
            pendingIntent2 = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
            if (System.currentTimeMillis() > time) {
                // setting time as AM and PM
                if (Calendar.AM_PM == 0)
                    time = time + (1000 * 60 * 60 * 12);
                else
                    time = time + (1000 * 60 * 60 * 24);
            }
            // Alarm rings continuously until toggle button is turned off
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent2);
            // alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (time * 1000), pendingIntent);
        }
        if (!((ToggleButton) view).isChecked() && spinner.getSelectedItem().toString().equals("Notification")){
            Log.i("bro", "gg");
            alarmManager.cancel(pendingIntent2);
            Toast.makeText(MainActivity.this, "NOTIFICATION OFF", Toast.LENGTH_SHORT).show();
        }
    }
}