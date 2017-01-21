package com.rss.aggregator.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;

/*
import ClientConnexion;
import TimeServer;
*/
public class ServerMain {
	public static void main(String[] args) throws Exception {
		ResourceBundle res = ResourceBundle.getBundle("rssAggregator.properties.config");
		String host = res.getString("rssAggreg.host");
		int port = Integer.parseInt(res.getString("rssAggreg.port"));
	    System.out.println("host :" + host);
	    RSSServer ts = new RSSServer(host, port);
	    ts.open();
	    
	    System.out.println("Serveur initialisé.");
	}
}
