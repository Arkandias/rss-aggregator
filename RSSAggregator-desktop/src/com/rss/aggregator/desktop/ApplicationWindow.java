package com.rss.aggregator.desktop;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JDialog;
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
import javax.swing.Popup;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JButton;

public class ApplicationWindow {

	private JFrame _frame;
	private List _list;
	private JMenuBar _menuBar;
	private JScrollPane _scrollPane;
	private JEditorPane _pane;
	private User _user;
	private Component horizontalGlue;
	private JButton _connectionBtn;
	private JButton _registerBtn;
//	private Map<String, String> _feedMap;
	
	public class User
	{
		public Map<String, String>	_feedMap;
		public String				account;
		
		public User() {
			this._feedMap = new HashMap<String, String>();
			this.account = new String ("");
		}
		public Map<String, String> getSubFeed() {
			return this._feedMap;
		}

		public String getAccount() {
			return this.account;
		}
		
		public void setAccount(String acc) {
			this.account = acc;
		}

		public void setSubFeed(HashMap<String, String> hashMap) {
			this._feedMap = hashMap;
		}

		public void addFeed(String name, String url) {
    		this._feedMap.put(name, url);
		}

		public void removeFeed(String name) {
			this._feedMap.remove(name);
		}

	}

	/**
	 * Launch the application.
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ApplicationWindow window = new ApplicationWindow();
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
		_frame = new JFrame();
		_list = new List();

		initialize();
		setMenu();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setList();
		_list.select(0);
		setFeedZone();
		populatePane(_list.getSelectedItem());
	}
	
	private void populatePane(String feedUrl) {
        HTMLDocument doc = (HTMLDocument) _pane.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) _pane.getEditorKit();

        try {
        	SyndFeed feeds = rssParser.getRSSContent(_user.getSubFeed().get(_list.getSelectedItem()));
			_pane.setText("");

			for (Iterator i = feeds.getEntries().iterator(); i.hasNext();) {
				SyndEntry entry = (SyndEntry) i.next();
	            editorKit.insertHTML(doc, doc.getLength(), "<h2><a href=\"" + entry.getLink() + "\">" +
	            		entry.getTitle() + "</a></h2>", 0, 0, null);
	            editorKit.insertHTML(doc, doc.getLength(), "<p>" + entry.getPublishedDate() + "</p></div>", 0, 0, null);
	            editorKit.insertHTML(doc, doc.getLength(), "<p>" + entry.getDescription().getValue() + "</p></div>", 0, 0, null);
			}
        } catch (Exception e) {
            ((Throwable) e).printStackTrace();
        }
		
	}


	private void setList() {
//		_user.setSubFeed(new HashMap<String, String>());
		/* request server to get rss and push to map*/
		_user.addFeed("rgagnon", "http://www.rgagnon.com/feed.xml");
		_user.addFeed("xkcd.com", "http://xkcd.com/rss.xml");
		_user.addFeed("inessential", "http://inessential.com/xml/rss.xml");
		
