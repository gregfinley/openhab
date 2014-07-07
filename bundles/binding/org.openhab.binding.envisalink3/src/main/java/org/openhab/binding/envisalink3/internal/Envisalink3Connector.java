/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved.  This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/Legal/epl-v10.html
 */
package org.openhab.binding.envisalink3.internal;

import java.io.IOException;
import java.io.*;
import java.lang.*;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ArrayList;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.io.PrintWriter;
import java.util.Iterator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Envisalink3 network communications connector.
 * Maintains the IP connection and reconnects on error.
 * If responses stop, the connection is reconnected.
 *
 * @author Greg Finley
 * @since 1.5.0-SNAPSHOT
 */
public class Envisalink3Connector {
	private static final Logger logger = LoggerFactory.getLogger(Envisalink3Connector.class);

	private static List<Envisalink3EventListener> _listeners = new ArrayList<Envisalink3EventListener>();

	private String ipAddress;
	private int ipPort;
	
	private Socket socket = null;
	private InputStream in = null;
	private OutputStream out = null;

	Thread inputThread = null;

	// The connectionStateCount is used to keep track of errors. It counts up by 1 for each message sent
	// and down by 2 for each message received. Thus if it ever gets "too high" then <50% of messages 
	// are being received.
	int connectionStateCount = 0;

	public Envisalink3Connector() {
	}

	public void connect(String address, int port) throws IOException {
		ipAddress = new String(address);
		ipPort = port;

		doConnect();
	}


	private void doConnect() throws IOException {
		DataInputStream inLogin = null;
		PrintWriter outLogin = null;	
		String bufferLogin = null;
		
		try {
			socket = new Socket(ipAddress, ipPort);
			in = socket.getInputStream();
			out = socket.getOutputStream();
			
			// Login 
			inLogin = new DataInputStream(in);
			bufferLogin = inLogin.readLine();
			logger.debug("Server: {}", bufferLogin);
			if (bufferLogin.equals("Login:")) {
				outLogin = new PrintWriter(out);
				logger.debug("Sending 'user'");
				outLogin.println("user");
				outLogin.flush();
			} else {
				logger.debug("Was expecting Login prompt");
				disconnect();
			}
			bufferLogin = inLogin.readLine();
			logger.debug("Server: {}", bufferLogin);
			if (!bufferLogin.equals("OK")) {
				logger.debug("Login not successful");
				disconnect();
			}
		} catch (UnknownHostException e) {
			logger.error("Can't find host: {}:{}.", ipAddress, ipPort);
		} catch (IOException e) {
			logger.error("Couldn't get I/O for the connection to: {}:{}.", ipAddress, ipPort);
			return;
		}

		

		inputThread = new InputReader(in);
		inputThread.start();

		connectionStateCount = 0;
	}

	public void disconnect() {
		if (socket == null)
			return;

		logger.debug("Interrupt connection");
		inputThread.interrupt();
	
		logger.debug("Close connection");
		try {
			out.close();
		} catch (IOException e) {
			logger.error("Error closing Envisalink3 connection: ", e.getMessage());
		}

		socket = null;
		in = null;
		out = null;
		inputThread = null;

		logger.debug("Ready");
	}


	/**
 	 * Sends a message 
	 * @param data
	 * 	Data to send
	 */
	public void sendMessage(byte[] data) {
	
	}


	public synchronized void addEventListener(Envisalink3EventListener listener) {
		_listeners.add(listener);
	}

	public synchronized void removeEventListener(Envisalink3EventListener listener) {
		_listeners.remove(listener);
	}

	/**
	 * Data receive thread 
	 */
	public class InputReader extends Thread {
		DataInputStream in;

		public InputReader(InputStream in) {
			this.in = new DataInputStream(in);
		}
	
		public void interrupt() {
			super.interrupt();
			try {
				in.close();
			} catch (IOException e) {
				logger.error("Error reading Envisalink3 connection: ", e.getMessage());
			}
		}

		public void run() {
			final int dataBufferMaxLen = 256;
			
			String buffer; 
			String cc = null;
			
			try {
				while ((buffer = in.readLine()) != null)
				{
					// Strip leading % and trailing $ from response
					buffer = buffer.substring(1, buffer.length()-1);
				
					// Message is received, send an event
					Envisalink3ResponseEvent event = new Envisalink3ResponseEvent(this);

					// Decrement the connectionStateCounter by 2
					if (connectionStateCount <= 2) 
						connectionStateCount = 0;
					else 
						connectionStateCount-=2;
					
					try {
						Iterator<Envisalink3EventListener> iterator = _listeners.iterator();

						while(iterator.hasNext()) {
							((Envisalink3EventListener)iterator.next()).packetReceived(event, buffer);
						}
					} catch (Exception e) {
						logger.error("Event listener error", e);
					}
				}
			} catch (InterruptedIOException e) {
				Thread.currentThread().interrupt();
				logger.error("Interrupted via IntterruptedIOException");
			} catch (IOException e) {
				logger.error("Reading from network failed", e);
			}

			logger.debug("Ready reading from network");
		}
	}
}
