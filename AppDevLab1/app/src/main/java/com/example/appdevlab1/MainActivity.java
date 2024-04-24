package com.example.appdevlab1;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    Button mybtn;
    TextView txtCount;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mybtn = (Button)findViewById(R.id.button);
        txtCount = (TextView) findViewById(R.id.count);
    }

    public void countClicks(View view) {
        count ++;
        txtCount.setText(String.valueOf(count));
    }

}