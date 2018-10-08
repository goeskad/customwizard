package com.tibco.customwizard.internal.console.proxies;

import java.util.List;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xsd.XSDValidationException;
import org.w3c.dom.Node;

import com.tibco.customwizard.config.IPasswordObfuscator;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.internal.xforms.validator.BaseXFormValidationContext;
import com.tibco.customwizard.internal.xforms.validator.MatchValueValidator;
import com.tibco.customwizard.internal.xforms.validator.ValidateManager;
import com.tibco.customwizard.support.SystemConsoleProxy;
import com.tibco.customwizard.util.XFormUtils;
import com.tibco.customwizard.validator.ICustomValidator;
import com.tibco.customwizard.validator.ValidateResult;

public class PasswordProxy extends TextProxy {
	private IPasswordObfuscator obfuscator;
	private UIControl confirmPasswordProxy;

	public void setConfirmPasswordProxy(UIControl confirmPasswordProxy) {
		this.confirmPasswordProxy = confirmPasswordProxy;
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

	protected void printDefaultValue() {
		if (value != null && value.length() > 0) {
			StringBuffer strbuf = new StringBuffer();
			for (int i = 0; i < value.length(); i++) {
				strbuf.append('*');
			}
			SystemConsoleProxy.print("[" + strbuf.toString() + "]");
		}
	}

	protected String readInput() {
		return SystemConsoleProxy.readPassword();
	}

	protected ValidateResult getValidateResult() {
		List<ICustomValidator> validatorList = ValidateManager.getValidatorList(this.element);
		if (validatorList != null) {
			for (ICustomValidator validator : validatorList) {
				if (!(validator instanceof MatchValueValidator)) {
					ValidateResult validateResult = validator.validate(new BaseXFormValidationContext(this));
					if (validateResult != null && !validateResult.isValid()) {
						return validateResult;
					}
				}
			}
		}
		return ValidateResult.VALID;
	}
	
	private void handleError(Exception e) {
		WizardInstance wizardInstance = XFormUtils.getWizardInstance(form.getForm());
		wizardInstance.getWizardConfig().getErrorHandler().handleError(wizardInstance, e);
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
}
