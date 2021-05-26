package com.example.tic_tac_toe;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkSingleton {
     private static Socket socket;
     private static PrintWriter pw;
     private static BufferedReader br;
     private static String msg;

     public static void setSocket(Socket socket){
         com.example.tic_tac_toe.NetworkSingleton.socket = socket;
     }

     public static Socket getSocket(){
         return com.example.tic_tac_toe.NetworkSingleton.socket;
     }

     public static void setPrintWriter(PrintWriter pw){
         com.example.tic_tac_toe.NetworkSingleton.pw = pw;
     }

     public static PrintWriter getPrintWriter(){
         return com.example.tic_tac_toe.NetworkSingleton.pw;
     }

     public static void setBufferedReader(BufferedReader br){
         com.example.tic_tac_toe.NetworkSingleton.br = br;
     }

     public static BufferedReader getBufferedReader(){
         return com.example.tic_tac_toe.NetworkSingleton.br;
     }

     public static void setMsg(String msg){
         NetworkSingleton.msg = msg;
     }

     public static String getMsg(){
         return NetworkSingleton.msg;
     }
}
