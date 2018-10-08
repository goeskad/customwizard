package com.tibco.customwizard.internal.console;

public class ControlStyleProperty implements StyleProperty {
	public boolean process(BuildContext descriptor, String value) {
		Class<?> proxyClass = descriptor.builder.getRegistry().getControlProxy(value);
		if (proxyClass != null) {
			descriptor.proxyClass = proxyClass;
		}
		return true;
	}
}
