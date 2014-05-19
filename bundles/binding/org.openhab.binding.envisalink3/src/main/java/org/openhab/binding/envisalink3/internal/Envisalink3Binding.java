/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.envisalink3.internal;

import java.util.Dictionary;
import java.util.ArrayList;
import java.util.List;
import java.util.EventObject;

import org.openhab.binding.envisalink3.Envisalink3BindingProvider;

import org.apache.commons.lang.StringUtils;
import org.openhab.core.binding.AbstractActiveBinding;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
	

/**
 * Implement this class if you are going create an actively polling service
 * like querying a Website/Device.
 * 
 * @author Greg Finley
 * @since 1.5.0
 */
public class Envisalink3Binding extends AbstractActiveBinding<Envisalink3BindingProvider> implements ManagedService {

	private static final Logger logger = 
		LoggerFactory.getLogger(Envisalink3Binding.class);

	// The IP Address of the envisalink3
	private String ipAddress;
	
	// The Port of the envisalink3
	private int ipPort;

	// The default port to use if none if configured 
	private final int defaultIpPort = 4025;

	private MessageListener eventListener = new MessageListener();
	// private Envisalink3Connector connector = null;
	
	/** 
	 * the refresh interval which is used to poll values from the Envisalink3
	 * server (optional, defaults to 60000ms)
	 */
	private long refreshInterval = 60000;
	
	
	public Envisalink3Binding() {
	}
		
	
	public void activate() {
		logger.debug("Envisalink3 binding activated");
		super.activate();
	}
	
	public void deactivate() {
		logger.debug("Envisalink3 binding deactivated");
		stopListening();	
	}

	private void listen() {
		stopListening();

		/**
		connector = new Envisalink3Connector();
		if (connector != null) {
			// Initialize the IP connection
			connector.addEventListener(eventListener);
			try {
				connector.connect(ipAddress, ipPort);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		**/
	}


	private void stopListening() {
		/**
		if (connector != null)
		{
			connector.disconnect();
			connector.removeEventListener(eventListener);
			connector = null;
		}
		**/
	}
		
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected long getRefreshInterval() {
		return refreshInterval;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	protected String getName() {
		return "Envisalink3 Refresh Service";
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void execute() {
		//logger.debug("execute() method is called!");
		logger.debug("Envisalink3 execute() method is called");
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void internalReceiveCommand(String itemName, Command command) {
		// the code being executed when a command was sent on the openHAB
		// event bus goes here. This method is only called if one of the 
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveCommand() is called!");
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void internalReceiveUpdate(String itemName, State newState) {
		// the code being executed when a state was sent on the openHAB
		// event bus goes here. This method is only called if one of the 
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveCommand() is called!");
	}
		
	/**
	 * @{inheritDoc}
	 */
	@Override
	public void updated(Dictionary<String, ?> config) throws ConfigurationException {
		logger.debug("Envisalink3 updated() method is called!");

		if (config != null) {
			
			// to override the default refresh interval one has to add a 
			// parameter to openhab.cfg like <bindingName>:refresh=<intervalInMs>
			String refreshIntervalString = (String) config.get("refresh");
			if (StringUtils.isNotBlank(refreshIntervalString)) {
				refreshInterval = Long.parseLong(refreshIntervalString);
			}
		
			String hostConfig = (String) config.get("host");
			if (StringUtils.isNotBlank(hostConfig)) {
				logger.debug("Setting Envisalink3 host=" + (String)hostConfig);
				ipAddress = hostConfig;
			}

			String hostPort = (String) config.get("port");
			if (StringUtils.isNotBlank(hostPort)) {
				logger.debug("Setting Envisalink3 port=" + (String)hostPort);
				ipPort = Integer.parseInt(hostPort);
			}

			// Start the listener
			listen(); 

			setProperlyConfigured(true);
		}
	}
	
	/**
  	 * Received incoming packets 
	 */
	private class MessageListener implements Envisalink3EventListener {
	
		public void packetReceived(EventObject event, byte[] packet) {

		}

			
	}
}
