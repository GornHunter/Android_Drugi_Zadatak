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
    
    public ConnectedClient(Socket socket, Map<String, ConnectedClient> users){
        this.socket = socket;
        this.users = users;
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
        
        //System.out.println("Broj povezanih klijenata je: " + this.users.size());
        
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
                break;
            }
            else if(option.equals("AZURIRAJ")){
                //this.users.get(username).setInGame(false);
                /*String players = new String();
                Map<String, Boolean> data = new HashMap<>();
            
                for(Map.Entry item : this.users.entrySet()){
                    data.put((String)item.getKey(), ((ConnectedClient)this.users.get(item.getKey())).getInGame());
                }
            
                int size = 0;
                for(Map.Entry item : data.entrySet()){
                    if(!item.getKey().equals(username)){
                        if(!(boolean)item.getValue()){
                            players += item.getKey() + ";";
                            size++;
                        }
                    }
                }
            
                String playersFull = "";
                if(!players.equals("")){
                    playersFull = size + ";" + players;
                    this.pw.println(playersFull.substring(0, playersFull.length()-1));*
                }
                else
                    this.pw.println(playersFull);*/
            }
            else if(option.split(";")[0].equals("POSALJI_ZAHTEV")){
                //System.out.println("Msg sent to: " + option.split(";")[1]);
                //this.users.get(option.split(";")[1]).getPw().println(username + " said hello!");
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
                    //break;
                }
            }
            else if(option.split(";")[0].equals("GO_BACK")){
                this.users.get(username).setInGame(false);
                azurirajIgrace();
            }
            else if(option.equals("POKRENI_IGRU")){
                this.users.get(username).setInGame(true);
                break;
            }
        }
        System.out.println(username + ": Igra je prihvacena!");
        
        /*String username;
        try{
            username = this.br.readLine();
        }
        catch(Exception e){
            e.printStackTrace();
            return;
        }
        this.users.put(username, this);
        
        String target;
        while(true){
            try{
                target = this.br.readLine();
            }
            catch(Exception e){
                e.printStackTrace();
                return;
            }
            
            ConnectedClient cc = this.users.get(target);
            cc.getPw().println(username);
        }*/
        
        //while(true){
            /*for(int i = 0; i < 2; i++){
                for (int j = 0; j < 2; j++){
                    for (int k = 0; k < 2; k++){
                        String ret = "";
                        ret+= i;
                        ret+=",";
                        ret+=j;
                        ret+=",";
                        ret+=k;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ServerAndroid.class.getName()).log(Level.SEVERE, null, ex);
                        }
                                
                        this.pw.println(ret);                        
                        System.out.println("Sending message " + ret);
                        String confirm = null;
                        try {
                            confirm = this.br.readLine();
                        } catch (IOException ex) {
                            Logger.getLogger(ServerAndroid.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.out.println(confirm);
                    }
                }
            }*/
        //}
    }  
}
