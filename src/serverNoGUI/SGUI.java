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
package serverNoGUI;

import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class SGUI implements ServerGUI {

	private int port;
	private String password;
	private String version = "V1.0";
	private Semaphore lock;
	
	public SGUI(String[] argv){
		this.lock = new Semaphore(1, true);
		this.lock.release();
		if (argv.length < 2)
			this.password = "";
		else
			this.password = argv[1];
		try{
		this.port = Integer.parseInt(argv[0]);
		}catch (Exception e){
			this.port = 6667;
		}
	}
	
	public SGUI(){
		this.lock = new Semaphore(1, true);
		this.lock.release();
		System.out.println("JChat Server " + this.version);
		System.out.println("...Created by Abraham Jonathan...");
		System.out.println("Please enter the port to listent [2009 by default]");
		Scanner sc = new Scanner(System.in);
		Scanner sc2 = new Scanner(System.in);
		
		int port = sc.nextInt();
		this.port = this.port <= 0 ? 6667 : port;
		System.out.println("Now enter the passphrase");
		this.password = sc2.nextLine();
		System.out.println("......Starting......");
		sc.close();
		sc2.close();
	}
	
	public int getPort() {
		return this.port;
	}

	@Override
	public void printMessage(String s) {
		System.out.println(s);
		
	}

	@Override
	public String getCommand() {
		return null;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public Semaphore getLock() {
		return this.lock;
	}

}