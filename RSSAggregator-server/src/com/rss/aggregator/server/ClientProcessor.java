package com.rss.aggregator.server;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;

import org.apache.commons.validator.routines.UrlValidator;

public class ClientProcessor implements Runnable {

	private Socket sock;
	private PrintWriter writer = null;
	private BufferedInputStream reader = null;
	private DatabaseManager _dbMan;

	public ClientProcessor(Socket pSock) {
		sock = pSock;
		try {
			_dbMan = new DatabaseManager();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Le traitement lancé dans un thread séparé
	public void run() {
		System.err.println("Lancement du traitement de la connexion cliente");
		boolean closeConnexion = false;
		while (!sock.isClosed()) {

			try {
				writer = new PrintWriter(sock.getOutputStream());
				reader = new BufferedInputStream(sock.getInputStream());
				// On attend la demande du client
				String response = read();
				/*
				InetSocketAddress remote = (InetSocketAddress) sock.getRemoteSocketAddress();

				String debug = "";
				debug = "Thread : " + Thread.currentThread().getName() + ". ";
				debug += "Demande de l'adresse : " + remote.getAddress().getHostAddress() + ".";
				debug += " Sur le port : " + remote.getPort() + ".\n";
				debug += "\t -> Commande reçue : " + response + "\n";
				System.err.println("\n" + debug);
*/
				String toSend = "";

				System.err.println("choose action with : " + response);
				if (response.startsWith("create"))
					toSend = createUser(response.substring(8));
				else if (response.startsWith("connect"))
					toSend = connectUser(response.substring(8));
				else if (response.startsWith("addRSS"))
					toSend = addRSS(response.substring(7));
				else if (response.startsWith("delRSS"))
					toSend = deleteRSS(response.substring(7));

				System.err.println("\nTo send : " + toSend);
				writer.write(toSend);
				writer.flush();

				if (closeConnexion) {
					System.err.println("COMMANDE CLOSE DETECTEE ! ");
					writer = null;
					reader = null;
					sock.close();
					break;
				}
			} catch (SocketException e) {
				System.err.println("LA CONNEXION A ETE INTERROMPUE ! ");
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String createUser(String userInfos) {
		String[] divInfos = userInfos.split("&");
		String userName = divInfos[0].split("=")[1];
		String userPwd = divInfos[1].split("=")[1];
		System.err.println("User to create :  " + userName + " --- " + userPwd);
		return _dbMan.addUser(userName, userPwd);
		// check if user don't exists yet and create it if not
	}

	private String connectUser(String userInfos) {
		String[] divInfos = userInfos.split("&");
		String userName = divInfos[0].split("=")[1];
		String userPwd = divInfos[1].split("=")[1];
		System.err.println("User to login :  " + userName + " --- " + userPwd);
		return "User created: " + _dbMan.checkUser(userName, userPwd);
		// get user in DB and send rss
	}

	private String addRSS(String infos) {
		String[] divInfos = infos.split("&");
		String userName = divInfos[0].split("=")[1];
		String rssName = divInfos[1].split("=")[1];
		String rssUrl = divInfos[2].split("=")[1];
		System.err.println("User add rss :  " + userName + " =>" + rssName + " -- " + rssUrl);
		// check if rss is valid
		// if it is add it to the user
		// otherwise send notification to user
		return checkRss(rssUrl) ? addRSSToDB(userName, rssName, rssUrl) : "Cannot add the rss, the URL is not valid";
	}

	private Boolean checkRss(String rssUrl){
		UrlValidator urlValidator = new UrlValidator();
		return urlValidator.isValid(rssUrl);
	}

	private String addRSSToDB(String userName, String rssName, String rssUrl) {
		_dbMan.addRSS(userName, rssName, rssUrl);
		return "RSS succesfully added.";
	}

	private String deleteRSS(String infos) {
		String[] divInfos = infos.split("&");
		String userName = divInfos[0].split("=")[1];
		String rssName = divInfos[1].split("=")[1];
		System.err.println("User del rss :  " + userName + " =>" + rssName);
		return _dbMan.delRSS(userName, rssName);
	}

	// La méthode que nous utilisons pour lire les réponses
	private String read() throws IOException {
		String response = "";
		int stream;
		byte[] b = new byte[4096];
		stream = reader.read(b);
		response = new String(b, 0, stream);
		return response;
	}
}