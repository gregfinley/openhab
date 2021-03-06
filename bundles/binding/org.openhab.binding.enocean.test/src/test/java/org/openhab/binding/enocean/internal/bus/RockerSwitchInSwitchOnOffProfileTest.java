/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.enocean.internal.bus;

import static org.junit.Assert.assertEquals;

import org.enocean.java.address.EnoceanId;
import org.enocean.java.address.EnoceanParameterAddress;
import org.enocean.java.common.EEPId;
import org.enocean.java.common.Parameter;
import org.enocean.java.common.values.ButtonState;
import org.junit.Before;
import org.junit.Test;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.OnOffType;

public class RockerSwitchInSwitchOnOffProfileTest extends BasicBindingTest {

    private static final String CHANNEL = "B";

    @Before
    public void setUpDefaultDevice() {
        parameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), CHANNEL, (String) null);
        provider.setParameterAddress(parameterAddress);
        provider.setItem(new SwitchItem("dummie"));
        provider.setEep(EEPId.EEP_F6_02_01);
        binding.addBindingProvider(provider);
    }

    @Test
    public void switchOnForButtonPressDown() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(
                EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), CHANNEL, Parameter.I);
        binding.valueChanged(valueParameterAddress, ButtonState.PRESSED);
        assertEquals("Update State", OnOffType.ON, publisher.popLastCommand());
    }

    @Test
    public void switchOffForButtonPressDown() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(
                EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), CHANNEL, Parameter.O);
        binding.valueChanged(valueParameterAddress, ButtonState.PRESSED);
        assertEquals("Update State", OnOffType.OFF, publisher.popLastCommand());
    }

}
