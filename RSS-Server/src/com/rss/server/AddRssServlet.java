package com.rss.server;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/addRSS")
public class AddRssServlet extends HttpServlet {
    
	String _url = "jdbc:mysql://localhost:3306/rssaggregatordb";
	String _user = "root";
	String _pwd = "toor";
	
 /**
  * @see AddRssServlet#AddRSSServlet()
  */
 public AddRssServlet() {
     super();
     // TODO Auto-generated constructor stub
 }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			DatabaseManager dbMan = new DatabaseManager();
			response.getWriter().append(dbMan.addRSS(request.getParameter("user"), request.getParameter("rssName"), request.getParameter("rssUrl")));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}
