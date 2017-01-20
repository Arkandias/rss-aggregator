package com.rss.aggregator.server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
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
	
	public String checkUser(String userName, String userPwd) {
		Connection connexion = null;
		PreparedStatement stmt = null;
		try {
			connexion = DriverManager.getConnection(_url, _user, _pwd);
			String sqlQuery = "SELECT * from user WHERE login LIKE ? AND password LIKE ?";
			stmt = connexion.prepareStatement(sqlQuery);
			stmt.setString(1, userName);
			stmt.setString(2, userPwd);
			ResultSet res =  stmt.executeQuery();
			int i = 0;
			while (res.next())
				i++;
			if (i > 0)
				return ("KO");			
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
	
	public String addUser(String userName, String userPwd) {
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
		try {
			connexion = DriverManager.getConnection(_url, _user, _pwd);
			String sqlQuery = "SELECT * from rss_domain WHERE title LIKE ? AND link LIKE ?";
			stmt = connexion.prepareStatement(sqlQuery);
			stmt.setString(1, rssName);
			stmt.setString(2, rssURL);
			ResultSet res =  stmt.executeQuery();
			int i = 0;
			while (res.next())
				i++;
			if (i > 0)
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
		return "OK";
	}
	
	public String addRSS(String userName, String rssName, String rssURL){
		Connection connexion = null;
		PreparedStatement stmt = null;
		try {
			connexion = DriverManager.getConnection(_url, _user, _pwd);
			if (checkRSS(rssName, rssURL).equals("KO"))
			{
				String sqlQuery = "INSERT INTO rsss_domain (title, link) VALUES (?, ?)";
				stmt = connexion.prepareStatement(sqlQuery);
				stmt.setString(1, rssName);
				stmt.setString(2, rssURL);
				int res =  stmt.executeUpdate();
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
	
	public String delRSS(String userName, String rssName) {
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
