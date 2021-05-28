package com.example.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "igor";
    public final static int CONFIRMATION_REQUEST = 1;

    EditText ipAdresa, port, ime;
    Button konekcija, azuriraj, provera, potvrda;
    TextView poruka;
    Spinner players;
    Socket[] socket;
    PrintWriter[] pw;
    BufferedReader[] br;
    Thread[] t = new Thread[1];
    Thread[] t2 = new Thread[1];

    String korisnickoIme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipAdresa = (EditText) findViewById(R.id.etIPAdresa);
        port = (EditText) findViewById(R.id.etPort);
        ime = (EditText) findViewById(R.id.etIme);

        konekcija = (Button) findViewById(R.id.btnConnect);
        azuriraj = (Button) findViewById(R.id.btnAzuriraj);
        provera = (Button) findViewById(R.id.btnProvera);
        potvrda = (Button) findViewById(R.id.btnPotvrda);

        poruka = (TextView) findViewById(R.id.tvPoruka);
        players = (Spinner) findViewById(R.id.sPlayers);

        List<String> arraySpinner = new ArrayList<>();
        arraySpinner.add("");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, arraySpinner);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        players.setAdapter(adapter);
        players.setEnabled(false);

        socket = new Socket[1];
        pw = new PrintWriter[1];
        br = new BufferedReader[1];
        boolean[] gotovo = new boolean[1];
        gotovo[0] = false;
        Object[] lock = new Object[1];
        lock[0] = 1;

        t[0] = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock[0]) {
                    while (true) {
                        BufferedReader[] b = new BufferedReader[1];
                        b[0] = br[0];
                        String response;
                        try {
                            response = b[0].readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }

                        if (response.split(";")[0].equals("AZURIRAJ")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "AZURIRAJ!", Toast.LENGTH_SHORT).show();
                                    arraySpinner.subList(1, arraySpinner.size()).clear();
                                    if (!response.split(";")[1].equals("-1")) {
                                        for (int i = 2; i < Integer.parseInt(response.split(";")[1]) + 2; i++) {
                                            if (!response.split(";")[i].equals(korisnickoIme))
                                                arraySpinner.add(response.split(";")[i]);
                                        }
                                    }
                                }
                            });
                        } else if (response.equals("")) {
                            arraySpinner.subList(1, arraySpinner.size()).clear();
                            try {
                                lock[0].wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                return;
                            }
                        } else if (response.split(";")[0].equals("PRIMI_ZAHTEV")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                                    adb.setTitle("Potvrda za igru");
                                    adb.setMessage(response.split(";")[2]);
                                    adb.setCancelable(false);

                                    adb.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            //players.setEnabled(true);
                                                            //players.setSelection(0);
                                                            //azuriraj.setEnabled(true);
                                                            //provera.setEnabled(true);
                                                            //potvrda.setEnabled(false);

                                                            //Toast.makeText(MainActivity.this, "Kliknuli ste da!", Toast.LENGTH_SHORT).show();
                                                            //pokreniIgru(response.split(";")[1] + ";" + korisnickoIme + ";" + ";" + false + ";" + false);
                                                        }
                                                    });
                                                    //gotovo[0] = true;
                                                    pokreniIgru(response.split(";")[1] + ";" + korisnickoIme + ";" + ";" + false + ";" + false);
                                                    PrintWriter p = pw[0];
                                                    p.println("VRATI_ODGOVOR" + ";" + response.split(";")[1] + ";" + "da");
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
                                                    PrintWriter p = pw[0];
                                                    p.println("VRATI_ODGOVOR" + ";" + response.split(";")[1] + ";" + "ne");
                                                }
                                            }).start();
                                        }
                                    });

                                    AlertDialog ad = adb.create();
                                    ad.show();
                                }
                            });
                        } else if (response.split(";")[0].equals("PRIMI_ODGOVOR")) {
                            if (response.split(";")[1].equals("da")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Toast.makeText(MainActivity.this, response.split(";")[2] + " je prihvatio zahtev za igru.", Toast.LENGTH_SHORT).show();
                                        //pokreniIgru(korisnickoIme + ";" + response.split(";")[2] + ";" + true + ";" + ";" + false);
                                    }
                                });
                                //gotovo[0] = true;
                                pokreniIgru(korisnickoIme + ";" + response.split(";")[2] + ";" + true + ";" + ";" + false);
                                //PrintWriter p = pw[0];
                                //p.println("POKRENI_IGRU");
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Toast.makeText(MainActivity.this, response.split(";")[2] + " je odbio zahtev za igru.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                }
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Zavrsen thread!", Toast.LENGTH_SHORT).show();
                    }
                });*/
            }
        });

        players.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(parent.getSelectedItem().toString().equals("")){

                        }
                        else{
                            PrintWriter p = pw[0];
                            p.println("POSALJI_ZAHTEV" + ";" + parent.getSelectedItem().toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    players.setSelection(0);
                                    //provera.setEnabled(false);
                                    //players.setEnabled(false);
                                    //potvrda.setEnabled(true);
                                }
                            });
                        }
                    }
                }).start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        konekcija.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ipAdresa.getText().toString().equals("") && !port.getText().toString().equals("") && !ime.getText().toString().equals("")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //socket[0] = null;
                            try {
                                if(socket[0] == null)
                                    socket[0] = new Socket(ipAdresa.getText().toString(), Integer.parseInt(port.getText().toString()));
                            } catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }

                            //pw[0] = null;
                            //br[0] = null;
                            try {
                                if(pw[0] == null && br[0] == null) {
                                    pw[0] = new PrintWriter(socket[0].getOutputStream(), true);
                                    br[0] = new BufferedReader(new InputStreamReader(socket[0].getInputStream()));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            NetworkSingleton.setSocket(socket[0]);
                            NetworkSingleton.setPrintWriter(pw[0]);
                            NetworkSingleton.setBufferedReader(br[0]);
                            NetworkSingleton.setLock(lock[0]);

                            pw[0].println(ime.getText());

                            String[] response = new String[1];
                            try{
                                response[0] = br[0].readLine();
                            }
                            catch(IOException e){
                                e.printStackTrace();
                            }

                            if(response[0].split(";")[0].equals("ok")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        azuriraj.setEnabled(true);
                                        konekcija.setEnabled(false);

                                        ipAdresa.setText("");
                                        ipAdresa.setEnabled(false);

                                        port.setText("");
                                        port.setEnabled(false);

                                        korisnickoIme = ime.getText().toString();
                                        poruka.setText("Vase korisnicko ime je " + korisnickoIme);

                                        ime.setText("");
                                        ime.setEnabled(false);

                                        players.setEnabled(true);
                                        azuriraj.setEnabled(true);
                                        provera.setEnabled(true);

                                        Toast.makeText(MainActivity.this, "Uspesno ste se prijavili na server!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                t[0].start();
                            } else if(response[0].split(";")[0].equals("no")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "Uneseno korisnicko ime vec postoji!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }
                else{
                    Toast.makeText(MainActivity.this, "Morate uneti podatke u sva polja!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void pokreniIgru(String text){
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra(EXTRA_MESSAGE,text);
        startActivityForResult(intent,CONFIRMATION_REQUEST);
    }
}