package com.tibco.configtool.actions;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.action.CustomActionGroup;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.support.MessageType;
import com.tibco.customwizard.util.ControlUtils;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.xforms.IXFormActionContext;

public class SummaryPagePostAction implements ICustomAction {
	private String successMessage;

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}
	
	public void execute(IActionContext actionContext) throws Exception {
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		XForm form = xformActionContext.getForm();

		WizardInstance wizardInstance = actionContext.getWizardInstance();
		
		UIControl actionGroupControl = form.getUI().getControl("postactions");
		Control actionComposite = (Control) actionGroupControl.getControl();
		List<UIControl> actionControlList = ControlUtils.getChildren(actionGroupControl);
		CustomActionGroup actionGroup = (CustomActionGroup) wizardInstance.getWizardConfig().getPostAction();
		if (actionGroup != null) {
			setStatus(actionControlList, "");
			List<ICustomAction> actionList = actionGroup.getActionList();
			for (ICustomAction action : actionList) {
				UIControl actionControl = findActionControl(action, actionContext.getWizardInstance());
				if (actionControl == null || isSelected(actionControl))
				{
					try {
						WizardHelper.executeAction(actionContext, action);
						updateActionStatus(action, actionControl, form, true);
					} catch (Exception e) {
						updateActionStatus(action, actionControl, form, false);
						throw e;
					}
				}
			}
		}
		
		if (actionComposite != null) {
			// Disable all the action button when successful
			ControlEnableState.disable(actionComposite.getParent());
			actionComposite.getShell().setActive();
		}

		WizardHelper.setWizardMessage(wizardInstance, successMessage, MessageType.INFORMATION);
	}

	protected boolean isSelected(UIControl checkBox) {
		return Boolean.parseBoolean(checkBox.getXMLValue());
	}
	
	protected void updateActionStatus(ICustomAction action, UIControl actionControl, XForm form, boolean success) {
		if (actionControl != null) {
			Object status = SummaryPagePreAction.getActionStatusLabel(actionControl);
			if (status != null) {
				((Label) status).setText(success ? "  Successful!" : "  Failed!");
			}
		}
	}

	protected void setStatus(List<UIControl> actionControlList, String statusText) {
		for (UIControl actionControl : actionControlList) {
			Object status = SummaryPagePreAction.getActionStatusLabel(actionControl);
			if (status != null) {
				if (!Boolean.parseBoolean(actionControl.getXMLValue())) {
					statusText = "";
				}
				((Label) status).setText(statusText);
			}
		}
	}
	
	private UIControl findActionControl(ICustomAction action, WizardInstance wizardInstance) {
		Map<UIControl, ICustomAction> controlActionMap = SummaryPagePreAction.getControlActionMap(wizardInstance);
		for (Entry<UIControl, ICustomAction> entry : controlActionMap.entrySet()) {
			if (entry.getValue() == action) {
				return entry.getKey();
			}
		}
		return null;
	}
}
