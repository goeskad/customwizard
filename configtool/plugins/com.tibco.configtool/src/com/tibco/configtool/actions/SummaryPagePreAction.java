package com.tibco.configtool.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.ui.swt.proxies.CheckBoxProxy;
import org.nuxeo.xforms.xforms.events.ClickEvent;
import org.nuxeo.xforms.xforms.events.XFormsEvent;
import org.nuxeo.xforms.xforms.model.XForm;
import org.nuxeo.xforms.xforms.model.actions.XFAction;

import com.tibco.configtool.support.IMessageProvider;
import com.tibco.configtool.support.TCTContext;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.action.CustomActionGroup;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.support.MessageType;
import com.tibco.customwizard.util.ConsoleHelper;
import com.tibco.customwizard.util.SWTHelper;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.xforms.IXFormActionContext;

public class SummaryPagePreAction implements ICustomAction, XFAction {
	private final static String CONTROL_ACTION_MAP = "control.action.map";
	
	private final static String SUMMARY_ACTION_STATUS = "summary.action.status";
	
	protected List<UIControl> actionControlList;

	protected Map<UIControl, ICustomAction> controlActionMap;
	
	protected WizardInstance wizardInstance;

	protected IMessageProvider messageProvider;
	
	@SuppressWarnings("unchecked")
	public static Map<UIControl, ICustomAction> getControlActionMap(WizardInstance wizardInstance) {
		return (Map<UIControl, ICustomAction>) wizardInstance.getAttribute(CONTROL_ACTION_MAP);
	}
	
	public static Object getActionStatusLabel(UIControl actionControl) {
		if (actionControl.getControl() != null) {
			return ((Control) actionControl.getControl()).getData(SUMMARY_ACTION_STATUS);
		}
		return null;
	}
	
	public void execute(IActionContext actionContext) throws Exception {
		wizardInstance = actionContext.getWizardInstance();
		messageProvider = TCTHelper.getMessageProvider(wizardInstance);
		
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		XForm form = xformActionContext.getForm();

		//remove the previous saved data file
		TCTHelper.removeDataModelFile(wizardInstance);
		
		File sessionDir = SaveDataModelAction.getSaveDataModelAction(wizardInstance).getSessionDir();
		form.getUI().getControl("datafile").setXMLValue(sessionDir.getAbsolutePath());

		UIControl actionGroupControl = form.getUI().getControl("postactions");
		if (actionControlList == null) {
			CustomActionGroup actionGroup = (CustomActionGroup) wizardInstance.getWizardConfig().getPostAction();
			if (actionGroup != null) {
				List<ICustomAction> actionList = actionGroup.getActionList();
				actionControlList = new ArrayList<UIControl>();
				controlActionMap = new HashMap<UIControl, ICustomAction>();
				for (ICustomAction action : actionList) {
					if (action instanceof TCTPostAction && ((TCTPostAction) action).isVisible()) {
						UIControl actionControl = createActionControl(actionGroupControl);
						actionControl.setLabel(action.toString());
						actionControl.addAction(ClickEvent.ID, this);
						actionControl.setXMLValue(((TCTPostAction) action).isEnable() + "");

						actionControlList.add(actionControl);
						controlActionMap.put(actionControl, action);
					}
				}
				wizardInstance.setAttribute(CONTROL_ACTION_MAP, controlActionMap);
			}
		} else {
			if (!TCTContext.getInstance().isConsoleMode()) {
				for (UIControl actionControl : actionControlList) {
					Label status = (Label) ((Control) actionControl.getControl()).getData(SummaryPagePreAction.SUMMARY_ACTION_STATUS);
					status.setText("");
				}
			}
		}
		
		WizardHelper.redraw(actionGroupControl);
	}

	protected UIControl createActionControl(UIControl actionGroupControl) throws Exception {
		if (TCTContext.getInstance().isConsoleMode()) {
			return ConsoleHelper.createCheckBox(actionGroupControl);
		} else {
			CheckBoxProxy checkBoxProxy = (CheckBoxProxy) SWTHelper.createControl(actionGroupControl, CheckBoxProxy.class, SWT.CHECK);
			checkBoxProxy.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Label status = new Label((Composite) actionGroupControl.getControl(), SWT.NONE);
			status.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			((Control) checkBoxProxy.getControl()).setData(SUMMARY_ACTION_STATUS, status);
			return checkBoxProxy;
		}
	}

	public void handleEvent(XForm form, XFormsEvent event) {
		int actionCount = actionControlList.size();
		if (actionCount > 1) {
			String message = null;
			UIControl lastCheckedActionControl = null;
			for (int i = actionControlList.size() - 1; i >= 0; i--) {
				UIControl actionControl = actionControlList.get(i);
				if (!isSelected(actionControl) && lastCheckedActionControl != null && isDepended(getAction(lastCheckedActionControl), getAction(actionControl))) {
					message = messageProvider.getMessage("SummaryPagePreAction.depends.warning", getAction(lastCheckedActionControl)
							.toString(), getAction(actionControl).toString());
					WizardHelper.setWizardMessage(wizardInstance, message, MessageType.WARNING);
					break;
				} else if (isSelected(actionControl)) {
					lastCheckedActionControl = actionControl;
				}
			}

			if (message == null) {
				WizardHelper.resetWizardMessage(wizardInstance);
			}
		}
	}

	protected boolean isSelected(UIControl checkBox) {
		return Boolean.parseBoolean(checkBox.getXMLValue());
	}
	
	protected ICustomAction getAction(UIControl actionControl) {
		return controlActionMap.get(actionControl);
	}
	
	private boolean isDepended(ICustomAction action1, ICustomAction action2) {
		if (action1 != null && action2 != null) {
			if (action1 instanceof TCTPostAction && action2 instanceof TCTPostAction) {
				String depends = ((TCTPostAction) action1).getDepends();
				if (depends != null) {
					String id = ((TCTPostAction) action2).getId();
					if (id == null || !depends.contains(id)) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}
}
