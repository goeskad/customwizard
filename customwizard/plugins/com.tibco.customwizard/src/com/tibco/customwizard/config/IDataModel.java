package com.tibco.customwizard.config;


public interface IDataModel {
    String getValue(String key);

    void setValue(String key, String value);
    
    boolean remove(String key);
}
