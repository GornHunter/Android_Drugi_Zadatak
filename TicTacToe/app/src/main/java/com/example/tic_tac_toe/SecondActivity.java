package com.example.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {
    TextView poruka;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        poruka = (TextView) findViewById(R.id.tvP);
        back = (Button) findViewById(R.id.btnBack);

        Intent intent = getIntent();
        String m = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        poruka.setText(m);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NetworkSingleton.getPrintWriter().println("GO_BACK;");
                        finish();
                    }
                }).start();
            }
        });
    }
}