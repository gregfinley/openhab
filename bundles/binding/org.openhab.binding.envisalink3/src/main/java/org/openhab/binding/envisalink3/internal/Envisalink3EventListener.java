/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/Legal/epl-v10.html
 */
package org.openhab.binding.envisalink3.internal;

import java.util.EventListener;
import java.util.EventObject;
	
/**
 * This interface defines an interface to receive data from the Envisalink3 board.
 *
 * @author Greg Finley
 * @since 1.5.0-SNAPSHOT
 */
public interface Envisalink3EventListener extends EventListener {

	/**
	 * Receive data from the Envisalink3 board.
	 *
 	 * @param data
	 */
	void packetReceived(EventObject event, String data);
}

