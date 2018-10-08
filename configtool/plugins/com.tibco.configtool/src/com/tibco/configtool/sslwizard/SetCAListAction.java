package com.tibco.configtool.sslwizard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.ui.swt.BuildContext;
import org.nuxeo.xforms.ui.swt.proxies.CheckBoxProxy;
import org.nuxeo.xforms.ui.swt.proxies.CompositeProxy;
import org.nuxeo.xforms.xforms.events.ClickEvent;
import org.nuxeo.xforms.xforms.events.XFormsEvent;
import org.nuxeo.xforms.xforms.model.XForm;
import org.nuxeo.xforms.xforms.model.actions.XFAction;

import com.tibco.configtool.actions.OpenSSLWizardAction;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.ConsoleHelper;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.util.XFormUtils;
import com.tibco.customwizard.validator.ValidateResult;
import com.tibco.customwizard.xforms.IXFormActionContext;
import com.tibco.trinity.server.credentialserver.common.util.SSLTrustWorkflow;

public class SetCAListAction implements ICustomAction, XFAction {
	private Map<UIControl, HashMap<String, Object>> controlCAMap;

	public void execute(IActionContext actionContext) throws Exception {
		XForm xform = ((IXFormActionContext) actionContext).getForm();

		WizardInstance wizardInstance = actionContext.getWizardInstance();
		OpenSSLWizardAction sourceAction = OpenSSLWizardAction.getOpenSSLWizardAction(wizardInstance);

		if (controlCAMap == null) {
			List<HashMap<String, Object>> caList = sourceAction.getCaList();

			UIControl caGroupControl = xform.getUI().getControl("cagroup");
			controlCAMap = new HashMap<UIControl, HashMap<String, Object>>();
			wizardInstance.setAttribute("control.ca", controlCAMap);

			for (HashMap<String, Object> caProps : caList) {
				UIControl caControl = createCAControl(caGroupControl);
				caControl.setLabel((String) caProps.get(SSLTrustWorkflow.SUBJECT_CN));
				caControl.addAction(ClickEvent.ID, this);
				controlCAMap.put(caControl, caProps);
			}
			
			if (WizardHelper.isConsoleMode(wizardInstance)) {
				xform.getUI().getControl("details").setLabel("Certificate details");
			}
			WizardHelper.redraw(caGroupControl);
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<UIControl, HashMap<String, Object>> getControlCAMap(WizardInstance wizardInstance) {
		return (Map<UIControl, HashMap<String, Object>>) wizardInstance.getAttribute("control.ca");
	}
	
	public void handleEvent(XForm form, XFormsEvent event) {
		UIControl control = ((ClickEvent) event).getControl();
		
		ValidateResult validateResult = ValidateResult.VALID;
		if (Boolean.parseBoolean(control.getXMLValue())) {
			HashMap<String, Object> certificateProps = controlCAMap.get(control);
			form.getUI().getControl("details").setXMLValue(getCertificateDetails(certificateProps));
		} else {
			for (UIControl caControl : controlCAMap.keySet()) {
				if (Boolean.parseBoolean(caControl.getXMLValue())) {
					return;
				}
			}

			validateResult = OpenSSLWizardAction.IN_VALID;
		}
		
		WizardInstance wizardInstance = XFormUtils.getWizardInstance(form);
		WizardHelper.setWizardValidateResult(wizardInstance, validateResult);
		WizardHelper.updateWizardButtons(wizardInstance);
	}

	private UIControl createCAControl(UIControl caGroupControl) throws Exception {
		if (WizardHelper.isSWTMode(XFormUtils.getWizardInstance(caGroupControl))) {
			BuildContext buildContext = new BuildContext(null);
			buildContext.parent = (CompositeProxy) caGroupControl;
			buildContext.style = SWT.CHECK;
			CheckBoxProxy checkBoxProxy = new CheckBoxProxy();
			checkBoxProxy.create(caGroupControl.getElement().getForm().getUI(), buildContext);
			checkBoxProxy.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			return checkBoxProxy;
		} else {
			return ConsoleHelper.createCheckBox(caGroupControl);
		}
	}

	private String getCertificateDetails(HashMap<String, Object> certificateProps) {
		return "Issued to: " + certificateProps.get(SSLTrustWorkflow.SUBJECT) + "\nIssued by: " + certificateProps.get(SSLTrustWorkflow.ISSUER)
				+ "\nValid from: " + certificateProps.get(SSLTrustWorkflow.VALID_FROM) + "\nValid through: "
				+ certificateProps.get(SSLTrustWorkflow.VALID_TO) + "\nSerial number: " + certificateProps.get(SSLTrustWorkflow.SERIAL_NUMBER)
				+ "\nSHA1 Thumbprint: " + certificateProps.get(SSLTrustWorkflow.SHA1_THUMBPRINT) + "\nMD5 Thumbprint: "
				+ certificateProps.get(SSLTrustWorkflow.MD5_THUMBPRINT) + "\nPublic Key Algorithm: "
				+ certificateProps.get(SSLTrustWorkflow.KEY_ALGORITHM);
	}
}
