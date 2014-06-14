/**
 * Copyright (r) 2010-2014, openHAB..org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/Legal/epl-v10.html
 */
package org.openhab.binding.envisalink3.internal.board;



import java.math.BigInteger;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.ContactItem;
import org.openhab.core.library.types.OpenClosedType;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Envisalink3Message {
	private static final Logger logger = LoggerFactory.getLogger(Envisalink3Message.class);
	private String message;
	private String cc;
	private CommandCode commandc;
	private String partition;
	private String ledBitField;
	private String alphaField;
	private String zoneField;
	private String partitionField;

	// Store status 
	private int[] zoneStatus;
	private int zone1Bypass;
	private String alphaContent;


	public Envisalink3Message(String data) {
		message = data;
		zoneStatus = new int[32];
	
		// Parse the message
		if (message.length() >= 2) {
			cc = message.substring(0,2);
			logger.debug("CC: {}", cc);
			if (cc.equals("00"))
				commandc = CommandCode.KEYPAD_UPDATE;
			else if (cc.equals("01"))
				commandc = CommandCode.ZONE_STATE_CHANGE;
			else if (cc.equals("02"))
				commandc = CommandCode.PARTITION_STATE_CHANGE;
			else if (cc.equals("03"))
				commandc = CommandCode.CID_EVENT;
			else if (cc.equals("FF"))
				commandc = CommandCode.ZONE_TIMER_DUMP;
			else 
				commandc = null;
		} else {
			cc = null;
			commandc = null;
			logger.debug("Invalid command code");
		}
		switch (commandc) {
			case KEYPAD_UPDATE: 
				// Virtual keypad Update
				ledBitField = message.substring(6,10);
				alphaField = message.substring(17, message.length());
				
				logger.debug("ledBitField: {}", ledBitField);
				logger.debug("alphaField: {}", alphaField);
				break;
			case ZONE_STATE_CHANGE:
				// Zone State Change
				zoneField = message.substring(3, message.length());
				String zoneGroup1 = zoneField.substring(0,2);
				for (int i = 0; i < 8; i++) {
					if (checkZone(zoneGroup1, i))
						setZoneStatus(i, true);
					else
						setZoneStatus(i, false);
				}
			
				String zoneGroup2 = zoneField.substring(2,4);
				for (int i = 0; i < 8; i++) {
					if (checkZone(zoneGroup2, i))
						setZoneStatus(9+i, true);
					else
						setZoneStatus(9+i, false);
				}
				// Other zone groups
				break;
			case PARTITION_STATE_CHANGE:
				// Patition State Change
				partitionField = message.substring(4, message.length());
				break;
			case CID_EVENT:
				// Realtime CID Event
				break;
			case ZONE_TIMER_DUMP:
				// Envisalink Zone Timer Dump
				break;
			default:
				break;
		}
	}

	private boolean checkZone(String zoneData, int zoneNum) {
		BigInteger bInt = new BigInteger(zoneData, 16);
		if (bInt.testBit(zoneNum))
			return true;
		else
			return false;
	}

	private void setZoneStatus(int zoneNum, boolean zoneValue) {
		if (zoneValue == true)
			zoneStatus[zoneNum] = 1;
		else
			zoneStatus[zoneNum] = 0;
	}

	public boolean getZoneStatus(int zoneNum) {
		if (zoneStatus[zoneNum] == 1)
			return true;
		else 
			return false;
	}
	
	public State getZoneState(Class<? extends Item> itemType, int zoneNum) {
		if (itemType == ContactItem.class)
		{
			if (zoneStatus[zoneNum] == 1) {
				return OpenClosedType.OPEN;
			} else {
				return OpenClosedType.CLOSED;
			}	
		} else {
			return OpenClosedType.CLOSED;
		}
	}

	public boolean isZoneChange() {
		if (cc.equals("01"))
			return true;
		else
			return false;
	}
		
	public enum CommandCode {
		KEYPAD_UPDATE, ZONE_STATE_CHANGE, PARTITION_STATE_CHANGE, CID_EVENT, ZONE_TIMER_DUMP 	
	}
}

				
