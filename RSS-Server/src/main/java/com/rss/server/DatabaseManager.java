package com.rss.server;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;



public class DatabaseManager {

	private String _user = "root";
	private String _pwd = "toor";
	private String _url = "jdbc:mysql://localhost:3306/rssaggregatordb";

	Properties prop = new Properties();
	InputStream input = null;

	public DatabaseManager() throws SQLException, IOException {
		DriverManager.registerDriver(new com.mysql.jdbc.Driver ());
		input = getClass().getClassLoader().getResourceAsStream("config.properties");
		if (input != null)
		{
			prop.load(input);
			_user = prop.getProperty("rssAggreg.user");
			_pwd = prop.getProperty("rssAggreg.pwd");
			_url = prop.getProperty("rssAggreg.url");
		}
	}
	
	public String checkUser(String userName, String userPwd, int req) {
		Connection connexion = null;
		PreparedStatement stmt = null;
		String id = null;
		JSONObject response = new JSONObject();
		
		try {
			connexion = DriverManager.getConnection(_url, _user, _pwd);
			if (connexion == null)
			{
				response.put("Success", "KO");
				return response.toString();
			}
			String sqlQuery = "SELECT * from user WHERE login LIKE ? AND password LIKE ?";
			stmt = connexion.prepareStatement(sqlQuery);
			stmt.setString(1, userName);
			stmt.setString(2, userPwd);
			ResultSet res =  stmt.executeQuery();
			int i = 0;
			while (res.next()) {
				id = res.getString("id");
				i++;
			}
			stmt.close();
			if (req > 0)
				return (i > 0 ? "KO" : "OK");
			else if (req == 0 && i == 0)
			{
				response.put("Success", "KO");
				return response.toString();
			}
			else
			{
				response.put("Success", "OK");
				response.put("userId", id);
				response.put("rss", getLinkedRss(id, connexion));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (stmt != null)
					stmt.close();
				if (connexion != null)
					connexion.close();
			} catch (SQLException ignore) {
			}
		}
		return response.toString();
	}
	
	private JSONArray getLinkedRss(String userId, Connection connexion) throws SQLException {
		JSONArray rssArr = new JSONArray();
		String sqlQuery = null;
		PreparedStatement stmt = null;
		// retrieve rssIds
		sqlQuery = "SELECT * FROM rss_domain RSS, user_domain_assoc ASSOC WHERE ASSOC.user_id = ? AND ASSOC.rss_domain_id = RSS.id";
		
//		sqlQuery = "SELECT * from user_domain_assoc WHERE user_id LIKE ?";
		stmt = connexion.prepareStatement(sqlQuery);
		stmt.setString(1, userId);
		ResultSet res =  stmt.executeQuery();
		while (res.next()) {
			JSONObject rss = new JSONObject();
			rss.put("rssId", res.getString("id"));
			rss.put("rssName", res.getString("title"));
			rss.put("rssLink", res.getString("link"));
			rssArr.put(rss);
		}
		return rssArr;
	}

