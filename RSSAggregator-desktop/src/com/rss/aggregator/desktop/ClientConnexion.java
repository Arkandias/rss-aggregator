package com.rss.aggregator.desktop;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

public class ClientConnexion implements Runnable {

	private Socket _connexion = null;
	private PrintWriter writer = null;
	private BufferedInputStream reader = null;

	// Notre liste de commandes. Le serveur nous r�pondra diff�remment selon la
	// commande utilis�e.
//	private String[] listCommands = { "FULL", "DATE", "HOUR", "NONE" };
	private static int count = 0;
	private String name = "Client-";
	private String _command;
	private int _req;

	public ClientConnexion(String host, int port){//, int req, String command) {
		name += ++count;
//		_req = req;
//		_command = command;
		try {
			_connexion = new Socket(host, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ClientConnexion(String host, int port, String userName, char[] userPwd) {
		name += ++count;
		try {
			_connexion = new Socket(host, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		connectUser(userName, userPwd);
	}

	public void connectUser(String userName, char[] userPwd) {
		try {
			writer = new PrintWriter(_connexion.getOutputStream(), true);
			reader = new BufferedInputStream(_connexion.getInputStream());
			writer.write("connect?user=" + userName + "&pwd="
			+ Arrays.toString(userPwd).replace(", ", "").substring(1, Arrays.toString(userPwd).replace(", ", "").length() - 1));
			writer.flush();
			String response = read();
			System.out.println("\t * " + name + " : R�ponse re�ue " + response);
			
			// send the response
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void createUser(String userName, char[] userPwd) {
		try {
			writer = new PrintWriter(_connexion.getOutputStream(), true);
			reader = new BufferedInputStream(_connexion.getInputStream());
			writer.write("create?user=" + userName + "&pwd="
			+ Arrays.toString(userPwd).replace(", ", "").substring(1, Arrays.toString(userPwd).replace(", ", "").length() - 1));
			writer.flush();
			System.out.println("\t Wait for response !!!");
			String response = read();
			System.out.println("\t * " + name + " : R�ponse re�ue " + response);
			
			// send the response
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	

	public void addRSS(String userName, String rssName, String rssURL) {
		try {
			writer = new PrintWriter(_connexion.getOutputStream(), true);
			reader = new BufferedInputStream(_connexion.getInputStream());
			writer.write("create?user=" + userName + "&rssName=" + rssName + "&rssUrl=" + rssURL);
			writer.flush();
			String response = read();
			System.out.println("\t * " + name + " : R�ponse re�ue " + response);
			
			// send the response
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}


	public void delRSS(String userName, String rssName) {
		try {
			writer = new PrintWriter(_connexion.getOutputStream(), true);
			reader = new BufferedInputStream(_connexion.getInputStream());
			writer.write("create?user=" + userName + "&rssName=" + rssName);
			writer.flush();
			String response = read();
			System.out.println("\t * " + name + " : R�ponse re�ue " + response);
			
			// send the response
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void run() {
		// nous n'allons faire que 10 demandes par thread...
		for (int i = 0; i < 10; i++) {
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {

				writer = new PrintWriter(_connexion.getOutputStream(), true);
				reader = new BufferedInputStream(_connexion.getInputStream());
				// On envoie la commande au serveur

//				String commande = getCommand();
				writer.write(_command);
				// TOUJOURS UTILISER flush() POUR ENVOYER R�ELLEMENT DES INFOS
				// AU SERVEUR
				writer.flush();

				System.out.println("Commande " + _command + " envoy�e au serveur");

				// On attend la r�ponse
				String response = read();
				System.out.println("\t * " + name + " : R�ponse re�ue " + response);

			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		writer.write("CLOSE");
		writer.flush();
		writer.close();
	}

	// M�thode qui permet d'envoyer des commandeS de fa�on al�atoire
//	private String getCommand() {
//		Random rand = new Random();
//		return listCommands[rand.nextInt(listCommands.length)];
//	}

	// M�thode pour lire les r�ponses du serveur
	private String read() throws IOException {
		String response = "";
		int stream;
		byte[] b = new byte[4096];
		stream = reader.read(b);
		response = new String(b, 0, stream);
		return response;
	}
}