package com.tibco.configtool.support;

import java.util.Properties;

import com.tibco.customwizard.config.IDataModel;

public class PropertiesDataModel implements IDataModel {
	private Properties properties;

	protected PropertiesDataModel(Properties properties) {
		this.properties = properties;
	}

	public String getValue(String key) {
		return properties.getProperty(key);
	}

	public boolean remove(String key) {
		return properties.remove(key) == null;
	}

	public void setValue(String key, String value) {
		properties.setProperty(key, value);
	}

	public void store() throws Exception {
	}
}
