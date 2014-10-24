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
import java.io.PrintWriter;
import java.net.Socket;

public class User {

	private int 			id;
	private String 			name;
	private BufferedReader	reader;
	private PrintWriter		writer;
	private Socket			socket;
	
	public User(int id, String name){
		this.setId(id);
		this.setName(name);
	}
	
	public User(int id, String name, BufferedReader reader, PrintWriter writer, Socket socket){
		this(id, name);
		this.reader = reader;
		this.writer = writer;
		this.socket = socket;
	}

	//============GETTERS===========================
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public Socket	getSocket(){
		return this.socket;
	}
	
	public BufferedReader	getReader(){
		return this.reader;		
	}
	
	public PrintWriter		getWriter(){
		return this.writer;
	}
	
	
	
	//=============SETTERS=====================
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setSocket(Socket s){
		this.socket = s;
	}
	
	public void setReader(BufferedReader r){
		this.reader = r;
	}

	public void setWriter(PrintWriter w){
		this.writer = w;
	}
	
	
	
	
	
	
	
	
	
}
