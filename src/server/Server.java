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
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;


public class Server {

//	User class contains sockets, readers and writers used by the current user
//	Un reader par socket donc par client, un seul writer
	
private int					port;
private ArrayList<User>		users;
private boolean				started;
private String				password;

private	ServerSocket		socket;
private ServerGUI			gui;
private ArrayList<Thread>	readers;


//==========================================


public Server(int port, ServerGUI gui){
	
	this.port = (port < 3000 && port > 2000) ? port : 2009;
	this.gui = gui;
	this.password = this.gui.getPassword();
	this.readers = new ArrayList<Thread>();
	this.users = new ArrayList<User>();
	
	if (this.start()){
		this.started = true;
		this.run();
	}
	
}
	

private boolean start(){
	try{
	this.socket = new ServerSocket(this.port);
	}
	catch(Exception e){
		this.gui.printMessage("Error when opening the socket, is the port already used?");
		return false;
	}
	return true;
}
	

private void run(){
	Socket temp;
	this.gui.printMessage("server started on port: " + this.port);
	while (this.started){
		try {
			temp = this.socket.accept();
			this.gui.printMessage("New User connection...");
			BufferedReader	reader = new BufferedReader(new InputStreamReader(temp.getInputStream(),"UTF-8"));
			PrintWriter		writer = new PrintWriter(new OutputStreamWriter(temp.getOutputStream(), "UTF-8"));
			if (this.newUser(reader, writer, temp)){
			}	
		} catch (IOException e) {
			this.gui.printMessage("User connection error");
		}
	}
}


private boolean newUser(BufferedReader reader, PrintWriter writer, Socket socket){
	String temp;
	writer.println("000");
	writer.flush();
	try {
		int	id = this.giveId();
		temp = reader.readLine();
		if (temp.equals(this.password)){
			writer.println(id);
			writer.flush();
			String username = reader.readLine();
			if (this.checkUsername(username)){
				User user = new User(id, username, reader, writer, socket);
				this.users.add(user);
				Thread readerDaemon = new Thread(new TReader(user));
				this.readers.add(readerDaemon);
				readerDaemon.start();
				writeToUsers("000USERC " + id + " " + username);
				this.sendUserList(user);

			}
			else{
				writer.println("-1");
				writer.flush();
				this.gui.printMessage("User connection error : username already used");
				return false;
			}
			this.gui.printMessage("User " + username + " is connected");
		}
		else{
			writer.println("-1");
			writer.flush();
			this.gui.printMessage("User connection error : Wrong password");
			return false;
		}
	} catch (IOException e) {
		this.gui.printMessage("User connection error : Exception...");
		return false;
	}
	return true;
}

private boolean checkUsername(String username){
	int	i = 0;
	while (i < this.users.size()){
		
		if (this.users.get(i).getName().equals(username))
			return false;
		i++;
	}
	return true;
}

private int giveId(){
int		min;
int		i = 0;

if (this.users.isEmpty())
	return 1;
min = this.users.get(0).getId();
while (i < this.users.size()){
	if (min >= users.get(i).getId())
		min = users.get(i).getId() + 1;
	i++;
}
return min;
}


//==========================WRITING FUNCTIONS=============================


private int findUser(int userId){
	int i = 0;
	
	while (i < this.users.size()){
		if (this.users.get(i).getId() == userId)
			return i;
		i++;
	}
	return  -1;
}

private synchronized void writeToUsers(String message){
	PrintWriter		writer;
	int				i = 0;

	while (i < this.users.size()){
		writer = users.get(i).getWriter();
		writer.println(message);
		writer.flush();
		i++;
	}
}

private	void	disconnectUser(int userId){
	NumberFormat formatId = new DecimalFormat("000");
	this.writeToUsers("000USERD " + formatId.format(userId));
	this.gui.printMessage("user " + this.users.get(this.findUser(userId)).getName() +  " is disconnected");
	try {
		this.users.get(this.findUser(userId)).getSocket().close();
	} catch (IOException e) {
		gui.printMessage("Error cannot close user: " + this.findUser(userId) + " socket");
	}
	finally{
		this.users.remove(this.findUser(userId));
	}
}

private void sendUserList(User user){
	String	temp = new String("000USERL ");
	int		i = 0;
	
	while (i < this.users.size()){
		temp += users.get(i).getId() + " " + users.get(i).getName() + " ";
		i++;
	}
	user.getWriter().println(temp);
	user.getWriter().flush();
}



//=============================TREADER CLASS================================

private class TReader implements Runnable{
	private BufferedReader	reader;
	private int				userId;
	private	Socket			socket;
	
	public TReader(User user){
		this.reader = user.getReader();
		this.userId = user.getId();
		this.socket = user.getSocket();
	}
	
	public void run() {
		String temp = new String("");
		while (this.socket.isBound() && temp != null){
			try {
				this.socket.setSoTimeout(0);
				temp = this.reader.readLine();
				//check commands here (next version ;-) )
				writeToUsers(temp);
			} catch (IOException e) {
				gui.printMessage("reader error with client " + this.userId);
				disconnectUser(userId);
			}
		}
		disconnectUser(userId);
		
	}
	
}

	
	//============MAIN=============================
	public static void main(String[] args) {
		Semaphore launch;
		ServerGUI gui = new SGUI();
		launch = gui.getLock();
		try {
			launch.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		@SuppressWarnings("unused")
		Server serv = new Server(gui.getPort(),gui);
	}
	
} 

