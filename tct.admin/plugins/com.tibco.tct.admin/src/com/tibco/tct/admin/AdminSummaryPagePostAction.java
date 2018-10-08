package com.tibco.tct.admin;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.action.ActionException;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.support.XFormControlState;
import com.tibco.customwizard.util.ControlUtils;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.util.XFormUtils;
import com.tibco.customwizard.xforms.IXFormActionContext;
import com.tibco.devtools.installsupport.commands.prodscripts.ProductScriptsConfig;
import com.tibco.tct.framework.actions.CLIAction;
import com.tibco.tct.framework.actions.SummaryPagePostAction;
import com.tibco.tct.framework.support.TCTContext;

public class AdminSummaryPagePostAction extends SummaryPagePostAction {
	private IActionContext actionContext;
	
	public void execute(IActionContext actionContext) throws Exception {
		this.actionContext = actionContext;
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		XForm form = xformActionContext.getForm();
		
		UIControl productGroup = AdminSummaryPagePreAction.getProductGroupControl(form.getUI());
		if (productGroup != null) {
			List<UIControl> productControlList = ControlUtils.getChildren(productGroup);
			setStatus(productControlList, "");
		}
		
		super.execute(actionContext);
		
		if (productGroup != null && !TCTContext.getInstance().isConsoleMode()) {
			XFormControlState.disable(productGroup);
		}
	}
	
	protected void updateActionStatus(ICustomAction action, UIControl actionControl, XForm form, boolean success) {
		super.updateActionStatus(action, actionControl, form, success);

		if (success && AdminSummaryPagePreAction.isCreateAdminAction(action)) {
			deployProducts(form);
		}
	}
	
	private void deployProducts(XForm form) {
		CLIAction action = new CLIAction();
		action.setTargetName("deploy-products");
		Properties props = new Properties();
		action.setProperties(props);

		UIControl productGroupControl = AdminSummaryPagePreAction.getProductGroupControl(form.getUI());
		if (productGroupControl != null) {
			List<UIControl> productControlList = ControlUtils.getChildren(productGroupControl);
			Map<UIControl, ProductScriptsConfig> controlProductMap = AdminSummaryPagePreAction.getControlProductMap(XFormUtils.getWizardInstance(form));
			for (UIControl productControl : productControlList) {
				ProductScriptsConfig productConfig = controlProductMap.get(productControl);
				if (productConfig != null) {
					if (isSelected(productControl)) {
						props.put("dev.node.products", productConfig.getName() + "/" + productConfig.getVersion());
						action.setLogFileName("deploy." + productConfig.getName() + ".log");
						action.setDescription("Deploy " + productConfig.getName());

						try {
							WizardHelper.executeAction(actionContext, action);
							super.updateActionStatus(action, productControl, form, true);
						} catch (Exception e) {
							super.updateActionStatus(action, productControl, form, false);
							if (e instanceof ActionException) {
								throw (ActionException) e;
							} else {
								throw new ActionException(e.getMessage(), e);
							}
						}
					}
				}
			}
		}
	}
}
