package com.example.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class SecondActivity extends AppCompatActivity {
    public final static String RESPONSE_MESSAGE = "msg";

    TextView igrac1, igrac2, rezultat1, rezultat2, ai;
    Button[] tabela = new Button[9];
    boolean[] aktivniIgrac = new boolean[1];
    int[] potez = new int[1];
    Button back, promeni;

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

        Object[] lock = new Object[1];
        lock[0] = NetworkSingleton.getLock();

        potez[0] = Integer.parseInt(msg.split(";")[4]);;
        String protivnik = igrac2.getText().toString();

        int[] rezultat = new int[1];
        rezultat[0] = Integer.parseInt(rezultat1.getText().toString());

        igrac1.setTextColor(Color.parseColor("#000000"));
        igrac2.setTextColor(Color.parseColor("#000000"));
        rezultat1.setTextColor(Color.parseColor("#000000"));
        rezultat2.setTextColor(Color.parseColor("#000000"));

        if(msg.split(";")[2].equals("")){
            aktivniIgrac[0] = Boolean.parseBoolean(msg.split(";")[3]);
            igrac2.setTextColor(Color.parseColor("#000000"));
        }
        else{
            aktivniIgrac[0] = Boolean.parseBoolean(msg.split(";")[2]);
            igrac1.setTextColor(Color.parseColor("#ff0000"));
        }

        //0 - prazno polje
        //1 - o
        //2 - x
        //Arrays.fill(stanje, 0);

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
                            Button trenutni = (Button) v;

                            if(trenutni.getText().toString().equals("")){
                                if(aktivniIgrac[0]) {
                                    if (potez[0] % 2 == 0) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                trenutni.setTextColor(Color.parseColor("#0bfc03"));
                                                trenutni.setText("x");
                                            }
                                        });
                                        int znak = 2;

                                        String buttonId = v.getResources().getResourceEntryName(v.getId());
                                        int pozicija = Integer.parseInt(buttonId.split("_")[1]);

                                        PrintWriter p = NetworkSingleton.getPrintWriter();
                                        p.println("POSALJI_POTEZ" + ";" + pozicija + ";" + znak + ";" + protivnik + ";" + rezultat[0]);
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                trenutni.setTextColor(Color.parseColor("#0384fc"));
                                                trenutni.setText("o");
                                            }
                                        });
                                        int znak = 1;

                                        String buttonId = v.getResources().getResourceEntryName(v.getId());
                                        int pozicija = Integer.parseInt(buttonId.split("_")[1]);

                                        PrintWriter p = NetworkSingleton.getPrintWriter();
                                        p.println("POSALJI_POTEZ" + ";" + pozicija + ";" + znak + ";" + protivnik + ";" + rezultat[0]);
                                    }
                                }
                                else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SecondActivity.this, "Sacekajte da protivnik odigra svoj potez!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                    }).start();
                }
            });
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock[0]) {
                    while (true) {
                        BufferedReader b = NetworkSingleton.getBufferedReader();
                        String response;
                        try {
                            response = b.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }

                        if(response.split(";")[0].equals("AZURIRAJ_PODATKE")){
                            aktivniIgrac[0] = Boolean.parseBoolean(response.split(";")[3]);
                            if(aktivniIgrac[0]){
                                if(Integer.parseInt(response.split(";")[2]) == 1){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tabela[Integer.parseInt(response.split(";")[1])].setTextColor(Color.parseColor("#0384fc"));
                                            tabela[Integer.parseInt(response.split(";")[1])].setText("o");
                                            igrac1.setTextColor(Color.parseColor("#ff0000"));
                                        }
                                    });
                                }
                                else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tabela[Integer.parseInt(response.split(";")[1])].setTextColor(Color.parseColor("#0bfc03"));
                                            tabela[Integer.parseInt(response.split(";")[1])].setText("x");
                                            igrac1.setTextColor(Color.parseColor("#ff0000"));
                                        }
                                    });
                                }
                                if(Boolean.parseBoolean(response.split(";")[4])){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            for(Button b : tabela) {
                                                b.setEnabled(false);
                                                b.setBackgroundColor(Color.parseColor("#000000"));
                                            }
                                        }
                                    });
                                }
                                if(!response.split(";")[5].equals("a") && !response.split(";")[5].equals("nereseno")){
                                    String msg = response.split(";")[5];
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tabela[Integer.parseInt(msg.split(":")[0])].setBackgroundColor(Color.parseColor("#ff0000"));
                                            tabela[Integer.parseInt(msg.split(":")[1])].setBackgroundColor(Color.parseColor("#ff0000"));
                                            tabela[Integer.parseInt(msg.split(":")[2])].setBackgroundColor(Color.parseColor("#ff0000"));
                                        }
                                    });
                                }
                            }
                            else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        igrac1.setTextColor(Color.parseColor("#000000"));

                                    }
                                });
                                if(Boolean.parseBoolean(response.split(";")[4])){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            for(Button b : tabela){
                                                b.setEnabled(false);
                                                b.setBackgroundColor(Color.parseColor("#000000"));
                                            }
                                        }
                                    });
                                }
                                if(!response.split(";")[5].equals("a") && !response.split(";")[5].equals("nereseno")){
                                    String msg = response.split(";")[5];
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tabela[Integer.parseInt(msg.split(":")[0])].setBackgroundColor(Color.parseColor("#0d7a23"));
                                            tabela[Integer.parseInt(msg.split(":")[1])].setBackgroundColor(Color.parseColor("#0d7a23"));
                                            tabela[Integer.parseInt(msg.split(":")[2])].setBackgroundColor(Color.parseColor("#0d7a23"));
                                        }
                                    });
                                }
                            }
                        }
                        else if(response.split(";")[0].equals("NERESENO")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SecondActivity.this, response.split(";")[1], Toast.LENGTH_SHORT).show();
                                    for (Button b : tabela) {
                                        b.setText("");
                                        b.setBackgroundColor(Color.parseColor("#000000"));
                                    }
                                }
                            });
                        }
                        else if(response.split(";")[0].equals("POBEDA")){
                            if(response.split(";")[1].equals("Vi ste pobedili.")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SecondActivity.this, response.split(";")[1], Toast.LENGTH_SHORT).show();
                                        int res = Integer.parseInt(rezultat1.getText().toString()) + 1;
                                        rezultat1.setText(res + "");
                                        rezultat[0] = Integer.parseInt(rezultat1.getText().toString());
                                        igrac1.setTextColor(Color.parseColor("#000000"));
                                        /*try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }*/
                                        for (Button b : tabela) {
                                            b.setText("");
                                            b.setBackgroundColor(Color.parseColor("#000000"));
                                        }
                                    }
                                });
                                aktivniIgrac[0] = Boolean.parseBoolean(response.split(";")[2]);
                                potez[0] = Integer.parseInt(response.split(";")[3]);
                            }
                            else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SecondActivity.this, response.split(";")[1], Toast.LENGTH_SHORT).show();
                                        int res = Integer.parseInt(rezultat2.getText().toString()) + 1;
                                        rezultat2.setText(res + "");
                                        rezultat[0] = Integer.parseInt(rezultat1.getText().toString());
                                        igrac1.setTextColor(Color.parseColor("#ff0000"));
                                        /*try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }*/
                                        for (Button b : tabela) {
                                            b.setText("");
                                            b.setBackgroundColor(Color.parseColor("#000000"));
                                        }
                                    }
                                });
                                aktivniIgrac[0] = Boolean.parseBoolean(response.split(";")[2]);
                                potez[0] = Integer.parseInt(response.split(";")[3]);
                            }
                        }
                        else if(response.split(";")[0].equals("NASTAVAK")){
                            if(response.split(";")[1].startsWith("Pobedili ste 3 runde.")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Toast.makeText(SecondActivity.this, response.split(";")[1], Toast.LENGTH_SHORT).show();
                                        int res = Integer.parseInt(rezultat1.getText().toString()) + 1;
                                        rezultat1.setText(res + "");
                                        rezultat[0] = 0;

                                        show(response.split(";")[1]);
                                    }
                                });
                            }
                            else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int res = Integer.parseInt(rezultat2.getText().toString()) + 1;
                                        rezultat2.setText(res + "");
                                        rezultat[0] = 0;

                                        show(response.split(";")[1]);
                                    }
                                });
                            }
                        }
                        else if(response.split(";")[0].equals("NASTAVI_IGRU")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rezultat1.setText("0");
                                    rezultat2.setText("0");
                                    for (Button b : tabela) {
                                        b.setEnabled(true);
                                        b.setText("");
                                        b.setBackgroundColor(Color.parseColor("#000000"));
                                    }
                                }
                            });
                            if(aktivniIgrac[0] && potez[0] == 1)
                                potez[0] = 2;

                            if(!aktivniIgrac[0] && potez[0] == 2)
                                potez[0] = 1;
                        }
                        else if(response.split(";")[0].equals("ZAVRSI_IGRU")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rezultat1.setText("0");
                                    rezultat2.setText("0");
                                    for (Button b : tabela) {
                                        b.setEnabled(true);
                                        b.setText("");
                                        b.setBackgroundColor(Color.parseColor("#000000"));
                                    }
                                }
                            });
                            PrintWriter p = NetworkSingleton.getPrintWriter();
                            p.println("VRATI_SE;");
                        }
                        else if (response.split(";")[0].equals("NAZAD")) {
                            lock[0].notify();
                            finish();
                            break;
                        }
                    }
                }
            }
        });
        t.start();
    }

    private void show(String msg){
        android.app.AlertDialog.Builder adb = new AlertDialog.Builder(SecondActivity.this);
        adb.setTitle("Nastavak igre");
        adb.setMessage(msg);
        adb.setCancelable(false);

        adb.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PrintWriter p = NetworkSingleton.getPrintWriter();
                        p.println("PROVERA;" + igrac1.getText().toString() + ":da" + ";" + aktivniIgrac[0] + ";" + potez[0]);
                    }
                }).start();
            }
        });

        adb.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PrintWriter p = NetworkSingleton.getPrintWriter();
                        p.println("PROVERA;" + igrac1.getText().toString() + ":ne");
                    }
                }).start();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }
}