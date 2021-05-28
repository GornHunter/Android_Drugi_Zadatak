package com.example.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

public class SecondActivity extends AppCompatActivity {
    TextView igrac1, igrac2, rezultat1, rezultat2, ai;
    Button[] tabela = new Button[9];
    int[] stanje = new int[9];
    boolean[] aktivniIgrac = new boolean[1];
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent intent = getIntent();
        String msg = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        igrac1 = (TextView) findViewById(R.id.tvIgrac1);
        igrac2 = (TextView) findViewById(R.id.tvIgrac2);
        rezultat1 = (TextView) findViewById(R.id.tvRezultat1);
        rezultat2 = (TextView) findViewById(R.id.tvRezultat2);
        ai = (TextView) findViewById(R.id.tvStanje);
        igrac1.setText(msg.split(";")[0]);
        igrac2.setText(msg.split(";")[1]);


        igrac1.setTextColor(Color.parseColor("#000000"));
        igrac2.setTextColor(Color.parseColor("#000000"));
        rezultat1.setTextColor(Color.parseColor("#000000"));
        rezultat2.setTextColor(Color.parseColor("#000000"));

        if(msg.split(";")[2].equals("")){
            aktivniIgrac[0] = Boolean.parseBoolean(msg.split(";")[3]);
            igrac2.setTextColor(Color.parseColor("#000000"));
            ai.setText(aktivniIgrac[0] + "");
        }
        else{
            aktivniIgrac[0] = Boolean.parseBoolean(msg.split(";")[2]);
            igrac1.setTextColor(Color.parseColor("#ff0000"));
            ai.setText(aktivniIgrac[0] + "");
        }

        //0 - prazno polje
        //1 - x
        //2 - o
        Arrays.fill(stanje, 0);

        for(int i = 0;i < tabela.length;i++){
            String buttonId = "btn_" + i;
            int resourceId = getResources().getIdentifier(buttonId, "id", getPackageName());
            tabela[i] = findViewById(resourceId);

            tabela[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Button trenutno = (Button) v;
                            if(trenutno.getText().toString().equals("")){

                            }
                        }
                    }).start();
                }
            });
        }

        if(aktivniIgrac[0]){
            for (Button button : tabela) {
                button.setEnabled(true);
            }
        }
        else{
            for (Button button : tabela) {
                button.setEnabled(false);
            }
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    BufferedReader b = NetworkSingleton.getBufferedReader();
                    String line;
                    try {
                        line = b.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }

                    if(line.split(";")[0].equals("POSALJI")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rezultat1.setText("hello");
                            }
                        });
                    }
                }
            }
        });
        t.start();

        //poruka = (TextView) findViewById(R.id.tvP);
        back = (Button) findViewById(R.id.btnBack);
        //poruka.setText(m);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NetworkSingleton.getPrintWriter().println("GO_BACK;");
                        //finish();
                    }
                }).start();
            }
        });
    }
}