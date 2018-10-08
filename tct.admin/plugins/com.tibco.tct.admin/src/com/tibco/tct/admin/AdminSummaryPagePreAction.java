package com.tibco.tct.admin;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.ui.forms.widgets.Hyperlink;
import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.ui.UIForm;
import org.nuxeo.xforms.xforms.events.XFormsEvent;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.support.XFormControlState;
import com.tibco.customwizard.util.ControlUtils;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.xforms.IXFormActionContext;
import com.tibco.devtools.installsupport.commands.prodscripts.ProductScriptsConfig;
import com.tibco.tct.amx.utils.MachineModelUtils;
import com.tibco.tct.amx.utils.ProductShellsUtils;
import com.tibco.tct.framework.actions.CLIAction;
import com.tibco.tct.framework.actions.SummaryPagePreAction;
import com.tibco.tct.framework.actions.TCTPostAction;
import com.tibco.tct.framework.support.TCTContext;
import com.tibco.tct.framework.utils.TCTHelper;

public class AdminSummaryPagePreAction extends SummaryPagePreAction {
	private static final String PRODUCT_GROUP_ID = "products";

	private static final String CONTROL_PRODUCT_MAP = "control.product.map";

	private List<UIControl> prodControlList;

	private XFormControlState state;
	
	public void execute(IActionContext actionContext) throws Exception {
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		XForm form = xformActionContext.getForm();

		IDataModel dataModel = actionContext.getDataModel();
		String adminUrl = getAdminURL(dataModel) + "/amxadministrator/loginForm.jsp";
		UIControl adminUrlControl = form.getUI().getControl("adminurl");
		adminUrlControl.setXMLValue(adminUrl);
		if (!TCTContext.getInstance().isConsoleMode()) {
			Hyperlink hyperlink = (Hyperlink) adminUrlControl.getControl();
			hyperlink.setHref(new URL(adminUrl));
		}

		super.execute(xformActionContext);
		
		createProductControls(form, actionContext.getWizardInstance());
		
		WizardHelper.redraw(adminUrlControl);
	}

	public static boolean isCreateAdminAction(ICustomAction action) {
		if (action instanceof CLIAction) {
			String targetName = ((CLIAction) action).getTargetName();
			if (targetName != null && targetName.equals("admin-full-setup")) {
				return true;
			}
		}
		return false;
	}
	
	protected void initActionControl(UIControl actionControl, TCTPostAction action) {
		super.initActionControl(actionControl, action);
		// we will separate deploy products action from admin creation
		if (isCreateAdminAction(action)) {
			Properties props = new Properties();
			props.put("dev.node.products", "");
			((CLIAction) action).setProperties(props);
		}
	}

	public void handleEvent(XForm form, XFormsEvent event) {
		super.handleEvent(form, event);

		UIControl adminActionControl = actionControlList.get(0);
		if (event.target == adminActionControl) {
			if (state == null) {
				UIControl productGroupControl = getProductGroupControl(form.getUI());
				state = XFormControlState.disable(productGroupControl);
			} else {
				state.restore();
				state = null;
			}
		}
	}
	
	protected void cleanActionStatus() {
		cleanActionStatus(actionControlList);
		if (prodControlList != null) {
			cleanActionStatus(prodControlList);
		}
	}
	
	protected void createProductControls(XForm form, WizardInstance wizardInstance) throws Exception {
		boolean createDevNode = Boolean.parseBoolean(wizardInstance.getDataModel().getValue("/admin/tibcohost/createnode"));
		UIControl productGroupControl = getProductGroupControl(form.getUI());
		if (createDevNode && prodControlList == null) {
			prodControlList = new ArrayList<UIControl>();
			Map<UIControl, ProductScriptsConfig> controlProductMap = new HashMap<UIControl, ProductScriptsConfig>();
			List<ProductScriptsConfig> prodList = ProductShellsUtils.queryProductConfigList(MachineModelUtils.getMachine());
			for (ProductScriptsConfig product : prodList) {
				UIControl productControl = createActionControl(productGroupControl);
				productControl.setLabel(product.getDisplayName());
				productControl.setXMLValue("true");
				prodControlList.add(productControl);
				controlProductMap.put(productControl, product);
			}
			wizardInstance.setAttribute(CONTROL_PRODUCT_MAP, controlProductMap);
		}

		if (productGroupControl != null) {
			ControlUtils.setVisible(productGroupControl, createDevNode);
		}
	}

	public static UIControl getProductGroupControl(UIForm uiForm) {
		return uiForm.getControl(PRODUCT_GROUP_ID);
	}

	@SuppressWarnings("unchecked")
	public static Map<UIControl, ProductScriptsConfig> getControlProductMap(WizardInstance wizardInstance) {
		return (Map<UIControl, ProductScriptsConfig>) wizardInstance.getAttribute(CONTROL_PRODUCT_MAP);
	}

	protected String getAdminURL(IDataModel dataModel) {
		String host = TCTHelper.convertHostForIPv6Url(dataModel.getValue("/admin/serverconnsetting/host"));
		boolean securedcommunication = Boolean.parseBoolean(dataModel.getValue("/admin/serverconnsetting/enablessl"));
		String adminUrl = (securedcommunication ? "https" : "http") + "://" + host + ":" + dataModel.getValue("/admin/serverconnsetting/httpport");
		dataModel.setValue("/admin/serverconnsetting/adminurl", adminUrl);
		return adminUrl;
	}
}
