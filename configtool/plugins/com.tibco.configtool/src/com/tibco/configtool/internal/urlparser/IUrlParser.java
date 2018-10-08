package com.tibco.configtool.internal.urlparser;

import java.util.Properties;

public interface IUrlParser {

	/**
	 * Just like it's name implies, don't use HEAVY arithmetic to implement.
	 * Usually, you can user String.startsWith() or String.contains() to test url.
	 * @param url
	 * @return
	 */
	boolean fastMatch(String url);


	/**
	 * Parse url and fill Properties with key-value pairs.
	 * @param url
	 * @return
	 * @throws Exception
	 */
	Properties parse(String url) throws Exception;

}
