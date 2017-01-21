package com.rss.aggregator.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class RSSServer {

   private int port = 2345;
   private String host = "localhost";
//   private String host = "127.0.0.1";
   private ServerSocket server = null;
   private boolean isRunning = true;
   
   public RSSServer(){
      try {
        server = new ServerSocket(port, 100, InetAddress.getByName(host));
//        server = new ServerSocket(port, 100, InetAddress.getLocalHost());
      } catch (UnknownHostException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   public RSSServer(String pHost, int pPort){
      host = pHost;
      port = pPort;
      try {
        server = new ServerSocket(port, 100, InetAddress.getByName(host));
//        server = new ServerSocket(port, 100, InetAddress.getLocalHost());
      } catch (UnknownHostException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   public void open(){
      Thread t = new Thread(new Runnable(){
         public void run(){
            while(isRunning == true){
               
               try {
                  //On attend une connexion d'un client
                  Socket client = server.accept();
                  
                  //Une fois reçue, on la traite dans un thread séparé
                  System.out.println("Connexion cliente reçue.");                  
                  Thread t = new Thread(new ClientProcessor(client));
                  t.start();
                  
               } catch (IOException e) {
                  e.printStackTrace();
               }
            }
         }
      });
      t.start();
   }
   
   public void close(){
      isRunning = false;
   }   
}