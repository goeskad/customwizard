package com.tibco.customwizard.internal.support;

import com.tibco.customwizard.config.IDataModel;


public class SystemPropertiesDataModel implements IDataModel {
    private static final SystemPropertiesDataModel instance = new SystemPropertiesDataModel();

    protected SystemPropertiesDataModel() {

    }

    public static SystemPropertiesDataModel getInstance() {
        return instance;
    }

    public String getValue(String key) {
        return System.getProperty(key);
    }

    public boolean remove(String key) {
        return false;
    }

    public void setValue(String key, String value) {
        System.setProperty(key, value);
    }

    public void store() throws Exception {
    }
}
