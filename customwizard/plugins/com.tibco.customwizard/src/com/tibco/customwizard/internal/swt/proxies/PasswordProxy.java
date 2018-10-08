package com.tibco.customwizard.internal.swt.proxies;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.ui.swt.BuildContext;
import org.nuxeo.xforms.ui.swt.proxies.TextProxy;
import org.nuxeo.xforms.xsd.XSDValidationException;
import org.w3c.dom.Node;

import com.tibco.customwizard.config.IPasswordObfuscator;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.XFormUtils;

/**
 * To support password obfuscation
 * 
 */
public class PasswordProxy extends TextProxy {
	private IPasswordObfuscator obfuscator;
	private UIControl confirmPasswordProxy;

	public void setConfirmPasswordProxy(UIControl confirmPasswordProxy) {
		this.confirmPasswordProxy = confirmPasswordProxy;
	}

	public Control create(BuildContext context, Composite parent, int style) {
		Control ctrl = new Text(parent, style | SWT.PASSWORD);
		return ctrl;
	}

	public void reset() {
		Node node = getInstanceNode();
		if (node == null)
			return;
		Node text = getFirstTextNode(node);
		if (text != null) {
			String value = text.getNodeValue();
			if (getPasswordObfuscator() != null && value != null) {
				try {
					value = obfuscator.decrypt(value);
				} catch (Exception e) {
					handleError(e);
				}
			}
			setXMLValue(value);
			if (confirmPasswordProxy != null) {
				confirmPasswordProxy.setXMLValue(value);
			}
		} else
			setXMLValue(null); // no value
	}

	public void commit() throws XSDValidationException {
		Node node = getInstanceNode();
		if (node == null)
			return;
		Node text = getFirstTextNode(node);
		String value = getXMLValue();
		if (getPasswordObfuscator() != null && value != null) {
			try {
				value = obfuscator.encrypt(value);
			} catch (Exception e) {
				handleError(e);
			}
		}

		if (text != null) {
			text.setNodeValue(value);
		} else if (value != null) {
			text = node.getOwnerDocument().createTextNode(value);
			node.appendChild(text);
		}
	}
	
	private IPasswordObfuscator getPasswordObfuscator() {
		if (obfuscator == null) {
			String obfuscatorClass = element.getAttribute("obfuscatorClass");
			if (obfuscatorClass != null) {
				WizardInstance wizardInstance = XFormUtils.getWizardInstance(form.getForm());
				try {
					obfuscator = (IPasswordObfuscator) wizardInstance.getWizardConfig().getExtendedClassLoader().loadClass(obfuscatorClass).newInstance();
				} catch (Exception e) {
					handleError(e);
				}
			}
		}
		return obfuscator;
	}
	
	private void handleError(Exception e) {
		WizardInstance wizardInstance = XFormUtils.getWizardInstance(form.getForm());
		wizardInstance.getWizardConfig().getErrorHandler().handleError(wizardInstance, e);
	}
}
