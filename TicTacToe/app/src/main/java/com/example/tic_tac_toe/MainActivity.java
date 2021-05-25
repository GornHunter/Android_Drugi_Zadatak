package com.example.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;

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
    Button konekcija, azuriraj, provera;
    TextView poruka, test;
    Spinner players;
    Socket[] socket;
    PrintWriter[] pw;
    BufferedReader[] br;

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

        poruka = (TextView) findViewById(R.id.tvPoruka);
        test = (TextView) findViewById(R.id.tvTest);
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

        players.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //runOnUiThread(new Runnable() {
                //    @Override
                //    public void run() {
                //        if(parent.getSelectedItem().toString().equals("")) {
                //            //nije izabran igrac, ne raditi nista
                //        }
                //        else {
                //            //izabran igrac
                //            //Toast.makeText(MainActivity.this, parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                //        }
                //    }
                //});

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(parent.getSelectedItem().toString().equals("")){

                        }
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    provera.setEnabled(false);
                                    players.setEnabled(false);
                                }
                            });
                            pw[0].println("POSALJI_ZAHTEV" + ";" + parent.getSelectedItem().toString());
                            //init(korisnickoIme);
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

                            pw[0].println(ime.getText());

                            String response = null;
                            try{
                                response = br[0].readLine();
                            }
                            catch(IOException e){
                                e.printStackTrace();
                            }

                            if(response.equals("ok")){
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
                                        Toast.makeText(MainActivity.this, "Uspesno ste se prijavili na server!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else{
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

        azuriraj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pw[0].println("AZURIRAJ");
                        String[] line = new String[1];
                        try{
                            line[0] = br[0].readLine();
                        }
                        catch(IOException e){
                            e.printStackTrace();
                        }

                        arraySpinner.subList(1, arraySpinner.size()).clear();
                        if(line[0].contains(";")) {
                            for (int i = 1; i < Integer.parseInt(line[0].split(";")[0]) + 1; i++) {
                                arraySpinner.add(line[0].split(";")[i]);
                            }
                        }
                        else if(line.equals("")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Svi igraci su u igri!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            provera.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(MainActivity.this, line[0], Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!players.isEnabled()) {
                                    provera.setEnabled(true);
                                    players.setEnabled(true);
                                }
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void init(String text){
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra(EXTRA_MESSAGE,text);
        startActivityForResult(intent,CONFIRMATION_REQUEST);
    }
}