package com.tibco.customwizard.internal.xforms.actions;

import java.util.Map;

import org.nuxeo.xforms.xforms.events.XFormsEvent;
import org.nuxeo.xforms.xforms.model.XForm;
import org.nuxeo.xforms.xforms.model.actions.XFAction;
import org.xml.sax.Attributes;

import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.internal.xforms.BaseXFormActionContext;
import com.tibco.customwizard.util.PropertyUtils;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.util.XFormUtils;

public class XFCustomAction implements XFAction {
    private String className;

    private Map<Object, Object> attrs;

    private ICustomAction action;
    
    public void setClassName(String className) {
        this.className = className;
    }

	public void setAttrs(Attributes attrs) {
		this.attrs = PropertyUtils.convertToMap(attrs);
	}

	public void setAction(ICustomAction action) {
		this.action = action;
	}

	protected ICustomAction getAction(WizardConfig wizardConfig) throws Exception {
		if (action == null) {
			Class<?> cls = wizardConfig.getExtendedClassLoader().loadClass(className);
			action = (ICustomAction) cls.newInstance();
			PropertyUtils.setAttributes(action, attrs);
		}
		return action;
	}

    public void handleEvent(XForm form, XFormsEvent event) {
    	WizardInstance wizardInstance = XFormUtils.getWizardInstance(form);
    	WizardConfig wizardConfig = wizardInstance.getWizardConfig();
    	
        BaseXFormActionContext actionContext = new BaseXFormActionContext(wizardInstance, form, event);
		try {
			WizardHelper.executeAction(actionContext, getAction(wizardConfig));
		} catch (Exception e) {
			wizardConfig.getErrorHandler().handleError(wizardInstance, e);
		}
	}
}
