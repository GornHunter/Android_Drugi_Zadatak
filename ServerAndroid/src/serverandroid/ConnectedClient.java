/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverandroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;

/**
 *
 * @author GornHunter
 */
public class ConnectedClient implements Runnable{
    private Socket socket;
    private PrintWriter pw;
    private BufferedReader br;
    private Map<String, ConnectedClient> users;
    private int[] stanje;
    private String[] ponovoIgranje;
    private boolean inGame = false;
    
    public Socket getSocket(){
        return this.socket;
    }
    
    public PrintWriter getPw(){
        return this.pw;
    }
    
    public void setInGame(boolean inGame){
        this.inGame = inGame;
    }
    
    public boolean getInGame(){
        return this.inGame;
    }
    
    public ConnectedClient(Socket socket, Map<String, ConnectedClient> users, int[] stanje, String[] ponovoIgranje){
        this.socket = socket;
        this.users = users;
        this.stanje = stanje;
        this.ponovoIgranje = ponovoIgranje;
        try {
            if (this.socket != null){
                this.pw = new PrintWriter(this.socket.getOutputStream(), true);
                this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerAndroid.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void azurirajIgrace(){
        String players = "";
        int size = 0;
                
        for(Map.Entry item : this.users.entrySet()){
                if(!this.users.get(item.getKey()).getInGame()){
                    players += item.getKey() + ";";
                    size++;
            }
        }
                
        if(players.equals("")){
            
        }
        else{
            String p = "AZURIRAJ" + ";" + size + ";" + players;
            for(Map.Entry item : this.users.entrySet()){
                this.users.get(item.getKey()).getPw().println(p.substring(0, p.length()-1));
            }
        }
    }
    
    private String provera(){
        int n;
        if((n = proveriUslovPobede(this.stanje[0], this.stanje[1], this.stanje[2])) != 0)
            return 0 + ":" + 1 + ":" + 2;
        
        if((n = proveriUslovPobede(this.stanje[3], this.stanje[4], this.stanje[5])) != 0)
            return 3 + ":" + 4 + ":" + 5;
        
        if((n = proveriUslovPobede(this.stanje[6], this.stanje[7], this.stanje[8])) != 0)
            return 6 + ":" + 7 + ":" + 8;
        
        if((n = proveriUslovPobede(this.stanje[0], this.stanje[3], this.stanje[6])) != 0)
            return 0 + ":" + 3 + ":" + 6;
        
        if((n = proveriUslovPobede(this.stanje[1], this.stanje[4], this.stanje[7])) != 0)
            return 1 + ":" + 4 + ":" + 7;
        
        if((n = proveriUslovPobede(this.stanje[2], this.stanje[5], this.stanje[8])) != 0)
            return 2 + ":" + 5 + ":" + 8;
        
        if((n = proveriUslovPobede(this.stanje[0], this.stanje[4], this.stanje[8])) != 0)
            return 0 + ":" + 4 + ":" + 8;
        
        if((n = proveriUslovPobede(this.stanje[2], this.stanje[4], this.stanje[6])) != 0)
            return 2 + ":" + 4 + ":" + 6;
        
        if(this.stanje[0] != 0 && this.stanje[1] != 0 && this.stanje[2] != 0 
           && this.stanje[3] != 0 && this.stanje[4] != 0 && this.stanje[5] != 0 
           && this.stanje[6] != 0 && this.stanje[7] != 0 && this.stanje[8] != 0)
            return "nereseno";
        
        return "";
    }
    
    private int proveriUslovPobede(int polje1, int polje2, int polje3){
        if(polje1 == polje2 && polje1 == polje3 && (polje1 != 0 && polje2 != 0 && polje3 != 0)) {
            if(polje1 == 1)
                return 1;
            else 
                return 2;
        }
        return 0;
    }

    @Override
    public void run() {
        //inicijalna komunikacija, slanje imena i njihovo cuvanje na serveru
        String username;
        String option;
        while(true){
            try{
                username = this.br.readLine();
            }
            catch(Exception e){
                e.printStackTrace();
                return;
            }
            if(!this.users.containsKey(username)){
                this.users.put(username, this);
                this.pw.println("ok;");
                
                break;
            }
            this.pw.println("no;");
        }
        
        azurirajIgrace();
        
        while(true){
            try{
                option = this.br.readLine();
            }
            catch(Exception e){
                e.printStackTrace();
                return;
            }
            
            //ovaj if je u slucaju da klijent klikne x na simualtoru ili se odjavi na drugaciji nacin od predvidjenog
            if(option == null){
                this.users.remove(username);
                azurirajIgrace();
                break;
            }
            else if(option.split(";")[0].equals("POSALJI_ZAHTEV")){
                this.users.get(option.split(";")[1]).getPw().println("PRIMI_ZAHTEV" + ";" + username + ";" + username + " ti salje zahtev za igru. Da li zelite da prihvatite?");
            }
            else if(option.split(";")[0].equals("VRATI_ODGOVOR")){
                if(option.split(";")[2].equals("ne"))
                    this.users.get(option.split(";")[1]).getPw().println("PRIMI_ODGOVOR" + ";" + option.split(";")[2] + ";" + username);
                else{
                    this.users.get(option.split(";")[1]).getPw().println("PRIMI_ODGOVOR" + ";" + option.split(";")[2] + ";" + username);
                    this.users.get(username).setInGame(true);
                    this.users.get(option.split(";")[1]).setInGame(true);
                    this.users.get(username).getPw().println("");
                    this.users.get(option.split(";")[1]).getPw().println("");
                    azurirajIgrace();
                }
            }
            else if(option.split(";")[0].equals("POSALJI_POTEZ")){
                int pozicija = Integer.parseInt(option.split(";")[1]);
                int znak = Integer.parseInt(option.split(";")[2]);
                
                this.stanje[pozicija] = znak;
                String msg = provera();
                if(msg.equals("")){
                    this.pw.println("AZURIRAJ_PODATKE;" + ";" + ";" + false + ";" + false + ";" + "a");
                    this.users.get(option.split(";")[3]).getPw().println("AZURIRAJ_PODATKE;" + pozicija + ";" + znak + ";" + true + ";" + false + ";" + "a");
                }
                else if(msg.equals("nereseno")){
                    this.pw.println("AZURIRAJ_PODATKE;" + pozicija + ";" + znak + ";" + false + ";" + false + ";" + msg);
                    this.users.get(option.split(";")[3]).getPw().println("AZURIRAJ_PODATKE;" + pozicija + ";" + znak + ";" + true + ";" + false + ";" + msg);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ConnectedClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    this.pw.println("NERESENO;" + "Neresena partija.");
                    this.users.get(option.split(";")[3]).getPw().println("NERESENO;" + "Neresena partija.");
                    Arrays.fill(this.stanje, 0);
                }
                else{
                    if(Integer.parseInt(option.split(";")[4]) + 1 == 3){
                        this.pw.println("AZURIRAJ_PODATKE;" + pozicija + ";" + znak + ";" + false + ";" + true + ";" + msg);
                        this.users.get(option.split(";")[3]).getPw().println("AZURIRAJ_PODATKE;" + pozicija + ";" + znak + ";" + true + ";" + true + ";" + msg);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ConnectedClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        this.pw.println("NASTAVAK;" + "Pobedili ste 3 runde. Da li zelite ponovo da igrate sa korisnikom " + option.split(";")[3] + "?");
                        this.users.get(option.split(";")[3]).getPw().println("NASTAVAK;" + username + " je pobedio 3 runde. Da li zelite ponovo da igrate sa njim?");
                        Arrays.fill(this.stanje, 0);
                    }
                    else{
                        this.pw.println("AZURIRAJ_PODATKE;" + pozicija + ";" + znak + ";" + false + ";" + false + ";" + msg);
                        this.users.get(option.split(";")[3]).getPw().println("AZURIRAJ_PODATKE;" + pozicija + ";" + znak + ";" + true + ";" + false + ";" + msg);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ConnectedClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        this.pw.println("POBEDA;" + "Vi ste pobedili." + ";" + false + ";" + 1);
                        this.users.get(option.split(";")[3]).getPw().println("POBEDA;" + username + " je pobedio." + ";" + true + ";" + 2);
                        Arrays.fill(this.stanje, 0);
                    }
                }
            }
            else if(option.split(";")[0].equals("PROVERA")){
                if(this.ponovoIgranje[0] == null)
                    this.ponovoIgranje[0] = option.split(";")[1];
                else
                    this.ponovoIgranje[1] = option.split(";")[1];
                
                if(this.ponovoIgranje[0] != null && this.ponovoIgranje[1] != null){
                    if(this.ponovoIgranje[0].split(":")[1].equals("da") && this.ponovoIgranje[1].split(":")[1].equals("da")){
                        this.users.get(this.ponovoIgranje[0].split(":")[0]).getPw().println("NASTAVI_IGRU;");
                        this.users.get(this.ponovoIgranje[1].split(":")[0]).getPw().println("NASTAVI_IGRU;");
                        this.ponovoIgranje[0] = null;
                        this.ponovoIgranje[1] = null;
                    }
                    else{
                       this.users.get(this.ponovoIgranje[0].split(":")[0]).getPw().println("ZAVRSI_IGRU;");
                       this.users.get(this.ponovoIgranje[1].split(":")[0]).getPw().println("ZAVRSI_IGRU;");
                       this.ponovoIgranje[0] = null;
                       this.ponovoIgranje[1] = null;
                    }
                }
            }
            //za vracanje na prvu aktivnost
            else if(option.split(";")[0].equals("VRATI_SE")){
                this.pw.println("NAZAD;");
                this.users.get(username).setInGame(false);
                azurirajIgrace();
            }
        }
        //System.out.println(username + ": Igra je prihvacena!");
    }
}