/*******************************************************************************
 * Copyright (c) 2014 Abraham Jonathan.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Abraham Jonathan - initial API and implementation
 ******************************************************************************/
package Client;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Client {

	private int				id;
	private ClientGUI		gui;
	private String			password;
	private String			username;
	private String			host;
	private int				port;
	private Semaphore		sendLock;

	
	private Socket			socket;
	private BufferedReader	reader;
	private PrintWriter		writer;
	private Thread			readerDaemon;
	private Thread			writerDaemon;
	private boolean			connected;
	private ArrayList<User> users;
	private	CryptAES		cryptor;
	
	
	
	public Client(ClientGUI gui){
		
		this.gui = gui;
		this.connected = false;
		this.sendLock = gui.getLock();
	}
	
	public boolean exec(){
		try {
			this.sendLock.acquire(); // la GUI release le lock au bouton connecter
			this.host = gui.getHost();
			this.port = gui.getPort();
			this.password = gui.getPassword();
			this.username = gui.getUsername();
			this.users = new ArrayList<User>();
			this.cryptor = new CryptAES(this.password);
			if (this.connect()){
				this.connected = true;
				this.gui.printMessage(000, "...OK for password and username...");
				this.readerDaemon = new Thread(new TReader());
				this.readerDaemon.start();
				this.writerDaemon = new Thread(new TWriter());
				this.writerDaemon.start();
				this.gui.printMessage(000, "...Encrypted Connection Established...");
				return true;
			}
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			this.gui.printMessage(000, "critical error, please restart the client");
			return false;
		} 
	}
	
	private boolean connect(){
		try{
			this.gui.printMessage(000, "...connecting to server...");
			this.socket = new Socket(this.host, this.port);
			this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(),"UTF-8"));
			this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			String temp = this.reader.readLine();
			if (temp.equals("000")){
				this.writer.println(this.password);
				this.writer.flush();
				temp = this.reader.readLine();
				if (!temp.equals("-1")){
					this.id = Integer.parseInt(temp);
				}
				else{
					throw new IOException("bad password");
				}
//				Checking the username
				this.writer.println(this.username);
				this.writer.flush();
				temp = this.reader.readLine();
				if (temp.equals("-1")){
					throw new IOException("Username already used");
				}
			}
			else{
				return false;
			}
		}
		catch(IOException e){
			this.gui.printMessage(000, "Unabled to connect to the server");
			this.gui.printMessage(000, e.getMessage());
			this.sendLock.release();
			return false;
		}
		
		
		return true;
	}
	
	
	
	//==================TReaderClass===============================
	
	private class TReader implements Runnable{
				
		public void run() {
			String	temp = new String("");

			while (connected && socket.isBound() && temp != null){
				try {
					socket.setSoTimeout(0);
					int tempId;
					String tempMessage;
					temp = reader.readLine();
					if (temp != null && isConforme(temp)){
						tempId = this.getId(temp);
						tempMessage = this.getMessage(temp);
						if (tempId == 0){
							this.checkCommand(tempMessage);
						}
						else{
							tempMessage = cryptor.decrypt(tempMessage);
							gui.printMessage(tempId, tempMessage);
						}
					}
					
				} catch (IOException e) {
					connected = false;
					gui.printMessage(000, "Disconnected from the server");
				} catch (GeneralSecurityException e) {
					gui.printMessage(000, "Fatal Error cannot decrypt message");
				}
				
			}
			gui.printMessage(000, "Disconnected from the server");
		}
		
		private int getId(String message){
			return Integer.parseInt(message.substring(0,3));
		}
		
		//		Ajouter ici le decryptage du message
		private String getMessage(String message){
			return message.substring(3);
		}
		
		private void checkCommand(String s){
			String[] command = s.split(" ");
			
			if (command != null){
				if (command[0].equals("USERC")){
					users.add(new User(Integer.parseInt(command[1]), command[2]));
					gui.checkUserList(users);
					gui.printMessage(000, "user " + command[2] + " is connected");
				}
			}
				if (command[0].equals("USERD")){
					for (int i = 0; i < users.size(); i++){
						User tempU = users.get(i);
						if (tempU.getId() == Integer.parseInt(command[1])){
							gui.printMessage(000, "User " + users.get(i).getName() + " is disconnected");
							users.remove(i);
							break;
						}
					}
					gui.checkUserList(users);
				}
				
				if (command[0].equals("USERL")){
					users.clear();
					for(int i = 1; i < command.length - 1; i += 2){
						users.add(new User(Integer.parseInt(command[i]), command[i + 1]));
					}
					gui.checkUserList(users);
				}
				
		}
		
		private boolean isConforme(String s){
			try{
				Integer.parseInt(s.substring(0, 3));
				return true;
			}catch (Exception e){
				return false;
			}
		}
		
		
	}
	
	
	
	
	
	//==============TWriterClas================================
	
	private class TWriter implements Runnable{

		public void run() {
			NumberFormat formatId = new DecimalFormat("000");
			while (connected){
				try {
					String temp;
					
					sendLock.acquire();
					temp = gui.getMessage();
					
					temp = cryptor.encrypt(temp);
					temp = formatId.format(id) + temp;
					writer.println(temp);
					writer.flush();
					sendLock.release();
				} catch (InterruptedException e) {
					gui.printMessage(000, "Sending message error");
				
				} catch (GeneralSecurityException e) {
					gui.printMessage(000, "Fatal Error cannot encrypt message");				
					}
			}
		}
		
	}
	
	
	//===============MAIN============================
	public static void main(String[] args) {
		try {
			GUI window = new GUI();
			window.frame.setTitle("JChat");
			window.frame.setVisible(true);
			Client client = new Client(window);
			while(!client.exec()){
				System.gc();
				window.restartGUI();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
