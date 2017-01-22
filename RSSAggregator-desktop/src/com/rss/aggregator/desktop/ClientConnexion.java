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

	private static int count = 0;
	private String name = "Client-";
	private String _command;

	public ClientConnexion(String host, int port){
		name += ++count;
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

	public String connectUser(String userName, char[] userPwd) {
		try {
			writer = new PrintWriter(_connexion.getOutputStream(), true);
			reader = new BufferedInputStream(_connexion.getInputStream());
			writer.write("connect?user=" + userName + "&pwd="
			+ Arrays.toString(userPwd).replace(", ", "").substring(1, Arrays.toString(userPwd).replace(", ", "").length() - 1));
			writer.flush();
			String response = read();
			System.out.println("\t * " + name + " : Réponse reçue " + response);
			return response;
			// send the response
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return "KO";
	}

	public String connectUser(String userName, String userPwd) {
		try {
			writer = new PrintWriter(_connexion.getOutputStream(), true);
			reader = new BufferedInputStream(_connexion.getInputStream());
			writer.write("connect?user=" + userName + "&pwd=" + userPwd);
			writer.flush();
			String response = read();
			System.out.println("\t * " + name + " : Réponse reçue " + response);
			return response;
			// send the response
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return "KO";
	}

	public String createUser(String userName, char[] userPwd) {
		try {
			writer = new PrintWriter(_connexion.getOutputStream(), true);
			reader = new BufferedInputStream(_connexion.getInputStream());
			writer.write("create?user=" + userName + "&pwd="
			+ Arrays.toString(userPwd).replace(", ", "").substring(1, Arrays.toString(userPwd).replace(", ", "").length() - 1));
			writer.flush();
			System.out.println("\t Wait for response !!!");
			String response = read();
			System.out.println("\t * " + name + " : Réponse reçue " + response);
			return response;
			
			// send the response
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return "KO";
	}
	

	public String addRSS(String userId, String rssName, String rssURL) {
		try {
			writer = new PrintWriter(_connexion.getOutputStream(), true);
			reader = new BufferedInputStream(_connexion.getInputStream());
			writer.write("addRSS?user=" + userId + "&rssName=" + rssName + "&rssUrl=" + rssURL);
			writer.flush();
			String response = read();
			System.out.println("\t * " + name + " : Réponse reçue " + response);
			return response;
			// send the response
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return "KO";
	}


	public String delRSS(String userId, String rssName) {
		try {
			writer = new PrintWriter(_connexion.getOutputStream(), true);
			reader = new BufferedInputStream(_connexion.getInputStream());
			writer.write("delRSS?user=" + userId + "&rssName=" + rssName);
			writer.flush();
			String response = read();
			System.out.println("\t * " + name + " : Réponse reçue " + response);
			return response;
			
			// send the response
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return "KO";
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
				// TOUJOURS UTILISER flush() POUR ENVOYER RÉELLEMENT DES INFOS
				// AU SERVEUR
				writer.flush();

				System.out.println("Commande " + _command + " envoyée au serveur");

				// On attend la réponse
				String response = read();
				System.out.println("\t * " + name + " : Réponse reçue " + response);

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

	// Méthode qui permet d'envoyer des commandeS de façon aléatoire
//	private String getCommand() {
//		Random rand = new Random();
//		return listCommands[rand.nextInt(listCommands.length)];
//	}

	// Méthode pour lire les réponses du serveur
	private String read() throws IOException {
		String response = "";
		int stream;
		byte[] b = new byte[4096];
		stream = reader.read(b);
		response = new String(b, 0, stream);
		return response;
	}
}