package com.tibco.customwizard.internal.console.proxies;

import org.nuxeo.xforms.ui.AbstractUIControl;
import org.nuxeo.xforms.xforms.events.XFormsEvent;

public abstract class ConsoleControlProxy extends AbstractUIControl {
	protected GroupProxy parent;
	
	protected String label;
	protected String toolTip;
	protected boolean enabled = true;

	public String getXMLValue() {
		return null;
	}

	public void setXMLValue(String value) {
		// do nothing
	}

	public GroupProxy getParent() {
		return parent;
	}

	public void setParent(GroupProxy parent) {
		this.parent = parent;
	}

	public boolean isContainer() {
		return false;
	}

	public boolean isEditable() {
		return false;
	}

	public void layout() {
		// do nothing
	}

	public void setBackground(String value) {
		// do nothing
	}

	public void setForeground(String value) {
		// do nothing
	}

	public boolean isEnabled() {
		return enabled && (parent == null ? true : parent.isEnabled());
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setFont(String font) {
		// do nothing
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setToolTipText(String toolTip) {
		this.toolTip = toolTip;
	}

	public abstract void present();
	
	protected void fireXFormsEvent(XFormsEvent event){
		form.getForm().getProcessor().dispatch(event);
	}
}
