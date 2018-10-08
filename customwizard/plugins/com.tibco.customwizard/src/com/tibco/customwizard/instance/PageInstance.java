package com.tibco.customwizard.instance;

import java.util.HashMap;
import java.util.Map;

import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.config.PageConfig;
import com.tibco.customwizard.internal.xforms.BaseXFormActionContext;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.util.XFormUtils;
import com.tibco.customwizard.xforms.events.PagePostEvent;
import com.tibco.customwizard.xforms.events.PageShownEvent;

public class PageInstance {
	private WizardInstance wizardInstance;
	private PageConfig pageConfig;

	private Object nativePage;
	
	private ICustomAction preAction;
	private ICustomAction postAction;
	
	private Map<Object, Object> attributes = new HashMap<Object, Object>();

	public PageInstance(WizardInstance wizardInstance, PageConfig pageConfig) {
		this.wizardInstance = wizardInstance;
		this.pageConfig = pageConfig;
	}

	public WizardInstance getWizardInstance() {
		return wizardInstance;
	}

	public PageConfig getPageConfig() {
		return pageConfig;
	}

	public Object getNativePage() {
		return nativePage;
	}

	public void setNativePage(Object nativePage) {
		this.nativePage = nativePage;
	}

	public Object getAttribute(Object key) {
		return attributes.get(key);
	}

	public void setAttribute(Object key, Object value) {
		attributes.put(key, value);
	}
	
	public void setPreAction(ICustomAction preAction) {
		this.preAction = preAction;
	}

	public void setPostAction(ICustomAction postAction) {
		this.postAction = postAction;
	}

	public ICustomAction getPreAction() {
		return preAction;
	}

	public ICustomAction getPostAction() {
		return postAction;
	}
	
	public void performPostAction() throws Exception {
		if (postAction != null) {
			BaseXFormActionContext actionContext = new BaseXFormActionContext(wizardInstance, XFormUtils.getXForm(this),
					new PagePostEvent());
			WizardHelper.executeAction(actionContext, postAction);
		}
	}

	public void performPreAction() throws Exception {
		if (preAction != null) {
			BaseXFormActionContext actionContext = new BaseXFormActionContext(wizardInstance, XFormUtils.getXForm(this),
					new PageShownEvent());
			WizardHelper.executeAction(actionContext, preAction);
		}
	}
}
