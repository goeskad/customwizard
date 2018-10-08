package com.tibco.customwizard.config;

public interface IPasswordObfuscator {
    String encrypt(String value) throws Exception;

    String decrypt(String value) throws Exception;
}
