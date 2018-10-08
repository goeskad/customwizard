package com.tibco.configtool.support;

import com.tibco.customwizard.config.IPasswordObfuscator;
import com.tibco.security.ObfuscationEngine;

public class PasswordObfuscator implements IPasswordObfuscator {
	public String decrypt(String value) throws Exception {
		if (value != null) {
			value = new String(ObfuscationEngine.decrypt(value));
		}
		return value;
	}

	public String encrypt(String value) throws Exception {
		if (value != null) {
			value = ObfuscationEngine.encrypt(value.toCharArray());
		}
		return value;
	}
}
