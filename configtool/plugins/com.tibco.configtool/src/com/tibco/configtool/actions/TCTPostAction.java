package com.tibco.configtool.actions;

import org.eclipse.core.runtime.IProgressMonitor;

import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.support.IProgressMonitorAware;

public abstract class TCTPostAction implements ICustomAction, IProgressMonitorAware {
	protected String id;
	protected boolean enable = true;
	protected boolean visible = true;
	protected String depends;
	protected String description;
	protected IProgressMonitor progressMonitor;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDepends() {
		return depends;
	}

	public void setDepends(String depends) {
		this.depends = depends;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public boolean isEnable() {
		return enable;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	public String toString() {
		return description == null ? super.toString() : description;
	}
}
