package com.rss.aggregator.server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.omg.CORBA.RepositoryIdHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

//import com.mysql.jdbc.PreparedStatement;

//import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DatabaseManager {

	private String _user;
	private String _pwd;
	private String _url;

	public DatabaseManager() throws SQLException {
		ResourceBundle res = ResourceBundle.getBundle("rssAggregator.properties.config");
		_user = res.getString("rssAggreg.user");
		_pwd = res.getString("rssAggreg.pwd");
		_url = res.getString("rssAggreg.url");

//		MysqlDataSource dataSource = new MysqlDataSource();
//		dataSource.setUser(_user);
//		dataSource.setPassword(_pwd);
//		dataSource.setServerName(_url);
	}
	
	public String checkUser(String userName, String userPwd, int req) {
		Connection connexion = null;
		PreparedStatement stmt = null;
		String id = null;
		String ret = null;
		try {
			connexion = DriverManager.getConnection(_url, _user, _pwd);
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
			else
			{
				ret = "OK:id=" + id;
				// retrieve rssIds
				List<String> rssIds = new ArrayList<String>();
				// get rss linked
				sqlQuery = "SELECT * from user_domain_assoc WHERE user_id LIKE ?";
				stmt = connexion.prepareStatement(sqlQuery);
				stmt.setString(1, id);
				res =  stmt.executeQuery();
				while (res.next())
					rssIds.add(res.getString("id"));
				stmt.close();
				// retrieve rss
				Map<String, String> rss = new HashMap<String, String>();
				// get rss linked
				sqlQuery = "SELECT * from rss_domain WHERE ";
				int a;
				for (a = 0; a < rssIds.size(); a++) {
					sqlQuery += (a > 0 ? " AND " : "");
					sqlQuery += "id Like ?";
				}
				stmt = connexion.prepareStatement(sqlQuery);
				for (int j = 0; j < rssIds.size(); j++) {
					stmt.setString(j + 1, rssIds.get(j));
				}
				res =  stmt.executeQuery();
				ret += ",rss:";
				while (res.next()) {
					ret += "title=" + res.getString("title") + "&link=" + res.getString("link") + ";";
					rss.put(res.getString("link"), res.getString("title"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				stmt.close();
				connexion.close();
			} catch (SQLException ignore) {
			}
		}
		return ret;
	}
	
	public String addUser(String userName, String userPwd) {
		
		checkUser(userName, userPwd, 1);
		Connection connexion = null;
		PreparedStatement stmt = null;
		try {
			connexion = DriverManager.getConnection(_url, _user, _pwd);
//		 	+ "VALUES ('" + paramEmail + "', MD5('" + paramMotDePasse + "'), '" + paramNom + "', NOW());" );
			String sqlQuery = "INSERT INTO user (login, password) VALUES (?, ?)";
			stmt = connexion.prepareStatement(sqlQuery);
			stmt.setString(1, userName);
			stmt.setString(2, userPwd);
			int res =  stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				stmt.close();
				connexion.close();
			} catch (SQLException ignore) {
			}
		}
		return "OK";
	}
	
	public String checkRSS(String rssName, String rssURL) {
		Connection connexion = null;
		PreparedStatement stmt = null;
		String id = null;
		try {
			connexion = DriverManager.getConnection(_url, _user, _pwd);
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
				return ("KO");			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				stmt.close();
				connexion.close();
			} catch (SQLException ignore) {
			}
		}
		return "OK:id=" + id;
	}
	
	public String addRSS(String userId, String rssName, String rssURL){
		Connection connexion = null;
		PreparedStatement stmt = null;
		String id = null;
		String response = null;
		try {
			connexion = DriverManager.getConnection(_url, _user, _pwd);
			response = checkRSS(rssName, rssURL);
			if (response.startsWith("KO"))
			{
				String sqlQuery = "INSERT INTO rsss_domain (title, link) VALUES (?, ?)";
				stmt = connexion.prepareStatement(sqlQuery);
				stmt.setString(1, rssName);
				stmt.setString(2, rssURL);
				int res =  stmt.executeUpdate();
				stmt.close();
				response = checkRSS(rssName, rssURL);
				id = response.substring(response.indexOf("="));
			}
			else
			{
				// get the id of rss if exists
				id = response.substring(response.indexOf("="));
			}
			// link user to rss
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				stmt.close();
				connexion.close();
			} catch (SQLException ignore) {
			}
		}
		return "OK";
	}
	
	public String delRSS(String userId, String rssName) {
		Connection connexion = null;
		PreparedStatement stmt = null;
		try {
			connexion = DriverManager.getConnection(_url, _user, _pwd);
			// remove link between user and rss
			
//			String sqlQuery = "SELECT * from user WHERE login LIKE ? AND password LIKE ?";
//			stmt = connexion.prepareStatement(sqlQuery);
//			stmt.setString(1, userName);
//			stmt.setString(2, rssName);
			int res =  stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				stmt.close();
				connexion.close();
			} catch (SQLException ignore) {
			}
		}
		return "OK";
	}
}
