package com.rss.aggregator.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
/*
import ClientConnexion;
import TimeServer;
*/
public class ServerMain {
	public static void main(String[] args) throws Exception {

		String host = "127.0.0.1";
	    int port = 2345;
	    
	    RSSServer ts = new RSSServer(host, port);
	    ts.open();
	    
	    System.out.println("Serveur initialisé.");
	}
}
