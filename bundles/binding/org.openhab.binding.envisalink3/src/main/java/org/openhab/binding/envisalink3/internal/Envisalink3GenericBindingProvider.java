/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.envisalink3.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openhab.binding.envisalink3.Envisalink3BindingProvider;
import org.openhab.binding.envisalink3.internal.board.Envisalink3Board;
import org.openhab.binding.envisalink3.internal.board.Envisalink3Board.Functions;
import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.items.ContactItem;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;


/**
 * This class is responsible for parsing the binding configuration.
 * 
 * @author Greg Finley
 * @since 1.5.0
 */
public class Envisalink3GenericBindingProvider extends AbstractGenericBindingProvider implements Envisalink3BindingProvider {
	
	/** {@Link Pattern} which matches an In-Binding */
	private static final Pattern BINDING_PATTERN = Pattern.compile("([0-9]+):([A-Z]+)");	

	static final Logger logger = LoggerFactory.getLogger(Envisalink3GenericBindingProvider.class);

	/**
	 * {@inheritDoc}
	 */
	public String getBindingType() {
		return "envisalink3";
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public void validateItemType(Item item, String bindingConfig) throws BindingConfigParseException {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item, String bindingConfig) throws BindingConfigParseException {
		super.processBindingConfiguration(context, item, bindingConfig);

		if (bindingConfig != null) {
			Envisalink3BindingConfig config = new Envisalink3BindingConfig();

			config.itemType = item.getClass();
		
			Matcher bindingMatcher = BINDING_PATTERN.matcher(bindingConfig);
			
			if (!bindingMatcher.matches()) {
				throw new BindingConfigParseException(getBindingType() + " binding configuration must consist of two parts [config="+bindingMatcher+"]");
			} else {
				config.zone = Integer.parseInt(bindingMatcher.group(1));
				config.function = Functions.valueOf(bindingMatcher.group(2));

				// Check the type for different functions 
				switch(config.function) {
					case STATUS:
						if (config.itemType != ContactItem.class) { 
							logger.error("Only Contact allowed for Envisalink3:{} function", config.function);
							config = null;
						}
						break;
					case BYPASS:
						if (config.itemType != SwitchItem.class) {
							logger.error("Only Switch allows for Envisalink3:{} function", config.function);
							config = null;
						}
						break;	
					default:
						config = null;
						logger.error("Unknown or unsupported Envisalink3 function:{}", bindingConfig);
						break;
				}
			}
			
			if (config != null) {
				addBindingConfig(item, config);
			}
		} else {
			logger.warn("bindingConfig is NULL (item=" + item + ") -> processing bindingConfig aborted!");
		}
	}

	/**
       	 * @{inheritDoc}
	 */
	public List<String> getBindingItemsAtZone(int zone) {
		List<String> bindings = new ArrayList<String>();
		for (String itemName : bindingConfigs.keySet()) {
			Envisalink3BindingConfig itemConfig = (Envisalink3BindingConfig)bindingConfigs.get(itemName);
			if (itemConfig.hasZone(zone)) {
				bindings.add(itemName);
			}
		}
		return bindings;
	}

	public Functions getFunction(String itemName) {
		Envisalink3BindingConfig config = (Envisalink3BindingConfig) bindingConfigs.get(itemName);
		return config != null ? config.function : null;
	}

	public int getZone(String itemName) {
		Envisalink3BindingConfig config = (Envisalink3BindingConfig) bindingConfigs.get(itemName);
		return config != null ? config.zone : -1;
	}

	public Class<? extends Item> getItemType(String itemName) {
		Envisalink3BindingConfig config = (Envisalink3BindingConfig) bindingConfigs.get(itemName);
		return config != null? config.itemType : null;
	}

	class Envisalink3BindingConfig implements BindingConfig {
		Class<? extends Item> itemType;
		int zone;
		Envisalink3Board.Functions function;
		
		boolean hasZone(int zonenum) {
			if (zone == zonenum) 
				return true;
			return false;
		}
	}
}
