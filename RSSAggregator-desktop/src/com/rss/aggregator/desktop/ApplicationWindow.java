package com.rss.aggregator.desktop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

public class ApplicationWindow {

	private JFrame _frame;
	private List _list;
	private JMenuBar _menuBar;
	private JScrollPane _scrollPane;
	private JEditorPane _pane;
	private Component horizontalGlue;
	private JButton _connectionBtn;
	private JButton _registerBtn;

	private User _user;

	public class User {
		public Map<String, List> _feedMap;
		public String account;
		public String pwd;
		public String id;

		public User() {
			this._feedMap = new HashMap<String, List>();
			this.account = new String("");
		}

		public Map<String, List> getSubFeed() {
			return this._feedMap;
		}

		public String getAccount() {
			return this.account;
		}

		public void setAccount(String acc) {
			this.account = acc;
		}

		public void setSubFeed(HashMap<String, List> hashMap) {
			this._feedMap = hashMap;
		}

		public void addFeed(String name, String url, String id) {
			List li = new List();
			li.add(id);
			li.add(url);
			this._feedMap.put(name, li);
		}

		public void removeFeed(String name) {
			this._feedMap.remove(name);
		}

	}

	private int _port;// = 8080;
	private String _host;// = "127.0.0.1";
	private ClientConnexion _cliCon;

