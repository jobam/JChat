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

import java.util.concurrent.Semaphore;

public interface ServerGUI {

	int			getPort();
	void		printMessage(String s);
	String		getCommand();
	String		getPassword();
	Semaphore	getLock();
	
	
}
