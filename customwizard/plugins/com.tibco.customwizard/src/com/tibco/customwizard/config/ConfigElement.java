package com.tibco.customwizard.config;


public class ConfigElement {
	protected ConfigElement parent;

	public ConfigElement getParentWizardConfig() {
		return parent;
	}

	public void setParent(ConfigElement parent) {
		this.parent = parent;
	}
}
