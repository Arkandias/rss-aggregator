package com.rss.aggregator.desktop;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.List;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import com.sun.org.apache.commons.digester.rss.Item;

public class ApplicationWindow {

	private JFrame _frame;
	private List _list;
	private JMenuBar _menuBar;
	private static Item[] _rssItems;
	private JScrollPane _scrollPane;
	private JEditorPane _pane;
	private Map<String, String> _feedMap;

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
		initialize();
		_list.select(0);
		populatePane(_list.getSelectedItem());
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		_frame = new JFrame();

		setList();
		setFeedZone();
		setMenu();
	}
	
	private void populatePane(String feedUrl) {
        HTMLDocument doc = (HTMLDocument) _pane.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) _pane.getEditorKit();

        try {
			_rssItems = rssParser.getContent(_feedMap.get(_list.getSelectedItem()));
			_pane.setText("");
		    for (int i = 0; i < _rssItems.length; i++) {
	            editorKit.insertHTML(doc, doc.getLength(), "<h2><a href=\"" + _rssItems[i].getLink() + "\">" +
	            											_rssItems[i].getTitle() + "</a></h2>", 0, 0, null);
	            editorKit.insertHTML(doc, doc.getLength(), "<p>" + _rssItems[i].getDescription() + "</p></div>", 0, 0, null);
            }
        } catch (Exception e) {
            ((Throwable) e).printStackTrace();
        }
		
	}


	private void setList() {
		_list = new List();
		_list.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				String feedUrl = _feedMap.get(_list.getSelectedItem());
				populatePane(feedUrl);
			}
		});
		
		_feedMap = new HashMap<String, String>();
		/* request server to get rss and push to map*/
		_feedMap.put("rgagnon", "http://www.rgagnon.com/feed.xml");
		_feedMap.put("xkcd.com", "http://xkcd.com/rss.xml");
		
		for (String key : _feedMap.keySet())
		{
			_list.add(key);
		}

		_list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (SwingUtilities.isRightMouseButton(arg0))
				{
					JPopupMenu popMenu = new JPopupMenu();
					JMenuItem addSub = new JMenuItem("Add a subscription");
					popMenu.add(addSub);
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
	
		_menuBar.add(menu1);
		_menuBar.add(menu2);
		_frame.setJMenuBar(_menuBar);
	}
}
