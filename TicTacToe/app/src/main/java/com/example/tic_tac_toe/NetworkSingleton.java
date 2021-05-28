package com.example.tic_tac_toe;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkSingleton {
     private static Socket socket;
     private static PrintWriter pw;
     private static BufferedReader br;
     private static Object lock;

     public static void setSocket(Socket socket){
         NetworkSingleton.socket = socket;
     }

     public static Socket getSocket(){
         return NetworkSingleton.socket;
     }

     public static void setPrintWriter(PrintWriter pw){
         NetworkSingleton.pw = pw;
     }

     public static PrintWriter getPrintWriter(){
         return NetworkSingleton.pw;
     }

     public static void setBufferedReader(BufferedReader br){
         NetworkSingleton.br = br;
     }

     public static BufferedReader getBufferedReader(){
         return NetworkSingleton.br;
     }

    public static void setLock(Object lock){
        NetworkSingleton.lock = lock;
    }

    public static Object getLock(){
        return NetworkSingleton.lock;
    }
}
