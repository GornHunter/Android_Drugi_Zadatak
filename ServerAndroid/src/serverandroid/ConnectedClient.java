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
    
    public Socket getSocket(){
        return this.socket;
    }
    
    public PrintWriter getPw(){
        return this.pw;
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
                this.pw.println("ok");
                break;
            }
            this.pw.println("no");
        }
        
        //System.out.println("Broj povezanih klijenata je: " + this.users.size());
        
        try{
            option = this.br.readLine();
        }
        catch(Exception e){
            e.printStackTrace();
            return;
        }
        
        if(option.equals("AZURIRAJ")){
            String players = new String();
            Set<String> keys = this.users.keySet();
            for(String item : keys){
                if(!item.equals(username))
                    players += item + ";";
            }
            String players1 = keys.size()-1 + ";" + players;
            this.pw.println(players1.substring(0, players1.length()-1));
        }
        else if(option.equals("POSALJI_ZAHTEV")){
            
        }
        
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
