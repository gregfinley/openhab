/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.envisalink3;

import java.util.List;

import org.openhab.core.binding.BindingProvider;
import org.openhab.binding.envisalink3.internal.board.Envisalink3Board.Functions;
import org.openhab.core.items.Item;

/**
 * @author Greg Finley
 * @since 1.5.0
 */
public interface Envisalink3BindingProvider extends BindingProvider {

	/**
	 * Returns a list of all items at the specified envisalink3 zone
  	 *
	 * @param zone
	 *	Envisalink zone number
	 * @return List<String> of items
	 */
	public List<String> getBindingItemsAtZone(int zone);

	/**
	 * Get the envisalink3 zone function linked to the envisalink3 item
	 *
	 * @param itemName
	 *	The item to which the function is required
	 * @return The Function
	 */
	public Functions getFunction(String itemName);

	/**
	 * Get the envisalink3 zone associated with a specified item name
	 *
	 * @param itemName
	 *	The item whose envisalink3 zone is required
	 * @return The Envisalink3 zone 
	 */
	public int getZone(String itemName);

	/**
	 * Get the item type for the specified item
	 *
	 * @param itemName
	 *	The item whose type is required
	 * @return The openHAB class type
	 */
	public Class<? extends Item> getItemType(String itemName);
}
