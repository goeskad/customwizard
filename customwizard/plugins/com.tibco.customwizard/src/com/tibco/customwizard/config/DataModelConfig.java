package com.tibco.customwizard.config;

public class DataModelConfig {
	private String file;
	private IDataModelLoader loader;
	
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public IDataModelLoader getLoader() {
		return loader;
	}

	public void setLoader(IDataModelLoader loader) {
		this.loader = loader;
	}
}
