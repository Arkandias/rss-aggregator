package com.rss.aggregator.desktop;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.List;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.sun.org.apache.commons.digester.rss.Item;

public class ApplicationWindow {

	private JFrame _frame;
	private List _list;
	private JMenuBar _menuBar;
	private static Item[] _rssItems;
	private JTextArea rssTextArea;
	private JScrollPane scrollPane;
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
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		_frame = new JFrame();
		_frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		_frame.setUndecorated(false);
		_frame.setVisible(true);

		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		_list = new List();
		_list.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				try {
					_rssItems = rssParser.getContent();
					rssTextArea.setText(null);
				    for (int i = 0; i < _rssItems.length; i++) {
				    	String text = rssTextArea.getText();
				    	rssTextArea.setText(text + _rssItems[i].getTitle() + "\n");
				    	text = rssTextArea.getText();
				    	rssTextArea.setText(text + _rssItems[i].getLink() + "\n");
				    	text = rssTextArea.getText();
				    	rssTextArea.setText(text + _rssItems[i].getDescription() + "\n" + "\n");
				     }
				    rssTextArea.setCaretPosition(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		_list.add("test1");
		_list.add("test2");
		_list.add("test3");
		_frame.getContentPane().add(_list, BorderLayout.WEST);
		
		rssTextArea = new JTextArea();
		scrollPane = new JScrollPane(rssTextArea);
		_frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		_menuBar = new JMenuBar();
		JMenu menu1 = new JMenu();
		menu1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
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
