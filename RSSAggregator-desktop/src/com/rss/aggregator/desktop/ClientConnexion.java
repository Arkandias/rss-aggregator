package com.rss.aggregator.desktop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.filter.*;

import org.json.JSONObject;

public class ClientConnexion implements Runnable {


	private static int count = 0;
	private String name = "Client-";
	private String _command;
	private String _host;
	private int _port;
	

	public ClientConnexion(String host, int port){
		name += ++count;
		_host = host;
		_port = port;
	}

	public ClientConnexion(String host, int port, String userName, char[] userPwd) {
		name += ++count;
		_host = host;
		_port = port;
		connectUser(userName, userPwd);
	}

	public JSONObject connectUser(String userName, char[] userPwd) {
		try {
			
			URL obj = new URL("http://" + _host + ":" + Integer.toString(_port) + "/api/api/connectUser?user=" + userName + "&pwd="
					+ Arrays.toString(userPwd).replace(", ", "").substring(1, Arrays.toString(userPwd).replace(", ", "").length() - 1));
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// optional default is GET
			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + _host);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			JSONObject tmp = new JSONObject(response.toString());
			
			//print result
			System.out.println(response.toString());
			return tmp;
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		JSONObject tmp = new JSONObject();
		tmp.append("Success", "KO");
		return tmp;
	}

	public JSONObject connectUser(String userName, String userPwd) {
		try {
			URL obj = new URL("http://" + _host + ":" + Integer.toString(_port) + "/api/api/connectUser?user=" + userName + "&pwd=" + userPwd);
			System.out.println("\nSending 'GET' request to URL : " + obj.toString());
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// optional default is GET
			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			JSONObject tmp = new JSONObject(response.toString());
			
			//print result
			System.out.println(response.toString());
			return tmp;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		JSONObject tmp = new JSONObject();
		tmp.append("Success", "KO");
		return tmp;
	}

	public JSONObject createUser(String userName, char[] userPwd) {
		try {
			URL obj = new URL("http://" + _host + ":" + Integer.toString(_port) + "/api/api/createUser?user=" + userName + "&pwd="
					+ Arrays.toString(userPwd).replace(", ", "").substring(1, Arrays.toString(userPwd).replace(", ", "").length() - 1));
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// optional default is GET
			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + obj.toString());
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			JSONObject tmp = new JSONObject(response.toString());
			
			//print result
			System.out.println(response.toString());
			return tmp;

			// send the response
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		JSONObject tmp = new JSONObject();
		tmp.append("Success", "KO");
		return tmp;
	}
	

	public JSONObject addRSS(String userId, String rssName, String rssURL) {
		try {
			HttpURLConnection conTest = (HttpURLConnection) new URL(rssURL).openConnection();
			JSONObject resp = new JSONObject();
			if (conTest == null)
				return resp.put("Success", "KO");
			Map<String, List<String>> header = conTest.getHeaderFields();
			if (!header.get("Content-Type").contains("application/xml"))
				return resp.put("Success", "KO");
			
			org.jdom.Document document = null;
			Element racine = null;
			SAXBuilder sxb = new SAXBuilder();
		      try
		      {
		         document = sxb.build(conTest.getInputStream());
		      }
		      catch(Exception e){
					return resp.put("Success", "KO");
		      }

		      racine = document.getRootElement();
		      if (!racine.getName().equals("rss")) {
		    	  System.err.println("not start with rss");
					return resp.put("Success", "KO");
		      }
			
			URL obj = new URL("http://" + _host + ":" + Integer.toString(_port) + "/api/api/addRSS?user=" + userId + "&rssName=" + rssName + "&rssURL=" + rssURL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// optional default is GET
			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + obj.toString());
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			JSONObject tmp = new JSONObject(response.toString());
			
			//print result
			System.out.println(response.toString());
			return tmp;

			// send the response
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		JSONObject tmp = new JSONObject();
		tmp.append("Success", "KO");
		return tmp;
	}


	public JSONObject delRSS(String userId, String rssId) {
		try {
			URL obj = new URL("http://" + _host + ":" + Integer.toString(_port) + "/api/api/delRSS?user=" + userId + "&rssId=" + rssId);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// optional default is GET
			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + obj.toString());
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			JSONObject tmp = new JSONObject(response.toString());
			
			//print result
			System.out.println(response.toString());
			return tmp;
			
			// send the response
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		JSONObject tmp = new JSONObject();
		tmp.append("Success", "KO");
		return tmp;
	}
	
	public void run() {
	}
}