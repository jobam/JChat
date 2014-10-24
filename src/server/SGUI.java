/*******************************************************************************
 * Copyright (c) 2014 Abraham Jonathan.
 * All rights reserved. This program and the accompanying materials
 * are licensed under the Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 * 
 * Contributors:
 *     Abraham Jonathan - initial API and implementation
 ******************************************************************************/
package server;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SGUI implements ServerGUI {

	private JFrame frame;
	private JTextField textField;
	private JTextArea textArea;
	private JButton btnStartServer;
	private JButton btnStopServer;
	private JTextField passField;
	private JLabel lblPassword;
	private Semaphore lock;
	
	private int port;
	private String key;

	/**
	 * Create the application.
	 */
	public SGUI() {
		this.key = "";
		this.lock = new Semaphore(1, true);
		try {
			this.lock.acquire();
		} catch (InterruptedException e) {
			this.printMessage("Cannot start server, error with lock");
			System.exit(1);
		}
		initialize();
		this.frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 583, 404);
		frame.setTitle("JChat Server");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		textField = new JTextField();
		textField.setBounds(133, 6, 83, 28);
		panel.add(textField);
		textField.setColumns(10);

		JLabel lblNewLabel = new JLabel("Port : ");
		lblNewLabel.setBounds(33, 12, 97, 16);
		panel.add(lblNewLabel);

		this.btnStartServer = new JButton("Start Server");
		this.btnStartServer.setBounds(453, 7, 97, 29);
		this.btnStartServer.addActionListener(new ConnectListerner());
		panel.add(btnStartServer);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(33, 64, 517, 279);
		panel.add(scrollPane);

		this.textArea = new JTextArea();
		scrollPane.setViewportView(this.textArea);
		
		this.passField = new JTextField();
		passField.setToolTipText("(optional encryption)");
		this.passField.setBounds(299, 6, 152, 28);
		panel.add(passField);
		this.passField.setColumns(10);
		
		lblPassword = new JLabel("password :");
		lblPassword.setBounds(228, 12, 83, 16);
		panel.add(lblPassword);
		
		this.btnStopServer = new JButton("Stop Server");
		this.btnStopServer.setBounds(333, 7, 97, 29);
		this.btnStopServer.setEnabled(false);
		// panel.add(btnStopServer);
	}

	public void exec() {
		this.textArea.setText("Starting server...");
		this.btnStopServer.setEnabled(true);
	}

	// ========Connect Listener============

	private class ConnectListerner implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if (this.checkField()) {
				exec();
				textField.setEditable(false);
				btnStartServer.setEnabled(false);

			}

		}

		private boolean checkField() {

			String temp = textField.getText();
			if (temp != null && !temp.equals("")) {
				try {
					port = Integer.parseInt(temp);
				} catch (NumberFormatException e1) {
					port = 6667;
					textField.setText("6667");
				}
				if (!passField.getText().equals("")){
					key = passField.getText();
				}
				lock.release();
				return (true);
			} else {
				JOptionPane.showMessageDialog(null,
						"Fill the port field please", "Warning",
						JOptionPane.WARNING_MESSAGE);
				return (false);
			}
		}

	}
	// ================FROM ServerGUI================

	public int getPort() {
		return port;
	}

	public void printMessage(String s) {
		this.textArea.setText(this.textArea.getText() + "\n" + s);
	}
	
	public String getCommand() {
		// to be implemented
		return null;
	}

	public Semaphore getLock() {
		return this.lock;
	}

	public String getPassword() {
		return this.key;
	}

	//===============StopListener=====================
	
//	private class StopListener implements ActionListener{
//
//		public void actionPerformed(ActionEvent e) {
//			serv.stopServer();
//			btnStartServer.setEnabled(true);
//			btnStopServer.setEnabled(false);
//			textArea.setEditable(true);
//			writeMessage("Server Stopped");	
//		}
//		
//	}
	
}
