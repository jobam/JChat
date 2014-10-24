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

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public interface ClientGUI {

	String		getUsername();
	String		getHost();
	int			getPort();
	String		getPassword();
	 
	boolean		isReady();
	Semaphore 	getLock();
	String		getMessage();
	
	
	void		printMessage(int id, String message);
	void		checkUserList(ArrayList<User> users);
	
	void		restartGUI();
	
}
