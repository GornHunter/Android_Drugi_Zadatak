/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverandroid;

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pedja
 */
public class ServerAndroid {

    private ServerSocket ss;
    private PrintWriter pw;
    private BufferedReader br;
    private ArrayList<Thread> clients;
    private Map<String, ConnectedClient> users;
    private Map<String, String> test;
            

    public ServerSocket getSs() {
        return ss;
    }

    public ServerAndroid() {
        clients = new ArrayList<>();
        users = new HashMap<>();
        test = new HashMap<>();
        try {
            ss = new ServerSocket(1025);
        } catch (IOException ex) {
            Logger.getLogger(ServerAndroid.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ServerAndroid sa = new ServerAndroid();
        Socket s = null;
        Thread thr;
        while (true) {
            System.out.println("Waiting for clients...");
            try {
                s = sa.getSs().accept();
            } catch (IOException ex) {
                Logger.getLogger(ServerAndroid.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println("Client connected!");
            
            if(s != null){
                //thr = 
                new Thread(new ConnectedClient(s, sa.users)).start();
                //sa.clients.add(thr);
                //thr.start();
            }
            //System.out.println(sa.users.size());
            /*try {
                if (s != null){
                    sa.pw = new PrintWriter(s.getOutputStream(), true);
                    sa.br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                }
            } catch (IOException ex) {
                Logger.getLogger(ServerAndroid.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            
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
                                
                        sa.pw.println(ret);                        
                        System.out.println("Sending message " + ret);
                        String confirm = null;
                        try {
                            confirm = sa.br.readLine();
                        } catch (IOException ex) {
                            Logger.getLogger(ServerAndroid.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.out.println(confirm);
                    }
                }
            }*/

        }

    }

}
