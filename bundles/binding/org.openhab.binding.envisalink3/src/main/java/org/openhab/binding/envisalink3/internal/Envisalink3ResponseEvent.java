/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/Legal/epl-v10.html
 */
package org.openhab.binding.envisalink3.internal;

import java.util.EventObject;


/**
 * The Listener interface for receiving data from Envisalink3 connector.
 *
 * @author Greg Finley
 * @since 1.5.0-SNAPSHOT
 */
public class Envisalink3ResponseEvent extends EventObject {
	private static final long serialVersionUID = 3821740012020068392L;

	public Envisalink3ResponseEvent(Object source) {
		super(source);
	}


	/**
	 * Invoked when data message is received from Envisalink3 board.
 	 *
	 * @param packet 
	 * 	Data from board.
	 */
	public void DataReceivedEvent(byte[] packet) {
	}
}