	public String addUser(String userName, String userPwd) {
		
		checkUser(userName, userPwd, 1);
		JSONObject response = new JSONObject();
		Connection connexion = null;
		PreparedStatement stmt = null;
		try {
			connexion = DriverManager.getConnection(_url, _user, _pwd);
			if (connexion == null)
			{
				response.put("Success", "KO");
				return response.toString();
			}
			String sqlQuery = "INSERT INTO user (id, login, password) VALUES (NULL, ?, ?)";
			stmt = connexion.prepareStatement(sqlQuery);
			stmt.setString(1, userName);
			stmt.setString(2, userPwd);
			int res =  stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (stmt != null)
					stmt.close();
				if (connexion != null)
					connexion.close();
			} catch (SQLException ignore) {
			}
		}
		response.put("Success", "OK");
		return response.toString();
	}
	
	public JSONObject checkRSS(String rssName, String rssURL) {
		Connection connexion = null;
		PreparedStatement stmt = null;
		String id = null;
		JSONObject response = new JSONObject();
		try {
			connexion = DriverManager.getConnection(_url, _user, _pwd);
			if (connexion == null)
			{
				response.put("Success", "KO");
				return response;
			}
			String sqlQuery = "SELECT * from rss_domain WHERE title LIKE ? AND link LIKE ?";
			stmt = connexion.prepareStatement(sqlQuery);
			stmt.setString(1, rssName);
			stmt.setString(2, rssURL);
			ResultSet res =  stmt.executeQuery();
			int i = 0;
			while (res.next()) {
				id = res.getString("id");
				i++;
			}
			if (i == 0)
			{
				response.put("Success", "KO");
				return (response);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (stmt != null)
					stmt.close();
				if (connexion != null)
					connexion.close();
			} catch (SQLException ignore) {
			}
		}
		response.put("Success", "OK");
		response.put("rssId", id);
		return response;
	}
	
	public String addRSS(String userId, String rssName, String rssURL){
		Connection connexion = null;
		PreparedStatement stmt = null;
		String rssId = null;
		System.out.println("rssName : " + rssName);
		System.out.println("rssURL : " + rssURL);
		JSONObject response = new JSONObject();
		try {
			connexion = DriverManager.getConnection(_url, _user, _pwd);
			if (connexion == null)
			{
				response.put("Success", "KO");
				return response.toString();
			}
			if (response.has("Sucess"))
				response.remove("Success");
			response = checkRSS(rssName, rssURL);
			if (response.get("Success").equals("KO"))
			{
				String sqlQuery = "INSERT INTO rss_domain (id, title, link) VALUES (NULL, ?, ?)";
				stmt = connexion.prepareStatement(sqlQuery);
				stmt.setString(1, rssName);
				stmt.setString(2, rssURL);
				int res =  stmt.executeUpdate();
				stmt.close();
				response = checkRSS(rssName, rssURL);
			}
			// get the id of rss if exists
			rssId = response.getString("rssId");
			if (!checkIfAlreadyLinked(userId, rssId))
			{
				String sqlQuery = "INSERT INTO user_domain_assoc (id, user_id, rss_domain_id) VALUES (NULL, ?, ?)";
				stmt = connexion.prepareStatement(sqlQuery);
				stmt.setString(1, userId);
				stmt.setString(2, rssId);
				stmt.executeUpdate();
			}
			response.put("Success", "OK");
			response.put("rss", getLinkedRss(userId, connexion));
			// link user to rss
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (stmt != null)
					stmt.close();
				if (connexion != null)
					connexion.close();
			} catch (SQLException ignore) {
			}
		}
		return response.toString();
	}
	
	private boolean checkIfAlreadyLinked(String userId, String rssId) {
		Connection connexion = null;
		PreparedStatement stmt = null;
		try {
			connexion = DriverManager.getConnection(_url, _user, _pwd);
			if (connexion == null)
			{
				return false;
			}
			String sqlQuery = "SELECT * FROM user_domain_assoc WHERE user_id = ? AND rss_domain_id = ?";
			stmt = connexion.prepareStatement(sqlQuery);
			stmt.setString(1, userId);
			stmt.setString(2, rssId);
			ResultSet res = stmt.executeQuery();
			int i = 0;
			while (res.next())
				i++;
			return i > 0 ? true : false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String delRSS(String userId, String rssId) {
		Connection connexion = null;
		PreparedStatement stmt = null;
		JSONObject response = new JSONObject();
		try {
			connexion = DriverManager.getConnection(_url, _user, _pwd);
			if (connexion == null)
			{
				response.put("Success", "KO");
				return response.toString();
			}
			// remove link between user and rss
			
			String sqlQuery = "DELETE FROM user_domain_assoc WHERE user_domain_assoc.user_id = ? AND user_domain_assoc.rss_domain_id = (SELECT id FROM rss_domain WHERE rss_domain.id = ?)";
			stmt = connexion.prepareStatement(sqlQuery);
			stmt.setString(1, userId);
			stmt.setString(2, rssId);
			int res =  stmt.executeUpdate();
			response.put("Success", "OK");
			response.put("rss", getLinkedRss(userId, connexion));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (stmt != null)
					stmt.close();
				if (connexion != null)
					connexion.close();
			} catch (SQLException ignore) {
			}
		}
		return response.toString();
	}
}
