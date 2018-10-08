package com.tibco.ert.model.core;

import com.tibco.security.ObfuscationEngine;

public class PasswordObfuscator {
    public static PasswordObfuscator instance = new PasswordObfuscator();
    
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