		_list.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				String feedUrl = _user.getSubFeed().get(_list.getSelectedItem());
				System.out.println("url : " + feedUrl);
				System.out.println("item : " + _list.getSelectedItem());
				populatePane(feedUrl);
			}
		});

		for (String key : _user.getSubFeed().keySet()) {
			_list.add(key);
		}

		_list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (SwingUtilities.isRightMouseButton(arg0))
				{
					JPopupMenu popMenu;
				    popMenu = new JPopupMenu();
				    drawSubMenu(popMenu);
					popMenu.show(arg0.getComponent(), arg0.getX(), arg0.getY());
					_frame.revalidate();
				}
			}
		});
		_frame.getContentPane().add(_list, BorderLayout.WEST);
	}

	private void setFeedZone() {
		_pane = new JEditorPane();
        _pane.setEditable(false);
        _pane.setContentType("text/html");

        _pane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {

                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
//                    System.out.println(e.getSourceElement());
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
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		_connectionBtn = new JButton("Connetion");
		_connectionBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				_connectUser();
			}
		});
		_registerBtn = new JButton("Cr\u00E9er un compte");
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

	private void _connectUser() {
		if (_user.getAccount().equals(""))
		{
           	JTextField name = new JTextField();
           	JTextField pwd = new JTextField();
            JPanel panel = new JPanel(new GridLayout(0, 1));
        	panel.add(new JLabel("Identifiant :"));
            panel.add(name);
        	panel.add(new JLabel("Mot de passe :"));
            panel.add(pwd);
        	int result = (int)JOptionPane.showConfirmDialog(null, panel, "Veuillez entrer votre identifiant.",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        	if (result == JOptionPane.OK_OPTION) {
        		_user.setAccount(name.getText());
        		// send to DB
				_connectionBtn.setText("Déconnection " + name.getText());
				// get users feed subscriptions and populate map
				
				_list.removeAll();
				initialize();
            } else {
                System.out.println("connexion Cancelled");
            }
		} else {
            JPanel panel = new JPanel(new GridLayout(0, 1));
        	int result = (int)JOptionPane.showConfirmDialog(null, panel, "Voulez vous déconnecter l'utilisateur : " + _user.getAccount() + " ?",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        	if (result == JOptionPane.OK_OPTION) {
        		_user.setAccount("");
        		// send to DB
				_connectionBtn.setText("Connection");
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
                System.out.println("deconnexion Cancelled");
            }
		}
	}

	private void registerModal(String string, int state) {
    	JTextField name = new JTextField(string);
    	JPasswordField pwd = new JPasswordField();
    	JPasswordField pwdConfirm = new JPasswordField();
        JPanel panel = new JPanel(new GridLayout(0, 1));
        if (state != 0) {
            JLabel error = new JLabel("Le mot de passe et la confirmation ne correspondent pas.");
            error.setForeground(Color.red);
        	panel.add(error);
        }
    	panel.add(new JLabel("Identifiant :"));
        panel.add(name);
    	panel.add(new JLabel("Mot de passe :"));
        panel.add(pwd);
    	panel.add(new JLabel("Confirmer le mot de passe :"));
        panel.add(pwdConfirm);
    	int result = (int)JOptionPane.showConfirmDialog(null, panel, "Veuillez entrer votre identifiant.",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    	if (result == JOptionPane.OK_OPTION && Arrays.equals(pwd.getPassword(), pwdConfirm.getPassword())) {
    		_user.setAccount(name.getText());
    		// send to DB
			_list.removeAll();
			initialize();
        } else if (result == JOptionPane.OK_OPTION && !(Arrays.equals(pwd.getPassword(), pwdConfirm.getPassword()))) {
        	registerModal(name.getText(), 1);
        } else {
            System.out.println("register Cancelled");
        }
	}
	private void drawSubMenu(JPopupMenu popMenu) {
		JMenuItem addSub = new JMenuItem("Add a subscription");
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
            	int result = (int)JOptionPane.showConfirmDialog(null, panel, "Ajouter un flux RSS",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            	if (result == JOptionPane.OK_OPTION) {
            		_user.addFeed(name.getText(), url.getText());
            		_list.add(name.getText());
            		// send to DB
                } else {
                    System.out.println("add sub Cancelled");
                }
            }
        });
		JMenuItem delSub = new JMenuItem("Supprimer");
		delSub.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        		_frame.revalidate();
                JPanel panel = new JPanel(new GridLayout(0, 1));
            	panel.add(new JLabel("Voulez-vous supprimer : " + _list.getSelectedItem() + " ?"));
            	int result = (int)JOptionPane.showConfirmDialog(null, panel, "Ajouter un flux RSS",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            	if (result == JOptionPane.OK_OPTION) {
            		_user.removeFeed(_list.getSelectedItem());
            		// send to DB
            		_list.remove(_list.getSelectedItem());
            		_list.select(0);
            		populatePane(_list.getSelectedItem());
                } else {
                    System.out.println("delete sub Cancelled");
                }
            }
           });
		
		popMenu.add(addSub);
		popMenu.add(delSub);
	}
}
