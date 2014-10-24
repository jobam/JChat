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
package Client;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Semaphore;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

public class GUI implements ClientGUI {

	JFrame frame;
	private JTextField hostfield;
	private JTextField userfield;
	private JTextField portfield;
	private JTextField textField;
	private JTextArea textArea;
	private JButton btnConnect;
	private JTextArea usersArea;
	private Semaphore lock;
	private DateFormat dateFormat;

	// =======Interface===========
	private String username;
	private String host;
	private String sendmessage;
	private String password = "";
	private int port;
	private JScrollPane scrollPane;
	private boolean started;
	private ArrayList<User> users;
	private JPasswordField passfield;

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
		this.sendmessage = "";
		this.lock = new Semaphore(1, true);
		this.started = false;
		this.users = new ArrayList<User>();
		this.dateFormat = new SimpleDateFormat("HH:mm");
		try {
			this.lock.acquire();
		} catch (InterruptedException e) {
		}

	}

	/*
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setIconImage(new ImageIcon(this.getClass()
				.getResource("logo.png")).getImage());
		frame.setBounds(100, 100, 820, 451);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.addWindowListener(new CloseListener());

		hostfield = new JTextField();
		hostfield.setBounds(49, 6, 134, 28);
		frame.getContentPane().add(hostfield);
		hostfield.setColumns(10);

		JLabel lblHost = new JLabel("Host:");
		lblHost.setBounds(6, 12, 61, 16);
		frame.getContentPane().add(lblHost);

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(195, 12, 75, 16);
		frame.getContentPane().add(lblUsername);

		userfield = new JTextField();
		userfield.setBounds(266, 6, 75, 28);
		frame.getContentPane().add(userfield);
		userfield.setColumns(10);

		portfield = new JTextField();
		portfield.setBounds(390, 6, 53, 28);
		frame.getContentPane().add(portfield);
		portfield.setColumns(10);

		JLabel lblPort = new JLabel("Port:");
		lblPort.setBounds(342, 12, 61, 16);
		frame.getContentPane().add(lblPort);

		this.btnConnect = new JButton("Connect");
		btnConnect.setBounds(669, 7, 117, 29);
		btnConnect.addActionListener(new Connect());
		frame.getContentPane().add(btnConnect);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(49, 68, 530, 280);
		scrollPane.setVisible(true);
		frame.getContentPane().add(scrollPane);

		this.textArea = new JTextArea();
		DefaultCaret caret = (DefaultCaret) this.textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textArea.setTabSize(4);
		textArea.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		textArea.setLineWrap(true);
		textArea.setBounds(0, 0, 519, 266);
		this.textArea.setEditable(false);
		this.textArea.setMargin(new Insets(2, 2, 2, 2));
		this.scrollPane.setViewportView(textArea);

		textField = new JTextField();
		textField.setBounds(49, 360, 434, 28);
		textField.addKeyListener(new EnterKey());
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		JButton btnSend = new JButton("Send");
		btnSend.setBounds(519, 360, 83, 29);
		btnSend.addActionListener(new Send());
		frame.getContentPane().add(btnSend);

		JLabel lblConnectedUsers = new JLabel("Connected Users");
		lblConnectedUsers.setBounds(591, 68, 122, 16);
		frame.getContentPane().add(lblConnectedUsers);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(601, 99, 94, 248);
		frame.getContentPane().add(scrollPane_1);

		this.usersArea = new JTextArea();
		this.usersArea.setEditable(false);
		scrollPane_1.setViewportView(this.usersArea);

		passfield = new JPasswordField();
		passfield.setToolTipText("optional for encryption");
		passfield.setBounds(523, 6, 134, 28);
		frame.getContentPane().add(passfield);
		passfield.setColumns(10);

		JLabel lblPassword = new JLabel("password:");
		lblPassword.setBounds(453, 12, 83, 16);
		frame.getContentPane().add(lblPassword);

		this.hostfield.setText("joker42.com");
		this.portfield.setText("6667");

	}

	private void writeUsers() {
		int i = 1;
		if (!this.users.isEmpty()) {
			this.usersArea.setText(this.users.get(0).getName());
			while (i < this.users.size()) {
				this.usersArea.setText(this.usersArea.getText() + "\n"
						+ this.users.get(i).getName());
				i++;
			}
		}
	}

	private int findUser(int userId) {
		int i = 0;

		while (i < this.users.size()) {
			if (this.users.get(i).getId() == userId)
				return i;
			i++;
		}
		return -1;
	}

	// ===============Connect Listener============
	private class Connect implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (this.checkfields()) {
				username = userfield.getText();
				username = username.replaceAll(" ", "_");
				host = hostfield.getText();
				password = String.valueOf(passfield.getPassword());
				try {
					port = Integer.parseInt(portfield.getText());
				} catch (NumberFormatException e1) {
					port = 6667;
					portfield.setText("6667");
				}
				btnConnect.setEnabled(false);
				userfield.setEditable(false);
				hostfield.setEditable(false);
				portfield.setEditable(false);
				passfield.setEditable(false);
				lock.release(); // permet de lancer le client
			}
		}

		boolean checkfields() {
			if (!hostfield.getText().equals("")
					&& !userfield.getText().equals("")
					&& !portfield.getText().equals(""))
				return (true);
			JOptionPane.showMessageDialog(null, "Fill all the fields please",
					"Warning", JOptionPane.WARNING_MESSAGE);
			return (false);
		}
	}

	public void enableConnection() {
		this.btnConnect.setEnabled(true);
		this.userfield.setEditable(true);
		this.hostfield.setEditable(true);
		this.portfield.setEditable(true);
		this.passfield.setEditable(true);
	}

	// ===============Send Listener============

	private class Send implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (!textField.getText().equals("")) {
				sendmessage = textField.getText();
				sendMessage();
			}
		}
	}

	// ============EnterKey Listener================

	private class EnterKey implements KeyListener {

		public void keyTyped(KeyEvent e) {

		}

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (!textField.getText().equals("")) {
					sendmessage = textField.getText();
					sendMessage();
				}
			}

		}

		public void keyReleased(KeyEvent e) {
		}

	}

	// =========class CloseListener===================

	private class CloseListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			try {
				if (started)
					printMessage(000, "closing");
			} catch (Exception f) {
			}
			System.exit(0);
		}
	}

	// ===========FROM ClientGUI======================

	public String getUsername() {
		return this.username;
	}

	public String getHost() {
		return this.host;
	}

	public int getPort() {
		return this.port;
	}

	public Semaphore getLock() {
		return this.lock;
	}

	public synchronized void sendMessage() {
		this.sendmessage = this.textField.getText();
		this.textField.setText("");
		this.lock.release();
		try {
			this.lock.acquire();
		} catch (InterruptedException e) {
		}
	}

	public String getPassword() {
		return this.password;
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMessage() {
		return this.sendmessage;
	}

	@Override
	public void printMessage(int id, String message) {
		Date date = new Date();
		String temp = new String("[" + this.dateFormat.format(date) + "]  ");
		if (id == 0) {
			temp = temp + "Server: " + message;
		} else
			temp = temp + this.users.get(findUser(id)).getName() + ": "
					+ message;
		if (this.textArea.getText().equals(""))
			this.textArea.setText(temp);
		else
			this.textArea.setText(this.textArea.getText() + "\n" + temp);

	}

	@Override
	public void checkUserList(ArrayList<User> users) {
		this.users = users;
		this.usersArea.setText("");
		this.writeUsers();
	}

	public void restartGUI() {
		try {
			this.lock.acquire();
		} catch (InterruptedException e) {
		}
		this.enableConnection();
	}
}
