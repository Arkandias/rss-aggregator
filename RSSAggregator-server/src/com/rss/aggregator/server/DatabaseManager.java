package com.rss.aggregator.server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
			else if (req == 0 && i == 0)
				return "KO";
			else
			{
				ret = "OK:id=" + id;
				ret += getLinkedRss(id, connexion);
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
	
	private String getLinkedRss(String userId, Connection connexion) throws SQLException {
		String ret = null;
		String sqlQuery = null;
		PreparedStatement stmt = null;
		// retrieve rssIds
		List<String> rssIds = new ArrayList<String>();
		// get rss linked
		sqlQuery = "SELECT * FROM rss_domain RSS, user_domain_assoc ASSOC WHERE ASSOC.user_id = ? AND ASSOC.rss_domain_id = RSS.id";

//		sqlQuery = "SELECT * from user_domain_assoc WHERE user_id LIKE ?";
		stmt = connexion.prepareStatement(sqlQuery);
		stmt.setString(1, userId);
		ResultSet res =  stmt.executeQuery();
		ret = ",rss[";
		while (res.next()) {
			ret += "id=" + res.getString("id") + "&title=" + res.getString("title") + "&link=" + res.getString("link") + ";";
		}
		return ret;
	}

	public String addUser(String userName, String userPwd) {
		
		checkUser(userName, userPwd, 1);
		Connection connexion = null;
		PreparedStatement stmt = null;
		try {
			connexion = DriverManager.getConnection(_url, _user, _pwd);
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
		String rssId = null;
		String response = null;
		String ret = null;
		try {
			connexion = DriverManager.getConnection(_url, _user, _pwd);
			response = checkRSS(rssName, rssURL);
			if (response.startsWith("KO"))
			{
				String sqlQuery = "INSERT INTO rss_domain (title, link) VALUES (?, ?)";
				stmt = connexion.prepareStatement(sqlQuery);
				stmt.setString(1, rssName);
				stmt.setString(2, rssURL);
				int res =  stmt.executeUpdate();
				stmt.close();
				response = checkRSS(rssName, rssURL);
			}
			// get the id of rss if exists
			rssId = response.substring(response.indexOf("=") + 1);
			if (!checkIfAlreadyLinked(userId, rssId))
			{
				String sqlQuery = "INSERT INTO user_domain_assoc (user_id, rss_domain_id) VALUES (?, ?)";
				stmt = connexion.prepareStatement(sqlQuery);
				stmt.setString(1, userId);
				stmt.setString(2, rssId);
				stmt.executeUpdate();
			}
			ret = "OK:";
			ret += getLinkedRss(userId, connexion);
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
		return ret;
	}
	
	private boolean checkIfAlreadyLinked(String userId, String rssId) {
		Connection connexion = null;
		PreparedStatement stmt = null;
		try {
			connexion = DriverManager.getConnection(_url, _user, _pwd);
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
		String ret = "";
		try {
			connexion = DriverManager.getConnection(_url, _user, _pwd);
			// remove link between user and rss
			
			String sqlQuery = "DELETE FROM user_domain_assoc WHERE user_domain_assoc.user_id = ? AND user_domain_assoc.rss_domain_id = (SELECT id FROM rss_domain WHERE rss_domain.id = ?)";
			stmt = connexion.prepareStatement(sqlQuery);
			stmt.setString(1, userId);
			stmt.setString(2, rssId);
			int res =  stmt.executeUpdate();
			ret = "OK:";
			ret += getLinkedRss(userId, connexion);
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
		return ret;
	}
}
