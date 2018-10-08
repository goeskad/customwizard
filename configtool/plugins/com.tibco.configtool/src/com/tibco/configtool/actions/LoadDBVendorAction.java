package com.tibco.configtool.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.configtool.utils.MachineModelUtils;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.configtool.utils.TPCLShellsUtils;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.xforms.IXFormActionContext;

public class LoadDBVendorAction implements ICustomAction {
	private boolean init = false;
	private String controlId = "vendor";

	private String supportedDbKey = "db";

	public void setControlId(String controlId) {
		this.controlId = controlId;
	}

	public void setSupportedDbKey(String supportedDbKey) {
		this.supportedDbKey = supportedDbKey;
	}

	public void execute(IActionContext actionContext) throws Exception {
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		XForm form = xformActionContext.getForm();

		UIControl vendorControl = form.getUI().getControl(controlId);
		if (!init) {
			Map<String, String> identifierMap = TPCLShellsUtils.getInstantiatedShellIdentifierMap(MachineModelUtils.getMachine(),
					TPCLShellsUtils.SHELL_TYPE_JDBC);
			Properties config = TCTHelper.getConfigProperties(actionContext.getWizardInstance());
			String[] supportedDbList = config.getProperty(supportedDbKey).split(",");
			Map<String, String> suppportMap = new HashMap<String, String>();
			for (String supportedDb : supportedDbList) {
				String supportedDbDisplayName = identifierMap.get(supportedDb);
				if (supportedDbDisplayName != null) {
					suppportMap.put(supportedDb, supportedDbDisplayName); // get display by key
				}
			}
			TCTHelper.updateCombo(vendorControl, suppportMap);

			init = true;
		}
	}
}
