package com.tibco.customwizard.internal.console;

import java.util.HashMap;
import java.util.Map;

import com.tibco.customwizard.internal.console.proxies.ButtonProxy;
import com.tibco.customwizard.internal.console.proxies.CheckBoxProxy;
import com.tibco.customwizard.internal.console.proxies.ComboProxy;
import com.tibco.customwizard.internal.console.proxies.ConfirmPasswordProxy;
import com.tibco.customwizard.internal.console.proxies.GroupProxy;
import com.tibco.customwizard.internal.console.proxies.LabelProxy;
import com.tibco.customwizard.internal.console.proxies.PasswordProxy;
import com.tibco.customwizard.internal.console.proxies.RadioProxy;
import com.tibco.customwizard.internal.console.proxies.TextProxy;
import com.tibco.customwizard.internal.console.style.Disable;
import com.tibco.customwizard.internal.console.style.ReadOnly;

public class ConsoleRegistry {
	private static ConsoleRegistry sInstance = null;

	Map<String, Class<?>> sControlProxyRegistry = new HashMap<String, Class<?>>();
	Map<String, StyleProperty> sPropertyRegistry = new HashMap<String, StyleProperty>();

	public static ConsoleRegistry getInstance() {
		if (sInstance == null) {
			sInstance = new ConsoleRegistry();
		}
		return sInstance;
	}

	protected ConsoleRegistry() {
		initializeRegistry(this);
	}

	public Class<?> getControlProxy(String name) {
		return (Class<?>) sControlProxyRegistry.get(name);
	}

	public void registerControlProxy(String name, Class<?> proxy) {
		sControlProxyRegistry.put(name, proxy);
	}

	public void registerStyleProperty(String name, StyleProperty prop) {
		sPropertyRegistry.put(name, prop);
	}

	public StyleProperty getStyleProperty(String name) {
		return (StyleProperty) sPropertyRegistry.get(name);
	}

	protected void initializeRegistry(ConsoleRegistry registry) {
		registry.registerControlProxy("group", GroupProxy.class);
		registry.registerControlProxy("text", TextProxy.class);
		registry.registerControlProxy("password", PasswordProxy.class);
		registry.registerControlProxy("confirmpassword", ConfirmPasswordProxy.class);
		registry.registerControlProxy("label", LabelProxy.class);
		registry.registerControlProxy("button", ButtonProxy.class);
		registry.registerControlProxy("check", CheckBoxProxy.class);
		registry.registerControlProxy("radio", RadioProxy.class);
		registry.registerControlProxy("combo", ComboProxy.class);
		
		registry.registerStyleProperty("control", new ControlStyleProperty());
		
		registry.registerStyleProperty("disable", new Disable());
		registry.registerStyleProperty("readOnly", new ReadOnly());
	}
}
