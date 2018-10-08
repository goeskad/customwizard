package com.tibco.configtool.actions;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.events.XFormsValueChangedEvent;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.configtool.support.TCTContext;
import com.tibco.configtool.utils.MachineModelUtils;
import com.tibco.configtool.utils.TPCLShellsUtils;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.util.ControlUtils;
import com.tibco.customwizard.xforms.IXFormActionContext;
import com.tibco.devtools.installsupport.commands.tpclshell.InstantiatedTPCLShellConfig;

public class ChooseDBAction implements ICustomAction {
	private String urlControlId = "url";

	public void setUrlControlId(String urlControlId) {
		this.urlControlId = urlControlId;
	}

	public void execute(IActionContext actionContext) throws Exception {
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		XForm form = xformActionContext.getForm();

		UIControl urlControl = form.getUI().getControl(urlControlId);
		if (ControlUtils.isEnabled(urlControl)) {
			String vendorIdentifier = ((XFormsValueChangedEvent) xformActionContext.getEvent()).getControl().getXMLValue();
			if (vendorIdentifier.length() == 0) {
				return;
			}
			InstantiatedTPCLShellConfig shellConfig = TPCLShellsUtils.queryInstantiatedTPCLShellConfig(MachineModelUtils
					.getMachine(), TPCLShellsUtils.SHELL_TYPE_JDBC, vendorIdentifier);
			String targetUrlPattern = shellConfig.getProperties().getProperty(TPCLShellsUtils.JDBC_URL_PATTERN);
			String targetDefaultUrl = shellConfig.getProperties().getProperty(TPCLShellsUtils.JDBC_DEFAULT_URL);
			String hostName = System.getProperty(TCTContext.HOST_NAME_PROPERTY);
			targetDefaultUrl = targetDefaultUrl.replace("localhost", hostName);
			setDBURL(actionContext, urlControl, targetDefaultUrl, targetUrlPattern);
		}
	}

	protected void setDBURL(IActionContext actionContext, UIControl urlControl, String targetDefaultUrl, String targetUrlPattern) {
		if (!isFromTheSameVendor(urlControl.getXMLValue(), targetDefaultUrl)) {
			urlControl.setToolTipText(targetUrlPattern);
			urlControl.setXMLValue(targetDefaultUrl);
		}
	}
	
	protected boolean isFromTheSameVendor(String orgUrl, String targetDefaultUrl) {
		if (orgUrl != null && orgUrl.length() > 0) {
			int orgIndex = orgUrl.indexOf(':');
			if (orgIndex > 0) {
				orgIndex = orgUrl.indexOf(':', orgIndex + 1);
				int targetIndex = targetDefaultUrl.indexOf(':');
				if (targetIndex > 0) {
					targetIndex = targetDefaultUrl.indexOf(':', targetIndex + 1);
					if (orgIndex > 0 && orgIndex == targetIndex) {
						String orgPrefix = orgUrl.substring(0, orgIndex);
						String targetPrefix = targetDefaultUrl.substring(0, targetIndex);
						return orgPrefix.equals(targetPrefix);
					}
				}
			}
		}
		return false;
	}
}
