package com.rss.aggregator.desktop;

import java.awt.EventQueue;

import javax.lang.model.element.QualifiedNameable;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;

import java.awt.BorderLayout;
import javax.swing.JTextField;
import java.awt.List;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class ApplicationWindow {

	private JFrame _frame;
	private List _list;
	private JMenuBar _menuBar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
		_frame.setBounds(100, 100, 557, 386);
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		_list = new List();
		_list.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				// selection item changed
			}
		});
		_list.add("test1");
		_list.add("test2");
		_list.add("test3");
		_frame.getContentPane().add(_list, BorderLayout.WEST);
		
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