	/**
	 * Launch the application.
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ApplicationWindow window = new ApplicationWindow();
					window._frame.setMinimumSize(new Dimension(600, 600));
					window._frame.setIconImage(
							ImageIO.read(getClass().getResource("/resources/rss.png")).getScaledInstance(25, 25, 3));
					window._frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ApplicationWindow() {
		_user = new User();
		_list = new List();
		getConfig();
		_frame = new JFrame();
		_list.setFont(new Font("Dialog", Font.BOLD, 12));
		_list.setForeground(Color.LIGHT_GRAY);
		_list.setBackground(Color.DARK_GRAY);

		initialize();
		setMenu();
	}

	private void getConfig() {
		ResourceBundle res = ResourceBundle.getBundle("rssAggregator.properties.config");
		_host = res.getString("rssAggreg.host");
		_port = Integer.parseInt(res.getString("rssAggreg.port"));
		_cliCon = new ClientConnexion(_host, _port);
		if (res.containsKey("rssAggreg.userName")) {
			_user.account = res.getString("rssAggreg.userName");
			_user.pwd = res.getString("rssAggreg.userPwd");
			_user.id = res.getString("rssAggreg.userId");
			
			JSONObject response = _cliCon.connectUser(_user.account, _user.pwd);
			System.out.println("response : " + response.toString());
			if (response.has("Success") && response.getString("Success").equals("OK"))
			{
				_list.removeAll();
				if (response.has("rss"))
					setUserSubFeed(response);				
			}
			else if (response.has("Success") && response.getString("Success").equals("KO"))
			{
				messageInfo("Could not join the server or connect the user.");
				_user.account = "";
				_user.pwd = "";
				_user.id = "";
			}
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setList();
		_list.select(0);
		setFeedZone();
		if (!_user._feedMap.isEmpty())
			populatePane(_list.getSelectedItem());
	}

	private void populatePane(String feedUrl) {
		HTMLDocument doc = (HTMLDocument) _pane.getDocument();
		HTMLEditorKit editorKit = (HTMLEditorKit) _pane.getEditorKit();

		try {
			SyndFeed feeds = null;
			if (!_user.account.isEmpty())
				feeds = rssParser.getRSSContent(_user.getSubFeed().get(_list.getSelectedItem()).getItem(1));

			_pane.setText("");
			if (feeds != null) {
				for (Iterator<?> i = feeds.getEntries().iterator(); i.hasNext();) {
					SyndEntry entry = (SyndEntry) i.next();
					editorKit.insertHTML(doc, doc.getLength(),
							"<h2><a href=\"" + entry.getLink() + "\">" + entry.getTitle() + "</a></h2>", 0, 0, null);
					editorKit.insertHTML(doc, doc.getLength(), "<div><p>" + entry.getPublishedDate() + "</p>", 0, 0,
							null);
					editorKit.insertHTML(doc, doc.getLength(), "<p>" + entry.getDescription().getValue() + "</p></div>",
							0, 0, null);
				}
			}
		} catch (Exception e) {
			messageInfo("Unable to display feeds.");
			((Throwable) e).printStackTrace();
		}
	}

	private void setList() {
		/* request server to get rss and push to map */
		/*
		 * _user.addFeed("rgagnon", "http://www.rgagnon.com/feed.xml");
		 * _user.addFeed("xkcd.com", "http://xkcd.com/rss.xml");
		 * _user.addFeed("inessential", "http://inessential.com/xml/rss.xml");
		 */
		_list.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				String feedUrl = _user.getSubFeed().get(_list.getSelectedItem()).getItem(1);
				populatePane(feedUrl);
			}
		});

		if (!_user.account.isEmpty()) {
			for (String key : _user.getSubFeed().keySet()) {
				_list.add(key);
			}
		}

		_list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (SwingUtilities.isRightMouseButton(arg0)) {
					JPopupMenu popMenu;
					popMenu = new JPopupMenu();
					drawSubMenu(popMenu);
					popMenu.show(arg0.getComponent(), arg0.getX(), arg0.getY());
					if (_user.id.isEmpty() || _user.account.isEmpty())
						popMenu.setEnabled(false);
					_frame.revalidate();
				}
			}
		});
		_frame.getContentPane().add(_list, BorderLayout.WEST);
	}

	private void setFeedZone() {
		_pane = new JEditorPane();
		_pane.setBackground(Color.GRAY);
		_pane.setEditable(false);
		_pane.setContentType("text/html");

		_pane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {

				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					// System.out.println(e.getSourceElement());
					if (e.getURL() != null) {
						try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
				}
			}
		});

		DefaultCaret caret = (DefaultCaret) _pane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		_scrollPane = new JScrollPane(_pane);
		_frame.getContentPane().add(_scrollPane, BorderLayout.CENTER);
		_frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
		});
		_frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		_frame.setVisible(true);
	}

	private void setMenu() {
		_menuBar = new JMenuBar();
		JMenu menu1 = new JMenu();
		menu1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				_frame.revalidate();
			}
		});
		JMenu menu2 = new JMenu();
		menu2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});

		menu1.setText("File");
		JMenuItem menuItem = new JMenuItem("Exit");
		menuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				System.exit(0);
			}
		});
		menu1.add(menuItem);
		menu2.setText("Other");

		horizontalGlue = Box.createHorizontalGlue();
		if (_user.account.isEmpty())
			_connectionBtn = new JButton("Log in");
		else
			_connectionBtn = new JButton("Log out " + _user.account);
		try {
			Image img = ImageIO.read(getClass().getResource("/resources/user-login.png"));
			_connectionBtn.setIcon(new ImageIcon(img.getScaledInstance(25, 25, 3)));
		} catch (Exception ex) {
			System.out.println(ex);
		}
		_connectionBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				_connectModal();
			}
		});
		_registerBtn = new JButton("Create account");
		try {
			Image img = ImageIO.read(getClass().getResource("/resources/user-add.png"));
			_registerBtn.setIcon(new ImageIcon(img.getScaledInstance(25, 25, 3)));
		} catch (Exception ex) {
			System.out.println(ex);
		}
		_registerBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				registerModal("", 0);
			}
		});
		_menuBar.add(menu1);
		_menuBar.add(menu2);
		_menuBar.add(horizontalGlue);
		_menuBar.add(_connectionBtn);
		_frame.setJMenuBar(_menuBar);
		_menuBar.add(_registerBtn);
	}

	private void _connectModal() {
		if (_user.getAccount().equals("")) {
			JTextField name = new JTextField();
			JPasswordField pwd = new JPasswordField();
			JPanel panel = new JPanel(new GridLayout(0, 1));
			panel.add(new JLabel("Login :"));
			panel.add(name);
			panel.add(new JLabel("password :"));
			panel.add(pwd);
			int result = (int) JOptionPane.showConfirmDialog(null, panel, "Enter your credentials.",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {
				JSONObject response = _cliCon.connectUser(name.getText(), pwd.getPassword());
				if (response.get("Success").equals("OK"))
				{
					_list.removeAll();
					if (response.has("rss"))
						setUserSubFeed(response);				
				}
				if (response.getString("Success").equals("OK")) {
					// user connected
					_user.setAccount(name.getText());
					_user.pwd = Arrays.toString(pwd.getPassword()).replace(", ", "").substring(1,
							Arrays.toString(pwd.getPassword()).replace(", ", "").length() - 1);
					_connectionBtn.setText("Déconnection " + name.getText());
					// set user id with the response
					_user.id = response.getString("userId");
					File inputFile = new File("src/rssAggregator/properties/config.properties");
					File tempFile = new File(inputFile.getAbsolutePath() + ".tmp");

					BufferedReader reader = null;
					BufferedWriter writer = null;
					try {
						reader = new BufferedReader(new FileReader(inputFile));
						writer = new BufferedWriter(new FileWriter(tempFile));

						String lineUserName = "rssAggreg.userName";
						String lineUserId = "rssAggreg.userId";
						String lineUserPwd = "rssAggreg.userPwd";
						String currentLine;

						while ((currentLine = reader.readLine()) != null) {
							// trim newline when comparing with lineToRemove
							String trimmedLine = currentLine.trim();
							if (trimmedLine.startsWith(lineUserId) || trimmedLine.startsWith(lineUserName)
									|| trimmedLine.startsWith(lineUserPwd))
								continue;
							writer.write(currentLine + System.getProperty("line.separator"));
						}
						writer.write("rssAggreg.userName=" + _user.account + System.getProperty("line.separator"));
						writer.write("rssAggreg.userPwd=" + _user.pwd + System.getProperty("line.separator"));
						writer.write("rssAggreg.userId=" + _user.id + System.getProperty("line.separator"));
						writer.close();
						reader.close();
						if (!inputFile.delete()) {
							System.out.println("Could not delete file");
							return;
						}

						// Rename the new file to the filename the original file
						// had.
						if (!tempFile.renameTo(inputFile))
							System.out.println("Could not rename file");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// get users feed subscriptions and populate map with the
					// response
					_list.removeAll();
					if (response.has("rss"))
						setUserSubFeed(response);
					initialize();
					messageInfo("User successfully connected.");
				} else {
					messageInfo("User could not be connected.");
				}

			} else {
				System.out.println("connexion Cancelled");
			}
		} else {
			JPanel panel = new JPanel(new GridLayout(0, 1));
			int result = (int) JOptionPane.showConfirmDialog(null, panel,
					"Disconnect user: " + _user.getAccount() + " ?",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {
				_user.setAccount("");
				// send to DB
				_connectionBtn.setText("Log in");
				// reset feed zone
				_pane.removeAll();
				try {
					_pane.getDocument().remove(0, _pane.getDocument().getLength());
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				// clear subscription map
				_user._feedMap.clear();
				_list.removeAll();
				_frame.revalidate();
			} else {
				System.out.println("log out Cancelled");
			}
		}
	}

	private void setUserSubFeed(JSONObject response) {
		JSONArray rssArray = response.getJSONArray("rss");
		_user._feedMap.clear();
		for (int i = 0; i < rssArray.length(); i++)
		{
			JSONObject rss = rssArray.getJSONObject(i);
			List li = new List();
			li.add(rss.getString("rssId"));
			li.add(rss.getString("rssLink"));
			_user._feedMap.put(rss.getString("rssName"), li);
		}
	}

	private void registerModal(String string, int state) {
		JTextField name = new JTextField(string);
		JPasswordField pwd = new JPasswordField();
		JPasswordField pwdConfirm = new JPasswordField();
		JPanel panel = new JPanel(new GridLayout(0, 1));
		if (state != 0) {
			JLabel error = new JLabel("password and confirmation does not match.");
			error.setForeground(Color.red);
			panel.add(error);
		}
		panel.add(new JLabel("Login :"));
		panel.add(name);
		panel.add(new JLabel("Password :"));
		panel.add(pwd);
		panel.add(new JLabel("Confirm your password :"));
		panel.add(pwdConfirm);
		int result = (int) JOptionPane.showConfirmDialog(null, panel, "Enter your credentials.",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION && Arrays.equals(pwd.getPassword(), pwdConfirm.getPassword())) {
			// send to DB
			JSONObject response = _cliCon.createUser(name.getText(), pwd.getPassword());
			if (response.getString("Success").equals("OK")) {
				// user created
				messageInfo("User successfully created");
			} else {
				// user was not create
				messageInfo("Could nor create user");
			}
			_list.removeAll();
			initialize();
		} else if (result == JOptionPane.OK_OPTION && !(Arrays.equals(pwd.getPassword(), pwdConfirm.getPassword()))) {
			registerModal(name.getText(), 1);
		} else {
			System.out.println("register Cancelled");
		}
	}

	private void messageInfo(String msg) {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(new JLabel(msg));
		JOptionPane.showConfirmDialog(null, panel, "Information", JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE);
	}

	private void drawSubMenu(JPopupMenu popMenu) {
		JMenuItem addSub = new JMenuItem("Add a subscription");
		try {
			Image img = ImageIO.read(getClass().getResource("/resources/rss-add.png"));
			addSub.setIcon(new ImageIcon(img.getScaledInstance(25, 25, 3)));
		} catch (Exception ex) {
			System.out.println(ex);
		}
		addSub.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_frame.revalidate();
				JTextField name = new JTextField();
				JTextField url = new JTextField();
				JPanel panel = new JPanel(new GridLayout(0, 1));
				panel.add(new JLabel("Name :"));
				panel.add(name);
				panel.add(new JLabel("URL :"));
				panel.add(url);
				int result = (int) JOptionPane.showConfirmDialog(null, panel, "Add an RSS",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (result == JOptionPane.OK_OPTION) {
					// send to DB
					System.err.println("userId : " + _user.id);
					
					JSONObject response = _cliCon.addRSS(_user.id, name.getText(), url.getText());
					if (response.getString("Success").equals("KO")) {
						messageInfo("The feed could not be added");
					} else {
						_list.removeAll();
						if (response.has("rss"))
							setUserSubFeed(response);
						initialize();
					}
				} else {
					System.out.println("add sub Cancelled");
				}
			}
		});
		JMenuItem delSub = new JMenuItem("Remove a subscription");
		try {
			Image img = ImageIO.read(getClass().getResource("/resources/rss-remove.png"));
			delSub.setIcon(new ImageIcon(img.getScaledInstance(25, 25, 3)));
		} catch (Exception ex) {
			System.out.println(ex);
		}
		delSub.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_frame.revalidate();
				JPanel panel = new JPanel(new GridLayout(0, 1));
				panel.add(new JLabel("Remove rss: " + _list.getSelectedItem() + " ?"));
				int result = (int) JOptionPane.showConfirmDialog(null, panel, "Remove an RSS",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (result == JOptionPane.OK_OPTION) {
					// send to DB
					JSONObject response = _cliCon.delRSS(_user.id, _user._feedMap.get(_list.getSelectedItem()).getItem(0));
					if (response.getString("Success").equals("KO")) {
						messageInfo("The feed could not be removed");
					} else {
						_list.removeAll();
						if (response.has("rss"))
							setUserSubFeed(response);
						initialize();
					}
				} else {
					System.out.println("delete sub Cancelled");
				}
			}
		});
		if (_user.id.isEmpty() || _user.account.isEmpty())
		{
			addSub.setEnabled(false);
			delSub.setEnabled(false);
		}
		popMenu.add(addSub);
		popMenu.add(delSub);
	}
}
